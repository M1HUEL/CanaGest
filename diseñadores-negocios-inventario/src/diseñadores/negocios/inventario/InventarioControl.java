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
    if (codigo == null || codigo.isBlank() || cantidad <= 0) {
      return false;
    }
    return Inventario.verificarStock(codigo, cantidad);
  }

  public void descontarStock(String codigo, int cantidad) {
    if (cantidad <= 0) {
      throw new IllegalArgumentException("Cantidad mayor a cero requerida");
    }
    ProductoDTO p = Inventario.obtenerProductoPorCodigo(codigo);
    if (p == null) {
      throw new IllegalStateException("Producto no existe");
    }
    if (p.getStock() < cantidad) {
      throw new IllegalStateException("Stock insuficiente");
    }
    Inventario.descontarStock(codigo, cantidad);
  }

  public void actualizarStock(String codigo, int nuevaCantidad) {
    if (nuevaCantidad < 0) {
      throw new IllegalArgumentException("Stock negativo no permitido");
    }
    if (Inventario.obtenerProductoPorCodigo(codigo) == null) {
      throw new IllegalStateException("Producto no existe");
    }
    Inventario.actualizarStock(codigo, nuevaCantidad);
  }

  public void actualizarStockCompleto(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    if (nuevoStock < 0 || nuevoMinimo < 0 || nuevoMaximo < 0) {
      throw new IllegalArgumentException("Valores negativos no permitidos");
    }
    if (nuevoMinimo > nuevoMaximo) {
      throw new IllegalArgumentException("Mínimo mayor al máximo");
    }
    if (Inventario.obtenerProductoPorCodigo(codigo) == null) {
      throw new IllegalStateException("Producto no existe");
    }
    Inventario.actualizarStockCompleto(codigo, nuevoStock, nuevoMinimo, nuevoMaximo);
  }

  public void ajustarStock(String codigo, int stockFisico) {
    if (stockFisico < 0) {
      throw new IllegalArgumentException("Stock físico negativo");
    }
    if (Inventario.obtenerProductoPorCodigo(codigo) == null) {
      throw new IllegalStateException("Producto no existe");
    }
    Inventario.ajustarStock(codigo, stockFisico);
  }

  public int[] obtenerEstadisticasConteo() {
    return Inventario.obtenerEstadisticasConteo();
  }

}
