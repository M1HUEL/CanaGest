package diseñadores.negocios.productos;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.*;
import java.util.List;

public class ProductosControl {

  private final InventarioControl inventarioControl;

  public ProductosControl(InventarioControl inventarioControl) {
    this.inventarioControl = inventarioControl;
  }

  public List<ProductoDTO> obtenerTodosProductos() {
    return InventarioRepository.getInstancia().getProductos();
  }

  public boolean buscarProductoPorCodigo(EscanearProductoDTO dto) {
    return inventarioControl.obtenerProductoPorCodigo(dto.getCodigo()) != null;
  }

  public boolean tieneStock(EscanearProductoDTO dto) {
    return inventarioControl.verificarStock(dto.getCodigo(), 1);
  }

  public ProductoDTO buscar(EscanearProductoDTO dto) {
    return inventarioControl.obtenerProductoPorCodigo(dto.getCodigo());
  }

}
