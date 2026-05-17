package diseñadores.negocios.autenticacion;

import diseñadores.negocios.dto.UsuarioDTO;
import java.util.Optional;

public class AutenticacionFacade implements IAutenticacion {

  private final AutenticacionControl control;

  public AutenticacionFacade() {
    this.control = new AutenticacionControl();
  }

  public AutenticacionFacade(AutenticacionControl control) {
    this.control = control;
  }

  @Override
  public Optional<UsuarioDTO> autenticar(String nombre, String contrasena) {
    return control.autenticar(nombre, contrasena);
  }

}
