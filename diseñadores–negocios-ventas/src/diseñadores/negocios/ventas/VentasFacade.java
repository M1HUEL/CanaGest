package diseñadores.negocios.ventas;

import diseñadores.infraestructura.dto.TipoPago;
import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.infraestructura.pagos.IPagos;
import diseñadores.negocios.dto.*;

import java.math.BigDecimal;
import java.util.List;

public class VentasFacade implements IVentas {

  private final VentasControl ventasControl;

  public VentasFacade(INotificaciones notificaciones, IPagos pagos) {
    this.ventasControl = new VentasControl(notificaciones, pagos);
  }

  @Override
  public VentaDTO iniciarNuevaVenta() {
    return new VentaDTO();
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO dto) {
    return ventasControl.existeProducto(dto);
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto) {
    return ventasControl.tieneStock(dto);
  }

  @Override
  public ProductoDTO procesarProducto(VentaDTO ventaActual, EscanearProductoDTO dto) {
    return ventasControl.procesarProducto(ventaActual, dto);
  }

  @Override
  public ResultadoPagoDTO procesarPagoEfectivo(VentaDTO ventaActual, PagoEfectivoDTO dto) {
    return ventasControl.procesarPagoEfectivo(ventaActual, dto);
  }

  @Override
  public ResultadoPagoDTO procesarPagoElectronico(VentaDTO ventaActual, TipoPago tipo, String datos) {
    return ventasControl.procesarPagoElectronico(ventaActual, tipo, datos);
  }

  @Override
  public BigDecimal procesarCalcularCambio(VentaDTO ventaActual, BigDecimal efectivo) {
    return ventasControl.procesarCalcularCambio(ventaActual, efectivo);
  }

  @Override
  public void procesarFinalizarVenta(VentaDTO ventaActual) {
    ventasControl.procesarFinalizarVenta(ventaActual);
  }

  @Override
  public VentaDTO obtenerResumenVenta(VentaDTO ventaActual) {
    return ventaActual;
  }

  @Override
  public TicketDTO generarTicket(VentaDTO ventaActual, BigDecimal montoRecibido) {
    return ventasControl.generarTicket(ventaActual, montoRecibido);
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return ventasControl.obtenerCatalogo();
  }

}
