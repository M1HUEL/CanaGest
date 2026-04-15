package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.Venta;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SeleccionarMetodoPago extends JFrame {

  private final JFrame owner;
  private final IVentas facade;
  private final Venta ventaActual;
  private final double total;
  private final Runnable onVentaFinalizada;

  public SeleccionarMetodoPago(JFrame owner, IVentas facade,
    Venta ventaActual, double total,
    Runnable onVentaFinalizada) {
    super("Metodo de pago");
    this.owner = owner;
    this.facade = facade;
    this.ventaActual = ventaActual;
    this.total = total;
    this.onVentaFinalizada = onVentaFinalizada;

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(owner.getWidth(), owner.getHeight());
    setLocation(owner.getLocation());
    construirUI();
    setVisible(true);
  }

  private void construirUI() {
    JPanel root = fondoAmarillo();
    root.add(topBar(), BorderLayout.NORTH);

    JPanel card = tarjetaBlanca();
    card.setLayout(new BorderLayout(0, 20));
    card.setBorder(new EmptyBorder(32, 32, 32, 32));

    JLabel titulo = new JLabel("Seleccione el metodo de pago", SwingConstants.CENTER);
    titulo.setFont(Fuentes.b(22));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    card.add(titulo, BorderLayout.NORTH);

    JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
    grid.setOpaque(false);
    grid.add(botonMetodo("Efectivo", Colores.VERDE_METODO, Colores.VERDE_METODO_H));
    grid.add(botonMetodo("Tarjeta", Colores.AZUL, Colores.AZUL_HOVER));
    grid.add(botonMetodo("CoDi", Colores.MORADO, Colores.MORADO_HOVER));
    grid.add(botonMetodo("Transferencia", Colores.NARANJA, Colores.NARANJA_HOVER));
    card.add(grid, BorderLayout.CENTER);

    JPanel botones = new JPanel(new GridLayout(1, 2, 16, 0));
    botones.setOpaque(false);
    botones.setPreferredSize(new Dimension(0, 60));
    JButton btnCancelar = accionBtn("Cancelar", Colores.ROJO, Colores.ROJO_HOVER);
    JButton btnVolver = accionBtn("Volver", Colores.GRIS_BTN, Colores.GRIS_BTN_HOVER);
    btnCancelar.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this, "¿Cancelar la venta?", "Cancelar",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (op == JOptionPane.YES_OPTION) {
        dispose();
        owner.setVisible(true);
      }
    });
    btnVolver.addActionListener(e -> {
      dispose();
      owner.setVisible(true);
    });
    botones.add(btnCancelar);
    botones.add(btnVolver);
    card.add(botones, BorderLayout.SOUTH);

    JPanel centrado = centrado(card, 280, 30);
    root.add(centrado, BorderLayout.CENTER);
    setContentPane(root);
  }

  private JPanel botonMetodo(String nombre, Color base, Color hover) {
    JPanel btn = new JPanel(new GridLayout(2, 1, 0, 8)) {
      boolean ov = false;

      {
        setOpaque(false);
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
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
        super.paintComponent(g2d);
      }

    };
    btn.setBorder(new EmptyBorder(28, 20, 28, 20));
    btn.add(new JLabel("", SwingConstants.CENTER));
    JLabel lNom = new JLabel(nombre, SwingConstants.CENTER);
    lNom.setFont(Fuentes.b(20));
    lNom.setForeground(Colores.BLANCO);
    btn.add(lNom);
    return btn;
  }

  private void seleccionarMetodo(String nombre) {
    this.setVisible(false);
    switch (nombre) {
      case "Efectivo" -> new RegistrarMetodoPagoEfectivo(this, owner, facade,
          ventaActual, total, onVentaFinalizada);
//      case "Tarjeta" -> new RegistrarMetodoPagoTarjeta(this, owner, facade,
//          ventaActual, total, onVentaFinalizada);
//      case "Transferencia" -> new RegistrarMetodoPagoTransferencia(this, owner, facade,
//          ventaActual, total, onVentaFinalizada);
      default -> {
        JOptionPane.showMessageDialog(owner,
          "El metodo " + nombre + " no esta disponible aun.",
          "No disponible", JOptionPane.INFORMATION_MESSAGE);
        this.setVisible(true);
      }
    }
  }

  // ── Helpers compartidos ───────────────────────────────────
  JPanel fondoAmarillo() {
    JPanel p = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    p.setOpaque(false);
    return p;
  }

  JPanel tarjetaBlanca() {
    return new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(4, 5, getWidth() - 5, getHeight() - 4, 22, 22));
        g.setColor(Colores.BLANCO);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 22, 22));
        super.paintComponent(g2d);
      }

    };
  }

  JPanel centrado(JPanel card, int margenH, int margenV) {
    JPanel c = new JPanel(new GridBagLayout());
    c.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(margenV, margenH, margenV, margenH);
    c.add(card, gbc);
    return c;
  }

  JPanel topBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));
    JButton btnCS = topBarBtn("Cerrar sesion");
    btnCS.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this, "¿Cerrar sesion?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (op == JOptionPane.YES_OPTION) {
        System.exit(0);
      }
    });
    bar.add(btnCS);
    return bar;
  }

  JButton accionBtn(String texto, Color base, Color hover) {
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
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(15));
    b.setHorizontalAlignment(SwingConstants.CENTER);
    return b;
  }

  JButton topBarBtn(String texto) {
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
        g.setColor(ov ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(13));
    b.setPreferredSize(new Dimension(160, 38));
    return b;
  }

}
