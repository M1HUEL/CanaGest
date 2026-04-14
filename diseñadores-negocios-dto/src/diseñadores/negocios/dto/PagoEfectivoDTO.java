package diseñadores.negocios.dto;

public class PagoEfectivoDTO {

  private final double montoRecibido;

  public PagoEfectivoDTO(double montoRecibido) {
    this.montoRecibido = montoRecibido;
  }

  public double getMontoRecibido() {
    return montoRecibido;
  }

}
