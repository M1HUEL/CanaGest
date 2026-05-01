package diseñadores.negocios.dto;

import java.math.BigDecimal;

public class OrdenCompraDTO {

  private String numero;
  private String fecha;
  private ProveedorDTO proveedor;
  private String estado;
  private int cantidadProductos;
  private BigDecimal total;

  public OrdenCompraDTO() {
  }

  public OrdenCompraDTO(String numero, String fecha, ProveedorDTO proveedor, String estado, int cantidadProductos, BigDecimal total) {
    this.numero = numero;
    this.fecha = fecha;
    this.proveedor = proveedor;
    this.estado = estado;
    this.cantidadProductos = cantidadProductos;
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

  public ProveedorDTO getProveedor() {
    return proveedor;
  }

  public void setProveedor(ProveedorDTO proveedor) {
    this.proveedor = proveedor;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public int getCantidadProductos() {
    return cantidadProductos;
  }

  public void setCantidadProductos(int cantidadProductos) {
    this.cantidadProductos = cantidadProductos;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public String getProveedorNombre() {
    return proveedor != null ? proveedor.getNombre() : "";
  }

  public int getProductos() {
    return cantidadProductos;
  }

  public void setProductos(int productos) {
    this.cantidadProductos = productos;
  }

  @Override
  public String toString() {
    return "OrdenCompraDTO{" + "numero=" + numero + ", fecha=" + fecha + ", proveedor=" + proveedor + ", estado=" + estado + ", cantidadProductos=" + cantidadProductos + ", total=" + total + '}';
  }

}
