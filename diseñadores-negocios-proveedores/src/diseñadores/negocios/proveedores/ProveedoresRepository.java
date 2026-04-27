package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import java.util.ArrayList;
import java.util.List;

public class ProveedoresRepository {

  private static ProveedoresRepository instancia;
  private final List<ProveedorDTO> proveedores;
  private final List<OrdenCompraDTO> ordenesCompra;

  private ProveedoresRepository() {
    this.proveedores = new ArrayList<>();
    this.ordenesCompra = new ArrayList<>();
    inicializarMocks();
  }

  public static synchronized ProveedoresRepository getInstancia() {
    if (instancia == null) {
      instancia = new ProveedoresRepository();
    }
    return instancia;
  }

  public List<ProveedorDTO> getProveedores() {
    return proveedores;
  }

  public List<OrdenCompraDTO> getOrdenesCompra() {
    return ordenesCompra;
  }

  public void agregarProveedor(ProveedorDTO proveedor) {
    proveedores.add(proveedor);
  }

  public void actualizarProveedor(ProveedorDTO proveedor) {
    for (int i = 0; i < proveedores.size(); i++) {
      if (proveedores.get(i).getCodigo().equalsIgnoreCase(proveedor.getCodigo())) {
        proveedores.set(i, proveedor);
        return;
      }
    }
  }

  public void agregarOrdenCompra(OrdenCompraDTO orden) {
    ordenesCompra.add(orden);
  }

  public void actualizarOrdenCompra(OrdenCompraDTO orden) {
    for (int i = 0; i < ordenesCompra.size(); i++) {
      if (ordenesCompra.get(i).getNumero().equalsIgnoreCase(orden.getNumero())) {
        ordenesCompra.set(i, orden);
        return;
      }
    }
  }

  private void inicializarMocks() {
    proveedores.add(new ProveedorDTO("Distribuidora Central", "PROV-001", "Juan Pérez", "555-0101",
      "contacto@distcentral.com", "Av. Principal 123, Col. Centro", "30 días", true));
    proveedores.add(new ProveedorDTO("Alimentos del Norte", "PROV-002", "María González", "555-0202",
      "ventas@alimnorte.com", "Calle Comercio 456, Col. Industrial", "15 días", true));
    proveedores.add(new ProveedorDTO("Lácteos Premium", "PROV-003", "Carlos Ramírez", "555-0303",
      "info@lacteospremium.com", "Blvd. Lácteo 789, Col. Valle", "45 días", true));
    proveedores.add(new ProveedorDTO("Granos y Semillas SA", "PROV-004", "Ana López", "555-0404",
      "contacto@granossemillas.com", "Carretera Norte Km 12", "30 días", false));

    ordenesCompra.add(new OrdenCompraDTO("OC-2026-001", "2026-04-20", proveedores.get(0), "Pendiente", 15, 12500.00));
    ordenesCompra.add(new OrdenCompraDTO("OC-2026-002", "2026-04-18", proveedores.get(1), "Aprobada", 23, 8750.50));
    ordenesCompra.add(new OrdenCompraDTO("OC-2026-003", "2026-04-15", proveedores.get(2), "Recibida", 10, 5200.00));
  }

}