package diseñadores.persistencia.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.persistencia.conexion.Conexion;
import diseñadores.persistencia.dao.IOrdenCompraDAO;
import org.bson.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraDAOImpl implements IOrdenCompraDAO {

  private static final String COLECCION = "ordenes_compra";
  private final MongoCollection<Document> coleccion;

  public OrdenCompraDAOImpl() {
    this.coleccion = Conexion.getInstancia()
      .getDatabase()
      .getCollection(COLECCION);
  }

  @Override
  public List<OrdenCompraDTO> obtenerTodas() {
    List<OrdenCompraDTO> lista = new ArrayList<>();
    for (Document doc : coleccion.find()) {
      lista.add(toDTO(doc));
    }
    return lista;
  }

  @Override
  public OrdenCompraDTO obtenerPorNumero(String numero) {
    Document doc = coleccion.find(Filters.eq("numero", numero)).first();
    return (doc != null) ? toDTO(doc) : null;
  }

  @Override
  public void guardar(OrdenCompraDTO orden) {
    coleccion.insertOne(toDocument(orden));
  }

  @Override
  public void actualizar(OrdenCompraDTO orden) {
    coleccion.replaceOne(
      Filters.eq("numero", orden.getNumero()),
      toDocument(orden),
      new ReplaceOptions().upsert(true)
    );
  }

  @Override
  public void eliminar(String numero) {
    coleccion.deleteOne(Filters.eq("numero", numero));
  }

  private OrdenCompraDTO toDTO(Document doc) {
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
        provDoc.getString("terminosPago"),
        Boolean.TRUE.equals(provDoc.getBoolean("activo"))
      );

      Double precioProveedor = provDoc.getDouble("precioProveedor");
      if (precioProveedor != null) {
        proveedor.setPrecioProveedor(BigDecimal.valueOf(precioProveedor));
      }
      proveedor.setTiempoEntregaProveedor(provDoc.getString("tiempoEntregaProveedor"));
    }

    return new OrdenCompraDTO(
      doc.getString("numero"),
      doc.getString("fecha"),
      proveedor,
      doc.getString("estado"),
      doc.getInteger("cantidadProductos", 0),
      BigDecimal.valueOf(doc.getDouble("total"))
    );
  }

  private Document toDocument(OrdenCompraDTO dto) {
    Document doc = new Document()
      .append("numero", dto.getNumero())
      .append("fecha", dto.getFecha())
      .append("estado", dto.getEstado())
      .append("cantidadProductos", dto.getCantidadProductos())
      .append("total", dto.getTotal().doubleValue());

    if (dto.getProveedor() != null) {
      ProveedorDTO prov = dto.getProveedor();
      Document provDoc = new Document()
        .append("nombre", prov.getNombre())
        .append("codigo", prov.getCodigo())
        .append("contacto", prov.getContacto())
        .append("telefono", prov.getTelefono())
        .append("email", prov.getEmail())
        .append("direccion", prov.getDireccion())
        .append("terminosPago", prov.getTerminosPago())
        .append("activo", prov.isActivo())
        .append("tiempoEntregaProveedor", prov.getTiempoEntregaProveedor());

      if (prov.getPrecioProveedor() != null) {
        provDoc.append("precioProveedor", prov.getPrecioProveedor().doubleValue());
      }

      doc.append("proveedor", provDoc);
    }

    return doc;
  }

}
