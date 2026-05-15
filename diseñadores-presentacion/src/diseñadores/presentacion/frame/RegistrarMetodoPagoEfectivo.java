package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Componentes;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RegistrarMetodoPagoEfectivo extends JFrame {

  private final VentasControl control;
  private final UsuarioDTO usuarioActivo;

  private BigDecimal recibido = BigDecimal.ZERO;
  private JLabel lblRecibido, lblCambio;
  private JButton btnCompletar;

  public RegistrarMetodoPagoEfectivo(
    SeleccionarMetodoPago pantallaPago,
    JFrame mainFrame,
    VentasControl control,
    Runnable onConfirmado,
    UsuarioDTO usuarioActivo) {

    super("Pago en Efectivo");
    this.control = control;
    this.usuarioActivo = usuarioActivo;

    configurarVentana(mainFrame);
    inicializarComponentes(pantallaPago, mainFrame, onConfirmado);
  }

  private void configurarVentana(JFrame mainFrame) {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());
  }

  private void inicializarComponentes(SeleccionarMetodoPago pantallaPago, JFrame mainFrame, Runnable onConfirmado) {
    JPanel root = Componentes.fondoAmarillo();
    root.add(Componentes.topBar(this, usuarioActivo, this.control), BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));
    cuerpo.add(crearPanelVolver(pantallaPago), BorderLayout.NORTH);
    cuerpo.add(Componentes.centrado(crearCard(mainFrame, onConfirmado), 240, 12), BorderLayout.CENTER);

    root.add(cuerpo, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel crearPanelVolver(SeleccionarMetodoPago pantallaPago) {
    return Componentes.panelVolver("Volver a métodos de pago", () -> {
      dispose();
      pantallaPago.setVisible(true);
    });
  }

  private JPanel crearCard(JFrame mainFrame, Runnable onConfirmado) {
    JPanel card = Componentes.tarjetaBlanca(20);
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(28, 32, 28, 32));

    card.add(crearTitulo());
    card.add(Box.createVerticalStrut(22));
    card.add(crearFilaInformacionPagos());
    card.add(Box.createVerticalStrut(22));
    card.add(crearSeccionDenominaciones());
    card.add(Box.createVerticalStrut(18));
    card.add(crearSeccionPersonalizada());
    card.add(Box.createVerticalStrut(20));
    card.add(crearFilaAcciones(mainFrame, onConfirmado));

    return card;
  }

  private JLabel crearTitulo() {
    JLabel titulo = Componentes.etiqueta("Pago en Efectivo", 24, true, Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    return titulo;
  }

  private JPanel crearFilaInformacionPagos() {
    JPanel fila = new JPanel(new GridLayout(1, 3, 12, 0));
    fila.setOpaque(false);
    fila.setAlignmentX(LEFT_ALIGNMENT);
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

    fila.add(crearCajaTotalAPagar());
    fila.add(crearCajaRecibido());
    fila.add(crearCajaCambio());

    return fila;
  }

  private JPanel crearCajaTotalAPagar() {
    return cajaInfo("Total a pagar",
      String.format("$%.2f", control.getVentaActual().getTotal()),
      Colores.TEXTO_OSCURO, Colores.BLANCO);
  }

  private JPanel crearCajaRecibido() {
    lblRecibido = Componentes.etiquetaCentrada(String.format("$%.2f", recibido), 22, true, Colores.AZUL);
    return cajaConLabel("Recibido", lblRecibido, Colores.AZUL_CLARO);
  }

  private JPanel crearCajaCambio() {
    lblCambio = Componentes.etiquetaCentrada("$0.00", 22, true, Colores.GRIS_TEXTO);
    return cajaConLabel("Cambio", lblCambio, Colores.BLANCO);
  }

  private JPanel crearSeccionDenominaciones() {
    JPanel contenedor = new JPanel();
    contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
    contenedor.setOpaque(false);
    contenedor.setAlignmentX(LEFT_ALIGNMENT);

    JLabel lbl = Componentes.etiqueta("Denominaciones rápidas", 14, true, Colores.TEXTO_OSCURO);
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    contenedor.add(lbl);
    contenedor.add(Box.createVerticalStrut(10));
    contenedor.add(crearGridDenominaciones());

    return contenedor;
  }

  private JPanel crearGridDenominaciones() {
    JPanel grid = new JPanel(new GridLayout(2, 3, 10, 10));
    grid.setOpaque(false);
    grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
    for (int valor : new int[]{20, 50, 100, 200, 500, 1000}) {
      grid.add(crearBotonDenominacion("$" + valor, valor));
    }
    return grid;
  }

  private JButton crearBotonDenominacion(String texto, int valor) {
    JButton b = Componentes.botonAccion(texto, Colores.VERDE, Colores.VERDE_HOVER);
    b.setFont(Fuentes.b(16));
    b.addActionListener(e -> onDenominacionSeleccionada(valor));
    return b;
  }

  private void onDenominacionSeleccionada(int valor) {
    recibido = recibido.add(BigDecimal.valueOf(valor)).setScale(2, RoundingMode.HALF_UP);
    actualizarUI();
  }

  private JPanel crearSeccionPersonalizada() {
    JPanel contenedor = new JPanel();
    contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
    contenedor.setOpaque(false);
    contenedor.setAlignmentX(LEFT_ALIGNMENT);

    JLabel lbl = Componentes.etiqueta("Cantidad personalizada", 14, true, Colores.TEXTO_OSCURO);
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    contenedor.add(lbl);
    contenedor.add(Box.createVerticalStrut(8));
    contenedor.add(crearFilaCantidadPersonalizada());

    return contenedor;
  }

  private JPanel crearFilaCantidadPersonalizada() {
    JTextField campo = Componentes.campoPill("Ingrese cantidad");
    JButton btnAgregar = Componentes.botonAccion("Agregar", Colores.VERDE, Colores.VERDE_HOVER);
    btnAgregar.setPreferredSize(new Dimension(110, 44));
    btnAgregar.addActionListener(e -> onAgregarCantidadPersonalizada(campo));
    campo.addActionListener(e -> onAgregarCantidadPersonalizada(campo));

    JPanel row = new JPanel(new BorderLayout(10, 0));
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    row.add(campo, BorderLayout.CENTER);
    row.add(btnAgregar, BorderLayout.EAST);
    return row;
  }

  private void onAgregarCantidadPersonalizada(JTextField campo) {
    try {
      BigDecimal val = new BigDecimal(campo.getText().trim().replace(",", "."));
      if (val.compareTo(BigDecimal.ZERO) > 0) {
        recibido = recibido.add(val).setScale(2, RoundingMode.HALF_UP);
        actualizarUI();
        campo.setText("");
      }
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Error", JOptionPane.WARNING_MESSAGE);
    }
  }

  private JPanel crearFilaAcciones(JFrame mainFrame, Runnable onConfirmado) {
    JButton btnLimpiar = Componentes.botonAccion("Limpiar", Colores.GRIS_BTN, Colores.GRIS_BTN_HOVER);
    btnLimpiar.addActionListener(e -> onLimpiar());

    btnCompletar = crearBotonCompletar(mainFrame, onConfirmado);

    JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
    row.add(btnLimpiar);
    row.add(btnCompletar);
    return row;
  }

  private JButton crearBotonCompletar(JFrame mainFrame, Runnable onConfirmado) {
    JButton btn = new JButton("Completar Pago") {
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
            onConfirmarPago(mainFrame, onConfirmado);
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean habilitado = recibido.compareTo(control.getVentaActual().getTotal()) >= 0;
        g.setColor(habilitado ? (ov ? Colores.VERDE_HOVER : Colores.VERDE) : Colores.GRIS_DISABLED);
        setCursor(Cursor.getPredefinedCursor(habilitado ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    btn.setForeground(Colores.BLANCO);
    btn.setFont(Fuentes.b(15));
    return btn;
  }

  private void onLimpiar() {
    recibido = BigDecimal.ZERO;
    actualizarUI();
  }

  private void onConfirmarPago(JFrame mainFrame, Runnable onConfirmado) {
    if (recibido.compareTo(control.getVentaActual().getTotal()) < 0) {
      return;
    }

    ResultadoPagoDTO resultado = control.procesarPagoEfectivo(new PagoEfectivoDTO(recibido));

    if (!resultado.isAprobado()) {
      JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Pago rechazado", JOptionPane.WARNING_MESSAGE);
      return;
    }

    finalizarVenta(mainFrame, onConfirmado);
  }

  private void finalizarVenta(JFrame mainFrame, Runnable onConfirmado) {
    control.finalizarVenta(TipoPago.EFECTIVO);
    TicketDTO ticketDTO = control.generarTicket(recibido);
    setVisible(false);
    new PantallaTicket(mainFrame, ticketDTO, onConfirmado, control);
  }

  private void actualizarUI() {
    lblRecibido.setText(String.format("$%.2f", recibido));
    BigDecimal cambio = control.calcularCambio(recibido);
    lblCambio.setText(String.format("$%.2f", cambio.max(BigDecimal.ZERO)));
    lblCambio.setForeground(
      recibido.compareTo(control.getVentaActual().getTotal()) >= 0
      ? Colores.VERDE : Colores.GRIS_TEXTO);
    btnCompletar.repaint();
  }

  private JPanel cajaInfo(String etiqueta, String valor, Color colorVal, Color bg) {
    JPanel p = crearCajaBase(bg);
    p.add(Componentes.etiquetaCentrada(etiqueta, 12, false, Colores.GRIS_TEXTO));
    p.add(Componentes.etiquetaCentrada(valor, 22, true, colorVal));
    return p;
  }

  private JPanel cajaConLabel(String etiqueta, JLabel valorLabel, Color bg) {
    JPanel p = crearCajaBase(bg);
    p.add(Componentes.etiquetaCentrada(etiqueta, 12, false, Colores.GRIS_TEXTO));
    p.add(valorLabel);
    return p;
  }

  private JPanel crearCajaBase(Color bg) {
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
    return p;
  }

}
