package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.dto.IngresarPagoDTO;
import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.dto.TipoPago;
import java.math.BigDecimal;

public class PagosControl {

  public RespuestaPagoDTO procesarPago(TipoPago tipo, BigDecimal monto, String referencia, String datos) {
    validarTipoPago(tipo);
    validarMontoPositivo(monto);
    validarReferenciaRequerida(referencia);

    IPagoStrategy strategy = obtenerEstrategia(tipo);
    IngresarPagoDTO ingreso = crearIngresoPago(monto, referencia, datos);

    return ejecutarProcesamiento(strategy, ingreso);
  }

  private void validarTipoPago(TipoPago tipo) {
    if (tipo == null) {
      throw new IllegalArgumentException("El tipo de pago no puede ser nulo.");
    }
  }

  private void validarMontoPositivo(BigDecimal monto) {
    if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El monto debe ser mayor a cero.");
    }
  }

  private void validarReferenciaRequerida(String referencia) {
    if (referencia == null || referencia.isBlank()) {
      throw new IllegalArgumentException("La referencia del pago no puede estar vacía.");
    }
  }

  private IPagoStrategy obtenerEstrategia(TipoPago tipo) {
    return PagoFactory.crear(tipo);
  }

  private IngresarPagoDTO crearIngresoPago(BigDecimal monto, String referencia, String datos) {
    return new IngresarPagoDTO(monto, referencia, datos);
  }

  private RespuestaPagoDTO ejecutarProcesamiento(IPagoStrategy strategy, IngresarPagoDTO ingreso) {
    return strategy.procesar(ingreso);
  }

}
