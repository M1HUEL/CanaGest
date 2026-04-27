package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;
import java.util.List;

public class InventarioFacade implements IInventario {

  private final InventarioControl control;

  public InventarioFacade() {
    this.control = new InventarioControl();
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
  public void reducirStock(String codigo, int cantidad) {
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

  public InventarioControl getControl() {
    return control;
  }

}