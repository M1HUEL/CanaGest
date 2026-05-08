package diseñadores.presentacion.control;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.ventas.IVentas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VentasControl {

  public enum ResultadoEscaneo {
    OK,
    NO_EXISTE,
    SIN_STOCK
  }

  private final IVentas ventasFachada;
  private final IUsuarios usuariosFachada;
  private final IInventario inventarioFachada;
  private final IProveedores proveedoresFachada;
  private final UsuarioDTO usuarioActivo;

  private VentaDTO ventaActual;
  private List<ProductoDTO> catalogoProductos;

  public VentasControl(IVentas ventasFachada,
    IUsuarios usuariosFachada,
    IInventario inventarioFachada,
    IProveedores proveedoresFachada,
    UsuarioDTO usuarioActivo) {
    this.ventasFachada = ventasFachada;
    this.usuariosFachada = usuariosFachada;
    this.inventarioFachada = inventarioFachada;
    this.proveedoresFachada = proveedoresFachada;
    this.usuarioActivo = usuarioActivo;
    this.ventaActual = new VentaDTO();
    this.catalogoProductos = ventasFachada.obtenerCatalogo();
  }

  public IVentas getVentasFachada() {
    return ventasFachada;
  }

  public IUsuarios getUsuariosFachada() {
    return usuariosFachada;
  }

  public IInventario getInventarioFachada() {
    return inventarioFachada;
  }

  public IProveedores getProveedoresFachada() {
    return proveedoresFachada;
  }

  public UsuarioDTO getUsuarioActivo() {
    return usuarioActivo;
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
      eliminarItem(item);
      return;
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

  public boolean carritoVacio() {
    return ventaActual.getItems().isEmpty();
  }

  public ResultadoPagoDTO procesarPagoEfectivo(PagoEfectivoDTO pagoDTO) {
    return ventasFachada.procesarPagoEfectivo(ventaActual, pagoDTO);
  }

  public ResultadoPagoDTO procesarPagoTarjeta(PagoTarjetaDTO pagoDTO) {
    return ventasFachada.procesarPagoTarjeta(ventaActual, pagoDTO);
  }

  public ResultadoPagoDTO procesarPagoCoDi(PagoQrDTO pagoDTO) {
    return ventasFachada.procesarPagoQr(ventaActual, pagoDTO);
  }

  public ResultadoPagoDTO procesarPagoTransferencia(PagoTransferenciaDTO pagoDTO) {
    return ventasFachada.procesarPagoTransferencia(ventaActual, pagoDTO);
  }

  public BigDecimal calcularCambio(BigDecimal recibido) {
    return ventasFachada.procesarCalcularCambio(ventaActual, recibido);
  }

  public void finalizarVenta() {
    ventasFachada.procesarFinalizarVenta(ventaActual);
  }

  public TicketDTO generarTicket(BigDecimal recibido) {
    return ventasFachada.generarTicket(ventaActual, recibido);
  }

  public TicketDTO generarTicket() {
    return ventasFachada.generarTicket(ventaActual, BigDecimal.ZERO);
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

  public List<ProductoDTO> getCatalogo() {
    return catalogoProductos;
  }

  public VentaDTO getVentaActual() {
    return ventaActual;
  }

  private void recalcularTotales() {
    List<ItemVentaDTO> items = ventaActual.getItems();
    BigDecimal total = calcularTotal(items);
    BigDecimal subtotal = calcularSubtotal(total);
    BigDecimal iva = calcularIva(total, subtotal);
    int totalUnidades = calcularUnidades(items);
    ventaActual.setTotal(total);
    ventaActual.setSubtotal(subtotal);
    ventaActual.setIva(iva);
    ventaActual.setTotalUnidades(totalUnidades);
  }

  private BigDecimal calcularTotal(List<ItemVentaDTO> items) {
    return items.stream()
      .map(ItemVentaDTO::getSubtotal)
      .reduce(BigDecimal.ZERO, BigDecimal::add)
      .setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal calcularSubtotal(BigDecimal total) {
    return total.divide(BigDecimal.valueOf(1.16), 2, RoundingMode.HALF_UP);
  }

  private BigDecimal calcularIva(BigDecimal total, BigDecimal subtotal) {
    return total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
  }

  private int calcularUnidades(List<ItemVentaDTO> items) {
    return items.stream().mapToInt(ItemVentaDTO::getCantidad).sum();
  }

}
