package diseñadores.negocios.ventas;

import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.infraestructura.notificaciones.NotificacionesFacade;
import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.InventarioFacade;
import diseñadores.negocios.productos.IProductos;
import diseñadores.negocios.productos.ProductosFacade;
import java.util.List;

public class VentasFacade implements IVentas {

  private final IProductos productosSubsistema;
  private final InventarioFacade inventarioSubsistema;
  private final VentasControl ventasControl;
  private final INotificaciones notificacionesSubsistema;

  public VentasFacade() {
    this.productosSubsistema = new ProductosFacade();
    this.inventarioSubsistema = new InventarioFacade();
    this.notificacionesSubsistema = new NotificacionesFacade();
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
  public double procesarCalcularCambio(VentaDTO ventaActual, double efectivo) {
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
  public TicketDTO generarTicket(VentaDTO ventaActual, double montoRecibido) {
    return ventasControl.generarTicket(ventaActual, montoRecibido);
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return productosSubsistema.obtenerCatalogo();
  }

}
