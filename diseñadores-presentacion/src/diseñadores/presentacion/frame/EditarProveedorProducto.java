package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Botones;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class EditarProveedorProducto extends JDialog {

  private final ProveedorDTO proveedor;
  private final ProductoDTO producto;
  private final Runnable onSuccess;

  public EditarProveedorProducto(JFrame parent, ProveedorDTO proveedor,
    ProductoDTO producto, Runnable onSuccess) {
    super(parent, "Editar Proveedor", true);
    this.proveedor = proveedor;
    this.producto = producto;
    this.onSuccess = onSuccess;

    setSize(480, 400);
    setLocationRelativeTo(parent);
    setResizable(false);
    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Editar proveedor asociado");
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);

    JLabel subtitulo = new JLabel("Actualizando datos para: " + producto.getNombre());
    subtitulo.setFont(Fuentes.r(13));
    subtitulo.setForeground(Colores.GRIS_TEXTO);
    subtitulo.setAlignmentX(LEFT_ALIGNMENT);

    panel.add(titulo);
    panel.add(Box.createVerticalStrut(4));
    panel.add(subtitulo);
    panel.add(Box.createVerticalStrut(24));

    JTextField tfNombre = crearCampo(proveedor.getNombre());
    JTextField tfPrecio = crearCampo(
      proveedor.getPrecioProveedor() != null
      ? proveedor.getPrecioProveedor().toPlainString()
      : "");
    JTextField tfEntrega = crearCampo(
      proveedor.getTiempoEntregaProveedor() != null
      ? proveedor.getTiempoEntregaProveedor()
      : "");

    panel.add(grupoCampo("Nombre del proveedor", tfNombre));
    panel.add(Box.createVerticalStrut(12));
    panel.add(grupoCampo("Precio proveedor ($)", tfPrecio));
    panel.add(Box.createVerticalStrut(12));
    panel.add(grupoCampo("Tiempo de entrega", tfEntrega));
    panel.add(Box.createVerticalStrut(24));

    JButton btnGuardar = Botones.azulDialogo("Guardar Cambios");
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.addActionListener(e -> guardar(tfNombre, tfPrecio, tfEntrega));
    panel.add(btnGuardar);

    setContentPane(panel);
  }

  private void guardar(JTextField tfNombre, JTextField tfPrecio, JTextField tfEntrega) {
    try {
      String nombre = tfNombre.getText().trim();
      if (nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this,
          "El nombre del proveedor es obligatorio.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }
      proveedor.setNombre(nombre);
      proveedor.setPrecioProveedor(
        tfPrecio.getText().trim().isEmpty()
        ? BigDecimal.ZERO
        : new BigDecimal(tfPrecio.getText().trim()));
      proveedor.setTiempoEntregaProveedor(tfEntrega.getText().trim());
      producto.setProveedor(proveedor);
      if (onSuccess != null) {
        onSuccess.run();
      }
      dispose();
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,
        "El precio debe ser un número válido.", "Error", JOptionPane.WARNING_MESSAGE);
    }
  }

  private JPanel grupoCampo(String labelTexto, JTextField campo) {
    JPanel grupo = new JPanel();
    grupo.setLayout(new BoxLayout(grupo, BoxLayout.Y_AXIS));
    grupo.setOpaque(false);
    grupo.setAlignmentX(LEFT_ALIGNMENT);

    JLabel lbl = new JLabel(labelTexto);
    lbl.setFont(Fuentes.b(12));
    lbl.setForeground(Colores.TEXTO_OSCURO);
    lbl.setAlignmentX(LEFT_ALIGNMENT);

    grupo.add(lbl);
    grupo.add(Box.createVerticalStrut(5));
    grupo.add(campo);
    return grupo;
  }

  private JTextField crearCampo(String valor) {
    JTextField tf = new JTextField(valor);
    tf.setFont(Fuentes.r(13));
    tf.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(Colores.BORDE_GRIS, 1, 8),
      new EmptyBorder(8, 12, 8, 12)));
    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    tf.setAlignmentX(LEFT_ALIGNMENT);
    return tf;
  }

}
