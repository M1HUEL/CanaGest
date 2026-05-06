package diseñadores.negocios.ventas;

import diseñadores.infraestructura.dto.RespuestaPagoDTO;
import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.infraestructura.notificaciones.NotificacionesFacade;
import diseñadores.infraestructura.pagos.IPagos;
import diseñadores.infraestructura.pagos.PagosFacade;
import diseñadores.negocios.dto.*;
import diseñadores.negocios.objetos.Venta;
import diseñadores.negocios.productos.IProductos;
import diseñadores.negocios.productos.ProductosFacade;

import java.math.BigDecimal;
import java.util.List;

public class VentasControl {

  private final INotificaciones servicioNotificaciones;
  private final IPagos serviciosPagos;
  private final IProductos serviciosProductos;

  private static final int STOCK_MINIMO = 3;
  private static final String NOMBRE_TIENDA = "La Canasta";
  private static final String RFC = "LCA123456ABC";
  private static final String DIRECCION = "Av. Principal #123, Col. Centro";
  private static final String TELEFONO = "Tel: (555) 123-4567";
  private static final String CAJERO = "Juan Pérez - Caja #1";

  public VentasControl() {
    this.servicioNotificaciones = new NotificacionesFacade();
    this.serviciosPagos = new PagosFacade();
    this.serviciosProductos = new ProductosFacade();
  }

  public VentasControl(INotificaciones sn, IPagos sp, IProductos spr) {
    this.servicioNotificaciones = sn;
    this.serviciosPagos = sp;
    this.serviciosProductos = spr;
  }

  public List<ProductoDTO> obtenerCatalogo() {
    return serviciosProductos.obtenerCatalogo();
  }

  public boolean existeProducto(EscanearProductoDTO dto) {
    validarDtoNoNulo(dto);
    return serviciosProductos.existeProducto(dto);
  }

  public boolean tieneStock(EscanearProductoDTO dto) {
    validarDtoNoNulo(dto);
    return serviciosProductos.tieneStock(dto, 1);
  }

  public ProductoDTO procesarProducto(VentaDTO venta, EscanearProductoDTO dto) {
    validarVentaNoNula(venta);
    validarDtoNoNulo(dto);

    ProductoDTO producto = serviciosProductos.buscarProductoPorCodigo(dto);
    if (producto == null) {
      return null;
    }

    if (!hayStockSuficiente(venta, producto)) {
      return null;
    }

    registrarProductoEnVenta(venta, producto);
    return producto;
  }

  public ResultadoPagoDTO procesarPagoEfectivo(VentaDTO venta, PagoEfectivoDTO dto) {
    validarVentaProcesable(venta);
    validarPagoNoNulo(dto);
    validarMontoRecibido(dto.getMontoRecibido());

    BigDecimal total = venta.getTotal();
    BigDecimal recibido = dto.getMontoRecibido();

    ResultadoPagoDTO resultado = verificarInsuficienciaMonto(venta.getTotal(), dto.getMontoRecibido());

    if (resultado != null) {
      return resultado;
    }

    asignarTipoPagoEfectivo(venta);

    return ResultadoPagoDTO.aprobado(recibido.subtract(total));
  }

  public ResultadoPagoDTO procesarPagoTarjeta(VentaDTO venta, PagoTarjetaDTO pagoTarjeta) {
    validarVentaProcesable(venta);
    validarPagoNoNulo(pagoTarjeta);

    String datos = "numero=" + pagoTarjeta.getNumero() + "|titular=" + pagoTarjeta.getTitular();
    return ejecutarFlujoPagoElectronico(venta, diseñadores.infraestructura.dto.TipoPago.TARJETA, datos, TipoPago.TARJETA);
  }

  public ResultadoPagoDTO procesarPagoTransferencia(VentaDTO venta, PagoTransferenciaDTO dto) {
    validarVentaProcesable(venta);
    validarPagoNoNulo(dto);

    String datos = "clabe=" + dto.getClabe() + "|referencia=" + dto.getReferencia();
    return ejecutarFlujoPagoElectronico(venta, diseñadores.infraestructura.dto.TipoPago.TRANSACCION, datos, TipoPago.TRANSACCION);
  }

  public ResultadoPagoDTO procesarPagoQr(VentaDTO venta, PagoQrDTO pagoQr) {
    validarVentaProcesable(venta);
    validarPagoNoNulo(pagoQr);

    String datos = "referencia=" + pagoQr.getReferencia();
    return ejecutarFlujoPagoElectronico(venta, diseñadores.infraestructura.dto.TipoPago.QR, datos, TipoPago.QR);
  }

  private ResultadoPagoDTO ejecutarFlujoPagoElectronico(VentaDTO v, diseñadores.infraestructura.dto.TipoPago tipoInfra, String datos, TipoPago tipoNegocio) {
    ResultadoPagoDTO resultado = procesarPagoElectronico(v, tipoInfra, datos);

    validarYAsignarTipoPago(v, resultado, tipoNegocio);

    return resultado;
  }

  ResultadoPagoDTO procesarPagoElectronico(VentaDTO venta, diseñadores.infraestructura.dto.TipoPago tipoInfra, String datos) {
    String referencia = generarReferenciaPago();

    RespuestaPagoDTO respuesta = serviciosPagos.procesarPago(tipoInfra, venta.getTotal(), referencia, datos);

    return convertirAResultadoNegocio(respuesta);
  }

  public void procesarFinalizarVenta(VentaDTO venta) {
    validarVentaProcesable(venta);

    validarVentaNoPagada(venta);

    marcarVentaComoPagada(venta);

    actualizarInventarioYAlertas(venta);

    registrarVentaSistema(venta);
  }

  private void actualizarInventarioYAlertas(VentaDTO venta) {
    for (ItemVentaDTO item : venta.getItems()) {
      serviciosProductos.descontarStock(item.getCodigo(), item.getCantidad());
      verificarAlertaReabastecimiento(item.getCodigo());
    }
  }

  private void verificarAlertaReabastecimiento(String codigo) {
    ProductoDTO estado = serviciosProductos.buscarProductoPorCodigo(new EscanearProductoDTO(codigo));
    if (estado != null && estado.getStock() < STOCK_MINIMO) {
      ejecutarProtocoloReabastecimiento(estado);
    }
  }

  public TicketDTO generarTicket(VentaDTO venta, BigDecimal efectivo) {
    validarVentaNoNula(venta);
    validarVentaFinalizada(venta);
    validarMontoEfectivoTicket(efectivo);

    BigDecimal cambio = calcularCambio(venta.getTotal(), efectivo);

    return TicketDTO.generarTicket(
      venta.getFolio(),
      venta.getItems(),
      venta.getSubtotal(),
      venta.getIva(),
      venta.getTotal(),
      efectivo,
      cambio,
      CAJERO,
      NOMBRE_TIENDA,
      RFC,
      DIRECCION,
      TELEFONO,
      venta.getTipoPago()
    );
  }

  public BigDecimal procesarCalcularCambio(VentaDTO venta, BigDecimal efectivo) {
    validarDatosParaCambio(venta, efectivo);

    return calcularCambio(venta.getTotal(), efectivo);
  }

  private BigDecimal calcularCambio(BigDecimal total, BigDecimal recibido) {
    return recibido.compareTo(total) >= 0 ? recibido.subtract(total) : BigDecimal.ZERO;
  }

  private boolean hayStockSuficiente(VentaDTO venta, ProductoDTO producto) {
    int cantidadEnCarrito = calcularCantidadProductoEnCarrito(venta, producto.getCodigo());
    return validarDisponibilidadStock(producto.getStock(), cantidadEnCarrito);
  }

  private int calcularCantidadProductoEnCarrito(VentaDTO venta, String codigoProducto) {
    return venta.getItems().stream()
      .filter(item -> esMismoProducto(item.getCodigo(), codigoProducto))
      .mapToInt(ItemVentaDTO::getCantidad)
      .sum();
  }

  private boolean esMismoProducto(String codigoItem, String codigoBuscado) {
    return codigoItem.equals(codigoBuscado);
  }

  private boolean validarDisponibilidadStock(int stockDisponible, int cantidadSolicitada) {
    return stockDisponible > cantidadSolicitada;
  }

  private void validarYAsignarTipoPago(VentaDTO v, ResultadoPagoDTO resultado, TipoPago tipoNegocio) {
    if (resultado.isAprobado()) {
      v.setTipoPago(tipoNegocio);
    }
  }

  private void validarVentaNoNula(VentaDTO venta) {
    if (venta == null) {
      throw new IllegalArgumentException("La venta no puede ser nula.");
    }
  }

  private void validarDtoNoNulo(Object dto) {
    if (dto == null) {
      throw new IllegalArgumentException("Los datos de entrada no pueden ser nulos.");
    }
  }

  private String generarReferenciaPago() {
    return "VENTA-" + System.currentTimeMillis();
  }

  private void validarVentaProcesable(VentaDTO venta) {
    validarVentaNoNula(venta);
    if (venta.getItems().isEmpty()) {
      throw new IllegalStateException("Venta sin productos.");
    }
  }

  private void validarVentaNoPagada(VentaDTO venta) {
    if (venta.isPagada()) {
      throw new IllegalStateException("La venta ya fue pagada.");
    }
  }

  private void validarVentaFinalizada(VentaDTO venta) {
    if (!venta.isPagada()) {
      throw new IllegalStateException("Venta no pagada.");
    }
  }

  private void validarPagoNoNulo(Object pagoDto) {
    if (pagoDto == null) {
      throw new IllegalArgumentException("Datos de pago nulos.");
    }
  }

  private void validarMontoRecibido(BigDecimal monto) {
    if (monto == null) {
      throw new IllegalArgumentException("Monto recibido nulo.");
    }
  }

  private void validarMontoEfectivoTicket(BigDecimal efectivo) {
    if (efectivo == null || efectivo.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Efectivo inválido.");
    }
  }

  private void validarDatosParaCambio(VentaDTO venta, BigDecimal efectivo) {
    if (venta == null || efectivo == null) {
      throw new IllegalArgumentException("Venta y efectivo son requeridos para calcular el cambio.");
    }
  }

  private ResultadoPagoDTO verificarInsuficienciaMonto(BigDecimal total, BigDecimal recibido) {
    if (recibido.compareTo(total) < 0) {
      return ResultadoPagoDTO.rechazado("Monto insuficiente.");
    }

    return null;
  }

  private ResultadoPagoDTO convertirAResultadoNegocio(RespuestaPagoDTO respuesta) {
    if (respuesta.isExitoso()) {
      return ResultadoPagoDTO.aprobado(respuesta.getCodigoAutorizacion());
    }
    return ResultadoPagoDTO.rechazado(respuesta.getMensaje());
  }

  private void marcarVentaComoPagada(VentaDTO venta) {
    venta.setPagada(true);
    venta.setFolio(generarFolio());
  }

  private void asignarTipoPagoEfectivo(VentaDTO venta) {
    venta.setTipoPago(TipoPago.EFECTIVO);
  }

  private void registrarVentaSistema(VentaDTO venta) {
    Venta.guardar(venta);
  }

  private void registrarProductoEnVenta(VentaDTO venta, ProductoDTO producto) {
    venta.agregarProducto(producto);
  }

  private void ejecutarProtocoloReabastecimiento(ProductoDTO p) {
    String msg = "Alerta: stock bajo para " + p.getNombre() + ". Quedan " + p.getStock() + " unidades.";
    servicioNotificaciones.enviarNotificacionStock(p.getProveedor().getEmail(), msg);
  }

  private String generarFolio() {
    return "TK-" + System.currentTimeMillis();
  }

}
