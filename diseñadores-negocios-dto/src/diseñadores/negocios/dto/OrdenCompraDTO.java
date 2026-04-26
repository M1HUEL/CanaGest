package diseñadores.negocios.dto;

public class OrdenCompraDTO {

  private String numero;
  private String fecha;
  private String proveedor;
  private String estado;
  private int productos;
  private double total;

  public OrdenCompraDTO() {
  }

  public OrdenCompraDTO(String numero, String fecha, String proveedor, String estado, int productos, double total) {
    this.numero = numero;
    this.fecha = fecha;
    this.proveedor = proveedor;
    this.estado = estado;
    this.productos = productos;
    this.total = total;
  }

  public String getNumero() {
    return numero;
  }

  public void setNumero(String numero) {
    this.numero = numero;
  }

  public String getFecha() {
    return fecha;
  }

  public void setFecha(String fecha) {
    this.fecha = fecha;
  }

  public String getProveedor() {
    return proveedor;
  }

  public void setProveedor(String proveedor) {
    this.proveedor = proveedor;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public int getProductos() {
    return productos;
  }

  public void setProductos(int productos) {
    this.productos = productos;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Override
  public String toString() {
    return "OrdenCompraDTO{" + "numero=" + numero + ", fecha=" + fecha + ", proveedor=" + proveedor + ", estado=" + estado + ", productos=" + productos + ", total=" + total + '}';
  }

}