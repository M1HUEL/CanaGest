package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;

public interface IPagoStrategy {

  RespuestaPagoDTO procesar(IngresarPagoDTO request);

}
