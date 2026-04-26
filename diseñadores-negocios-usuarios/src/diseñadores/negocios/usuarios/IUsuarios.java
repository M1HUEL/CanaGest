package diseñadores.negocios.usuarios;

import diseñadores.negocios.dto.UsuarioDTO;

public interface IUsuarios {

  UsuarioDTO autenticarse(String usuario, String contrasena);

}
