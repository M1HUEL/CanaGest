package diseñadores.negocios.usuarios;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.UsuarioRol;

import java.util.List;
import java.util.Optional;

public class UsuariosFacade implements IUsuarios {

  private final UsuariosControl control;

  public UsuariosFacade() {
    this.control = new UsuariosControl();
  }

  @Override
  public Optional<UsuarioDTO> autenticarse(String nombre, String contrasena) {
    return control.autenticar(nombre, contrasena);
  }

  @Override
  public List<UsuarioDTO> obtenerTodos() {
    return control.obtenerTodos();
  }

  @Override
  public void guardarUsuario(UsuarioDTO usuario) {
    control.guardarUsuario(usuario);
  }

  @Override
  public void actualizarUsuario(UsuarioDTO usuario) {
    control.actualizarUsuario(usuario);
  }

  @Override
  public void eliminarUsuario(String nombre) {
    control.eliminarUsuario(nombre);
  }

  @Override
  public void cambiarRol(String nombre, UsuarioRol nuevoRol) {
    control.cambiarRol(nombre, nuevoRol);
  }

}
