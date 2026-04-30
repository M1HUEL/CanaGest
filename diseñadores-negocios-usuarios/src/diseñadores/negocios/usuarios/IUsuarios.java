package diseñadores.negocios.usuarios;

import diseñadores.negocios.dto.UsuarioDTO;
import java.util.Optional;

public interface IUsuarios {

  Optional<UsuarioDTO> autenticarse(String usuario, String contrasena);

}
