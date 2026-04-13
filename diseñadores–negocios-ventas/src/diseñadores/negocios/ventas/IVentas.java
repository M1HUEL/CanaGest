package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.Producto;
import diseñadores.negocios.dto.Ticket;
import diseñadores.negocios.dto.Venta;

public interface IVentas {

  void nuevaVenta();

  Producto procesarProducto(String codigo);

  double procesarPagoEfectivo(double montoRecibido);

  double calcularCambio(double efectivo);

  void procesarFinalizarVenta();

  Venta obtenerVentaActual();

  Ticket generarTicket();

}
