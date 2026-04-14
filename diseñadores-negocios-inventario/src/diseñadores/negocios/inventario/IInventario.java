package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.Producto;

public interface IInventario {

  boolean verificarStock(String codigo, int cantidad);

  void reducirStock(String codigo, int cantidad);

  Producto obtenerProducto(String codigo);

  void actualizarStock(String codigo, int nuevaCantidad);

}
