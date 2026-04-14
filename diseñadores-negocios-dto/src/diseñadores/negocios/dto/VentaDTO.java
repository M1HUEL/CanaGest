package diseñadores.negocios.dto;

import java.util.List;

public class VentaDTO {

  private final List<ItemVentaDTO> items;
  private final double subtotal;
  private final double iva;
  private final double total;
  private final int totalUnidades;

  public VentaDTO(List<ItemVentaDTO> items, double subtotal, double iva, double total, int totalUnidades) {
    this.items = items;
    this.subtotal = subtotal;
    this.iva = iva;
    this.total = total;
    this.totalUnidades = totalUnidades;
  }

  public List<ItemVentaDTO> getItems() {
    return items;
  }

  public double getSubtotal() {
    return subtotal;
  }

  public double getIva() {
    return iva;
  }

  public double getTotal() {
    return total;
  }

  public int getTotalUnidades() {
    return totalUnidades;
  }

}
