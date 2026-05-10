package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class AgregarProveedorProducto extends JDialog {

  private final VentasControl control;
  private final ProductoDTO producto;
  private final List<ProductoDTO> todosLosProductos;
  private final Runnable onSuccess;

  public AgregarProveedorProducto(JFrame parent, VentasControl control,
    ProductoDTO producto, List<ProductoDTO> todosLosProductos,
    Runnable onSuccess) {
    super(parent, "Agregar Proveedor a " + producto.getNombre(), true);
    this.control = control;
    this.producto = producto;
    this.todosLosProductos = todosLosProductos;
    this.onSuccess = onSuccess;

    setSize(460, 420);
    setLocationRelativeTo(parent);
    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Asociar Proveedor");
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);

    panel.add(Box.createVerticalStrut(6));
    JLabel sub = new JLabel("Producto: " + producto.getNombre() + " (" + producto.getCodigo() + ")");
    sub.setFont(Fuentes.r(13));
    sub.setForeground(Colores.GRIS_TEXTO);
    sub.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(sub);

    panel.add(Box.createVerticalStrut(20));
    JLabel lblProv = new JLabel("Seleccionar Proveedor");
    lblProv.setFont(Fuentes.b(12));
    lblProv.setForeground(Colores.TEXTO_OSCURO);
    lblProv.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(lblProv);

    List<ProveedorDTO> proveedores = control.obtenerProveedores();
    String[] nombresProv = proveedores.stream()
      .map(ProveedorDTO::getNombre)
      .toArray(String[]::new);
    JComboBox<String> comboProveedor = new JComboBox<>(nombresProv);
    comboProveedor.setFont(Fuentes.r(13));
    comboProveedor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    comboProveedor.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(comboProveedor);

    panel.add(Box.createVerticalStrut(10));
    panel.add(crearEtiqueta("Precio ($)"));
    JTextField campoPrecio = crearCampoTexto();
    panel.add(campoPrecio);

    panel.add(Box.createVerticalStrut(10));
    panel.add(crearEtiqueta("Tiempo de Entrega"));
    JTextField campoTiempo = crearCampoTexto();
    panel.add(campoTiempo);

    panel.add(Box.createVerticalStrut(18));
    JButton btnGuardar = crearBoton("Asociar Proveedor");
    btnGuardar.addActionListener(e -> {
      String nombre = (String) comboProveedor.getSelectedItem();
      String precio = campoPrecio.getText().trim();
      String tiempo = campoTiempo.getText().trim();

      if (nombre == null || precio.isEmpty() || tiempo.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Campos obligatorios.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      if (proveedorYaUsado(nombre)) {
        JOptionPane.showMessageDialog(this, "Proveedor ya ocupado.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      ProveedorDTO provDto = proveedores.stream()
        .filter(p -> p.getNombre().equals(nombre))
        .findFirst().orElse(null);

      provDto.setPrecioProveedor(new BigDecimal(precio.replace("$", "")));
      provDto.setTiempoEntregaProveedor(tiempo.contains("día") ? tiempo : tiempo + " días");
      producto.setProveedor(provDto);
      if (onSuccess != null) {
        onSuccess.run();
      }
      dispose();
    });
    panel.add(btnGuardar);

    setContentPane(panel);
  }

  private JLabel crearEtiqueta(String texto) {
    JLabel lbl = new JLabel(texto);
    lbl.setFont(Fuentes.b(12));
    return lbl;
  }

  private JTextField crearCampoTexto() {
    JTextField tf = new JTextField();
    tf.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(Colores.BORDE_GRIS, 1, 8),
      new EmptyBorder(8, 12, 8, 12)));
    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    tf.setAlignmentX(LEFT_ALIGNMENT);
    return tf;
  }

  private JButton crearBoton(String texto) {
    JButton btn = new JButton(texto);
    btn.setFont(Fuentes.b(14));
    btn.setForeground(Colores.BLANCO);
    btn.setBackground(Colores.AZUL);
    btn.setOpaque(true);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setAlignmentX(LEFT_ALIGNMENT);
    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    return btn;
  }

  private boolean proveedorYaUsado(String nombre) {
    return todosLosProductos.stream()
      .anyMatch(p -> p != producto
      && p.getProveedor() != null
      && p.getProveedor().getNombre().equalsIgnoreCase(nombre));
  }

}
