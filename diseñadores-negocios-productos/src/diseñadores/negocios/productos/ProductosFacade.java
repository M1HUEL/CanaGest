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
    return control.buscar(dto) != null;
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return control.obtenerCatalogo();
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto) {
    ProductoDTO p = control.buscar(dto);
    return p != null && p.getStock() > 0;
  }

}
