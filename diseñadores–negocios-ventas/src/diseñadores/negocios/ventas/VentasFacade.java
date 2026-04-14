package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.PagoEfectivoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ResultadoPagoDTO;
import diseñadores.negocios.dto.TicketDTO;
import diseñadores.negocios.dto.VentaDTO;
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
  public void nuevaVenta() {
    ventasControl.iniciarNuevaVenta();
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO dto) {
    return productosControl.existeProducto(dto);
  }

  @Override
  public ProductoDTO procesarProducto(EscanearProductoDTO dto) {
    return ventasControl.procesarProducto(dto);
  }

  @Override
  public ResultadoPagoDTO procesarPagoEfectivo(PagoEfectivoDTO dto) {
    return ventasControl.procesarPagoEfectivo(dto);
  }

  @Override
  public double calcularCambio(double efectivo) {
    return ventasControl.calcularCambio(efectivo);
  }

  @Override
  public void procesarFinalizarVenta() {
    ventasControl.procesarFinalizarVenta();
  }

  @Override
  public VentaDTO obtenerVentaActual() {
    return ventasControl.obtenerVentaDTO();
  }

  @Override
  public TicketDTO generarTicket() {
    return ventasControl.generarTicket();
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return productosControl.obtenerCatalogo();
  }

}
