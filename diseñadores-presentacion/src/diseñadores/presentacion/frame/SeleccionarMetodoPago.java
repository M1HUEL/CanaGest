package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.VentaDTO;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class SeleccionarMetodoPago extends JFrame {

  private final VentaDTO ventaActual;
  private final JFrame frame;
  private final IVentas fachada;
  private final double total;
  private final Runnable onVentaFinalizada;

  private static final Map<String, Color[]> COLORES_METODO = new LinkedHashMap<>();

  static {
    COLORES_METODO.put("Efectivo", new Color[]{Colores.VERDE_METODO, Colores.VERDE_METODO_H});
    COLORES_METODO.put("Tarjeta", new Color[]{Colores.AZUL, Colores.AZUL_HOVER});
    COLORES_METODO.put("CoDi", new Color[]{Colores.MORADO, Colores.MORADO_HOVER});
    COLORES_METODO.put("Transferencia", new Color[]{Colores.NARANJA, Colores.NARANJA_HOVER});
  }

  public SeleccionarMetodoPago(JFrame frame, IVentas fachada,
    VentaDTO ventaActual, double total,
    Runnable onVentaFinalizada) {
    super("Metodo de pago");
    this.ventaActual = ventaActual;
    this.frame = frame;
    this.fachada = fachada;
    this.total = total;
    this.onVentaFinalizada = onVentaFinalizada;

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(frame.getWidth(), frame.getHeight());
    setLocation(frame.getLocation());

    JPanel root = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);
    root.add(crearTopBar(), BorderLayout.NORTH);
    root.add(crearCentrado(buildCard(), 280, 30), BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel crearTopBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));

    JButton btnMenu = new JButton("Menu Principal") {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            ov = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            ov = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? Colores.AMARILLO_BTN_HOVER : Colores.AMARILLO_BTN);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    btnMenu.setForeground(Colores.TEXTO_OSCURO);
    btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btnMenu.setPreferredSize(new Dimension(160, 38));
    btnMenu.addActionListener(e -> {
      dispose();
      new MenuPrincipal(null).setVisible(true);
    });

    bar.add(btnMenu);
    return bar;
  }

  private JPanel crearCentrado(JComponent contenido, int margenH, int margenV) {
    JPanel c = new JPanel(new GridBagLayout());
    c.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(margenV, margenH, margenV, margenH);
    c.add(contenido, gbc);
    return c;
  }

  private JPanel buildCard() {
    JPanel card = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, 22, 22));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 22, 22));
        super.paintComponent(g2d);
      }

    };
    card.setOpaque(false);
    card.setLayout(new BorderLayout(0, 20));
    card.setBorder(new EmptyBorder(32, 32, 32, 32));

    JLabel titulo = new JLabel("Seleccione el metodo de pago", SwingConstants.CENTER);
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
    titulo.setForeground(Colores.TEXTO_OSCURO);

    card.add(titulo, BorderLayout.NORTH);
    card.add(gridMetodos(), BorderLayout.CENTER);
    card.add(botonesInferiores(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel gridMetodos() {
    JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
    grid.setOpaque(false);
    COLORES_METODO.forEach((nombre, colores) -> grid.add(botonMetodo(nombre, colores[0], colores[1])));
    return grid;
  }

  private JPanel botonMetodo(String nombre, Color base, Color hover) {
    JPanel btn = new JPanel(new GridLayout(2, 1, 0, 8)) {
      boolean ov = false;

      {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent e) {
            ov = true;
            repaint();
          }

          @Override
          public void mouseExited(MouseEvent e) {
            ov = false;
            repaint();
          }

          @Override
          public void mouseClicked(MouseEvent e) {
            seleccionarMetodo(nombre);
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? hover : base);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
        super.paintComponent(g2d);
      }

    };
    btn.setBorder(new EmptyBorder(28, 20, 28, 20));
    btn.add(new JLabel("", SwingConstants.CENTER));

    JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
    lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 20));
    lblNombre.setForeground(Colores.BLANCO);
    btn.add(lblNombre);
    return btn;
  }

  private JPanel botonesInferiores() {
    JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
    row.setOpaque(false);
    row.setPreferredSize(new Dimension(0, 60));

    JButton btnCancelar = crearBotonAccion("Cancelar", Colores.ROJO, Colores.ROJO_HOVER);
    btnCancelar.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this, "¿Cancelar la venta?", "Cancelar",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (op == JOptionPane.YES_OPTION) {
        volver();
      }
    });

    JButton btnVolver = crearBotonAccion("Volver", Colores.GRIS_BTN, Colores.GRIS_BTN_HOVER);
    btnVolver.addActionListener(e -> volver());

    row.add(btnCancelar);
    row.add(btnVolver);
    return row;
  }

  private void seleccionarMetodo(String nombre) {
    this.setVisible(false);
    switch (nombre) {
      case "Efectivo" ->
        new RegistrarMetodoPagoEfectivo(this, frame, fachada, ventaActual, total, onVentaFinalizada);
      default -> {
        JOptionPane.showMessageDialog(frame,
          "El metodo '" + nombre + "' no esta disponible aun.",
          "No disponible", JOptionPane.INFORMATION_MESSAGE);
        this.setVisible(true);
      }
    }
  }

  void volver() {
    dispose();
    frame.setVisible(true);
  }

  private JButton crearBotonAccion(String texto, Color base, Color hover) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            ov = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            ov = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? hover : base);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(new Font("Segoe UI", Font.BOLD, 14));
    b.setHorizontalAlignment(SwingConstants.CENTER);
    return b;
  }

}
