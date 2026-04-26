package diseñadores.negocios.usuarios;

import diseñadores.negocios.dto.UsuarioDTO;

public class UsuariosFacade implements IUsuarios {

  private final UsuariosControl control;

  public UsuariosFacade() {
    this.control = new UsuariosControl();
  }

  public UsuariosFacade(UsuariosControl control) {
    this.control = control;
  }

  @Override
  public UsuarioDTO autenticarse(String usuario, String contrasena) {
    if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
      return null;
    }
    return control.autenticar(usuario, contrasena);
  }

}
