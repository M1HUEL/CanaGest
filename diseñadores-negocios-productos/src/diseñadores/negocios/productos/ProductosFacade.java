package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.inventario.InventarioControl;
import java.util.List;

public class ProductosFacade implements IProductos {

  private final ProductosControl control;

  public ProductosFacade() {
    InventarioControl inventarioControl = new InventarioControl();
    this.control = new ProductosControl(inventarioControl);
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return control.obtenerTodosProductos();
  }

  @Override
  public ProductoDTO buscarProductoPorCodigo(EscanearProductoDTO dto) {
    return control.buscar(dto);
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO dto) {
    return control.buscarProductoPorCodigo(dto);
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto) {
    return control.tieneStock(dto);
  }

  public ProductosControl getControl() {
    return control;
  }

}
