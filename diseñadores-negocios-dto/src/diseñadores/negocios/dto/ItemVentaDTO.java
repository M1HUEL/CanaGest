package diseñadores.negocios.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemVentaDTO {

  private final String codigo;
  private final String nombre;
  private final BigDecimal precioUnitario;
  private final int cantidad;
  private final BigDecimal subtotal;

  public ItemVentaDTO(String codigo, String nombre, BigDecimal precioUnitario, int cantidad) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.precioUnitario = precioUnitario;
    this.cantidad = cantidad;
    this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public BigDecimal getPrecioUnitario() {
    return precioUnitario;
  }

  public int getCantidad() {
    return cantidad;
  }

  public BigDecimal getSubtotal() {
    return subtotal;
  }

  public ItemVentaDTO conCantidad(int nuevaCantidad) {
    return new ItemVentaDTO(codigo, nombre, precioUnitario, nuevaCantidad);
  }

}
