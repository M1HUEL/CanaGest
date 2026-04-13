package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.Producto;
import diseñadores.negocios.dto.Ticket;
import diseñadores.negocios.dto.Venta;

public class VentasFacade implements IVentas {

  private VentasControl control = new VentasControl();

  @Override
  public void nuevaVenta() {
    control.iniciarNuevaVenta();
  }

  @Override
  public Producto procesarProducto(String codigo) {
    return control.procesarProducto(codigo);
  }

  @Override
  public double procesarPagoEfectivo(double montoRecibido) {
    return control.procesarPagoEfectivo(montoRecibido);
  }

  @Override
  public double calcularCambio(double efectivo) {
    return control.procesarCalculoCambio(efectivo);
  }

  @Override
  public void procesarFinalizarVenta() {
    control.procesarFinalizarVenta();
  }

  @Override
  public Venta obtenerVentaActual() {
    return control.getVentaActual();
  }

  @Override
  public Ticket generarTicket() {
    return control.generarTicket();
  }

}
