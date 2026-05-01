package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.ProductoDTO;

import java.util.List;

public class ProductosFacade implements IProductos {

  private final ProductosControl control;

  public ProductosFacade() {
    this.control = new ProductosControl();
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
  public boolean existeProducto(EscanearProductoDTO dto) {
    return control.existeProducto(dto);
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto) {
    return control.tieneStock(dto);
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
