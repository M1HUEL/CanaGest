package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class RegistrarMetodoPagoTransferencia extends JFrame {

  private static final String CLABE_TIENDA = "012180001234567890";
  private static final String CUENTA = "0123456789";
  private static final String BANCO = "BBVA México";
  private static final String BENEFICIARIO = "La Canasta SA de CV";

  private final JFrame frame;
  private final SeleccionarMetodoPago seleccionarMetodoPago;
  private final IVentas ventasFachada;
  private final IInventario inventarioFachada;
  private final IUsuarios usuariosFachada;
  private final IProveedores proveedoresFachada;
  private final VentaDTO ventaActual;
  private final BigDecimal total;
  private final Runnable onVentaFinalizada;
  private final UsuarioDTO usuarioActivo;
  private final String referencia;

  public RegistrarMetodoPagoTransferencia(
    SeleccionarMetodoPago seleccionarMetodoPago,
    JFrame frame,
    IVentas ventasFachada,
    IInventario inventarioFachada,
    IUsuarios usuariosFachada,
    IProveedores proveedoresFachada,
    VentaDTO ventaActual,
    BigDecimal total,
    Runnable onVentaFinalizada,
    UsuarioDTO usuarioActivo) {

    super("Transferencia Bancaria");
    this.seleccionarMetodoPago = seleccionarMetodoPago;
    this.frame = frame;
    this.ventasFachada = ventasFachada;
    this.inventarioFachada = inventarioFachada;
    this.usuariosFachada = usuariosFachada;
    this.proveedoresFachada = proveedoresFachada;
    this.ventaActual = ventaActual;
    this.total = total;
    this.onVentaFinalizada = onVentaFinalizada;
    this.usuarioActivo = usuarioActivo;
    this.referencia = generarReferencia();

    configurarVentana();
    inicializarComponentes();
  }

  private void configurarVentana() {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(frame.getWidth(), frame.getHeight());
    setLocation(frame.getLocation());
  }

  private void inicializarComponentes() {
    JPanel root = fondo();
    root.add(topBar(), BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));
    cuerpo.add(btnVolverRow(), BorderLayout.NORTH);
    cuerpo.add(centrar(scroll(buildCard()), 240, 10), BorderLayout.CENTER);

    root.add(cuerpo, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel buildCard() {
    JPanel card = tarjetaBlanca();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(28, 32, 32, 32));

    card.add(header("TR", "Transferencia Bancaria", Colores.NARANJA));
    card.add(Box.createVerticalStrut(22));
    card.add(cajaTotal());
    card.add(Box.createVerticalStrut(20));
    card.add(datosBancarios());
    card.add(Box.createVerticalStrut(20));
    card.add(instrucciones());
    card.add(Box.createVerticalStrut(24));
    card.add(crearBotonConfirmar());

    return card;
  }

  private JButton crearBotonConfirmar() {
    JButton btnConfirmar = boton("Ya realicé la transferencia", Colores.NARANJA, Colores.NARANJA_HOVER);
    btnConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
    btnConfirmar.addActionListener(e -> seleccionarConfirmarTransferencia());
    return btnConfirmar;
  }

  private void seleccionarConfirmarTransferencia() {
    procesarConBanco();
  }

  private void procesarConBanco() {
    alternarEstadoPantalla(false);

    SwingWorker<ResultadoPagoDTO, Void> worker = new SwingWorker<>() {
      @Override
      protected ResultadoPagoDTO doInBackground() {
        return ventasFachada.procesarPagoTransferencia(
          ventaActual,
          new PagoTransferenciaDTO(CLABE_TIENDA, referencia));
      }

      @Override
      protected void done() {
        alternarEstadoPantalla(true);
        try {
          manejarResultado(get(), onVentaFinalizada);
        } catch (InterruptedException | ExecutionException ex) {
          mostrarError(ex.getMessage());
        }
      }

    };
    worker.execute();
  }

  private void alternarEstadoPantalla(boolean activado) {
    setCursor(activado ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    setEnabled(activado);
  }

  private void manejarResultado(ResultadoPagoDTO resultado, Runnable onConfirmado) {
    if (resultado.isAprobado()) {
      finalizarVentaExitosa(onConfirmado);
    } else {
      mostrarRechazo(resultado.getMensaje());
    }
  }

  private void finalizarVentaExitosa(Runnable onConfirmado) {
    try {
      ventasFachada.procesarFinalizarVenta(ventaActual);
      TicketDTO ticketDTO = ventasFachada.generarTicket(ventaActual, BigDecimal.ZERO);
      this.setVisible(false);
      new PantallaTicket(frame, ticketDTO, onConfirmado, usuariosFachada,
        ventasFachada, inventarioFachada, proveedoresFachada);
    } catch (Exception ex) {
      mostrarError("Transferencia aprobada, pero error al cerrar la venta:\n" + ex.getMessage());
    }
  }

  private JPanel cajaTotal() {
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

    c.add(crearLabelTotal("Total a pagar", 13, Font.PLAIN, Colores.GRIS_TEXTO));
    c.add(crearLabelTotal(String.format("$%,.2f", total), 38, Font.BOLD, Colores.NARANJA));
    return c;
  }

  private JLabel crearLabelTotal(String texto, int size, int style, Color color) {
    JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
    lbl.setFont(new Font("Segoe UI", style, size));
    lbl.setForeground(color);
    return lbl;
  }

  private JPanel datosBancarios() {
    JPanel box = panelFondo(Colores.FONDO_GRIS_CLARO, 14);
    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
    box.setBorder(new EmptyBorder(16, 16, 16, 16));
    box.setAlignmentX(LEFT_ALIGNMENT);
    box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

    box.add(crearTituloSeccion("Datos para transferencia"));
    box.add(Box.createVerticalStrut(12));
    box.add(filaDato("Banco", BANCO, null, false));
    box.add(Box.createVerticalStrut(8));
    box.add(filaDato("CLABE", CLABE_TIENDA, CLABE_TIENDA, true));
    box.add(Box.createVerticalStrut(8));
    box.add(filaDato("Número de cuenta", CUENTA, CUENTA, true));
    box.add(Box.createVerticalStrut(8));
    box.add(filaDato("Beneficiario", BENEFICIARIO, null, false));
    box.add(Box.createVerticalStrut(8));
    box.add(filaDato("Referencia", referencia, referencia, true, Colores.NARANJA));

    return box;
  }

  private JLabel crearTituloSeccion(String texto) {
    JLabel tit = new JLabel(texto);
    tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
    tit.setForeground(Colores.TEXTO_OSCURO);
    tit.setAlignmentX(LEFT_ALIGNMENT);
    return tit;
  }

  private JPanel filaDato(String etq, String val, String copiar, boolean conCopia) {
    return filaDato(etq, val, copiar, conCopia, Colores.TEXTO_OSCURO);
  }

  private JPanel filaDato(String etq, String val, String copiar, boolean conCopia, Color colorVal) {
    JPanel row = crearContenedorFila();

    JPanel txt = new JPanel(new GridLayout(2, 1, 0, 4));
    txt.setOpaque(false);
    txt.add(crearLabelSubtitulo(etq));
    txt.add(crearLabelValor(val, colorVal));

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
    JLabel le = new JLabel(texto);
    le.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    le.setForeground(Colores.GRIS_TEXTO);
    return le;
  }

  private JLabel crearLabelValor(String texto, Color color) {
    JLabel lv = new JLabel(texto);
    lv.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lv.setForeground(color);
    return lv;
  }

  private JPanel crearPanelBotonCopiar(String texto) {
    JButton btn = new JButton("Copiar") {
      boolean ov = false;

      {
        configurarEstiloBoton(this);
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
        dibujarFondoBoton(g2d, getWidth(), getHeight(), ov ? Colores.NARANJA_HOVER : Colores.NARANJA, 8);
        super.paintComponent(g2d);
      }

    };
    btn.setForeground(Colores.BLANCO);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btn.setPreferredSize(new Dimension(64, 36));
    btn.addActionListener(e -> seleccionarCopiarDato(texto));

    JPanel w = new JPanel(new GridBagLayout());
    w.setOpaque(false);
    w.add(btn);
    return w;
  }

  private void seleccionarCopiarDato(String texto) {
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(texto), null);
    JOptionPane.showMessageDialog(this, "Copiado: " + texto, "Copiado", JOptionPane.INFORMATION_MESSAGE);
  }

  private JPanel instrucciones() {
    JPanel box = panelFondo(Colores.NARANJA_INST_BG, 12);
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

  private JPanel topBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));

    JButton btn = boton("Menu Principal", Colores.AMARILLO_BTN, Colores.AMARILLO_BTN_HOVER);
    btn.setForeground(Colores.TEXTO_OSCURO);
    btn.setPreferredSize(new Dimension(160, 38));
    btn.addActionListener(e -> seleccionarMenuPrincipal());

    bar.add(btn);
    return bar;
  }

  private void seleccionarMenuPrincipal() {
    dispose();
    new MenuPrincipal(usuarioActivo, usuariosFachada, ventasFachada, inventarioFachada, proveedoresFachada).setVisible(true);
  }

  private JPanel btnVolverRow() {
    JButton b = new JButton("← Volver a métodos de pago");
    b.setContentAreaFilled(false);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    b.setForeground(Colores.TEXTO_OSCURO);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    b.addActionListener(e -> seleccionarVolver());

    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    p.setOpaque(false);
    p.add(b);
    return p;
  }

  private void seleccionarVolver() {
    dispose();
    seleccionarMetodoPago.setVisible(true);
  }

  private JPanel fondo() {
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

  private JPanel tarjetaBlanca() {
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

  private JPanel panelFondo(Color bg, int arc) {
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

  private JScrollPane scroll(JPanel c) {
    JScrollPane sp = new JScrollPane(c);
    sp.setBorder(BorderFactory.createEmptyBorder());
    sp.setOpaque(false);
    sp.getViewport().setOpaque(false);
    sp.getVerticalScrollBar().setUnitIncrement(12);
    sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    return sp;
  }

  private JPanel header(String ico, String tit, Color col) {
    JPanel h = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    h.setOpaque(false);
    h.setAlignmentX(LEFT_ALIGNMENT);

    JPanel ib = panelFondo(col, 14);
    ib.setLayout(new BorderLayout());
    ib.setOpaque(false);
    ib.setPreferredSize(new Dimension(52, 52));

    JLabel il = new JLabel(ico, SwingConstants.CENTER);
    il.setFont(new Font("Segoe UI", Font.BOLD, 16));
    il.setForeground(Colores.BLANCO);
    ib.add(il);

    JLabel tl = new JLabel(tit);
    tl.setFont(new Font("Segoe UI", Font.BOLD, 24));
    tl.setForeground(Colores.TEXTO_OSCURO);

    h.add(ib);
    h.add(tl);
    return h;
  }

  private JButton boton(String txt, Color base, Color hover) {
    JButton b = new JButton(txt) {
      boolean ov = false;

      {
        configurarEstiloBoton(this);
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
        dibujarFondoBoton(g2d, getWidth(), getHeight(), ov ? hover : base, 12);
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(new Font("Segoe UI", Font.BOLD, 15));
    b.setHorizontalAlignment(SwingConstants.CENTER);
    return b;
  }

  private void configurarEstiloBoton(JButton b) {
    b.setContentAreaFilled(false);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  private void dibujarFondoBoton(Graphics g2d, int w, int h, Color color, int arc) {
    Graphics2D g = (Graphics2D) g2d;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(color);
    g.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
  }

  private String generarReferencia() {
    LocalDate h = LocalDate.now();
    int r = new Random().nextInt(9000) + 1000;
    return String.format("TRANS-%d-%02d%02d-%d", h.getYear(), h.getMonthValue(), h.getDayOfMonth(), r);
  }

  private void mostrarError(String mensaje) {
    JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void mostrarRechazo(String mensaje) {
    JOptionPane.showMessageDialog(this, "❌ Transferencia rechazada:\n\n" + mensaje, "Rechazada", JOptionPane.WARNING_MESSAGE);
  }

}
