package diseñadores.persistencia.dao;

import diseñadores.negocios.dto.ConteoInventarioDTO;
import java.util.List;

/**
 * Interfaz que define las operaciones de persistencia para el control
 * unificado de Conteos Generales de Inventario utilizando DTOs.
 * 
 * @author ERICK
 */
public interface IConteoInventarioDAO {

    /**
     * Recupera el historial completo de todas las sesiones de auditoría global.
     * 
     * @return Una lista con todos los conteos generales en formato DTO.
     */
    List<ConteoInventarioDTO> obtenerTodos();

    /**
     * Busca una sesión de auditoría global específica mediante su código comercial.
     * 
     * @param codigoGeneral Código único de la sesión (ej. "AUD-2026-05-18").
     * @return El objeto DTO correspondiente, o null si no se encuentra.
     */
    ConteoInventarioDTO obtenerPorCodigoGeneral(String codigoGeneral);

    /**
     * Registra un nuevo documento maestro de inventario unificado en la base de datos.
     * 
     * @param conteoGeneral Objeto DTO con los datos y submódulos a persistir.
     */
    void guardar(ConteoInventarioDTO conteoGeneral);

    /**
     * Actualiza el documento maestro completo en MongoDB (reemplazo atómico).
     * Sobreescribe los totales calculados y el arreglo embebido de sub-conteos.
     * 
     * @param conteoGeneral Objeto DTO actualizado.
     */
    void actualizar(ConteoInventarioDTO conteoGeneral);

    /**
     * Elimina de manera permanente una auditoría global y todos sus conteos asociados.
     * 
     * @param codigoGeneral Código único de la sesión a dar de baja.
     */
    void eliminar(String codigoGeneral);
}