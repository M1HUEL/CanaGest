package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.ProveedorDTO;
import java.util.ArrayList;
import java.util.List;

public class ProveedoresRepository {

  private static ProveedoresRepository instancia;
  private final List<ProveedorDTO> proveedores;

  private ProveedoresRepository() {
    this.proveedores = new ArrayList<>();
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

  private void inicializarMocks() {
    proveedores.add(new ProveedorDTO("Distribuidora Central", "PROV-001", "Juan Pérez", "555-0101",
      "contacto@distcentral.com", "Av. Principal 123, Col. Centro", "30 días", true));
    proveedores.add(new ProveedorDTO("Alimentos del Norte", "PROV-002", "María González", "555-0202",
      "ventas@alimnorte.com", "Calle Comercio 456, Col. Industrial", "15 días", true));
    proveedores.add(new ProveedorDTO("Lácteos Premium", "PROV-003", "Carlos Ramírez", "555-0303",
      "info@lacteospremium.com", "Blvd. Lácteo 789, Col. Valle", "45 días", true));
    proveedores.add(new ProveedorDTO("Granos y Semillas SA", "PROV-004", "Ana López", "555-0404",
      "contacto@granossemillas.com", "Carretera Norte Km 12", "30 días", false));
  }

}