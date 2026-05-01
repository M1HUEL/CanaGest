package diseñadores.persistencia.dao;

import diseñadores.negocios.dto.ProveedorDTO;
import java.util.List;

public interface IProveedorDAO {

  List<ProveedorDTO> obtenerTodos();

  ProveedorDTO obtenerPorCodigo(String codigo);

  void guardar(ProveedorDTO proveedor);

  void actualizar(ProveedorDTO proveedor);

  void eliminar(String codigo);

}
