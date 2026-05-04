package diseñadores.negocios.dto;

public final class PagoTransferenciaDTO {

  private final String clabe;
  private final String referencia;

  public PagoTransferenciaDTO(String clabe, String referencia) {
    if (clabe == null || clabe.isBlank()) {
      throw new IllegalArgumentException("CLABE requerida.");
    }
    if (referencia == null || referencia.isBlank()) {
      throw new IllegalArgumentException("Referencia requerida.");
    }
    this.clabe = clabe.replaceAll("\\s", "");
    this.referencia = referencia.trim();
  }

  public String getClabe() {
    return clabe;
  }

  public String getReferencia() {
    return referencia;
  }

}
