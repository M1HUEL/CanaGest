package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.TipoPago;
import java.math.BigDecimal;

public class PagosControl {

  public RespuestaPagoDTO procesarPago(TipoPago tipo, BigDecimal monto, String referencia, String datos) {
    if (tipo == null) {
      throw new IllegalArgumentException("El tipo de pago no puede ser nulo.");
    }
    if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El monto debe ser mayor a cero.");
    }
    if (referencia == null || referencia.isBlank()) {
      throw new IllegalArgumentException("La referencia del pago no puede estar vacía.");
    }

    IPagoStrategy strategy = PagoFactory.crear(tipo);
    return strategy.procesar(new IngresarPagoDTO(monto, referencia, datos));
  }

}
