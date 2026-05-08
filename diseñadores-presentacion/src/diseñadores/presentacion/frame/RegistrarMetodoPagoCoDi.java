package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Colores;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.Random;

public class RegistrarMetodoPagoCoDi extends JFrame {

  private final SeleccionarMetodoPago seleccionarMetodoPago;
  private final JFrame mainFrame;
  private final VentasControl control;
  private final Runnable onVentaFinalizada;
  private final String referencia;

  public RegistrarMetodoPagoCoDi(
    SeleccionarMetodoPago seleccionarMetodoPago,
    JFrame mainFrame,
    VentasControl control,
    Runnable onVentaFinalizada) {

    super("Registrar Pago CoDi");
    this.seleccionarMetodoPago = seleccionarMetodoPago;
    this.mainFrame = mainFrame;
    this.control = control;
    this.onVentaFinalizada = onVentaFinalizada;
    this.referencia = generarReferencia();

    configurarVentana();
    inicializarContenido();
  }

  private void configurarVentana() {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());
  }

  private void inicializarContenido() {
    JPanel root = crearPanelBase();
    root.add(crearTopBar(), BorderLayout.NORTH);
    root.add(crearCuerpo(), BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel crearCuerpo() {
    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));
    cuerpo.add(crearFilaVolver(), BorderLayout.NORTH);
    cuerpo.add(crearCentrado(crearScroll(crearCard()), 240, 10), BorderLayout.CENTER);
    return cuerpo;
  }

  private JPanel crearPanelBase() {
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

  private JPanel crearFilaVolver() {
    JButton btn = crearBtnTexto("← Volver a métodos de pago");
    btn.addActionListener(e -> onVolver());
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    row.setOpaque(false);
    row.add(btn);
    return row;
  }

  private void onVolver() {
    dispose();
    seleccionarMetodoPago.setVisible(true);
  }

  private JScrollPane crearScroll(JPanel contenido) {
    JScrollPane scroll = new JScrollPane(contenido);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(12);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    return scroll;
  }

  private JPanel crearCard() {
    JPanel card = crearContenedorTarjeta();
    GridBagConstraints c = crearGridCard();
    int row = 0;

    c.gridy = row++;
    c.insets = new Insets(0, 0, 22, 0);
    card.add(crearHeader(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 24, 0);
    card.add(crearCajaTotal(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 16, 0);
    card.add(crearPanelQR(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 24, 0);
    card.add(crearInstrucciones(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 0, 0);
    card.add(crearBotonConfirmar(), c);

    return card;
  }

  private JPanel crearContenedorTarjeta() {
    JPanel card = new JPanel(new GridBagLayout()) {
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
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(28, 32, 32, 32));
    return card;
  }

  private GridBagConstraints crearGridCard() {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    return c;
  }

  private JButton crearBotonConfirmar() {
    JButton btn = crearBoton("Confirmar Pago CoDi", Colores.MORADO, Colores.MORADO_HOVER);
    btn.setPreferredSize(new Dimension(0, 54));
    btn.addActionListener(e -> onConfirmarPago());
    return btn;
  }

  private void onConfirmarPago() {
    setEnabled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    SwingWorker<ResultadoPagoDTO, Void> worker = new SwingWorker<>() {
      @Override
      protected ResultadoPagoDTO doInBackground() {
        return control.procesarPagoCoDi(new PagoQrDTO(referencia));
      }

      @Override
      protected void done() {
        setCursor(Cursor.getDefaultCursor());
        setEnabled(true);
        try {
          manejarResultado(get());
        } catch (java.util.concurrent.ExecutionException ex) {
          mostrarError(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
        } catch (InterruptedException ex) {
          mostrarError(ex.getMessage());
        }
      }

    };
    worker.execute();
  }

  private void manejarResultado(ResultadoPagoDTO resultado) {
    if (resultado.isAprobado()) {
      finalizarVenta();
    } else {
      mostrarRechazo(resultado.getMensaje());
    }
  }

  private void finalizarVenta() {
    try {
      control.finalizarVenta();
      TicketDTO ticketDTO = control.generarTicket();
      setVisible(false);
      new PantallaTicket(mainFrame, ticketDTO, onVentaFinalizada, control);
    } catch (Exception ex) {
      mostrarError("Pago aprobado, pero error al cerrar la venta:\n" + ex.getMessage());
    }
  }

  private JPanel crearHeader() {
    JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    header.setOpaque(false);
    header.add(crearIconoBox());
    header.add(crearTituloLabel());
    return header;
  }

  private JPanel crearIconoBox() {
    JPanel box = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.MORADO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    box.setOpaque(false);
    box.setPreferredSize(new Dimension(52, 52));
    JLabel lbl = new JLabel("QR", SwingConstants.CENTER);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lbl.setForeground(Colores.BLANCO);
    box.add(lbl);
    return box;
  }

  private JLabel crearTituloLabel() {
    JLabel lbl = new JLabel("Pago con CoDi");
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lbl.setForeground(Colores.TEXTO_OSCURO);
    return lbl;
  }

  private JPanel crearCajaTotal() {
    JPanel caja = new JPanel(new GridLayout(2, 1, 0, 8)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.MORADO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    caja.setOpaque(false);
    caja.setBorder(new EmptyBorder(22, 20, 22, 20));
    caja.add(crearLabelTotalTxt());
    caja.add(crearLabelTotalVal());
    return caja;
  }

  private JLabel crearLabelTotalTxt() {
    JLabel lbl = new JLabel("Total a pagar", SwingConstants.CENTER);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lbl.setForeground(new Color(220, 200, 255));
    return lbl;
  }

  private JLabel crearLabelTotalVal() {
    JLabel lbl = new JLabel(
      String.format("$%,.2f", control.getVentaActual().getTotal()), SwingConstants.CENTER);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 38));
    lbl.setForeground(Colores.BLANCO);
    return lbl;
  }

  private JPanel crearPanelQR() {
    JPanel contenedor = new JPanel();
    contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
    contenedor.setOpaque(false);
    contenedor.add(crearMarcoQR());
    contenedor.add(Box.createVerticalStrut(8));
    contenedor.add(crearLabelReferencia());
    return contenedor;
  }

  private JPanel crearMarcoQR() {
    JPanel marco = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
        g.setColor(Colores.MORADO);
        g.setStroke(new BasicStroke(2));
        g.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));
        super.paintComponent(g2d);
      }

    };
    marco.setOpaque(false);
    marco.setBorder(new EmptyBorder(16, 16, 16, 16));
    marco.setAlignmentX(CENTER_ALIGNMENT);
    marco.add(crearComponenteGraficoQR(), BorderLayout.CENTER);
    return marco;
  }

  private JComponent crearComponenteGraficoQR() {
    JPanel qr = new JPanel() {
      private final boolean[][] mapa = generarMapaQR(21);

      @Override
      protected void paintComponent(Graphics g2d) {
        super.paintComponent(g2d);
        dibujarMapaQR((Graphics2D) g2d, mapa);
      }

      @Override
      public Dimension getPreferredSize() {
        return new Dimension(200, 200);
      }

    };
    qr.setOpaque(false);
    return qr;
  }

  private void dibujarMapaQR(Graphics2D g, boolean[][] mapa) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int n = mapa.length;
    int cell = Math.min(getWidth(), getHeight()) / n;
    int offX = (getWidth() - n * cell) / 2;
    int offY = (getHeight() - n * cell) / 2;
    for (int y = 0; y < n; y++) {
      for (int x = 0; x < n; x++) {
        g.setColor(mapa[y][x] ? Colores.MORADO : Colores.BLANCO);
        g.fillRect(offX + x * cell, offY + y * cell, cell, cell);
      }
    }
    dibujarMarcador(g, offX, offY, cell, 7);
    dibujarMarcador(g, offX + (n - 7) * cell, offY, cell, 7);
    dibujarMarcador(g, offX, offY + (n - 7) * cell, cell, 7);
  }

  private void dibujarMarcador(Graphics2D g, int ox, int oy, int cell, int size) {
    g.setColor(Colores.MORADO);
    g.fillRect(ox, oy, size * cell, size * cell);
    g.setColor(Colores.BLANCO);
    g.fillRect(ox + cell, oy + cell, (size - 2) * cell, (size - 2) * cell);
    g.setColor(Colores.MORADO);
    g.fillRect(ox + 2 * cell, oy + 2 * cell, (size - 4) * cell, (size - 4) * cell);
  }

  private JLabel crearLabelReferencia() {
    JLabel lbl = new JLabel("Referencia: " + referencia, SwingConstants.CENTER);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    lbl.setForeground(Colores.GRIS_TEXTO);
    lbl.setAlignmentX(CENTER_ALIGNMENT);
    return lbl;
  }

  private boolean[][] generarMapaQR(int n) {
    Random rnd = new Random();
    boolean[][] m = new boolean[n][n];
    for (int y = 0; y < n; y++) {
      for (int x = 0; x < n; x++) {
        m[y][x] = rnd.nextBoolean();
      }
    }
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        m[i][j] = false;
        m[i][n - 1 - j] = false;
        m[n - 1 - i][j] = false;
      }
    }
    return m;
  }

  private JPanel crearInstrucciones() {
    JPanel box = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    box.setOpaque(false);
    box.setBorder(new EmptyBorder(16, 18, 16, 18));

    GridBagConstraints ci = new GridBagConstraints();
    ci.gridx = 0;
    ci.fill = GridBagConstraints.HORIZONTAL;
    ci.weightx = 1;

    JLabel tit = new JLabel("Instrucciones:");
    tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
    tit.setForeground(Colores.TEXTO_OSCURO);
    ci.gridy = 0;
    ci.insets = new Insets(0, 0, 10, 0);
    box.add(tit, ci);

    String[] pasos = {
      "Abra su aplicación bancaria y seleccione CoDi",
      "Escanee el código QR mostrado en pantalla",
      "Confirme el monto y autorice el pago en su app",
      "Espere la notificación de confirmación"
    };
    for (int i = 0; i < pasos.length; i++) {
      ci.gridy = i + 1;
      ci.insets = new Insets(0, 0, i < pasos.length - 1 ? 6 : 0, 0);
      box.add(crearFilaPaso(pasos[i], i + 1), ci);
    }
    return box;
  }

  private JPanel crearFilaPaso(String texto, int numero) {
    JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    fila.setOpaque(false);
    JLabel num = new JLabel(numero + ".");
    num.setFont(new Font("Segoe UI", Font.BOLD, 13));
    num.setForeground(Colores.MORADO);
    JLabel txt = new JLabel(texto);
    txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txt.setForeground(Colores.TEXTO_OSCURO);
    fila.add(num);
    fila.add(txt);
    return fila;
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
      control.getUsuariosFachada(),
      control.getVentasFachada(),
      control.getInventarioFachada(),
      control.getProveedoresFachada()).setVisible(true);
  }

  private JPanel crearCentrado(JComponent contenido, int margenH, int margenV) {
    JPanel p = new JPanel(new GridBagLayout());
    p.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(margenV, margenH, margenV, margenH);
    p.add(contenido, gbc);
    return p;
  }

  private JButton crearBoton(String texto, Color base, Color hover) {
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
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(new Font("Segoe UI", Font.BOLD, 15));
    b.setHorizontalAlignment(SwingConstants.CENTER);
    return b;
  }

  private JButton crearBtnTexto(String texto) {
    JButton b = new JButton(texto);
    b.setContentAreaFilled(false);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setForeground(Colores.TEXTO_OSCURO);
    b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return b;
  }

  private String generarReferencia() {
    LocalDate hoy = LocalDate.now();
    int r = new Random().nextInt(9000) + 1000;
    return String.format("CODI-%d-%02d%02d-%d",
      hoy.getYear(), hoy.getMonthValue(), hoy.getDayOfMonth(), r);
  }

  private void mostrarError(String mensaje) {
    JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void mostrarRechazo(String mensaje) {
    JOptionPane.showMessageDialog(this,
      "Pago CoDi rechazado:\n\n" + mensaje, "Rechazado", JOptionPane.WARNING_MESSAGE);
  }

}
