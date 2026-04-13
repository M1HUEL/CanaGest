package diseñadores.negocios.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class Ticket {

  private final String folio;
  private final List<Producto> productos;
  private final double subtotal;
  private final double iva;
  private final double total;
  private final double efectivoRecibido;
  private final double cambio;
  private final LocalDateTime fechaHora;
  private final String cajero;
  private final String nombreTienda;
  private final String rfc;
  private final String direccion;
  private final String telefono;

  public Ticket(String folio, List<Producto> productos,
    double total, double efectivoRecibido, double cambio) {
    this.folio = folio;
    this.productos = productos;
    this.total = total;
    this.subtotal = total / 1.16;
    this.iva = total - this.subtotal;
    this.efectivoRecibido = efectivoRecibido;
    this.cambio = cambio;
    this.fechaHora = LocalDateTime.now();
    this.cajero = "Miguel - Caja #1";
    this.nombreTienda = "La Canasta";
    this.rfc = "LCA123456ABC";
    this.direccion = "Av. Principal #123, Col. Centro";
    this.telefono = "Tel: (555) 123-4567";
  }

  public String getFolio() {
    return folio;
  }

  public List<Producto> getProductos() {
    return productos;
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

  public LocalDateTime getFechaHora() {
    return fechaHora;
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

  public String getFechaFormateada() {
    return fechaHora.format(
      DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-MX"))
    );
  }

  public String getHoraFormateada() {
    return fechaHora.format(DateTimeFormatter.ofPattern("hh:mm a"));
  }

}
