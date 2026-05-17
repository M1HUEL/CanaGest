package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.negocios.objetos.Proveedor;
import java.util.List;
import java.util.UUID;

public class ProveedoresControl {

  public List<ProveedorDTO> obtenerProveedores() {
    return Proveedor.obtenerTodos();
  }

  public ProveedorDTO obtenerPorCodigo(String codigo) {
    validarCodigoRequerido(codigo);
    return ejecutarObtencionPorCodigo(codigo);
  }

  public int contarActivos() {
    return (int) obtenerProveedores().stream()
      .filter(ProveedorDTO::isActivo)
      .count();
  }

  public void guardar(ProveedorDTO proveedor) {
    validarProveedorNoNulo(proveedor);
    validarDatosObligatoriosProveedor(proveedor);
    asignarNuevoCodigo(proveedor);
    ejecutarGuardadoProveedor(proveedor);
  }

  public void actualizar(ProveedorDTO proveedor) {
    validarProveedorNoNulo(proveedor);
    validarCodigoObligatorio(proveedor.getCodigo());
    validarNombreObligatorio(proveedor.getNombre());
    validarExistenciaProveedor(proveedor.getCodigo());
    ejecutarActualizacionProveedor(proveedor);
  }

  public void eliminarProveedor(String codigo) {
    validarCodigoRequerido(codigo);
    validarExistenciaProveedor(codigo);
    Proveedor.eliminar(codigo);
  }

  private void validarCodigoRequerido(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("El código del proveedor no puede estar vacío.");
    }
  }

  private void validarProveedorNoNulo(ProveedorDTO proveedor) {
    if (proveedor == null) {
      throw new IllegalArgumentException("El proveedor no puede ser nulo.");
    }
  }

  private void validarDatosObligatoriosProveedor(ProveedorDTO proveedor) {
    validarNombreObligatorio(proveedor.getNombre());
    if (proveedor.getContacto() == null || proveedor.getContacto().isBlank()) {
      throw new IllegalArgumentException("El contacto del proveedor es obligatorio.");
    }
    if (proveedor.getTelefono() == null || proveedor.getTelefono().isBlank()) {
      throw new IllegalArgumentException("El teléfono del proveedor es obligatorio.");
    }
    if (proveedor.getEmail() == null || proveedor.getEmail().isBlank()) {
      throw new IllegalArgumentException("El email del proveedor es obligatorio.");
    }
  }

  private void validarNombreObligatorio(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
    }
  }

  private void validarCodigoObligatorio(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("El código del proveedor es obligatorio.");
    }
  }

  private void validarExistenciaProveedor(String codigo) {
    if (ejecutarObtencionPorCodigo(codigo) == null) {
      throw new IllegalStateException("No existe un proveedor con el código: " + codigo);
    }
  }

  private void asignarNuevoCodigo(ProveedorDTO proveedor) {
    proveedor.setCodigo(generarCodigoProveedor());
  }

  private ProveedorDTO ejecutarObtencionPorCodigo(String codigo) {
    return Proveedor.obtenerPorCodigo(codigo);
  }

  private void ejecutarGuardadoProveedor(ProveedorDTO proveedor) {
    Proveedor.guardar(proveedor);
  }

  private void ejecutarActualizacionProveedor(ProveedorDTO proveedor) {
    Proveedor.actualizar(proveedor);
  }

  private String generarCodigoProveedor() {
    return "PROV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }
}
