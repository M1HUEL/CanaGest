package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.presentacion.control.VentasControl;
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

  private static final Font FONT_TITULO = new Font("Segoe UI", Font.BOLD, 22);
  private static final Font FONT_BOTON_METODO = new Font("Segoe UI", Font.BOLD, 20);
  private static final Font FONT_BOTON_ACCION = new Font("Segoe UI", Font.BOLD, 14);

  private static final Map<String, Color[]> COLORES_METODO = new LinkedHashMap<>();

  static {
    COLORES_METODO.put("Efectivo", new Color[]{Colores.VERDE_METODO, Colores.VERDE_METODO_H});
    COLORES_METODO.put("Tarjeta", new Color[]{Colores.AZUL, Colores.AZUL_HOVER});
    COLORES_METODO.put("CoDi", new Color[]{Colores.MORADO, Colores.MORADO_HOVER});
    COLORES_METODO.put("Transferencia", new Color[]{Colores.NARANJA, Colores.NARANJA_HOVER});
  }

  private final JFrame frameAnterior;
  private final VentasControl control;
  private final UsuarioDTO usuarioActivo;
  private final Runnable onVentaFinalizada;

  public SeleccionarMetodoPago(JFrame frameAnterior,
    VentasControl control,
    Runnable onVentaFinalizada,
    UsuarioDTO usuarioActivo) {

    super("Método de Pago");
    this.frameAnterior = frameAnterior;
    this.control = control;
    this.usuarioActivo = usuarioActivo;
    this.onVentaFinalizada = onVentaFinalizada;

    configurarVentana();
    inicializarComponentes();
  }

  private void configurarVentana() {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(frameAnterior.getWidth(), frameAnterior.getHeight());
    setLocation(frameAnterior.getLocation());
  }

  private void inicializarComponentes() {
    JPanel root = crearPanelBase();
    root.add(crearTopBar(), BorderLayout.NORTH);
    root.add(crearContenedorCentrado(crearCard(), 280, 30), BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel crearPanelBase() {
    JPanel p = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
      }

    };
    p.setOpaque(false);
    return p;
  }

  private JPanel crearTopBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));
    bar.add(crearBotonMenuPrincipal());
    return bar;
  }

  private JButton crearBotonMenuPrincipal() {
    JButton btn = crearBotonAccion("Menú Principal", Colores.AMARILLO_BTN, Colores.AMARILLO_BTN_HOVER);
    btn.setForeground(Colores.TEXTO_OSCURO);
    btn.setPreferredSize(new Dimension(160, 38));
    btn.addActionListener(e -> irAlMenuPrincipal());
    return btn;
  }

  private void irAlMenuPrincipal() {
    dispose();
    new MenuPrincipal(usuarioActivo,
      control.getUsuariosFachada(),
      control.getVentasFachada(),
      control.getInventarioFachada(),
      control.getProveedoresFachada()).setVisible(true);
  }

  private JPanel crearCard() {
    JPanel card = crearContenedorTarjeta();
    card.add(crearTituloCard(), BorderLayout.NORTH);
    card.add(crearGridMetodos(), BorderLayout.CENTER);
    card.add(crearPanelBotonesInferiores(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel crearContenedorTarjeta() {
    JPanel card = new JPanel(new BorderLayout(0, 20)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, 22, 22));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 22, 22));
      }

    };
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(32, 32, 32, 32));
    return card;
  }

  private JLabel crearTituloCard() {
    JLabel titulo = new JLabel("Seleccione el método de pago", SwingConstants.CENTER);
    titulo.setFont(FONT_TITULO);
    titulo.setForeground(Colores.TEXTO_OSCURO);
    return titulo;
  }

  private JPanel crearGridMetodos() {
    JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
    grid.setOpaque(false);
    COLORES_METODO.forEach((nombre, colores)
      -> grid.add(crearBotonMetodo(nombre, colores[0], colores[1]))
    );
    return grid;
  }

  private JPanel crearBotonMetodo(String nombre, Color base, Color hover) {
    JPanel btn = new JPanel(new GridLayout(2, 1, 0, 8)) {
      boolean isHover = false;

      {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            isHover = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            isHover = false;
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
        g.setColor(isHover ? hover : base);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
      }

    };
    btn.setBorder(new EmptyBorder(28, 20, 28, 20));
    btn.add(new JLabel("", SwingConstants.CENTER));
    btn.add(crearLabelNombreMetodo(nombre));
    return btn;
  }

  private JLabel crearLabelNombreMetodo(String nombre) {
    JLabel lbl = new JLabel(nombre, SwingConstants.CENTER);
    lbl.setFont(FONT_BOTON_METODO);
    lbl.setForeground(Colores.BLANCO);
    return lbl;
  }

  private JPanel crearPanelBotonesInferiores() {
    JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
    row.setOpaque(false);
    row.setPreferredSize(new Dimension(0, 60));
    row.add(crearBotonCancelar());
    row.add(crearBotonVolver());
    return row;
  }

  private JButton crearBotonCancelar() {
    JButton btn = crearBotonAccion("Cancelar", Colores.ROJO, Colores.ROJO_HOVER);
    btn.addActionListener(e -> onCancelarVenta());
    return btn;
  }

  private JButton crearBotonVolver() {
    JButton btn = crearBotonAccion("Volver", Colores.GRIS_BTN, Colores.GRIS_BTN_HOVER);
    btn.addActionListener(e -> volverAtras());
    return btn;
  }

  private void onCancelarVenta() {
    int op = JOptionPane.showConfirmDialog(this, "¿Cancelar la venta?", "Confirmar",
      JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    if (op == JOptionPane.YES_OPTION) {
      volverAtras();
    }
  }

  private void seleccionarMetodo(String nombre) {
    setVisible(false);
    switch (nombre) {
      case "Efectivo" ->
        abrirPagoEfectivo();
      case "Tarjeta" ->
        abrirPagoTarjeta();
      case "CoDi" ->
        abrirPagoCoDi();
      case "Transferencia" ->
        abrirPagoTransferencia();
      default ->
        manejarMetodoNoDisponible();
    }
  }

  private void abrirPagoEfectivo() {
    new RegistrarMetodoPagoEfectivo(this, frameAnterior, control, onVentaFinalizada, usuarioActivo);
  }

  private void abrirPagoTarjeta() {
    new RegistrarMetodoPagoTarjeta(this, frameAnterior, control, onVentaFinalizada);
  }

  private void abrirPagoCoDi() {
    new RegistrarMetodoPagoCoDi(this, frameAnterior,
      control.getVentasFachada(), control.getInventarioFachada(),
      control.getUsuariosFachada(), control.getProveedoresFachada(),
      control.getVentaActual(), control.getVentaActual().getTotal(),
      onVentaFinalizada, usuarioActivo);
  }

  private void abrirPagoTransferencia() {
    new RegistrarMetodoPagoTransferencia(this, frameAnterior, this.control, onVentaFinalizada);
  }

  private void manejarMetodoNoDisponible() {
    JOptionPane.showMessageDialog(frameAnterior, "Método no disponible", "Error", JOptionPane.ERROR_MESSAGE);
    setVisible(true);
  }

  private void volverAtras() {
    dispose();
    frameAnterior.setVisible(true);
  }

  private JButton crearBotonAccion(String texto, Color base, Color hover) {
    JButton b = new JButton(texto) {
      boolean isHover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            isHover = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            isHover = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(isHover ? hover : base);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(FONT_BOTON_ACCION);
    return b;
  }

  private JPanel crearContenedorCentrado(JComponent contenido, int margenH, int margenV) {
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

}
