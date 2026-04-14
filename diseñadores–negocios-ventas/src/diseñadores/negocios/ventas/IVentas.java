package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.PagoEfectivoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ResultadoPagoDTO;
import diseñadores.negocios.dto.TicketDTO;
import diseñadores.negocios.dto.Venta;
import diseñadores.negocios.dto.VentaDTO;
import java.util.List;

public interface IVentas {

  Venta iniciarNuevaVenta();

  ProductoDTO procesarProducto(Venta ventaActual, EscanearProductoDTO dto);

  boolean existeProducto(EscanearProductoDTO dto);

  ResultadoPagoDTO procesarPagoEfectivo(Venta ventaActual, PagoEfectivoDTO dto);

  double calcularCambio(Venta ventaActual, double efectivo);

  void procesarFinalizarVenta(Venta ventaActual);

  VentaDTO obtenerResumenVenta(Venta ventaActual);

  TicketDTO generarTicket(Venta ventaActual, double montoRecibido);

  List<ProductoDTO> obtenerCatalogo();

}
