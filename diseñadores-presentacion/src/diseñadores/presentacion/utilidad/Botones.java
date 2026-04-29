package diseñadores.presentacion.utilidad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class Botones {

  private Botones() {
  }

  public static JButton azul(String texto) {
    return crear(texto, Colores.AZUL, Colores.AZUL_HOVER, 10, Colores.BLANCO, Fuentes.b(14), new Dimension(150, 42));
  }

  public static JButton azulChico(String texto) {
    return crear(texto, Colores.AZUL, Colores.AZUL_HOVER, 8, Colores.BLANCO, Fuentes.b(12), new Dimension(100, 34));
  }

  public static JButton amarillo(String texto) {
    return crear(texto, new Color(255, 200, 0), new Color(240, 180, 0), 10, new Color(30, 30, 30), Fuentes.b(13), new Dimension(180, 42));
  }

  public static JButton verde(String texto) {
    return crear(texto, Colores.VERDE, Colores.VERDE_HOVER, 8, Colores.BLANCO, Fuentes.b(13), new Dimension(120, 38));
  }

  public static JButton rojo(String texto) {
    return crear(texto, Colores.ROJO, new Color(200, 50, 50), 8, Colores.BLANCO, Fuentes.b(13), new Dimension(120, 38));
  }

  public static JButton gris(String texto) {
    return crear(texto, new Color(245, 246, 248), new Color(229, 231, 235), 8, Colores.TEXTO_OSCURO, Fuentes.b(13), new Dimension(120, 38));
  }

  public static JButton azulDialogo(String texto) {
    return crear(texto, Colores.AZUL, Colores.AZUL_HOVER, 10, Colores.BLANCO, Fuentes.b(14), new Dimension(Integer.MAX_VALUE, 48));
  }

  public static JButton menuAzul(String texto) {
    return crear(texto, Colores.AZUL, Colores.AZUL_HOVER, 12, Colores.BLANCO, Fuentes.b(14), new Dimension(Integer.MAX_VALUE, 48));
  }

  public static JButton menuRojo(String texto) {
    return crear(texto, Colores.ROJO, Colores.ROJO_HOVER, 12, Colores.BLANCO, Fuentes.b(14), new Dimension(Integer.MAX_VALUE, 48));
  }

  private static JButton crear(String texto, Color base, Color hover, int radio, Color textoColor, Font fuente, Dimension tamano) {
    JButton b = new JButton(texto) {
      private boolean ov = false;

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
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radio, radio));
        super.paintComponent(g2d);
      }
    };
    b.setForeground(textoColor);
    b.setFont(fuente);
    if (tamano != null) {
      if (tamano.width == Integer.MAX_VALUE) {
        b.setMaximumSize(tamano);
      } else {
        b.setPreferredSize(tamano);
      }
    }
    return b;
  }

}
