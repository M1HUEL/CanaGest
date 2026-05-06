package diseñadores.negocios.ventas;

import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.infraestructura.pagos.IPagos;
import diseñadores.negocios.dto.*;
import diseñadores.negocios.productos.IProductos;
import java.math.BigDecimal;
import java.util.List;

public class VentasFacade implements IVentas {

  private final VentasControl control;

  public VentasFacade() {
    this.control = new VentasControl();
  }

  public VentasFacade(INotificaciones n, IPagos p, IProductos pr) {
    this.control = new VentasControl(n, p, pr);
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return control.obtenerCatalogo();
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO d) {
    return control.existeProducto(d);
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO d) {
    return control.tieneStock(d);
  }

  @Override
  public ProductoDTO procesarProducto(VentaDTO v, EscanearProductoDTO d) {
    return control.procesarProducto(v, d);
  }

  @Override
  public ResultadoPagoDTO procesarPagoEfectivo(VentaDTO v, PagoEfectivoDTO d) {
    return control.procesarPagoEfectivo(v, d);
  }

  @Override
  public ResultadoPagoDTO procesarPagoTarjeta(VentaDTO v, PagoTarjetaDTO d) {
    return control.procesarPagoTarjeta(v, d);
  }

  @Override
  public ResultadoPagoDTO procesarPagoTransferencia(VentaDTO v, PagoTransferenciaDTO d) {
    return control.procesarPagoTransferencia(v, d);
  }

  @Override
  public ResultadoPagoDTO procesarPagoQr(VentaDTO v, PagoQrDTO d) {
    return control.procesarPagoQr(v, d);
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
