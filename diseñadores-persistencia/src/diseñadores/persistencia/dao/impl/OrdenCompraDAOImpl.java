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
      lista.add(convertirADTO(doc));
    }
    return lista;
  }

  @Override
  public OrdenCompraDTO obtenerPorNumero(String numero) {
    validarNumeroRequerido(numero);
    Document doc = buscarDocumentoPorNumero(numero);
    return (doc != null) ? convertirADTO(doc) : null;
  }

  @Override
  public void guardar(OrdenCompraDTO orden) {
    validarDatosOrden(orden);
    validarNumeroDisponible(orden.getNumero());
    ejecutarInsercion(orden);
  }

  @Override
  public void actualizar(OrdenCompraDTO orden) {
    validarDatosOrden(orden);
    validarOrdenExiste(orden.getNumero());
    ejecutarReemplazo(orden);
  }

  @Override
  public void eliminar(String numero) {
    validarNumeroRequerido(numero);
    validarOrdenExiste(numero);
    ejecutarEliminacion(numero);
  }

  private void validarNumeroRequerido(String numero) {
    if (numero == null || numero.isBlank()) {
      throw new IllegalArgumentException("El número de orden es obligatorio");
    }
  }

  private void validarDatosOrden(OrdenCompraDTO orden) {
    if (orden == null) {
      throw new IllegalArgumentException("La orden de compra no puede ser nula");
    }
    validarNumeroRequerido(orden.getNumero());
    if (orden.getProveedor() == null) {
      throw new IllegalArgumentException("La orden debe tener un proveedor asignado");
    }
    if (orden.getTotal() == null || orden.getTotal().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("El total de la orden no puede ser negativo");
    }
  }

  private void validarNumeroDisponible(String numero) {
    if (buscarDocumentoPorNumero(numero) != null) {
      throw new IllegalStateException("El número de orden ya está registrado");
    }
  }

  private void validarOrdenExiste(String numero) {
    if (buscarDocumentoPorNumero(numero) == null) {
      throw new IllegalStateException("La orden de compra no existe");
    }
  }

  private Document buscarDocumentoPorNumero(String numero) {
    return coleccion.find(Filters.eq("numero", numero)).first();
  }

  private void ejecutarInsercion(OrdenCompraDTO orden) {
    coleccion.insertOne(convertirADocumento(orden));
  }

  private void ejecutarReemplazo(OrdenCompraDTO orden) {
    coleccion.replaceOne(
      Filters.eq("numero", orden.getNumero()),
      convertirADocumento(orden),
      new ReplaceOptions().upsert(true)
    );
  }

  private void ejecutarEliminacion(String numero) {
    coleccion.deleteOne(Filters.eq("numero", numero));
  }

  private OrdenCompraDTO convertirADTO(Document doc) {
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

  private Document convertirADocumento(OrdenCompraDTO dto) {
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
