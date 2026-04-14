package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.InventarioFacade;
import diseñadores.negocios.productos.ProductosFacade;
import java.util.List;

public class VentasFacade implements IVentas {

  private final ProductosFacade productosSubsistema;
  private final InventarioFacade inventarioSubsistema;
  private final VentasControl ventasControl;

  public VentasFacade() {
    this.productosSubsistema = new ProductosFacade();
    this.inventarioSubsistema = new InventarioFacade();
    this.ventasControl = new VentasControl(
      productosSubsistema.getControl(),
      inventarioSubsistema
    );
  }

  @Override
  public Venta iniciarNuevaVenta() {
    return new Venta();
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
  public ProductoDTO procesarProducto(Venta ventaActual, EscanearProductoDTO dto) {
    return ventasControl.procesarProducto(ventaActual, dto);
  }

  @Override
  public ResultadoPagoDTO procesarPagoEfectivo(Venta ventaActual, PagoEfectivoDTO dto) {
    return ventasControl.procesarPagoEfectivo(ventaActual, dto);
  }

  @Override
  public double calcularCambio(Venta ventaActual, double efectivo) {
    if (ventaActual == null) {
      return 0;
    }
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
    return productosSubsistema.obtenerCatalogo();
  }

}
