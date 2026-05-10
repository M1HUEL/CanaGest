package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DetalleOrdenCompra extends JDialog {

  private final OrdenCompraDTO orden;

  public DetalleOrdenCompra(JFrame parent, OrdenCompraDTO orden) {
    super(parent, "Detalle de Orden de Compra", true);
    this.orden = orden;

    setSize(600, 500);
    setLocationRelativeTo(parent);
    setResizable(true);

    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    panel.add(crearEncabezado());
    panel.add(Box.createVerticalStrut(6));
    panel.add(crearLabelFecha());
    panel.add(Box.createVerticalStrut(24));

    panel.add(crearSeccion("PROVEEDOR"));
    panel.add(Box.createVerticalStrut(12));
    panel.add(crearFilaInfo("Nombre", orden.getProveedorNombre()));
    panel.add(Box.createVerticalStrut(20));

    panel.add(crearSeccion("DETALLE DE LA ORDEN"));
    panel.add(Box.createVerticalStrut(12));
    panel.add(crearFilaInfo("Cantidad de productos", orden.getProductos() + " items"));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Total", String.format("$%,.2f", orden.getTotal().doubleValue())));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Estado", orden.getEstado()));

    setContentPane(panel);
  }

  private JPanel crearEncabezado() {
    JPanel headerRow = new JPanel(new BorderLayout(10, 0));
    headerRow.setOpaque(false);

    JLabel lblNum = new JLabel(orden.getNumero());
    lblNum.setFont(Fuentes.b(22));
    lblNum.setForeground(Colores.TEXTO_OSCURO);

    JLabel badge = crearBadgeEstado();
    headerRow.add(lblNum, BorderLayout.WEST);
    headerRow.add(badge, BorderLayout.EAST);
    return headerRow;
  }

  private JLabel crearLabelFecha() {
    JLabel lblFecha = new JLabel("Fecha: " + orden.getFecha());
    lblFecha.setFont(Fuentes.r(13));
    lblFecha.setForeground(Colores.GRIS_TEXTO);
    return lblFecha;
  }

  private JLabel crearBadgeEstado() {
    Color badgeColor, badgeBg;
    switch (orden.getEstado()) {
      case "Pendiente" -> {
        badgeColor = new Color(161, 110, 0);
        badgeBg = new Color(254, 243, 199);
      }
      case "Aprobada" -> {
        badgeColor = new Color(30, 80, 180);
        badgeBg = new Color(219, 234, 254);
      }
      default -> {
        badgeColor = new Color(21, 128, 61);
        badgeBg = new Color(220, 252, 231);
      }
    }
    JLabel badge = new JLabel(orden.getEstado(), SwingConstants.CENTER);
    badge.setFont(Fuentes.b(11));
    badge.setForeground(badgeColor);
    badge.setOpaque(true);
    badge.setBackground(badgeBg);
    badge.setBorder(new EmptyBorder(4, 12, 4, 12));
    return badge;
  }

  private JLabel crearSeccion(String texto) {
    JLabel label = new JLabel(texto);
    label.setFont(Fuentes.b(12));
    label.setForeground(Colores.GRIS_TEXTO);
    label.setAlignmentX(LEFT_ALIGNMENT);
    return label;
  }

  private JPanel crearFilaInfo(String label, String valor) {
    JPanel row = new JPanel(new BorderLayout(10, 0)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(new EmptyBorder(12, 14, 12, 14));
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    row.setAlignmentX(LEFT_ALIGNMENT);

    JLabel l = new JLabel(label);
    l.setFont(Fuentes.r(12));
    l.setForeground(Colores.GRIS_TEXTO);

    JLabel v = new JLabel(valor != null ? valor : "-");
    v.setFont(Fuentes.r(14));
    v.setForeground(Colores.TEXTO_OSCURO);

    row.add(l, BorderLayout.WEST);
    row.add(v, BorderLayout.EAST);
    return row;
  }

}
