package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.persistencia.IPersistencia;
import diseñadores.persistencia.PersistenciaFacade;
import java.util.List;

public class Producto {

  private static final IPersistencia PERSISTENCIA = PersistenciaFacade.getInstancia();

  public static List<ProductoDTO> obtenerTodos() {
    return PERSISTENCIA.obtenerProductos();
  }

  public static ProductoDTO obtenerPorCodigo(String codigo) {
    return PERSISTENCIA.obtenerProductoPorCodigo(codigo);
  }

  public static void guardar(ProductoDTO producto) {
    PERSISTENCIA.guardarProducto(producto);
  }

  public static void actualizar(ProductoDTO producto) {
    PERSISTENCIA.actualizarProducto(producto);
  }

  public static void eliminar(String codigo) {
    PERSISTENCIA.eliminarProducto(codigo);
  }

}
