package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;

import java.util.List;

public interface IProveedores {

  List<ProveedorDTO> obtenerProveedores();

  ProveedorDTO obtenerProveedorPorCodigo(String codigo);

  void guardarProveedor(ProveedorDTO proveedor);

  void actualizarProveedor(ProveedorDTO proveedor);

  int contarProveedoresActivos();

  List<OrdenCompraDTO> obtenerOrdenesCompra();

  void guardarOrdenCompra(OrdenCompraDTO orden);

  void actualizarOrdenCompra(OrdenCompraDTO orden);

  void cambiarEstadoOrden(String numero, String nuevoEstado);

}
