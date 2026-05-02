package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.TipoPago;
import java.math.BigDecimal;

public class PagosFacade implements IPagos {

  private final PagosControl control;

  public PagosFacade() {
    this.control = new PagosControl();
  }

  @Override
  public RespuestaPagoDTO procesarPago(TipoPago tipo, BigDecimal monto, String referencia, String datos) {
    return control.procesarPago(tipo, monto, referencia, datos);
  }

}
