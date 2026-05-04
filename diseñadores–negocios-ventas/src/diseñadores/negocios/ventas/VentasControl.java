package diseñadores.negocios.ventas;

import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.TipoPago;
import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.infraestructura.notificaciones.NotificacionesFacade;
import diseñadores.infraestructura.pagos.IPagos;
import diseñadores.infraestructura.pagos.PagosFacade;
import diseñadores.negocios.dto.*;
import diseñadores.negocios.objetos.Inventario;
import diseñadores.negocios.objetos.Producto;
import diseñadores.negocios.objetos.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VentasControl {

  private final INotificaciones servicioNotificaciones;
  private final IPagos serviciosPagos;

  private static final int STOCK_MINIMO = 3;
  private static final String NOMBRE_TIENDA = "La Canasta";
  private static final String RFC = "LCA123456ABC";
  private static final String DIRECCION = "Av. Principal #123, Col. Centro";
  private static final String TELEFONO = "Tel: (555) 123-4567";
  private static final String CAJERO = "Juan Pérez - Caja #1";

  public VentasControl() {
    this.servicioNotificaciones = new NotificacionesFacade();
    this.serviciosPagos = new PagosFacade();
  }

  public VentasControl(INotificaciones servicioNotificaciones, IPagos serviciosPagos) {
    this.servicioNotificaciones = servicioNotificaciones;
    this.serviciosPagos = serviciosPagos;
  }

  public List<ProductoDTO> obtenerCatalogo() {
    return Producto.obtenerTodos();
  }

  public boolean existeProducto(EscanearProductoDTO dto) {
    if (dto == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
      return false;
    }
    return Producto.obtenerPorCodigo(dto.getCodigo()) != null;
  }

  public boolean tieneStock(EscanearProductoDTO dto) {
    if (dto == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
      return false;
    }
    ProductoDTO p = Producto.obtenerPorCodigo(dto.getCodigo());
    return p != null && p.getStock() >= 1;
  }

  public ProductoDTO procesarProducto(VentaDTO ventaActual, EscanearProductoDTO dto) {
    if (ventaActual == null) {
      throw new IllegalArgumentException("La venta no puede ser nula.");
    }
    if (dto == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
      throw new IllegalArgumentException("El código del producto no puede estar vacío.");
    }

    ProductoDTO producto = Producto.obtenerPorCodigo(dto.getCodigo());
    if (producto == null) {
      return null;
    }

    int cantidadEnCarrito = ventaActual.getItems().stream()
      .filter(i -> i.getCodigo().equals(producto.getCodigo()))
      .mapToInt(ItemVentaDTO::getCantidad)
      .sum();

    if (producto.getStock() <= cantidadEnCarrito) {
      return null;
    }
    ventaActual.agregarProducto(producto);
    return producto;
  }

  public ResultadoPagoDTO procesarPagoEfectivo(VentaDTO ventaActual, PagoEfectivoDTO dto) {
    validarVentaConItems(ventaActual);
    if (dto == null || dto.getMontoRecibido() == null) {
      throw new IllegalArgumentException("El monto recibido no puede ser nulo.");
    }
    if (dto.getMontoRecibido().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El monto recibido debe ser mayor a cero.");
    }

    BigDecimal total = ventaActual.getTotal();
    BigDecimal recibido = dto.getMontoRecibido();

    if (recibido.compareTo(total) < 0) {
      BigDecimal faltante = total.subtract(recibido);
      return ResultadoPagoDTO.rechazado(
        String.format("Monto insuficiente. Faltan $%.2f para completar el pago.", faltante));
    }
    return ResultadoPagoDTO.aprobado(recibido.subtract(total));
  }

  public ResultadoPagoDTO procesarPagoTarjeta(VentaDTO ventaActual, PagoTarjetaDTO dto) {
    validarVentaConItems(ventaActual);
    if (dto == null) {
      throw new IllegalArgumentException("Los datos de tarjeta no pueden ser nulos.");
    }

    String datos = "numero=" + dto.getNumero() + "|titular=" + dto.getTitular();
    return procesarPagoElectronico(ventaActual, TipoPago.TARJETA, datos);
  }

  public ResultadoPagoDTO procesarPagoTransferencia(VentaDTO ventaActual, PagoTransferenciaDTO dto) {
    validarVentaConItems(ventaActual);
    if (dto == null) {
      throw new IllegalArgumentException("Los datos de transferencia no pueden ser nulos.");
    }

    String datos = "clabe=" + dto.getClabe() + "|referencia=" + dto.getReferencia();
    return procesarPagoElectronico(ventaActual, TipoPago.TRANSACCION, datos);
  }

  public ResultadoPagoDTO procesarPagoCoDi(VentaDTO ventaActual, PagoQrDTO dto) {
    validarVentaConItems(ventaActual);
    if (dto == null) {
      throw new IllegalArgumentException("Los datos de CoDi no pueden ser nulos.");
    }

    String datos = "referencia=" + dto.getReferencia();
    return procesarPagoElectronico(ventaActual, TipoPago.QR, datos);
  }

  ResultadoPagoDTO procesarPagoElectronico(VentaDTO ventaActual, TipoPago tipo, String datos) {
    String referencia = "VENTA-" + System.currentTimeMillis();

    RespuestaPagoDTO respuesta = serviciosPagos.procesarPago(
      tipo, ventaActual.getTotal(), referencia, datos);

    if (respuesta.isExitoso()) {
      return ResultadoPagoDTO.aprobado(respuesta.getCodigoAutorizacion());
    } else {
      return ResultadoPagoDTO.rechazado(respuesta.getMensaje());
    }
  }

  public BigDecimal procesarCalcularCambio(VentaDTO ventaActual, BigDecimal efectivo) {
    if (ventaActual == null || efectivo == null) {
      return BigDecimal.ZERO;
    }
    BigDecimal total = ventaActual.getTotal();
    return efectivo.compareTo(total) >= 0 ? efectivo.subtract(total) : BigDecimal.ZERO;
  }

  public void procesarFinalizarVenta(VentaDTO ventaActual) {
    if (ventaActual == null) {
      throw new IllegalArgumentException("La venta no puede ser nula.");
    }
    if (ventaActual.getItems().isEmpty()) {
      throw new IllegalStateException("Venta sin productos.");
    }
    if (ventaActual.isPagada()) {
      throw new IllegalStateException("La venta ya fue pagada.");
    }

    ventaActual.setPagada(true);
    ventaActual.setFolio(generarFolio());

    for (ItemVentaDTO item : ventaActual.getItems()) {
      Inventario.descontarStock(item.getCodigo(), item.getCantidad());
      ProductoDTO estado = Inventario.obtenerProductoPorCodigo(item.getCodigo());
      if (estado != null && estado.getStock() < STOCK_MINIMO) {
        ejecutarProtocoloReabastecimiento(estado);
      }
    }
    Venta.guardar(ventaActual);
  }

  public TicketDTO generarTicket(VentaDTO ventaActual, BigDecimal ultimoEfectivo) {
    if (ventaActual == null) {
      throw new IllegalArgumentException("La venta no puede ser nula.");
    }
    if (!ventaActual.isPagada()) {
      throw new IllegalStateException("La venta no ha sido pagada.");
    }
    if (ultimoEfectivo == null || ultimoEfectivo.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Monto de efectivo inválido.");
    }

    BigDecimal cambio = ultimoEfectivo.compareTo(BigDecimal.ZERO) > 0
      ? ultimoEfectivo.subtract(ventaActual.getTotal())
      : BigDecimal.ZERO;

    return new TicketDTO(
      ventaActual.getFolio(),
      ventaActual.getItems(),
      ventaActual.getSubtotal(),
      ventaActual.getIva(),
      ventaActual.getTotal(),
      ultimoEfectivo,
      cambio,
      LocalDateTime.now(),
      CAJERO, NOMBRE_TIENDA, RFC, DIRECCION, TELEFONO,
      ventaActual.getTipoPago()
    );
  }

  private void validarVentaConItems(VentaDTO venta) {
    if (venta == null) {
      throw new IllegalArgumentException("La venta no puede ser nula.");
    }
    if (venta.getItems().isEmpty()) {
      throw new IllegalStateException("No se puede pagar una venta sin productos.");
    }
  }

  private void ejecutarProtocoloReabastecimiento(ProductoDTO p) {
    String msg = "Alerta: stock bajo para " + p.getNombre() + ". Quedan " + p.getStock() + " unidades.";
    boolean enviado = servicioNotificaciones.enviarNotificacionStock(p.getProveedor().getEmail(), msg);
    if (enviado) {
      System.out.println("[Stock] Notificación enviada: " + p.getNombre());
    }
  }

  private String generarFolio() {
    return "TK-" + System.currentTimeMillis();
  }

}
