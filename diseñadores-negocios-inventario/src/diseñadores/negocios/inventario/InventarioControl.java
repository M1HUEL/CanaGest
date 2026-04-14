package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.Producto;
import diseñadores.negocios.dto.ProductoDTO;

public class InventarioControl {

  public ProductoDTO obtenerProductoDTO(String codigo) {
    Producto p = obtenerEntidad(codigo);
    return p != null ? toDTO(p) : null;
  }

  public boolean verificarStock(String codigo, int cantidad) {
    Producto p = obtenerEntidad(codigo);
    return p != null && p.getStock() >= cantidad;
  }

  public void descontar(String codigo, int cantidad) {
    Producto p = obtenerEntidad(codigo);
    if (p != null && p.getStock() >= cantidad) {
      p.setStock(p.getStock() - cantidad);
    }
  }

  public void actualizarStock(String codigo, int nuevaCantidad) {
    Producto p = obtenerEntidad(codigo);
    if (p != null) {
      p.setStock(nuevaCantidad);
    }
  }

  Producto obtenerEntidad(String codigo) {
    return InventarioRepository.getInstancia().getDatos().stream()
      .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
      .findFirst()
      .orElse(null);
  }

  private ProductoDTO toDTO(Producto p) {
    return new ProductoDTO(p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock());
  }

}
