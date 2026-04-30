package diseñadores.negocios.ventas;

import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.productos.IProductos;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentasControl {

  private final IProductos productosFacade;
  private final IInventario inventario;
  private final INotificaciones servicioNotificaciones;

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

    int cantidadEnCarrito = ventaActual.getItems().stream()
      .filter(i -> i.getCodigo().equals(productoDTO.getCodigo()))
      .mapToInt(ItemVentaDTO::getCantidad)
      .sum();

    if (productoDTO.getStock() <= cantidadEnCarrito) {
      return null;
    }

    ventaActual.agregarProducto(productoDTO);

    return productoDTO;
  }

  public ResultadoPagoDTO procesarPagoEfectivo(VentaDTO ventaActual, PagoEfectivoDTO dto) {
    BigDecimal total = ventaActual.getTotal();
    BigDecimal recibido = dto.getMontoRecibido();

    if (recibido.compareTo(total) < 0) {
      BigDecimal faltante = total.subtract(recibido);
      return ResultadoPagoDTO.rechazado(
        String.format("Monto insuficiente. Faltan $%.2f para completar el pago.", faltante));
    }

    return ResultadoPagoDTO.aprobado(recibido.subtract(total));
  }

  public BigDecimal procesarCalcularCambio(VentaDTO ventaActual, BigDecimal efectivo) {
    if (ventaActual == null) {
      return BigDecimal.ZERO;
    }
    BigDecimal total = ventaActual.getTotal();
    return efectivo.compareTo(total) >= 0 ? efectivo.subtract(total) : BigDecimal.ZERO;
  }

  public void procesarFinalizarVenta(VentaDTO ventaActual) {
    ventaActual.setPagada(true);
    ventaActual.setFolio(generarFolio());

    for (ItemVentaDTO item : ventaActual.getItems()) {
      inventario.reducirStock(item.getCodigo(), item.getCantidad());

      ProductoDTO estadoActual = inventario.obtenerProductoPorCodigo(item.getCodigo());
      if (estadoActual != null && estadoActual.getStock() < STOCK_MINIMO) {
        ejecutarProtocoloReabastecimiento(estadoActual);
      }
    }
  }

  public TicketDTO generarTicket(VentaDTO ventaActual, BigDecimal ultimoEfectivo) {
    if (ventaActual == null) {
      return null;
    }

    BigDecimal total = ventaActual.getTotal();
    BigDecimal subtotal = ventaActual.getSubtotal();
    BigDecimal iva = ventaActual.getIva();
    BigDecimal cambio = ultimoEfectivo.subtract(total);
    LocalDateTime ahora = LocalDateTime.now();

    return new TicketDTO(
      ventaActual.getFolio(),
      ventaActual.getItems(),
      subtotal, iva, total, ultimoEfectivo, cambio,
      ahora, CAJERO, NOMBRE_TIENDA, RFC, DIRECCION, TELEFONO
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
