package diseñadores.persistencia.dao;

import diseñadores.negocios.dto.UsuarioDTO;
import java.util.List;

public interface IUsuarioDAO {

  List<UsuarioDTO> obtenerTodos();

  UsuarioDTO obtenerPorNombre(String nombre);

  void guardar(UsuarioDTO usuario);

  void actualizar(UsuarioDTO usuario);

  void eliminar(String nombre);

}
