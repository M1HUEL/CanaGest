package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.productos.ProductosControl;
import java.util.List;

public class VentasFacade implements IVentas {

  private final ProductosControl productosControl;
  private final VentasControl ventasControl;

  public VentasFacade() {
    this.productosControl = new ProductosControl();
    this.ventasControl = new VentasControl(productosControl);
  }

  @Override
  public Venta iniciarNuevaVenta() {
    return new Venta();
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO dto) {
    return productosControl.existeProducto(dto);
  }

  @Override
  public ProductoDTO procesarProducto(Venta ventaActual, EscanearProductoDTO dto) {
    return ventasControl.procesarProducto(ventaActual, dto);
  }

  @Override
  public ResultadoPagoDTO procesarPagoEfectivo(Venta ventaActual, PagoEfectivoDTO dto) {
    return ventasControl.procesarPagoEfectivo(ventaActual, dto);
  }

  @Override
  public double calcularCambio(Venta ventaActual, double efectivo) {
    double total = ventaActual.getSubtotalVenta();
    return efectivo >= total ? efectivo - total : 0;
  }

  @Override
  public void procesarFinalizarVenta(Venta ventaActual) {
    ventasControl.procesarFinalizarVenta(ventaActual);
  }

  @Override
  public VentaDTO obtenerResumenVenta(Venta ventaActual) {
    return ventasControl.crearVentaDTO(ventaActual);
  }

  @Override
  public TicketDTO generarTicket(Venta ventaActual, double montoRecibido) {
    return ventasControl.generarTicket(ventaActual, montoRecibido);
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return productosControl.obtenerCatalogo();
  }

}
