package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;

public class InventarioControl {

  public ProductoDTO obtenerProductoPorCodigo(String codigo) {
    return InventarioRepository.getInstancia().getProductos().stream()
      .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
      .findFirst()
      .orElse(null);
  }

  public boolean verificarStock(String codigo, int cantidad) {
    ProductoDTO p = obtenerProductoPorCodigo(codigo);
    return p != null && p.getStock() >= cantidad;
  }

  public void descontarStock(String codigo, int cantidad) {
    ProductoDTO p = obtenerProductoPorCodigo(codigo);
    if (p != null && p.getStock() >= cantidad) {
      p.setStock(p.getStock() - cantidad);
    }
  }

  public void actualizarStock(String codigo, int nuevaCantidad) {
    ProductoDTO p = obtenerProductoPorCodigo(codigo);
    if (p != null) {
      p.setStock(nuevaCantidad);
    }
  }

}
