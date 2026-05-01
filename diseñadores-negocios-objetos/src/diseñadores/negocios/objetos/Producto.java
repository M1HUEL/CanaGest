package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.persistencia.dao.IProductoDAO;
import diseñadores.persistencia.dao.impl.ProductoDAOImpl;
import java.util.List;

public class Producto {

  private static final IProductoDAO DAO = new ProductoDAOImpl();

  public static List<ProductoDTO> obtenerTodos() {
    return DAO.obtenerTodos();
  }

  public static ProductoDTO obtenerPorCodigo(String codigo) {
    return DAO.obtenerPorCodigo(codigo);
  }

  public static void guardar(ProductoDTO producto) {
    DAO.guardar(producto);
  }

  public static void actualizar(ProductoDTO producto) {
    DAO.actualizar(producto);
  }

  public static void eliminar(String codigo) {
    DAO.eliminar(codigo);
  }

}
