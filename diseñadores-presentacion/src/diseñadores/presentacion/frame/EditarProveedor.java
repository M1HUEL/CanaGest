package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class EditarProveedor extends JDialog {

  private final VentasControl control;
  private final ProveedorDTO proveedor;
  private final Runnable onSuccess;
  private JTextField tfNombre, tfCodigo, tfTerminosPago, tfContacto, tfTelefono, tfEmail, tfDireccion;
  private JCheckBox chkActivo;

  public EditarProveedor(JFrame parent, VentasControl control, ProveedorDTO proveedor, Runnable onSuccess) {
    super(parent, "Editar Proveedor", true);
    this.control = control;
    this.proveedor = proveedor;
    this.onSuccess = onSuccess;

    setSize(540, 680);
    setLocationRelativeTo(parent);
    setResizable(true);
    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(24, 28, 24, 28));
    panel.setBackground(Colores.BLANCO);

    agregarCabecera(panel);
    panel.add(Box.createVerticalStrut(18));
    panel.add(crearSeccion("DATOS GENERALES"));
    panel.add(Box.createVerticalStrut(10));
    panel.add(crearCamposGenerales());

    panel.add(Box.createVerticalStrut(16));
    panel.add(crearSeccion("CONTACTO"));
    panel.add(Box.createVerticalStrut(10));
    panel.add(crearCamposContacto());

    panel.add(Box.createVerticalStrut(16));
    panel.add(crearPanelCheckbox());
    panel.add(Box.createVerticalStrut(20));
    panel.add(crearBotonGuardar());

    setContentPane(new JScrollPane(panel) {
      {
        setBorder(BorderFactory.createEmptyBorder());
        getVerticalScrollBar().setUnitIncrement(12);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      }

    });
  }

  private void agregarCabecera(JPanel panel) {
    JLabel titulo = new JLabel("Editar: " + proveedor.getNombre());
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);

    JLabel subtitulo = new JLabel("Modifique la información del proveedor");
    subtitulo.setFont(Fuentes.r(13));
    subtitulo.setForeground(Colores.GRIS_TEXTO);
    subtitulo.setAlignmentX(LEFT_ALIGNMENT);

    panel.add(titulo);
    panel.add(Box.createVerticalStrut(4));
    panel.add(subtitulo);
  }

  private JLabel crearSeccion(String texto) {
    JLabel label = new JLabel(texto);
    label.setFont(Fuentes.b(11));
    label.setForeground(Colores.GRIS_TEXTO);
    label.setAlignmentX(LEFT_ALIGNMENT);
    return label;
  }

  private JPanel crearCamposGenerales() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    tfNombre = crearCampo(proveedor.getNombre());
    panel.add(crearFilaCampo("Nombre *", tfNombre));
    panel.add(Box.createVerticalStrut(8));

    tfCodigo = crearCampo(proveedor.getCodigo());
    panel.add(crearFilaCampo("Código", tfCodigo));
    panel.add(Box.createVerticalStrut(8));

    tfTerminosPago = crearCampo(proveedor.getTerminosPago());
    panel.add(crearFilaCampo("Términos de pago", tfTerminosPago));
    return panel;
  }

  private JPanel crearCamposContacto() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    tfContacto = crearCampo(proveedor.getContacto());
    panel.add(crearFilaCampo("Contacto *", tfContacto));
    panel.add(Box.createVerticalStrut(8));

    tfTelefono = crearCampo(proveedor.getTelefono());
    panel.add(crearFilaCampo("Teléfono *", tfTelefono));
    panel.add(Box.createVerticalStrut(8));

    tfEmail = crearCampo(proveedor.getEmail());
    panel.add(crearFilaCampo("Email *", tfEmail));
    panel.add(Box.createVerticalStrut(8));

    tfDireccion = crearCampo(proveedor.getDireccion());
    panel.add(crearFilaCampo("Dirección", tfDireccion));
    return panel;
  }

  private JTextField crearCampo(String valor) {
    JTextField tf = new JTextField(valor);
    tf.setFont(Fuentes.r(13));
    tf.setForeground(Colores.TEXTO_OSCURO);
    tf.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(Colores.BORDE_GRIS, 1, 8),
      new EmptyBorder(8, 12, 8, 12)));
    tf.setAlignmentX(LEFT_ALIGNMENT);
    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    tf.setPreferredSize(new Dimension(0, 40));
    return tf;
  }

  private JPanel crearFilaCampo(String label, JTextField campo) {
    JPanel row = new JPanel();
    row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

    JLabel l = new JLabel(label);
    l.setFont(Fuentes.b(12));
    l.setForeground(Colores.TEXTO_OSCURO);
    l.setAlignmentX(LEFT_ALIGNMENT);

    row.add(l);
    row.add(Box.createVerticalStrut(5));
    row.add(campo);
    return row;
  }

  private JPanel crearPanelCheckbox() {
    chkActivo = new JCheckBox("Proveedor activo", proveedor.isActivo());
    chkActivo.setFont(Fuentes.r(13));
    chkActivo.setForeground(Colores.TEXTO_OSCURO);
    chkActivo.setOpaque(false);

    JPanel estadoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    estadoRow.setOpaque(false);
    estadoRow.setAlignmentX(LEFT_ALIGNMENT);
    estadoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
    estadoRow.add(chkActivo);
    return estadoRow;
  }

  private JButton crearBotonGuardar() {
    JButton btnGuardar = new JButton("Guardar Cambios");
    btnGuardar.setFont(Fuentes.b(14));
    btnGuardar.setForeground(Colores.BLANCO);
    btnGuardar.setBackground(Colores.AZUL);
    btnGuardar.setOpaque(true);
    btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

    btnGuardar.addActionListener(e -> manejarGuardado());
    return btnGuardar;
  }

  private void manejarGuardado() {
    if (!validarObligatorios()) {
      return;
    }

    try {
      proveedor.setNombre(tfNombre.getText().trim());
      proveedor.setCodigo(tfCodigo.getText().trim());
      proveedor.setContacto(tfContacto.getText().trim());
      proveedor.setTelefono(tfTelefono.getText().trim());
      proveedor.setEmail(tfEmail.getText().trim());
      proveedor.setDireccion(tfDireccion.getText().trim());
      proveedor.setTerminosPago(tfTerminosPago.getText().trim());
      proveedor.setActivo(chkActivo.isSelected());
      control.actualizarProveedor(proveedor);
      if (onSuccess != null) {
        onSuccess.run();
      }
      dispose();
    } catch (IllegalArgumentException | IllegalStateException ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(),
        "Error de validación", JOptionPane.WARNING_MESSAGE);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private boolean validarObligatorios() {
    if (tfNombre.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.WARNING_MESSAGE);
      return false;
    }
    if (tfContacto.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "El contacto es obligatorio.", "Error", JOptionPane.WARNING_MESSAGE);
      return false;
    }
    if (tfTelefono.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "El teléfono es obligatorio.", "Error", JOptionPane.WARNING_MESSAGE);
      return false;
    }
    if (tfEmail.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "El email es obligatorio.", "Error", JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }

}
