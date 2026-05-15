package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Botones;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.Random;

public class RegistrarMetodoPagoQr extends JFrame {

  private static final int SEGUNDOS_EXPIRACION = 180; // 3 minutos

  private final SeleccionarMetodoPago seleccionarMetodoPago;
  private final JFrame mainFrame;
  private final VentasControl control;
  private final Runnable onVentaFinalizada;

  private String referencia;
  private boolean[][] mapaQR;

  private JButton btnConfirmar;
  private JLabel lblTimer;
  private JLabel lblEstadoQR;
  private JPanel panelQRWrap;
  private Timer countdown;
  private int segundosRestantes;

  public RegistrarMetodoPagoQr(
    SeleccionarMetodoPago seleccionarMetodoPago,
    JFrame mainFrame,
    VentasControl control,
    Runnable onVentaFinalizada) {
    super("Registrar Pago CoDi");
    this.seleccionarMetodoPago = seleccionarMetodoPago;
    this.mainFrame = mainFrame;
    this.control = control;
    this.onVentaFinalizada = onVentaFinalizada;

    configurarVentana();
    generarNuevoQR();
    inicializarContenido();
    iniciarTimer();
  }

  private void configurarVentana() {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());
  }

  private void generarNuevoQR() {
    referencia = generarReferencia();
    mapaQR = generarMapaQR(21);
  }

  private void iniciarTimer() {
    segundosRestantes = SEGUNDOS_EXPIRACION;
    countdown = new Timer(1000, e -> tickTimer());
    countdown.start();
  }

  private void tickTimer() {
    segundosRestantes--;
    actualizarLabelTimer();

    if (segundosRestantes <= 0) {
      onQRExpirado();
    }
  }

  private void actualizarLabelTimer() {
    int min = segundosRestantes / 60;
    int seg = segundosRestantes % 60;
    lblTimer.setText(String.format("%d:%02d", min, seg));

    // Cambia color al acercarse a expirar
    if (segundosRestantes <= 30) {
      lblTimer.setForeground(Colores.ROJO);
    } else if (segundosRestantes <= 60) {
      lblTimer.setForeground(new Color(161, 110, 0));
    } else {
      lblTimer.setForeground(Colores.GRIS_TEXTO);
    }
  }

  private void onQRExpirado() {
    countdown.stop();
    btnConfirmar.setEnabled(false);
    lblEstadoQR.setText("QR expirado");
    lblEstadoQR.setForeground(Colores.ROJO);
    lblTimer.setText("0:00");
    lblTimer.setForeground(Colores.ROJO);
    panelQRWrap.repaint();
  }

  private void inicializarContenido() {
    JPanel root = crearPanelBase();
    root.add(crearTopBar(), BorderLayout.NORTH);
    root.add(crearCuerpo(), BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
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

  private JPanel crearCuerpo() {
    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));
    cuerpo.add(crearFilaVolver(), BorderLayout.NORTH);
    cuerpo.add(crearCentrado(crearScroll(crearCard()), 240, 10), BorderLayout.CENTER);
    return cuerpo;
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
    countdown.stop();
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

  private JPanel crearHeader() {
    JPanel header = new JPanel(new BorderLayout(14, 0));
    header.setOpaque(false);

    JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    izq.setOpaque(false);
    izq.add(crearIconoBox());
    izq.add(crearTituloLabel());

    header.add(izq, BorderLayout.WEST);
    header.add(crearPanelTimer(), BorderLayout.EAST);
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
    lbl.setFont(Fuentes.b(16));
    lbl.setForeground(Colores.BLANCO);
    box.add(lbl);
    return box;
  }

  private JLabel crearTituloLabel() {
    JLabel lbl = new JLabel("Pago con CoDi");
    lbl.setFont(Fuentes.b(24));
    lbl.setForeground(Colores.TEXTO_OSCURO);
    return lbl;
  }

  private JPanel crearPanelTimer() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setOpaque(false);

    JLabel lblTit = new JLabel("Expira en");
    lblTit.setFont(Fuentes.r(11));
    lblTit.setForeground(Colores.GRIS_TEXTO);
    lblTit.setAlignmentX(CENTER_ALIGNMENT);

    lblTimer = new JLabel("3:00");
    lblTimer.setFont(Fuentes.b(18));
    lblTimer.setForeground(Colores.GRIS_TEXTO);
    lblTimer.setAlignmentX(CENTER_ALIGNMENT);

    lblEstadoQR = new JLabel("Vigente");
    lblEstadoQR.setFont(Fuentes.r(11));
    lblEstadoQR.setForeground(new Color(21, 128, 61));
    lblEstadoQR.setAlignmentX(CENTER_ALIGNMENT);

    p.add(lblTit);
    p.add(Box.createVerticalStrut(2));
    p.add(lblTimer);
    p.add(Box.createVerticalStrut(2));
    p.add(lblEstadoQR);
    return p;
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

    JLabel txt = new JLabel("Total a pagar", SwingConstants.CENTER);
    txt.setFont(Fuentes.r(13));
    txt.setForeground(new Color(220, 200, 255));

    JLabel val = new JLabel(
      String.format("$%,.2f", control.getVentaActual().getTotal()),
      SwingConstants.CENTER);
    val.setFont(Fuentes.b(38));
    val.setForeground(Colores.BLANCO);

    caja.add(txt);
    caja.add(val);
    return caja;
  }

  private JPanel crearPanelQR() {
    JPanel contenedor = new JPanel();
    contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
    contenedor.setOpaque(false);

    panelQRWrap = crearMarcoQR();
    panelQRWrap.setAlignmentX(CENTER_ALIGNMENT);

    JLabel lblRef = new JLabel("Referencia: " + referencia, SwingConstants.CENTER);
    lblRef.setFont(Fuentes.r(11));
    lblRef.setForeground(Colores.GRIS_TEXTO);
    lblRef.setAlignmentX(CENTER_ALIGNMENT);

    contenedor.add(panelQRWrap);
    contenedor.add(Box.createVerticalStrut(8));
    contenedor.add(lblRef);
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
        g.setColor(segundosRestantes > 0 ? Colores.MORADO : Colores.BORDE_GRIS);
        g.setStroke(new BasicStroke(2));
        g.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));
        super.paintComponent(g2d);
      }

    };
    marco.setOpaque(false);
    marco.setBorder(new EmptyBorder(16, 16, 16, 16));
    marco.add(crearComponenteGraficoQR(), BorderLayout.CENTER);
    return marco;
  }

  private JComponent crearComponenteGraficoQR() {
    JPanel qr = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        super.paintComponent(g2d);
        dibujarMapaQR((Graphics2D) g2d, mapaQR, getWidth(), getHeight());
      }

      @Override
      public Dimension getPreferredSize() {
        return new Dimension(200, 200);
      }

    };
    qr.setOpaque(false);
    return qr;
  }

  private void dibujarMapaQR(Graphics2D g, boolean[][] mapa, int w, int h) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int n = mapa.length;
    int cell = Math.min(w, h) / n;
    int offX = (w - n * cell) / 2;
    int offY = (h - n * cell) / 2;

    Color colorModulo = segundosRestantes > 0 ? Colores.MORADO : Colores.BORDE_GRIS;

    for (int y = 0; y < n; y++) {
      for (int x = 0; x < n; x++) {
        g.setColor(mapa[y][x] ? colorModulo : Colores.BLANCO);
        g.fillRect(offX + x * cell, offY + y * cell, cell, cell);
      }
    }
    dibujarMarcador(g, offX, offY, cell, 7, colorModulo);
    dibujarMarcador(g, offX + (n - 7) * cell, offY, cell, 7, colorModulo);
    dibujarMarcador(g, offX, offY + (n - 7) * cell, cell, 7, colorModulo);
  }

  private void dibujarMarcador(Graphics2D g, int ox, int oy, int cell, int size, Color color) {
    g.setColor(color);
    g.fillRect(ox, oy, size * cell, size * cell);
    g.setColor(Colores.BLANCO);
    g.fillRect(ox + cell, oy + cell, (size - 2) * cell, (size - 2) * cell);
    g.setColor(color);
    g.fillRect(ox + 2 * cell, oy + 2 * cell, (size - 4) * cell, (size - 4) * cell);
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
    tit.setFont(Fuentes.b(14));
    tit.setForeground(Colores.TEXTO_OSCURO);
    ci.gridy = 0;
    ci.insets = new Insets(0, 0, 10, 0);
    box.add(tit, ci);

    String[] pasos = {
      "Abra su aplicación bancaria y seleccione CoDi",
      "Escanee el código QR mostrado en pantalla",
      "Confirme el monto y autorice el pago en su app",
      "Haga clic en \"Confirmar\" para registrar el pago"
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
    num.setFont(Fuentes.b(13));
    num.setForeground(Colores.MORADO);

    JLabel txt = new JLabel(texto);
    txt.setFont(Fuentes.r(13));
    txt.setForeground(Colores.TEXTO_OSCURO);

    fila.add(num);
    fila.add(txt);
    return fila;
  }

  private JButton crearBotonConfirmar() {
    btnConfirmar = new JButton("Confirmar Pago CoDi") {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Fuentes.b(15));
        setForeground(Colores.BLANCO);
        setPreferredSize(new Dimension(0, 54));
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
        boolean habilitado = isEnabled();
        g.setColor(habilitado
          ? (ov ? Colores.MORADO_HOVER : Colores.MORADO)
          : Colores.GRIS_DISABLED);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    btnConfirmar.addActionListener(e -> onConfirmarPago());
    return btnConfirmar;
  }

  private void onConfirmarPago() {
    if (segundosRestantes <= 0) {
      JOptionPane.showMessageDialog(this,
        "El código QR ha expirado. Regresa al menú anterior y vuelve a intentarlo.",
        "QR Expirado", JOptionPane.WARNING_MESSAGE);
      return;
    }

    btnConfirmar.setEnabled(false);
    btnConfirmar.setText("Procesando...");
    countdown.stop();

    SwingWorker<ResultadoPagoDTO, Void> worker = new SwingWorker<>() {
      @Override
      protected ResultadoPagoDTO doInBackground() {
        return control.procesarPagoCoDi(new PagoQrDTO(referencia));
      }

      @Override
      protected void done() {
        try {
          manejarResultado(get());
        } catch (java.util.concurrent.ExecutionException ex) {
          restaurarBoton();
          mostrarError(ex.getCause() != null
            ? ex.getCause().getMessage() : ex.getMessage());
        } catch (InterruptedException ex) {
          restaurarBoton();
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
      restaurarBoton();
      mostrarRechazo(resultado.getMensaje());
    }
  }

  private void finalizarVenta() {
    countdown.stop();
    try {
      control.finalizarVenta(TipoPago.QR);
      TicketDTO ticketDTO = control.generarTicket();
      setVisible(false);
      new PantallaTicket(mainFrame, ticketDTO, onVentaFinalizada, control);
    } catch (Exception ex) {
      mostrarError("Pago aprobado, pero error al cerrar la venta:\n" + ex.getMessage());
    }
  }

  private JPanel crearTopBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));

    JButton btn = Botones.amarillo("Menú Principal");
    btn.setForeground(new Color(30, 30, 30));
    btn.addActionListener(e -> {
      countdown.stop();
      dispose();
      new MenuPrincipal(control.getUsuarioActivo(), control).setVisible(true);
    });
    bar.add(btn);
    return bar;
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

  private JButton crearBtnTexto(String texto) {
    JButton b = new JButton(texto);
    b.setContentAreaFilled(false);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setForeground(Colores.TEXTO_OSCURO);
    b.setFont(Fuentes.r(14));
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return b;
  }

  private String generarReferencia() {
    LocalDate hoy = LocalDate.now();
    int r = new Random().nextInt(9000) + 1000;
    return String.format("CODI-%d-%02d%02d-%d",
      hoy.getYear(), hoy.getMonthValue(), hoy.getDayOfMonth(), r);
  }

  private boolean[][] generarMapaQR(int n) {
    Random rnd = new Random();
    boolean[][] m = new boolean[n][n];
    for (int y = 0; y < n; y++) {
      for (int x = 0; x < n; x++) {
        m[y][x] = rnd.nextBoolean();
      }
    }
    // Limpiar esquinas para los marcadores
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        m[i][j] = false;
        m[i][n - 1 - j] = false;
        m[n - 1 - i][j] = false;
      }
    }
    return m;
  }

  private JPanel crearCard() {
    JPanel card = crearContenedorTarjeta();
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    int row = 0;

    c.gridy = row++;
    c.insets = new Insets(0, 0, 18, 0);
    card.add(crearHeader(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 20, 0);
    card.add(crearCajaTotal(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 14, 0);
    card.add(crearPanelQR(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 14, 0);
    card.add(crearPanelEstado(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 20, 0);
    card.add(crearInstrucciones(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 0, 0);
    card.add(crearBotonEscanear(), c);

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
    panel.setBorder(new EmptyBorder(12, 16, 12, 16));

    lblEstadoQR = new JLabel("● Esperando que el cliente escanee el código");
    lblEstadoQR.setFont(Fuentes.b(13));
    lblEstadoQR.setForeground(new Color(100, 116, 139));

    panel.add(lblEstadoQR);
    return panel;
  }

  private JButton crearBotonEscanear() {
    btnConfirmar = new JButton("Simular Escaneo") {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Fuentes.b(15));
        setForeground(Colores.BLANCO);
        setPreferredSize(new Dimension(0, 54));
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
        boolean habilitado = isEnabled();
        g.setColor(habilitado
          ? (ov ? Colores.MORADO_HOVER : Colores.MORADO)
          : Colores.GRIS_DISABLED);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    btnConfirmar.addActionListener(e -> onSimularEscaneo());
    return btnConfirmar;
  }

  private void onSimularEscaneo() {
    if (segundosRestantes <= 0) {
      JOptionPane.showMessageDialog(this,
        "El código QR ha expirado. Vuelve al menú anterior e intenta de nuevo.",
        "QR Expirado", JOptionPane.WARNING_MESSAGE);
      return;
    }

    btnConfirmar.setEnabled(false);
    countdown.stop();

    actualizarEstado("📱 Escaneando código QR...", new Color(109, 40, 217));

    Timer pasoEscaneo = new Timer(1500, e -> {
      actualizarEstado("🔄 Conectando con el banco...", new Color(161, 110, 0));
      procesarConBanco();
    });
    pasoEscaneo.setRepeats(false);
    pasoEscaneo.start();
  }

  private void procesarConBanco() {
    SwingWorker<ResultadoPagoDTO, Void> worker = new SwingWorker<>() {
      @Override
      protected ResultadoPagoDTO doInBackground() throws Exception {
        Thread.sleep(1000);
        return control.procesarPagoCoDi(new PagoQrDTO(referencia));
      }

      @Override
      protected void done() {
        try {
          ResultadoPagoDTO resultado = get();
          if (resultado.isAprobado()) {
            actualizarEstado("✓ Pago aprobado por el banco", new Color(21, 128, 61));
            Timer pasoFinal = new Timer(800, e -> finalizarVenta());
            pasoFinal.setRepeats(false);
            pasoFinal.start();
          } else {
            actualizarEstado("✗ Pago rechazado", Colores.ROJO);
            mostrarRechazo(resultado.getMensaje());
            restaurarBoton();
          }
        } catch (java.util.concurrent.ExecutionException ex) {
          actualizarEstado("✗ Error al procesar", Colores.ROJO);
          mostrarError(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
          restaurarBoton();
        } catch (InterruptedException ex) {
          restaurarBoton();
        }
      }

    };
    worker.execute();
  }

  private void actualizarEstado(String texto, Color color) {
    lblEstadoQR.setText(texto);
    lblEstadoQR.setForeground(color);
    lblEstadoQR.getParent().revalidate();
    lblEstadoQR.getParent().repaint();
  }

  private void restaurarBoton() {
    btnConfirmar.setEnabled(true);
    btnConfirmar.setText("Simular Escaneo");
    actualizarEstado("● Esperando que el cliente escanee el código", new Color(100, 116, 139));
    if (segundosRestantes > 0) {
      countdown.start();
    }
  }

  private void mostrarError(String mensaje) {
    JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void mostrarRechazo(String mensaje) {
    JOptionPane.showMessageDialog(this,
      "Pago CoDi rechazado:\n\n" + mensaje, "Rechazado", JOptionPane.WARNING_MESSAGE);
  }

}
