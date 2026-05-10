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
import java.math.BigDecimal;

public class AgregarExistenciaProducto extends JDialog {

  private final VentasControl control;
  private final Runnable onSuccess;

  public AgregarExistenciaProducto(JFrame parent, VentasControl control, Runnable onSuccess) {
    super(parent, "Nuevo Producto", true);
    this.control = control;
    this.onSuccess = onSuccess;

    setSize(520, 520);
    setLocationRelativeTo(parent);
    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Nuevo Producto");
    titulo.setFont(Fuentes.b(20));
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(20));

    JTextField tfCodigo = crearCampo();
    JTextField tfNombre = crearCampo();
    JTextField tfPrecio = crearCampo();
    JTextField tfStock = crearCampo();
    JTextField tfMinimo = crearCampo();
    JTextField tfMaximo = crearCampo();

    panel.add(tfCodigo);
    panel.add(Box.createVerticalStrut(10));
    panel.add(tfNombre);
    panel.add(Box.createVerticalStrut(10));
    panel.add(tfPrecio);
    panel.add(Box.createVerticalStrut(10));
    panel.add(tfStock);
    panel.add(Box.createVerticalStrut(10));
    panel.add(tfMinimo);
    panel.add(Box.createVerticalStrut(10));
    panel.add(tfMaximo);
    panel.add(Box.createVerticalStrut(20));

    JButton btnGuardar = crearBoton();
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    btnGuardar.addActionListener(e -> {
      try {
        String codigo = tfCodigo.getText().trim();
        String nombre = tfNombre.getText().trim();
        BigDecimal precio = new BigDecimal(tfPrecio.getText().trim());
        int stock = Integer.parseInt(tfStock.getText().trim());
        int min = Integer.parseInt(tfMinimo.getText().trim());
        int max = Integer.parseInt(tfMaximo.getText().trim());

        if (codigo.isEmpty() || nombre.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Código y nombre son obligatorios.", "Error", JOptionPane.WARNING_MESSAGE);
          return;
        }

        ProductoDTO nuevo = new ProductoDTO(codigo, nombre, precio, stock, min, max, null);
        control.guardarProducto(nuevo);
        if (onSuccess != null) {
          onSuccess.run();
        }
        dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Valores numéricos inválidos.", "Error", JOptionPane.WARNING_MESSAGE);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    panel.add(btnGuardar);
    setContentPane(panel);
  }

  private JTextField crearCampo() {
    JTextField tf = new JTextField();
    tf.setFont(Fuentes.r(13));
    tf.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(Colores.BORDE_GRIS, 1, 8),
      new EmptyBorder(8, 12, 8, 12)));
    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    tf.setAlignmentX(LEFT_ALIGNMENT);
    return tf;
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
