package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.persistencia.dao.IUsuarioDAO;
import diseñadores.persistencia.dao.impl.UsuarioDAOImpl;
import java.util.List;
import java.util.Optional;

public class Usuario {

  private static final IUsuarioDAO DAO = new UsuarioDAOImpl();

  public static List<UsuarioDTO> obtenerTodos() {
    return DAO.obtenerTodos();
  }

  public static Optional<UsuarioDTO> autenticar(String nombre, String contrasena) {
    UsuarioDTO usuario = DAO.obtenerPorNombre(nombre);
    if (usuario == null) {
      return Optional.empty();
    }
    if (!usuario.getContrasena().equals(contrasena)) {
      return Optional.empty();
    }
    return Optional.of(usuario);
  }

  public static void guardar(UsuarioDTO usuario) {
    DAO.guardar(usuario);
  }

  public static void actualizar(UsuarioDTO usuario) {
    DAO.actualizar(usuario);
  }

  public static void eliminar(String nombre) {
    DAO.eliminar(nombre);
  }

}
