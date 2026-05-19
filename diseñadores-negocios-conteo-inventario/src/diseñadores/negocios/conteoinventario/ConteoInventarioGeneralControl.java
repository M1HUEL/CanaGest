package diseñadores.negocios.conteoinventario;

import diseñadores.negocios.dto.ConteoInventarioGeneralDTO;
import diseñadores.negocios.dto.ItemConteoDTO;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.inventario.InventarioFacade;
import diseñadores.negocios.objetos.ConteoInventarioGeneral;
import java.util.List;
import java.util.UUID;

/**
 * Clase de Control de Negocio encargada de coordinar el flujo operativo, las
 * validaciones y las reglas de negocio de las sesiones de auditoría masiva de inventario.
 * 
 * Sincroniza los cierres globales con el stock actual del catálogo del sistema.
 * Propaga excepciones en tiempo de ejecución sin capturarlas.
 * 
 * @author ERICK
 */
public class ConteoInventarioGeneralControl {

    private final IInventario serviciosInventario;

    /**
     * Constructor por defecto que inicializa la fachada de inventarios.
     */
    public ConteoInventarioGeneralControl() {
        this.serviciosInventario = new InventarioFacade();
    }

    /**
     * Constructor para inyección de dependencias (ideal para pruebas unitarias).
     */
    public ConteoInventarioGeneralControl(IInventario serviciosInventario) {
        this.serviciosInventario = serviciosInventario;
    }
    
    /**
     * Guarda el avance progresivo de la auditoría directamente en MongoDB.
     * Permite guardar comentarios y firmas parciales sin alterar el stock del sistema.
     */
    public void guardarProgresoAuditoria(ConteoInventarioGeneralDTO sesion) {
        validarSesionNoNula(sesion);
        // Guarda el estado actual del JSON (con los ajustes que lleve el usuario)
        ConteoInventarioGeneral.actualizar(sesion);
    }

    /**
     * CIERRE DEFINITIVO: Consolida la sesión, valida que los ítems con discrepancias 
     * tengan su respectiva firma/justificación e impacta permanentemente el stock del sistema.
     */
    public void registrarYAplicarAuditoriaGlobal(ConteoInventarioGeneralDTO sesion) {
        validarSesionNoNula(sesion);
        
        // Ejecuta la validación inteligente enfocada solo en los cambios reales
        validarFirmasEnDiscrepancias(sesion);

        // 1. Forzar bandera de cierre y actualizar estado final en Mongo
        sesion.setVerificadoGlobal(true);
        ConteoInventarioGeneral.actualizar(sesion);

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
            // Si hay un desfase de inventario
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
     * Crea y guarda una nueva sesión global de auditoría en MongoDB como borrador
     * o ticket inicial. No altera el stock base del sistema hasta su verificación.
     */
    public void crearSesionAuditoria(ConteoInventarioGeneralDTO sesion) {
        validarSesionNoNula(sesion);
        
        if (sesion.getTodosLosConteos() == null || sesion.getTodosLosConteos().isEmpty()) {
            throw new IllegalArgumentException("No se puede aperturar una sesión de auditoría sin productos para contar.");
        }

        // Generar folios automáticos si no vienen preestablecidos
        if (sesion.getCodigoGeneral() == null || sesion.getCodigoGeneral().isBlank()) {
            sesion.setCodigoGeneral(generarCodigoGeneral());
        }

        // Propagar el código general a cada sub-conteo y validar consistencia básica
        for (ItemConteoDTO item : sesion.getTodosLosConteos()) {
            if (item.getProductoCodigo() == null || item.getProductoCodigo().isBlank()) {
                throw new IllegalArgumentException("Todos los ítems auditados deben poseer un código de producto válido.");
            }
            if (item.getProductoStockFisico() < 0) {
                throw new IllegalArgumentException("El stock físico del producto " + item.getProductoCodigo() + " no puede ser negativo.");
            }
            item.setCodigoConteo(sesion.getCodigoGeneral());
        }

        ConteoInventarioGeneral.guardar(sesion);
    }

    /**
     * Recupera el historial de todas las auditorías generales realizadas.
     */
    public List<ConteoInventarioGeneralDTO> obtenerHistorialSesiones() {
        return ConteoInventarioGeneral.obtenerTodos();
    }

    /**
     * Busca y extrae una sesión masiva completa filtrada por su identificador/folio general.
     */
    public ConteoInventarioGeneralDTO buscarSesionPorCodigo(String codigoGeneral) {
        if (codigoGeneral == null || codigoGeneral.isBlank()) {
            throw new IllegalArgumentException("El código de búsqueda provisto es inválido.");
        }
        return ConteoInventarioGeneral.obtenerPorCodigo(codigoGeneral);
    }

    // --- Métodos de Validación Privados ---

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