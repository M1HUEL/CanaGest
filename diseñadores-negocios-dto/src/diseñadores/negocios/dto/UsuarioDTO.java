package diseñadores.negocios.dto;

public class UsuarioDTO {

  private String nombre;
  private String contrasena;
  private UsuarioRol rol;

  public UsuarioDTO() {
  }

  public UsuarioDTO(String nombre, String contrasena, UsuarioRol rol) {
    this.nombre = nombre;
    this.contrasena = contrasena;
    this.rol = rol;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getContrasena() {
    return contrasena;
  }

  public void setContrasena(String contrasena) {
    this.contrasena = contrasena;
  }

  public UsuarioRol getRol() {
    return rol;
  }

  public void setRol(UsuarioRol rol) {
    this.rol = rol;
  }

}
