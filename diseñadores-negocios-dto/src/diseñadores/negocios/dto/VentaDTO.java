package diseñadores.negocios.dto;

import java.util.ArrayList;
import java.util.List;

public class VentaDTO {

  private List<ItemVentaDTO> items;
  private double subtotal;
  private double iva;
  private double total;
  private int totalUnidades;
  private boolean pagada;

  public VentaDTO() {
    this.items = new ArrayList<>();
    this.pagada = false;
    this.total = 0.0;
  }

  public void agregarProducto(ProductoDTO producto) {
    ItemVentaDTO itemExistente = items.stream()
      .filter(i -> i.getCodigo().equals(producto.getCodigo()))
      .findFirst()
      .orElse(null);

    if (itemExistente != null) {
      int index = items.indexOf(itemExistente);
      items.set(index, itemExistente.conCantidad(itemExistente.getCantidad() + 1));
    } else {
      items.add(new ItemVentaDTO(producto.getCodigo(), producto.getNombre(), producto.getPrecio(), 1));
    }

    recalcularTotales();
  }

  private void recalcularTotales() {
    this.total = items.stream().mapToDouble(ItemVentaDTO::getSubtotal).sum();
    this.subtotal = this.total / 1.16;
    this.iva = this.total - this.subtotal;
    this.totalUnidades = items.stream().mapToInt(ItemVentaDTO::getCantidad).sum();
  }

  public List<ItemVentaDTO> getItems() {
    return items;
  }

  public void setItems(List<ItemVentaDTO> items) {
    this.items = items;
  }

  public double getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(double subtotal) {
    this.subtotal = subtotal;
  }

  public double getIva() {
    return iva;
  }

  public void setIva(double iva) {
    this.iva = iva;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public int getTotalUnidades() {
    return totalUnidades;
  }

  public void setTotalUnidades(int totalUnidades) {
    this.totalUnidades = totalUnidades;
  }

  public boolean isPagada() {
    return pagada;
  }

  public void setPagada(boolean pagada) {
    this.pagada = pagada;
  }

}
