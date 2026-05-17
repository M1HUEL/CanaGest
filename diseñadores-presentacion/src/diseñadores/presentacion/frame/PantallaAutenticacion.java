package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Optional;

public class PantallaAutenticacion extends JFrame {

  private final VentasControl ventasControl;

  public PantallaAutenticacion(VentasControl ventasControl) {
    this.ventasControl = ventasControl;

    configurarVentana();
    iniciarComponentes();
  }

  private void configurarVentana() {
    setTitle("La Canasta - Iniciar Sesión");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);
  }

  private void iniciarComponentes() {
    JPanel root = crearPanelFondo();
    JPanel tarjeta = crearTarjetaLogin();

    root.add(tarjeta, new GridBagConstraints());
    setContentPane(root);
  }

  private JPanel crearPanelFondo() {
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
    return root;
  }

  private JPanel crearTarjetaLogin() {
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

    JLabel lblTitulo = crearLabelTitulo();
    JLabel lblSubtitulo = crearLabelSubtitulo();

    JTextField campoUsuario = crearCampoTexto("Ingrese su usuario");
    JPasswordField campoContrasena = crearCampoContrasena("Ingrese su contraseña");
    JLabel lblError = crearLabelError();
    JButton btnIniciar = crearBotonLogin(campoUsuario, campoContrasena, lblError);

    tarjeta.add(lblTitulo);
    tarjeta.add(Box.createVerticalStrut(6));
    tarjeta.add(lblSubtitulo);
    tarjeta.add(Box.createVerticalStrut(32));
    tarjeta.add(crearFilaCampo("Usuario", campoUsuario));
    tarjeta.add(Box.createVerticalStrut(18));
    tarjeta.add(crearFilaCampo("Contraseña", campoContrasena));
    tarjeta.add(Box.createVerticalStrut(10));
    tarjeta.add(lblError);
    tarjeta.add(Box.createVerticalStrut(10));
    tarjeta.add(btnIniciar);

    return tarjeta;
  }

  private JLabel crearLabelTitulo() {
    JLabel lbl = new JLabel("La Canasta", SwingConstants.CENTER);
    lbl.setFont(Fuentes.b(30));
    lbl.setForeground(new Color(30, 50, 200));
    lbl.setAlignmentX(CENTER_ALIGNMENT);
    return lbl;
  }

  private JLabel crearLabelSubtitulo() {
    JLabel lbl = new JLabel("Inicie sesión para continuar", SwingConstants.CENTER);
    lbl.setFont(Fuentes.r(14));
    lbl.setForeground(Colores.GRIS_TEXTO);
    lbl.setAlignmentX(CENTER_ALIGNMENT);
    return lbl;
  }

  private JLabel crearLabelError() {
    JLabel lbl = new JLabel(" ", SwingConstants.CENTER);
    lbl.setFont(Fuentes.r(12));
    lbl.setForeground(Colores.ROJO);
    lbl.setAlignmentX(CENTER_ALIGNMENT);
    return lbl;
  }

  private JTextField crearCampoTexto(String placeholder) {
    JTextField tf = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarFondoCampo((Graphics2D) g2d, getWidth(), getHeight());
        super.paintComponent(g2d);
      }

    };
    configurarCampoBase(tf, placeholder);
    configurarPlaceholderTexto(tf, placeholder);
    return tf;
  }

  private JPasswordField crearCampoContrasena(String placeholder) {
    JPasswordField pf = new JPasswordField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarFondoCampo((Graphics2D) g2d, getWidth(), getHeight());
        super.paintComponent(g2d);
      }

    };
    configurarCampoBase(pf, placeholder);
    configurarPlaceholderContrasena(pf, placeholder);
    return pf;
  }

  private void configurarCampoBase(JTextField campo, String placeholder) {
    campo.setOpaque(false);
    campo.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(new Color(213, 218, 230), 1, 10),
      new EmptyBorder(10, 16, 10, 16)));
    campo.setFont(Fuentes.r(14));
    campo.setForeground(Colores.GRIS_TEXTO);
    campo.setPreferredSize(new Dimension(100, 50));
  }

  private void dibujarFondoCampo(Graphics2D g, int w, int h) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(new Color(248, 249, 252));
    g.fill(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));
  }

  private void configurarPlaceholderTexto(JTextField tf, String placeholder) {
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

  private void configurarPlaceholderContrasena(JPasswordField pf, String placeholder) {
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
  }

  private JPanel crearFilaCampo(String titulo, JComponent campo) {
    JPanel fila = new JPanel();
    fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
    fila.setOpaque(false);
    fila.setAlignmentX(Component.CENTER_ALIGNMENT);
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

    JLabel label = new JLabel(titulo);
    label.setFont(Fuentes.b(13));
    label.setForeground(Colores.TEXTO_OSCURO);
    label.setAlignmentX(Component.CENTER_ALIGNMENT);

    campo.setAlignmentX(Component.CENTER_ALIGNMENT);
    campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

    fila.add(label);
    fila.add(Box.createVerticalStrut(6));
    fila.add(campo);
    return fila;
  }

  private JButton crearBotonLogin(JTextField campoUsuario, JPasswordField campoContrasena, JLabel lblError) {
    JButton btn = new JButton("Iniciar Sesión") {
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

    btn.setForeground(Colores.BLANCO);
    btn.setFont(Fuentes.b(16));
    btn.setAlignmentX(CENTER_ALIGNMENT);
    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

    Runnable accionLogin = () -> ejecutarLogin(campoUsuario, campoContrasena, lblError);

    btn.addActionListener(e -> accionLogin.run());
    campoContrasena.addActionListener(e -> accionLogin.run());
    campoUsuario.addActionListener(e -> campoContrasena.requestFocus());

    return btn;
  }

  private void ejecutarLogin(JTextField campoUsuario, JPasswordField campoContrasena, JLabel lblError) {
    String usuario = campoUsuario.getText().trim();
    String contrasena = new String(campoContrasena.getPassword()).trim();

    Optional<UsuarioDTO> usuarioOpt = ventasControl.autenticar(usuario, contrasena);

    if (usuarioOpt.isPresent()) {
      dispose();
      new MenuPrincipal(usuarioOpt.get(), crearVentasControlAutenticado(usuarioOpt.get())).setVisible(true);
    } else {
      lblError.setText("Usuario o contraseña incorrectos.");
      campoContrasena.setText("");
      campoContrasena.requestFocus();
    }
  }

  private VentasControl crearVentasControlAutenticado(UsuarioDTO usuario) {
    return new VentasControl(
      ventasControl.getVentasFachada(),
      ventasControl.getUsuariosFachada(),
      ventasControl.getInventarioFachada(),
      ventasControl.getProveedoresFachada(),
      ventasControl.getAutenticacionFachada(),
      usuario
    );
  }

}
