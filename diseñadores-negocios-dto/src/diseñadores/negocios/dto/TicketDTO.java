package diseñadores.negocios.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TicketDTO {

  private final String folio;
  private final List<ItemVentaDTO> items;
  private final BigDecimal subtotal;
  private final BigDecimal iva;
  private final BigDecimal total;
  private final BigDecimal efectivoRecibido;
  private final BigDecimal cambio;
  private final String fechaFormateada;
  private final String horaFormateada;
  private final String cajero;
  private final String nombreTienda;
  private final String rfc;
  private final String direccion;
  private final String telefono;

  public TicketDTO(String folio, List<ItemVentaDTO> items,
    BigDecimal subtotal, BigDecimal iva, BigDecimal total,
    BigDecimal efectivoRecibido, BigDecimal cambio,
    LocalDateTime fechaHora,
    String cajero, String nombreTienda,
    String rfc, String direccion, String telefono) {
    this.folio = folio;
    this.items = items;
    this.subtotal = subtotal;
    this.iva = iva;
    this.total = total;
    this.efectivoRecibido = efectivoRecibido;
    this.cambio = cambio;
    this.fechaFormateada = fechaHora.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX")));
    this.horaFormateada = fechaHora.format(DateTimeFormatter.ofPattern("hh:mm a"));
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

  public BigDecimal getSubtotal() {
    return subtotal;
  }

  public BigDecimal getIva() {
    return iva;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public BigDecimal getEfectivoRecibido() {
    return efectivoRecibido;
  }

  public BigDecimal getCambio() {
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
