package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.banco.BancoCliente;
import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaBancoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.SolicitudBancoDTO;

public class QRStrategy implements IPagoStrategy {

  @Override
  public RespuestaPagoDTO procesar(IngresarPagoDTO request) {
    System.out.println("[QRStrategy] Procesando pago CoDi/QR...");
    System.out.println("             Monto:      $" + request.getMonto());
    System.out.println("             Referencia: " + request.getReferencia());

    String referencia = extraer(request.getDatos(), "referencia");

    SolicitudBancoDTO solicitud = SolicitudBancoDTO.builder()
      .monto(request.getMonto())
      .referencia(referencia.isBlank() ? request.getReferencia() : referencia)
      .build();

    BancoCliente banco = new BancoCliente();
    RespuestaBancoDTO resp = banco.procesarQR(solicitud);

    System.out.println("             Resultado: " + resp);
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

}
