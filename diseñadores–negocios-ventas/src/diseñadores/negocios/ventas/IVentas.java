package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.EscanearProductoDTO;
import diseñadores.negocios.dto.PagoEfectivoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ResultadoPagoDTO;
import diseñadores.negocios.dto.TicketDTO;
import diseñadores.negocios.dto.VentaDTO;
import java.util.List;

public interface IVentas {

  void nuevaVenta();

  ProductoDTO procesarProducto(EscanearProductoDTO dto);

  boolean existeProducto(EscanearProductoDTO dto);

  ResultadoPagoDTO procesarPagoEfectivo(PagoEfectivoDTO dto);

  double calcularCambio(double efectivo);

  void procesarFinalizarVenta();

  VentaDTO obtenerVentaActual();

  TicketDTO generarTicket();

  List<ProductoDTO> obtenerCatalogo();

}
