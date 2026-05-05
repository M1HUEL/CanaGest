package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import java.util.List;

public class ProductosFacade implements IProductos {

  private final ProductosControl control;

  public ProductosFacade() {
    this.control = new ProductosControl();
  }

  public ProductosFacade(ProductosControl control) {
    this.control = control;
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return control.obtenerCatalogo();
  }

  @Override
  public ProductoDTO buscarProductoPorCodigo(EscanearProductoDTO dto) {
    return control.buscarProductoPorCodigo(dto);
  }

  @Override
  public boolean validarExistenciaProducto(EscanearProductoDTO dto) {
    return control.validarExistenciaProducto(dto);
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto, int cantidad) {
    return control.tieneStock(dto, cantidad);
  }

  @Override
  public void descontarStock(String codigo, int cantidad) {
    control.descontarStock(codigo, cantidad);
  }

  @Override
  public void guardarProducto(ProductoDTO producto) {
    control.guardarProducto(producto);
  }

  @Override
  public void actualizarProducto(ProductoDTO producto) {
    control.actualizarProducto(producto);
  }

  @Override
  public void eliminarProducto(String codigo) {
    control.eliminarProducto(codigo);
  }

}
