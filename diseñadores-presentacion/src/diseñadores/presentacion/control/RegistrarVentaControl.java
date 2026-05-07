package diseñadores.presentacion.control;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.ventas.IVentas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrarVentaControl {

  public enum ResultadoEscaneo {
    OK,
    NO_EXISTE,
    SIN_STOCK
  }

  private final IVentas ventasFachada;
  private VentaDTO ventaActual;
  private List<ProductoDTO> catalogoProductos;

  public RegistrarVentaControl(IVentas ventasFachada) {
    this.ventasFachada = ventasFachada;
    this.ventaActual = new VentaDTO();
    this.catalogoProductos = ventasFachada.obtenerCatalogo();
  }

  public ResultadoEscaneo procesarEscaneo(String codigo) {
    EscanearProductoDTO dto = new EscanearProductoDTO(codigo);
    if (!ventasFachada.existeProducto(dto)) {
      return ResultadoEscaneo.NO_EXISTE;
    }
    if (!ventasFachada.tieneStock(dto)) {
      return ResultadoEscaneo.SIN_STOCK;
    }
    ventasFachada.procesarProducto(ventaActual, dto);
    return ResultadoEscaneo.OK;
  }

  public void decrementarItem(ItemVentaDTO item) {
    List<ItemVentaDTO> items = ventaActual.getItems();
    if (item.getCantidad() > 1) {
      int idx = items.indexOf(item);
      if (idx >= 0) {
        items.set(idx, item.conCantidad(item.getCantidad() - 1));
      }
    } else {
      items.removeIf(i -> i.getCodigo().equalsIgnoreCase(item.getCodigo()));
    }
    recalcularTotales();
  }

  public void eliminarItem(ItemVentaDTO item) {
    ventaActual.getItems().removeIf(i -> i.getCodigo().equalsIgnoreCase(item.getCodigo()));
    recalcularTotales();
  }

  public void cancelarVenta() {
    ventaActual.getItems().clear();
    recalcularTotales();
  }

  public void iniciarNuevaVenta() {
    ventaActual = new VentaDTO();
  }

  public List<ProductoDTO> filtrarCatalogo(String query) {
    if (query == null || query.isEmpty()) {
      return new ArrayList<>(catalogoProductos);
    }
    String q = query.toLowerCase();
    return catalogoProductos.stream()
      .filter(p -> p.getNombre().toLowerCase().contains(q)
      || p.getCodigo().toLowerCase().contains(q))
      .collect(Collectors.toList());
  }

  public List<ProductoDTO> refrescarCatalogo() {
    catalogoProductos = ventasFachada.obtenerCatalogo();
    return catalogoProductos;
  }

  public VentaDTO getVentaActual() {
    return ventaActual;
  }

  public List<ProductoDTO> getCatalogo() {
    return catalogoProductos;
  }

  public boolean carritoVacio() {
    return ventaActual.getItems().isEmpty();
  }

  private void recalcularTotales() {
    List<ItemVentaDTO> items = ventaActual.getItems();
    BigDecimal total = items.stream()
      .map(ItemVentaDTO::getSubtotal)
      .reduce(BigDecimal.ZERO, BigDecimal::add)
      .setScale(2, RoundingMode.HALF_UP);
    BigDecimal subtotal = total.divide(BigDecimal.valueOf(1.16), 2, RoundingMode.HALF_UP);
    BigDecimal iva = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
    int totalUnidades = items.stream().mapToInt(ItemVentaDTO::getCantidad).sum();
    ventaActual.setTotal(total);
    ventaActual.setSubtotal(subtotal);
    ventaActual.setIva(iva);
    ventaActual.setTotalUnidades(totalUnidades);
  }

}
