package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.TipoPago;
import java.math.BigDecimal;

public interface IPagos {

  RespuestaPagoDTO procesarPago(TipoPago tipo, BigDecimal monto, String referencia, String datos);

}
