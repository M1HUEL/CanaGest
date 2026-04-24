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

  private void inicializarMocks() {
    ProveedorDTO provGranos = new ProveedorDTO("Abarrotes del Mayo", "ventas@mayo.com");
    ProveedorDTO provAceites = new ProveedorDTO("Distribuidora Sonora", "contacto@distsonora.com");

    productos.add(new ProductoDTO("7501001000011", "Arroz", 28.00, 50, provGranos));
    productos.add(new ProductoDTO("7501001000028", "Frijol", 32.00, 30, provGranos));
    productos.add(new ProductoDTO("7501001000035", "Azúcar", 26.00, 20, provGranos));
    productos.add(new ProductoDTO("7501002000010", "Aceite", 48.00, 15, provAceites));
    productos.add(new ProductoDTO("7501002000027", "Atún", 18.00, 40, provAceites));
    productos.add(new ProductoDTO("7501003000019", "Leche", 30.00, 35, provGranos));
    productos.add(new ProductoDTO("7501001000042", "Sal", 8.00, 60, provGranos));
    productos.add(new ProductoDTO("7501004000018", "Café", 55.00, 25, provGranos));
    productos.add(new ProductoDTO("7501005000017", "Jabón", 22.00, 45, provAceites));
  }

}
