package diseñadores.negocios.usuarios;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.UsuarioRol;
import diseñadores.negocios.objetos.Usuario;

import java.util.List;
import java.util.Optional;

public class UsuariosControl {

  public Optional<UsuarioDTO> autenticar(String nombre, String contrasena) {
    validarCredencialesEntrada(nombre, contrasena);

    return ejecutarAutenticacion(nombre, contrasena);
  }

  public List<UsuarioDTO> obtenerTodos() {
    return Usuario.obtenerTodos();
  }

  public void guardarUsuario(UsuarioDTO usuario) {
    validarUsuarioNoNulo(usuario);
    validarNombreObligatorio(usuario.getNombre());
    validarContrasenaObligatoria(usuario.getContrasena());
    validarFormatoContrasena(usuario.getContrasena());
    validarRolObligatorio(usuario.getRol());
    validarNombreDisponible(usuario.getNombre());

    ejecutarGuardado(usuario);
  }

  public void actualizarUsuario(UsuarioDTO usuario) {
    validarUsuarioNoNulo(usuario);
    validarNombreObligatorio(usuario.getNombre());
    validarFormatoContrasenaOpcional(usuario.getContrasena());
    validarRolObligatorio(usuario.getRol());
    validarExistenciaUsuario(usuario.getNombre());

    ejecutarActualizacion(usuario);
  }

  public void eliminarUsuario(String nombre) {
    validarNombreObligatorio(nombre);
    validarExistenciaUsuario(nombre);

    ejecutarEliminacion(nombre);
  }

  public void cambiarRol(String nombre, UsuarioRol nuevoRol) {
    validarNombreObligatorio(nombre);
    validarNuevoRolNoNulo(nuevoRol);

    UsuarioDTO usuario = buscarUsuarioPorNombre(nombre);
    asignarNuevoRol(usuario, nuevoRol);

    ejecutarActualizacion(usuario);
  }

  private void validarCredencialesEntrada(String nombre, String contrasena) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
    }
    if (contrasena == null || contrasena.isBlank()) {
      throw new IllegalArgumentException("La contraseña no puede estar vacía.");
    }
  }

  private void validarUsuarioNoNulo(UsuarioDTO usuario) {
    if (usuario == null) {
      throw new IllegalArgumentException("El usuario no puede ser nulo.");
    }
  }

  private void validarNombreObligatorio(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
    }
  }

  private void validarContrasenaObligatoria(String contrasena) {
    if (contrasena == null || contrasena.isBlank()) {
      throw new IllegalArgumentException("La contraseña es obligatoria.");
    }
  }

  private void validarFormatoContrasena(String contrasena) {
    if (contrasena.length() < 4) {
      throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
    }
  }

  private void validarFormatoContrasenaOpcional(String contrasena) {
    if (contrasena != null) {
      validarFormatoContrasena(contrasena);
    }
  }

  private void validarRolObligatorio(UsuarioRol rol) {
    if (rol == null) {
      throw new IllegalArgumentException("El rol del usuario es obligatorio.");
    }
  }

  private void validarNuevoRolNoNulo(UsuarioRol rol) {
    if (rol == null) {
      throw new IllegalArgumentException("El nuevo rol no puede ser nulo.");
    }
  }

  private void validarNombreDisponible(String nombre) {
    if (Usuario.obtenerTodos().stream().anyMatch(u -> u.getNombre().equalsIgnoreCase(nombre))) {
      throw new IllegalStateException("Ya existe un usuario con el nombre: " + nombre);
    }
  }

  private void validarExistenciaUsuario(String nombre) {
    if (Usuario.obtenerTodos().stream().noneMatch(u -> u.getNombre().equals(nombre))) {
      throw new IllegalStateException("No existe un usuario con el nombre: " + nombre);
    }
  }

  private Optional<UsuarioDTO> ejecutarAutenticacion(String nombre, String contrasena) {
    String nombreNormalizado = nombre.toLowerCase().trim();
    return Usuario.autenticar(nombreNormalizado, contrasena);
  }

  private void ejecutarGuardado(UsuarioDTO usuario) {
    Usuario.guardar(usuario);
  }

  private void ejecutarActualizacion(UsuarioDTO usuario) {
    Usuario.actualizar(usuario);
  }

  private void ejecutarEliminacion(String nombre) {
    Usuario.eliminar(nombre);
  }

  private UsuarioDTO buscarUsuarioPorNombre(String nombre) {
    return Usuario.obtenerTodos().stream()
      .filter(u -> u.getNombre().equals(nombre))
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("No existe un usuario con el nombre: " + nombre));
  }

  private void asignarNuevoRol(UsuarioDTO usuario, UsuarioRol rol) {
    usuario.setRol(rol);
  }

}
