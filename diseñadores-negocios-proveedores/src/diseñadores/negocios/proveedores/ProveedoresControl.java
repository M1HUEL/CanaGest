package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.negocios.objetos.OrdenCompra;
import diseñadores.negocios.objetos.Proveedor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProveedoresControl {

  public List<ProveedorDTO> obtenerTodos() {
    return Proveedor.obtenerTodos();
  }

  public ProveedorDTO obtenerPorCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("El código del proveedor no puede estar vacío.");
    }

    return Proveedor.obtenerPorCodigo(codigo);
  }

  public int contarActivos() {
    return (int) Proveedor.obtenerTodos().stream()
      .filter(ProveedorDTO::isActivo)
      .count();
  }

  public void guardar(ProveedorDTO proveedor) {
    if (proveedor == null) {
      throw new IllegalArgumentException("El proveedor no puede ser nulo.");
    }
    if (proveedor.getNombre() == null || proveedor.getNombre().isBlank()) {
      throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
    }
    if (proveedor.getContacto() == null || proveedor.getContacto().isBlank()) {
      throw new IllegalArgumentException("El contacto del proveedor es obligatorio.");
    }
    if (proveedor.getTelefono() == null || proveedor.getTelefono().isBlank()) {
      throw new IllegalArgumentException("El teléfono del proveedor es obligatorio.");
    }
    if (proveedor.getEmail() == null || proveedor.getEmail().isBlank()) {
      throw new IllegalArgumentException("El email del proveedor es obligatorio.");
    }

    proveedor.setCodigo(generarCodigoProveedor());
    Proveedor.guardar(proveedor);
  }

  public void actualizar(ProveedorDTO proveedor) {
    if (proveedor == null) {
      throw new IllegalArgumentException("El proveedor no puede ser nulo.");
    }
    if (proveedor.getCodigo() == null || proveedor.getCodigo().isBlank()) {
      throw new IllegalArgumentException("El código del proveedor es obligatorio.");
    }
    if (proveedor.getNombre() == null || proveedor.getNombre().isBlank()) {
      throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
    }
    if (Proveedor.obtenerPorCodigo(proveedor.getCodigo()) == null) {
      throw new IllegalStateException("No existe un proveedor con el código: " + proveedor.getCodigo());
    }

    Proveedor.actualizar(proveedor);
  }

  public List<OrdenCompraDTO> obtenerOrdenesCompra() {
    return OrdenCompra.obtenerTodas();
  }

  public OrdenCompraDTO obtenerOrdenPorNumero(String numero) {
    if (numero == null || numero.isBlank()) {
      throw new IllegalArgumentException("El número de orden no puede estar vacío.");
    }

    OrdenCompraDTO orden = OrdenCompra.obtenerPorNumero(numero);
    if (orden == null) {
      throw new IllegalStateException("No existe una orden con el número: " + numero);
    }

    return orden;
  }

  public void guardarOrdenCompra(ProveedorDTO proveedor, int cantidadProductos, BigDecimal total) {
    if (proveedor == null) {
      throw new IllegalArgumentException("El proveedor no puede ser nulo.");
    }
    if (cantidadProductos <= 0) {
      throw new IllegalArgumentException("La cantidad de productos debe ser mayor a cero.");
    }
    if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El total de la orden debe ser mayor a cero.");
    }
    if (Proveedor.obtenerPorCodigo(proveedor.getCodigo()) == null) {
      throw new IllegalStateException("No existe un proveedor con el código: " + proveedor.getCodigo());
    }

    ProveedorDTO provRef = new ProveedorDTO(
      proveedor.getNombre(), proveedor.getCodigo(),
      proveedor.getContacto(), proveedor.getTelefono(), proveedor.getEmail(),
      proveedor.getDireccion(), proveedor.getTerminosPago(), proveedor.isActivo()
    );

    OrdenCompraDTO orden = new OrdenCompraDTO(
      generarNumeroOrden(),
      LocalDate.now().toString(),
      provRef,
      "Pendiente",
      cantidadProductos,
      total
    );

    OrdenCompra.guardar(orden);
  }

  public void actualizarOrdenCompra(OrdenCompraDTO orden) {
    if (orden == null) {
      throw new IllegalArgumentException("La orden no puede ser nula.");
    }
    if (orden.getNumero() == null || orden.getNumero().isBlank()) {
      throw new IllegalArgumentException("El número de orden es obligatorio.");
    }
    if (OrdenCompra.obtenerPorNumero(orden.getNumero()) == null) {
      throw new IllegalStateException("No existe una orden con el número: " + orden.getNumero());
    }

    OrdenCompra.actualizar(orden);
  }

  public void cambiarEstadoOrden(String numero, String nuevoEstado) {
    if (numero == null || numero.isBlank()) {
      throw new IllegalArgumentException("El número de orden no puede estar vacío.");
    }
    if (nuevoEstado == null || nuevoEstado.isBlank()) {
      throw new IllegalArgumentException("El nuevo estado no puede estar vacío.");
    }

    OrdenCompraDTO orden = OrdenCompra.obtenerPorNumero(numero);
    if (orden == null) {
      throw new IllegalStateException("No existe una orden con el número: " + numero);
    }

    List<String> estadosValidos = List.of("Pendiente", "Aprobada", "Recibida", "Cancelada");
    if (!estadosValidos.contains(nuevoEstado)) {
      throw new IllegalArgumentException("Estado inválido. Los estados válidos son: " + estadosValidos);
    }

    orden.setEstado(nuevoEstado);
    OrdenCompra.actualizar(orden);
  }

  private String generarCodigoProveedor() {
    return "PROV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private String generarNumeroOrden() {
    return String.format("OC-%d-%s",
      LocalDate.now().getYear(),
      UUID.randomUUID().toString().substring(0, 8).toUpperCase());
  }

}
