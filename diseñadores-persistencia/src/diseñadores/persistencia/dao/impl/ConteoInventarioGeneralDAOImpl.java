package diseñadores.persistencia.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import diseñadores.negocios.dto.ConteoInventarioGeneralDTO;
import diseñadores.persistencia.conexion.Conexion;
import diseñadores.persistencia.dao.IConteoInventarioGeneralDAO;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación para la persistencia de Conteos Generales de Inventario utilizando Documentos de MongoDB.
 * Utiliza directamente ConteoInventarioGeneralDTO y propaga las excepciones sin capturarlas.
 * 
 * @author ERICK
 */
public class ConteoInventarioGeneralDAOImpl implements IConteoInventarioGeneralDAO {

    private static final String COLECCION = "conteos_inventario_general";
    private final MongoCollection<Document> coleccion;

    /**
     * Constructor que inicializa la conexión con la colección mapeada a través de Documentos genéricos.
     */
    public ConteoInventarioGeneralDAOImpl() {
        this.coleccion = Conexion.getInstancia()
                .getDatabase()
                .getCollection(COLECCION);
    }

    @Override
    public List<ConteoInventarioGeneralDTO> obtenerTodos() {
        List<ConteoInventarioGeneralDTO> lista = new ArrayList<>();
        for (Document doc : coleccion.find()) {
            lista.add(convertirADTO(doc));
        }
        return lista;
    }

    @Override
    public ConteoInventarioGeneralDTO obtenerPorCodigoGeneral(String codigoGeneral) {
        validarCodigoRequerido(codigoGeneral);
        Document doc = buscarDocumentoPorCodigoGeneral(codigoGeneral);
        return (doc != null) ? convertirADTO(doc) : null;
    }

    @Override
    public void guardar(ConteoInventarioGeneralDTO conteoGeneral) {
        validarDatosConteoGeneral(conteoGeneral);
        validarCodigoDisponible(conteoGeneral.getCodigoGeneral());
        ejecutarInsercion(conteoGeneral);
    }

    @Override
    public void actualizar(ConteoInventarioGeneralDTO conteoGeneral) {
        validarDatosConteoGeneral(conteoGeneral);
        validarConteoGeneralExiste(conteoGeneral.getCodigoGeneral());
        ejecutarReemplazo(conteoGeneral);
    }

    @Override
    public void eliminar(String codigoGeneral) {
        validarCodigoRequerido(codigoGeneral);
        validarConteoGeneralExiste(codigoGeneral);
        ejecutarEliminacion(codigoGeneral);
    }

    // ==========================================
    // METODOS DE VALIDACIÓN
    // ==========================================

    private void validarCodigoRequerido(String codigoGeneral) {
        if (codigoGeneral == null || codigoGeneral.isBlank()) {
            throw new IllegalArgumentException("El código del conteo general es un dato obligatorio requerido.");
        }
    }

    private void validarDatosConteoGeneral(ConteoInventarioGeneralDTO conteoGeneral) {
        if (conteoGeneral == null) {
            throw new IllegalArgumentException("El objeto de registro general no puede ser nulo.");
        }
        validarCodigoRequerido(conteoGeneral.getCodigoGeneral());
        if (conteoGeneral.getFechaRegistro() == null) {
            throw new IllegalArgumentException("La fecha de captura de la auditoría general es obligatoria.");
        }
        if (conteoGeneral.getTodosLosConteos() == null) {
            throw new IllegalArgumentException("La lista interna de sub-conteos individuales no puede estar vacía o nula.");
        }
    }

    private void validarCodigoDisponible(String codigoGeneral) {
        if (buscarDocumentoPorCodigoGeneral(codigoGeneral) != null) {
            throw new IllegalStateException("El código de auditoría general '" + codigoGeneral + "' ya se encuentra registrado en el sistema.");
        }
    }

    private void validarConteoGeneralExiste(String codigoGeneral) {
        if (buscarDocumentoPorCodigoGeneral(codigoGeneral) == null) {
            throw new IllegalStateException("El registro de auditoría general con código '" + codigoGeneral + "' no existe en el sistema.");
        }
    }

    // ==========================================
    // METODOS DE OPERACIÓN DIRECTA MONGO
    // ==========================================

    private Document buscarDocumentoPorCodigoGeneral(String codigoGeneral) {
        return coleccion.find(Filters.eq("codigoGeneral", codigoGeneral.trim())).first();
    }

    private void ejecutarInsercion(ConteoInventarioGeneralDTO conteoGeneral) {
        coleccion.insertOne(convertirADocumento(conteoGeneral));
    }

    private void ejecutarReemplazo(ConteoInventarioGeneralDTO conteoGeneral) {
        coleccion.replaceOne(
                Filters.eq("codigoGeneral", conteoGeneral.getCodigoGeneral()),
                convertirADocumento(conteoGeneral),
                new ReplaceOptions().upsert(true)
        );
    }

    private void ejecutarEliminacion(String codigoGeneral) {
        coleccion.deleteOne(Filters.eq("codigoGeneral", codigoGeneral));
    }

    // ==========================================
    // MÉTODOS DE CONVERSIÓN (DTO <-> DOCUMENT)
    // ==========================================

    private ConteoInventarioGeneralDTO convertirADTO(Document doc) {
        ConteoInventarioGeneralDTO dto = new ConteoInventarioGeneralDTO();
        dto.setCodigoGeneral(doc.getString("codigoGeneral"));
        dto.setFechaRegistro(doc.getDate("fechaRegistro"));
        
        if (doc.get("todosLosConteos") != null) {
            dto.setTodosLosConteos(doc.getList("todosLosConteos", Object.class)); 
        }
        
        return dto;
    }

    private Document convertirADocumento(ConteoInventarioGeneralDTO dto) {
        Document doc = new Document()
                .append("codigoGeneral", dto.getCodigoGeneral())
                .append("fechaRegistro", dto.getFechaRegistro());

        if (dto.getTodosLosConteos() != null) {
            doc.append("todosLosConteos", dto.getTodosLosConteos());
        }

        return doc;
    }
}