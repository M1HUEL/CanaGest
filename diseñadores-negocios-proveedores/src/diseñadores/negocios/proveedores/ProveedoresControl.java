package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.ProveedorDTO;
import java.util.List;

public class ProveedoresControl {

  public List<ProveedorDTO> obtenerTodos() {
    return ProveedoresRepository.getInstancia().getProveedores();
  }

  public ProveedorDTO obtenerPorCodigo(String codigo) {
    return ProveedoresRepository.getInstancia().getProveedores().stream()
      .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
      .findFirst()
      .orElse(null);
  }

  public void guardar(ProveedorDTO proveedor) {
    String codigo = generarCodigo();
    proveedor.setCodigo(codigo);
    ProveedoresRepository.getInstancia().agregarProveedor(proveedor);
  }

  public void actualizar(ProveedorDTO proveedor) {
    ProveedoresRepository.getInstancia().actualizarProveedor(proveedor);
  }

  public int contarActivos() {
    return (int) ProveedoresRepository.getInstancia().getProveedores().stream()
      .filter(ProveedorDTO::isActivo)
      .count();
  }

  private String generarCodigo() {
    int siguiente = ProveedoresRepository.getInstancia().getProveedores().size() + 1;
    return String.format("PROV-%03d", siguiente);
  }

}