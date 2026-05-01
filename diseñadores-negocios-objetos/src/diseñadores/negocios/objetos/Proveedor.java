package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.persistencia.IPersistencia;
import diseñadores.persistencia.PersistenciaFacade;

import java.util.List;

public class Proveedor {

  private static final IPersistencia PERSISTENCIA = PersistenciaFacade.getInstancia();

  public static List<ProveedorDTO> obtenerTodos() {
    return PERSISTENCIA.obtenerProveedores();
  }

  public static ProveedorDTO obtenerPorCodigo(String codigo) {
    return PERSISTENCIA.obtenerProveedorPorCodigo(codigo);
  }

  public static void guardar(ProveedorDTO proveedor) {
    PERSISTENCIA.guardarProveedor(proveedor);
  }

  public static void actualizar(ProveedorDTO proveedor) {
    PERSISTENCIA.actualizarProveedor(proveedor);
  }

  public static void eliminar(String codigo) {
    PERSISTENCIA.eliminarProveedor(codigo);
  }

}
