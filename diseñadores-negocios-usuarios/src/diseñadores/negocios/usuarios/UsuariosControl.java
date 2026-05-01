package diseñadores.negocios.usuarios;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.UsuarioRol;
import diseñadores.negocios.objetos.Usuario;

import java.util.List;
import java.util.Optional;

public class UsuariosControl {

  public Optional<UsuarioDTO> autenticar(String nombre, String contrasena) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
    }
    if (contrasena == null || contrasena.isBlank()) {
      throw new IllegalArgumentException("La contraseña no puede estar vacía.");
    }

    return Usuario.autenticar(nombre.toLowerCase().trim(), contrasena);
  }

  public List<UsuarioDTO> obtenerTodos() {
    return Usuario.obtenerTodos();
  }

  public void guardarUsuario(UsuarioDTO usuario) {
    if (usuario == null) {
      throw new IllegalArgumentException("El usuario no puede ser nulo.");
    }
    if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
      throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
    }
    if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
      throw new IllegalArgumentException("La contraseña es obligatoria.");
    }
    if (usuario.getContrasena().length() < 4) {
      throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
    }
    if (usuario.getRol() == null) {
      throw new IllegalArgumentException("El rol del usuario es obligatorio.");
    }
    if (Usuario.autenticar(usuario.getNombre(), usuario.getContrasena()).isPresent()) {
      throw new IllegalStateException("Ya existe un usuario con el nombre: " + usuario.getNombre());
    }

    Usuario.guardar(usuario);
  }

  public void actualizarUsuario(UsuarioDTO usuario) {
    if (usuario == null) {
      throw new IllegalArgumentException("El usuario no puede ser nulo.");
    }
    if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
      throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
    }
    if (usuario.getContrasena() != null && usuario.getContrasena().length() < 4) {
      throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
    }
    if (usuario.getRol() == null) {
      throw new IllegalArgumentException("El rol del usuario es obligatorio.");
    }
    if (Usuario.obtenerTodos().stream().noneMatch(u -> u.getNombre().equals(usuario.getNombre()))) {
      throw new IllegalStateException("No existe un usuario con el nombre: " + usuario.getNombre());
    }

    Usuario.actualizar(usuario);
  }

  public void eliminarUsuario(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
    }
    if (Usuario.obtenerTodos().stream().noneMatch(u -> u.getNombre().equals(nombre))) {
      throw new IllegalStateException("No existe un usuario con el nombre: " + nombre);
    }

    Usuario.eliminar(nombre);
  }

  public void cambiarRol(String nombre, UsuarioRol nuevoRol) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
    }
    if (nuevoRol == null) {
      throw new IllegalArgumentException("El nuevo rol no puede ser nulo.");
    }

    UsuarioDTO usuario = Usuario.obtenerTodos().stream()
      .filter(u -> u.getNombre().equals(nombre))
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("No existe un usuario con el nombre: " + nombre));

    usuario.setRol(nuevoRol);
    Usuario.actualizar(usuario);
  }

}
