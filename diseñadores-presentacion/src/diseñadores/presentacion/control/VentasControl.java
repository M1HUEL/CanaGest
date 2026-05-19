package diseñadores.presentacion.control;

import diseñadores.negocios.autenticacion.IAutenticacion;
import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.ordenes.compras.IOrdenesCompras;
import diseñadores.negocios.productos.IProductos;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.ventas.IVentas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import diseñadores.negocios.conteoinventario.IConteoInventario;

public class VentasControl {

  private final IVentas ventasFachada;
  private final IUsuarios usuariosFachada;
  private final IInventario inventarioFachada;
  private final IProveedores proveedoresFachada;
  private final IAutenticacion autenticacionFachada;
  private final IOrdenesCompras ordenesComprasFachada;
  private final IProductos productosFachada;
  private final IConteoInventario conteoInventarioGeneralFacade;

  private final UsuarioDTO usuarioActivo;

  private VentaDTO ventaActual;
  private List<ProductoDTO> catalogoProductos;

  public VentasControl(
    IVentas ventasFachada,
    IUsuarios usuariosFachada,
    IInventario inventarioFachada,
    IProveedores proveedoresFachada,
    IAutenticacion autenticacionFachada,
    IOrdenesCompras ordenesComprasFachada,
    IProductos productosFachada,
    IConteoInventario conteoInventarioGeneralFacade,
    UsuarioDTO usuarioActivo) {
    this.ventasFachada = ventasFachada;
    this.usuariosFachada = usuariosFachada;
    this.inventarioFachada = inventarioFachada;
    this.proveedoresFachada = proveedoresFachada;
    this.autenticacionFachada = autenticacionFachada;
    this.ordenesComprasFachada = ordenesComprasFachada;
    this.productosFachada = productosFachada;
    this.conteoInventarioGeneralFacade = conteoInventarioGeneralFacade;
    this.usuarioActivo = usuarioActivo;

    inicializarEstado();
  }

  private void inicializarEstado() {
    iniciarNuevaVenta();
    refrescarCatalogo();
  }

  public Optional<UsuarioDTO> autenticar(String nombre, String contrasena) {
    return autenticacionFachada.autenticar(nombre, contrasena);
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

  public IAutenticacion getAutenticacionFachada() {
    return autenticacionFachada;
  }

  public IOrdenesCompras getOrdenesComprasFachada() {
    return ordenesComprasFachada;
  }

  public IConteoInventario getConteoInventarioGeneralFacade() {
    return conteoInventarioGeneralFacade;
  }

  public UsuarioDTO getUsuarioActivo() {
    return usuarioActivo;
  }

  public List<ProveedorDTO> obtenerProveedores() {
    return proveedoresFachada.obtenerProveedores();
  }

  public IProductos getProductosFachada() {
    return productosFachada;
  }

  public int contarProveedoresActivos() {
    return proveedoresFachada.contarProveedoresActivos();
  }

  public void guardarProveedor(ProveedorDTO proveedor) {
    proveedoresFachada.guardarProveedor(proveedor);
  }

  public void actualizarProveedor(ProveedorDTO proveedor) {
    proveedoresFachada.actualizarProveedor(proveedor);
  }

  public List<OrdenCompraDTO> obtenerOrdenesCompra() {
    return ordenesComprasFachada.obtenerOrdenesCompra();
  }

  public void guardarOrdenCompra(OrdenCompraDTO orden) {
    ordenesComprasFachada.guardarOrdenCompra(orden);
  }

  public void cambiarEstadoOrden(String numero, String nuevoEstado) {
    ordenesComprasFachada.cambiarEstadoOrden(numero, nuevoEstado);
  }

  public List<ProductoDTO> obtenerProductosInventario() {
    return inventarioFachada.obtenerTodos();
  }

  public void ajustarStock(String codigo, int nuevoStockFisico) {
    inventarioFachada.ajustarStock(codigo, nuevoStockFisico);
  }

  public ResultadoEscaneo procesarEscaneo(String codigo) {
    EscanearProductoDTO dto = new EscanearProductoDTO(codigo);
    return validarYProcesarProducto(dto);
  }

  public void actualizarStockCompleto(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo) {
    inventarioFachada.actualizarStockCompleto(codigo, nuevoStock, nuevoMinimo, nuevoMaximo);
  }

  public void guardarProducto(ProductoDTO producto) {
    productosFachada.guardarProducto(producto);
  }

  private ResultadoEscaneo validarYProcesarProducto(EscanearProductoDTO dto) {
    if (!ventasFachada.existeProducto(dto)) {
      return ResultadoEscaneo.NO_EXISTE;
    }
    if (!ventasFachada.tieneStock(dto)) {
      return ResultadoEscaneo.SIN_STOCK;
    }
    ProductoDTO productoProcesado = ventasFachada.procesarProducto(ventaActual, dto);
    if (productoProcesado == null) {
      return ResultadoEscaneo.SIN_STOCK;
    }
    recalcularTotales();
    return ResultadoEscaneo.OK;
  }

  public void decrementarItem(ItemVentaDTO item) {
    if (item.getCantidad() > 1) {
      ajustarCantidadItem(item, item.getCantidad() - 1);
    } else {
      eliminarItem(item);
    }
  }

  private void ajustarCantidadItem(ItemVentaDTO item, int nuevaCantidad) {
    List<ItemVentaDTO> items = ventaActual.getItems();
    int idx = items.indexOf(item);
    if (idx >= 0) {
      items.set(idx, item.conCantidad(nuevaCantidad));
      recalcularTotales();
    }
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
    this.ventaActual = new VentaDTO();
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

  public void finalizarVenta(TipoPago tipoPago) {
    if (tipoPago == null) {
      throw new IllegalStateException("El tipo de pago no ha sido asignado antes de finalizar la venta.");
    }
    ventaActual.setTipoPago(tipoPago);
    ventaActual.setCajero(usuarioActivo.getNombre());
    ventasFachada.procesarFinalizarVenta(ventaActual);
  }

  public TicketDTO generarTicket(BigDecimal recibido) {
    return ventasFachada.generarTicket(ventaActual, recibido);
  }

  public TicketDTO generarTicket() {
    return generarTicket(BigDecimal.ZERO);
  }

  public List<ProductoDTO> filtrarCatalogo(String query) {
    if (esQueryInvalida(query)) {
      return new ArrayList<>(catalogoProductos);
    }
    return ejecutarFiltro(query.toLowerCase());
  }

  private boolean esQueryInvalida(String query) {
    return query == null || query.isEmpty();
  }

  private List<ProductoDTO> ejecutarFiltro(String q) {
    return catalogoProductos.stream()
      .filter(p -> p.getNombre().toLowerCase().contains(q)
      || p.getCodigo().toLowerCase().contains(q))
      .collect(Collectors.toList());
  }

  public List<ProductoDTO> refrescarCatalogo() {
    this.catalogoProductos = ventasFachada.obtenerCatalogo();
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
    actualizarDatosVenta(total, subtotal, iva, totalUnidades);
  }

  private void actualizarDatosVenta(BigDecimal total, BigDecimal subtotal, BigDecimal iva, int unidades) {
    ventaActual.setTotal(total);
    ventaActual.setSubtotal(subtotal);
    ventaActual.setIva(iva);
    ventaActual.setTotalUnidades(unidades);
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

  public List<VentaDTO> obtenerHistorialVentas() {
    return ventasFachada.obtenerHistorialVentas();
  }

  public void eliminarProveedor(ProveedorDTO proveedor) {
    proveedoresFachada.eliminarProveedor(proveedor.getCodigo());
  }

  /**
   * Recupera la sesión de auditoría masiva que se encuentre abierta actualmente
   * (cuyo flag 'verificadoGlobal' sea falso). Si no hay ninguna sesión en
   * curso, retorna null.
   */
  public ConteoInventarioDTO obtenerAuditoriaActiva() {
    List<ConteoInventarioDTO> historial = conteoInventarioGeneralFacade.obtenerHistorialSesiones();
    if (historial != null && !historial.isEmpty()) {
      for (ConteoInventarioDTO aud : historial) {
        if (!aud.getVerificadoGlobal()) {
          return aud;
        }
      }
    }
    return null;
  }

  /**
   * Disparador del evento "Iniciar Nuevo Conteo". Congela el stock actual del
   * catálogo relacional y genera un documento borrador totalmente nuevo en
   * MongoDB con su folio único.
   */
  public ConteoInventarioDTO inicializarNuevoConteoGeneral() {
    ConteoInventarioDTO nuevaAuditoria = new ConteoInventarioDTO();
    nuevaAuditoria.setCodigoGeneral("AUD-" + (System.currentTimeMillis() / 1000));

    String fechaActual = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
    nuevaAuditoria.setFechaRegistro(fechaActual);
    nuevaAuditoria.setVerificadoGlobal(false);

    List<ProductoDTO> productosSistema = obtenerProductosInventario();
    List<ItemConteoDTO> itemsIniciales = new ArrayList<>();

    int secuencia = 1;
    for (ProductoDTO p : productosSistema) {
      // Usamos el constructor vacío para evitar desajustes de parámetros
      ItemConteoDTO item = new ItemConteoDTO();

      // Identificadores base del renglón
      item.setCodigo("ITM-" + nuevaAuditoria.getCodigoGeneral() + "-" + secuencia++);
      item.setComentario("");
      item.setVerificado(false);

      // Datos del Auditor activo asignado
      item.setNombreUsuario(usuarioActivo.getNombre());
      item.setRolUsuario(usuarioActivo.getRol().toString());

      // Datos del Producto asociado extraídos del sistema
      item.setProductoCodigo(p.getCodigo());
      item.setProductoNombre(p.getNombre());
      item.setProductoStockSistema(p.getStock());
      item.setProductoStockFisico(p.getStock());
      
      item.setNombreUsuario(usuarioActivo.getNombre());
      item.setRolUsuario(usuarioActivo.getRol().toString());

      itemsIniciales.add(item);
    }

    nuevaAuditoria.setTodosLosConteos(itemsIniciales);
    nuevaAuditoria.recalcularMetricas();

    conteoInventarioGeneralFacade.crearSesionAuditoria(nuevaAuditoria);
    return nuevaAuditoria;
  }

  /**
   * Vuelca las actualizaciones progresivas en la base de datos de MongoDB.
   * Almacena firmas y justificaciones de forma atómica sin alterar el stock del
   * catálogo.
   */
  public void guardarProgresoAuditoria(ConteoInventarioDTO sesionGeneral) {
    if (sesionGeneral != null) {
      sesionGeneral.recalcularMetricas();
      this.conteoInventarioGeneralFacade.guardarProgresoAuditoria(sesionGeneral);
    }
  }

  /**
   * Cierre y Consolidación Final de la auditoría. Valida firmas en desajustes e
   * impacta permanentemente el stock en el catálogo del sistema.
   */
  public void actualizarAuditoriaGeneral(ConteoInventarioDTO sesionGeneral) {
    if (sesionGeneral != null) {
      sesionGeneral.recalcularMetricas();
      this.conteoInventarioGeneralFacade.registrarYAplicarAuditoriaGlobal(sesionGeneral);
    }
  }

  /**
   * Busca los detalles completos de una sesión de auditoría mediante su
   * ID/Folio.
   */
  public ConteoInventarioDTO buscarSesionAuditoriaPorCodigo(String codigoGeneral) {
    return this.conteoInventarioGeneralFacade.buscarSesionPorCodigo(codigoGeneral);
  }

  /**
   * Recupera la lista con todo el historial de auditorías del sistema.
   */
  public List<ConteoInventarioDTO> obtenerHistorialSesionesAuditoria() {
    List<ConteoInventarioDTO> historial = this.conteoInventarioGeneralFacade.obtenerHistorialSesiones();
    return historial != null ? historial : new ArrayList<>();
  }
}
