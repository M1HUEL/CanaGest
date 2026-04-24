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
  public double calcularCambio(VentaDTO ventaActual, double efectivo) {
    if (ventaActual == null) {
      return 0;
    }
    double total = ventaActual.getTotal();
    return efectivo >= total ? efectivo - total : 0;
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
