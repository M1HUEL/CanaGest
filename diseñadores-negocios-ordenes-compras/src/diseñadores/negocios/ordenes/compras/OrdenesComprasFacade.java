package diseñadores.negocios.ordenes.compras;

import diseñadores.negocios.dto.OrdenCompraDTO;
import java.util.List;

public class OrdenesComprasFacade implements IOrdenesCompras {

  private final OrdenesComprasControl control;

  public OrdenesComprasFacade() {
    this.control = new OrdenesComprasControl();
  }

  public OrdenesComprasFacade(OrdenesComprasControl control) {
    this.control = control;
  }

  @Override
  public List<OrdenCompraDTO> obtenerOrdenesCompra() {
    return control.obtenerOrdenesCompra();
  }

  @Override
  public OrdenCompraDTO obtenerOrdenPorNumero(String numero) {
    return control.obtenerOrdenPorNumero(numero);
  }

  @Override
  public void guardarOrdenCompra(OrdenCompraDTO orden) {
    control.guardarOrdenCompra(orden.getProveedor(), orden.getCantidadProductos(), orden.getTotal());
  }

  @Override
  public void actualizarOrdenCompra(OrdenCompraDTO orden) {
    control.actualizarOrdenCompra(orden);
  }

  @Override
  public void cambiarEstadoOrden(String numero, String nuevoEstado) {
    control.cambiarEstadoOrden(numero, nuevoEstado);
  }
}
