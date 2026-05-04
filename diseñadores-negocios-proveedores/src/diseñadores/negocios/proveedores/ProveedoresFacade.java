package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;

import java.util.List;

public class ProveedoresFacade implements IProveedores {

  private final ProveedoresControl control;

  public ProveedoresFacade() {
    this.control = new ProveedoresControl();
  }

  public ProveedoresFacade(ProveedoresControl control) {
    this.control = control;
  }

  @Override
  public List<ProveedorDTO> obtenerProveedores() {
    return control.obtenerTodos();
  }

  @Override
  public ProveedorDTO obtenerProveedorPorCodigo(String codigo) {
    return control.obtenerPorCodigo(codigo);
  }

  @Override
  public void guardarProveedor(ProveedorDTO proveedor) {
    control.guardar(proveedor);
  }

  @Override
  public void actualizarProveedor(ProveedorDTO proveedor) {
    control.actualizar(proveedor);
  }

  @Override
  public int contarProveedoresActivos() {
    return control.contarActivos();
  }

  @Override
  public List<OrdenCompraDTO> obtenerOrdenesCompra() {
    return control.obtenerOrdenesCompra();
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
