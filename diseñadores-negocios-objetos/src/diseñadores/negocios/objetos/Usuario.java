package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.persistencia.IPersistencia;
import diseñadores.persistencia.PersistenciaFacade;

import java.util.List;
import java.util.Optional;

public class Usuario {

  private static final IPersistencia PERSISTENCIA = PersistenciaFacade.getInstancia();

  public static List<UsuarioDTO> obtenerTodos() {
    return PERSISTENCIA.obtenerUsuarios();
  }

  public static Optional<UsuarioDTO> autenticar(String nombre, String contrasena) {
    Optional<UsuarioDTO> resultado = PERSISTENCIA.obtenerUsuarioPorNombre(nombre);
    return resultado.filter(u -> u.getContrasena().equals(contrasena));
  }

  public static void guardar(UsuarioDTO usuario) {
    PERSISTENCIA.guardarUsuario(usuario);
  }

  public static void actualizar(UsuarioDTO usuario) {
    PERSISTENCIA.actualizarUsuario(usuario);
  }

  public static void eliminar(String nombre) {
    PERSISTENCIA.eliminarUsuario(nombre);
  }

}
