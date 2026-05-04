package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.banco.BancoCliente;
import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaBancoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.SolicitudBancoDTO;

public class TarjetaStrategy implements IPagoStrategy {

  @Override
  public RespuestaPagoDTO procesar(IngresarPagoDTO request) {
    System.out.println("[TarjetaStrategy] Procesando pago con tarjeta...");
    System.out.println("                  Monto:      $" + request.getMonto());
    System.out.println("                  Referencia: " + request.getReferencia());

    String numero = extraer(request.getDatos(), "numero");
    String titular = extraer(request.getDatos(), "titular");
    System.out.println("                  Tarjeta:    " + enmascarar(numero));
    System.out.println("                  Titular:    " + titular);

    SolicitudBancoDTO solicitud = SolicitudBancoDTO.builder()
      .monto(request.getMonto())
      .referencia(request.getReferencia())
      .dato("numero", numero)
      .dato("titular", titular)
      .build();

    BancoCliente banco = new BancoCliente();
    RespuestaBancoDTO resp = banco.procesarTarjeta(solicitud);

    System.out.println("                  Resultado: " + resp);
    return traducir(resp);
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

  static RespuestaPagoDTO traducir(RespuestaBancoDTO resp) {
    return resp.isAprobado()
      ? RespuestaPagoDTO.aprobado(resp.getAutorizacion())
      : RespuestaPagoDTO.rechazado(resp.getMensaje());
  }

}
