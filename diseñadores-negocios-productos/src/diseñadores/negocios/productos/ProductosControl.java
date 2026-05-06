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
    validarDtoEscaneo(dto);

    ProductoDTO p = obtenerProductoBase(dto.getCodigo());

    validarProductoExistente(p);

    sincronizarStockDesdeInventario(p, dto.getCodigo());

    return p;
  }

  public boolean existeProducto(EscanearProductoDTO dto) {
    if (esDtoInvalido(dto)) {
      return false;
    }
    return obtenerProductoBase(dto.getCodigo()) != null;
  }

  public boolean tieneStock(EscanearProductoDTO dto, int cantidad) {
    if (esDtoInvalido(dto)) {
      return false;
    }
    return verificarStockServicio(dto.getCodigo(), cantidad);
  }

  public void descontarStock(String codigo, int cantidad) {
    serviciosInventario.descontarStock(codigo, cantidad);
  }

  public void guardarProducto(ProductoDTO producto) {
    validarDatosProducto(producto);
    registrarNuevoProducto(producto);
  }

  public void actualizarProducto(ProductoDTO producto) {
    validarDatosProducto(producto);
    ejecutarActualizacion(producto);
  }

  public void eliminarProducto(String codigo) {
    validarCodigoRequerido(codigo);
    ejecutarEliminacion(codigo);
  }

  private void validarDtoEscaneo(EscanearProductoDTO dto) {
    if (dto == null || dto.getCodigo() == null || dto.getCodigo().isBlank()) {
      throw new IllegalArgumentException("Código inválido");
    }
  }

  private boolean esDtoInvalido(EscanearProductoDTO dto) {
    return dto == null || dto.getCodigo() == null;
  }

  private void validarProductoExistente(ProductoDTO p) {
    if (p == null) {
      throw new IllegalStateException("Producto no existe");
    }
  }

  private void validarDatosProducto(ProductoDTO producto) {
    if (producto == null || producto.getCodigo() == null) {
      throw new IllegalArgumentException("Datos insuficientes");
    }
  }

  private void validarCodigoRequerido(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("Código requerido");
    }
  }

  private ProductoDTO obtenerProductoBase(String codigo) {
    return Producto.obtenerPorCodigo(codigo);
  }

  private void sincronizarStockDesdeInventario(ProductoDTO p, String codigo) {
    ProductoDTO infoInventario = serviciosInventario.obtenerProductoPorCodigo(codigo);

    actualizarStockSiExiste(p, infoInventario);
  }

  private boolean verificarStockServicio(String codigo, int cantidad) {
    return serviciosInventario.verificarStock(codigo, cantidad);
  }

  private void registrarNuevoProducto(ProductoDTO producto) {
    Producto.guardar(producto);
  }

  private void ejecutarActualizacion(ProductoDTO producto) {
    Producto.actualizar(producto);
  }

  private void ejecutarEliminacion(String codigo) {
    Producto.eliminar(codigo);
  }

  private void actualizarStockSiExiste(ProductoDTO p, ProductoDTO infoInventario) {
    if (infoInventario != null) {
      p.setStock(infoInventario.getStock());
    }
  }

}
