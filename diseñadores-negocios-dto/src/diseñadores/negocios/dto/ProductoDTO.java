package diseñadores.negocios.dto;

public class ProductoDTO {

  private String codigo;
  private String nombre;
  private double precio;
  private int stock;
  private ProveedorDTO proveedor;

  public ProductoDTO() {
  }

  public ProductoDTO(String codigo, String nombre, double precio, int stock, ProveedorDTO proveedor) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.precio = precio;
    this.stock = stock;
    this.proveedor = proveedor;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public double getPrecio() {
    return precio;
  }

  public void setPrecio(double precio) {
    this.precio = precio;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }

  public ProveedorDTO getProveedor() {
    return proveedor;
  }

  public void setProveedor(ProveedorDTO proveedor) {
    this.proveedor = proveedor;
  }

  @Override
  public String toString() {
    return "ProductoDTO{" + "codigo=" + codigo + ", nombre=" + nombre + ", precio=" + precio + ", stock=" + stock + ", proveedor=" + proveedor + '}';
  }

}
