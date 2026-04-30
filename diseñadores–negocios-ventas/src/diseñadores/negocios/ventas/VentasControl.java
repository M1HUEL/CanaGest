package diseñadores.negocios.ventas;

import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.productos.IProductos;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class VentasControl {

  private final IProductos productosFacade;
  private final IInventario inventario;
  private INotificaciones servicioNotificaciones;

  private static final int STOCK_MINIMO = 3;
  private static final String NOMBRE_TIENDA = "La Canasta";
  private static final String RFC = "LCA123456ABC";
  private static final String DIRECCION = "Av. Principal #123, Col. Centro";
  private static final String TELEFONO = "Tel: (555) 123-4567";
  private static final String CAJERO = "Juan Pérez - Caja #1";

  public VentasControl(IProductos productosFacade, IInventario inventario, INotificaciones servicioNotificaciones) {
    this.productosFacade = productosFacade;
    this.inventario = inventario;
    this.servicioNotificaciones = servicioNotificaciones;
  }

  public ProductoDTO procesarProducto(VentaDTO ventaActual, EscanearProductoDTO dto) {
    ProductoDTO productoDTO = productosFacade.buscarProductoPorCodigo(dto);

    if (productoDTO == null) {
      return null;
    }

    ventaActual.agregarProducto(productoDTO);

    return productoDTO;
  }

  public ResultadoPagoDTO procesarPagoEfectivo(VentaDTO ventaActual, PagoEfectivoDTO dto) {
    double total = ventaActual.getTotal();
    double recibido = dto.getMontoRecibido();

    if (recibido < total) {
      double faltante = total - recibido;
      return ResultadoPagoDTO.rechazado(
        String.format("Monto insuficiente. Faltan $%.2f para completar el pago.", faltante));
    }

    return ResultadoPagoDTO.aprobado(recibido - total);
  }

  public double procesarCalcularCambio(VentaDTO ventaActual, double efectivo) {
    if (ventaActual == null) {
      return 0;
    }
    double total = ventaActual.getTotal();
    return efectivo >= total ? efectivo - total : 0;
  }

  public void procesarFinalizarVenta(VentaDTO ventaActual) {
    ventaActual.setPagada(true);

    for (ItemVentaDTO item : ventaActual.getItems()) {
      inventario.reducirStock(item.getCodigo(), item.getCantidad());
    }

    for (ItemVentaDTO item : ventaActual.getItems()) {
      ProductoDTO estadoActual = inventario.obtenerProductoPorCodigo(item.getCodigo());
      if (estadoActual != null && estadoActual.getStock() < STOCK_MINIMO) {
        ejecutarProtocoloReabastecimiento(estadoActual);
      }
    }
  }

  public TicketDTO generarTicket(VentaDTO ventaActual, double ultimoEfectivo) {
    if (ventaActual == null) {
      return null;
    }

    double total = ventaActual.getTotal();
    double subtotal = ventaActual.getSubtotal();
    double iva = ventaActual.getIva();
    double cambio = ultimoEfectivo - total;
    String folio = generarFolio();

    LocalDateTime ahora = LocalDateTime.now();
    String fecha = ahora.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX")));
    String hora = ahora.format(DateTimeFormatter.ofPattern("hh:mm a"));

    return new TicketDTO(
      folio,
      ventaActual.getItems(),
      subtotal, iva, total, ultimoEfectivo, cambio,
      fecha, hora, CAJERO, NOMBRE_TIENDA, RFC, DIRECCION, TELEFONO
    );
  }

  private void ejecutarProtocoloReabastecimiento(ProductoDTO p) {
    String mensaje = "Alerta: El stock se encuentra bajo para el producto " + p.getNombre()
      + ". Solo quedan " + p.getStock() + " unidades disponibles.";
    boolean enviado = servicioNotificaciones.enviarNotificacionStock(p.getProveedor().getEmail(), mensaje);

    if (enviado) {
      System.out.println("Notificación de stock bajo enviada satisfactoriamente para: " + p.getNombre());
    }
  }

  private String generarFolio() {
    return "TK-" + System.currentTimeMillis();
  }

}
