package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import java.util.UUID;

public class TarjetaStrategy implements IPagoStrategy {

  @Override
  public RespuestaPagoDTO procesar(IngresarPagoDTO request) {
    System.out.println("Procesando pago con tarjeta...");
    System.out.println("Monto:$ " + request.getMonto());
    System.out.println("Referencia: " + request.getReferencia());
    System.out.println("Datos: " + request.getDatos());

    String autorizacion = "TARJETA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    System.out.println("Autorización: " + autorizacion);

    return RespuestaPagoDTO.aprobado(autorizacion);
  }

}
