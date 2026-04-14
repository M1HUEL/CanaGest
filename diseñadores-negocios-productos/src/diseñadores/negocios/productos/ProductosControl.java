package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.Producto;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.inventario.InventarioRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ProductosControl {

  public boolean existe(EscanearProductoDTO dto) {
    return encontrar(dto.getCodigo()) != null;
  }

  public boolean tieneStock(EscanearProductoDTO dto) {
    Producto p = encontrar(dto.getCodigo());
    return p != null && p.getStock() > 0;
  }

  public ProductoDTO buscar(EscanearProductoDTO dto) {
    Producto p = encontrar(dto.getCodigo());
    return p != null ? toDTO(p) : null;
  }

  public List<ProductoDTO> obtenerCatalogo() {
    return InventarioRepository.getInstancia().getDatos().stream()
      .map(this::toDTO)
      .collect(Collectors.toList());
  }

  public Producto encontrarEntidad(String criterio) {
    return encontrar(criterio);
  }

  private Producto encontrar(String criterio) {
    return InventarioRepository.getInstancia().getDatos().stream()
      .filter(p -> p.getCodigo().equalsIgnoreCase(criterio)
      || p.getNombre().equalsIgnoreCase(criterio))
      .findFirst()
      .orElse(null);
  }

  private ProductoDTO toDTO(Producto p) {
    return new ProductoDTO(p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock());
  }

}
