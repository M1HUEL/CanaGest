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
    if (esConsultaInvalida(codigo, cantidad)) {
      return false;
    }
    return ejecutarVerificacionStock(codigo, cantidad);
  }

  public void descontarStock(String codigo, int cantidad) {
    validarCantidadPositiva(cantidad);

    ProductoDTO producto = Inventario.obtenerProductoPorCodigo(codigo);
    validarExistenciaProducto(producto);
    validarSuficienciaStock(producto, cantidad);

    Inventario.descontarStock(codigo, cantidad);
  }

  public void actualizarStock(String codigo, int nuevaCantidad) {
    validarStockNoNegativo(nuevaCantidad);
    validarExistenciaProductoPorCodigo(codigo);

    Inventario.actualizarStock(codigo, nuevaCantidad);
  }

  public void actualizarStockCompleto(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    validarValoresNoNegativos(nuevoStock, nuevoMinimo, nuevoMaximo);
    validarJerarquiaStock(nuevoMinimo, nuevoMaximo);
    validarExistenciaProductoPorCodigo(codigo);

    ejecutarActualizacionCompleta(codigo, nuevoStock, nuevoMinimo, nuevoMaximo);
  }

  public void ajustarStock(String codigo, int stockFisico) {
    validarStockFisicoNoNegativo(stockFisico);
    validarExistenciaProductoPorCodigo(codigo);

    Inventario.ajustarStock(codigo, stockFisico);
  }

  public int[] obtenerEstadisticasConteo() {
    return Inventario.obtenerEstadisticasConteo();
  }

  private boolean esConsultaInvalida(String codigo, int cantidad) {
    return codigo == null || codigo.isBlank() || cantidad <= 0;
  }

  private void validarCantidadPositiva(int cantidad) {
    if (cantidad <= 0) {
      throw new IllegalArgumentException("Cantidad mayor a cero requerida");
    }
  }

  private void validarStockNoNegativo(int cantidad) {
    if (cantidad < 0) {
      throw new IllegalArgumentException("Stock negativo no permitido");
    }
  }

  private void validarStockFisicoNoNegativo(int stockFisico) {
    if (stockFisico < 0) {
      throw new IllegalArgumentException("Stock físico negativo");
    }
  }

  private void validarValoresNoNegativos(int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    if (nuevoStock < 0 || nuevoMinimo < 0 || nuevoMaximo < 0) {
      throw new IllegalArgumentException("Valores negativos no permitidos");
    }
  }

  private void validarJerarquiaStock(int minimo, int maximo) {
    if (minimo > maximo) {
      throw new IllegalArgumentException("Mínimo mayor al máximo");
    }
  }

  private void validarExistenciaProducto(ProductoDTO p) {
    if (p == null) {
      throw new IllegalStateException("Producto no existe");
    }
  }

  private void validarExistenciaProductoPorCodigo(String codigo) {
    if (ejecutarObtencionPorCodigo(codigo) == null) {
      throw new IllegalStateException("Producto no existe");
    }
  }

  private void validarSuficienciaStock(ProductoDTO p, int cantidad) {
    if (p.getStock() < cantidad) {
      throw new IllegalStateException("Stock insuficiente");
    }
  }

  private ProductoDTO ejecutarObtencionPorCodigo(String codigo) {
    return Inventario.obtenerProductoPorCodigo(codigo);
  }

  private boolean ejecutarVerificacionStock(String codigo, int cantidad) {
    return Inventario.verificarStock(codigo, cantidad);
  }

  private void ejecutarActualizacionCompleta(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    Inventario.actualizarStockCompleto(codigo, nuevoStock, nuevoMinimo, nuevoMaximo);
  }

}
