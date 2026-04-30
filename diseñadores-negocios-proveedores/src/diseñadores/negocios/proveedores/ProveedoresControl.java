package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProveedoresControl {

  private final IProveedoresRepository repository;

  public ProveedoresControl(IProveedoresRepository repository) {
    this.repository = repository;
  }

  public List<ProveedorDTO> obtenerTodos() {
    return repository.getProveedores();
  }

  public ProveedorDTO obtenerPorCodigo(String codigo) {
    return repository.getProveedores().stream()
      .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
      .findFirst()
      .orElse(null);
  }

  public void guardar(ProveedorDTO proveedor) {
    String codigo = generarCodigoProveedor();
    proveedor.setCodigo(codigo);
    repository.agregarProveedor(proveedor);
  }

  public void actualizar(ProveedorDTO proveedor) {
    repository.actualizarProveedor(proveedor);
  }

  public int contarActivos() {
    return (int) repository.getProveedores().stream()
      .filter(ProveedorDTO::isActivo)
      .count();
  }

  public List<OrdenCompraDTO> obtenerOrdenesCompra() {
    return repository.getOrdenesCompra();
  }

  public void guardarOrdenCompra(ProveedorDTO proveedor, int cantidadProductos, BigDecimal total) {
    String numero = generarNumeroOrden();
    String fecha = LocalDate.now().toString();
    ProveedorDTO provRef = new ProveedorDTO(
      proveedor.getNombre(), proveedor.getCodigo(),
      proveedor.getContacto(), proveedor.getTelefono(), proveedor.getEmail(),
      proveedor.getDireccion(), proveedor.getTerminosPago(), proveedor.isActivo()
    );
    OrdenCompraDTO orden = new OrdenCompraDTO(numero, fecha, provRef, "Pendiente", cantidadProductos, total);
    repository.agregarOrdenCompra(orden);
  }

  public void actualizarEstadoOrden(String numero, String nuevoEstado) {
    repository.getOrdenesCompra().stream()
      .filter(o -> o.getNumero().equals(numero))
      .findFirst()
      .ifPresent(o -> o.setEstado(nuevoEstado));
  }

  private String generarCodigoProveedor() {
    return "PROV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private String generarNumeroOrden() {
    return String.format("OC-%d-%s", java.time.LocalDate.now().getYear(),
      UUID.randomUUID().toString().substring(0, 8).toUpperCase());
  }

}
