package diseñadores.persistencia.dao;

import diseñadores.negocios.dto.OrdenCompraDTO;
import java.util.List;

public interface IOrdenCompraDAO {

  List<OrdenCompraDTO> obtenerTodas();

  OrdenCompraDTO obtenerPorNumero(String numero);

  void guardar(OrdenCompraDTO orden);

  void actualizar(OrdenCompraDTO orden);

  void eliminar(String numero);

}
