package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.presentacion.utilidad.Colores;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;

public class RegistrarMetodoPagoTarjeta extends JFrame {

  private final SeleccionarMetodoPago seleccionarMetodoPago;
  private final JFrame frame;

  private final IVentas ventasFachada;
  private final IInventario inventarioFachada;
  private final IUsuarios usuariosFachada;
  private final IProveedores proveedoresFachada;

  private final UsuarioDTO usuarioActivo;

  private final VentaDTO ventaActual;

  private final BigDecimal total;
  private final Runnable onVentaFinalizada;

  public RegistrarMetodoPagoTarjeta(
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

    super("Pago con Tarjeta");
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

    configurarVentana();
    inicializarComponentes();
  }

  private void configurarVentana() {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(frame.getWidth(), frame.getHeight());
    setLocation(frame.getLocation());
  }

  private void inicializarComponentes() {
    JPanel root = fondoPanel();
    root.add(crearTopBar(), BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));
    cuerpo.add(btnVolverRow(), BorderLayout.NORTH);

    JScrollPane scroll = scroll(buildCard());
    cuerpo.add(centrar(scroll, 240, 10), BorderLayout.CENTER);

    root.add(cuerpo, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel buildCard() {
    JPanel card = tarjetaBlanca();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(28, 32, 32, 32));

    card.add(header("TC", "Pago con Tarjeta", Colores.AZUL));
    card.add(Box.createVerticalStrut(22));
    card.add(cajaTotalColor(total, Colores.AZUL_CLARO, Colores.AZUL));
    card.add(Box.createVerticalStrut(20));
    card.add(instrucciones(
      new String[]{
        "Inserte o deslice la tarjeta en el lector",
        "Ingrese el PIN cuando se le solicite",
        "Espere la confirmación de la transacción"},
      Colores.AZUL, Colores.FONDO_GRIS_CLARO));
    card.add(Box.createVerticalStrut(20));

    card.add(label("Número de tarjeta"));
    card.add(Box.createVerticalStrut(6));
    JTextField campoNum = pill("**** **** **** ****");
    formatearTarjeta(campoNum);
    card.add(campoNum);
    card.add(Box.createVerticalStrut(14));

    card.add(label("Nombre del titular"));
    card.add(Box.createVerticalStrut(6));
    JTextField campoNom = pill("Nombre como aparece en la tarjeta");
    card.add(campoNom);
    card.add(Box.createVerticalStrut(24));

    JButton btnProcesar = boton("Procesar Pago", Colores.AZUL, Colores.AZUL_HOVER);
    btnProcesar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
    btnProcesar.addActionListener(e -> procesarConBanco(campoNum.getText(), campoNom.getText()));
    card.add(btnProcesar);

    return card;
  }

  private void procesarConBanco(String numRaw, String titular) {
    String numero = numRaw.replaceAll("\\s", "");

    if (numero.length() != 16 || !numero.matches("[0-9]+")) {
      JOptionPane.showMessageDialog(this, "Ingrese un número de tarjeta válido (16 dígitos).", "Dato inválido", JOptionPane.WARNING_MESSAGE);
      return;
    }
    if (titular.isBlank() || titular.equals("Nombre como aparece en la tarjeta")) {
      JOptionPane.showMessageDialog(this, "Ingrese el nombre del titular.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
      return;
    }

    setEnabled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    SwingWorker<ResultadoPagoDTO, Void> worker = new SwingWorker<>() {
      @Override
      protected ResultadoPagoDTO doInBackground() {
        return ventasFachada.procesarPagoTarjeta(ventaActual, new PagoTarjetaDTO(numero, titular));
      }

      @Override
      protected void done() {
        setCursor(Cursor.getDefaultCursor());
        setEnabled(true);
        try {
          manejarResultado(get(), onVentaFinalizada, BigDecimal.ZERO);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(RegistrarMetodoPagoTarjeta.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }

    };
    worker.execute();
  }

  private void manejarResultado(ResultadoPagoDTO resultado, Runnable onConfirmado, BigDecimal efectivo) {
    if (resultado.isAprobado()) {
      try {
        ventasFachada.procesarFinalizarVenta(ventaActual);
        TicketDTO ticketDTO = ventasFachada.generarTicket(ventaActual, efectivo);
        this.setVisible(false);
        new PantallaTicket(frame, ticketDTO, onConfirmado, usuariosFachada, ventasFachada, inventarioFachada, proveedoresFachada);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error al finalizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(this, "❌ Pago rechazado:\n\n" + resultado.getMensaje(), "Rechazado", JOptionPane.WARNING_MESSAGE);
    }
  }

  private JPanel crearTopBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));

    JButton btn = boton("Menu Principal", Colores.AMARILLO_BTN, Colores.AMARILLO_BTN_HOVER);
    btn.setForeground(Colores.TEXTO_OSCURO);
    btn.setPreferredSize(new Dimension(160, 38));
    btn.addActionListener(e -> {
      dispose();
      new MenuPrincipal(usuarioActivo, usuariosFachada, ventasFachada, inventarioFachada, proveedoresFachada).setVisible(true);
    });

    bar.add(btn);
    return bar;
  }

  private JPanel btnVolverRow() {
    JButton b = new JButton("← Volver a métodos de pago");
    b.setContentAreaFilled(false);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    b.setForeground(Colores.TEXTO_OSCURO);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    b.addActionListener(e -> {
      dispose();
      seleccionarMetodoPago.setVisible(true);
    });

    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    p.setOpaque(false);
    p.add(b);
    return p;
  }

  private JPanel fondoPanel() {
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

    JPanel ib = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(col);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
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

  private JPanel cajaTotalColor(BigDecimal total, Color bg, Color fg) {
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

  private JPanel instrucciones(String[] pasos, Color colorNum, Color bg) {
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
      JPanel f = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
      f.setOpaque(false);
      JLabel n = new JLabel((i + 1) + ".");
      n.setFont(new Font("Segoe UI", Font.BOLD, 13));
      n.setForeground(colorNum);
      JLabel t = new JLabel(pasos[i]);
      t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
      t.setForeground(Colores.TEXTO_OSCURO);
      f.add(n);
      f.add(t);
      c.gridy = i + 1;
      c.insets = new Insets(0, 0, i < pasos.length - 1 ? 6 : 0, 0);
      box.add(f, c);
    }
    return box;
  }

  private JLabel label(String txt) {
    JLabel l = new JLabel(txt);
    l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    l.setForeground(Colores.TEXTO_OSCURO);
    l.setAlignmentX(LEFT_ALIGNMENT);
    return l;
  }

  private JTextField pill(String ph) {
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
    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    tf.setAlignmentX(LEFT_ALIGNMENT);
    tf.setText(ph);
    tf.setForeground(Colores.GRIS_TEXTO);
    tf.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        if (tf.getText().equals(ph)) {
          tf.setText("");
          tf.setForeground(Colores.TEXTO_OSCURO);
        }
      }

      public void focusLost(FocusEvent e) {
        if (tf.getText().isEmpty()) {
          tf.setText(ph);
          tf.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });
    return tf;
  }

  private void formatearTarjeta(JTextField tf) {
    tf.getDocument().addDocumentListener(new DocumentListener() {
      boolean ed = false;

      private void fmt() {
        if (ed) {
          return;
        }
        ed = true;
        String r = tf.getText().replaceAll("[^0-9]", "");
        if (r.length() > 16) {
          r = r.substring(0, 16);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < r.length(); i++) {
          if (i > 0 && i % 4 == 0) {
            sb.append(' ');
          }
          sb.append(r.charAt(i));
        }
        tf.setText(sb.toString());
        ed = false;
      }

      public void insertUpdate(DocumentEvent e) {
        fmt();
      }

      public void removeUpdate(DocumentEvent e) {
        fmt();
      }

      public void changedUpdate(DocumentEvent e) {
      }

    });
  }

  private JButton boton(String txt, Color base, Color hover) {
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
    return b;
  }

}
