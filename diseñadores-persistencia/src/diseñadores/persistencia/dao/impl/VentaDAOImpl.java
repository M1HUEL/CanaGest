package diseñadores.persistencia.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import diseñadores.negocios.dto.ItemVentaDTO;
import diseñadores.negocios.dto.VentaDTO;
import diseñadores.persistencia.conexion.Conexion;
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
      lista.add(convertirADTO(doc));
    }
    return lista;
  }

  @Override
  public VentaDTO obtenerPorFolio(String folio) {
    validarFolioRequerido(folio);
    Document doc = buscarDocumentoPorFolio(folio);
    return (doc != null) ? convertirADTO(doc) : null;
  }

  @Override
  public void guardar(VentaDTO venta) {
    validarDatosVenta(venta);
    validarFolioExistente(venta.getFolio());
    ejecutarInsercion(venta);
  }

  @Override
  public void actualizar(VentaDTO venta) {
    validarDatosVenta(venta);
    validarVentaExiste(venta.getFolio());
    ejecutarReemplazo(venta);
  }

  @Override
  public void eliminar(String folio) {
    validarFolioRequerido(folio);
    validarVentaExiste(folio);
    ejecutarEliminacion(folio);
  }

  private void validarFolioRequerido(String folio) {
    if (folio == null || folio.isBlank()) {
      throw new IllegalArgumentException("El folio de la venta es obligatorio");
    }
  }

  private void validarDatosVenta(VentaDTO venta) {
    if (venta == null) {
      throw new IllegalArgumentException("La venta no puede ser nula");
    }
    validarFolioRequerido(venta.getFolio());
    if (venta.getItems() == null || venta.getItems().isEmpty()) {
      throw new IllegalArgumentException("La venta debe contener al menos un item");
    }
  }

  private void validarFolioExistente(String folio) {
    if (buscarDocumentoPorFolio(folio) != null) {
      throw new IllegalStateException("Ya existe una venta registrada con el folio: " + folio);
    }
  }

  private void validarVentaExiste(String folio) {
    if (buscarDocumentoPorFolio(folio) == null) {
      throw new IllegalStateException("La venta con folio " + folio + " no existe");
    }
  }

  private Document buscarDocumentoPorFolio(String folio) {
    return coleccion.find(Filters.eq("folio", folio)).first();
  }

  private void ejecutarInsercion(VentaDTO venta) {
    coleccion.insertOne(convertirADocumento(venta));
  }

  private void ejecutarReemplazo(VentaDTO venta) {
    coleccion.replaceOne(
      Filters.eq("folio", venta.getFolio()),
      convertirADocumento(venta),
      new ReplaceOptions().upsert(true)
    );
  }

  private void ejecutarEliminacion(String folio) {
    coleccion.deleteOne(Filters.eq("folio", folio));
  }

  private VentaDTO convertirADTO(Document doc) {
    VentaDTO dto = new VentaDTO();
    dto.setFolio(doc.getString("folio"));
    dto.setPagada(Boolean.TRUE.equals(doc.getBoolean("pagada")));
    dto.setSubtotal(BigDecimal.valueOf(doc.getDouble("subtotal")));
    dto.setIva(BigDecimal.valueOf(doc.getDouble("iva")));
    dto.setTotal(BigDecimal.valueOf(doc.getDouble("total")));
    dto.setTotalUnidades(doc.getInteger("totalUnidades", 0));
    dto.setFecha(doc.getString("fecha"));
    dto.setCajero(doc.getString("cajero"));

    String tipoPagoStr = doc.getString("tipoPago");
    if (tipoPagoStr != null) {
      try {
        dto.setTipoPago(diseñadores.negocios.dto.TipoPago.valueOf(tipoPagoStr));
      } catch (IllegalArgumentException ignored) {
      }
    }

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

  private Document convertirADocumento(VentaDTO dto) {
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
      .append("tipoPago", dto.getTipoPago() != null ? dto.getTipoPago().name() : null)
      .append("cajero", dto.getCajero())
      .append("fecha", dto.getFecha())
      .append("items", itemDocs);
  }

}
