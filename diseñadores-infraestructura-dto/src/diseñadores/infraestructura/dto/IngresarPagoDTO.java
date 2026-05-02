package diseñadores.infraestructura.dto;

import java.math.BigDecimal;

public class IngresarPagoDTO {

  private BigDecimal monto;
  private String referencia;
  private String datos;

  public IngresarPagoDTO() {
  }

  public IngresarPagoDTO(BigDecimal monto, String referencia, String datos) {
    this.monto = monto;
    this.referencia = referencia;
    this.datos = datos;
  }

  public BigDecimal getMonto() {
    return monto;
  }

  public void setMonto(BigDecimal monto) {
    this.monto = monto;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public String getDatos() {
    return datos;
  }

  public void setDatos(String datos) {
    this.datos = datos;
  }

  @Override
  public String toString() {
    return "IngresarPagoDTO{" + "monto=" + monto + ", referencia=" + referencia + ", datos=" + datos + '}';
  }

}
