package diseñadores.negocios.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemCarritoDTO {

  private String nombre;
  private BigDecimal precio;
  private int cantidad;

  public ItemCarritoDTO() {
  }

  public ItemCarritoDTO(String nombre, BigDecimal precio, int cantidad) {
    this.nombre = nombre;
    this.precio = precio;
    this.cantidad = cantidad;
  }

  public BigDecimal getSubtotal() {
    if (precio == null) {
      return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
    return precio.multiply(BigDecimal.valueOf(cantidad))
      .setScale(2, RoundingMode.HALF_UP);
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public BigDecimal getPrecio() {
    return precio;
  }

  public void setPrecio(BigDecimal precio) {
    this.precio = precio;
  }

  public int getCantidad() {
    return cantidad;
  }

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }

}
