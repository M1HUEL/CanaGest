package diseñadores.presentacion.frame;

import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class PantallaAutenticacion extends JFrame {

  private final IUsuarios usuariosFachada;
  private final IVentas ventasFachada;
  private final IInventario inventarioFachada;
  private final IProveedores proveedoresFachada;

  public PantallaAutenticacion(
    IUsuarios usuariosFachada,
    IVentas ventasFachada,
    IInventario inventarioFachada,
    IProveedores proveedoresFachada) {
    this.usuariosFachada = usuariosFachada;
    this.ventasFachada = ventasFachada;
    this.inventarioFachada = inventarioFachada;
    this.proveedoresFachada = proveedoresFachada;

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

    JLabel lblTitulo = new JLabel("La Canasta", SwingConstants.CENTER);
    lblTitulo.setFont(Fuentes.b(30));
    lblTitulo.setForeground(new Color(30, 50, 200));
    lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

    JLabel lblSubtitulo = new JLabel("Inicie sesión para continuar", SwingConstants.CENTER);
    lblSubtitulo.setFont(Fuentes.r(14));
    lblSubtitulo.setForeground(Colores.GRIS_TEXTO);
    lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

    JTextField campoUsuario = campoLogin("Ingrese su usuario", false);
    JPasswordField campoContrasena = (JPasswordField) campoLogin("Ingrese su contraseña", true);
    JLabel lblError = new JLabel(" ", SwingConstants.CENTER);
    lblError.setFont(Fuentes.r(12));
    lblError.setForeground(Colores.ROJO);
    lblError.setAlignmentX(CENTER_ALIGNMENT);

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

  private JPanel crearFilaCampo(String titulo, JComponent campo) {
    JPanel fila = new JPanel();
    fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
    fila.setOpaque(false);
    fila.setAlignmentX(LEFT_ALIGNMENT);
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

    JLabel label = new JLabel(titulo);
    label.setFont(Fuentes.b(13));
    label.setForeground(Colores.TEXTO_OSCURO);
    label.setAlignmentX(LEFT_ALIGNMENT);

    campo.setAlignmentX(LEFT_ALIGNMENT);
    campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

    fila.add(label);
    fila.add(Box.createVerticalStrut(6));
    fila.add(campo);
    return fila;
  }

  private JButton crearBotonLogin(JTextField u, JPasswordField c, JLabel error) {
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

    Runnable accionLogin = () -> {
      String usuario = u.getText().trim();
      String contrasena = new String(c.getPassword()).trim();
      var usuarioOpt = usuariosFachada.autenticarse(usuario, contrasena);

      if (usuarioOpt.isPresent()) {
        dispose();
        new MenuPrincipal(usuarioOpt.get(), usuariosFachada, ventasFachada,
          inventarioFachada, proveedoresFachada).setVisible(true);
      } else {
        error.setText("Usuario o contraseña incorrectos.");
        c.setText("");
        c.requestFocus();
      }
    };

    btn.addActionListener(e -> accionLogin.run());
    c.addActionListener(e -> accionLogin.run());
    u.addActionListener(e -> c.requestFocus());

    return btn;
  }

  private JTextField campoLogin(String placeholder, boolean esContrasena) {
    JTextField tf = esContrasena ? new JPasswordField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarFondoCampo((Graphics2D) g2d, getWidth(), getHeight());
        super.paintComponent(g2d);
      }

    } : new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarFondoCampo((Graphics2D) g2d, getWidth(), getHeight());
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
      configurarPlaceholderContrasena((JPasswordField) tf, placeholder);
    } else {
      configurarPlaceholderTexto(tf, placeholder);
    }

    return tf;
  }

  private void dibujarFondoCampo(Graphics2D g2d, int w, int h) {
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setColor(new Color(248, 249, 252));
    g2d.fill(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));
  }

  private void configurarPlaceholderTexto(JTextField tf, String sh) {
    tf.setText(sh);
    tf.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (tf.getText().equals(sh)) {
          tf.setText("");
          tf.setForeground(Colores.TEXTO_OSCURO);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (tf.getText().isEmpty()) {
          tf.setText(sh);
          tf.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });
  }

  private void configurarPlaceholderContrasena(JPasswordField pf, String sh) {
    pf.setEchoChar((char) 0);
    pf.setText(sh);
    pf.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (new String(pf.getPassword()).equals(sh)) {
          pf.setText("");
          pf.setEchoChar('\u2022');
          pf.setForeground(Colores.TEXTO_OSCURO);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (new String(pf.getPassword()).isEmpty()) {
          pf.setEchoChar((char) 0);
          pf.setText(sh);
          pf.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });
  }

}
