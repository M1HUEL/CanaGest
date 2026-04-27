package diseñadores.negocios.dto;

public class ProductoDTO {

  private String codigo;
  private String nombre;
  private double precio;
  private int stockActual;
  private int stockMinimo;
  private int stockMaximo;
  private String fechaModificacion;
  private ProveedorDTO proveedor;

  public ProductoDTO() {
  }

  public ProductoDTO(String codigo, String nombre, double precio, int stockActual, int stockMinimo, int stockMaximo, ProveedorDTO proveedor) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.precio = precio;
    this.stockActual = stockActual;
    this.stockMinimo = stockMinimo;
    this.stockMaximo = stockMaximo;
    this.fechaModificacion = java.time.LocalDate.now().toString();
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
    return stockActual;
  }

  public int getStockActual() {
    return stockActual;
  }

  public void setStockActual(int stockActual) {
    this.stockActual = stockActual;
    this.fechaModificacion = java.time.LocalDate.now().toString();
  }

  public int getStockMinimo() {
    return stockMinimo;
  }

  public void setStockMinimo(int stockMinimo) {
    this.stockMinimo = stockMinimo;
    this.fechaModificacion = java.time.LocalDate.now().toString();
  }

  public int getStockMaximo() {
    return stockMaximo;
  }

  public void setStockMaximo(int stockMaximo) {
    this.stockMaximo = stockMaximo;
    this.fechaModificacion = java.time.LocalDate.now().toString();
  }

  public String getFechaModificacion() {
    return fechaModificacion;
  }

  public void setFechaModificacion(String fechaModificacion) {
    this.fechaModificacion = fechaModificacion;
  }

  public void setStock(int stock) {
    this.stockActual = stock;
    this.fechaModificacion = java.time.LocalDate.now().toString();
  }

  public ProveedorDTO getProveedor() {
    return proveedor;
  }

  public void setProveedor(ProveedorDTO proveedor) {
    this.proveedor = proveedor;
  }

  public boolean estaBajoMinimo() {
    return stockActual < stockMinimo;
  }

  public boolean estaSobreMaximo() {
    return stockActual > stockMaximo;
  }

  public boolean necesitaReorden() {
    return stockActual <= stockMinimo;
  }

  @Override
  public String toString() {
    return "ProductoDTO{" + "codigo=" + codigo + ", nombre=" + nombre + ", precio=" + precio + ", stockActual=" + stockActual + ", proveedor=" + proveedor + '}';
  }

}
