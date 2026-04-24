package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;

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

  public InventarioControl getControl() {
    return control;
  }

}
