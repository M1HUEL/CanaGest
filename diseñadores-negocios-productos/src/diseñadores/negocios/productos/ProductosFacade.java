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
  public ProductoDTO buscarProducto(EscanearProductoDTO dto) {
    return control.buscar(dto);
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO dto) {
    return control.existe(dto);
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto) {
    return control.tieneStock(dto);
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return control.obtenerCatalogo();
  }

  public ProductosControl getControl() {
    return control;
  }

}
