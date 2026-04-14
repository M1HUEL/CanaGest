package diseñadores.negocios.dto;

public class ResultadoPagoDTO {

  private final boolean aprobado;
  private final double cambio;
  private final String mensaje;

  private ResultadoPagoDTO(boolean aprobado, double cambio, String mensaje) {
    this.aprobado = aprobado;
    this.cambio = cambio;
    this.mensaje = mensaje;
  }

  public static ResultadoPagoDTO aprobado(double cambio) {
    return new ResultadoPagoDTO(true, cambio, null);
  }

  public static ResultadoPagoDTO rechazado(String razon) {
    return new ResultadoPagoDTO(false, 0.0, razon);
  }

  public boolean isAprobado() {
    return aprobado;
  }

  public double getCambio() {
    return cambio;
  }

  public String getMensaje() {
    return mensaje;
  }

}
