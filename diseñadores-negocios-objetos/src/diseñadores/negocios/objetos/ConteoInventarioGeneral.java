package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.ConteoInventarioGeneralDTO;
import diseñadores.persistencia.dao.IConteoInventarioGeneralDAO;
import diseñadores.persistencia.dao.impl.ConteoInventarioGeneralDAOImpl;
import java.util.List;

/**
 * Clase de Objeto de Negocio (BO) encargada de gestionar el flujo unificado
 * de sesiones globales de auditoría de inventario y sus métricas analíticas.
 * 
 * Utiliza única y exclusivamente la abstracción del DAO basada en DTOs y no captura excepciones.
 * 
 * @author ERICK
 */
public class ConteoInventarioGeneral {

    // Se instancia el DAO directo que ya procesa y retorna puros DTOs
    private static final IConteoInventarioGeneralDAO CONTEO_DAO = new ConteoInventarioGeneralDAOImpl();

    /**
     * Recupera el historial completo de sesiones globales de auditoría de la base de datos
     * directamente en formato DTO listo para los paneles de la UI.
     */
    public static List<ConteoInventarioGeneralDTO> obtenerTodos() {
        return CONTEO_DAO.obtenerTodos();
    }

    /**
     * Busca una sesión de auditoría masiva específica utilizando su código unificado.
     */
    public static ConteoInventarioGeneralDTO obtenerPorCodigo(String codigoGeneral) {
        if (codigoGeneral == null || codigoGeneral.trim().isEmpty()) {
            return null;
        }
        return CONTEO_DAO.obtenerPorCodigoGeneral(codigoGeneral);
    }

    /**
     * Manda a registrar una nueva sesión global de auditoría con todos sus sub-conteos e índices.
     */
    public static void guardar(ConteoInventarioGeneralDTO conteoGeneral) {
        if (conteoGeneral == null) return;
        
        // Recalcula métricas antes de persistir para asegurar coherencia en la BD
        conteoGeneral.recalcularMetricas();
        CONTEO_DAO.guardar(conteoGeneral);
    }

    /**
     * Actualiza la información, contadores o estado de verificación de una sesión global existente.
     */
    public static void actualizar(ConteoInventarioGeneralDTO conteoGeneral) {
        if (conteoGeneral == null) return;
        
        // Recalcula métricas antes de persistir para asegurar coherencia en la BD
        conteoGeneral.recalcularMetricas();
        CONTEO_DAO.actualizar(conteoGeneral);
    }

    /**
     * Elimina del historial una sesión completa de auditoría mediante su código único general.
     */
    public static void eliminar(String codigoGeneral) {
        if (codigoGeneral == null || codigoGeneral.trim().isEmpty()) return;
        CONTEO_DAO.eliminar(codigoGeneral);
    }
}