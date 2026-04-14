package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;

public class InventarioFacade implements IInventario {

  private final InventarioControl control;

  public InventarioFacade() {
    this.control = new InventarioControl();
  }

  @Override
  public boolean verificarStock(String codigo, int cantidad) {
    return control.verificarStock(codigo, cantidad);
  }

  @Override
  public void reducirStock(String codigo, int cantidad) {
    control.descontar(codigo, cantidad);
  }

  @Override
  public void actualizarStock(String codigo, int nuevaCantidad) {
    control.actualizarStock(codigo, nuevaCantidad);
  }

  @Override
  public ProductoDTO obtenerProducto(String codigo) {
    return control.obtenerProductoDTO(codigo);
  }

  public InventarioControl getControl() {
    return control;
  }

}
