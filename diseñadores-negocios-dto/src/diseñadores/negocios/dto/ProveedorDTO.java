package diseñadores.negocios.dto;

public class ProveedorDTO {

  private String nombre;
  private String codigo;
  private String contacto;
  private String telefono;
  private String email;
  private String direccion;
  private String terminosPago;
  private boolean activo;
  private double precioProveedor;
  private String tiempoEntregaProveedor;

  public ProveedorDTO() {
  }

  public ProveedorDTO(String nombre, String codigo, String contacto, String telefono,
    String email, String direccion, String terminosPago, boolean activo) {
    this.nombre = nombre;
    this.codigo = codigo;
    this.contacto = contacto;
    this.telefono = telefono;
    this.email = email;
    this.direccion = direccion;
    this.terminosPago = terminosPago;
    this.activo = activo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getContacto() {
    return contacto;
  }

  public void setContacto(String contacto) {
    this.contacto = contacto;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDireccion() {
    return direccion;
  }

  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  public String getTerminosPago() {
    return terminosPago;
  }

  public void setTerminosPago(String terminosPago) {
    this.terminosPago = terminosPago;
  }

  public boolean isActivo() {
    return activo;
  }

  public void setActivo(boolean activo) {
    this.activo = activo;
  }

  public double getPrecioProveedor() {
    return precioProveedor;
  }

  public void setPrecioProveedor(double precioProveedor) {
    this.precioProveedor = precioProveedor;
  }

  public String getTiempoEntregaProveedor() {
    return tiempoEntregaProveedor;
  }

  public void setTiempoEntregaProveedor(String tiempoEntregaProveedor) {
    this.tiempoEntregaProveedor = tiempoEntregaProveedor;
  }

}
