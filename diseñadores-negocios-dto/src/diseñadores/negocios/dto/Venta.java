package diseñadores.negocios.dto;

import java.util.ArrayList;
import java.util.List;

public class Venta {

  private final List<Producto> productos = new ArrayList<>();
  private boolean pagada = false;

  public void agregarProducto(Producto p) {
    if (!pagada) {
      this.productos.add(p);
    }
  }

  public double getSubtotalVenta() {
    return productos.stream().mapToDouble(Producto::getPrecio).sum();
  }

  public void setPagada(boolean estado) {
    this.pagada = estado;
  }

  public boolean isPagada() {
    return pagada;
  }

  public List<Producto> getListaProductos() {
    return productos;
  }

}
