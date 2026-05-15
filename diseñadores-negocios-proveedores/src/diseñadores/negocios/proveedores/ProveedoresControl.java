package diseñadores.negocios.proveedores;

import diseñadores.infraestructura.notificaciones.INotificaciones;
import diseñadores.infraestructura.notificaciones.NotificacionesFacade;
import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.negocios.objetos.OrdenCompra;
import diseñadores.negocios.objetos.Proveedor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProveedoresControl {

  private final INotificaciones notificaciones;

  public ProveedoresControl() {
    this.notificaciones = new NotificacionesFacade();
  }

  public ProveedoresControl(INotificaciones notificaciones) {
    this.notificaciones = notificaciones;
  }

  public List<ProveedorDTO> obtenerTodos() {
    return Proveedor.obtenerTodos();
  }

  public ProveedorDTO obtenerPorCodigo(String codigo) {
    validarCodigoRequerido(codigo);

    return ejecutarObtencionPorCodigo(codigo);
  }

  public int contarActivos() {
    return (int) obtenerTodos().stream()
      .filter(ProveedorDTO::isActivo)
      .count();
  }

  public void guardar(ProveedorDTO proveedor) {
    validarProveedorNoNulo(proveedor);
    validarDatosObligatoriosProveedor(proveedor);

    asignarNuevoCodigo(proveedor);
    ejecutarGuardadoProveedor(proveedor);
  }

  public void actualizar(ProveedorDTO proveedor) {
    validarProveedorNoNulo(proveedor);
    validarCodigoObligatorio(proveedor.getCodigo());
    validarNombreObligatorio(proveedor.getNombre());
    validarExistenciaProveedor(proveedor.getCodigo());

    ejecutarActualizacionProveedor(proveedor);
  }

  public List<OrdenCompraDTO> obtenerOrdenesCompra() {
    return OrdenCompra.obtenerTodas();
  }

  public OrdenCompraDTO obtenerOrdenPorNumero(String numero) {
    validarNumeroOrdenRequerido(numero);

    OrdenCompraDTO orden = ejecutarObtencionOrden(numero);
    validarExistenciaOrden(orden, numero);

    return orden;
  }

  public void guardarOrdenCompra(ProveedorDTO proveedor, int cantidadProductos, BigDecimal total) {
    validarProveedorNoNulo(proveedor);
    validarCantidadProductos(cantidadProductos);
    validarTotalOrden(total);
    validarExistenciaProveedor(proveedor.getCodigo());

    OrdenCompraDTO orden = crearNuevaOrden(proveedor, cantidadProductos, total);
    ejecutarGuardadoOrden(orden);
    enviarCorreoNuevaOrden(orden);
  }

  public void actualizarOrdenCompra(OrdenCompraDTO orden) {
    validarOrdenNoNula(orden);
    validarNumeroOrdenRequerido(orden.getNumero());
    validarExistenciaOrdenPorNumero(orden.getNumero());

    ejecutarActualizacionOrden(orden);
  }

  public void cambiarEstadoOrden(String numero, String nuevoEstado) {
    validarNumeroOrdenRequerido(numero);
    validarEstadoRequerido(nuevoEstado);

    OrdenCompraDTO orden = ejecutarObtencionOrden(numero);
    validarExistenciaOrden(orden, numero);
    validarEstadoValido(nuevoEstado);

    actualizarEstadoEnOrden(orden, nuevoEstado);
    ejecutarActualizacionOrden(orden);
  }

  private void validarCodigoRequerido(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("El código del proveedor no puede estar vacío.");
    }
  }

  private void validarProveedorNoNulo(ProveedorDTO proveedor) {
    if (proveedor == null) {
      throw new IllegalArgumentException("El proveedor no puede ser nulo.");
    }
  }

  private void validarDatosObligatoriosProveedor(ProveedorDTO proveedor) {
    validarNombreObligatorio(proveedor.getNombre());
    if (proveedor.getContacto() == null || proveedor.getContacto().isBlank()) {
      throw new IllegalArgumentException("El contacto del proveedor es obligatorio.");
    }
    if (proveedor.getTelefono() == null || proveedor.getTelefono().isBlank()) {
      throw new IllegalArgumentException("El teléfono del proveedor es obligatorio.");
    }
    if (proveedor.getEmail() == null || proveedor.getEmail().isBlank()) {
      throw new IllegalArgumentException("El email del proveedor es obligatorio.");
    }
  }

  private void validarNombreObligatorio(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
    }
  }

  private void validarCodigoObligatorio(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw new IllegalArgumentException("El código del proveedor es obligatorio.");
    }
  }

  private void validarExistenciaProveedor(String codigo) {
    if (ejecutarObtencionPorCodigo(codigo) == null) {
      throw new IllegalStateException("No existe un proveedor con el código: " + codigo);
    }
  }

  private void validarNumeroOrdenRequerido(String numero) {
    if (numero == null || numero.isBlank()) {
      throw new IllegalArgumentException("El número de orden no puede estar vacío.");
    }
  }

  private void validarExistenciaOrden(OrdenCompraDTO orden, String numero) {
    if (orden == null) {
      throw new IllegalStateException("No existe una orden con el número: " + numero);
    }
  }

  private void validarExistenciaOrdenPorNumero(String numero) {
    if (ejecutarObtencionOrden(numero) == null) {
      throw new IllegalStateException("No existe una orden con el número: " + numero);
    }
  }

  private void validarCantidadProductos(int cantidad) {
    if (cantidad <= 0) {
      throw new IllegalArgumentException("La cantidad de productos debe ser mayor a cero.");
    }
  }

  private void validarTotalOrden(BigDecimal total) {
    if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El total de la orden debe ser mayor a cero.");
    }
  }

  private void validarOrdenNoNula(OrdenCompraDTO orden) {
    if (orden == null) {
      throw new IllegalArgumentException("La orden no puede ser nula.");
    }
  }

  private void validarEstadoRequerido(String estado) {
    if (estado == null || estado.isBlank()) {
      throw new IllegalArgumentException("El nuevo estado no puede estar vacío.");
    }
  }

  private void validarEstadoValido(String estado) {
    List<String> estadosValidos = List.of("Pendiente", "Aprobada", "Recibida", "Cancelada");
    if (!estadosValidos.contains(estado)) {
      throw new IllegalArgumentException("Estado inválido. Los estados válidos son: " + estadosValidos);
    }
  }

  private void asignarNuevoCodigo(ProveedorDTO proveedor) {
    proveedor.setCodigo(generarCodigoProveedor());
  }

  private void actualizarEstadoEnOrden(OrdenCompraDTO orden, String estado) {
    orden.setEstado(estado);
  }

  private ProveedorDTO ejecutarObtencionPorCodigo(String codigo) {
    return Proveedor.obtenerPorCodigo(codigo);
  }

  private void ejecutarGuardadoProveedor(ProveedorDTO proveedor) {
    Proveedor.guardar(proveedor);
  }

  private void ejecutarActualizacionProveedor(ProveedorDTO proveedor) {
    Proveedor.actualizar(proveedor);
  }

  private OrdenCompraDTO ejecutarObtencionOrden(String numero) {
    return OrdenCompra.obtenerPorNumero(numero);
  }

  private void ejecutarGuardadoOrden(OrdenCompraDTO orden) {
    OrdenCompra.guardar(orden);
  }

  private void ejecutarActualizacionOrden(OrdenCompraDTO orden) {
    OrdenCompra.actualizar(orden);
  }

  private OrdenCompraDTO crearNuevaOrden(ProveedorDTO proveedor, int cantidad, BigDecimal total) {
    ProveedorDTO provRef = new ProveedorDTO(
      proveedor.getNombre(), proveedor.getCodigo(),
      proveedor.getContacto(), proveedor.getTelefono(), proveedor.getEmail(),
      proveedor.getDireccion(), proveedor.getTerminosPago(), proveedor.isActivo()
    );

    return new OrdenCompraDTO(
      generarNumeroOrden(),
      LocalDate.now().toString(),
      provRef,
      "Pendiente",
      cantidad,
      total
    );
  }

  private String generarCodigoProveedor() {
    return "PROV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private String generarNumeroOrden() {
    return String.format("OC-%d-%s",
      LocalDate.now().getYear(),
      UUID.randomUUID().toString().substring(0, 8).toUpperCase());
  }

  public void eliminarProveedor(String codigo) {
    validarCodigoRequerido(codigo);
    validarExistenciaProveedor(codigo);
    Proveedor.eliminar(codigo);
  }

  private void enviarCorreoNuevaOrden(OrdenCompraDTO orden) {
    try {
      String emailProveedor = orden.getProveedor().getEmail();
      String nombreProveedor = orden.getProveedor().getNombre();
      String numeroOrden = orden.getNumero();
      String fecha = orden.getFecha();
      String productos = String.valueOf(orden.getCantidadProductos());
      String total = String.format("$%.2f", orden.getTotal().doubleValue());
      String estado = orden.getEstado();

      String cuerpoHtml = """
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }
                        .contenedor { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
                        .encabezado { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }
                        .encabezado h1 { margin: 0; font-size: 22px; }
                        .cuerpo { padding: 30px; }
                        .fila { margin-bottom: 12px; }
                        .etiqueta { font-weight: bold; color: #34495e; display: inline-block; width: 130px; }
                        .valor { color: #2c3e50; }
                        .detalle { background-color: #f0f8ff; border-left: 4px solid #3498db; padding: 12px; margin: 20px 0; }
                        .btn { display: inline-block; padding: 12px 25px; background-color: #27ae60; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }
                        .pie { background: #ecf0f1; padding: 15px; text-align: center; font-size: 12px; color: #7f8c8d; }
                    </style>
                </head>
                <body>
                    <div class="contenedor">
                        <div class="encabezado">
                            <h1>📦 Nueva Orden de Compra</h1>
                        </div>
                        <div class="cuerpo">
                            <p>Estimado proveedor <strong>%s</strong>,</p>
                            <p>Le informamos que se ha generado una nueva orden de compra desde <strong>La Canasta</strong>.</p>
                            <div class="fila"><span class="etiqueta">Orden No.:</span><span class="valor">%s</span></div>
                            <div class="fila"><span class="etiqueta">Fecha:</span><span class="valor">%s</span></div>
                            <div class="fila"><span class="etiqueta">Productos:</span><span class="valor">%s artículo(s)</span></div>
                            <div class="fila"><span class="etiqueta">Total:</span><span class="valor" style="font-weight:bold; color:#27ae60;">%s</span></div>
                            <div class="fila"><span class="etiqueta">Estado:</span><span class="valor">%s</span></div>
                            <div class="detalle">
                                Por favor, revise los detalles en el portal de proveedores o contacte a nuestro departamento de compras para cualquier aclaración.
                            </div>
                            <p>Puede comunicarse con nosotros al teléfono <strong>(555) 123-4567</strong> o al correo <a href="mailto:compras@lacanasta.com">compras@lacanasta.com</a>.</p>
                            <a href="#" class="btn">Ir al portal del proveedor</a>
                        </div>
                        <div class="pie">
                            Este mensaje fue generado automáticamente por el sistema La Canasta. No responda a este correo.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(nombreProveedor, numeroOrden, fecha, productos, total, estado);

      notificaciones.enviarNotificacionStock(emailProveedor, cuerpoHtml, true);
    } catch (Exception e) {
      // Loguear el error sin detener el flujo principal
      System.err.println("❌ No se pudo enviar el correo de la orden: " + e.getMessage());
    }
  }

}
