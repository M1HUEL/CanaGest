package diseñadores.presentacion;

import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.usuarios.UsuariosFacade;
import diseñadores.negocios.ventas.*;
import diseñadores.presentacion.frame.*;
import diseñadores.presentacion.utilidad.*;
import javax.swing.SwingUtilities;

public class Main {

  public static void main(String[] args) {

    Fuentes.cargar();

    IVentas ventasFachada = new VentasFacade();
    IUsuarios usuariosFachada = new UsuariosFacade();

    SwingUtilities.invokeLater(() -> {
      new PantallaLogin(usuariosFachada).setVisible(true);
    });
  }

}
