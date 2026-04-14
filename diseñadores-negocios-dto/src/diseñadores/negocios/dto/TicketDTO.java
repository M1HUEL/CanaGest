package diseñadores.negocios.dto;

import java.util.List;

public class TicketDTO {

  private final String folio;
  private final List<ItemVentaDTO> items;
  private final double subtotal;
  private final double iva;
  private final double total;
  private final double efectivoRecibido;
  private final double cambio;
  private final String fechaFormateada;
  private final String horaFormateada;
  private final String cajero;
  private final String nombreTienda;
  private final String rfc;
  private final String direccion;
  private final String telefono;

  public TicketDTO(String folio, List<ItemVentaDTO> items,
    double subtotal, double iva, double total,
    double efectivoRecibido, double cambio,
    String fechaFormateada, String horaFormateada,
    String cajero, String nombreTienda,
    String rfc, String direccion, String telefono) {
    this.folio = folio;
    this.items = items;
    this.subtotal = subtotal;
    this.iva = iva;
    this.total = total;
    this.efectivoRecibido = efectivoRecibido;
    this.cambio = cambio;
    this.fechaFormateada = fechaFormateada;
    this.horaFormateada = horaFormateada;
    this.cajero = cajero;
    this.nombreTienda = nombreTienda;
    this.rfc = rfc;
    this.direccion = direccion;
    this.telefono = telefono;
  }

  public String getFolio() {
    return folio;
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

  public double getEfectivoRecibido() {
    return efectivoRecibido;
  }

  public double getCambio() {
    return cambio;
  }

  public String getFechaFormateada() {
    return fechaFormateada;
  }

  public String getHoraFormateada() {
    return horaFormateada;
  }

  public String getCajero() {
    return cajero;
  }

  public String getNombreTienda() {
    return nombreTienda;
  }

  public String getRfc() {
    return rfc;
  }

  public String getDireccion() {
    return direccion;
  }

  public String getTelefono() {
    return telefono;
  }

}
