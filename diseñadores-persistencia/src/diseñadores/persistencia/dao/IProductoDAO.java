package diseñadores.persistencia.dao;

import diseñadores.negocios.dto.ProductoDTO;
import java.util.List;

public interface IProductoDAO {

  List<ProductoDTO> obtenerTodos();

  ProductoDTO obtenerPorCodigo(String codigo);

  void guardar(ProductoDTO producto);

  void actualizar(ProductoDTO producto);

  void eliminar(String codigo);

}
