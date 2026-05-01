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
      lista.add(toDTO(doc));
    }
    return lista;
  }

  @Override
  public ProveedorDTO obtenerPorCodigo(String codigo) {
    Document doc = coleccion.find(Filters.eq("codigo", codigo)).first();
    return (doc != null) ? toDTO(doc) : null;
  }

  @Override
  public void guardar(ProveedorDTO proveedor) {
    coleccion.insertOne(toDocument(proveedor));
  }

  @Override
  public void actualizar(ProveedorDTO proveedor) {
    coleccion.replaceOne(
      Filters.eq("codigo", proveedor.getCodigo()),
      toDocument(proveedor),
      new ReplaceOptions().upsert(true)
    );
  }

  @Override
  public void eliminar(String codigo) {
    coleccion.deleteOne(Filters.eq("codigo", codigo));
  }

  private ProveedorDTO toDTO(Document doc) {
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

  private Document toDocument(ProveedorDTO dto) {
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
