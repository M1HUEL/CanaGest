package diseñadores.negocios.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que replica con exactitud la estructura de la entidad de dominio ConteoInventarioGeneral.
 * Utilizado para la transferencia de datos de las sesiones globales de auditoría entre capas.
 * 
 * @author ERICK
 */
public class ConteoInventarioGeneralDTO {

    private String id; 
    private String codigoGeneral;
    private String fechaRegistro;
    private Boolean verificadoGlobal;

    private int cantidadVerificados;
    private int cantidadNoVerificados;
    private int diferenciasTotales;

    // Lista de ítems que representan los sub-conteos individuales
    private List<ItemConteoDTO> todosLosConteos;

    /**
     * Constructor por defecto. Inicializa la lista interna para mitigar errores de puntero nulo.
     */
    public ConteoInventarioGeneralDTO() {
        this.todosLosConteos = new ArrayList<>();
        this.verificadoGlobal = false; // Por defecto inicia sin verificar/aplicar definitivamente
    }

    /**
     * Constructor completo con todos los parámetros idénticos a la entidad de dominio.
     */
    public ConteoInventarioGeneralDTO(String id, String codigoGeneral, String fechaRegistro, Boolean verificadoGlobal, 
                                      int cantidadVerificados, int cantidadNoVerificados, int diferenciasTotales, 
                                      List<ItemConteoDTO> todosLosConteos) {
        this.id = id;
        this.codigoGeneral = codigoGeneral;
        this.fechaRegistro = fechaRegistro;
        this.verificadoGlobal = verificadoGlobal != null ? verificadoGlobal : false;
        this.cantidadVerificados = cantidadVerificados;
        this.cantidadNoVerificados = cantidadNoVerificados;
        this.diferenciasTotales = diferenciasTotales;
        this.todosLosConteos = todosLosConteos != null ? todosLosConteos : new ArrayList<>();
    }

    /**
     * UTILERÍA DE NEGOCIO: Recalcula de forma automática los contadores y 
     * estadísticas del conteo masivo basándose en el estado de la lista actual.
     */
    public void recalcularMetricas() {
        if (this.todosLosConteos == null) return;
        
        int verificados = 0;
        int noVerificados = 0;
        int descuadres = 0;

        for (ItemConteoDTO item : todosLosConteos) {
            
            if (item.getDiferencia() != 0) {
                descuadres++;
            }
            
            if (item.isVerificado()) {
                verificados++;
            } else {
                noVerificados++;
            }
        }

        this.cantidadVerificados = verificados;
        this.cantidadNoVerificados = noVerificados;
        this.diferenciasTotales = descuadres;
    }
    
    public String getId() { 
        return id; 
    }
    
    public void setId(String id) { 
        this.id = id; 
    }

    public String getCodigoGeneral() { 
        return codigoGeneral; 
    }
    
    public void setCodigoGeneral(String codigoGeneral) { 
        this.codigoGeneral = codigoGeneral; 
    }

    public String getFechaRegistro() { 
        return fechaRegistro; 
    }
    
    public void setFechaRegistro(String fechaRegistro) { 
        this.fechaRegistro = fechaRegistro; 
    }

    public Boolean getVerificadoGlobal() { 
        return verificadoGlobal; 
    }
    
    public void setVerificadoGlobal(Boolean verificadoGlobal) { 
        this.verificadoGlobal = verificadoGlobal; 
    }

    public int getCantidadVerificados() { 
        return cantidadVerificados; 
    }
    public void setCantidadVerificados(int cantidadVerificados) { 
        this.cantidadVerificados = cantidadVerificados; 
    }

    public int getCantidadNoVerificados() { 
        return cantidadNoVerificados; 
    }
    
    public void setCantidadNoVerificados(int cantidadNoVerificados) { 
        this.cantidadNoVerificados = cantidadNoVerificados; 
    }

    public int getDiferenciasTotales() { 
        return diferenciasTotales; 
    }
    
    public void setDiferenciasTotales(int diferenciasTotales) { 
        this.diferenciasTotales = diferenciasTotales; 
    }

    public List<ItemConteoDTO> getTodosLosConteos() { 
        return todosLosConteos; 
    }
    
    public void setTodosLosConteos(List<ItemConteoDTO> todosLosConteos) {
        this.todosLosConteos = todosLosConteos != null ? todosLosConteos : new ArrayList<>();
        recalcularMetricas();
    }

    @Override
    public String toString() {
        return "ConteoInventarioGeneralDTO{" + "id=" + id + ", codigoGeneral=" + codigoGeneral + ", verificadoGlobal=" + verificadoGlobal + ", cantidadVerificados=" + cantidadVerificados + ", cantidadNoVerificados=" + cantidadNoVerificados + ", diferenciasTotales=" + diferenciasTotales + '}';
    }
}