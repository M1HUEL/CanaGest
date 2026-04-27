package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;
import java.util.List;

public interface IInventario {

  ProductoDTO obtenerProductoPorCodigo(String codigo);

  boolean verificarStock(String codigo, int cantidad);

  void reducirStock(String codigo, int cantidad);

  void actualizarStock(String codigo, int nuevaCantidad);

  void actualizarStockCompleto(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo);

  List<ProductoDTO> obtenerTodos();

  List<ProductoDTO> obtenerProductosBajoMinimo();

  List<ProductoDTO> necesitanReorden();

  void ajustarStock(String codigo, int stockFisico);

  int[] obtenerEstadisticasConteo();

}
