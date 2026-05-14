package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Botones;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class AgregarProveedor extends JDialog {

  private final VentasControl control;
  private final Runnable onSuccess;
  private JTextField[] campos;
  private JCheckBox chkActivo;

  public AgregarProveedor(JFrame parent, VentasControl control, Runnable onSuccess) {
    super(parent, "Nuevo Proveedor", true);
    this.control = control;
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
    JLabel titulo = new JLabel("Nuevo Proveedor");
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);

    JLabel subtitulo = new JLabel("Ingrese la información del proveedor");
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

    campos = new JTextField[7];
    campos[0] = crearCampo("", 50);
    panel.add(crearFilaCampo("Nombre *", campos[0]));
    panel.add(Box.createVerticalStrut(8));

    campos[1] = crearCampo("", 20);
    panel.add(crearFilaCampo("Código", campos[1]));
    panel.add(Box.createVerticalStrut(8));

    campos[6] = crearCampo("", 100);
    panel.add(crearFilaCampo("Términos de pago", campos[6]));
    return panel;
  }

  private JPanel crearCamposContacto() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    campos[2] = crearCampo("", 100);
    panel.add(crearFilaCampo("Contacto *", campos[2]));
    panel.add(Box.createVerticalStrut(8));

    campos[3] = crearCampo("", 20);
    panel.add(crearFilaCampo("Teléfono *", campos[3]));
    panel.add(Box.createVerticalStrut(8));

    campos[4] = crearCampo("", 100);
    panel.add(crearFilaCampo("Email *", campos[4]));
    panel.add(Box.createVerticalStrut(8));

    campos[5] = crearCampo("", 200);
    panel.add(crearFilaCampo("Dirección", campos[5]));
    return panel;
  }

  private JTextField crearCampo(String valor, int limite) {
    JTextField tf = new JTextField(valor);
    tf.setFont(Fuentes.r(13));
    tf.setForeground(Colores.TEXTO_OSCURO);
    tf.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(Colores.BORDE_GRIS, 1, 8),
      new EmptyBorder(8, 12, 8, 12)));
    tf.setAlignmentX(LEFT_ALIGNMENT);
    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    tf.setPreferredSize(new Dimension(0, 40));

    if (limite > 0) {
      tf.setDocument(new PlainDocument() {
        @Override
        public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
          throws javax.swing.text.BadLocationException {
          if (getLength() + str.length() <= limite) {
            super.insertString(offs, str, a);
          }
        }

      });
      try {
        tf.getDocument().insertString(0, valor, null);
      } catch (javax.swing.text.BadLocationException ignored) {
      }
    }
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
    chkActivo = new JCheckBox("Proveedor activo", true);
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
    JButton btn = Botones.azulDialogo("Agregar Proveedor");
    btn.setAlignmentX(LEFT_ALIGNMENT);
    btn.addActionListener(e -> manejarGuardado());
    return btn;
  }

  private void manejarGuardado() {
    if (!validarObligatorios()) {
      return;
    }

    try {
      ProveedorDTO nuevo = new ProveedorDTO(
        campos[0].getText().trim(),
        campos[1].getText().trim().isEmpty() ? null : campos[1].getText().trim(),
        campos[2].getText().trim(),
        campos[3].getText().trim(),
        campos[4].getText().trim(),
        campos[5].getText().trim(),
        campos[6].getText().trim(),
        chkActivo.isSelected());
      control.guardarProveedor(nuevo);
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
    String[] nombres = {"nombre", "contacto", "teléfono", "email"};
    int[] indices = {0, 2, 3, 4};
    for (int i = 0; i < indices.length; i++) {
      if (campos[indices[i]].getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
          "El " + nombres[i] + " es obligatorio.",
          "Error", JOptionPane.WARNING_MESSAGE);
        return false;
      }
    }
    return true;
  }

}
