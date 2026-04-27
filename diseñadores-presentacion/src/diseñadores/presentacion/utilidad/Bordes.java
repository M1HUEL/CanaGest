package diseñadores.presentacion.utilidad;

import javax.swing.border.AbstractBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Bordes extends AbstractBorder {

  private final Color color;
  private final int thickness;
  private final int arc;

  public static Bordes redondeado(Color color, int thickness, int arc) {
    return new Bordes(color, thickness, arc);
  }

  public Bordes(Color color, int thickness, int arc) {
    this.color = color;
    this.thickness = thickness;
    this.arc = arc;
  }

  @Override
  public void paintBorder(Component c, Graphics g2d, int x, int y, int w, int h) {
    Graphics2D g = (Graphics2D) g2d.create();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(color);
    g.setStroke(new BasicStroke(thickness));
    g.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, w - 1, h - 1, arc, arc));
    g.dispose();
  }

  @Override
  public Insets getBorderInsets(Component c) {
    return new Insets(thickness, thickness, thickness, thickness);
  }

  @Override
  public Insets getBorderInsets(Component c, Insets insets) {
    insets.set(thickness, thickness, thickness, thickness);
    return insets;
  }
}