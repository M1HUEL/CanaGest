package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ItemVentaDTO;
import diseñadores.negocios.dto.TipoPago;
import diseñadores.negocios.dto.VentaDTO;
import diseñadores.presentacion.utilidad.Botones;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;

public class DetalleVenta extends JDialog {

  public DetalleVenta(JFrame parent, VentaDTO venta) {
    super(parent, "Detalle de Venta", true);
    setSize(560, 640);
    setLocationRelativeTo(parent);
    setResizable(false);
    construirContenido(venta);
  }

  private void construirContenido(VentaDTO venta) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Colores.BLANCO);

    panel.add(crearCabecera(venta), BorderLayout.NORTH);
    panel.add(crearListaItems(venta), BorderLayout.CENTER);
    panel.add(crearPieTotales(venta), BorderLayout.SOUTH);

    setContentPane(panel);
  }

  // ── Cabecera: folio, fecha, tipo de pago ─────────────────────────────
  private JPanel crearCabecera(VentaDTO venta) {
    JPanel cab = new JPanel(new BorderLayout(0, 6));
    cab.setBackground(Colores.BLANCO);
    cab.setBorder(BorderFactory.createCompoundBorder(
      new MatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS),
      new EmptyBorder(24, 28, 18, 28)));

    JLabel lblTitulo = new JLabel("Detalle de Venta");
    lblTitulo.setFont(Fuentes.b(20));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);

    JPanel metadatos = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    metadatos.setOpaque(false);

    JLabel lblFolio = new JLabel(venta.getFolio() != null ? venta.getFolio() : "Sin folio");
    lblFolio.setFont(Fuentes.b(13));
    lblFolio.setForeground(new Color(29, 78, 216));

    JLabel lblSep = new JLabel("  ·  ");
    lblSep.setFont(Fuentes.r(13));
    lblSep.setForeground(Colores.GRIS_TEXTO);

    JLabel lblFecha = new JLabel(venta.getFecha() != null ? venta.getFecha() : "—");
    lblFecha.setFont(Fuentes.r(13));
    lblFecha.setForeground(Colores.GRIS_TEXTO);

    metadatos.add(lblFolio);
    metadatos.add(lblSep);
    metadatos.add(lblFecha);

    // badge tipo pago
    JLabel badge = badgePago(venta.getTipoPago());
    JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
    badgeWrap.setOpaque(false);
    badgeWrap.add(badge);

    JPanel info = new JPanel(new BorderLayout());
    info.setOpaque(false);
    info.add(metadatos, BorderLayout.WEST);
    info.add(badgeWrap, BorderLayout.EAST);

    cab.add(lblTitulo, BorderLayout.NORTH);
    cab.add(info, BorderLayout.CENTER);
    return cab;
  }

  // ── Lista de ítems de la venta ────────────────────────────────────────
  private JScrollPane crearListaItems(VentaDTO venta) {
    JPanel lista = new JPanel();
    lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
    lista.setBackground(Colores.BLANCO);
    lista.setBorder(new EmptyBorder(8, 0, 8, 0));

    // encabezado de columnas
    JPanel header = new JPanel(new GridLayout(1, 4));
    header.setBackground(new Color(248, 249, 252));
    header.setBorder(new EmptyBorder(10, 28, 10, 28));
    header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
    for (String col : new String[]{"Producto", "Precio unit.", "Cantidad", "Subtotal"}) {
      JLabel lbl = new JLabel(col);
      lbl.setFont(Fuentes.b(12));
      lbl.setForeground(new Color(100, 116, 139));
      header.add(lbl);
    }
    lista.add(header);

    if (venta.getItems() != null) {
      for (ItemVentaDTO item : venta.getItems()) {
        lista.add(filaItem(item));
        lista.add(sepDelgado());
      }
    }

    JScrollPane scroll = new JScrollPane(lista);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setBackground(Colores.BLANCO);
    scroll.getVerticalScrollBar().setUnitIncrement(12);
    return scroll;
  }

  private JPanel filaItem(ItemVentaDTO item) {
    JPanel fila = new JPanel(new GridLayout(1, 4));
    fila.setBackground(Colores.BLANCO);
    fila.setBorder(new EmptyBorder(12, 28, 12, 28));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

    JLabel lblNombre = new JLabel(item.getNombre());
    lblNombre.setFont(Fuentes.r(13));
    lblNombre.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblPrecio = new JLabel("$" + fmt(item.getPrecioUnitario()));
    lblPrecio.setFont(Fuentes.r(13));
    lblPrecio.setForeground(Colores.GRIS_TEXTO);

    JLabel lblCantidad = new JLabel(String.valueOf(item.getCantidad()));
    lblCantidad.setFont(Fuentes.b(13));
    lblCantidad.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblSubtotal = new JLabel("$" + fmt(item.getSubtotal()));
    lblSubtotal.setFont(Fuentes.b(13));
    lblSubtotal.setForeground(Colores.TEXTO_OSCURO);

    fila.add(lblNombre);
    fila.add(lblPrecio);
    fila.add(lblCantidad);
    fila.add(lblSubtotal);
    return fila;
  }

  // ── Pie: subtotal, IVA, total ─────────────────────────────────────────
  private JPanel crearPieTotales(VentaDTO venta) {
    JPanel pie = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 249, 252));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Colores.BORDE_GRIS);
        g.drawLine(0, 0, getWidth(), 0);
        super.paintComponent(g2d);
      }

    };
    pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));
    pie.setOpaque(false);
    pie.setBorder(new EmptyBorder(16, 28, 20, 28));

    pie.add(filaTotales("Subtotal (sin IVA)", "$" + fmt(venta.getSubtotal()), false));
    pie.add(Box.createVerticalStrut(6));
    pie.add(filaTotales("IVA (16%)", "$" + fmt(venta.getIva()), false));
    pie.add(Box.createVerticalStrut(10));

    // separador
    JSeparator sep = new JSeparator();
    sep.setForeground(Colores.BORDE_GRIS);
    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    pie.add(sep);
    pie.add(Box.createVerticalStrut(10));

    pie.add(filaTotales("Total", "$" + fmt(venta.getTotal()), true));
    pie.add(Box.createVerticalStrut(16));

    JButton btnCerrar = Botones.azulDialogo("Cerrar");
    btnCerrar.setAlignmentX(LEFT_ALIGNMENT);
    btnCerrar.addActionListener(e -> dispose());
    pie.add(btnCerrar);

    return pie;
  }

  private JPanel filaTotales(String label, String valor, boolean destacado) {
    JPanel fila = new JPanel(new BorderLayout());
    fila.setOpaque(false);
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

    JLabel lbl = new JLabel(label);
    lbl.setFont(destacado ? Fuentes.b(15) : Fuentes.r(13));
    lbl.setForeground(destacado ? Colores.TEXTO_OSCURO : Colores.GRIS_TEXTO);

    JLabel val = new JLabel(valor);
    val.setFont(destacado ? Fuentes.b(16) : Fuentes.r(13));
    val.setForeground(destacado ? new Color(29, 78, 216) : Colores.GRIS_TEXTO);

    fila.add(lbl, BorderLayout.WEST);
    fila.add(val, BorderLayout.EAST);
    return fila;
  }

  // ── Helpers ───────────────────────────────────────────────────────────
  private JLabel badgePago(TipoPago tipo) {
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
    JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
    lbl.setFont(Fuentes.b(11));
    lbl.setForeground(color);
    lbl.setOpaque(true);
    lbl.setBackground(bg);
    lbl.setBorder(new EmptyBorder(4, 12, 4, 12));
    return lbl;
  }

  private JPanel sepDelgado() {
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
