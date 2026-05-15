package diseñadores.nucleo;

import diseñadores.negocios.dto.ItemVentaDTO;
import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.negocios.dto.TipoPago;
import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.UsuarioRol;
import diseñadores.negocios.dto.VentaDTO;
import diseñadores.negocios.objetos.OrdenCompra;
import diseñadores.negocios.objetos.Producto;
import diseñadores.negocios.objetos.Proveedor;
import diseñadores.negocios.objetos.Usuario;
import diseñadores.negocios.objetos.Venta;
import diseñadores.persistencia.PersistenciaFacade;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    System.out.println("═══════════════════════════════════════════════");
    System.out.println("       SISTEMA CANAGEST - CARGA INICIAL         ");
    System.out.println("═══════════════════════════════════════════════\n");

    insertarProveedores();
    insertarProductos();
    insertarUsuarios();
    insertarOrdenesCompra();
    insertarVentas();

    System.out.println("\n═══════════════════════════════════════════════");
    System.out.println("       VERIFICACIÓN DE DATOS                    ");
    System.out.println("═══════════════════════════════════════════════\n");

    verificarProveedores();
    verificarProductos();
    verificarUsuarios();
    verificarOrdenesCompra();
    verificarVentas();

    PersistenciaFacade.getInstancia();
    System.out.println("\n✔ Carga inicial completada.");
  }

  // ─────────────────────── INSERCIÓN ───────────────────────────────
  private static void insertarProveedores() {
    System.out.println("── Insertando proveedores...");

    ProveedorDTO p1 = new ProveedorDTO(
      "Abarrotes del Mayo", "PROV-001", "Juan Pérez",
      "555-0101", "ventas@mayo.com",
      "Av. Mayo 123", "30 días", true
    );
    ProveedorDTO p2 = new ProveedorDTO(
      "Distribuidora Sonora", "PROV-002", "María López",
      "555-0202", "contacto@distsonora.com",
      "Calle Sonora 456", "15 días", true
    );
    ProveedorDTO p3 = new ProveedorDTO(
      "Lácteos Premium", "PROV-003", "Carlos Ramírez",
      "555-0303", "info@lacteospremium.com",
      "Blvd. Lácteo 789", "45 días", true
    );
    ProveedorDTO p4 = new ProveedorDTO(
      "Granos y Semillas SA", "PROV-004", "Ana López",
      "555-0404", "contacto@granossemillas.com",
      "Carretera Norte Km 12", "30 días", false
    );

    Proveedor.guardar(p1);
    Proveedor.guardar(p2);
    Proveedor.guardar(p3);
    Proveedor.guardar(p4);

    System.out.println("   ✔ " + 4 + " proveedores insertados.\n");
  }

  private static void insertarProductos() {
    System.out.println("── Insertando productos...");

    ProveedorDTO provGranos = Proveedor.obtenerPorCodigo("PROV-001");
    ProveedorDTO provAceites = Proveedor.obtenerPorCodigo("PROV-002");
    ProveedorDTO provLacteos = Proveedor.obtenerPorCodigo("PROV-003");

    List<ProductoDTO> productos = List.of(
      new ProductoDTO("7501001000011", "Arroz", BigDecimal.valueOf(28.00), 50, 10, 100, provGranos),
      new ProductoDTO("7501001000028", "Frijol", BigDecimal.valueOf(32.00), 30, 5, 80, provGranos),
      new ProductoDTO("7501001000035", "Azúcar", BigDecimal.valueOf(26.00), 20, 5, 60, provGranos),
      new ProductoDTO("7501002000010", "Aceite", BigDecimal.valueOf(48.00), 15, 3, 50, provAceites),
      new ProductoDTO("7501002000027", "Atún", BigDecimal.valueOf(18.00), 40, 10, 100, provAceites),
      new ProductoDTO("7501003000019", "Leche", BigDecimal.valueOf(30.00), 35, 8, 90, provLacteos),
      new ProductoDTO("7501001000042", "Sal", BigDecimal.valueOf(8.00), 60, 15, 150, provGranos),
      new ProductoDTO("7501004000018", "Café", BigDecimal.valueOf(55.00), 25, 5, 70, provGranos),
      new ProductoDTO("7501005000017", "Jabón", BigDecimal.valueOf(22.00), 45, 10, 120, provAceites),
      new ProductoDTO("7501003000026", "Mantequilla", BigDecimal.valueOf(42.00), 20, 5, 60, provLacteos),
      new ProductoDTO("7501003000033", "Crema", BigDecimal.valueOf(24.00), 18, 5, 50, provLacteos),
      new ProductoDTO("7501001000059", "Maíz", BigDecimal.valueOf(20.00), 40, 10, 90, provGranos)
    );

    for (ProductoDTO p : productos) {
      Producto.guardar(p);
    }

    System.out.println("   ✔ " + productos.size() + " productos insertados.\n");
  }

  private static void insertarUsuarios() {
    System.out.println("── Insertando usuarios...");

    List<UsuarioDTO> usuarios = List.of(
      new UsuarioDTO("erick armenta", "1234", UsuarioRol.ADMINISTRADOR),
      new UsuarioDTO("isaias coronado", "1234", UsuarioRol.CAJERO),
      new UsuarioDTO("miguel angel", "1234", UsuarioRol.ENCARGADO_ALMACEN)
    );

    for (UsuarioDTO u : usuarios) {
      Usuario.guardar(u);
    }

    System.out.println("   ✔ " + usuarios.size() + " usuarios insertados.\n");
  }

  private static void insertarOrdenesCompra() {
    System.out.println("── Insertando órdenes de compra...");

    ProveedorDTO prov1 = Proveedor.obtenerPorCodigo("PROV-001");
    ProveedorDTO prov2 = Proveedor.obtenerPorCodigo("PROV-002");
    ProveedorDTO prov3 = Proveedor.obtenerPorCodigo("PROV-003");

    List<OrdenCompraDTO> ordenes = List.of(
      new OrdenCompraDTO("OC-2026-001", LocalDate.now().toString(), prov1, "Pendiente", 15, BigDecimal.valueOf(12500.00)),
      new OrdenCompraDTO("OC-2026-002", LocalDate.now().minusDays(2).toString(), prov2, "Aprobada", 23, BigDecimal.valueOf(8750.50)),
      new OrdenCompraDTO("OC-2026-003", LocalDate.now().minusDays(5).toString(), prov3, "Recibida", 10, BigDecimal.valueOf(5200.00)),
      new OrdenCompraDTO("OC-2026-004", LocalDate.now().minusDays(1).toString(), prov1, "Pendiente", 8, BigDecimal.valueOf(3400.00)),
      new OrdenCompraDTO("OC-2026-005", LocalDate.now().minusDays(7).toString(), prov2, "Cancelada", 12, BigDecimal.valueOf(6100.75))
    );

    for (OrdenCompraDTO o : ordenes) {
      OrdenCompra.guardar(o);
    }

    System.out.println("   ✔ " + ordenes.size() + " órdenes de compra insertadas.\n");
  }

  private static void insertarVentas() {
    System.out.println("── Insertando ventas...");

    ProductoDTO arroz = Producto.obtenerPorCodigo("7501001000011");
    ProductoDTO frijol = Producto.obtenerPorCodigo("7501001000028");
    ProductoDTO leche = Producto.obtenerPorCodigo("7501003000019");
    ProductoDTO aceite = Producto.obtenerPorCodigo("7501002000010");
    ProductoDTO cafe = Producto.obtenerPorCodigo("7501004000018");

    VentaDTO venta1 = new VentaDTO();
    venta1.agregarProducto(arroz);
    venta1.agregarProducto(arroz);
    venta1.agregarProducto(frijol);
    venta1.setPagada(true);
    venta1.setFolio("TK-" + System.currentTimeMillis());
    venta1.setTipoPago(TipoPago.EFECTIVO);
    venta1.setCajero("erick armenta");

    VentaDTO venta2 = new VentaDTO();
    venta2.agregarProducto(leche);
    venta2.agregarProducto(aceite);
    venta2.agregarProducto(cafe);
    venta2.setPagada(true);
    venta2.setFolio("TK-" + (System.currentTimeMillis() + 1));
    venta2.setTipoPago(TipoPago.TARJETA);
    venta2.setCajero("isaias coronado");

    VentaDTO venta3 = new VentaDTO();
    venta3.agregarProducto(arroz);
    venta3.agregarProducto(leche);
    venta3.agregarProducto(leche);
    venta3.agregarProducto(frijol);
    venta3.setPagada(true);
    venta3.setFolio("TK-" + (System.currentTimeMillis() + 2));
    venta3.setTipoPago(TipoPago.TRANSACCION); // Transferencia
    venta3.setCajero("miguel angel");

    Venta.guardar(venta1);
    Venta.guardar(venta2);
    Venta.guardar(venta3);

    System.out.println("   ✔ 3 ventas insertadas.\n");
  }

  // ─────────────────────── VERIFICACIÓN ────────────────────────────
  private static void verificarProveedores() {
    System.out.println("── Proveedores en base de datos:");
    List<ProveedorDTO> proveedores = Proveedor.obtenerTodos();
    for (ProveedorDTO p : proveedores) {
      System.out.printf("   [%s] %-25s | Contacto: %-18s | Activo: %s%n",
        p.getCodigo(), p.getNombre(), p.getContacto(), p.isActivo() ? "Sí" : "No");
    }
    System.out.println("   Total: " + proveedores.size() + "\n");
  }

  private static void verificarProductos() {
    System.out.println("── Productos en base de datos:");
    List<ProductoDTO> productos = Producto.obtenerTodos();
    for (ProductoDTO p : productos) {
      System.out.printf("   [%s] %-15s | Precio: $%6.2f | Stock: %3d | Proveedor: %s%n",
        p.getCodigo(), p.getNombre(), p.getPrecio(),
        p.getStock(), p.getProveedor() != null ? p.getProveedor().getNombre() : "N/A");
    }
    System.out.println("   Total: " + productos.size() + "\n");
  }

  private static void verificarUsuarios() {
    System.out.println("── Usuarios en base de datos:");
    List<UsuarioDTO> usuarios = Usuario.obtenerTodos();
    for (UsuarioDTO u : usuarios) {
      System.out.printf("   %-10s | Rol: %s%n", u.getNombre(), u.getRol());
    }
    System.out.println("   Total: " + usuarios.size() + "\n");
  }

  private static void verificarOrdenesCompra() {
    System.out.println("── Órdenes de compra en base de datos:");
    List<OrdenCompraDTO> ordenes = OrdenCompra.obtenerTodas();
    for (OrdenCompraDTO o : ordenes) {
      System.out.printf("   [%s] Fecha: %s | Estado: %-10s | Productos: %2d | Total: $%8.2f | Proveedor: %s%n",
        o.getNumero(), o.getFecha(), o.getEstado(),
        o.getCantidadProductos(), o.getTotal(), o.getProveedorNombre());
    }
    System.out.println("   Total: " + ordenes.size() + "\n");
  }

  private static void verificarVentas() {
    System.out.println("── Ventas en base de datos:");
    List<VentaDTO> ventas = Venta.obtenerTodas();
    for (VentaDTO v : ventas) {
      System.out.printf("   [%s] Unidades: %2d | Subtotal: $%7.2f | IVA: $%6.2f | Total: $%7.2f | Pagada: %s%n",
        v.getFolio(), v.getTotalUnidades(),
        v.getSubtotal(), v.getIva(), v.getTotal(),
        v.isPagada() ? "Sí" : "No");
      for (ItemVentaDTO item : v.getItems()) {
        System.out.printf("      · %-15s x%d  $%.2f%n",
          item.getNombre(), item.getCantidad(), item.getSubtotal());
      }
    }
    System.out.println("   Total: " + ventas.size() + "\n");
  }

}
