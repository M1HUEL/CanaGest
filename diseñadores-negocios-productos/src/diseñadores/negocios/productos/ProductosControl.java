package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.Producto;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.Proveedor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductosControl implements IProductos {

  private final List<Producto> inventario;

  public ProductosControl() {
    this.inventario = new ArrayList<>();

    Proveedor provGranos = new Proveedor("Abarrotes del Mayo", "ventas@mayo.com");
    Proveedor provAceites = new Proveedor("Distribuidora Sonora", "contacto@distsonora.com");

    inventario.add(new Producto("PROD-8342-2323", "Arroz", 28.00, 50, provGranos));
    inventario.add(new Producto("PROD-8342-2324", "Frijol", 32.00, 30, provGranos));
    inventario.add(new Producto("PROD-8342-2325", "Azúcar", 26.00, 20, provGranos));
    inventario.add(new Producto("PROD-8342-2326", "Aceite", 48.00, 15, provAceites));
    inventario.add(new Producto("PROD-8342-2327", "Atún", 18.00, 40, provAceites));
    inventario.add(new Producto("PROD-8342-2328", "Leche", 30.00, 35, provGranos));
    inventario.add(new Producto("PROD-8342-2329", "Sal", 8.00, 60, provGranos));
    inventario.add(new Producto("PROD-8342-2330", "Café", 55.00, 25, provGranos));
    inventario.add(new Producto("PROD-8342-2331", "Jabón", 22.00, 45, provAceites));
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO dto) {
    return encontrar(dto.getCodigo()) != null;
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto) {
    Producto p = encontrar(dto.getCodigo());
    return p != null && p.getStock() > 0;
  }

  @Override
  public ProductoDTO buscarProducto(EscanearProductoDTO dto) {
    Producto p = encontrar(dto.getCodigo());
    if (p == null || p.getStock() <= 0) {
      return null;
    }
    return toDTO(p);
  }

  @Override
  public void reducirStock(String codigo) {
    Producto p = encontrar(codigo);
    if (p != null && p.getStock() > 0) {
      p.setStock(p.getStock() - 1);
    }
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return inventario.stream()
      .map(this::toDTO)
      .collect(Collectors.toList());
  }

  public Producto obtenerEntidadPorCodigo(String codigo) {
    return encontrar(codigo);
  }

  private Producto encontrar(String codigoONombre) {
    return inventario.stream()
      .filter(p -> p.getCodigo().equalsIgnoreCase(codigoONombre)
      || p.getNombre().equalsIgnoreCase(codigoONombre))
      .findFirst()
      .orElse(null);
  }

  private ProductoDTO toDTO(Producto p) {
    return new ProductoDTO(p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock());
  }

}
