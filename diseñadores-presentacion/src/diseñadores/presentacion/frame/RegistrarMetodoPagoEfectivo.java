package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Componentes;
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

    JPanel root = Componentes.fondoAmarillo();
    root.add(Componentes.topBar(this), BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));
    cuerpo.add(Componentes.panelVolver("Volver a metodos de pago",
      () -> {
        dispose();
        pantallaPago.setVisible(true);
      }), BorderLayout.NORTH);
    cuerpo.add(Componentes.centrado(buildCard(total, mainFrame, onConfirmado), 240, 12), BorderLayout.CENTER);

    root.add(cuerpo, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel buildCard(double total, JFrame mainFrame, Runnable onConfirmado) {
    JPanel card = Componentes.tarjetaBlanca(20);
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(28, 32, 28, 32));

    JLabel titulo = Componentes.etiqueta("Pago en Efectivo", 24, true, Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    card.add(titulo);
    card.add(Box.createVerticalStrut(22));

    JPanel cajasRow = new JPanel(new GridLayout(1, 3, 12, 0));
    cajasRow.setOpaque(false);
    cajasRow.setAlignmentX(LEFT_ALIGNMENT);
    cajasRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
    cajasRow.add(cajaInfo("Total a pagar", String.format("$%.2f", total), Colores.TEXTO_OSCURO, Colores.BLANCO));

    lblRecibido = Componentes.etiquetaCentrada(String.format("$%.2f", recibido), 22, true, Colores.AZUL);
    cajasRow.add(cajaConLabel("Recibido", lblRecibido, Colores.AZUL_CLARO));

    lblCambio = Componentes.etiquetaCentrada("$0.00", 22, true, Colores.GRIS_TEXTO);
    cajasRow.add(cajaConLabel("Cambio", lblCambio, Colores.BLANCO));

    card.add(cajasRow);
    card.add(Box.createVerticalStrut(22));

    JLabel lblDenom = Componentes.etiqueta("Denominaciones rapidas", 14, true, Colores.TEXTO_OSCURO);
    lblDenom.setAlignmentX(LEFT_ALIGNMENT);
    card.add(lblDenom);
    card.add(Box.createVerticalStrut(10));

    JPanel gridDenom = new JPanel(new GridLayout(2, 3, 10, 10));
    gridDenom.setOpaque(false);
    gridDenom.setAlignmentX(LEFT_ALIGNMENT);
    gridDenom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
    for (int v : new int[]{20, 50, 100, 200, 500, 1000}) {
      gridDenom.add(botonDenominacion("$" + v, v));
    }
    card.add(gridDenom);
    card.add(Box.createVerticalStrut(18));

    JLabel lblCustom = Componentes.etiqueta("Cantidad personalizada", 14, true, Colores.TEXTO_OSCURO);
    lblCustom.setAlignmentX(LEFT_ALIGNMENT);
    card.add(lblCustom);
    card.add(Box.createVerticalStrut(8));
    card.add(filaCantidadPersonalizada());
    card.add(Box.createVerticalStrut(20));
    card.add(filaAcciones(mainFrame, onConfirmado));
    return card;
  }

  private JPanel filaCantidadPersonalizada() {
    JTextField campoCustom = Componentes.campoPill("Ingrese cantidad");
    JButton btnAgregar = Componentes.botonAccion("Agregar", Colores.VERDE, Colores.VERDE_HOVER);
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

    JPanel row = new JPanel(new BorderLayout(10, 0));
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    row.add(campoCustom, BorderLayout.CENTER);
    row.add(btnAgregar, BorderLayout.EAST);
    return row;
  }

  private JPanel filaAcciones(JFrame mainFrame, Runnable onConfirmado) {
    JButton btnLimpiar = Componentes.botonAccion("Limpiar", Colores.GRIS_BTN, Colores.GRIS_BTN_HOVER);
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
            confirmarPago(mainFrame, onConfirmado);
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

    JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
    row.add(btnLimpiar);
    row.add(btnCompletar);
    return row;
  }

  private void confirmarPago(JFrame mainFrame, Runnable onConfirmado) {
    if (recibido < totalAPagar) {
      return;
    }

    PagoEfectivoDTO pagoDTO = new PagoEfectivoDTO(recibido);
    ResultadoPagoDTO resultado = facade.procesarPagoEfectivo(ventaActual, pagoDTO);

    if (!resultado.isAprobado()) {
      JOptionPane.showMessageDialog(this, resultado.getMensaje(),
        "Pago rechazado", JOptionPane.WARNING_MESSAGE);
      return;
    }

    facade.procesarFinalizarVenta(ventaActual);
    TicketDTO ticketDTO = facade.generarTicket(ventaActual, recibido);

    this.setVisible(false);
    new PantallaTicket(mainFrame, ticketDTO, onConfirmado);
  }

  private void actualizarUI() {
    lblRecibido.setText(String.format("$%.2f", recibido));
    double cambio = facade.calcularCambio(ventaActual, recibido);
    lblCambio.setText(String.format("$%.2f", Math.max(cambio, 0)));
    lblCambio.setForeground(recibido >= totalAPagar ? Colores.VERDE : Colores.GRIS_TEXTO);
    btnCompletar.repaint();
  }

  private JButton botonDenominacion(String texto, int valor) {
    JButton b = Componentes.botonAccion(texto, Colores.VERDE, Colores.VERDE_HOVER);
    b.setFont(Fuentes.b(16));
    b.addActionListener(e -> {
      recibido += valor;
      actualizarUI();
    });
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
    p.add(Componentes.etiquetaCentrada(etiqueta, 12, false, Colores.GRIS_TEXTO));
    p.add(Componentes.etiquetaCentrada(valor, 22, true, colorVal));
    return p;
  }

  private JPanel cajaConLabel(String etiqueta, JLabel valorLabel, Color bg) {
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
    p.add(Componentes.etiquetaCentrada(etiqueta, 12, false, Colores.GRIS_TEXTO));
    p.add(valorLabel);
    return p;
  }

}
