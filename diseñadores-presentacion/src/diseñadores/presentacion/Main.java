package diseñadores.presentacion;

import diseñadores.infraestructura.notificaciones.NotificacionesFacade;
import diseñadores.infraestructura.pagos.PagosFacade;
import diseñadores.negocios.inventario.*;
import diseñadores.negocios.proveedores.*;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.usuarios.UsuariosFacade;
import diseñadores.negocios.ventas.VentasFacade;
import diseñadores.presentacion.frame.*;
import diseñadores.presentacion.utilidad.*;
import javax.swing.SwingUtilities;

public class Main {

  public static void main(String[] args) {
    Fuentes.cargar();

    InventarioControl inventarioControl = new InventarioControl();

    IUsuarios usuariosFachada = new UsuariosFacade();
    IInventario inventarioFachada = new InventarioFacade(inventarioControl);
    IProveedores proveedoresFachada = new ProveedoresFacade();

    VentasFacade ventasFachada = new VentasFacade();

    SwingUtilities.invokeLater(() -> {
      new PantallaAutenticacion(usuariosFachada, ventasFachada, inventarioFachada, proveedoresFachada).setVisible(true);
    });
  }

}
