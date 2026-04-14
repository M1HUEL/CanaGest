package diseñadores.negocios.dto;

public class ItemVentaDTO {

  private final String codigo;
  private final String nombre;
  private final double precioUnitario;
  private final int cantidad;
  private final double subtotal;

  public ItemVentaDTO(String codigo, String nombre, double precioUnitario, int cantidad) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.precioUnitario = precioUnitario;
    this.cantidad = cantidad;
    this.subtotal = precioUnitario * cantidad;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public double getPrecioUnitario() {
    return precioUnitario;
  }

  public int getCantidad() {
    return cantidad;
  }

  public double getSubtotal() {
    return subtotal;
  }

}
