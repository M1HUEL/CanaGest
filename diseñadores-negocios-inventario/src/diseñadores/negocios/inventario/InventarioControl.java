package diseñadores.negocios.inventario;

import diseñadores.negocios.dto.Producto;

public class InventarioControl {

  public Producto obtenerEntidad(String codigo) {
    return InventarioRepository.getInstancia().getDatos().stream()
      .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
      .findFirst()
      .orElse(null);
  }

  public void descontar(String codigo, int cantidad) {
    Producto p = obtenerEntidad(codigo);
    if (p != null && p.getStock() >= cantidad) {
      p.setStock(p.getStock() - cantidad);
    }
  }

}
