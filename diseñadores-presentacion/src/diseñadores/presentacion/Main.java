package diseñadores.presentacion;

import diseñadores.negocios.ventas.IVentas;
import diseñadores.negocios.ventas.VentasFacade;
import diseñadores.presentacion.frame.RegistrarVenta;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.SwingUtilities;

public class Main {

  public static void main(String[] args) {

    Fuentes.cargar();

    IVentas facade = new VentasFacade();

    SwingUtilities.invokeLater(() -> {
      RegistrarVenta ventana = new RegistrarVenta(facade);
      ventana.setVisible(true);
    });
  }

}
