package diseñadores.negocios.conteoinventario;

import diseñadores.negocios.dto.ConteoInventarioDTO;
import java.util.List;

/**
 * Interfaz que define las operaciones permitidas para el subsistema de 
 * auditorías globales y control unificado de conteos de inventario.
 * 
 * @author ERICK
 */
public interface IConteoInventario {

    /**
     * Crea y pre-registra una sesión global de auditoría en estado de borrador.
     */
    void crearSesionAuditoria(ConteoInventarioDTO sesion);

    /**
     * Consolida la sesión de auditoría en la base de datos e impacta los
     * stocks físicos directamente en el catálogo del sistema.
     */
    void registrarYAplicarAuditoriaGlobal(ConteoInventarioDTO sesion);

    /**
     * Recupera el historial completo de las sesiones masivas de inventario.
     */
    List<ConteoInventarioDTO> obtenerHistorialSesiones();

    /**
     * Busca los detalles de una sesión de auditoría unificada mediante su código.
     */
    ConteoInventarioDTO buscarSesionPorCodigo(String codigoGeneral);
    
    /**
     * Guarda modificaciones progresivas sobre los ítems (comentarios y firmas) 
     * sin cerrar la auditoría ni alterar los stocks del catálogo.
     */
    void guardarProgresoAuditoria(ConteoInventarioDTO sesion);
}