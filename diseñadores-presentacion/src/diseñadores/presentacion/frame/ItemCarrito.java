package diseñadores.presentacion.frame;

public class ItemCarrito {

  public String nombre;
  public double precio;
  public int cantidad;

  public ItemCarrito(String n, double p, int c) {
    nombre = n;
    precio = p;
    cantidad = c;
  }

  public double subtotal() {
    return precio * cantidad;
  }

}
