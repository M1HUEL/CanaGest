package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.TipoPago;
import diseñadores.negocios.dto.VentaDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Botones;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HistorialVentas extends JFrame {

  private final JFrame frame;
  private final VentasControl control;
  private final List<VentaDTO> ventas = new ArrayList<>();
  private JPanel panelTabla;
  private JTextField campoBusqueda;

  public HistorialVentas(JFrame frame, VentasControl control) {
    this.frame = frame;
    this.control = control;

    setTitle("La Canasta - Historial de Ventas");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    ventas.addAll(control.obtenerHistorialVentas());
    inicializarComponentes();
  }

  private void inicializarComponentes() {
    JPanel root = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);
    root.add(buildTopBar(), BorderLayout.NORTH);
    root.add(buildContenido(), BorderLayout.CENTER);
    setContentPane(root);
  }

  private JPanel buildTopBar() {
    JPanel bar = new JPanel(new BorderLayout());
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS),
      new EmptyBorder(0, 24, 0, 24)));
    bar.setPreferredSize(new Dimension(0, 66));

    JButton btnMenu = Botones.amarillo("Menú Principal");
    btnMenu.addActionListener(e -> {
      dispose();
      frame.setVisible(true);
    });

    JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
    der.setOpaque(false);
    der.add(btnMenu);
    bar.add(der, BorderLayout.EAST);
    return bar;
  }

  private JPanel buildContenido() {
    JPanel contenido = new JPanel(new BorderLayout());
    contenido.setOpaque(false);
    contenido.setBorder(new EmptyBorder(28, 32, 28, 32));

    // — Fila superior: título + resumen + buscador —
    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setOpaque(false);
    topRow.setBorder(new EmptyBorder(0, 0, 20, 0));
    topRow.add(crearTitulo(), BorderLayout.WEST);
    topRow.add(crearBarraBusqueda(), BorderLayout.EAST);

    contenido.add(topRow, BorderLayout.NORTH);
    contenido.add(crearContenedorTabla(), BorderLayout.CENTER);
    return contenido;
  }

  private JPanel crearTitulo() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    JLabel lblTitulo = new JLabel("Historial de Ventas");
    lblTitulo.setFont(Fuentes.b(26));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblDesc = new JLabel("Registro completo de transacciones · " + ventas.size() + " venta(s)");
    lblDesc.setFont(Fuentes.r(14));
    lblDesc.setForeground(Colores.GRIS_TEXTO);

    panel.add(lblTitulo);
    panel.add(Box.createVerticalStrut(4));
    panel.add(lblDesc);
    return panel;
  }

  private JPanel crearBarraBusqueda() {
    campoBusqueda = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 249, 252));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    campoBusqueda.setPreferredSize(new Dimension(260, 42));
    campoBusqueda.setOpaque(false);
    campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(new Color(213, 218, 230), 1, 8),
      new EmptyBorder(8, 14, 8, 14)));
    campoBusqueda.setFont(Fuentes.r(14));
    campoBusqueda.setForeground(Colores.GRIS_TEXTO);
    campoBusqueda.setText("Buscar por folio...");

    campoBusqueda.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (campoBusqueda.getText().startsWith("Buscar")) {
          campoBusqueda.setText("");
          campoBusqueda.setForeground(Colores.TEXTO_OSCURO);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (campoBusqueda.getText().isEmpty()) {
          campoBusqueda.setText("Buscar por folio...");
          campoBusqueda.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });

    campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        filtrar();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        filtrar();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
      }

    });

    JPanel wrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    wrap.setOpaque(false);
    wrap.add(campoBusqueda);
    return wrap;
  }

  private JPanel crearContenedorTabla() {
    JPanel wrap = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, 14, 14));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 14, 14));
        super.paintComponent(g2d);
      }

    };
    wrap.setOpaque(false);
    wrap.add(crearEncabezadoTabla(), BorderLayout.NORTH);

    panelTabla = new JPanel();
    panelTabla.setLayout(new BoxLayout(panelTabla, BoxLayout.Y_AXIS));
    panelTabla.setOpaque(false);
    construirTabla(ventas);

    JScrollPane scroll = new JScrollPane(panelTabla);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    wrap.add(scroll, BorderLayout.CENTER);
    return wrap;
  }

  private JPanel crearEncabezadoTabla() {
    JPanel headerTabla = new JPanel(new GridLayout(1, 7));
    headerTabla.setOpaque(false);
    headerTabla.setBorder(new EmptyBorder(16, 24, 16, 24));

    String[] cols = {"Folio", "Fecha", "Productos", "Subtotal", "IVA", "Total", "Tipo Pago"};
    for (String col : cols) {
      JLabel lbl = new JLabel(col);
      lbl.setFont(Fuentes.b(13));
      lbl.setForeground(Colores.TEXTO_OSCURO);
      headerTabla.add(lbl);
    }

    JLabel lblAcc = new JLabel("Acciones");
    lblAcc.setFont(Fuentes.b(13));
    lblAcc.setForeground(Colores.TEXTO_OSCURO);

    JPanel headerConAccion = new JPanel(new GridLayout(1, 8));
    headerConAccion.setOpaque(false);
    headerConAccion.setBorder(new EmptyBorder(16, 24, 16, 24));
    for (String col : cols) {
      JLabel lbl = new JLabel(col);
      lbl.setFont(Fuentes.b(13));
      lbl.setForeground(Colores.TEXTO_OSCURO);
      headerConAccion.add(lbl);
    }
    headerConAccion.add(lblAcc);

    JPanel sep = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.BORDE_GRIS);
        g.drawLine(0, 0, getWidth(), 0);
      }

    };
    sep.setOpaque(false);
    sep.setPreferredSize(new Dimension(0, 1));

    JPanel headerWrap = new JPanel(new BorderLayout());
    headerWrap.setOpaque(false);
    headerWrap.add(headerConAccion, BorderLayout.CENTER);
    headerWrap.add(sep, BorderLayout.SOUTH);
    return headerWrap;
  }

  private void filtrar() {
    String q = campoBusqueda.getText();
    if (q.startsWith("Buscar") || q.isEmpty()) {
      construirTabla(ventas);
      return;
    }
    String ql = q.toLowerCase();
    List<VentaDTO> filtradas = new ArrayList<>();
    for (VentaDTO v : ventas) {
      if (v.getFolio() != null && v.getFolio().toLowerCase().contains(ql)) {
        filtradas.add(v);
      }
    }
    construirTabla(filtradas);
  }

  private void construirTabla(List<VentaDTO> lista) {
    panelTabla.removeAll();

    if (lista.isEmpty()) {
      panelTabla.add(crearEstadoVacio());
    } else {
      for (VentaDTO v : lista) {
        panelTabla.add(filaVenta(v));
        panelTabla.add(crearSeparador());
      }
    }

    panelTabla.revalidate();
    panelTabla.repaint();
  }

  private JPanel crearEstadoVacio() {
    JPanel vacio = new JPanel(new GridBagLayout());
    vacio.setOpaque(false);
    vacio.setPreferredSize(new Dimension(0, 200));

    JPanel col = new JPanel();
    col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
    col.setOpaque(false);

    JLabel lbl1 = new JLabel("No hay ventas registradas");
    lbl1.setFont(Fuentes.b(16));
    lbl1.setForeground(Colores.GRIS_TEXTO);
    lbl1.setAlignmentX(CENTER_ALIGNMENT);

    JLabel lbl2 = new JLabel("Las ventas finalizadas aparecerán aquí");
    lbl2.setFont(Fuentes.r(13));
    lbl2.setForeground(new Color(180, 183, 189));
    lbl2.setAlignmentX(CENTER_ALIGNMENT);

    col.add(lbl1);
    col.add(Box.createVerticalStrut(6));
    col.add(lbl2);
    vacio.add(col);
    return vacio;
  }

  private JPanel filaVenta(VentaDTO v) {
    JPanel fila = new JPanel(new GridLayout(1, 8));
    fila.setOpaque(false);
    fila.setBorder(new EmptyBorder(14, 24, 14, 24));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

    fila.add(labelTabla(v.getFolio() != null ? v.getFolio() : "—", true));

    fila.add(labelTabla(v.getFecha() != null ? v.getFecha() : "—", false));

    int numItems = v.getItems() != null ? v.getItems().size() : 0;
    int unidades = v.getTotalUnidades();
    fila.add(labelTabla(numItems + " artículo(s)  ·  " + unidades + " ud.", false));

    fila.add(labelTabla("$" + fmt(v.getSubtotal()), false));

    // IVA
    fila.add(labelTabla("$" + fmt(v.getIva()), false));

    // Total — resaltado
    JLabel lblTotal = new JLabel("$" + fmt(v.getTotal()));
    lblTotal.setFont(Fuentes.b(14));
    lblTotal.setForeground(Colores.TEXTO_OSCURO);
    fila.add(lblTotal);

    fila.add(crearBadgePago(v.getTipoPago()));

    JPanel wrapBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapBtn.setOpaque(false);
    JButton btnDetalle = Botones.azulChico("Ver detalle");
    btnDetalle.addActionListener(e -> new DetalleVenta(HistorialVentas.this, v).setVisible(true));
    wrapBtn.add(btnDetalle);
    fila.add(wrapBtn);

    return fila;
  }

  private JLabel labelTabla(String texto, boolean negrita) {
    JLabel lbl = new JLabel(texto);
    lbl.setFont(negrita ? Fuentes.b(13) : Fuentes.r(13));
    lbl.setForeground(negrita ? Colores.TEXTO_OSCURO : Colores.GRIS_TEXTO);
    return lbl;
  }

  private JPanel crearBadgePago(TipoPago tipo) {
    String texto;
    Color color, bg;

    if (tipo == null) {
      texto = "—";
      color = Colores.GRIS_TEXTO;
      bg = new Color(245, 246, 248);
    } else {
      texto = switch (tipo) {
        case EFECTIVO ->
          "Efectivo";
        case TARJETA ->
          "Tarjeta";
        case TRANSACCION ->
          "Transferencia";
        case QR ->
          "CoDi / QR";
      };
      color = switch (tipo) {
        case EFECTIVO ->
          new Color(21, 128, 61);
        case TARJETA ->
          new Color(29, 78, 216);
        case TRANSACCION ->
          new Color(161, 110, 0);
        case QR ->
          new Color(109, 40, 217);
      };
      bg = switch (tipo) {
        case EFECTIVO ->
          new Color(220, 252, 231);
        case TARJETA ->
          new Color(219, 234, 254);
        case TRANSACCION ->
          new Color(254, 243, 199);
        case QR ->
          new Color(237, 233, 254);
      };
    }

    JLabel badge = new JLabel(texto, SwingConstants.CENTER);
    badge.setFont(Fuentes.b(11));
    badge.setForeground(color);
    badge.setOpaque(true);
    badge.setBackground(bg);
    badge.setBorder(new EmptyBorder(3, 10, 3, 10));

    JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrap.setOpaque(false);
    wrap.add(badge);
    return wrap;
  }

  private JPanel crearSeparador() {
    JPanel sep = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.BORDE_GRIS);
        g.drawLine(0, 0, getWidth(), 0);
      }

    };
    sep.setOpaque(false);
    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    sep.setPreferredSize(new Dimension(0, 1));
    return sep;
  }

  private String fmt(BigDecimal val) {
    return val != null ? String.format("%.2f", val) : "0.00";
  }

}
