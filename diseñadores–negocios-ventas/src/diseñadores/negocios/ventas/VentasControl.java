package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.productos.ProductosControl;
import diseñadores.negocios.ventas.notificacion.IServicioNotificacion;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VentasControl {

  private final ProductosControl productosControl;
  private final IInventario inventario;
  private IServicioNotificacion servicioCorreo;

  private static final int STOCK_MINIMO = 3;
  private static final String NOMBRE_TIENDA = "La Canasta";
  private static final String RFC = "LCA123456ABC";
  private static final String DIRECCION = "Av. Principal #123, Col. Centro";
  private static final String TELEFONO = "Tel: (555) 123-4567";
  private static final String CAJERO = "Juan Pérez - Caja #1";

  public VentasControl(ProductosControl productosControl, IInventario inventario) {
    this.productosControl = productosControl;
    this.inventario = inventario;
  }

  public void setServicioCorreo(IServicioNotificacion servicio) {
    this.servicioCorreo = servicio;
  }

  public ProductoDTO procesarProducto(Venta ventaActual, EscanearProductoDTO dto) {
    ProductoDTO productoDTO = productosControl.buscar(dto);
    if (productoDTO == null) {
      return null;
    }

    Producto entidad = productosControl.encontrarEntidad(dto.getCodigo());
    if (entidad == null) {
      return null;
    }

    ventaActual.agregarProducto(entidad);

    inventario.reducirStock(dto.getCodigo(), 1);

    return productoDTO;
  }

  public ResultadoPagoDTO procesarPagoEfectivo(Venta ventaActual, PagoEfectivoDTO dto) {
    double total = ventaActual.getSubtotalVenta();
    double recibido = dto.getMontoRecibido();

    if (recibido < total) {
      double faltante = total - recibido;
      return ResultadoPagoDTO.rechazado(
        String.format("Monto insuficiente. Faltan $%.2f para completar el pago.", faltante));
    }

    ventaActual.setPagada(true);
    return ResultadoPagoDTO.aprobado(recibido - total);
  }

  public void procesarFinalizarVenta(Venta ventaActual) {
    ventaActual.setPagada(true);
    for (Producto p : ventaActual.getListaProductos()) {
      ProductoDTO actual = inventario.obtenerProducto(p.getCodigo());
      if (actual != null && actual.getStock() < STOCK_MINIMO) {
        ejecutarProtocoloReabastecimiento(p);
      }
    }
  }

  public VentaDTO crearVentaDTO(Venta ventaActual) {
    if (ventaActual == null) {
      return null;
    }

    List<ItemVentaDTO> items = new ArrayList<>();
    for (Producto p : ventaActual.getListaProductos()) {
      long cantidad = ventaActual.getListaProductos().stream()
        .filter(x -> x.getCodigo().equals(p.getCodigo()))
        .count();
      if (items.stream().noneMatch(i -> i.getCodigo().equals(p.getCodigo()))) {
        items.add(new ItemVentaDTO(p.getCodigo(), p.getNombre(), p.getPrecio(), (int) cantidad));
      }
    }

    double total = ventaActual.getSubtotalVenta();
    double subtotal = total / 1.16;
    double iva = total - subtotal;
    int unidades = ventaActual.getListaProductos().size();

    return new VentaDTO(items, subtotal, iva, total, unidades);
  }

  public TicketDTO generarTicket(Venta ventaActual, double ultimoEfectivo) {
    if (ventaActual == null) {
      return null;
    }

    double total = ventaActual.getSubtotalVenta();
    double subtotal = total / 1.16;
    double iva = total - subtotal;
    double cambio = ultimoEfectivo - total;
    String folio = "TK-" + System.currentTimeMillis();

    LocalDateTime ahora = LocalDateTime.now();
    String fecha = ahora.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX")));
    String hora = ahora.format(DateTimeFormatter.ofPattern("hh:mm a"));

    List<ItemVentaDTO> items = new ArrayList<>();
    for (Producto p : ventaActual.getListaProductos()) {
      long cantidad = ventaActual.getListaProductos().stream()
        .filter(x -> x.getCodigo().equals(p.getCodigo()))
        .count();
      if (items.stream().noneMatch(i -> i.getCodigo().equals(p.getCodigo()))) {
        items.add(new ItemVentaDTO(p.getCodigo(), p.getNombre(), p.getPrecio(), (int) cantidad));
      }
    }

    return new TicketDTO(
      folio, items, subtotal, iva, total,
      ultimoEfectivo, cambio,
      fecha, hora, CAJERO, NOMBRE_TIENDA, RFC, DIRECCION, TELEFONO
    );
  }

  private void ejecutarProtocoloReabastecimiento(Producto p) {
    if (servicioCorreo == null) {
      System.out.println("[REABASTECIMIENTO] Sin servicio de correo. Producto: "
        + p.getNombre() + " — stock bajo.");
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
