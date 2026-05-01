package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.persistencia.IPersistencia;
import diseñadores.persistencia.PersistenciaFacade;

import java.util.List;
import java.util.stream.Collectors;

public class Inventario {

  private static final IPersistencia PERSISTENCIA = PersistenciaFacade.getInstancia();

  public static List<ProductoDTO> obtenerTodos() {
    return PERSISTENCIA.obtenerProductos();
  }

  public static ProductoDTO obtenerProductoPorCodigo(String codigo) {
    return PERSISTENCIA.obtenerProductoPorCodigo(codigo);
  }

  public static List<ProductoDTO> obtenerProductosBajoMinimo() {
    return PERSISTENCIA.obtenerProductos().stream()
      .filter(ProductoDTO::estaBajoMinimo)
      .collect(Collectors.toList());
  }

  public static List<ProductoDTO> necesitanReorden() {
    return PERSISTENCIA.obtenerProductos().stream()
      .filter(ProductoDTO::necesitaReorden)
      .collect(Collectors.toList());
  }

  public static boolean verificarStock(String codigo, int cantidad) {
    ProductoDTO p = PERSISTENCIA.obtenerProductoPorCodigo(codigo);
    return p != null && p.getStock() >= cantidad;
  }

  public static void descontarStock(String codigo, int cantidad) {
    ProductoDTO p = PERSISTENCIA.obtenerProductoPorCodigo(codigo);
    if (p != null && p.getStock() >= cantidad) {
      p.setStockActual(p.getStock() - cantidad);
      PERSISTENCIA.actualizarProducto(p);
    }
  }

  public static void actualizarStock(String codigo, int nuevaCantidad) {
    ProductoDTO p = PERSISTENCIA.obtenerProductoPorCodigo(codigo);
    if (p != null) {
      p.setStockActual(nuevaCantidad);
      PERSISTENCIA.actualizarProducto(p);
    }
  }

  public static void actualizarStockCompleto(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    ProductoDTO p = PERSISTENCIA.obtenerProductoPorCodigo(codigo);
    if (p != null) {
      p.setStockActual(nuevoStock);
      p.setStockMinimo(nuevoMinimo);
      p.setStockMaximo(nuevoMaximo);
      PERSISTENCIA.actualizarProducto(p);
    }
  }

  public static void ajustarStock(String codigo, int stockFisico) {
    ProductoDTO p = PERSISTENCIA.obtenerProductoPorCodigo(codigo);
    if (p != null) {
      p.setStockActual(stockFisico);
      PERSISTENCIA.actualizarProducto(p);
    }
  }

  public static int[] obtenerEstadisticasConteo() {
    List<ProductoDTO> productos = PERSISTENCIA.obtenerProductos();
    int total = productos.size();
    int pendientes = (int) productos.stream()
      .filter(p -> p.getStock() < p.getStockMinimo())
      .count();
    int diferencias = productos.stream()
      .mapToInt(p -> Math.abs(p.getStock() - (p.getStockMinimo() + p.getStockMaximo()) / 2))
      .sum();
    return new int[]{total, pendientes, diferencias};
  }

}
