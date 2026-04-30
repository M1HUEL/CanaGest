package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;
import java.util.List;
import java.util.stream.Collectors;

public class InventarioControl {

  public ProductoDTO obtenerProductoPorCodigo(String codigo) {
    return InventarioRepository.getInstancia().getProductos().stream()
      .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
      .findFirst()
      .orElse(null);
  }

  public List<ProductoDTO> obtenerTodos() {
    return InventarioRepository.getInstancia().getProductos();
  }

  public List<ProductoDTO> obtenerProductosBajoMinimo() {
    return InventarioRepository.getInstancia().getProductos().stream()
      .filter(ProductoDTO::estaBajoMinimo)
      .collect(Collectors.toList());
  }

  public List<ProductoDTO> necesitanReorden() {
    return InventarioRepository.getInstancia().getProductos().stream()
      .filter(ProductoDTO::necesitaReorden)
      .collect(Collectors.toList());
  }

  public boolean verificarStock(String codigo, int cantidad) {
    ProductoDTO p = obtenerProductoPorCodigo(codigo);
    return p != null && p.getStock() >= cantidad;
  }

  public void descontarStock(String codigo, int cantidad) {
    ProductoDTO p = obtenerProductoPorCodigo(codigo);
    if (p != null && p.getStock() >= cantidad) {
      p.setStockActual(p.getStock() - cantidad);
    }
  }

  public void actualizarStock(String codigo, int nuevaCantidad) {
    ProductoDTO p = obtenerProductoPorCodigo(codigo);
    if (p != null) {
      p.setStockActual(nuevaCantidad);
    }
  }

  public void actualizarStockCompleto(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    InventarioRepository.getInstancia().actualizarStock(codigo, nuevoStock, nuevoMinimo, nuevoMaximo);
  }

  public void ajustarStock(String codigo, int stockFisico) {
    ProductoDTO p = obtenerProductoPorCodigo(codigo);
    if (p != null) {
      p.setStockActual(stockFisico);
    }
  }

  public int[] obtenerEstadisticasConteo() {
    List<ProductoDTO> productos = obtenerTodos();
    int total = productos.size();
    int pendientes = (int) productos.stream().filter(p -> p.getStock() < p.getStockMinimo()).count();
    int diferencias = productos.stream()
      .mapToInt(p -> Math.abs(p.getStock() - (p.getStockMinimo() + p.getStockMaximo()) / 2))
      .sum();
    return new int[]{total, pendientes, diferencias};
  }

}
