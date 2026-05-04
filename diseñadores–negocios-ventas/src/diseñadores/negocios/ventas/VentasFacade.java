package diseñadores.negocios.ventas;

import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.infraestructura.pagos.IPagos;
import diseñadores.negocios.dto.*;

import java.math.BigDecimal;
import java.util.List;

public class VentasFacade implements IVentas {

  private final VentasControl control;

  public VentasFacade(INotificaciones notificaciones, IPagos pagos) {
    this.control = new VentasControl(notificaciones, pagos);
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return control.obtenerCatalogo();
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO dto) {
    return control.existeProducto(dto);
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto) {
    return control.tieneStock(dto);
  }

  @Override
  public ProductoDTO procesarProducto(VentaDTO v, EscanearProductoDTO dto) {
    return control.procesarProducto(v, dto);
  }

  @Override
  public ResultadoPagoDTO procesarPagoEfectivo(VentaDTO v, PagoEfectivoDTO dto) {
    return control.procesarPagoEfectivo(v, dto);
  }

  @Override
  public ResultadoPagoDTO procesarPagoTarjeta(VentaDTO v, PagoTarjetaDTO dto) {
    return control.procesarPagoTarjeta(v, dto);
  }

  @Override
  public ResultadoPagoDTO procesarPagoTransferencia(VentaDTO v, PagoTransferenciaDTO dto) {
    return control.procesarPagoTransferencia(v, dto);
  }

  @Override
  public ResultadoPagoDTO procesarPagoCoDi(VentaDTO v, PagoQrDTO dto) {
    return control.procesarPagoCoDi(v, dto);
  }

  @Override
  public BigDecimal procesarCalcularCambio(VentaDTO v, BigDecimal ef) {
    return control.procesarCalcularCambio(v, ef);
  }

  @Override
  public void procesarFinalizarVenta(VentaDTO v) {
    control.procesarFinalizarVenta(v);
  }

  @Override
  public TicketDTO generarTicket(VentaDTO v, BigDecimal ef) {
    return control.generarTicket(v, ef);
  }

}
