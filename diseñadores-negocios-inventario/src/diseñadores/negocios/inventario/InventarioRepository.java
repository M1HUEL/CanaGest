package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepository {

  private static InventarioRepository instancia;
  private final List<ProductoDTO> productos;

  private InventarioRepository() {
    this.productos = new ArrayList<>();
    inicializarMocks();
  }

  public static synchronized InventarioRepository getInstancia() {
    if (instancia == null) {
      instancia = new InventarioRepository();
    }
    return instancia;
  }

  public List<ProductoDTO> getProductos() {
    return productos;
  }

  public void actualizarStock(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    for (ProductoDTO p : productos) {
      if (p.getCodigo().equals(codigo)) {
        p.setStockActual(nuevoStock);
        p.setStockMinimo(nuevoMinimo);
        p.setStockMaximo(nuevoMaximo);
        return;
      }
    }
  }

  private void inicializarMocks() {
    ProveedorDTO provGranos = new ProveedorDTO("Abarrotes del Mayo", "PROV-001", "Juan Pérez", "555-0101", "ventas@mayo.com", "Av. Mayo 123", "30 días", true);
    ProveedorDTO provAceites = new ProveedorDTO("Distribuidora Sonora", "PROV-002", "María López", "555-0202", "contacto@distsonora.com", "Calle Sonora 456", "15 días", true);

    productos.add(new ProductoDTO("7501001000011", "Arroz", 28.00, 50, 10, 100, provGranos));
    productos.add(new ProductoDTO("7501001000028", "Frijol", 32.00, 30, 5, 80, provGranos));
    productos.add(new ProductoDTO("7501001000035", "Azúcar", 26.00, 20, 5, 60, provGranos));
    productos.add(new ProductoDTO("7501002000010", "Aceite", 48.00, 15, 3, 50, provAceites));
    productos.add(new ProductoDTO("7501002000027", "Atún", 18.00, 40, 10, 100, provAceites));
    productos.add(new ProductoDTO("7501003000019", "Leche", 30.00, 35, 8, 90, provGranos));
    productos.add(new ProductoDTO("7501001000042", "Sal", 8.00, 60, 15, 150, provGranos));
    productos.add(new ProductoDTO("7501004000018", "Café", 55.00, 25, 5, 70, provGranos));
    productos.add(new ProductoDTO("7501005000017", "Jabón", 22.00, 45, 10, 120, provAceites));
  }

}
