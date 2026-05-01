package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.VentaDTO;
import diseñadores.persistencia.dao.IVentaDAO;
import diseñadores.persistencia.dao.impl.VentaDAOImpl;
import java.util.List;

public class Venta {

  private static final IVentaDAO DAO = new VentaDAOImpl();

  public static List<VentaDTO> obtenerTodas() {
    return DAO.obtenerTodas();
  }

  public static VentaDTO obtenerPorFolio(String folio) {
    return DAO.obtenerPorFolio(folio);
  }

  public static void guardar(VentaDTO venta) {
    DAO.guardar(venta);
  }

  public static void actualizar(VentaDTO venta) {
    DAO.actualizar(venta);
  }

  public static void eliminar(String folio) {
    DAO.eliminar(folio);
  }

}
