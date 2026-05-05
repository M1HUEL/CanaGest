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
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

public class RegistrarMetodoPagoCoDi extends JFrame {

  private final SeleccionarMetodoPago seleccionarMetodoPago;
  private final JFrame frame;
  private final IVentas ventasFachada;
  private final IInventario inventarioFachada;
  private final IUsuarios usuariosFachada;
  private final IProveedores proveedoresFachada;
  private final VentaDTO ventaActual;
  private final BigDecimal total;
  private final Runnable onVentaFinalizada;
  private final UsuarioDTO usuarioActivo;
  private final String referencia;

  public RegistrarMetodoPagoCoDi(
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

    super("Registrar Pago CoDi");
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

    JPanel root = panelBase();
    root.add(crearTopBar(), BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));
    cuerpo.add(seccionVolver(), BorderLayout.NORTH);
    cuerpo.add(crearCentrado(crearScroll(buildCard()), 240, 10), BorderLayout.CENTER);

    root.add(cuerpo, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private void configurarVentana() {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(frame.getWidth(), frame.getHeight());
    setLocation(frame.getLocation());
  }

  private JPanel panelBase() {
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

  private JPanel seccionVolver() {
    JButton btnVolver = btnTexto("← Volver a métodos de pago");
    btnVolver.addActionListener(e -> {
      dispose();
      seleccionarMetodoPago.setVisible(true);
    });
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    row.setOpaque(false);
    row.add(btnVolver);
    return row;
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

  private JPanel buildCard() {
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

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
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

    JButton btnConfirmar = crearBoton("Confirmar Pago CoDi", Colores.MORADO, Colores.MORADO_HOVER);
    btnConfirmar.setPreferredSize(new Dimension(0, 54));
    btnConfirmar.addActionListener(e -> procesarConBanco());

    c.gridy = row++;
    c.insets = new Insets(0, 0, 0, 0);
    card.add(btnConfirmar, c);

    return card;
  }

  private void procesarConBanco() {
    setEnabled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    SwingWorker<ResultadoPagoDTO, Void> worker = new SwingWorker<>() {
      @Override
      protected ResultadoPagoDTO doInBackground() {
        return ventasFachada.procesarPagoCoDi(
          ventaActual, new PagoQrDTO(referencia));
      }

      @Override
      protected void done() {
        setCursor(Cursor.getDefaultCursor());
        setEnabled(true);
        try {
          manejarResultado(get());
        } catch (java.util.concurrent.ExecutionException ex) {
          Throwable causa = ex.getCause();
          JOptionPane.showMessageDialog(RegistrarMetodoPagoCoDi.this,
            causa != null ? causa.getMessage() : ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException ex) {
          JOptionPane.showMessageDialog(RegistrarMetodoPagoCoDi.this,
            ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        }
      }

    };
    worker.execute();
  }

  private void manejarResultado(ResultadoPagoDTO resultado) {
    if (resultado.isAprobado()) {
      try {
        ventasFachada.procesarFinalizarVenta(ventaActual);
        TicketDTO ticketDTO = ventasFachada.generarTicket(ventaActual, BigDecimal.ZERO);
        this.setVisible(false);
        new PantallaTicket(frame, ticketDTO, onVentaFinalizada,
          usuariosFachada, ventasFachada, inventarioFachada, proveedoresFachada);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
          "Pago aprobado, pero error al cerrar la venta:\n" + ex.getMessage(),
          "Error al finalizar", JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(this,
        "❌ Pago CoDi rechazado:\n\n" + resultado.getMensaje(),
        "Rechazado", JOptionPane.WARNING_MESSAGE);
    }
  }

  private JPanel crearHeader() {
    JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    header.setOpaque(false);

    JPanel icoBox = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.MORADO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    icoBox.setOpaque(false);
    icoBox.setPreferredSize(new Dimension(52, 52));

    JLabel icoLbl = new JLabel("QR", SwingConstants.CENTER);
    icoLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
    icoLbl.setForeground(Colores.BLANCO);
    icoBox.add(icoLbl);

    JLabel titLbl = new JLabel("Pago con CoDi");
    titLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titLbl.setForeground(Colores.TEXTO_OSCURO);

    header.add(icoBox);
    header.add(titLbl);
    return header;
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

    JLabel lTxt = new JLabel("Total a pagar", SwingConstants.CENTER);
    lTxt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lTxt.setForeground(new Color(220, 200, 255));

    JLabel lVal = new JLabel(String.format("$%,.2f", total), SwingConstants.CENTER);
    lVal.setFont(new Font("Segoe UI", Font.BOLD, 38));
    lVal.setForeground(Colores.BLANCO);

    caja.add(lTxt);
    caja.add(lVal);
    return caja;
  }

  private JPanel crearPanelQR() {
    JPanel contenedor = new JPanel();
    contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
    contenedor.setOpaque(false);

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

    JPanel qr = new JPanel() {
      private final boolean[][] mapa = generarMapaQR(21);

      @Override
      protected void paintComponent(Graphics g2d) {
        super.paintComponent(g2d);
        Graphics2D g = (Graphics2D) g2d;
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

      @Override
      public Dimension getPreferredSize() {
        return new Dimension(200, 200);
      }

    };
    qr.setOpaque(false);
    marco.add(qr, BorderLayout.CENTER);

    JLabel lblRef = new JLabel("Referencia: " + referencia, SwingConstants.CENTER);
    lblRef.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    lblRef.setForeground(Colores.GRIS_TEXTO);
    lblRef.setAlignmentX(CENTER_ALIGNMENT);

    contenedor.add(marco);
    contenedor.add(Box.createVerticalStrut(8));
    contenedor.add(lblRef);
    return contenedor;
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
      JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
      fila.setOpaque(false);
      JLabel num = new JLabel((i + 1) + ".");
      num.setFont(new Font("Segoe UI", Font.BOLD, 13));
      num.setForeground(Colores.MORADO);
      JLabel txt = new JLabel(pasos[i]);
      txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
      txt.setForeground(Colores.TEXTO_OSCURO);
      fila.add(num);
      fila.add(txt);
      ci.gridy = i + 1;
      ci.insets = new Insets(0, 0, i < pasos.length - 1 ? 6 : 0, 0);
      box.add(fila, ci);
    }
    return box;
  }

  private JPanel crearTopBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));
    JButton btn = crearBoton("Menu Principal", Colores.AMARILLO_BTN, Colores.AMARILLO_BTN_HOVER);
    btn.setForeground(Colores.TEXTO_OSCURO);
    btn.setPreferredSize(new Dimension(160, 38));
    btn.addActionListener(e -> {
      dispose();
      new MenuPrincipal(usuarioActivo, usuariosFachada, ventasFachada,
        inventarioFachada, proveedoresFachada).setVisible(true);
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

  private JButton btnTexto(String texto) {
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

}
