package diseñadores.presentacion.utilidad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public final class Componentes {

  private Componentes() {
  }

  public static JPanel fondoAmarillo() {
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

  public static JPanel tarjetaBlanca(int radio) {
    JPanel p = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, radio, radio));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, radio, radio));
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    return p;
  }

  public static JPanel tarjeta() {
    JPanel p = tarjetaBlanca(18);
    p.setBorder(new EmptyBorder(20, 20, 20, 20));
    return p;
  }

  public static JPanel centrado(JComponent contenido, int margenH, int margenV) {
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

  public static JPanel topBar(JFrame owner) {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));
    JButton btnCS = botonTopBar("Cerrar sesion");
    btnCS.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(owner,
        "¿Cerrar sesion?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (op == JOptionPane.YES_OPTION) {
        System.exit(0);
      }
    });
    bar.add(btnCS);
    return bar;
  }

  public static JPanel panelVolver(String texto, Runnable accion) {
    JButton btn = new JButton(texto);
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setForeground(Colores.TEXTO_OSCURO);
    btn.setFont(Fuentes.r(14));
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.addActionListener(e -> accion.run());
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    row.setOpaque(false);
    row.add(btn);
    return row;
  }

  public static JPanel sepLine() {
    JPanel s = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        super.paintComponent(g2d);
        Graphics2D g = (Graphics2D) g2d;
        g.setColor(Colores.BORDE_GRIS);
        float[] dash = {4f, 4f};
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash, 0));
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
      }

    };
    s.setOpaque(false);
    s.setPreferredSize(new Dimension(0, 2));
    return s;
  }

  public static JLabel etiqueta(String txt, int size, boolean bold, Color color) {
    JLabel l = new JLabel(txt);
    l.setFont(bold ? Fuentes.b(size) : Fuentes.r(size));
    l.setForeground(color);
    return l;
  }

  public static JLabel etiquetaCentrada(String txt, int size, boolean bold, Color color) {
    JLabel l = etiqueta(txt, size, bold, color);
    l.setHorizontalAlignment(SwingConstants.CENTER);
    return l;
  }

  public static JTextField campoPill(String placeholder) {
    JTextField tf = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_INPUT);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    tf.setOpaque(false);
    tf.setBorder(new EmptyBorder(8, 14, 8, 14));
    tf.setFont(Fuentes.r(14));
    aplicarPlaceholder(tf, placeholder);
    return tf;
  }

  public static JTextField campoTexto(String placeholder) {
    JTextField tf = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        g.setColor(Colores.BORDE_GRIS);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        super.paintComponent(g2d);
      }

    };
    tf.setOpaque(false);
    tf.setBorder(new EmptyBorder(12, 14, 12, 14));
    tf.setFont(Fuentes.r(14));
    tf.setPreferredSize(new Dimension(0, 50));
    aplicarPlaceholder(tf, placeholder);
    return tf;
  }

  private static void aplicarPlaceholder(JTextField tf, String placeholder) {
    tf.setText(placeholder);
    tf.setForeground(Colores.GRIS_TEXTO);
    tf.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        if (tf.getText().equals(placeholder)) {
          tf.setText("");
          tf.setForeground(Colores.TEXTO_OSCURO);
        }
      }

      public void focusLost(FocusEvent e) {
        if (tf.getText().isEmpty()) {
          tf.setText(placeholder);
          tf.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });
  }

  public static JButton botonAccion(String texto, Color base, Color hover) {
    JButton b = crearBoton(texto, base, hover, 10);
    b.setFont(Fuentes.b(14));
    return b;
  }

  public static JButton botonTopBar(String texto) {
    JButton b = crearBoton(texto, Colores.AZUL, Colores.AZUL_HOVER, 10);
    b.setFont(Fuentes.b(13));
    b.setPreferredSize(new Dimension(160, 38));
    return b;
  }

  public static JButton botonIcono(String texto, Color baseBg, Color hoverBg, Color colorTexto, int fontSize) {
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
        g.setColor(ov ? hoverBg : baseBg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(colorTexto);
    b.setFont(Fuentes.b(fontSize));
    b.setPreferredSize(new Dimension(32, 32));
    return b;
  }

  private static JButton crearBoton(String texto, Color base, Color hover, int radio) {
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
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radio, radio));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setHorizontalAlignment(SwingConstants.CENTER);
    return b;
  }

}
