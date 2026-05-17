package diseñadores.negocios.autenticacion;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.objetos.Usuario;
import java.util.Optional;

public class AutenticacionControl {

  public Optional<UsuarioDTO> autenticar(String nombre, String contrasena) {
    validarCredencialesEntrada(nombre, contrasena);
    return ejecutarAutenticacion(nombre, contrasena);
  }

  private void validarCredencialesEntrada(String nombre, String contrasena) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
    }
    if (contrasena == null || contrasena.isBlank()) {
      throw new IllegalArgumentException("La contraseña no puede estar vacía.");
    }
  }

  private Optional<UsuarioDTO> ejecutarAutenticacion(String nombre, String contrasena) {
    String nombreNormalizado = nombre.toLowerCase().trim();
    return Usuario.autenticar(nombreNormalizado, contrasena);
  }

}
