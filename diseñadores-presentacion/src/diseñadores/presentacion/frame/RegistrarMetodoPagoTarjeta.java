package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Colores;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class RegistrarMetodoPagoTarjeta extends JFrame {

  private final SeleccionarMetodoPago seleccionarMetodoPago;
  private final JFrame mainFrame;
  private final VentasControl control;
  private final Runnable onVentaFinalizada;

  public RegistrarMetodoPagoTarjeta(
    SeleccionarMetodoPago seleccionarMetodoPago,
    JFrame mainFrame,
    VentasControl control,
    Runnable onVentaFinalizada) {

    super("Pago con Tarjeta");
    this.seleccionarMetodoPago = seleccionarMetodoPago;
    this.mainFrame = mainFrame;
    this.control = control;
    this.onVentaFinalizada = onVentaFinalizada;

    configurarVentana();
    inicializarComponentes();
  }

  private void configurarVentana() {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());
  }

  private void inicializarComponentes() {
    JPanel root = crearFondoPanel();
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

  private JPanel crearCard() {
    JPanel card = crearTarjetaBlanca();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(28, 32, 32, 32));

    card.add(crearHeader("TC", "Pago con Tarjeta", Colores.AZUL));
    card.add(Box.createVerticalStrut(22));
    card.add(crearCajaTotal(control.getVentaActual().getTotal(), Colores.AZUL_CLARO, Colores.AZUL));
    card.add(Box.createVerticalStrut(20));
    card.add(crearSeccionInstrucciones());
    card.add(Box.createVerticalStrut(20));

    JTextField campoNum = crearCampoNumeroTarjeta(card);
    JTextField campoNom = crearCampoNombreTitular(card);

    card.add(Box.createVerticalStrut(24));
    card.add(crearBotonProcesar(campoNum, campoNom));
    return card;
  }

  private JPanel crearSeccionInstrucciones() {
    return crearInstrucciones(
      new String[]{
        "Inserte o deslice la tarjeta en el lector",
        "Ingrese el PIN cuando se le solicite",
        "Espere la confirmación de la transacción"},
      Colores.AZUL, Colores.FONDO_GRIS_CLARO);
  }

  private JTextField crearCampoNumeroTarjeta(JPanel card) {
    card.add(crearLabel("Número de tarjeta"));
    card.add(Box.createVerticalStrut(6));
    JTextField campo = crearPill("**** **** **** ****");
    aplicarFormatoTarjeta(campo);
    card.add(campo);
    card.add(Box.createVerticalStrut(14));
    return campo;
  }

  private JTextField crearCampoNombreTitular(JPanel card) {
    card.add(crearLabel("Nombre del titular"));
    card.add(Box.createVerticalStrut(6));
    JTextField campo = crearPill("Nombre como aparece en la tarjeta");
    card.add(campo);
    return campo;
  }

  private JButton crearBotonProcesar(JTextField campoNum, JTextField campoNom) {
    JButton btn = crearBoton("Procesar Pago", Colores.AZUL, Colores.AZUL_HOVER);
    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
    btn.addActionListener(e -> onProcesarPago(campoNum.getText(), campoNom.getText()));
    return btn;
  }

  private void onProcesarPago(String numRaw, String titular) {
    String numero = numRaw.replaceAll("\\s", "");
    if (validarDatos(numero, titular)) {
      ejecutarProcesoPago(numero, titular);
    }
  }

  private boolean validarDatos(String numero, String titular) {
    if (numero.length() != 16 || !numero.matches("[0-9]+")) {
      JOptionPane.showMessageDialog(this,
        "Ingrese un número de tarjeta válido (16 dígitos).", "Dato inválido", JOptionPane.WARNING_MESSAGE);
      return false;
    }
    if (titular.isBlank() || titular.equals("Nombre como aparece en la tarjeta")) {
      JOptionPane.showMessageDialog(this,
        "Ingrese el nombre del titular.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }

  private void ejecutarProcesoPago(String numero, String titular) {
    alternarEstadoCarga(true);
    SwingWorker<ResultadoPagoDTO, Void> worker = new SwingWorker<>() {
      @Override
      protected ResultadoPagoDTO doInBackground() {
        return control.procesarPagoTarjeta(new PagoTarjetaDTO(numero, titular));
      }

      @Override
      protected void done() {
        alternarEstadoCarga(false);
        try {
          manejarResultado(get());
        } catch (InterruptedException | ExecutionException ex) {
          JOptionPane.showMessageDialog(RegistrarMetodoPagoTarjeta.this,
            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }

    };
    worker.execute();
  }

  private void alternarEstadoCarga(boolean cargando) {
    setEnabled(!cargando);
    setCursor(cargando ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
  }

  private void manejarResultado(ResultadoPagoDTO resultado) {
    if (resultado.isAprobado()) {
      finalizarVenta();
    } else {
      JOptionPane.showMessageDialog(this,
        "Pago rechazado por el banco:\n\n" + resultado.getMensaje(),
        "Pago rechazado", JOptionPane.WARNING_MESSAGE);
    }
  }

  private void finalizarVenta() {
    try {
      control.finalizarVenta();
      TicketDTO ticketDTO = control.generarTicket();
      setVisible(false);
      new PantallaTicket(mainFrame, ticketDTO, onVentaFinalizada, control);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
        "Pago aprobado, pero ocurrió un error al cerrar la venta:\n" + ex.getMessage(),
        "Error al finalizar", JOptionPane.ERROR_MESSAGE);
    }
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

  private JPanel crearFondoPanel() {
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

    JPanel iconBox = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
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

  private JPanel crearCajaTotal(BigDecimal total, Color bg, Color fg) {
    JPanel c = new JPanel(new GridLayout(2, 1, 0, 8)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    c.setOpaque(false);
    c.setBorder(new EmptyBorder(22, 20, 22, 20));
    c.setAlignmentX(LEFT_ALIGNMENT);
    c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

    JLabel lt = new JLabel("Total a pagar", SwingConstants.CENTER);
    lt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lt.setForeground(Colores.GRIS_TEXTO);

    JLabel lv = new JLabel(String.format("$%,.2f", total), SwingConstants.CENTER);
    lv.setFont(new Font("Segoe UI", Font.BOLD, 38));
    lv.setForeground(fg);

    c.add(lt);
    c.add(lv);
    return c;
  }

  private JPanel crearInstrucciones(String[] pasos, Color colorNum, Color bg) {
    JPanel box = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    box.setOpaque(false);
    box.setBorder(new EmptyBorder(16, 18, 16, 18));
    box.setAlignmentX(LEFT_ALIGNMENT);
    box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;

    JLabel tit = new JLabel("Instrucciones:");
    tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
    tit.setForeground(Colores.TEXTO_OSCURO);
    c.gridy = 0;
    c.insets = new Insets(0, 0, 10, 0);
    box.add(tit, c);

    for (int i = 0; i < pasos.length; i++) {
      JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
      fila.setOpaque(false);
      JLabel num = new JLabel((i + 1) + ".");
      num.setFont(new Font("Segoe UI", Font.BOLD, 13));
      num.setForeground(colorNum);
      JLabel texto = new JLabel(pasos[i]);
      texto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
      texto.setForeground(Colores.TEXTO_OSCURO);
      fila.add(num);
      fila.add(texto);
      c.gridy = i + 1;
      c.insets = new Insets(0, 0, i < pasos.length - 1 ? 6 : 0, 0);
      box.add(fila, c);
    }
    return box;
  }

  private JLabel crearLabel(String txt) {
    JLabel l = new JLabel(txt);
    l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    l.setForeground(Colores.TEXTO_OSCURO);
    l.setAlignmentX(LEFT_ALIGNMENT);
    return l;
  }

  private JTextField crearPill(String placeholder) {
    JTextField tf = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        g.setColor(Colores.BORDE_GRIS);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        super.paintComponent(g2d);
      }

    };
    tf.setOpaque(false);
    tf.setBorder(new EmptyBorder(12, 14, 12, 14));
    tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    tf.setPreferredSize(new Dimension(0, 50));
    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    tf.setAlignmentX(LEFT_ALIGNMENT);
    tf.setText(placeholder);
    tf.setForeground(Colores.GRIS_TEXTO);
    tf.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        if (tf.getText().equals(placeholder)) {
          tf.setText("");
          tf.setForeground(Colores.TEXTO_OSCURO);
        }
      }

      public void focusLost(FocusEvent e) {
        if (tf.getText().isEmpty()) {
          tf.setText(placeholder);
          tf.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });
    return tf;
  }

  private void aplicarFormatoTarjeta(JTextField tf) {
    tf.getDocument().addDocumentListener(new DocumentListener() {
      boolean editando = false;

      private void formatear() {
        if (editando) {
          return;
        }
        SwingUtilities.invokeLater(() -> {
          if (editando) {
            return;
          }
          editando = true;
          try {
            String raw = tf.getText().replaceAll("[^0-9]", "");
            if (raw.length() > 16) {
              raw = raw.substring(0, 16);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < raw.length(); i++) {
              if (i > 0 && i % 4 == 0) {
                sb.append(' ');
              }
              sb.append(raw.charAt(i));
            }
            int caret = Math.min(tf.getCaretPosition(), sb.length());
            tf.setText(sb.toString());
            tf.setCaretPosition(caret);
          } finally {
            editando = false;
          }
        });
      }

      public void insertUpdate(DocumentEvent e) {
        formatear();
      }

      public void removeUpdate(DocumentEvent e) {
        formatear();
      }

      public void changedUpdate(DocumentEvent e) {
      }

    });
  }

  private JButton crearBoton(String txt, Color base, Color hover) {
    JButton b = new JButton(txt) {
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

}
