package diseñadores.negocios.usuarios;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.UsuarioRolDTO;
import java.util.HashMap;
import java.util.Map;

public class UsuariosControl {

  private final Map<String, UsuarioDTO> usuariosMock = new HashMap<>();

  public UsuariosControl() {
    usuariosMock.put("admin",
      new UsuarioDTO("admin", "1234", UsuarioRolDTO.ADMINISTRADOR));

    usuariosMock.put("cajero",
      new UsuarioDTO("cajero", "1234", UsuarioRolDTO.CAJERO));

    usuariosMock.put("almacen",
      new UsuarioDTO("almacen", "1234", UsuarioRolDTO.ENCARGADO_ALMACEN));
  }

  public UsuarioDTO autenticar(String nombre, String contrasena) {
    UsuarioDTO usuario = usuariosMock.get(nombre.toLowerCase().trim());
    if (usuario == null) {
      return null;
    }
    if (!usuario.getContrasena().equals(contrasena)) {
      return null;
    }
    return usuario;
  }

}
