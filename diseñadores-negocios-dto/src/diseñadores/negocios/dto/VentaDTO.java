package diseñadores.negocios.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class VentaDTO {

  private List<ItemVentaDTO> items;
  private BigDecimal subtotal;
  private BigDecimal iva;
  private BigDecimal total;
  private int totalUnidades;
  private boolean pagada;

  public VentaDTO() {
    this.items = new ArrayList<>();
    this.pagada = false;
    this.total = BigDecimal.ZERO;
    this.subtotal = BigDecimal.ZERO;
    this.iva = BigDecimal.ZERO;
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
    this.total = items.stream()
      .map(ItemVentaDTO::getSubtotal)
      .reduce(BigDecimal.ZERO, BigDecimal::add)
      .setScale(2, RoundingMode.HALF_UP);
    this.subtotal = this.total.divide(BigDecimal.valueOf(1.16), 2, RoundingMode.HALF_UP);
    this.iva = this.total.subtract(this.subtotal).setScale(2, RoundingMode.HALF_UP);
    this.totalUnidades = items.stream().mapToInt(ItemVentaDTO::getCantidad).sum();
  }

  public List<ItemVentaDTO> getItems() {
    return items;
  }

  public void setItems(List<ItemVentaDTO> items) {
    this.items = items;
  }

  public BigDecimal getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(BigDecimal subtotal) {
    this.subtotal = subtotal;
  }

  public BigDecimal getIva() {
    return iva;
  }

  public void setIva(BigDecimal iva) {
    this.iva = iva;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
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
