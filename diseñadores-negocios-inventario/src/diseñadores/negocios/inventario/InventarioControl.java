package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.objetos.Inventario;
import java.util.List;

public class InventarioControl {

  public ProductoDTO obtenerProductoPorCodigo(String codigo) {
    return Inventario.obtenerProductoPorCodigo(codigo);
  }

  public List<ProductoDTO> obtenerTodos() {
    return Inventario.obtenerTodos();
  }

  public List<ProductoDTO> obtenerProductosBajoMinimo() {
    return Inventario.obtenerProductosBajoMinimo();
  }

  public List<ProductoDTO> necesitanReorden() {
    return Inventario.necesitanReorden();
  }

  public boolean verificarStock(String codigo, int cantidad) {
    if (codigo == null || codigo.isBlank()) {
      return false;
    }
    if (cantidad <= 0) {
      return false;
    }
    return Inventario.verificarStock(codigo, cantidad);
  }

  public void descontarStock(String codigo, int cantidad) {
    if (cantidad <= 0) {
      throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero.");
    }

    ProductoDTO p = Inventario.obtenerProductoPorCodigo(codigo);
    if (p == null) {
      throw new IllegalStateException("No existe un producto con el código: " + codigo);
    }
    if (p.getStock() < cantidad) {
      throw new IllegalStateException("Stock insuficiente. Disponible: " + p.getStock() + ", requerido: " + cantidad);
    }

    Inventario.descontarStock(codigo, cantidad);
  }

  public void actualizarStock(String codigo, int nuevaCantidad) {
    if (nuevaCantidad < 0) {
      throw new IllegalArgumentException("El stock no puede ser negativo.");
    }

    ProductoDTO p = Inventario.obtenerProductoPorCodigo(codigo);
    if (p == null) {
      throw new IllegalStateException("No existe un producto con el código: " + codigo);
    }

    Inventario.actualizarStock(codigo, nuevaCantidad);
  }

  public void actualizarStockCompleto(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    if (nuevoStock < 0 || nuevoMinimo < 0 || nuevoMaximo < 0) {
      throw new IllegalArgumentException("Los valores de stock no pueden ser negativos.");
    }
    if (nuevoMinimo > nuevoMaximo) {
      throw new IllegalArgumentException("El stock mínimo no puede ser mayor al máximo.");
    }

    ProductoDTO p = Inventario.obtenerProductoPorCodigo(codigo);
    if (p == null) {
      throw new IllegalStateException("No existe un producto con el código: " + codigo);
    }

    Inventario.actualizarStockCompleto(codigo, nuevoStock, nuevoMinimo, nuevoMaximo);
  }

  public void ajustarStock(String codigo, int stockFisico) {
    if (stockFisico < 0) {
      throw new IllegalArgumentException("El stock físico no puede ser negativo.");
    }

    ProductoDTO p = Inventario.obtenerProductoPorCodigo(codigo);
    if (p == null) {
      throw new IllegalStateException("No existe un producto con el código: " + codigo);
    }

    Inventario.ajustarStock(codigo, stockFisico);
  }

  public int[] obtenerEstadisticasConteo() {
    return Inventario.obtenerEstadisticasConteo();
  }

}
