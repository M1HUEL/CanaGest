package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.*;
import java.math.BigDecimal;
import java.util.List;

public interface IVentas {

  List<ProductoDTO> obtenerCatalogo();

  boolean existeProducto(EscanearProductoDTO dto);

  boolean tieneStock(EscanearProductoDTO dto);

  ProductoDTO procesarProducto(VentaDTO venta, EscanearProductoDTO dto);

  ResultadoPagoDTO procesarPagoEfectivo(VentaDTO venta, PagoEfectivoDTO dto);

  ResultadoPagoDTO procesarPagoTarjeta(VentaDTO venta, PagoTarjetaDTO dto);

  ResultadoPagoDTO procesarPagoTransferencia(VentaDTO venta, PagoTransferenciaDTO dto);

  ResultadoPagoDTO procesarPagoQr(VentaDTO venta, PagoQrDTO dto);

  BigDecimal procesarCalcularCambio(VentaDTO venta, BigDecimal efectivo);

  void procesarFinalizarVenta(VentaDTO venta);

  TicketDTO generarTicket(VentaDTO venta, BigDecimal efectivoRecibido);

  void guardarProducto(ProductoDTO producto);

  void actualizarStockCompleto(String codigo, int nuevoStock, int nuevoMinimo, int nuevoMaximo);

  List<VentaDTO> obtenerHistorialVentas();

}
