package diseñadores.negocios.conteoinventario;

import diseñadores.negocios.dto.ConteoInventarioGeneralDTO;
import diseñadores.negocios.dto.ItemConteoDTO;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.inventario.InventarioFacade;
import diseñadores.persistencia.dao.IConteoInventarioGeneralDAO;
import diseñadores.persistencia.dao.impl.ConteoInventarioGeneralDAOImpl;
import java.util.List;
import java.util.UUID;

/**
 * Clase de Control de Negocio encargada de coordinar el flujo operativo, las
 * validaciones y las reglas de negocio de las sesiones de auditoría masiva de inventario.
 * 
 * Sincroniza los cierres globales con el stock actual del catálogo del sistema.
 * Utiliza única y exclusivamente la abstracción del DAO basada en DTOs.
 * 
 * @author ERICK
 */
public class ConteoInventarioGeneralControl {

    private final IInventario serviciosInventario;
    private final IConteoInventarioGeneralDAO conteoDAO;

    /**
     * Constructor por defecto que inicializa la fachada de inventarios y el DAO basado en DTOs.
     */
    public ConteoInventarioGeneralControl() {
        this.serviciosInventario = new InventarioFacade();
        this.conteoDAO = new ConteoInventarioGeneralDAOImpl();
    }

    /**
     * Constructor para inyección de dependencias (ideal para pruebas unitarias).
     */
    public ConteoInventarioGeneralControl(IInventario serviciosInventario, IConteoInventarioGeneralDAO conteoDAO) {
        this.serviciosInventario = serviciosInventario;
        this.conteoDAO = conteoDAO;
    }
    
    /**
     * Guarda el avance progresivo de la auditoría directamente en MongoDB.
     */
    public void guardarProgresoAuditoria(ConteoInventarioGeneralDTO sesion) {
        validarSesionNoNula(sesion);
        conteoDAO.actualizar(sesion);
    }

    /**
     * CIERRE DEFINITIVO: Consolida la sesión, valida discrepancias e impacta el stock del sistema.
     */
    public void registrarYAplicarAuditoriaGlobal(ConteoInventarioGeneralDTO sesion) {
        validarSesionNoNula(sesion);
        validarFirmasEnDiscrepancias(sesion);

        // 1. Forzar bandera de cierre y actualizar estado final en Mongo
        sesion.setVerificadoGlobal(true);
        conteoDAO.actualizar(sesion);

        // 2. Sincronizar el stock real del sistema
        for (ItemConteoDTO item : sesion.getTodosLosConteos()) {
            serviciosInventario.actualizarStock(item.getProductoCodigo(), item.getProductoStockFisico());
        }
    }

    /**
     * Validación Inteligente: Si el stock físico difiere del sistema, 
     * exige obligatoriamente la firma del auditor y su justificación.
     */
    private void validarFirmasEnDiscrepancias(ConteoInventarioGeneralDTO sesion) {
        if (sesion.getTodosLosConteos() == null || sesion.getTodosLosConteos().isEmpty()) {
            throw new IllegalArgumentException("La sesión no registra ningún conteo de inventario.");
        }
        
        for (ItemConteoDTO item : sesion.getTodosLosConteos()) {
            if (item.getProductoStockSistema() != item.getProductoStockFisico()) {
                if (item.getNombreUsuario() == null || item.getNombreUsuario().isBlank()) {
                    throw new IllegalArgumentException("El producto " + item.getProductoCodigo() 
                        + " presenta discrepancias y requiere la firma del auditor para el cierre.");
                }
                if (item.getComentario() == null || item.getComentario().isBlank()) {
                    throw new IllegalArgumentException("Debe indicar el motivo (comentario) del desfase en el producto: " 
                        + item.getProductoCodigo());
                }
            }
        }
    }

    /**
     * Crea y guarda una nueva sesión global de auditoría en MongoDB como borrador.
     */
    public void crearSesionAuditoria(ConteoInventarioGeneralDTO sesion) {
        validarSesionNoNula(sesion);
        
        if (sesion.getTodosLosConteos() == null || sesion.getTodosLosConteos().isEmpty()) {
            throw new IllegalArgumentException("No se puede aperturar una sesión de auditoría sin productos para contar.");
        }

        if (sesion.getCodigoGeneral() == null || sesion.getCodigoGeneral().isBlank()) {
            sesion.setCodigoGeneral(generarCodigoGeneral());
        }

        for (ItemConteoDTO item : sesion.getTodosLosConteos()) {
            if (item.getProductoCodigo() == null || item.getProductoCodigo().isBlank()) {
                throw new IllegalArgumentException("Todos los ítems auditados deben poseer un código de producto válido.");
            }
            if (item.getProductoStockFisico() < 0) {
                throw new IllegalArgumentException("El stock físico del producto " + item.getProductoCodigo() + " no puede ser negativo.");
            }
            item.setCodigoConteo(sesion.getCodigoGeneral());
        }

        conteoDAO.guardar(sesion);
    }

    /**
     * Recupera el historial de todas las auditorías generales en formato DTO.
     */
    public List<ConteoInventarioGeneralDTO> obtenerHistorialSesiones() {
        return conteoDAO.obtenerTodos();
    }

    /**
     * Busca una sesión masiva completa filtrada por su identificador general.
     */
    public ConteoInventarioGeneralDTO buscarSesionPorCodigo(String codigoGeneral) {
        if (codigoGeneral == null || codigoGeneral.isBlank()) {
            throw new IllegalArgumentException("El código de búsqueda provisto es inválido.");
        }
        return conteoDAO.obtenerPorCodigoGeneral(codigoGeneral);
    }

    private void validarSesionNoNula(ConteoInventarioGeneralDTO sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("El contenedor general de auditoría no puede ser nulo.");
        }
    }

    private void validarDatosObligatorios(ConteoInventarioGeneralDTO sesion) {
        if (sesion.getTodosLosConteos() == null || sesion.getTodosLosConteos().isEmpty()) {
            throw new IllegalArgumentException("La sesión no registra ningún conteo de inventario adjunto.");
        }
        
        for (ItemConteoDTO item : sesion.getTodosLosConteos()) {
            if (item.getNombreUsuario() == null || item.getNombreUsuario().isBlank()) {
                throw new IllegalArgumentException("La firma del auditor es requerida en cada ítem de la sesión.");
            }
            if (item.getProductoCodigo() == null || item.getProductoCodigo().isBlank()) {
                throw new IllegalArgumentException("Código de producto faltante en una de las líneas de la sesión.");
            }
            if (item.getProductoStockFisico() < 0) {
                throw new IllegalArgumentException("Existen lecturas con stock físico en valores negativos.");
            }
        }
    }

    private String generarCodigoGeneral() {
        return "AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}