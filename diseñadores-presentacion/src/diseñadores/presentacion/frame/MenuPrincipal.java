package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Botones;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MenuPrincipal extends JFrame {

  private final UsuarioDTO usuarioActivo;
  private final VentasControl control;

  public MenuPrincipal(UsuarioDTO usuarioActivo, VentasControl control) {
    this.usuarioActivo = usuarioActivo;
    this.control = control;

    configurarVentana();
    inicializarComponentes();
  }

  private void configurarVentana() {
    setTitle("La Canasta");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
  }

  private void inicializarComponentes() {
    JPanel root = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);

    JPanel tarjeta = crearTarjetaPrincipal();

    JPanel centrado = new JPanel(new GridBagLayout());
    centrado.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.insets = new Insets(20, 0, 20, 0);
    centrado.add(tarjeta, gbc);

    root.add(centrado, new GridBagConstraints());
    setContentPane(root);
  }

  private JPanel crearTarjetaPrincipal() {
    JPanel tarjeta = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(4, 5, getWidth() - 5, getHeight() - 4, 28, 28));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 28, 28));
        super.paintComponent(g2d);
      }

    };

    tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
    tarjeta.setOpaque(false);
    tarjeta.setBorder(new EmptyBorder(36, 52, 36, 52));
    tarjeta.setPreferredSize(new Dimension(420, 720));

    agregarCabecera(tarjeta);
    agregarSeccionVentas(tarjeta);
    agregarSeccionInventario(tarjeta);
    agregarSeccionProveedores(tarjeta);
    agregarSeccionSesion(tarjeta);

    return tarjeta;
  }

  private void agregarCabecera(JPanel tarjeta) {
    JPanel panelTitulo = new JPanel();
    panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
    panelTitulo.setOpaque(false);
    panelTitulo.setAlignmentX(LEFT_ALIGNMENT);
    panelTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

    JLabel titulo = new JLabel("La Canasta", SwingConstants.CENTER);
    titulo.setFont(Fuentes.b(30));
    titulo.setForeground(new Color(30, 50, 200));
    titulo.setAlignmentX(CENTER_ALIGNMENT);

    String infoUsuario = usuarioActivo.getNombre() + "  ·  " + formatearRol(usuarioActivo.getRol().name());
    JLabel lblUsuario = new JLabel(infoUsuario, SwingConstants.CENTER);
    lblUsuario.setFont(Fuentes.r(12));
    lblUsuario.setForeground(Colores.GRIS_TEXTO);
    lblUsuario.setAlignmentX(CENTER_ALIGNMENT);

    panelTitulo.add(titulo);
    panelTitulo.add(Box.createVerticalStrut(4));
    panelTitulo.add(lblUsuario);

    tarjeta.add(panelTitulo);
    tarjeta.add(Box.createVerticalStrut(28));
  }

  private void agregarSeccionVentas(JPanel tarjeta) {
    tarjeta.add(seccionLabel("Ventas"));
    tarjeta.add(Box.createVerticalStrut(8));

    JButton btnVender = Botones.menuAzul("Vender Producto");
    btnVender.addActionListener(e -> {
      this.setVisible(false);
      Fuentes.cargar();
      new RegistrarVenta(control).setVisible(true);
    });

    JButton btnHistorial = Botones.menuAzul("Historial de Ventas");
    btnHistorial.addActionListener(e -> {
      this.setVisible(false);
      new HistorialVentas(this, control).setVisible(true);
    });

    tarjeta.add(btnVender);
    tarjeta.add(Box.createVerticalStrut(10));
    tarjeta.add(btnHistorial);
    tarjeta.add(Box.createVerticalStrut(20));
  }

  private void agregarSeccionInventario(JPanel tarjeta) {
    tarjeta.add(seccionLabel("Inventario"));
    tarjeta.add(Box.createVerticalStrut(8));

    JButton btnExistencia = Botones.menuAzul("Existencia de Productos");
    btnExistencia.addActionListener(e -> {
      this.setVisible(false);
      new ExistenciaProductos(this, control).setVisible(true);
    });

    JButton btnConsolidar = Botones.menuAzul("Consolidar Inventario");
    btnConsolidar.addActionListener(e -> {
      this.setVisible(false);
      new ConsolidarInventario(this, control).setVisible(true);
    });

    tarjeta.add(btnExistencia);
    tarjeta.add(Box.createVerticalStrut(10));
    tarjeta.add(btnConsolidar);
    tarjeta.add(Box.createVerticalStrut(20));
  }

  private void agregarSeccionProveedores(JPanel tarjeta) {
    tarjeta.add(seccionLabel("Proveedores"));
    tarjeta.add(Box.createVerticalStrut(8));

    JButton btnProveedores = Botones.menuAzul("Administrar Proveedores");
    btnProveedores.addActionListener(e -> {
      this.setVisible(false);
      new AdministrarProveedores(this, control).setVisible(true);
    });

    JButton btnOrdenes = Botones.menuAzul("Órdenes de Compra");
    btnOrdenes.addActionListener(e -> {
      this.setVisible(false);
      new OrdenesCompras(this, control).setVisible(true);
    });

    JButton btnAsociar = Botones.menuAzul("Asociar Productos con Proveedores");
    btnAsociar.addActionListener(e -> {
      this.setVisible(false);
      new AsociarProveedorProducto(
        this,
        control
      ).setVisible(true);
    });

    tarjeta.add(btnProveedores);
    tarjeta.add(Box.createVerticalStrut(10));
    tarjeta.add(btnOrdenes);
    tarjeta.add(Box.createVerticalStrut(10));
    tarjeta.add(btnAsociar);
    tarjeta.add(Box.createVerticalStrut(24));
  }

  private void agregarSeccionSesion(JPanel tarjeta) {
    JButton btnCerrar = Botones.menuRojo("Cerrar Sesión");
    btnCerrar.addActionListener(e -> {
      dispose();
      new PantallaAutenticacion(
        this.control
      ).setVisible(true);
    });
    tarjeta.add(btnCerrar);
  }

  private String formatearRol(String rol) {
    return switch (rol) {
      case "ADMINISTRADOR" ->
        "Administrador";
      case "CAJERO" ->
        "Cajero";
      case "ENCARGADO_ALMACEN" ->
        "Encargado de Almacén";
      default ->
        rol;
    };
  }

  private JLabel seccionLabel(String texto) {
    JLabel lbl = new JLabel(texto.toUpperCase());
    lbl.setFont(Fuentes.b(10));
    lbl.setForeground(new Color(160, 163, 175));
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    lbl.setHorizontalAlignment(SwingConstants.LEFT);
    lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
    return lbl;
  }

}
