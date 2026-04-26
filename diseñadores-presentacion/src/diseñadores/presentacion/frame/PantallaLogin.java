package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class PantallaLogin extends JFrame {

  private IUsuarios fachada;

  private static final String USUARIO = "admin";
  private static final String CONTRASENA = "1234";

  public PantallaLogin(IUsuarios fachada) {
    this.fachada = fachada;

    setTitle("La Canasta - Iniciar Sesión");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    JPanel root = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        super.paintComponent(g2d);
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(
          0, 0, new Color(255, 240, 130),
          getWidth(), getHeight(), new Color(255, 215, 60));
        g.setPaint(gp);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);

    JPanel card = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0, 0, 0, 28));
        g.fill(new RoundRectangle2D.Float(5, 7, getWidth() - 7, getHeight() - 6, 28, 28));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 28, 28));
        super.paintComponent(g2d);
      }

    };
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(48, 52, 48, 52));
    card.setPreferredSize(new Dimension(420, 460));

    JLabel lblTitulo = new JLabel("La Canasta", SwingConstants.CENTER);
    lblTitulo.setFont(Fuentes.b(30));
    lblTitulo.setForeground(new Color(30, 50, 200));
    lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

    JLabel lblSubtitulo = new JLabel("Inicie sesión para continuar", SwingConstants.CENTER);
    lblSubtitulo.setFont(Fuentes.r(14));
    lblSubtitulo.setForeground(Colores.GRIS_TEXTO);
    lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

    JLabel lblUsuario = new JLabel("Usuario");
    lblUsuario.setFont(Fuentes.b(13));
    lblUsuario.setForeground(Colores.TEXTO_OSCURO);
    lblUsuario.setAlignmentX(LEFT_ALIGNMENT);

    JTextField campoUsuario = campoLogin("Ingrese su usuario", false);
    campoUsuario.setAlignmentX(LEFT_ALIGNMENT);
    campoUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

    JLabel lblContrasena = new JLabel("Contraseña");
    lblContrasena.setFont(Fuentes.b(13));
    lblContrasena.setForeground(Colores.TEXTO_OSCURO);
    lblContrasena.setAlignmentX(LEFT_ALIGNMENT);

    JPasswordField campoContrasena = (JPasswordField) campoLogin("Ingrese su contraseña", true);
    campoContrasena.setAlignmentX(LEFT_ALIGNMENT);
    campoContrasena.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

    JLabel lblError = new JLabel(" ", SwingConstants.CENTER);
    lblError.setFont(Fuentes.r(12));
    lblError.setForeground(Colores.ROJO);
    lblError.setAlignmentX(CENTER_ALIGNMENT);

    JButton btnIniciar = new JButton("Iniciar Sesión") {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          @Override
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
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    btnIniciar.setForeground(Colores.BLANCO);
    btnIniciar.setFont(Fuentes.b(16));
    btnIniciar.setAlignmentX(CENTER_ALIGNMENT);
    btnIniciar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

    Runnable accionLogin = () -> {
      String usuario = campoUsuario.getText().trim();
      String contrasena = new String(campoContrasena.getPassword()).trim();

      UsuarioDTO usuarioDTO = fachada.autenticarse(usuario, contrasena);
      if (usuarioDTO != null) {
        dispose();
        new MenuPrincipal(usuarioDTO).setVisible(true);
      } else {
        lblError.setText("Usuario o contraseña incorrectos.");
        campoContrasena.setText("");
        campoContrasena.requestFocus();
      }
    };

    btnIniciar.addActionListener(e -> accionLogin.run());
    campoContrasena.addActionListener(e -> accionLogin.run());
    campoUsuario.addActionListener(e -> campoContrasena.requestFocus());

    card.add(lblTitulo);
    card.add(Box.createVerticalStrut(6));
    card.add(lblSubtitulo);
    card.add(Box.createVerticalStrut(32));
    card.add(lblUsuario);
    card.add(Box.createVerticalStrut(6));
    card.add(campoUsuario);
    card.add(Box.createVerticalStrut(18));
    card.add(lblContrasena);
    card.add(Box.createVerticalStrut(6));
    card.add(campoContrasena);
    card.add(Box.createVerticalStrut(10));
    card.add(lblError);
    card.add(Box.createVerticalStrut(10));
    card.add(btnIniciar);

    root.add(card, new GridBagConstraints());
    setContentPane(root);
  }

  JTextField campoLogin(String placeholder, boolean esContrasena) {
    JTextField tf = esContrasena
      ? new JPasswordField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 249, 252));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    }
      : new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 249, 252));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };

    tf.setOpaque(false);
    tf.setBorder(BorderFactory.createCompoundBorder(
      new RoundedLineBorder(new Color(213, 218, 230), 1, 10),
      new EmptyBorder(10, 16, 10, 16)));
    tf.setFont(Fuentes.r(14));
    tf.setForeground(Colores.GRIS_TEXTO);
    tf.setPreferredSize(new Dimension(0, 50));

    if (esContrasena) {
      JPasswordField pf = (JPasswordField) tf;
      pf.setEchoChar((char) 0);
      pf.setText(placeholder);
      pf.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
          if (new String(pf.getPassword()).equals(placeholder)) {
            pf.setText("");
            pf.setEchoChar('\u2022');
            pf.setForeground(Colores.TEXTO_OSCURO);
          }
        }

        @Override
        public void focusLost(FocusEvent e) {
          if (new String(pf.getPassword()).isEmpty()) {
            pf.setEchoChar((char) 0);
            pf.setText(placeholder);
            pf.setForeground(Colores.GRIS_TEXTO);
          }
        }

      });
    } else {
      tf.setText(placeholder);
      tf.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
          if (tf.getText().equals(placeholder)) {
            tf.setText("");
            tf.setForeground(Colores.TEXTO_OSCURO);
          }
        }

        @Override
        public void focusLost(FocusEvent e) {
          if (tf.getText().isEmpty()) {
            tf.setText(placeholder);
            tf.setForeground(Colores.GRIS_TEXTO);
          }
        }

      });
    }
    return tf;
  }

  static class RoundedLineBorder extends AbstractBorder {

    private final Color color;
    private final int thickness;
    private final int arc;

    RoundedLineBorder(Color color, int thickness, int arc) {
      this.color = color;
      this.thickness = thickness;
      this.arc = arc;
    }

    @Override
    public void paintBorder(Component c, Graphics g2d, int x, int y, int w, int h) {
      Graphics2D g = (Graphics2D) g2d.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setColor(color);
      g.setStroke(new BasicStroke(thickness));
      g.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, w - 1, h - 1, arc, arc));
      g.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
      return new Insets(thickness, thickness, thickness, thickness);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets i) {
      i.set(thickness, thickness, thickness, thickness);
      return i;
    }

  }
}
