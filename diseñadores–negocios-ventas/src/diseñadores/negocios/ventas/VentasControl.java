package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.ItemVentaDTO;
import diseñadores.negocios.dto.PagoEfectivoDTO;
import diseñadores.negocios.dto.Producto;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.Proveedor;
import diseñadores.negocios.dto.ResultadoPagoDTO;
import diseñadores.negocios.dto.TicketDTO;
import diseñadores.negocios.dto.Venta;
import diseñadores.negocios.dto.VentaDTO;
import diseñadores.negocios.productos.ProductosControl;
import diseñadores.negocios.ventas.notificacion.IServicioNotificacion;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VentasControl {

  private Venta ventaActual;
  private double ultimoEfectivo;
  private IServicioNotificacion servicioCorreo;

  private final ProductosControl productosControl;
  private final int STOCK_MINIMO = 3;

  private static final String NOMBRE_TIENDA = "La Canasta";
  private static final String RFC = "LCA123456ABC";
  private static final String DIRECCION = "Av. Principal #123, Col. Centro";
  private static final String TELEFONO = "Tel: (555) 123-4567";
  private static final String CAJERO = "Juan Pérez - Caja #1";

  public VentasControl(ProductosControl productosControl) {
    this.productosControl = productosControl;
  }

  public void setServicioCorreo(IServicioNotificacion servicio) {
    this.servicioCorreo = servicio;
  }

  public void iniciarNuevaVenta() {
    this.ventaActual = new Venta();
    this.ultimoEfectivo = 0.0;
  }

  public ProductoDTO procesarProducto(EscanearProductoDTO dto) {
    ProductoDTO productoDTO = productosControl.buscarProducto(dto);
    if (productoDTO == null) {
      return null;
    }

    Producto entidad = productosControl.obtenerEntidadPorCodigo(dto.getCodigo());
    if (entidad == null) {
      return null;
    }

    ventaActual.agregarProducto(entidad);
    productosControl.reducirStock(dto.getCodigo());

    return productoDTO;
  }

  public boolean existeProducto(EscanearProductoDTO dto) {
    return productosControl.existeProducto(dto);
  }

  public ResultadoPagoDTO procesarPagoEfectivo(PagoEfectivoDTO dto) {
    double total = ventaActual.getSubtotalVenta();
    double recibido = dto.getMontoRecibido();

    if (recibido < total) {
      double faltante = total - recibido;
      return ResultadoPagoDTO.rechazado(
        String.format("Monto insuficiente. Faltan $%.2f para completar el pago.", faltante));
    }

    this.ultimoEfectivo = recibido;
    ventaActual.setPagada(true);
    return ResultadoPagoDTO.aprobado(recibido - total);
  }

  public double calcularCambio(double efectivo) {
    if (ventaActual == null) {
      return 0;
    }
    double total = ventaActual.getSubtotalVenta();
    return efectivo >= total ? efectivo - total : 0;
  }

  public void procesarFinalizarVenta() {
    ventaActual.setPagada(true);
    for (Producto p : ventaActual.getListaProductos()) {
      if (p.getStock() < STOCK_MINIMO) {
        ejecutarProtocoloReabastecimiento(p);
      }
    }
  }

  public VentaDTO obtenerVentaDTO() {
    if (ventaActual == null) {
      return null;
    }

    List<ItemVentaDTO> items = new ArrayList<>();
    for (Producto p : ventaActual.getListaProductos()) {
      long cantidadVendida = ventaActual.getListaProductos().stream()
        .filter(x -> x.getCodigo().equals(p.getCodigo()))
        .count();
      items.add(new ItemVentaDTO(p.getCodigo(), p.getNombre(),
        p.getPrecio(), (int) cantidadVendida));
    }

    double total = ventaActual.getSubtotalVenta();
    double subtotalSinIVA = total / 1.16;
    double iva = total - subtotalSinIVA;
    int totalUnidades = items.stream().mapToInt(ItemVentaDTO::getCantidad).sum();

    return new VentaDTO(items, subtotalSinIVA, iva, total, totalUnidades);
  }

  public TicketDTO generarTicket() {
    if (ventaActual == null) {
      return null;
    }

    double total = ventaActual.getSubtotalVenta();
    double subtotalSinIVA = total / 1.16;
    double iva = total - subtotalSinIVA;
    double cambio = ultimoEfectivo - total;
    String folio = "TK-" + System.currentTimeMillis();

    LocalDateTime ahora = LocalDateTime.now();
    String fecha = ahora.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX")));
    String hora = ahora.format(DateTimeFormatter.ofPattern("hh:mm a"));

    List<ItemVentaDTO> items = new ArrayList<>();
    for (Producto p : ventaActual.getListaProductos()) {
      items.add(new ItemVentaDTO(p.getCodigo(), p.getNombre(), p.getPrecio(), 1));
    }

    return new TicketDTO(
      folio, items, subtotalSinIVA, iva, total,
      ultimoEfectivo, cambio,
      fecha, hora, CAJERO, NOMBRE_TIENDA, RFC, DIRECCION, TELEFONO
    );
  }

  private void ejecutarProtocoloReabastecimiento(Producto p) {
    if (servicioCorreo == null) {
      System.out.println("[REABASTECIMIENTO] Producto con stock bajo: "
        + p.getNombre() + " (" + p.getStock() + " unidades) — sin servicio de correo.");
      return;
    }
    Proveedor prov = p.getProveedor();
    String mensaje = "ALERTA DE STOCK: " + p.getNombre()
      + " tiene solo " + p.getStock() + " unidades.";
    boolean enviado = servicioCorreo.enviarNotificacionStock(prov.getEmail(), mensaje);
    if (enviado) {
      System.out.println("Notificación enviada a: " + prov.getNombre());
    }
  }

}
