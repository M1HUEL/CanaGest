package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.objetos.Inventario;
import java.util.List;

public class InventarioControl {

  public ProductoDTO obtenerProductoPorCodigo(String codigo) {
    return ejecutarObtencionPorCodigo(codigo);
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

    ProductoDTO producto = ejecutarObtencionPorCodigo(codigo);
    validarExistenciaProducto(producto);
    validarSuficienciaStock(producto, cantidad);

    ejecutarDescuentoStock(codigo, cantidad);
  }

  public void actualizarStock(String codigo, int nuevaCantidad) {
    validarStockNoNegativo(nuevaCantidad);
    validarExistenciaProductoPorCodigo(codigo);

    ejecutarActualizacionStock(codigo, nuevaCantidad);
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

    ejecutarAjusteStock(codigo, stockFisico);
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

  private void validarValoresNoNegativos(int s, int min, int max) {
    if (s < 0 || min < 0 || max < 0) {
      throw new IllegalArgumentException("Valores negativos no permitidos");
    }
  }

  private void validarJerarquiaStock(int min, int max) {
    if (min > max) {
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

  private void ejecutarDescuentoStock(String codigo, int cantidad) {
    Inventario.descontarStock(codigo, cantidad);
  }

  private void ejecutarActualizacionStock(String codigo, int nuevaCantidad) {
    Inventario.actualizarStock(codigo, nuevaCantidad);
  }

  private void ejecutarActualizacionCompleta(String c, int s, int min, int max) {
    Inventario.actualizarStockCompleto(c, s, min, max);
  }

  private void ejecutarAjusteStock(String codigo, int stockFisico) {
    Inventario.ajustarStock(codigo, stockFisico);
  }

}
