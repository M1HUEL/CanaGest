package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.Producto;

public class InventarioFacade implements IInventario {

  private final InventarioControl control;

  public InventarioFacade() {
    this.control = new InventarioControl();
  }

  @Override
  public boolean verificarStock(String codigo, int cantidad) {
    Producto p = control.obtenerEntidad(codigo);
    return p != null && p.getStock() >= cantidad;
  }

  @Override
  public void reducirStock(String codigo, int cantidad) {
    control.descontar(codigo, cantidad);
  }

  @Override
  public Producto obtenerProducto(String codigo) {
    return control.obtenerEntidad(codigo);
  }

  @Override
  public void actualizarStock(String codigo, int nuevaCantidad) {
    Producto p = control.obtenerEntidad(codigo);
    if (p != null) {
      p.setStock(nuevaCantidad);
    }
  }

}
