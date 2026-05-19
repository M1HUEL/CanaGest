package diseñadores.presentacion;

import diseñadores.negocios.autenticacion.AutenticacionFacade;
import diseñadores.negocios.autenticacion.IAutenticacion;
import diseñadores.negocios.conteoinventario.ConteoInventarioFacade;
import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.inventario.*;
import diseñadores.negocios.ordenes.compras.IOrdenesCompras;
import diseñadores.negocios.ordenes.compras.OrdenesComprasFacade;
import diseñadores.negocios.productos.IProductos;
import diseñadores.negocios.productos.ProductosFacade;
import diseñadores.negocios.proveedores.*;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.usuarios.UsuariosFacade;
import diseñadores.negocios.ventas.VentasFacade;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.frame.*;
import diseñadores.presentacion.utilidad.*;
import javax.swing.SwingUtilities;
import diseñadores.negocios.conteoinventario.IConteoInventario;

public class Main {

  public static void main(String[] args) {
    Fuentes.cargar();

    InventarioControl inventarioControl = new InventarioControl();

    IUsuarios usuariosFachada = new UsuariosFacade();
    IInventario inventarioFachada = new InventarioFacade(inventarioControl);
    IProveedores proveedoresFachada = new ProveedoresFacade();
    IAutenticacion autenticacionFachada = new AutenticacionFacade();
    IOrdenesCompras ordenesComprasFachada = new OrdenesComprasFacade();
    IProductos productosFachada = new ProductosFacade();
    IConteoInventario conteoInventarioFachada = new ConteoInventarioFacade();

    VentasFacade ventasFachada = new VentasFacade();

    VentasControl control = new VentasControl(
      ventasFachada,
      usuariosFachada,
      inventarioFachada,
      proveedoresFachada,
      autenticacionFachada,
      ordenesComprasFachada,
      productosFachada,
      conteoInventarioFachada,
      new UsuarioDTO()
    );

    SwingUtilities.invokeLater(() -> {
      new PantallaAutenticacion(control).setVisible(true);
    });
  }

}
