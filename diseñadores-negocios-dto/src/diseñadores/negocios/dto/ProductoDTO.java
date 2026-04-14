package diseñadores.negocios.dto;

public class ProductoDTO {

  private final String codigo;
  private final String nombre;
  private final double precio;
  private final int stock;

  public ProductoDTO(String codigo, String nombre, double precio, int stock) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.precio = precio;
    this.stock = stock;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public double getPrecio() {
    return precio;
  }

  public int getStock() {
    return stock;
  }

  @Override
  public String toString() {
    return "ProductoDTO{codigo='" + codigo + "', nombre='" + nombre + "', precio=" + precio + ", stock=" + stock + "}";
  }

}
