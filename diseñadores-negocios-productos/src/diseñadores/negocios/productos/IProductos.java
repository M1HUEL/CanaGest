package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import java.util.List;

public interface IProductos {

  List<ProductoDTO> obtenerCatalogo();

  ProductoDTO buscarProductoPorCodigo(EscanearProductoDTO dto);

  boolean existeProducto(EscanearProductoDTO dto);

  boolean tieneStock(EscanearProductoDTO dto);

}
