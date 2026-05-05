package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;

public class PantallaTicket extends JFrame {

  private final IUsuarios usuariosFachada;
  private final IVentas ventasFachada;
  private final IInventario inventarioFachada;
  private final IProveedores proveedoresFachada;

  public PantallaTicket(
    JFrame frame,
    TicketDTO ticket,
    Runnable onConfirmado,
    IUsuarios usuariosFachada,
    IVentas ventasFachada,
    IInventario inventarioFachada,
    IProveedores proveedoresFachada) {

    super("Ticket de Venta");
    this.usuariosFachada = usuariosFachada;
    this.ventasFachada = ventasFachada;
    this.inventarioFachada = inventarioFachada;
    this.proveedoresFachada = proveedoresFachada;

    configurarVentana(frame);

    JPanel root = fondoAmarillo();
    root.add(topBar(), BorderLayout.NORTH);

    JPanel ticketPanel = buildTicket(ticket);
    JScrollPane scroll = configurarScroll(ticketPanel);

    JPanel centrado = new JPanel(new GridBagLayout());
    centrado.setOpaque(false);
    centrado.setBorder(new EmptyBorder(20, 0, 6, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 340, 0, 340);
    centrado.add(scroll, gbc);

    root.add(centrado, BorderLayout.CENTER);
    root.add(crearBarraInferior(frame, onConfirmado), BorderLayout.SOUTH);

    setContentPane(root);
    setVisible(true);
  }

  private void configurarVentana(JFrame mainFrame) {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());
  }

  private JScrollPane configurarScroll(JPanel contenido) {
    JScrollPane scroll = new JScrollPane(contenido);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    return scroll;
  }

  private JPanel crearBarraInferior(JFrame mainFrame, Runnable onConfirmado) {
    JPanel barraInf = new JPanel(new GridLayout(1, 2, 12, 0));
    barraInf.setOpaque(false);
    barraInf.setBorder(new EmptyBorder(8, 340, 16, 340));
    barraInf.setPreferredSize(new Dimension(0, 66));

    JButton btnDescargar = accionBtn("Descargar Ticket", Colores.AZUL, Colores.AZUL_HOVER);
    btnDescargar.addActionListener(e -> JOptionPane.showMessageDialog(this,
      "Función de descarga pendiente de implementar.", "Descargar",
      JOptionPane.INFORMATION_MESSAGE));

    JButton btnFinalizar = accionBtn("Finalizar Venta", Colores.VERDE, Colores.VERDE_HOVER);
    btnFinalizar.addActionListener(e -> {
      onConfirmado.run();
      dispose();
      mainFrame.setVisible(true);
    });

    barraInf.add(btnDescargar);
    barraInf.add(btnFinalizar);
    return barraInf;
  }

  private JPanel buildTicket(TicketDTO t) {
    JPanel p = new JPanel(new BorderLayout()) {
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
    p.add(crearCabeceraTicket(), BorderLayout.NORTH);
    p.add(crearCuerpoTicket(t), BorderLayout.CENTER);
    return p;
  }

  private JPanel crearCabeceraTicket() {
    JPanel cab = new JPanel(new GridLayout(2, 1, 0, 6)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.AZUL);
        g.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 20, 20);
        super.paintComponent(g2d);
      }

    };
    cab.setOpaque(false);
    cab.setBorder(new EmptyBorder(20, 20, 20, 20));
    cab.setPreferredSize(new Dimension(0, 112));

    JLabel lblTit = new JLabel("TICKET DE VENTA", SwingConstants.CENTER);
    lblTit.setFont(Fuentes.b(20));
    lblTit.setForeground(Colores.BLANCO);

    JLabel lblSub = new JLabel("Sistema de Punto de Venta", SwingConstants.CENTER);
    lblSub.setFont(Fuentes.r(12));
    lblSub.setForeground(new Color(180, 210, 255));

    cab.add(lblTit);
    cab.add(lblSub);
    return cab;
  }

  private JPanel crearCuerpoTicket(TicketDTO t) {
    JPanel body = new JPanel(new GridBagLayout());
    body.setOpaque(false);
    body.setBorder(new EmptyBorder(20, 28, 24, 28));

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    int row = 0;

    c.gridy = row++;
    body.add(fullLabel(t.getNombreTienda(), 17, true, Colores.TEXTO_OSCURO, SwingConstants.CENTER), c);
    c.gridy = row++;
    body.add(fullLabel("RFC: " + t.getRfc(), 11, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    body.add(fullLabel(t.getDireccion(), 11, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    body.add(fullLabel(t.getTelefono(), 11, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);

    c.gridy = row++;
    c.insets = new Insets(14, 0, 12, 0);
    body.add(sepLine(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 6, 0);
    body.add(crearFilaMeta(t), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 12, 0);
    JLabel lblCajero = new JLabel("Cajero: " + t.getCajero());
    lblCajero.setFont(Fuentes.r(12));
    lblCajero.setForeground(Colores.TEXTO_OSCURO);
    body.add(lblCajero, c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 14, 0);
    body.add(crearCajaFolio(t.getFolio()), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 0, 0);
    body.add(sepLine(), c);

    c.gridy = row++;
    c.insets = new Insets(10, 0, 10, 0);
    body.add(fullLabel("Productos", 15, true, Colores.TEXTO_OSCURO, SwingConstants.LEFT), c);

    for (ItemVentaDTO item : t.getItems()) {
      c.gridy = row++;
      c.insets = new Insets(0, 0, 8, 0);
      body.add(filaProducto(item), c);
    }

    c.gridy = row++;
    c.insets = new Insets(4, 0, 12, 0);
    body.add(sepLine(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 6, 0);
    body.add(filaResumen("Subtotal", t.getSubtotal(), false), c);

    c.gridy = row++;
    body.add(filaResumen("IVA (16%)", t.getIva(), false), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 8, 0);
    body.add(filaResumen("TOTAL", t.getTotal(), true), c);

    if (t.getEfectivoRecibido().compareTo(BigDecimal.ZERO) > 0) {
      c.gridy = row++;
      c.insets = new Insets(0, 0, 4, 0);
      body.add(filaResumen("Efectivo recibido", t.getEfectivoRecibido(), false), c);
      c.gridy = row++;
      c.insets = new Insets(0, 0, 8, 0);
      body.add(filaResumen("Cambio", t.getCambio(), false), c);
    }

    c.gridy = row++;
    c.insets = new Insets(4, 0, 12, 0);
    body.add(filaTipoPago(t.getTipoPago()), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 16, 0);
    body.add(sepLine(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 3, 0);
    body.add(fullLabel("¡Gracias por su compra!", 12, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    body.add(fullLabel("Conserve este ticket para cualquier aclaración", 11, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    body.add(fullLabel("*** TICKET VÁLIDO ***", 10, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);

    return body;
  }

  private JPanel filaTipoPago(TipoPago tipo) {
    JPanel row = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(colorFondoTipoPago(tipo));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(new EmptyBorder(10, 14, 10, 14));

    JLabel lEtq = new JLabel("Método de pago");
    lEtq.setFont(Fuentes.r(12));
    lEtq.setForeground(Colores.GRIS_TEXTO);

    JLabel lVal = new JLabel(nombreTipoPago(tipo));
    lVal.setFont(Fuentes.b(13));
    lVal.setForeground(colorTextoTipoPago(tipo));

    row.add(lEtq, BorderLayout.WEST);
    row.add(lVal, BorderLayout.EAST);
    return row;
  }

  private String nombreTipoPago(TipoPago tipo) {
    if (tipo == null) {
      return "—";
    }
    return switch (tipo) {
      case EFECTIVO ->
        "Efectivo";
      case TARJETA ->
        "Tarjeta de crédito / débito";
      case TRANSACCION ->
        "Transferencia bancaria";
      case QR ->
        "CoDi / QR";
      default ->
        tipo.name();
    };
  }

  private Color colorTextoTipoPago(TipoPago tipo) {
    if (tipo == null) {
      return Colores.GRIS_TEXTO;
    }
    switch (tipo) {
      case EFECTIVO:
        return Colores.VERDE;
      case TARJETA:
        return Colores.AZUL;
      case TRANSACCION:
        return Colores.NARANJA;
      case QR:
        return Colores.MORADO;
      default:
        return Colores.TEXTO_OSCURO;
    }
  }

  private Color colorFondoTipoPago(TipoPago tipo) {
    if (tipo == null) {
      return Colores.FONDO_ITEM;
    }
    return switch (tipo) {
      case EFECTIVO ->
        new Color(240, 253, 244);
      case TARJETA ->
        Colores.AZUL_CLARO;
      case TRANSACCION ->
        Colores.NARANJA_BG;
      case QR ->
        new Color(245, 240, 255);
      default ->
        Colores.FONDO_ITEM;
    };
  }

  private JPanel crearFilaMeta(TicketDTO t) {
    JPanel metaRow = new JPanel(new BorderLayout());
    metaRow.setOpaque(false);
    JLabel lFecha = new JLabel("Fecha: " + t.getFechaFormateada());
    lFecha.setFont(Fuentes.r(12));
    lFecha.setForeground(Colores.TEXTO_OSCURO);
    JLabel lHora = new JLabel("Hora: " + t.getHoraFormateada());
    lHora.setFont(Fuentes.r(12));
    lHora.setForeground(Colores.TEXTO_OSCURO);
    metaRow.add(lFecha, BorderLayout.WEST);
    metaRow.add(lHora, BorderLayout.EAST);
    return metaRow;
  }

  private JPanel crearCajaFolio(String folio) {
    JPanel folioBox = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_FOLIO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    folioBox.setOpaque(false);
    folioBox.setBorder(new EmptyBorder(10, 14, 10, 14));
    folioBox.add(fullLabel("Folio de venta", 11, false, Colores.GRIS_TEXTO, SwingConstants.LEFT));
    folioBox.add(fullLabel(folio, 15, true, Colores.TEXTO_OSCURO, SwingConstants.LEFT));
    return folioBox;
  }

  private JPanel filaProducto(ItemVentaDTO item) {
    JPanel row = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_ITEM);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(new EmptyBorder(10, 12, 10, 12));

    GridBagConstraints c = new GridBagConstraints();
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;

    c.gridx = 0;
    c.weightx = 1;
    JLabel lNom = new JLabel(item.getNombre());
    lNom.setFont(Fuentes.b(13));
    lNom.setForeground(Colores.TEXTO_OSCURO);
    row.add(lNom, c);

    c.gridx = 1;
    c.weightx = 0;
    JLabel lSub = new JLabel(String.format("$%.2f", item.getSubtotal().doubleValue()));
    lSub.setFont(Fuentes.b(13));
    lSub.setForeground(Colores.TEXTO_OSCURO);
    row.add(lSub, c);

    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    c.insets = new Insets(3, 0, 0, 0);
    JLabel lCod = new JLabel(item.getCodigo());
    lCod.setFont(Fuentes.r(10));
    lCod.setForeground(Colores.GRIS_TEXTO);
    row.add(lCod, c);

    c.gridy = 2;
    c.insets = new Insets(2, 0, 0, 0);
    JLabel lCant = new JLabel(item.getCantidad() + " x $" + String.format("%.2f", item.getPrecioUnitario().doubleValue()));
    lCant.setFont(Fuentes.r(11));
    lCant.setForeground(Colores.GRIS_TEXTO);
    row.add(lCant, c);

    return row;
  }

  private JPanel filaResumen(String etiqueta, BigDecimal valor, boolean esTotal) {
    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    JLabel lE = new JLabel(etiqueta);
    lE.setFont(esTotal ? Fuentes.b(15) : Fuentes.r(13));
    lE.setForeground(esTotal ? Colores.TEXTO_OSCURO : Colores.GRIS_TEXTO);
    JLabel lV = new JLabel(String.format("$%.2f", valor.doubleValue()));
    lV.setFont(esTotal ? Fuentes.b(15) : Fuentes.r(13));
    lV.setForeground(esTotal ? Colores.AZUL : Colores.GRIS_TEXTO);
    row.add(lE, BorderLayout.WEST);
    row.add(lV, BorderLayout.EAST);
    return row;
  }

  private JPanel fondoAmarillo() {
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

  private JPanel topBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));
    JButton btnCS = topBarBtn("Menu Principal");
    btnCS.addActionListener(e -> {
      dispose();
      new MenuPrincipal(null, usuariosFachada, ventasFachada,
        inventarioFachada, proveedoresFachada).setVisible(true);
    });
    bar.add(btnCS);
    return bar;
  }

  private JLabel fullLabel(String txt, int size, boolean bold, Color color, int halign) {
    JLabel l = new JLabel(txt, halign);
    l.setFont(bold ? Fuentes.b(size) : Fuentes.r(size));
    l.setForeground(color);
    return l;
  }

  private JPanel sepLine() {
    JPanel s = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        super.paintComponent(g2d);
        Graphics2D g = (Graphics2D) g2d;
        g.setColor(Colores.BORDE_GRIS);
        float[] dash = {4f, 4f};
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash, 0));
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
      }

    };
    s.setOpaque(false);
    s.setPreferredSize(new Dimension(0, 2));
    return s;
  }

  private JButton accionBtn(String texto, Color base, Color hover) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
    b.setFont(Fuentes.b(15));
    b.setHorizontalAlignment(SwingConstants.CENTER);
    return b;
  }

  private JButton topBarBtn(String texto) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(13));
    b.setPreferredSize(new Dimension(160, 38));
    return b;
  }

}
