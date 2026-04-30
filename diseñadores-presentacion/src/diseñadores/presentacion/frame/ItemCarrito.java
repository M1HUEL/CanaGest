package diseñadores.presentacion.frame;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemCarrito {

  public String nombre;
  public BigDecimal precio;
  public int cantidad;

  public ItemCarrito(String n, BigDecimal p, int c) {
    nombre = n;
    precio = p;
    cantidad = c;
  }

  public BigDecimal subtotal() {
    return precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
  }

}
