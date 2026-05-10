package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
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
import java.util.List;

public class RegistrarNuevaOrdenCompra extends JDialog {

  private final VentasControl control;
  private final Runnable onSuccess;

  public RegistrarNuevaOrdenCompra(JFrame parent, VentasControl control, Runnable onSuccess) {
    super(parent, "Nueva Orden de Compra", true);
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

    JLabel titulo = new JLabel("Nueva Orden de Compra");
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(20));

    List<ProveedorDTO> proveedores = control.obtenerProveedores();
    List<ProveedorDTO> activos = proveedores.stream()
      .filter(ProveedorDTO::isActivo)
      .toList();

    String[] nombresProveedores = activos.stream()
      .map(ProveedorDTO::getNombre)
      .toArray(String[]::new);

    JComboBox<String> comboProveedor = new JComboBox<>(nombresProveedores);
    comboProveedor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    comboProveedor.setAlignmentX(LEFT_ALIGNMENT);

    panel.add(crearEtiqueta("Proveedor"));
    panel.add(Box.createVerticalStrut(4));
    panel.add(comboProveedor);
    panel.add(Box.createVerticalStrut(10));

    JTextField tfCant = crearCampo();
    JTextField tfTotal = crearCampo();

    panel.add(crearEtiqueta("Cantidad de productos"));
    panel.add(Box.createVerticalStrut(4));
    panel.add(tfCant);
    panel.add(Box.createVerticalStrut(10));
    panel.add(crearEtiqueta("Total ($)"));
    panel.add(Box.createVerticalStrut(4));
    panel.add(tfTotal);
    panel.add(Box.createVerticalStrut(20));

    JButton btnCrear = crearBoton("Crear Orden");
    btnCrear.setAlignmentX(LEFT_ALIGNMENT);
    btnCrear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

    btnCrear.addActionListener(e -> {
      String nombreProv = (String) comboProveedor.getSelectedItem();

      if (nombreProv == null || activos.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No hay proveedores activos disponibles.",
          "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      ProveedorDTO prov = activos.stream()
        .filter(p -> p.getNombre().equals(nombreProv))
        .findFirst().orElse(null);

      if (prov == null) {
        JOptionPane.showMessageDialog(this, "No se encontró el proveedor seleccionado.",
          "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      try {
        int cant = Integer.parseInt(tfCant.getText().trim());
        BigDecimal tot = new BigDecimal(tfTotal.getText().trim());

        if (cant <= 0) {
          JOptionPane.showMessageDialog(this, "La cantidad de productos debe ser mayor a cero.",
            "Error", JOptionPane.WARNING_MESSAGE);
          return;
        }
        if (tot.compareTo(BigDecimal.ZERO) <= 0) {
          JOptionPane.showMessageDialog(this, "El total debe ser mayor a cero.",
            "Error", JOptionPane.WARNING_MESSAGE);
          return;
        }

        OrdenCompraDTO nueva = new OrdenCompraDTO(null, null, prov, "Pendiente", cant, tot);
        control.guardarOrdenCompra(nueva);
        if (onSuccess != null) {
          onSuccess.run();
        }
        dispose();

      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Ingrese números válidos en cantidad y total.",
          "Error", JOptionPane.WARNING_MESSAGE);
      } catch (IllegalArgumentException | IllegalStateException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(),
          "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    panel.add(btnCrear);

    JScrollPane sp = new JScrollPane(panel);
    sp.setBorder(BorderFactory.createEmptyBorder());
    setContentPane(sp);
  }

  private JLabel crearEtiqueta(String texto) {
    JLabel lbl = new JLabel(texto);
    lbl.setFont(Fuentes.b(12));
    lbl.setForeground(Colores.TEXTO_OSCURO);
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    return lbl;
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

  private JButton crearBoton(String texto) {
    JButton btn = new JButton(texto) {
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
    btn.setForeground(Colores.BLANCO);
    btn.setFont(Fuentes.b(14));
    return btn;
  }

}
