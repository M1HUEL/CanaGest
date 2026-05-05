package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import java.util.List;

public interface IProductos {

  List<ProductoDTO> obtenerCatalogo();

  ProductoDTO buscarProductoPorCodigo(EscanearProductoDTO dto);

  boolean validarExistenciaProducto(EscanearProductoDTO dto);

  boolean tieneStock(EscanearProductoDTO dto, int cantidad);

  void descontarStock(String codigo, int cantidad);

  void guardarProducto(ProductoDTO producto);

  void actualizarProducto(ProductoDTO producto);

  void eliminarProducto(String codigo);

}
