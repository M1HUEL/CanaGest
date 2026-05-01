package diseñadores.persistencia.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.persistencia.Conexion;
import diseñadores.persistencia.dao.IProductoDAO;
import org.bson.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements IProductoDAO {

  private static final String COLECCION = "productos";
  private final MongoCollection<Document> coleccion;

  public ProductoDAOImpl() {
    this.coleccion = Conexion.getInstancia()
      .getDatabase()
      .getCollection(COLECCION);
  }

  @Override
  public List<ProductoDTO> obtenerTodos() {
    List<ProductoDTO> lista = new ArrayList<>();
    for (Document doc : coleccion.find()) {
      lista.add(toDTO(doc));
    }
    return lista;
  }

  @Override
  public ProductoDTO obtenerPorCodigo(String codigo) {
    Document doc = coleccion.find(Filters.eq("codigo", codigo)).first();
    return (doc != null) ? toDTO(doc) : null;
  }

  @Override
  public void guardar(ProductoDTO producto) {
    coleccion.insertOne(toDocument(producto));
  }

  @Override
  public void actualizar(ProductoDTO producto) {
    coleccion.replaceOne(
      Filters.eq("codigo", producto.getCodigo()),
      toDocument(producto),
      new ReplaceOptions().upsert(true)
    );
  }

  @Override
  public void eliminar(String codigo) {
    coleccion.deleteOne(Filters.eq("codigo", codigo));
  }

  private ProductoDTO toDTO(Document doc) {
    ProveedorDTO proveedor = null;

    Document provDoc = doc.get("proveedor", Document.class);
    if (provDoc != null) {
      proveedor = new ProveedorDTO(
        provDoc.getString("nombre"),
        provDoc.getString("codigo"),
        provDoc.getString("contacto"),
        provDoc.getString("telefono"),
        provDoc.getString("email"),
        provDoc.getString("direccion"),
        provDoc.getString("plazoEntrega"),
        Boolean.TRUE.equals(provDoc.getBoolean("activo"))
      );
    }

    ProductoDTO dto = new ProductoDTO(
      doc.getString("codigo"),
      doc.getString("nombre"),
      BigDecimal.valueOf(doc.getDouble("precio")),
      doc.getInteger("stockActual", 0),
      doc.getInteger("stockMinimo", 0),
      doc.getInteger("stockMaximo", 0),
      proveedor
    );

    dto.setFechaModificacion(doc.getString("fechaModificacion"));
    return dto;
  }

  private Document toDocument(ProductoDTO dto) {
    Document doc = new Document()
      .append("codigo", dto.getCodigo())
      .append("nombre", dto.getNombre())
      .append("precio", dto.getPrecio().doubleValue())
      .append("stockActual", dto.getStockActual())
      .append("stockMinimo", dto.getStockMinimo())
      .append("stockMaximo", dto.getStockMaximo())
      .append("fechaModificacion", dto.getFechaModificacion());

    if (dto.getProveedor() != null) {
      ProveedorDTO prov = dto.getProveedor();
      doc.append("proveedor", new Document()
        .append("nombre", prov.getNombre())
        .append("codigo", prov.getCodigo())
        .append("contacto", prov.getContacto())
        .append("telefono", prov.getTelefono())
        .append("email", prov.getEmail())
        .append("direccion", prov.getDireccion())
        .append("plazoEntrega", prov.getTiempoEntregaProveedor())
        .append("activo", prov.isActivo())
      );
    }
    return doc;
  }

}
