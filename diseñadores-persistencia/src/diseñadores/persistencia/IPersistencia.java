package diseñadores.persistencia;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.VentaDTO;

import java.util.List;
import java.util.Optional;

public interface IPersistencia {

  List<ProductoDTO> obtenerProductos();

  ProductoDTO obtenerProductoPorCodigo(String codigo);

  void guardarProducto(ProductoDTO producto);

  void actualizarProducto(ProductoDTO producto);

  void eliminarProducto(String codigo);

  List<VentaDTO> obtenerVentas();

  VentaDTO obtenerVentaPorFolio(String folio);

  void guardarVenta(VentaDTO venta);

  void actualizarVenta(VentaDTO venta);

  void eliminarVenta(String folio);

  List<UsuarioDTO> obtenerUsuarios();

  Optional<UsuarioDTO> obtenerUsuarioPorNombre(String nombre);

  void guardarUsuario(UsuarioDTO usuario);

  void actualizarUsuario(UsuarioDTO usuario);

  void eliminarUsuario(String nombre);

}
