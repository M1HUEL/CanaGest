package diseñadores.negocios.dto;

public final class PagoQrDTO {

  private final String referencia;

  public PagoQrDTO(String referencia) {
    if (referencia == null || referencia.isBlank()) {
      throw new IllegalArgumentException("Referencia requerida.");
    }
    this.referencia = referencia.trim();
  }

  public String getReferencia() {
    return referencia;
  }

}
