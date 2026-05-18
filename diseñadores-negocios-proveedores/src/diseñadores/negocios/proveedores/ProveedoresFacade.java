package diseñadores.negocios.proveedores;

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
    return control.obtenerProveedores();
  }

  @Override
  public ProveedorDTO obtenerProveedorPorCodigo(String codigo) {
    return control.obtenerProveedorPorCodigo(codigo);
  }

  @Override
  public void guardarProveedor(ProveedorDTO proveedor) {
    control.guardarProveedor(proveedor);
  }

  @Override
  public void actualizarProveedor(ProveedorDTO proveedor) {
    control.actualizarProveedor(proveedor);
  }

  @Override
  public void eliminarProveedor(String codigo) {
    control.eliminarProveedor(codigo);
  }

  @Override
  public int contarProveedoresActivos() {
    return control.contarProveedoresActivos();
  }
}
