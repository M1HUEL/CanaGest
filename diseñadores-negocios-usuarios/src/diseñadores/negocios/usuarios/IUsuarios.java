package diseñadores.negocios.usuarios;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.UsuarioRol;

import java.util.List;
import java.util.Optional;

public interface IUsuarios {

  Optional<UsuarioDTO> autenticarse(String nombre, String contrasena);

  List<UsuarioDTO> obtenerTodos();

  void guardarUsuario(UsuarioDTO usuario);

  void actualizarUsuario(UsuarioDTO usuario);

  void eliminarUsuario(String nombre);

  void cambiarRol(String nombre, UsuarioRol nuevoRol);

}
