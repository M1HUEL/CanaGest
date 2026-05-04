package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.banco.BancoCliente;
import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaBancoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.SolicitudBancoDTO;

public class TransaccionStrategy implements IPagoStrategy {

  @Override
  public RespuestaPagoDTO procesar(IngresarPagoDTO request) {
    System.out.println("[TransaccionStrategy] Procesando transferencia bancaria...");
    System.out.println("                      Monto:      $" + request.getMonto());
    System.out.println("                      Referencia: " + request.getReferencia());

    String clabe = extraer(request.getDatos(), "clabe");
    String referencia = extraer(request.getDatos(), "referencia");
    System.out.println("                      CLABE:      " + enmascarar(clabe));

    SolicitudBancoDTO solicitud = SolicitudBancoDTO.builder()
      .monto(request.getMonto())
      .referencia(referencia.isBlank() ? request.getReferencia() : referencia)
      .dato("clabe", clabe)
      .build();

    BancoCliente banco = new BancoCliente();
    RespuestaBancoDTO resp = banco.procesarTransferencia(solicitud);

    System.out.println("                      Resultado: " + resp);
    return TarjetaStrategy.traducir(resp);
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

  private static String enmascarar(String val) {
    if (val == null || val.length() < 8) {
      return "****";
    }
    return val.substring(0, 4) + "**********" + val.substring(val.length() - 4);
  }

}
