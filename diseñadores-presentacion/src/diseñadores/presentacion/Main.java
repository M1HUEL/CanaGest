package diseñadores.presentacion;

import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.usuarios.UsuariosFacade;
import diseñadores.presentacion.frame.*;
import diseñadores.presentacion.utilidad.*;
import javax.swing.SwingUtilities;

public class Main {

  public static void main(String[] args) {

    Fuentes.cargar();

    IUsuarios usuariosFachada = new UsuariosFacade();

    SwingUtilities.invokeLater(() -> {
      new PantallaAutenticacion(usuariosFachada).setVisible(true);
    });
  }

}
