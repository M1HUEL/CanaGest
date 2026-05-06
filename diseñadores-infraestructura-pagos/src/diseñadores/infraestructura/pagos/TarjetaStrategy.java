package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.banco.BancoCliente;
import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaBancoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.SolicitudBancoDTO;

public class TarjetaStrategy implements IPagoStrategy {

  @Override
  public RespuestaPagoDTO procesar(IngresarPagoDTO request) {
    imprimirInicio("[TarjetaStrategy] Procesando pago con tarjeta...", request);

    String numero = extraer(request.getDatos(), "numero");
    String titular = extraer(request.getDatos(), "titular");

    imprimirDetallesTarjeta(numero, titular);

    SolicitudBancoDTO solicitud = construirSolicitud(request, numero, titular);
    RespuestaBancoDTO resp = ejecutarOperacionBancaria(solicitud);

    imprimirResultado(resp);

    return traducir(resp);
  }

  private void imprimirInicio(String tag, IngresarPagoDTO request) {
    System.out.println(tag);
    System.out.println("                  Monto:      $" + request.getMonto());
    System.out.println("                  Referencia: " + request.getReferencia());
  }

  private void imprimirDetallesTarjeta(String numero, String titular) {
    System.out.println("                  Tarjeta:    " + enmascarar(numero));
    System.out.println("                  Titular:    " + titular);
  }

  private void imprimirResultado(RespuestaBancoDTO resp) {
    System.out.println("                  Resultado: " + resp);
  }

  private SolicitudBancoDTO construirSolicitud(IngresarPagoDTO request, String numero, String titular) {
    return SolicitudBancoDTO.builder()
      .monto(request.getMonto())
      .referencia(request.getReferencia())
      .dato("numero", numero)
      .dato("titular", titular)
      .build();
  }

  private RespuestaBancoDTO ejecutarOperacionBancaria(SolicitudBancoDTO solicitud) {
    return new BancoCliente().procesarTarjeta(solicitud);
  }

  private static String extraer(String datos, String clave) {
    if (datos == null) {
      return "";
    }
    for (String par : datos.split("\\|")) {
      String[] kv = par.split("=", 2);
      if (kv.length == 2 && kv[0].trim().equals(clave)) {
        return kv[1].trim();
      }
    }
    return "";
  }

  private static String enmascarar(String num) {
    if (num == null || num.length() < 8) {
      return "****";
    }
    return num.substring(0, 4) + "********" + num.substring(num.length() - 4);
  }

  public static RespuestaPagoDTO traducir(RespuestaBancoDTO resp) {
    if (resp.isAprobado()) {
      return RespuestaPagoDTO.aprobado(resp.getAutorizacion());
    }
    return RespuestaPagoDTO.rechazado(resp.getMensaje());
  }

}
