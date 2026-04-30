package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import java.math.BigDecimal;
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
    String codigo = generarCodigoProveedor();
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

  public List<OrdenCompraDTO> obtenerOrdenesCompra() {
    return ProveedoresRepository.getInstancia().getOrdenesCompra();
  }

  public void guardarOrdenCompra(ProveedorDTO proveedor, int cantidadProductos, BigDecimal total) {
    String numero = generarNumeroOrden();
    String fecha = java.time.LocalDate.now().toString();
    ProveedorDTO provRef = new ProveedorDTO(proveedor.getNombre(), proveedor.getCodigo(),
      proveedor.getContacto(), proveedor.getTelefono(), proveedor.getEmail(),
      proveedor.getDireccion(), proveedor.getTerminosPago(), proveedor.isActivo());
    OrdenCompraDTO orden = new OrdenCompraDTO(numero, fecha, provRef, "Pendiente", cantidadProductos, total);
    ProveedoresRepository.getInstancia().agregarOrdenCompra(orden);
  }

  public void actualizarEstadoOrden(String numero, String nuevoEstado) {
    ProveedoresRepository.getInstancia().getOrdenesCompra().stream()
      .filter(o -> o.getNumero().equals(numero))
      .findFirst()
      .ifPresent(o -> o.setEstado(nuevoEstado));
  }

  private String generarCodigoProveedor() {
    int siguiente = ProveedoresRepository.getInstancia().getProveedores().size() + 1;
    return String.format("PROV-%03d", siguiente);
  }

  private String generarNumeroOrden() {
    int siguiente = ProveedoresRepository.getInstancia().getOrdenesCompra().size() + 1;
    return String.format("OC-%d-%03d", java.time.LocalDate.now().getYear(), siguiente);
  }

}