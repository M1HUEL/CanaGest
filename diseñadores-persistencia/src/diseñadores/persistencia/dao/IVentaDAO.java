package diseñadores.persistencia.dao;

import diseñadores.negocios.dto.VentaDTO;
import java.util.List;

public interface IVentaDAO {

  List<VentaDTO> obtenerTodas();

  VentaDTO obtenerPorFolio(String folio);

  void guardar(VentaDTO venta);

  void actualizar(VentaDTO venta);

  void eliminar(String folio);

}
