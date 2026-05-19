package diseñadores.persistencia.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import diseñadores.negocios.dto.ConteoInventarioGeneralDTO;
import diseñadores.negocios.dto.ItemConteoDTO;
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

    private ConteoInventarioGeneralDTO convertirADTO(Document doc) {
        ConteoInventarioGeneralDTO dto = new ConteoInventarioGeneralDTO();

        // Mapeo de la raíz del Documento Maestro
        dto.setId(doc.getObjectId("_id") != null ? doc.getObjectId("_id").toHexString() : null);
        dto.setCodigoGeneral(doc.getString("codigoGeneral"));
        dto.setFechaRegistro(doc.getString("fechaRegistro")); 
        dto.setVerificadoGlobal(doc.getBoolean("verificadoGlobal", false));
        dto.setCantidadVerificados(doc.getInteger("cantidadVerificados", 0));
        dto.setCantidadNoVerificados(doc.getInteger("cantidadNoVerificados", 0));
        dto.setDiferenciasTotales(doc.getInteger("diferenciasTotales", 0));

        // Mapeo seguro del arreglo anidado 'todosLosConteos'
        List<Document> listaConteosDoc = doc.getList("todosLosConteos", Document.class);
        if (listaConteosDoc != null) {
          List<ItemConteoDTO> listaItems = new ArrayList<>();

          for (Document itemDoc : listaConteosDoc) {
            ItemConteoDTO item = new ItemConteoDTO();

            // Atributos de la raíz del item
            item.setCodigoConteo(itemDoc.getString("codigo"));
            item.setFecha(itemDoc.getString("fecha"));

            item.setComentario(itemDoc.getString("comentario"));
            item.setProductoStockFisico(itemDoc.getInteger("cantidadContada", 0));
            item.setVerificado(itemDoc.getBoolean("estado", false));

            // Extraer subdocumento de Producto
            Document prodDoc = itemDoc.get("producto", Document.class);
            if (prodDoc != null) {
              item.setProductoCodigo(prodDoc.getString("idProducto"));
              item.setProductoNombre(prodDoc.getString("nombre"));
              item.setProductoStockSistema(prodDoc.getInteger("stockSistema", 0));
            }

            // Extraer subdocumento de Usuario
            Document usrDoc = itemDoc.get("usuario", Document.class);
            if (usrDoc != null) {
              item.setCodigoUsuario(usrDoc.getString("idUsuario"));
              item.setNombreUsuario(usrDoc.getString("nombre"));
              item.setRolUsuario(usrDoc.getString("rol"));
            }

            listaItems.add(item);
          }
          dto.setTodosLosConteos(listaItems);
        }

        return dto;
    }

    private Document convertirADocumento(ConteoInventarioGeneralDTO dto) {
        Document doc = new Document();

        // Mapear id de Mongo si ya existe (evita duplicados al actualizar)
        if (dto.getId() != null && !dto.getId().isBlank()) {
          doc.append("_id", new org.bson.types.ObjectId(dto.getId()));
        }

        doc.append("codigoGeneral", dto.getCodigoGeneral())
           .append("fechaRegistro", dto.getFechaRegistro())
           .append("verificadoGlobal", dto.getVerificadoGlobal())
           .append("cantidadVerificados", dto.getCantidadVerificados())
           .append("cantidadNoVerificados", dto.getCantidadNoVerificados())
           .append("diferenciasTotales", dto.getDiferenciasTotales());

        if (dto.getTodosLosConteos() != null) {
          List<Document> listaConteosDoc = new ArrayList<>();

          for (ItemConteoDTO item : dto.getTodosLosConteos()) {
            Document itemDoc = new Document()
              .append("codigo", item.getCodigoConteo())
              .append("fecha", item.getFecha())

              .append("comentario", item.getComentario())
              .append("diferencia", item.getDiferencia())
              .append("cantidadContada", item.getProductoStockFisico())
              .append("estado", item.isVerificado());

            Document prodDoc = new Document()
              .append("idProducto", item.getProductoCodigo())
              .append("nombre", item.getProductoNombre())
              .append("stockSistema", item.getProductoStockSistema());
            itemDoc.append("producto", prodDoc);

            // Empaquetar subdocumento Usuario
            Document usrDoc = new Document()
              .append("idUsuario", item.getCodigoUsuario())
              .append("nombre", item.getNombreUsuario())
              .append("rol", item.getRolUsuario());
            itemDoc.append("usuario", usrDoc);

            listaConteosDoc.add(itemDoc);
          }
          doc.append("todosLosConteos", listaConteosDoc);
        }

        return doc;
    }
}