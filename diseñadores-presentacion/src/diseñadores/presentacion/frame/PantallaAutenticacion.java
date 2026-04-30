package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class PantallaAutenticacion extends JFrame {

  private final IUsuarios fachada;

  public PantallaAutenticacion(IUsuarios fachada) {
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

    JPanel tarjeta = new JPanel() {
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
    tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
    tarjeta.setOpaque(false);
    tarjeta.setBorder(new EmptyBorder(48, 52, 48, 52));
    tarjeta.setPreferredSize(new Dimension(420, 460));

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

    JPanel usuarioRow = new JPanel();
    usuarioRow.setLayout(new BoxLayout(usuarioRow, BoxLayout.Y_AXIS));
    usuarioRow.setOpaque(false);
    usuarioRow.setAlignmentX(LEFT_ALIGNMENT);
    usuarioRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
    usuarioRow.add(lblUsuario);
    usuarioRow.add(Box.createVerticalStrut(6));
    usuarioRow.add(campoUsuario);

    JLabel lblContrasena = new JLabel("Contraseña");
    lblContrasena.setFont(Fuentes.b(13));
    lblContrasena.setForeground(Colores.TEXTO_OSCURO);
    lblContrasena.setAlignmentX(LEFT_ALIGNMENT);

    JPasswordField campoContrasena = (JPasswordField) campoLogin("Ingrese su contraseña", true);
    campoContrasena.setAlignmentX(LEFT_ALIGNMENT);
    campoContrasena.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

    JPanel contrasenaRow = new JPanel();
    contrasenaRow.setLayout(new BoxLayout(contrasenaRow, BoxLayout.Y_AXIS));
    contrasenaRow.setOpaque(false);
    contrasenaRow.setAlignmentX(LEFT_ALIGNMENT);
    contrasenaRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
    contrasenaRow.add(lblContrasena);
    contrasenaRow.add(Box.createVerticalStrut(6));
    contrasenaRow.add(campoContrasena);

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

          @Override
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

      var usuarioOpt = fachada.autenticarse(usuario, contrasena);
      if (usuarioOpt.isPresent()) {
        dispose();
        new MenuPrincipal(usuarioOpt.get()).setVisible(true);
      } else {
        lblError.setText("Usuario o contraseña incorrectos.");
        campoContrasena.setText("");
        campoContrasena.requestFocus();
      }
    };

    btnIniciar.addActionListener(e -> accionLogin.run());
    campoContrasena.addActionListener(e -> accionLogin.run());
    campoUsuario.addActionListener(e -> campoContrasena.requestFocus());

    tarjeta.add(lblTitulo);
    tarjeta.add(Box.createVerticalStrut(6));
    tarjeta.add(lblSubtitulo);
    tarjeta.add(Box.createVerticalStrut(32));
    tarjeta.add(usuarioRow);
    tarjeta.add(Box.createVerticalStrut(18));
    tarjeta.add(contrasenaRow);
    tarjeta.add(Box.createVerticalStrut(10));
    tarjeta.add(lblError);
    tarjeta.add(Box.createVerticalStrut(10));
    tarjeta.add(btnIniciar);

    root.add(tarjeta, new GridBagConstraints());
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
      new Bordes(new Color(213, 218, 230), 1, 10),
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

}
