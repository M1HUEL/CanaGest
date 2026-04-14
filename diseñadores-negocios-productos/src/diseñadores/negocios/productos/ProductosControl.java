package diseñadores.negocios.productos;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.InventarioRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ProductosControl {

  public ProductoDTO buscar(EscanearProductoDTO dto) {
    Producto p = encontrarEnRepo(dto.getCodigo());
    return (p != null) ? toDTO(p) : null;
  }

  public List<ProductoDTO> obtenerCatalogo() {
    return InventarioRepository.getInstancia().getDatos().stream()
      .map(this::toDTO)
      .collect(Collectors.toList());
  }

  private Producto encontrarEnRepo(String criterio) {
    return InventarioRepository.getInstancia().getDatos().stream()
      .filter(p -> p.getCodigo().equalsIgnoreCase(criterio)
      || p.getNombre().equalsIgnoreCase(criterio))
      .findFirst().orElse(null);
  }

  private ProductoDTO toDTO(Producto p) {
    return new ProductoDTO(p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock());
  }

}
