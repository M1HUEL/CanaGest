package diseñadores.negocios.usuarios;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.UsuarioRol;

import java.util.List;

public interface IUsuarios {

  List<UsuarioDTO> obtenerUsuarios();

  void guardarUsuario(UsuarioDTO usuario);

  void actualizarUsuario(UsuarioDTO usuario);

  void eliminarUsuario(String nombre);

  void cambiarRol(String nombre, UsuarioRol nuevoRol);

}
