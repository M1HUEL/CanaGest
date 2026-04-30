package diseñadores.negocios.dto;

import java.math.BigDecimal;

public class ResultadoPagoDTO {

  private final boolean aprobado;
  private final BigDecimal cambio;
  private final String mensaje;

  private ResultadoPagoDTO(boolean aprobado, BigDecimal cambio, String mensaje) {
    this.aprobado = aprobado;
    this.cambio = cambio;
    this.mensaje = mensaje;
  }

  public static ResultadoPagoDTO aprobado(BigDecimal cambio) {
    return new ResultadoPagoDTO(true, cambio, null);
  }

  public static ResultadoPagoDTO rechazado(String razon) {
    return new ResultadoPagoDTO(false, BigDecimal.ZERO, razon);
  }

  public boolean isAprobado() {
    return aprobado;
  }

  public BigDecimal getCambio() {
    return cambio;
  }

  public String getMensaje() {
    return mensaje;
  }

}
