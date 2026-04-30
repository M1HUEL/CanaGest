package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.inventario.IInventario;
import java.util.List;

public class ProductosFacade implements IProductos {

  private final IInventario inventarioControl;

  public ProductosFacade(IInventario inventarioControl) {
    this.inventarioControl = inventarioControl;
  }

  @Override
  public List<ProductoDTO> obtenerCatalogo() {
    return inventarioControl.obtenerTodos();
  }

  @Override
  public ProductoDTO buscarProductoPorCodigo(EscanearProductoDTO dto) {
    return inventarioControl.obtenerProductoPorCodigo(dto.getCodigo());
  }

  @Override
  public boolean existeProducto(EscanearProductoDTO dto) {
    return inventarioControl.obtenerProductoPorCodigo(dto.getCodigo()) != null;
  }

  @Override
  public boolean tieneStock(EscanearProductoDTO dto) {
    return inventarioControl.verificarStock(dto.getCodigo(), 1);
  }

}
