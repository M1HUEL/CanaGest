package diseñadores.negocios.objetos;

public class Proveedor {

  private String nombre;
  private String codigo;
  private String contacto;
  private String telefono;
  private String email;
  private String direccion;
  private String plazoEntrega;
  private boolean activo;

  public Proveedor() {
  }

  public Proveedor(String nombre, String codigo, String contacto, String telefono,
    String email, String direccion, String plazoEntrega, boolean activo) {
    this.nombre = nombre;
    this.codigo = codigo;
    this.contacto = contacto;
    this.telefono = telefono;
    this.email = email;
    this.direccion = direccion;
    this.plazoEntrega = plazoEntrega;
    this.activo = activo;
  }

  public String getNombre() {
    return nombre;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getContacto() {
    return contacto;
  }

  public String getTelefono() {
    return telefono;
  }

  public String getEmail() {
    return email;
  }

  public String getDireccion() {
    return direccion;
  }

  public String getPlazoEntrega() {
    return plazoEntrega;
  }

  public boolean isActivo() {
    return activo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public void setContacto(String contacto) {
    this.contacto = contacto;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  public void setPlazoEntrega(String plazoEntrega) {
    this.plazoEntrega = plazoEntrega;
  }

  public void setActivo(boolean activo) {
    this.activo = activo;
  }

}
