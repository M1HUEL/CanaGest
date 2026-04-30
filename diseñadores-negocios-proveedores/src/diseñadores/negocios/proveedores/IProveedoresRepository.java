package diseñadores.negocios.proveedores;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import java.util.List;

public interface IProveedoresRepository {

  List<ProveedorDTO> getProveedores();

  void agregarProveedor(ProveedorDTO proveedor);

  void actualizarProveedor(ProveedorDTO proveedor);

  List<OrdenCompraDTO> getOrdenesCompra();

  void agregarOrdenCompra(OrdenCompraDTO orden);

}
