package diseñadores.negocios.objetos;

import diseñadores.negocios.dto.VentaDTO;
import diseñadores.persistencia.IPersistencia;
import diseñadores.persistencia.PersistenciaFacade;

import java.util.List;

public class Venta {

  private static final IPersistencia PERSISTENCIA = PersistenciaFacade.getInstancia();

  public static List<VentaDTO> obtenerTodas() {
    return PERSISTENCIA.obtenerVentas();
  }

  public static VentaDTO obtenerPorFolio(String folio) {
    return PERSISTENCIA.obtenerVentaPorFolio(folio);
  }

  public static void guardar(VentaDTO venta) {
    PERSISTENCIA.guardarVenta(venta);
  }

  public static void actualizar(VentaDTO venta) {
    PERSISTENCIA.actualizarVenta(venta);
  }

  public static void eliminar(String folio) {
    PERSISTENCIA.eliminarVenta(folio);
  }

}
