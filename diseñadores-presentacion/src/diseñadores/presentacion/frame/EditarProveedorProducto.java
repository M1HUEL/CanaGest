package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

public class EditarProveedorProducto extends JDialog {

  private final ProveedorDTO proveedor;
  private final ProductoDTO producto;
  private final Runnable onSuccess;

  public EditarProveedorProducto(JFrame parent, ProveedorDTO proveedor,
    ProductoDTO producto, Runnable onSuccess) {
    super(parent, "Editar Proveedor: " + proveedor.getNombre(), true);
    this.proveedor = proveedor;
    this.producto = producto;
    this.onSuccess = onSuccess;

    setSize(460, 380);
    setLocationRelativeTo(parent);
    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Editar Proveedor Asociado");
    titulo.setFont(Fuentes.b(20));
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(20));

    String[] etqs = {"Nombre", "Precio ($)", "Tiempo Entrega"};
    String[] vals = {
      proveedor.getNombre(),
      proveedor.getPrecioProveedor() != null ? proveedor.getPrecioProveedor().toString() : "",
      proveedor.getTiempoEntregaProveedor()
    };
    JTextField[] campos = new JTextField[3];

    for (int i = 0; i < 3; i++) {
      JLabel lbl = new JLabel(etqs[i]);
      lbl.setFont(Fuentes.b(12));
      panel.add(lbl);
      campos[i] = new JTextField(vals[i]);
      campos[i].setBorder(BorderFactory.createCompoundBorder(
        new Bordes(Colores.BORDE_GRIS, 1, 8),
        new EmptyBorder(8, 12, 8, 12)));
      campos[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
      campos[i].setAlignmentX(LEFT_ALIGNMENT);
      panel.add(campos[i]);
      panel.add(Box.createVerticalStrut(10));
    }

    JButton btnGuardar = new JButton("Guardar Cambios");
    btnGuardar.setFont(Fuentes.b(14));
    btnGuardar.setForeground(Colores.BLANCO);
    btnGuardar.setBackground(Colores.AZUL);
    btnGuardar.setOpaque(true);
    btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    btnGuardar.addActionListener(e -> {
      proveedor.setNombre(campos[0].getText().trim());
      proveedor.setPrecioProveedor(new BigDecimal(campos[1].getText().trim()));
      proveedor.setTiempoEntregaProveedor(campos[2].getText().trim());
      producto.setProveedor(proveedor);
      if (onSuccess != null) {
        onSuccess.run();
      }
      dispose();
    });
    panel.add(btnGuardar);

    setContentPane(panel);
  }

}
