package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.ventas.VentasFacade;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class MenuPrincipal extends JFrame {

  private final UsuarioDTO usuarioActivo;

  public MenuPrincipal(UsuarioDTO usuarioActivo) {
    this.usuarioActivo = usuarioActivo;
    setTitle("La Canasta");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    JPanel root = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);

    JPanel card = new JPanel() {
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
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(36, 52, 36, 52));
    card.setPreferredSize(new Dimension(420, 660));

    JPanel panelTitulo = new JPanel();
    panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
    panelTitulo.setOpaque(false);
    panelTitulo.setAlignmentX(LEFT_ALIGNMENT);
    panelTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

    JLabel titulo = new JLabel("La Canasta", SwingConstants.CENTER);
    titulo.setFont(Fuentes.b(30));
    titulo.setForeground(new Color(30, 50, 200));
    titulo.setAlignmentX(CENTER_ALIGNMENT);

    JLabel lblUsuario = new JLabel(
      usuarioActivo.getNombre() + "  ·  " + formatearRol(usuarioActivo.getRol().name()),
      SwingConstants.CENTER);
    lblUsuario.setFont(Fuentes.r(12));
    lblUsuario.setForeground(Colores.GRIS_TEXTO);
    lblUsuario.setAlignmentX(CENTER_ALIGNMENT);

    panelTitulo.add(titulo);
    panelTitulo.add(Box.createVerticalStrut(4));
    panelTitulo.add(lblUsuario);

    card.add(panelTitulo);
    card.add(Box.createVerticalStrut(28));

    card.add(seccionLabel("Ventas"));
    card.add(Box.createVerticalStrut(8));

    JButton btnVender = botonMenuAzul("Vender Producto");
    btnVender.addActionListener(e -> {
      this.setVisible(false);
      Fuentes.cargar();
      new RegistrarVenta(new VentasFacade(), usuarioActivo).setVisible(true);
    });
    card.add(btnVender);
    card.add(Box.createVerticalStrut(20));

    card.add(seccionLabel("Inventario"));
    card.add(Box.createVerticalStrut(8));

    JButton btnExistencia = botonMenuAzul("Existencia de Productos");
    btnExistencia.addActionListener(e -> {
      this.setVisible(false);
      new ExistenciaProductos(this).setVisible(true);
    });
    card.add(btnExistencia);
    card.add(Box.createVerticalStrut(10));

    JButton btnConsolidar = botonMenuAzul("Consolidar Inventario");
    btnConsolidar.addActionListener(e -> {
      this.setVisible(false);
      new ConsolidarInventario(this).setVisible(true);
    });
    card.add(btnConsolidar);
    card.add(Box.createVerticalStrut(20));

    card.add(seccionLabel("Proveedores"));
    card.add(Box.createVerticalStrut(8));

    JButton btnProveedores = botonMenuAzul("Administrar Proveedores");
    btnProveedores.addActionListener(e -> {
      this.setVisible(false);
      new AdministrarProveedores(this).setVisible(true);
    });
    card.add(btnProveedores);
    card.add(Box.createVerticalStrut(10));

    JButton btnOrdenes = botonMenuAzul("Órdenes de Compra");
    btnOrdenes.addActionListener(e -> {
      this.setVisible(false);
      new OrdenesCompras(this).setVisible(true);
    });
    card.add(btnOrdenes);
    card.add(Box.createVerticalStrut(10));

    JButton btnAsociar = botonMenuAzul("Asociar Productos con Proveedores");
    btnAsociar.addActionListener(e -> {
      this.setVisible(false);
      new AsociarProductosProveedores(this).setVisible(true);
    });
    card.add(btnAsociar);
    card.add(Box.createVerticalStrut(24));

    JButton btnCerrar = botonMenuRojo("Cerrar Sesión");
    btnCerrar.addActionListener(e -> {
      dispose();
      new MenuPrincipal(null).setVisible(true);
    });
    card.add(btnCerrar);

    JPanel centrado = new JPanel(new GridBagLayout());
    centrado.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.insets = new Insets(20, 0, 20, 0);
    centrado.add(card, gbc);

    root.add(centrado, new GridBagConstraints());
    setContentPane(root);
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

  JButton botonMenuAzul(String texto) {
    JButton b = new JButton(texto) {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            hover = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            hover = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(14));
    b.setAlignmentX(LEFT_ALIGNMENT);
    b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    b.setPreferredSize(new Dimension(316, 48));
    return b;
  }

  JButton botonMenuRojo(String texto) {
    JButton b = new JButton(texto) {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            hover = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            hover = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? Colores.ROJO_HOVER : Colores.ROJO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(14));
    b.setAlignmentX(LEFT_ALIGNMENT);
    b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    b.setPreferredSize(new Dimension(316, 48));
    return b;
  }

}
