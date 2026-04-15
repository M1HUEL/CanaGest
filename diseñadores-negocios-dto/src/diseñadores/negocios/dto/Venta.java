package diseñadores.negocios.dto;

import java.util.ArrayList;
import java.util.List;

public class Venta {

  private final List<Producto> listaProductos;
  private boolean pagada;

  public Venta() {
    this.listaProductos = new ArrayList<>();
    this.pagada = false;
  }

  public void agregarProducto(Producto p) {
    listaProductos.add(p);
  }

  public void removerUnaUnidad(String codigo) {
    for (int i = listaProductos.size() - 1; i >= 0; i--) {
      if (listaProductos.get(i).getCodigo().equalsIgnoreCase(codigo)) {
        listaProductos.remove(i);
        return;
      }
    }
  }

  public void removerTodas(String codigo) {
    listaProductos.removeIf(p -> p.getCodigo().equalsIgnoreCase(codigo));
  }

  public double getSubtotalVenta() {
    return listaProductos.stream().mapToDouble(Producto::getPrecio).sum();
  }

  public List<Producto> getListaProductos() {
    return listaProductos;
  }

  public boolean isPagada() {
    return pagada;
  }

  public void setPagada(boolean p) {
    this.pagada = p;
  }

  public boolean isEmpty() {
    return listaProductos.isEmpty();
  }

}
