package diseñadores.negocios.ventas;

import diseñadores.negocios.dto.Producto;
import diseñadores.negocios.dto.Proveedor;
import diseñadores.negocios.dto.Ticket;
import diseñadores.negocios.dto.Venta;
import diseñadores.negocios.ventas.notificacion.IServicioNotificacion;
import java.util.ArrayList;
import java.util.List;

public class VentasControl {

  private Venta ventaActual;
  private final List<Producto> inventarioMock;
  private double ultimoEfectivo;
  private IServicioNotificacion servicioCorreo;
  private final int STOCK_MINIMO = 3;

  public VentasControl() {
    this.inventarioMock = new ArrayList<>();

      Proveedor provGranos = new Proveedor("Abarrotes del Mayo", "ventas@mayo.com");
    Proveedor provAceites = new Proveedor("Distribuidora Sonora", "contacto@distsonora.com");

    inventarioMock.add(new Producto("PROD-8342-2323", "Arroz", 28.00, 50, provGranos));
    inventarioMock.add(new Producto("PROD-8342-2324", "Frijol", 32.00, 30, provGranos));
    inventarioMock.add(new Producto("PROD-8342-2325", "Azúcar", 26.00, 20, provGranos));
    inventarioMock.add(new Producto("PROD-8342-2326", "Aceite", 48.00, 15, provAceites));
    inventarioMock.add(new Producto("PROD-8342-2327", "Atún", 18.00, 40, provAceites));
    inventarioMock.add(new Producto("PROD-8342-2328", "Leche", 30.00, 35, provGranos));
    inventarioMock.add(new Producto("PROD-8342-2329", "Sal", 8.00, 60, provGranos));
    inventarioMock.add(new Producto("PROD-8342-2330", "Café", 55.00, 25, provGranos));
    inventarioMock.add(new Producto("PROD-8342-2331", "Jabón", 22.00, 45, provAceites));
  }

  public void setServicioCorreo(IServicioNotificacion servicio) {
    this.servicioCorreo = servicio;
  }

  public void iniciarNuevaVenta() {
    this.ventaActual = new Venta();
    this.ultimoEfectivo = 0.0;
  }

  public Producto procesarProducto(String codigo) {
    Producto p = inventarioMock.stream()
      .filter(prod -> prod.getCodigo().equalsIgnoreCase(codigo)
      || prod.getNombre().equalsIgnoreCase(codigo))
      .findFirst()
      .orElse(null);

    if (p != null && p.getStock() > 0) {
      ventaActual.agregarProducto(p);
      p.setStock(p.getStock() - 1);
      return p;
    }
    return null;
  }

  public double procesarPagoEfectivo(double efectivo) {
    double total = ventaActual.getSubtotalVenta();
    if (efectivo >= total) {
      this.ultimoEfectivo = efectivo;
      double cambio = efectivo - total;
      ventaActual.setPagada(true);
      return cambio;
    }
    return -1;
  }

  public double procesarCalculoCambio(double efectivo) {
    double total = ventaActual.getSubtotalVenta();
    if (efectivo < total) {
      return -1;
    }
    return efectivo - total;
  }

  public void cerrarVenta() {
    ventaActual.setPagada(true);
  }

  public Venta getVentaActual() {
    return ventaActual;
  }

  public Ticket generarTicket() {
    if (ventaActual == null) {
      return null;
    }
    double total = ventaActual.getSubtotalVenta();
    double cambio = ultimoEfectivo - total;
    String folio = "TK-" + System.currentTimeMillis();
    return new Ticket(folio, ventaActual.getListaProductos(), total, ultimoEfectivo, cambio);
  }

  public void procesarFinalizarVenta() {
    ventaActual.setPagada(true);
    for (Producto p : ventaActual.getListaProductos()) {
      if (p.getStock() < STOCK_MINIMO) {
        ejecutarProtocoloReabastecimiento(p);
      }
    }
  }

  private void ejecutarProtocoloReabastecimiento(Producto p) {
    if (servicioCorreo == null) {
      System.out.println("[REABASTECIMIENTO] Sin servicio de correo configurado. "
        + "Producto con stock bajo: " + p.getNombre() + " (" + p.getStock() + " unidades)");
      return;
    }
    Proveedor prov = p.getProveedor();
    String mensaje = "ALERTA DE STOCK: El producto " + p.getNombre()
      + " tiene solo " + p.getStock() + " unidades.";
    boolean enviado = servicioCorreo.enviarNotificacionStock(prov.getEmail(), mensaje);
    if (enviado) {
      System.out.println("Notificación enviada con éxito a: " + prov.getNombre());
    }
  }

}
