package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.Producto;
import diseñadores.negocios.dto.Proveedor;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepository {

  private static InventarioRepository instancia;
  private final List<Producto> datos;

  private InventarioRepository() {
    this.datos = new ArrayList<>();
    inicializarMocks();
  }

  public static synchronized InventarioRepository getInstancia() {
    if (instancia == null) {
      instancia = new InventarioRepository();
    }
    return instancia;
  }

  public List<Producto> getDatos() {
    return datos;
  }

  private void inicializarMocks() {
    Proveedor provGranos = new Proveedor("Abarrotes del Mayo", "ventas@mayo.com");
    Proveedor provAceites = new Proveedor("Distribuidora Sonora", "contacto@distsonora.com");

    datos.add(new Producto("PROD-8342-2323", "Arroz", 28.00, 50, provGranos));
    datos.add(new Producto("PROD-8342-2324", "Frijol", 32.00, 30, provGranos));
    datos.add(new Producto("PROD-8342-2325", "Azúcar", 26.00, 20, provGranos));
    datos.add(new Producto("PROD-8342-2326", "Aceite", 48.00, 15, provAceites));
    datos.add(new Producto("PROD-8342-2327", "Atún", 18.00, 40, provAceites));
    datos.add(new Producto("PROD-8342-2328", "Leche", 30.00, 35, provGranos));
    datos.add(new Producto("PROD-8342-2329", "Sal", 8.00, 60, provGranos));
    datos.add(new Producto("PROD-8342-2330", "Café", 55.00, 25, provGranos));
    datos.add(new Producto("PROD-8342-2331", "Jabón", 22.00, 45, provAceites));
  }

}
