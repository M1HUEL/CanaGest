package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.ConteoInventarioDTO;
import diseñadores.persistencia.dao.impl.ConteoInventarioDAOImpl;
import java.util.List;
import diseñadores.persistencia.dao.IConteoInventarioDAO;

/**
 * Clase de Objeto de Negocio (BO) encargada de gestionar el flujo unificado de
 * sesiones globales de auditoría de inventario y sus métricas analíticas.
 *
 * Utiliza única y exclusivamente la abstracción del DAO basada en DTOs y no
 * captura excepciones.
 *
 * @author Erick
 */
public class ConteoInventario {

  private static final IConteoInventarioDAO PERSISTENCIA = new ConteoInventarioDAOImpl();

  public static List<ConteoInventarioDTO> obtenerTodos() {
    return PERSISTENCIA.obtenerTodos();
  }

  public static ConteoInventarioDTO obtenerPorCodigo(String codigoGeneral) {
    if (codigoGeneral == null || codigoGeneral.trim().isEmpty()) {
      return null;
    }
    return PERSISTENCIA.obtenerPorCodigoGeneral(codigoGeneral);
  }

  public static void guardar(ConteoInventarioDTO conteoGeneral) {
    if (conteoGeneral == null) {
      return;
    }

    conteoGeneral.recalcularMetricas();
    PERSISTENCIA.guardar(conteoGeneral);
  }

  public static void actualizar(ConteoInventarioDTO conteoGeneral) {
    if (conteoGeneral == null) {
      return;
    }

    conteoGeneral.recalcularMetricas();
    PERSISTENCIA.actualizar(conteoGeneral);
  }

  public static void eliminar(String codigoGeneral) {
    if (codigoGeneral == null || codigoGeneral.trim().isEmpty()) {
      return;
    }
    PERSISTENCIA.eliminar(codigoGeneral);
  }
}
