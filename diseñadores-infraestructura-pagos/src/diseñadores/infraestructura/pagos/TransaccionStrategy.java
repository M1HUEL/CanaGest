package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.banco.BancoCliente;
import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaBancoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.SolicitudBancoDTO;

public class TransaccionStrategy implements IPagoStrategy {

  @Override
  public RespuestaPagoDTO procesar(IngresarPagoDTO request) {
    imprimirInicio("[TransaccionStrategy] Procesando transferencia bancaria...", request);

    String clabe = extraer(request.getDatos(), "clabe");
    String referenciaBancaria = extraer(request.getDatos(), "referencia");

    imprimirDetalleClabe(clabe);

    SolicitudBancoDTO solicitud = construirSolicitud(request, clabe, referenciaBancaria);
    RespuestaBancoDTO resp = ejecutarOperacionBancaria(solicitud);

    imprimirResultado(resp);

    return TarjetaStrategy.traducir(resp);
  }

  private void imprimirInicio(String tag, IngresarPagoDTO request) {
    System.out.println(tag);
    System.out.println("                      Monto:      $" + request.getMonto());
    System.out.println("                      Referencia: " + request.getReferencia());
  }

  private void imprimirDetalleClabe(String clabe) {
    System.out.println("                      CLABE:      " + enmascarar(clabe));
  }

  private void imprimirResultado(RespuestaBancoDTO resp) {
    System.out.println("                      Resultado: " + resp);
  }

  private SolicitudBancoDTO construirSolicitud(IngresarPagoDTO request, String clabe, String refBancaria) {
    return SolicitudBancoDTO.builder()
      .monto(request.getMonto())
      .referencia(seleccionarReferencia(request.getReferencia(), refBancaria))
      .dato("clabe", clabe)
      .build();
  }

  private String seleccionarReferencia(String refOriginal, String refDatos) {
    return refDatos.isBlank() ? refOriginal : refDatos;
  }

  private RespuestaBancoDTO ejecutarOperacionBancaria(SolicitudBancoDTO solicitud) {
    return new BancoCliente().procesarTransferencia(solicitud);
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
