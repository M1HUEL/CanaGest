package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Colores;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class RegistrarMetodoPagoTransferencia extends JFrame {

  private static final String CLABE_TIENDA = "012180001234567890";
  private static final String CUENTA = "0123456789";
  private static final String BANCO = "BBVA México";
  private static final String BENEFICIARIO = "La Canasta SA de CV";

  private final SeleccionarMetodoPago seleccionarMetodoPago;
  private final JFrame mainFrame;
  private final VentasControl control;
  private final Runnable onVentaFinalizada;
  private final String referencia;

  private JLabel lblEstado;
  private JButton btnConfirmar;

  public RegistrarMetodoPagoTransferencia(
    SeleccionarMetodoPago seleccionarMetodoPago,
    JFrame mainFrame,
    VentasControl control,
    Runnable onVentaFinalizada) {

    super("Transferencia Bancaria");
    this.seleccionarMetodoPago = seleccionarMetodoPago;
    this.mainFrame = mainFrame;
    this.control = control;
    this.onVentaFinalizada = onVentaFinalizada;
    this.referencia = generarReferencia();

    configurarVentana();
    inicializarComponentes();
  }

  private void configurarVentana() {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());
  }

  private void inicializarComponentes() {
    JPanel root = crearFondo();
    root.add(crearTopBar(), BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));
    cuerpo.add(crearFilaVolver(), BorderLayout.NORTH);
    cuerpo.add(centrar(crearScroll(crearCard()), 240, 10), BorderLayout.CENTER);

    root.add(cuerpo, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private void finalizarVenta() {
    try {
      control.finalizarVenta();
      TicketDTO ticketDTO = control.generarTicket();
      setVisible(false);
      new PantallaTicket(mainFrame, ticketDTO, onVentaFinalizada, control);
    } catch (Exception ex) {
      mostrarError("Transferencia aprobada, pero error al cerrar la venta:\n" + ex.getMessage());
    }
  }

  private JPanel crearCajaTotal() {
    JPanel c = new JPanel(new GridLayout(2, 1, 0, 8)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.NARANJA_BG);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    c.setOpaque(false);
    c.setBorder(new EmptyBorder(22, 20, 22, 20));
    c.setAlignmentX(LEFT_ALIGNMENT);
    c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    c.add(crearLabelCaja("Total a pagar", 13, Font.PLAIN, Colores.GRIS_TEXTO));
    c.add(crearLabelCaja(String.format("$%,.2f", control.getVentaActual().getTotal()), 38, Font.BOLD, Colores.NARANJA));
    return c;
  }

  private JLabel crearLabelCaja(String texto, int size, int style, Color color) {
    JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
    lbl.setFont(new Font("Segoe UI", style, size));
    lbl.setForeground(color);
    return lbl;
  }

  private JPanel crearDatosBancarios() {
    JPanel box = crearPanelFondo(Colores.FONDO_GRIS_CLARO, 14);
    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
    box.setBorder(new EmptyBorder(16, 16, 16, 16));
    box.setAlignmentX(LEFT_ALIGNMENT);
    box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

    box.add(crearTituloSeccion("Datos para transferencia"));
    box.add(Box.createVerticalStrut(12));
    box.add(crearFilaDato("Banco", BANCO, null, false));
    box.add(Box.createVerticalStrut(8));
    box.add(crearFilaDato("CLABE", CLABE_TIENDA, CLABE_TIENDA, true));
    box.add(Box.createVerticalStrut(8));
    box.add(crearFilaDato("Número de cuenta", CUENTA, CUENTA, true));
    box.add(Box.createVerticalStrut(8));
    box.add(crearFilaDato("Beneficiario", BENEFICIARIO, null, false));
    box.add(Box.createVerticalStrut(8));
    box.add(crearFilaDato("Referencia", referencia, referencia, true, Colores.NARANJA));
    return box;
  }

  private JLabel crearTituloSeccion(String texto) {
    JLabel tit = new JLabel(texto);
    tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
    tit.setForeground(Colores.TEXTO_OSCURO);
    tit.setAlignmentX(LEFT_ALIGNMENT);
    return tit;
  }

  private JPanel crearFilaDato(String etiqueta, String valor, String copiar, boolean conCopia) {
    return crearFilaDato(etiqueta, valor, copiar, conCopia, Colores.TEXTO_OSCURO);
  }

  private JPanel crearFilaDato(String etiqueta, String valor, String copiar, boolean conCopia, Color colorVal) {
    JPanel row = crearContenedorFila();

    JPanel txt = new JPanel(new GridLayout(2, 1, 0, 4));
    txt.setOpaque(false);
    txt.add(crearLabelSubtitulo(etiqueta));
    txt.add(crearLabelValor(valor, colorVal));
    row.add(txt, BorderLayout.CENTER);

    if (conCopia) {
      row.add(crearPanelBotonCopiar(copiar), BorderLayout.EAST);
    }
    return row;
  }

  private JPanel crearContenedorFila() {
    JPanel row = new JPanel(new BorderLayout(10, 0)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        g.setColor(Colores.BORDE_GRIS);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(new EmptyBorder(12, 16, 12, 12));
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
    row.setAlignmentX(LEFT_ALIGNMENT);
    return row;
  }

  private JLabel crearLabelSubtitulo(String texto) {
    JLabel lbl = new JLabel(texto);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    lbl.setForeground(Colores.GRIS_TEXTO);
    return lbl;
  }

  private JLabel crearLabelValor(String texto, Color color) {
    JLabel lbl = new JLabel(texto);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lbl.setForeground(color);
    return lbl;
  }

  private void onCopiarDato(String texto) {
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(texto), null);
    JOptionPane.showMessageDialog(this, "Copiado: " + texto, "Copiado", JOptionPane.INFORMATION_MESSAGE);
  }

  private JPanel crearInstrucciones() {
    JPanel box = crearPanelFondo(Colores.NARANJA_INST_BG, 12);
    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
    box.setBorder(new EmptyBorder(16, 18, 16, 18));
    box.setAlignmentX(LEFT_ALIGNMENT);
    box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

    box.add(crearTituloSeccion("Instrucciones:"));
    box.add(Box.createVerticalStrut(10));

    String[] pasos = {
      "Realiza la transferencia desde tu banca en línea o app móvil",
      "Usa la CLABE o número de cuenta proporcionados",
      "Incluye la referencia exacta en tu transferencia",
      "Espera la confirmación automática del pago"
    };
    for (int i = 0; i < pasos.length; i++) {
      box.add(crearFilaInstruccion(i + 1, pasos[i]));
      if (i < pasos.length - 1) {
        box.add(Box.createVerticalStrut(6));
      }
    }
    return box;
  }

  private JPanel crearFilaInstruccion(int numero, String texto) {
    JPanel f = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    f.setOpaque(false);
    f.setAlignmentX(LEFT_ALIGNMENT);
    JLabel n = new JLabel(numero + ".");
    n.setFont(new Font("Segoe UI", Font.BOLD, 13));
    n.setForeground(Colores.NARANJA);
    JLabel t = new JLabel(texto);
    t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    t.setForeground(Colores.TEXTO_OSCURO);
    f.add(n);
    f.add(t);
    return f;
  }

  private JPanel crearTopBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));
    bar.add(crearBotonMenuPrincipal());
    return bar;
  }

  private JButton crearBotonMenuPrincipal() {
    JButton btn = crearBoton("Menu Principal", Colores.AMARILLO_BTN, Colores.AMARILLO_BTN_HOVER);
    btn.setForeground(Colores.TEXTO_OSCURO);
    btn.setPreferredSize(new Dimension(160, 38));
    btn.addActionListener(e -> onMenuPrincipal());
    return btn;
  }

  private void onMenuPrincipal() {
    dispose();
    new MenuPrincipal(
      control.getUsuarioActivo(),
      this.control).setVisible(true);
  }

  private JPanel crearFilaVolver() {
    JButton b = new JButton("← Volver a métodos de pago");
    b.setContentAreaFilled(false);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    b.setForeground(Colores.TEXTO_OSCURO);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    b.addActionListener(e -> onVolver());
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    p.setOpaque(false);
    p.add(b);
    return p;
  }

  private void onVolver() {
    dispose();
    seleccionarMetodoPago.setVisible(true);
  }

  private JPanel crearFondo() {
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

  private JPanel crearTarjetaBlanca() {
    JPanel p = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, 20, 20));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 20, 20));
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    return p;
  }

  private JPanel crearPanelFondo(Color bg, int arc) {
    return new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
        super.paintComponent(g2d);
      }

    };
  }

  private JPanel centrar(JComponent c, int mH, int mV) {
    JPanel p = new JPanel(new GridBagLayout());
    p.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(mV, mH, mV, mH);
    p.add(c, gbc);
    return p;
  }

  private JScrollPane crearScroll(JPanel c) {
    JScrollPane sp = new JScrollPane(c);
    sp.setBorder(BorderFactory.createEmptyBorder());
    sp.setOpaque(false);
    sp.getViewport().setOpaque(false);
    sp.getVerticalScrollBar().setUnitIncrement(12);
    sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    return sp;
  }

  private JPanel crearHeader(String icono, String titulo, Color color) {
    JPanel h = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    h.setOpaque(false);
    h.setAlignmentX(LEFT_ALIGNMENT);

    JPanel iconBox = crearPanelFondo(color, 14);
    iconBox.setLayout(new BorderLayout());
    iconBox.setOpaque(false);
    iconBox.setPreferredSize(new Dimension(52, 52));
    JLabel iconLabel = new JLabel(icono, SwingConstants.CENTER);
    iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    iconLabel.setForeground(Colores.BLANCO);
    iconBox.add(iconLabel);

    JLabel tituloLabel = new JLabel(titulo);
    tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    tituloLabel.setForeground(Colores.TEXTO_OSCURO);

    h.add(iconBox);
    h.add(tituloLabel);
    return h;
  }

  private JButton crearBoton(String txt, Color base, Color hover) {
    JButton b = new JButton(txt) {
      boolean ov = false;

      {
        aplicarEstiloBoton(this);
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
        dibujarFondo(g2d, getWidth(), getHeight(), ov ? hover : base, 12);
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(new Font("Segoe UI", Font.BOLD, 15));
    b.setHorizontalAlignment(SwingConstants.CENTER);
    return b;
  }

  private void aplicarEstiloBoton(JButton b) {
    b.setContentAreaFilled(false);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  private void dibujarFondo(Graphics g2d, int w, int h, Color color, int arc) {
    Graphics2D g = (Graphics2D) g2d;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(color);
    g.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
  }

  private JPanel crearCard() {
    JPanel card = crearTarjetaBlanca();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(28, 32, 32, 32));
    card.add(crearHeader("TR", "Transferencia Bancaria", Colores.NARANJA));
    card.add(Box.createVerticalStrut(22));
    card.add(crearCajaTotal());
    card.add(Box.createVerticalStrut(20));
    card.add(crearDatosBancarios());
    card.add(Box.createVerticalStrut(20));
    card.add(crearPanelEstado());
    card.add(Box.createVerticalStrut(14));
    card.add(crearInstrucciones());
    card.add(Box.createVerticalStrut(24));
    card.add(crearBotonConfirmar());
    return card;
  }

  private JPanel crearPanelEstado() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 249, 252));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        g.setColor(Colores.BORDE_GRIS);
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        super.paintComponent(g2d);
      }

    };
    panel.setOpaque(false);
    panel.setAlignmentX(LEFT_ALIGNMENT);
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    panel.setBorder(new EmptyBorder(10, 16, 10, 16));

    lblEstado = new JLabel("● Esperando confirmación de transferencia");
    lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblEstado.setForeground(new Color(100, 116, 139));
    panel.add(lblEstado);
    return panel;
  }

  private JButton crearBotonConfirmar() {
    btnConfirmar = crearBoton("Ya realicé la transferencia", Colores.NARANJA, Colores.NARANJA_HOVER);
    btnConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
    btnConfirmar.addActionListener(e -> onConfirmarTransferencia());
    return btnConfirmar;
  }

  private void onConfirmarTransferencia() {
    btnConfirmar.setEnabled(false);
    btnConfirmar.setText("Procesando...");
    actualizarEstado("🔄 Verificando transferencia con el banco...", new Color(161, 110, 0));
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    SwingWorker<ResultadoPagoDTO, Void> worker = new SwingWorker<>() {
      @Override
      protected ResultadoPagoDTO doInBackground() {
        return control.procesarPagoTransferencia(
          new PagoTransferenciaDTO(CLABE_TIENDA, referencia));
      }

      @Override
      protected void done() {
        setCursor(Cursor.getDefaultCursor());
        try {
          manejarResultado(get());
        } catch (InterruptedException | ExecutionException ex) {
          restaurarBoton();
          mostrarError(ex.getMessage());
        }
      }

    };
    worker.execute();
  }

  private void manejarResultado(ResultadoPagoDTO resultado) {
    if (resultado.isAprobado()) {
      actualizarEstado("✓ Transferencia aprobada", new Color(21, 128, 61));
      Timer pausa = new Timer(600, e -> finalizarVenta());
      pausa.setRepeats(false);
      pausa.start();
    } else {
      actualizarEstado("✗ Transferencia rechazada", Colores.ROJO);
      mostrarRechazo(resultado.getMensaje());
      restaurarBoton();
    }
  }

  private void restaurarBoton() {
    btnConfirmar.setEnabled(true);
    btnConfirmar.setText("Ya realicé la transferencia");
    actualizarEstado("● Esperando confirmación de transferencia", new Color(100, 116, 139));
  }

  private void actualizarEstado(String texto, Color color) {
    lblEstado.setText(texto);
    lblEstado.setForeground(color);
    lblEstado.getParent().revalidate();
    lblEstado.getParent().repaint();
  }

  private JPanel crearPanelBotonCopiar(String texto) {
    JButton btn = new JButton("Copiar") {
      boolean ov = false;

      {
        aplicarEstiloBoton(this);
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
        dibujarFondo(g2d, getWidth(), getHeight(),
          ov ? Colores.NARANJA_HOVER : Colores.NARANJA, 8);
        super.paintComponent(g2d);
      }

    };
    btn.setForeground(Colores.BLANCO);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btn.setPreferredSize(new Dimension(64, 36));
    btn.addActionListener(e -> {
      Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(new StringSelection(texto), null);

      btn.setText("✓");
      Timer reset = new Timer(1500, ev -> btn.setText("Copiar"));
      reset.setRepeats(false);
      reset.start();
    });

    JPanel wrapper = new JPanel(new GridBagLayout());
    wrapper.setOpaque(false);
    wrapper.add(btn);
    return wrapper;
  }

  private String generarReferencia() {
    LocalDate hoy = LocalDate.now();
    int r = new Random().nextInt(9000) + 1000;
    return String.format("TRANS-%d-%02d%02d-%d",
      hoy.getYear(), hoy.getMonthValue(), hoy.getDayOfMonth(), r);
  }

  private void mostrarError(String mensaje) {
    JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void mostrarRechazo(String mensaje) {
    JOptionPane.showMessageDialog(this,
      "Transferencia rechazada:\n\n" + mensaje, "Rechazada", JOptionPane.WARNING_MESSAGE);
  }

}
