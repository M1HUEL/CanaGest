package diseñadores.negocios.dto;

import java.math.BigDecimal;

public final class ResultadoPagoDTO {

  private final boolean aprobado;
  private final BigDecimal cambio;
  private final String autorizacion;
  private final String mensaje;

  private ResultadoPagoDTO(boolean aprobado, BigDecimal cambio,
    String autorizacion, String mensaje) {
    this.aprobado = aprobado;
    this.cambio = cambio;
    this.autorizacion = autorizacion;
    this.mensaje = mensaje;
  }

  public static ResultadoPagoDTO aprobado(BigDecimal cambio) {
    return new ResultadoPagoDTO(true, cambio, null, "Pago aprobado");
  }

  public static ResultadoPagoDTO aprobado(String autorizacion) {
    return new ResultadoPagoDTO(true, BigDecimal.ZERO, autorizacion, "Pago aprobado");
  }

  public static ResultadoPagoDTO rechazado(String mensaje) {
    return new ResultadoPagoDTO(false, null, null, mensaje);
  }

  public boolean isAprobado() {
    return aprobado;
  }

  public BigDecimal getCambio() {
    return cambio != null ? cambio : BigDecimal.ZERO;
  }

  public String getAutorizacion() {
    return autorizacion != null ? autorizacion : "";
  }

  public String getMensaje() {
    return mensaje;
  }

  @Override
  public String toString() {
    return String.format("ResultadoPago{aprobado=%s, autorizacion='%s', cambio=%s, mensaje='%s'}",
      aprobado, autorizacion, cambio, mensaje);
  }

}
