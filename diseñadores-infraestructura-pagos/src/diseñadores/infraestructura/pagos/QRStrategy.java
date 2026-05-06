package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.banco.BancoCliente;
import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaBancoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.SolicitudBancoDTO;

public class QRStrategy implements IPagoStrategy {

  @Override
  public RespuestaPagoDTO procesar(IngresarPagoDTO request) {
    imprimirInicio("[QRStrategy] Procesando pago CoDi/QR...", request);

    String referenciaQR = extraer(request.getDatos(), "referencia");

    SolicitudBancoDTO solicitud = construirSolicitud(request, referenciaQR);
    RespuestaBancoDTO resp = ejecutarOperacionBancaria(solicitud);

    imprimirResultado(resp);

    return TarjetaStrategy.traducir(resp);
  }

  private void imprimirInicio(String tag, IngresarPagoDTO request) {
    System.out.println(tag);
    System.out.println("             Monto:      $" + request.getMonto());
    System.out.println("             Referencia: " + request.getReferencia());
  }

  private void imprimirResultado(RespuestaBancoDTO resp) {
    System.out.println("             Resultado: " + resp);
  }

  private SolicitudBancoDTO construirSolicitud(IngresarPagoDTO request, String refDatos) {
    return SolicitudBancoDTO.builder()
      .monto(request.getMonto())
      .referencia(seleccionarReferencia(request.getReferencia(), refDatos))
      .build();
  }

  private String seleccionarReferencia(String refOriginal, String refDatos) {
    return refDatos.isBlank() ? refOriginal : refDatos;
  }

  private RespuestaBancoDTO ejecutarOperacionBancaria(SolicitudBancoDTO solicitud) {
    return new BancoCliente().procesarQR(solicitud);
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
