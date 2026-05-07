package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.ventas.IVentas;
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

  private final VentaDTO ventaActual;
  private final UsuarioDTO usuarioActivo;
  private final IUsuarios usuariosFachada;
  private final IVentas ventasFachada;
  private final IInventario inventarioFachada;
  private final IProveedores proveedoresFachada;
  private final BigDecimal totalAPagar;

  private BigDecimal recibido = BigDecimal.ZERO;
  private JLabel lblRecibido, lblCambio;
  private JButton btnCompletar;

  public RegistrarMetodoPagoEfectivo(
    SeleccionarMetodoPago seleccionarMetodoPago,
    JFrame frame,
    IVentas ventasFachada,
    IInventario inventarioFachada,
    IUsuarios usuariosFachada,
    IProveedores proveedoresFachada,
    VentaDTO ventaActual,
    BigDecimal total,
    Runnable onConfirmado,
    UsuarioDTO usuarioActivo) {
    super("Pago en Efectivo");

    this.usuarioActivo = usuarioActivo;
    this.ventaActual = ventaActual;
    this.usuariosFachada = usuariosFachada;
    this.ventasFachada = ventasFachada;
    this.inventarioFachada = inventarioFachada;
    this.proveedoresFachada = proveedoresFachada;
    this.totalAPagar = total;

    configurarVentana(frame);
    inicializarComponentes(seleccionarMetodoPago, frame, onConfirmado);
  }

  private void configurarVentana(JFrame mainFrame) {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());
  }

  private void inicializarComponentes(SeleccionarMetodoPago pantallaPago, JFrame mainFrame, Runnable onConfirmado) {
    JPanel root = Componentes.fondoAmarillo();
    root.add(Componentes.topBar(this, usuarioActivo, usuariosFachada, ventasFachada,
      inventarioFachada, proveedoresFachada), BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));

    cuerpo.add(crearPanelVolver(pantallaPago), BorderLayout.NORTH);
    cuerpo.add(Componentes.centrado(buildCard(mainFrame, onConfirmado), 240, 12), BorderLayout.CENTER);

    root.add(cuerpo, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel crearPanelVolver(SeleccionarMetodoPago pantallaPago) {
    return Componentes.panelVolver("Volver a metodos de pago", () -> seleccionarVolver(pantallaPago));
  }

  private void seleccionarVolver(SeleccionarMetodoPago pantallaPago) {
    dispose();
    pantallaPago.setVisible(true);
  }

  private JPanel buildCard(JFrame mainFrame, Runnable onConfirmado) {
    JPanel card = Componentes.tarjetaBlanca(20);
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(28, 32, 28, 32));

    card.add(crearEtiquetaTitulo());
    card.add(Box.createVerticalStrut(22));
    card.add(crearFilaInformacionPagos());
    card.add(Box.createVerticalStrut(22));
    card.add(crearSeccionDenominaciones());
    card.add(Box.createVerticalStrut(18));
    card.add(crearSeccionPersonalizada());
    card.add(Box.createVerticalStrut(20));
    card.add(filaAcciones(mainFrame, onConfirmado));

    return card;
  }

  private JLabel crearEtiquetaTitulo() {
    JLabel titulo = Componentes.etiqueta("Pago en Efectivo", 24, true, Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    return titulo;
  }

  private JPanel crearFilaInformacionPagos() {
    JPanel cajasRow = new JPanel(new GridLayout(1, 3, 12, 0));
    cajasRow.setOpaque(false);
    cajasRow.setAlignmentX(LEFT_ALIGNMENT);
    cajasRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

    cajasRow.add(cajaInfo("Total a pagar", String.format("$%.2f", totalAPagar), Colores.TEXTO_OSCURO, Colores.BLANCO));

    lblRecibido = Componentes.etiquetaCentrada(String.format("$%.2f", recibido), 22, true, Colores.AZUL);
    cajasRow.add(cajaConLabel("Recibido", lblRecibido, Colores.AZUL_CLARO));

    lblCambio = Componentes.etiquetaCentrada("$0.00", 22, true, Colores.GRIS_TEXTO);
    cajasRow.add(cajaConLabel("Cambio", lblCambio, Colores.BLANCO));

    return cajasRow;
  }

  private JPanel crearSeccionDenominaciones() {
    JPanel contenedor = new JPanel();
    contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
    contenedor.setOpaque(false);
    contenedor.setAlignmentX(LEFT_ALIGNMENT);

    JLabel lblDenom = Componentes.etiqueta("Denominaciones rapidas", 14, true, Colores.TEXTO_OSCURO);
    lblDenom.setAlignmentX(LEFT_ALIGNMENT);
    contenedor.add(lblDenom);
    contenedor.add(Box.createVerticalStrut(10));

    JPanel gridDenom = new JPanel(new GridLayout(2, 3, 10, 10));
    gridDenom.setOpaque(false);
    gridDenom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

    for (int v : new int[]{20, 50, 100, 200, 500, 1000}) {
      gridDenom.add(botonDenominacion("$" + v, v));
    }

    contenedor.add(gridDenom);
    return contenedor;
  }

  private JPanel crearSeccionPersonalizada() {
    JPanel contenedor = new JPanel();
    contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
    contenedor.setOpaque(false);
    contenedor.setAlignmentX(LEFT_ALIGNMENT);

    JLabel lblCustom = Componentes.etiqueta("Cantidad personalizada", 14, true, Colores.TEXTO_OSCURO);
    lblCustom.setAlignmentX(LEFT_ALIGNMENT);
    contenedor.add(lblCustom);
    contenedor.add(Box.createVerticalStrut(8));
    contenedor.add(filaCantidadPersonalizada());

    return contenedor;
  }

  private JPanel filaCantidadPersonalizada() {
    JTextField campoCustom = Componentes.campoPill("Ingrese cantidad");
    JButton btnAgregar = Componentes.botonAccion("Agregar", Colores.VERDE, Colores.VERDE_HOVER);
    btnAgregar.setPreferredSize(new Dimension(110, 44));

    btnAgregar.addActionListener(e -> seleccionarAgregarCantidad(campoCustom));
    campoCustom.addActionListener(e -> seleccionarAgregarCantidad(campoCustom));

    JPanel row = new JPanel(new BorderLayout(10, 0));
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    row.add(campoCustom, BorderLayout.CENTER);
    row.add(btnAgregar, BorderLayout.EAST);

    return row;
  }

  private void seleccionarAgregarCantidad(JTextField campo) {
    try {
      BigDecimal val = new BigDecimal(campo.getText().trim().replace(",", "."));
      if (val.compareTo(BigDecimal.ZERO) > 0) {
        recibido = recibido.add(val).setScale(2, RoundingMode.HALF_UP);
        actualizarUI();
        campo.setText("");
      }
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Ingrese un numero valido.", "Error", JOptionPane.WARNING_MESSAGE);
    }
  }

  private JPanel filaAcciones(JFrame mainFrame, Runnable onConfirmado) {
    JButton btnLimpiar = Componentes.botonAccion("Limpiar", Colores.GRIS_BTN, Colores.GRIS_BTN_HOVER);
    btnLimpiar.addActionListener(e -> seleccionarLimpiar());

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
          @Override
          public void mouseEntered(MouseEvent e) {
            ov = true;
            repaint();
          }

          @Override
          public void mouseExited(MouseEvent e) {
            ov = false;
            repaint();
          }

          @Override
          public void mouseClicked(MouseEvent e) {
            seleccionarConfirmarPago(mainFrame, onConfirmado);
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean hab = recibido.compareTo(totalAPagar) >= 0;
        g.setColor(hab ? (ov ? Colores.VERDE_HOVER : Colores.VERDE) : Colores.GRIS_DISABLED);
        setCursor(Cursor.getPredefinedCursor(hab ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    btn.setForeground(Colores.BLANCO);
    btn.setFont(Fuentes.b(15));
    return btn;
  }

  private void seleccionarLimpiar() {
    recibido = BigDecimal.ZERO;
    actualizarUI();
  }

  private void seleccionarConfirmarPago(JFrame mainFrame, Runnable onConfirmado) {
    if (recibido.compareTo(totalAPagar) < 0) {
      return;
    }

    PagoEfectivoDTO pagoDTO = new PagoEfectivoDTO(recibido);
    ResultadoPagoDTO resultado = ventasFachada.procesarPagoEfectivo(ventaActual, pagoDTO);

    if (!resultado.isAprobado()) {
      JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Pago rechazado", JOptionPane.WARNING_MESSAGE);
      return;
    }

    ejecutarFinalizacionVenta(mainFrame, onConfirmado);
  }

  private void ejecutarFinalizacionVenta(JFrame mainFrame, Runnable onConfirmado) {
    ventasFachada.procesarFinalizarVenta(ventaActual);
    TicketDTO ticketDTO = ventasFachada.generarTicket(ventaActual, recibido);

    this.setVisible(false);
    new PantallaTicket(mainFrame, ticketDTO, onConfirmado, usuariosFachada, ventasFachada,
      inventarioFachada, proveedoresFachada);
  }

  private void actualizarUI() {
    lblRecibido.setText(String.format("$%.2f", recibido));
    BigDecimal cambio = ventasFachada.procesarCalcularCambio(ventaActual, recibido);
    lblCambio.setText(String.format("$%.2f", cambio.max(BigDecimal.ZERO).doubleValue()));
    lblCambio.setForeground(recibido.compareTo(totalAPagar) >= 0 ? Colores.VERDE : Colores.GRIS_TEXTO);
    btnCompletar.repaint();
  }

  private JButton botonDenominacion(String texto, int valor) {
    JButton b = Componentes.botonAccion(texto, Colores.VERDE, Colores.VERDE_HOVER);
    b.setFont(Fuentes.b(16));
    b.addActionListener(e -> seleccionarDenominacion(valor));
    return b;
  }

  private void seleccionarDenominacion(int valor) {
    recibido = recibido.add(BigDecimal.valueOf(valor)).setScale(2, RoundingMode.HALF_UP);
    actualizarUI();
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
