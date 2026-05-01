package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.objetos.Producto;

import java.util.List;

public class ProductosControl {

  public List<ProductoDTO> obtenerCatalogo() {
    return Producto.obtenerTodos();
  }

  public ProductoDTO buscarProductoPorCodigo(EscanearProductoDTO dto) {
    if (dto == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
      throw new IllegalArgumentException("El código del producto no puede estar vacío.");
    }

    ProductoDTO p = Producto.obtenerPorCodigo(dto.getCodigo());
    if (p == null) {
      throw new IllegalStateException("No existe un producto con el código: " + dto.getCodigo());
    }

    return p;
  }

  public boolean existeProducto(EscanearProductoDTO dto) {
    if (dto == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
      return false;
    }

    return Producto.obtenerPorCodigo(dto.getCodigo()) != null;
  }

  public boolean tieneStock(EscanearProductoDTO dto) {
    if (dto == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
      return false;
    }

    ProductoDTO p = Producto.obtenerPorCodigo(dto.getCodigo());
    return p != null && p.getStock() >= 1;
  }

  public void guardarProducto(ProductoDTO producto) {
    if (producto == null) {
      throw new IllegalArgumentException("El producto no puede ser nulo.");
    }
    if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
      throw new IllegalArgumentException("El código del producto es obligatorio.");
    }
    if (producto.getNombre() == null || producto.getNombre().isBlank()) {
      throw new IllegalArgumentException("El nombre del producto es obligatorio.");
    }
    if (producto.getPrecio() == null || producto.getPrecio().doubleValue() <= 0) {
      throw new IllegalArgumentException("El precio del producto debe ser mayor a cero.");
    }
    if (Producto.obtenerPorCodigo(producto.getCodigo()) != null) {
      throw new IllegalStateException("Ya existe un producto con el código: " + producto.getCodigo());
    }

    Producto.guardar(producto);
  }

  public void actualizarProducto(ProductoDTO producto) {
    if (producto == null) {
      throw new IllegalArgumentException("El producto no puede ser nulo.");
    }
    if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
      throw new IllegalArgumentException("El código del producto es obligatorio.");
    }
    if (producto.getPrecio() == null || producto.getPrecio().doubleValue() <= 0) {
      throw new IllegalArgumentException("El precio del producto debe ser mayor a cero.");
    }
    if (Producto.obtenerPorCodigo(producto.getCodigo()) == null) {
      throw new IllegalStateException("No existe un producto con el código: " + producto.getCodigo());
    }

    Producto.actualizar(producto);
  }

  public void eliminarProducto(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("El código del producto no puede estar vacío.");
    }
    if (Producto.obtenerPorCodigo(codigo) == null) {
      throw new IllegalStateException("No existe un producto con el código: " + codigo);
    }

    Producto.eliminar(codigo);
  }

}
