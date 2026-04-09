package diseñadores.negocios.ventas;

import diseñadores.negocios.ventas.dominio.Producto;
import diseñadores.negocios.ventas.dominio.Ticket;
import diseñadores.negocios.ventas.dominio.Venta;

public interface IVentas {

  void nuevaVenta();

  Producto procesarProducto(String codigo);

  double procesarPagoEfectivo(double montoRecibido);

  double calcularCambio(double efectivo);

  void procesarFinalizarVenta();

  Venta obtenerVentaActual();

  Ticket generarTicket();

}
