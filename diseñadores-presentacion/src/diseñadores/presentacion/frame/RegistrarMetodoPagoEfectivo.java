package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class RegistrarMetodoPagoEfectivo extends JFrame {

  private double recibido = 0.0;
  private final double totalAPagar;
  private final IVentas facade;
  private final Venta ventaActual;

  private JLabel lblRecibido, lblCambio;
  private JButton btnCompletar;

  public RegistrarMetodoPagoEfectivo(SeleccionarMetodoPago pantallaPago, JFrame mainFrame,
    IVentas facade, Venta ventaActual,
    double total, Runnable onConfirmado) {
    super("Pago en Efectivo");
    this.totalAPagar = total;
    this.facade = facade;
    this.ventaActual = ventaActual;

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());

    JPanel root = pantallaPago.fondoAmarillo();
    root.add(pantallaPago.topBar(), BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));

    JButton btnVolver = new JButton("Volver a metodos de pago");
    btnVolver.setContentAreaFilled(false);
    btnVolver.setBorderPainted(false);
    btnVolver.setFocusPainted(false);
    btnVolver.setForeground(Colores.TEXTO_OSCURO);
    btnVolver.setFont(Fuentes.r(14));
    btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnVolver.addActionListener(e -> {
      dispose();
      pantallaPago.setVisible(true);
    });
    JPanel volverRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    volverRow.setOpaque(false);
    volverRow.add(btnVolver);
    cuerpo.add(volverRow, BorderLayout.NORTH);

    JPanel card = buildCard(total, pantallaPago, mainFrame, onConfirmado);
    JPanel centrado = pantallaPago.centrado(card, 240, 12);
    cuerpo.add(centrado, BorderLayout.CENTER);

    root.add(cuerpo, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel buildCard(double total, SeleccionarMetodoPago pantallaPago,
    JFrame mainFrame, Runnable onConfirmado) {
    JPanel card = new JPanel() {
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
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(28, 32, 28, 32));

    JLabel titulo = new JLabel("Pago en Efectivo");
    titulo.setFont(Fuentes.b(24));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    card.add(titulo);
    card.add(Box.createVerticalStrut(22));

    JPanel cajasRow = new JPanel(new GridLayout(1, 3, 12, 0));
    cajasRow.setOpaque(false);
    cajasRow.setAlignmentX(LEFT_ALIGNMENT);
    cajasRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

    cajasRow.add(cajaInfo("Total a pagar", String.format("$%.2f", total), Colores.TEXTO_OSCURO, Colores.BLANCO));

    lblRecibido = new JLabel(String.format("$%.2f", recibido), SwingConstants.CENTER);
    lblRecibido.setFont(Fuentes.b(22));
    lblRecibido.setForeground(Colores.AZUL);
    cajasRow.add(cajaInfoConLabel("Recibido", lblRecibido, Colores.AZUL_CLARO));

    lblCambio = new JLabel("$0.00", SwingConstants.CENTER);
    lblCambio.setFont(Fuentes.b(22));
    lblCambio.setForeground(Colores.GRIS_TEXTO);
    cajasRow.add(cajaInfoConLabel("Cambio", lblCambio, Colores.BLANCO));

    card.add(cajasRow);
    card.add(Box.createVerticalStrut(22));

    JLabel lblDenom = new JLabel("Denominaciones rapidas");
    lblDenom.setFont(Fuentes.b(14));
    lblDenom.setForeground(Colores.TEXTO_OSCURO);
    lblDenom.setAlignmentX(LEFT_ALIGNMENT);
    card.add(lblDenom);
    card.add(Box.createVerticalStrut(10));

    JPanel gridDenom = new JPanel(new GridLayout(2, 3, 10, 10));
    gridDenom.setOpaque(false);
    gridDenom.setAlignmentX(LEFT_ALIGNMENT);
    gridDenom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
    for (int v : new int[]{20, 50, 100, 200, 500, 1000}) {
      gridDenom.add(botonDenom("$" + v, v));
    }
    card.add(gridDenom);
    card.add(Box.createVerticalStrut(18));

    JLabel lblCustom = new JLabel("Cantidad personalizada");
    lblCustom.setFont(Fuentes.b(14));
    lblCustom.setForeground(Colores.TEXTO_OSCURO);
    lblCustom.setAlignmentX(LEFT_ALIGNMENT);
    card.add(lblCustom);
    card.add(Box.createVerticalStrut(8));

    JPanel customRow = new JPanel(new BorderLayout(10, 0));
    customRow.setOpaque(false);
    customRow.setAlignmentX(LEFT_ALIGNMENT);
    customRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

    JTextField campoCustom = campoPill("Ingrese cantidad");
    JButton btnAgregar = botonVerde("Agregar");
    btnAgregar.setPreferredSize(new Dimension(110, 44));
    btnAgregar.addActionListener(e -> {
      try {
        double val = Double.parseDouble(campoCustom.getText().trim().replace(",", "."));
        if (val > 0) {
          recibido += val;
          actualizarUI();
          campoCustom.setText("");
        }
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Ingrese un numero valido.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });
    campoCustom.addActionListener(e -> btnAgregar.doClick());
    customRow.add(campoCustom, BorderLayout.CENTER);
    customRow.add(btnAgregar, BorderLayout.EAST);
    card.add(customRow);
    card.add(Box.createVerticalStrut(20));

    JPanel accionesRow = new JPanel(new GridLayout(1, 2, 12, 0));
    accionesRow.setOpaque(false);
    accionesRow.setAlignmentX(LEFT_ALIGNMENT);
    accionesRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

    JButton btnLimpiar = pantallaPago.accionBtn("Limpiar", Colores.GRIS_BTN, Colores.GRIS_BTN_HOVER);
    btnLimpiar.addActionListener(e -> {
      recibido = 0;
      actualizarUI();
    });

    btnCompletar = new JButton("Completar Pago") {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            ov = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            ov = false;
            repaint();
          }

          public void mouseClicked(MouseEvent e) {
            if (recibido < totalAPagar) {
              return;
            }

            PagoEfectivoDTO pagoDTO = new PagoEfectivoDTO(recibido);
            ResultadoPagoDTO resultado = facade.procesarPagoEfectivo(ventaActual, pagoDTO);

            if (!resultado.isAprobado()) {
              JOptionPane.showMessageDialog(RegistrarMetodoPagoEfectivo.this,
                resultado.getMensaje(), "Pago rechazado", JOptionPane.WARNING_MESSAGE);
              return;
            }

            facade.procesarFinalizarVenta(ventaActual);

            TicketDTO ticketDTO = facade.generarTicket(ventaActual, recibido);

            RegistrarMetodoPagoEfectivo.this.setVisible(false);
            new PantallaTicket(mainFrame, ticketDTO, onConfirmado);
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean hab = recibido >= totalAPagar;
        g.setColor(hab ? (ov ? Colores.VERDE_HOVER : Colores.VERDE) : Colores.GRIS_DISABLED);
        setCursor(Cursor.getPredefinedCursor(hab ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    btnCompletar.setForeground(Colores.BLANCO);
    btnCompletar.setFont(Fuentes.b(15));

    accionesRow.add(btnLimpiar);
    accionesRow.add(btnCompletar);
    card.add(accionesRow);
    return card;
  }

  private void actualizarUI() {
    lblRecibido.setText(String.format("$%.2f", recibido));
    double cambio = facade.calcularCambio(ventaActual, recibido);
    lblCambio.setText(String.format("$%.2f", Math.max(cambio, 0)));
    lblCambio.setForeground(recibido >= totalAPagar ? Colores.VERDE : Colores.GRIS_TEXTO);
    btnCompletar.repaint();
  }

  private JButton botonDenom(String texto, int valor) {
    JButton b = botonVerde(texto);
    b.addActionListener(e -> {
      recibido += valor;
      actualizarUI();
    });
    return b;
  }

  private JButton botonVerde(String texto) {
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
        g.setColor(ov ? Colores.VERDE_HOVER : Colores.VERDE);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(16));
    return b;
  }

  private JPanel cajaInfo(String etiqueta, String valor, Color colorVal, Color bg) {
    JPanel p = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        g.setColor(Colores.BORDE_GRIS);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    p.setBorder(new EmptyBorder(10, 14, 10, 14));
    JLabel lE = new JLabel(etiqueta, SwingConstants.CENTER);
    lE.setFont(Fuentes.r(12));
    lE.setForeground(Colores.GRIS_TEXTO);
    JLabel lV = new JLabel(valor, SwingConstants.CENTER);
    lV.setFont(Fuentes.b(22));
    lV.setForeground(colorVal);
    p.add(lE);
    p.add(lV);
    return p;
  }

  private JPanel cajaInfoConLabel(String etiqueta, JLabel valorLabel, Color bg) {
    JPanel p = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        g.setColor(Colores.BORDE_GRIS);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    p.setBorder(new EmptyBorder(10, 14, 10, 14));
    JLabel lE = new JLabel(etiqueta, SwingConstants.CENTER);
    lE.setFont(Fuentes.r(12));
    lE.setForeground(Colores.GRIS_TEXTO);
    p.add(lE);
    p.add(valorLabel);
    return p;
  }

  private JTextField campoPill(String placeholder) {
    JTextField tf = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_INPUT);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    tf.setOpaque(false);
    tf.setBorder(new EmptyBorder(8, 14, 8, 14));
    tf.setFont(Fuentes.r(14));
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

}
