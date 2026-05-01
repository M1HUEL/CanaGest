package diseñadores.persistencia;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.VentaDTO;
import diseñadores.persistencia.dao.IProductoDAO;
import diseñadores.persistencia.dao.IUsuarioDAO;
import diseñadores.persistencia.dao.IVentaDAO;
import diseñadores.persistencia.dao.impl.ProductoDAOImpl;
import diseñadores.persistencia.dao.impl.UsuarioDAOImpl;
import diseñadores.persistencia.dao.impl.VentaDAOImpl;

import java.util.List;
import java.util.Optional;

public class PersistenciaFacade implements IPersistencia {

  private static PersistenciaFacade instancia;

  private final IProductoDAO productoDAO;
  private final IVentaDAO ventaDAO;
  private final IUsuarioDAO usuarioDAO;

  private PersistenciaFacade() {
    this.productoDAO = new ProductoDAOImpl();
    this.ventaDAO = new VentaDAOImpl();
    this.usuarioDAO = new UsuarioDAOImpl();
  }

  public static synchronized PersistenciaFacade getInstancia() {
    if (instancia == null) {
      instancia = new PersistenciaFacade();
    }
    return instancia;
  }

  @Override
  public List<ProductoDTO> obtenerProductos() {
    return productoDAO.obtenerTodos();
  }

  @Override
  public ProductoDTO obtenerProductoPorCodigo(String codigo) {
    return productoDAO.obtenerPorCodigo(codigo);
  }

  @Override
  public void guardarProducto(ProductoDTO producto) {
    productoDAO.guardar(producto);
  }

  @Override
  public void actualizarProducto(ProductoDTO producto) {
    productoDAO.actualizar(producto);
  }

  @Override
  public void eliminarProducto(String codigo) {
    productoDAO.eliminar(codigo);
  }

  @Override
  public List<VentaDTO> obtenerVentas() {
    return ventaDAO.obtenerTodas();
  }

  @Override
  public VentaDTO obtenerVentaPorFolio(String folio) {
    return ventaDAO.obtenerPorFolio(folio);
  }

  @Override
  public void guardarVenta(VentaDTO venta) {
    ventaDAO.guardar(venta);
  }

  @Override
  public void actualizarVenta(VentaDTO venta) {
    ventaDAO.actualizar(venta);
  }

  @Override
  public void eliminarVenta(String folio) {
    ventaDAO.eliminar(folio);
  }

  @Override
  public List<UsuarioDTO> obtenerUsuarios() {
    return usuarioDAO.obtenerTodos();
  }

  @Override
  public Optional<UsuarioDTO> obtenerUsuarioPorNombre(String nombre) {
    UsuarioDTO usuario = usuarioDAO.obtenerPorNombre(nombre);
    return Optional.ofNullable(usuario);
  }

  @Override
  public void guardarUsuario(UsuarioDTO usuario) {
    usuarioDAO.guardar(usuario);
  }

  @Override
  public void actualizarUsuario(UsuarioDTO usuario) {
    usuarioDAO.actualizar(usuario);
  }

  @Override
  public void eliminarUsuario(String nombre) {
    usuarioDAO.eliminar(nombre);
  }

}
