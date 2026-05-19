package diseñadores.negocios.conteoinventario;

import diseñadores.negocios.dto.ConteoInventarioDTO;
import diseñadores.negocios.dto.ItemConteoDTO;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.inventario.InventarioFacade;
import diseñadores.negocios.objetos.ConteoInventario;
import java.util.List;
import java.util.UUID;

public class ConteoInventarioControl {

  private final IInventario serviciosInventario;

  public ConteoInventarioControl() {
    this.serviciosInventario = new InventarioFacade();
  }

  public ConteoInventarioControl(IInventario serviciosInventario) {
    this.serviciosInventario = serviciosInventario;
  }

  public void guardarProgresoAuditoria(ConteoInventarioDTO sesion) {
    validarSesionNoNula(sesion);
    ConteoInventario.actualizar(sesion);
  }

  public void registrarYAplicarAuditoriaGlobal(ConteoInventarioDTO sesion) {
    validarSesionNoNula(sesion);
    validarFirmasEnDiscrepancias(sesion);

    sesion.setVerificadoGlobal(true);
    ConteoInventario.actualizar(sesion);

    for (ItemConteoDTO item : sesion.getTodosLosConteos()) {
      serviciosInventario.actualizarStock(item.getProductoCodigo(), item.getProductoStockFisico());
    }
  }

  private void validarFirmasEnDiscrepancias(ConteoInventarioDTO sesion) {
    if (sesion.getTodosLosConteos() == null || sesion.getTodosLosConteos().isEmpty()) {
      throw new IllegalArgumentException("La sesión no registra ningún conteo de inventario.");
    }

    for (ItemConteoDTO item : sesion.getTodosLosConteos()) {
      if (item.getProductoStockSistema() != item.getProductoStockFisico()) {
        if (item.getNombreUsuario() == null || item.getNombreUsuario().isBlank()) {
          throw new IllegalArgumentException("El producto " + item.getProductoCodigo()
            + " presenta discrepancias y requiere la firma del auditor para el cierre.");
        }
        if (item.getComentario() == null || item.getComentario().isBlank()) {
          throw new IllegalArgumentException("Debe indicar el motivo (comentario) del desfase en el producto: "
            + item.getProductoCodigo());
        }
      }
    }
  }

  public void crearSesionAuditoria(ConteoInventarioDTO sesion) {
    validarSesionNoNula(sesion);

    if (sesion.getTodosLosConteos() == null || sesion.getTodosLosConteos().isEmpty()) {
      throw new IllegalArgumentException("No se puede aperturar una sesión de auditoría sin productos para contar.");
    }

    if (sesion.getCodigoGeneral() == null || sesion.getCodigoGeneral().isBlank()) {
      sesion.setCodigoGeneral(generarCodigoGeneral());
    }

    for (ItemConteoDTO item : sesion.getTodosLosConteos()) {
      if (item.getProductoCodigo() == null || item.getProductoCodigo().isBlank()) {
        throw new IllegalArgumentException("Todos los ítems auditados deben poseer un código de producto válido.");
      }
      if (item.getProductoStockFisico() < 0) {
        throw new IllegalArgumentException("El stock físico del producto " + item.getProductoCodigo() + " no puede ser negativo.");
      }
      item.setCodigo(sesion.getCodigoGeneral());
    }

    ConteoInventario.guardar(sesion);
  }

  public List<ConteoInventarioDTO> obtenerHistorialSesiones() {
    return ConteoInventario.obtenerTodos();
  }

  public ConteoInventarioDTO buscarSesionPorCodigo(String codigoGeneral) {
    if (codigoGeneral == null || codigoGeneral.isBlank()) {
      throw new IllegalArgumentException("El código de búsqueda provisto es inválido.");
    }
    return ConteoInventario.obtenerPorCodigo(codigoGeneral);
  }

  private void validarSesionNoNula(ConteoInventarioDTO sesion) {
    if (sesion == null) {
      throw new IllegalArgumentException("El contenedor general de auditoría no puede ser nulo.");
    }
  }

  private void validarDatosObligatorios(ConteoInventarioDTO sesion) {
    if (sesion.getTodosLosConteos() == null || sesion.getTodosLosConteos().isEmpty()) {
      throw new IllegalArgumentException("La sesión no registra ningún conteo de inventario adjunto.");
    }

    for (ItemConteoDTO item : sesion.getTodosLosConteos()) {
      if (item.getNombreUsuario() == null || item.getNombreUsuario().isBlank()) {
        throw new IllegalArgumentException("La firma del auditor es requerida en cada ítem de la sesión.");
      }
      if (item.getProductoCodigo() == null || item.getProductoCodigo().isBlank()) {
        throw new IllegalArgumentException("Código de producto faltante en una de las líneas de la sesión.");
      }
      if (item.getProductoStockFisico() < 0) {
        throw new IllegalArgumentException("Existen lecturas con stock físico en valores negativos.");
      }
    }
  }

  private String generarCodigoGeneral() {
    return "AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }
}
