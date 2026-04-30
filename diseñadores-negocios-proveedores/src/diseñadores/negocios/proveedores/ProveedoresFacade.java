package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import java.util.List;

public class ProveedoresFacade implements IProveedores {

  private final ProveedoresControl control;

  public ProveedoresFacade() {
    IProveedoresRepository repository = ProveedoresRepository.getInstancia();
    this.control = new ProveedoresControl(repository);
  }

  public ProveedoresFacade(ProveedoresControl control) {
    this.control = control;
  }

  public ProveedoresFacade(IProveedoresRepository repository) {
    this.control = new ProveedoresControl(repository);
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
    ProveedorDTO prov = orden.getProveedor();
    control.guardarOrdenCompra(prov, orden.getCantidadProductos(), orden.getTotal());
  }

  @Override
  public void actualizarOrdenCompra(OrdenCompraDTO orden) {
    control.actualizar(orden.getProveedor());
  }

  public void cambiarEstadoOrden(String numero, String nuevoEstado) {
    control.actualizarEstadoOrden(numero, nuevoEstado);
  }

  public ProveedoresControl getControl() {
    return control;
  }

}
