package diseñadores.negocios.dto;

import java.math.BigDecimal;

public class PagoEfectivoDTO {

  private final BigDecimal montoRecibido;

  public PagoEfectivoDTO(BigDecimal montoRecibido) {
    this.montoRecibido = montoRecibido;
  }

  public BigDecimal getMontoRecibido() {
    return montoRecibido;
  }

}
