package diseñadores.negocios.ventas;

import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.TipoPago;
import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.infraestructura.pagos.IPagos;
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
    if (ventaActual == null) {
      throw new IllegalArgumentException("La venta no puede ser nula.");
    }
    if (dto == null || dto.getMontoRecibido() == null) {
      throw new IllegalArgumentException("El monto recibido no puede ser nulo.");
    }
    if (dto.getMontoRecibido().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El monto recibido debe ser mayor a cero.");
    }
    if (ventaActual.getItems().isEmpty()) {
      throw new IllegalStateException("No se puede pagar una venta sin productos.");
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

  public ResultadoPagoDTO procesarPagoElectronico(VentaDTO ventaActual, TipoPago tipo, String datos) {
    if (ventaActual == null) {
      throw new IllegalArgumentException("La venta no puede ser nula.");
    }
    if (tipo == null) {
      throw new IllegalArgumentException("El tipo de pago no puede ser nulo.");
    }
    if (datos == null || datos.isBlank()) {
      throw new IllegalArgumentException("Los datos del pago no pueden estar vacíos.");
    }
    if (ventaActual.getItems().isEmpty()) {
      throw new IllegalStateException("No se puede pagar una venta sin productos.");
    }

    String referencia = "VENTA-" + System.currentTimeMillis();

    RespuestaPagoDTO response = serviciosPagos.procesarPago(
      tipo,
      ventaActual.getTotal(),
      referencia,
      datos
    );

    if (response.isExitoso()) {
      return ResultadoPagoDTO.aprobado(BigDecimal.ZERO);
    } else {
      return ResultadoPagoDTO.rechazado(response.getMensaje());
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
      throw new IllegalStateException("No se puede finalizar una venta sin productos.");
    }
    if (ventaActual.isPagada()) {
      throw new IllegalStateException("La venta ya fue pagada anteriormente.");
    }

    ventaActual.setPagada(true);
    ventaActual.setFolio(generarFolio());

    for (ItemVentaDTO item : ventaActual.getItems()) {
      Inventario.descontarStock(item.getCodigo(), item.getCantidad());
      ProductoDTO estadoActual = Inventario.obtenerProductoPorCodigo(item.getCodigo());
      if (estadoActual != null && estadoActual.getStock() < STOCK_MINIMO) {
        ejecutarProtocoloReabastecimiento(estadoActual);
      }
    }

    Venta.guardar(ventaActual);
  }

  public TicketDTO generarTicket(VentaDTO ventaActual, BigDecimal ultimoEfectivo) {
    if (ventaActual == null) {
      throw new IllegalArgumentException("La venta no puede ser nula.");
    }
    if (!ventaActual.isPagada()) {
      throw new IllegalStateException("No se puede generar un ticket de una venta no pagada.");
    }
    if (ultimoEfectivo == null || ultimoEfectivo.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("El monto de efectivo no es válido.");
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
      LocalDateTime.now(), CAJERO, NOMBRE_TIENDA, RFC, DIRECCION, TELEFONO
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
