package diseñadores.infraestructura.pagos;

import diseñadores.infraestructura.dto.TipoPago;

public class PagoFactory {

  public static IPagoStrategy crear(TipoPago tipo) {
    switch (tipo) {
      case TARJETA -> {
        return new TarjetaStrategy();
      }
      case TRANSACCION -> {
        return new TransaccionStrategy();
      }
      case QR -> {
        return new QRStrategy();
      }
      default ->
        throw new IllegalArgumentException("Tipo de pago no soportado: " + tipo);
    }
  }

}
