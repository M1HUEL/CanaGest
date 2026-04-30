package diseñadores.negocios.ventas;

import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.productos.IProductos;
import java.math.BigDecimal;
import java.util.List;

public class VentasFacade implements IVentas {

  private final IProductos productosSubsistema;
  private final IInventario inventarioSubsistema;
  private final VentasControl ventasControl;
  private final INotificaciones notificacionesSubsistema;

  public VentasFacade(IProductos productosSubsistema, IInventario inventarioSubsistema, INotificaciones notificacionesSubsistema) {
    this.productosSubsistema = productosSubsistema;
    this.inventarioSubsistema = inventarioSubsistema;
    this.notificacionesSubsistema = notificacionesSubsistema;
    this.ventasControl = new VentasControl(
      productosSubsistema,
      inventarioSubsistema,
      notificacionesSubsistema
    );
  }

  @Override
  public VentaDTO iniciarNuevaVenta() {
    return new VentaDTO();
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO dto) {
    return productosSubsistema.existeProducto(dto);
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto) {
    return productosSubsistema.tieneStock(dto);
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
    return productosSubsistema.obtenerCatalogo();
  }

}
