package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.productos.ProductosControl;
import diseñadores.negocios.ventas.notificacion.IServicioNotificacion;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

  public ProductoDTO procesarProducto(VentaDTO ventaActual, EscanearProductoDTO dto) {
    ProductoDTO productoDTO = productosControl.buscar(dto);

    if (productoDTO == null) {
      return null;
    }

    ventaActual.agregarProducto(productoDTO);
    inventario.reducirStock(dto.getCodigo(), 1);

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

    ventaActual.setPagada(true);
    return ResultadoPagoDTO.aprobado(recibido - total);
  }

  public void procesarFinalizarVenta(VentaDTO ventaActual) {
    ventaActual.setPagada(true);

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
    String folio = "TK-" + System.currentTimeMillis();

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
    if (servicioCorreo == null) {
      System.out.println("El stock se encuentra bajo para el producto: " + p.getNombre()
        + ". Cantidad actual: " + p.getStock() + " unidades.");
      return;
    }

    String mensaje = "Alerta: El stock se encuentra bajo para el producto " + p.getNombre()
      + ". Solo quedan " + p.getStock() + " unidades disponibles.";
    boolean enviado = servicioCorreo.enviarNotificacionStock(p.getProveedor().getEmail(), mensaje);

    if (enviado) {
      System.out.println("Notificación de stock bajo enviada satisfactoriamente para: " + p.getNombre());
    }
  }

}
