package diseñadores.presentacion;

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

    IProveedoresRepository proveedoresRepo = ProveedoresRepository.getInstancia();

    InventarioControl inventarioControl = new InventarioControl();
    ProveedoresControl proveedoresControl = new ProveedoresControl(proveedoresRepo);

    IUsuarios usuariosFachada = new UsuariosFacade();
    IInventario inventarioFachada = new InventarioFacade(inventarioControl);
    IProveedores proveedoresFachada = new ProveedoresFacade(proveedoresControl);

    VentasFacade ventasFachada = new VentasFacade(
      new diseñadores.negocios.productos.ProductosFacade(inventarioFachada),
      inventarioFachada,
      new diseñadores.infraestructura.notificaciones.NotificacionesFacade()
    );

    SwingUtilities.invokeLater(() -> {
      new PantallaAutenticacion(usuariosFachada, ventasFachada, inventarioFachada, proveedoresFachada).setVisible(true);
    });
  }

}
