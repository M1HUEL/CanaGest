package diseñadores.infraestructura.dto;

public class RespuestaPagoDTO {

  private boolean exitoso;
  private String mensaje;
  private String codigoAutorizacion;

  public RespuestaPagoDTO() {
  }

  public RespuestaPagoDTO(boolean exitoso, String mensaje, String codigoAutorizacion) {
    this.exitoso = exitoso;
    this.mensaje = mensaje;
    this.codigoAutorizacion = codigoAutorizacion;
  }

  public static RespuestaPagoDTO aprobado(String codigoAutorizacion) {
    return new RespuestaPagoDTO(true, "Pago aprobado.", codigoAutorizacion);
  }

  public static RespuestaPagoDTO rechazado(String motivo) {
    return new RespuestaPagoDTO(false, motivo, null);
  }

  public boolean isExitoso() {
    return exitoso;
  }

  public void setExitoso(boolean exitoso) {
    this.exitoso = exitoso;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public String getCodigoAutorizacion() {
    return codigoAutorizacion;
  }

  public void setCodigoAutorizacion(String codigoAutorizacion) {
    this.codigoAutorizacion = codigoAutorizacion;
  }

  @Override
  public String toString() {
    return "RespuestaPagoDTO{" + "exitoso=" + exitoso + ", mensaje=" + mensaje + ", codigoAutorizacion=" + codigoAutorizacion + '}';
  }

}
