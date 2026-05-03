package diseñadores.infraestructura.dto;

public final class RespuestaBancoDTO {

  private final boolean aprobado;
  private final String autorizacion;
  private final String mensaje;
  private final String codigo;

  private RespuestaBancoDTO(boolean aprobado, String autorizacion,
    String mensaje, String codigo) {
    this.aprobado = aprobado;
    this.autorizacion = autorizacion;
    this.mensaje = mensaje;
    this.codigo = codigo;
  }

  public boolean isAprobado() {
    return aprobado;
  }

  public String getAutorizacion() {
    return autorizacion;
  }

  public String getMensaje() {
    return mensaje;
  }

  public String getCodigo() {
    return codigo;
  }

  public static RespuestaBancoDTO aprobado(String autorizacion) {
    return new RespuestaBancoDTO(true, autorizacion, "Pago aprobado", "00");
  }

  public static RespuestaBancoDTO rechazado(String codigo, String mensaje) {
    return new RespuestaBancoDTO(false, null, mensaje, codigo);
  }

  public static RespuestaBancoDTO error(String mensaje) {
    return new RespuestaBancoDTO(false, null, mensaje, "96");
  }

  public String toJson() {
    return JsonUtil.build(
      "aprobado", aprobado,
      "autorizacion", autorizacion != null ? autorizacion : "",
      "mensaje", mensaje,
      "codigo", codigo
    );
  }

  public static RespuestaBancoDTO fromJson(String json) {
    boolean aprobado = JsonUtil.getBoolean(json, "aprobado");
    String autorizacion = JsonUtil.getString(json, "autorizacion");
    String mensaje = JsonUtil.getString(json, "mensaje");
    String codigo = JsonUtil.getString(json, "codigo");
    return new RespuestaBancoDTO(aprobado,
      aprobado ? autorizacion : null,
      mensaje != null ? mensaje : "Sin mensaje",
      codigo != null ? codigo : "96");
  }

  @Override
  public String toString() {
    return String.format("RespuestaBancoDTO{aprobado=%s, codigo='%s', autorizacion='%s', mensaje='%s'}",
      aprobado, codigo, autorizacion, mensaje);
  }

}
