package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;

public interface IInventario {

  ProductoDTO obtenerProductoPorCodigo(String codigo);

  boolean verificarStock(String codigo, int cantidad);

  void reducirStock(String codigo, int cantidad);

  void actualizarStock(String codigo, int nuevaCantidad);

}
