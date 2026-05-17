package diseñadores.negocios.ordenes.compras;

import diseñadores.negocios.dto.OrdenCompraDTO;
import java.util.List;

public interface IOrdenesCompras {

  List<OrdenCompraDTO> obtenerOrdenesCompra();

  OrdenCompraDTO obtenerOrdenPorNumero(String numero);

  void guardarOrdenCompra(OrdenCompraDTO orden);

  void actualizarOrdenCompra(OrdenCompraDTO orden);

  void cambiarEstadoOrden(String numero, String nuevoEstado);

}
