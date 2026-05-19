package diseñadores.negocios.conteoinventario;

import diseñadores.negocios.dto.ConteoInventarioGeneralDTO;
import java.util.List;

/**
 * Fachada del subsistema de Conteo de Inventario General.
 * Centraliza y simplifica el acceso a los métodos de control y reglas de negocio,
 * aislando la complejidad del flujo masivo de auditorías para la capa de presentación.
 * 
 * PROPAGA las excepciones en tiempo de ejecución sin capturarlas ni declararlas.
 * 
 * @author ERICK
 */
public class ConteoInventarioGeneralFacade implements IConteoInventarioGeneral {

    private final ConteoInventarioGeneralControl control;

    /**
     * Constructor por defecto que inicializa el controlador correspondiente.
     */
    public ConteoInventarioGeneralFacade() {
        this.control = new ConteoInventarioGeneralControl();
    }

    /**
     * Constructor para inyección de dependencias (útil para pruebas de integración).
     */
    public ConteoInventarioGeneralFacade(ConteoInventarioGeneralControl control) {
        this.control = control;
    }

    @Override
    public void crearSesionAuditoria(ConteoInventarioGeneralDTO sesion) {
        control.crearSesionAuditoria(sesion);
    }

    @Override
    public void registrarYAplicarAuditoriaGlobal(ConteoInventarioGeneralDTO sesion) {
        control.registrarYAplicarAuditoriaGlobal(sesion);
    }

    @Override
    public List<ConteoInventarioGeneralDTO> obtenerHistorialSesiones() {
        return control.obtenerHistorialSesiones();
    }

    @Override
    public ConteoInventarioGeneralDTO buscarSesionPorCodigo(String codigoGeneral) {
        return control.buscarSesionPorCodigo(codigoGeneral);
    }
    
    @Override
    public void guardarProgresoAuditoria(ConteoInventarioGeneralDTO sesion) {
        control.guardarProgresoAuditoria(sesion);
    }
}