package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class EditarExistenciaProducto extends JDialog {

  private final VentasControl control;
  private final ProductoDTO producto;
  private final Runnable onSuccess;

  public EditarExistenciaProducto(JFrame parent, VentasControl control, ProductoDTO producto, Runnable onSuccess) {
    super(parent, "Actualizar Stock: " + producto.getNombre(), true);
    this.control = control;
    this.producto = producto;
    this.onSuccess = onSuccess;

    setSize(564, 480);
    setLocationRelativeTo(parent);
    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Actualizar: " + producto.getNombre());
    titulo.setFont(Fuentes.b(18));
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(20));

    String[] etqs = {"Stock Actual", "Stock Mínimo", "Stock Máximo"};
    String[] vals = {
      String.valueOf(producto.getStock()),
      String.valueOf(producto.getStockMinimo()),
      String.valueOf(producto.getStockMaximo())
    };
    JTextField[] campos = new JTextField[3];

    for (int i = 0; i < etqs.length; i++) {
      JLabel lbl = new JLabel(etqs[i]);
      lbl.setFont(Fuentes.b(12));
      lbl.setAlignmentX(LEFT_ALIGNMENT);

      JTextField tf = new JTextField(vals[i]);
      tf.setFont(Fuentes.r(13));
      tf.setBorder(BorderFactory.createCompoundBorder(
        new Bordes(Colores.BORDE_GRIS, 1, 8),
        new EmptyBorder(8, 12, 8, 12)));
      tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
      tf.setAlignmentX(LEFT_ALIGNMENT);

      campos[i] = tf;
      panel.add(lbl);
      panel.add(Box.createVerticalStrut(4));
      panel.add(tf);
      panel.add(Box.createVerticalStrut(10));
    }

    JButton btnGuardar = crearBoton();
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    btnGuardar.addActionListener(e -> {
      try {
        int stock = Integer.parseInt(campos[0].getText().trim());
        int min = Integer.parseInt(campos[1].getText().trim());
        int max = Integer.parseInt(campos[2].getText().trim());
        control.actualizarStockCompleto(producto.getCodigo(), stock, min, max);
        if (onSuccess != null) {
          onSuccess.run();
        }
        dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Valores no válidos.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });

    panel.add(Box.createVerticalGlue());
    panel.add(btnGuardar);
    setContentPane(panel);
  }

  private JButton crearBoton() {
    JButton btn = new JButton("Guardar") {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(Colores.BLANCO);
        setFont(Fuentes.b(14));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            hover = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            hover = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    return btn;
  }

}
