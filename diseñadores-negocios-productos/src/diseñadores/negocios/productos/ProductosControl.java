package diseñadores.negocios.productos;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.objetos.Producto;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.inventario.InventarioFacade;
import java.util.List;

public class ProductosControl {

  private final IInventario serviciosInventario;

  public ProductosControl() {
    this.serviciosInventario = new InventarioFacade();
  }

  public ProductosControl(IInventario serviciosInventario) {
    this.serviciosInventario = serviciosInventario;
  }

  public List<ProductoDTO> obtenerCatalogo() {
    return Producto.obtenerTodos();
  }

  public ProductoDTO buscarProductoPorCodigo(EscanearProductoDTO dto) {
    if (dto == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
      throw new IllegalArgumentException("Código inválido");
    }
    ProductoDTO p = Producto.obtenerPorCodigo(dto.getCodigo());
    if (p == null) {
      throw new IllegalStateException("Producto no existe");
    }

    ProductoDTO infoInventario = serviciosInventario.obtenerProductoPorCodigo(dto.getCodigo());
    if (infoInventario != null) {
      p.setStock(infoInventario.getStock());
    }
    return p;
  }

  public boolean validarExistenciaProducto(EscanearProductoDTO dto) {
    if (dto == null || dto.getCodigo() == null) {
      return false;
    }
    return Producto.obtenerPorCodigo(dto.getCodigo()) != null;
  }

  public boolean tieneStock(EscanearProductoDTO dto, int cantidad) {
    if (dto == null || dto.getCodigo() == null) {
      return false;
    }
    return serviciosInventario.verificarStock(dto.getCodigo(), cantidad);
  }

  public void descontarStock(String codigo, int cantidad) {
    serviciosInventario.descontarStock(codigo, cantidad);
  }

  public void guardarProducto(ProductoDTO producto) {
    if (producto == null || producto.getCodigo() == null) {
      throw new IllegalArgumentException("Datos insuficientes");
    }
    Producto.guardar(producto);
  }

  public void actualizarProducto(ProductoDTO producto) {
    if (producto == null || producto.getCodigo() == null) {
      throw new IllegalArgumentException("Datos insuficientes");
    }
    Producto.actualizar(producto);
  }

  public void eliminarProducto(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("Código requerido");
    }
    Producto.eliminar(codigo);
  }

}
