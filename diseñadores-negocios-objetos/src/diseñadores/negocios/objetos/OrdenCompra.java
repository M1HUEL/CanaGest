package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.persistencia.IPersistencia;
import diseñadores.persistencia.PersistenciaFacade;

import java.util.List;

public class OrdenCompra {

  private static final IPersistencia PERSISTENCIA = PersistenciaFacade.getInstancia();

  public static List<OrdenCompraDTO> obtenerTodas() {
    return PERSISTENCIA.obtenerOrdenesCompra();
  }

  public static OrdenCompraDTO obtenerPorNumero(String numero) {
    return PERSISTENCIA.obtenerOrdenCompraPorNumero(numero);
  }

  public static void guardar(OrdenCompraDTO orden) {
    PERSISTENCIA.guardarOrdenCompra(orden);
  }

  public static void actualizar(OrdenCompraDTO orden) {
    PERSISTENCIA.actualizarOrdenCompra(orden);
  }

  public static void eliminar(String numero) {
    PERSISTENCIA.eliminarOrdenCompra(numero);
  }

}
