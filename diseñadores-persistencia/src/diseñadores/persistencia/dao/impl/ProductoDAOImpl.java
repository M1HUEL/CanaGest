package diseñadores.persistencia.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.persistencia.conexion.Conexion;
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
      lista.add(convertirADTO(doc));
    }
    return lista;
  }

  @Override
  public ProductoDTO obtenerPorCodigo(String codigo) {
    validarCodigoRequerido(codigo);
    Document doc = buscarDocumentoPorCodigo(codigo);
    return (doc != null) ? convertirADTO(doc) : null;
  }

  @Override
  public void guardar(ProductoDTO producto) {
    validarDatosProducto(producto);
    validarCodigoDisponible(producto.getCodigo());
    ejecutarInsercion(producto);
  }

  @Override
  public void actualizar(ProductoDTO producto) {
    validarDatosProducto(producto);
    validarProductoExiste(producto.getCodigo());
    ejecutarReemplazo(producto);
  }

  @Override
  public void eliminar(String codigo) {
    validarCodigoRequerido(codigo);
    validarProductoExiste(codigo);
    ejecutarEliminacion(codigo);
  }

  private void validarCodigoRequerido(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("El código del producto es obligatorio");
    }
  }

  private void validarDatosProducto(ProductoDTO producto) {
    if (producto == null) {
      throw new IllegalArgumentException("El producto no puede ser nulo");
    }
    validarCodigoRequerido(producto.getCodigo());
    if (producto.getNombre() == null || producto.getNombre().isBlank()) {
      throw new IllegalArgumentException("El nombre del producto es obligatorio");
    }
    if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("El precio debe ser un valor positivo");
    }
  }

  private void validarCodigoDisponible(String codigo) {
    if (buscarDocumentoPorCodigo(codigo) != null) {
      throw new IllegalStateException("El código de producto ya está registrado");
    }
  }

  private void validarProductoExiste(String codigo) {
    if (buscarDocumentoPorCodigo(codigo) == null) {
      throw new IllegalStateException("El producto no existe");
    }
  }

  private Document buscarDocumentoPorCodigo(String codigo) {
    return coleccion.find(Filters.eq("codigo", codigo)).first();
  }

  private void ejecutarInsercion(ProductoDTO producto) {
    coleccion.insertOne(convertirADocumento(producto));
  }

  private void ejecutarReemplazo(ProductoDTO producto) {
    coleccion.replaceOne(
      Filters.eq("codigo", producto.getCodigo()),
      convertirADocumento(producto),
      new ReplaceOptions().upsert(true)
    );
  }

  private void ejecutarEliminacion(String codigo) {
    coleccion.deleteOne(Filters.eq("codigo", codigo));
  }

  private ProductoDTO convertirADTO(Document doc) {
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

  private Document convertirADocumento(ProductoDTO dto) {
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
