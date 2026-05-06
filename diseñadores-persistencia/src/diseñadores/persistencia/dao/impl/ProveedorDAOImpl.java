package diseñadores.persistencia.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.persistencia.conexion.Conexion;
import diseñadores.persistencia.dao.IProveedorDAO;
import org.bson.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAOImpl implements IProveedorDAO {

  private static final String COLECCION = "proveedores";
  private final MongoCollection<Document> coleccion;

  public ProveedorDAOImpl() {
    this.coleccion = Conexion.getInstancia()
      .getDatabase()
      .getCollection(COLECCION);
  }

  @Override
  public List<ProveedorDTO> obtenerTodos() {
    List<ProveedorDTO> lista = new ArrayList<>();
    for (Document doc : coleccion.find()) {
      lista.add(convertirADTO(doc));
    }
    return lista;
  }

  @Override
  public ProveedorDTO obtenerPorCodigo(String codigo) {
    validarCodigoRequerido(codigo);
    Document doc = buscarDocumentoPorCodigo(codigo);
    return (doc != null) ? convertirADTO(doc) : null;
  }

  @Override
  public void guardar(ProveedorDTO proveedor) {
    validarDatosProveedor(proveedor);
    validarCodigoDisponible(proveedor.getCodigo());
    ejecutarInsercion(proveedor);
  }

  @Override
  public void actualizar(ProveedorDTO proveedor) {
    validarDatosProveedor(proveedor);
    validarProveedorExiste(proveedor.getCodigo());
    ejecutarReemplazo(proveedor);
  }

  @Override
  public void eliminar(String codigo) {
    validarCodigoRequerido(codigo);
    validarProveedorExiste(codigo);
    ejecutarEliminacion(codigo);
  }

  private void validarCodigoRequerido(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("El código del proveedor es obligatorio");
    }
  }

  private void validarDatosProveedor(ProveedorDTO proveedor) {
    if (proveedor == null) {
      throw new IllegalArgumentException("El proveedor no puede ser nulo");
    }
    validarCodigoRequerido(proveedor.getCodigo());
    if (proveedor.getNombre() == null || proveedor.getNombre().isBlank()) {
      throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
    }
  }

  private void validarCodigoDisponible(String codigo) {
    if (buscarDocumentoPorCodigo(codigo) != null) {
      throw new IllegalStateException("El código de proveedor ya está registrado");
    }
  }

  private void validarProveedorExiste(String codigo) {
    if (buscarDocumentoPorCodigo(codigo) == null) {
      throw new IllegalStateException("El proveedor no existe");
    }
  }

  private Document buscarDocumentoPorCodigo(String codigo) {
    return coleccion.find(Filters.eq("codigo", codigo)).first();
  }

  private void ejecutarInsercion(ProveedorDTO proveedor) {
    coleccion.insertOne(convertirADocumento(proveedor));
  }

  private void ejecutarReemplazo(ProveedorDTO proveedor) {
    coleccion.replaceOne(
      Filters.eq("codigo", proveedor.getCodigo()),
      convertirADocumento(proveedor),
      new ReplaceOptions().upsert(true)
    );
  }

  private void ejecutarEliminacion(String codigo) {
    coleccion.deleteOne(Filters.eq("codigo", codigo));
  }

  private ProveedorDTO convertirADTO(Document doc) {
    ProveedorDTO dto = new ProveedorDTO(
      doc.getString("nombre"),
      doc.getString("codigo"),
      doc.getString("contacto"),
      doc.getString("telefono"),
      doc.getString("email"),
      doc.getString("direccion"),
      doc.getString("terminosPago"),
      Boolean.TRUE.equals(doc.getBoolean("activo"))
    );

    Double precioProveedor = doc.getDouble("precioProveedor");
    if (precioProveedor != null) {
      dto.setPrecioProveedor(BigDecimal.valueOf(precioProveedor));
    }

    dto.setTiempoEntregaProveedor(doc.getString("tiempoEntregaProveedor"));
    return dto;
  }

  private Document convertirADocumento(ProveedorDTO dto) {
    Document doc = new Document()
      .append("nombre", dto.getNombre())
      .append("codigo", dto.getCodigo())
      .append("contacto", dto.getContacto())
      .append("telefono", dto.getTelefono())
      .append("email", dto.getEmail())
      .append("direccion", dto.getDireccion())
      .append("terminosPago", dto.getTerminosPago())
      .append("activo", dto.isActivo())
      .append("tiempoEntregaProveedor", dto.getTiempoEntregaProveedor());

    if (dto.getPrecioProveedor() != null) {
      doc.append("precioProveedor", dto.getPrecioProveedor().doubleValue());
    }

    return doc;
  }

}
