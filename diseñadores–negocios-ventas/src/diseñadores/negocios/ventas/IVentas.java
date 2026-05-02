package diseñadores.negocios.ventas;

import diseñadores.infraestructura.dto.TipoPago;
import diseñadores.negocios.dto.*;

import java.math.BigDecimal;
import java.util.List;

public interface IVentas {

  VentaDTO iniciarNuevaVenta();

  boolean existeProducto(EscanearProductoDTO dto);

  boolean tieneStock(EscanearProductoDTO dto);

  ProductoDTO procesarProducto(VentaDTO ventaActual, EscanearProductoDTO dto);

  ResultadoPagoDTO procesarPagoEfectivo(VentaDTO ventaActual, PagoEfectivoDTO dto);

  ResultadoPagoDTO procesarPagoElectronico(VentaDTO ventaActual, TipoPago tipo, String datos);

  BigDecimal procesarCalcularCambio(VentaDTO ventaActual, BigDecimal efectivo);

  void procesarFinalizarVenta(VentaDTO ventaActual);

  VentaDTO obtenerResumenVenta(VentaDTO ventaActual);

  TicketDTO generarTicket(VentaDTO ventaActual, BigDecimal montoRecibido);

  List<ProductoDTO> obtenerCatalogo();

}
