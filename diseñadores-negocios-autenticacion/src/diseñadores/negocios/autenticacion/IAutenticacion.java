package diseñadores.negocios.autenticacion;

import diseñadores.negocios.dto.UsuarioDTO;
import java.util.Optional;

public interface IAutenticacion {

  Optional<UsuarioDTO> autenticar(String nombre, String contrasena);

}
