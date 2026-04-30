package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;
import java.util.List;

public class InventarioFacade implements IInventario {

  private final InventarioControl control;

  public InventarioFacade(InventarioControl control) {
    this.control = control;
  }

  @Override
  public ProductoDTO obtenerProductoPorCodigo(String codigo) {
    return control.obtenerProductoPorCodigo(codigo);
  }

  @Override
  public List<ProductoDTO> obtenerTodos() {
    return control.obtenerTodos();
  }

  @Override
  public List<ProductoDTO> obtenerProductosBajoMinimo() {
    return control.obtenerProductosBajoMinimo();
  }

  @Override
  public List<ProductoDTO> necesitanReorden() {
    return control.necesitanReorden();
  }

  @Override
  public boolean verificarStock(String codigo, int cantidad) {
    return control.verificarStock(codigo, cantidad);
  }

  @Override
  public void descontarStock(String codigo, int cantidad) {
    control.descontarStock(codigo, cantidad);
  }

  @Override
  public void actualizarStock(String codigo, int nuevaCantidad) {
    control.actualizarStock(codigo, nuevaCantidad);
  }

  @Override
  public void actualizarStockCompleto(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    control.actualizarStockCompleto(codigo, nuevoStock, nuevoMinimo, nuevoMaximo);
  }

  @Override
  public void ajustarStock(String codigo, int stockFisico) {
    control.ajustarStock(codigo, stockFisico);
  }

  @Override
  public int[] obtenerEstadisticasConteo() {
    return control.obtenerEstadisticasConteo();
  }

}
