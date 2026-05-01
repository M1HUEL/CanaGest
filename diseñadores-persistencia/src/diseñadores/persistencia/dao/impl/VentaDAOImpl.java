package diseñadores.persistencia.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import diseñadores.negocios.dto.ItemVentaDTO;
import diseñadores.negocios.dto.VentaDTO;
import diseñadores.persistencia.Conexion;
import diseñadores.persistencia.dao.IVentaDAO;
import org.bson.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VentaDAOImpl implements IVentaDAO {

  private static final String COLECCION = "ventas";
  private final MongoCollection<Document> coleccion;

  public VentaDAOImpl() {
    this.coleccion = Conexion.getInstancia()
      .getDatabase()
      .getCollection(COLECCION);
  }

  @Override
  public List<VentaDTO> obtenerTodas() {
    List<VentaDTO> lista = new ArrayList<>();
    for (Document doc : coleccion.find()) {
      lista.add(toDTO(doc));
    }
    return lista;
  }

  @Override
  public VentaDTO obtenerPorFolio(String folio) {
    Document doc = coleccion.find(Filters.eq("folio", folio)).first();
    return (doc != null) ? toDTO(doc) : null;
  }

  @Override
  public void guardar(VentaDTO venta) {
    coleccion.insertOne(toDocument(venta));
  }

  @Override
  public void actualizar(VentaDTO venta) {
    coleccion.replaceOne(
      Filters.eq("folio", venta.getFolio()),
      toDocument(venta),
      new ReplaceOptions().upsert(true)
    );
  }

  @Override
  public void eliminar(String folio) {
    coleccion.deleteOne(Filters.eq("folio", folio));
  }

  private VentaDTO toDTO(Document doc) {
    VentaDTO dto = new VentaDTO();

    dto.setFolio(doc.getString("folio"));
    dto.setPagada(Boolean.TRUE.equals(doc.getBoolean("pagada")));
    dto.setSubtotal(BigDecimal.valueOf(doc.getDouble("subtotal")));
    dto.setIva(BigDecimal.valueOf(doc.getDouble("iva")));
    dto.setTotal(BigDecimal.valueOf(doc.getDouble("total")));
    dto.setTotalUnidades(doc.getInteger("totalUnidades", 0));

    List<ItemVentaDTO> items = new ArrayList<>();
    List<Document> itemDocs = doc.getList("items", Document.class);
    if (itemDocs != null) {
      for (Document itemDoc : itemDocs) {
        items.add(new ItemVentaDTO(
          itemDoc.getString("codigo"),
          itemDoc.getString("nombre"),
          BigDecimal.valueOf(itemDoc.getDouble("precioUnitario")),
          itemDoc.getInteger("cantidad", 1)
        ));
      }
    }
    dto.setItems(items);

    return dto;
  }

  private Document toDocument(VentaDTO dto) {
    List<Document> itemDocs = new ArrayList<>();
    for (ItemVentaDTO item : dto.getItems()) {
      itemDocs.add(new Document()
        .append("codigo", item.getCodigo())
        .append("nombre", item.getNombre())
        .append("precioUnitario", item.getPrecioUnitario().doubleValue())
        .append("cantidad", item.getCantidad())
        .append("subtotal", item.getSubtotal().doubleValue())
      );
    }

    return new Document()
      .append("folio", dto.getFolio())
      .append("pagada", dto.isPagada())
      .append("subtotal", dto.getSubtotal().doubleValue())
      .append("iva", dto.getIva().doubleValue())
      .append("total", dto.getTotal().doubleValue())
      .append("totalUnidades", dto.getTotalUnidades())
      .append("items", itemDocs);
  }

}
