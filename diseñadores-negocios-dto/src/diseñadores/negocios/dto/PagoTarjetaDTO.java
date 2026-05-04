package diseñadores.negocios.dto;

public final class PagoTarjetaDTO {

  private final String numero;
  private final String titular;

  public PagoTarjetaDTO(String numero, String titular) {
    if (numero == null || numero.isBlank()) {
      throw new IllegalArgumentException("Número de tarjeta requerido.");
    }
    if (titular == null || titular.isBlank()) {
      throw new IllegalArgumentException("Titular requerido.");
    }
    this.numero = numero.replaceAll("\\s", "");
    this.titular = titular.trim().toUpperCase();
  }

  public String getNumero() {
    return numero;
  }

  public String getTitular() {
    return titular;
  }

}
