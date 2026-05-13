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
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.util.List;

public class AgregarExistenciaProducto extends JDialog {

  private final VentasControl control;
  private final Runnable onSuccess;

  public AgregarExistenciaProducto(JFrame parent, VentasControl control, Runnable onSuccess) {
    super(parent, "Nuevo Producto", true);
    this.control = control;
    this.onSuccess = onSuccess;
    setSize(520, 640);
    setLocationRelativeTo(parent);
    setResizable(false);
    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Nuevo Producto");
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);

    JLabel subtitulo = new JLabel("Completa los datos para registrar el producto.");
    subtitulo.setFont(Fuentes.r(13));
    subtitulo.setForeground(Colores.GRIS_TEXTO);
    subtitulo.setAlignmentX(LEFT_ALIGNMENT);

    panel.add(titulo);
    panel.add(Box.createVerticalStrut(4));
    panel.add(subtitulo);
    panel.add(Box.createVerticalStrut(24));

    JTextField tfCodigo = crearCampo();
    JTextField tfNombre = crearCampo();
    JTextField tfPrecio = crearCampo();
    JTextField tfStock = crearCampo();
    JTextField tfMinimo = crearCampo();
    JTextField tfMaximo = crearCampo();

    panel.add(crearGrupoCampo("Código *", tfCodigo));
    panel.add(Box.createVerticalStrut(12));
    panel.add(crearGrupoCampo("Nombre *", tfNombre));
    panel.add(Box.createVerticalStrut(12));
    panel.add(crearGrupoCampo("Precio unitario ($)", tfPrecio));
    panel.add(Box.createVerticalStrut(12));

    JPanel filaStock = new JPanel(new GridLayout(1, 3, 12, 0));
    filaStock.setOpaque(false);
    filaStock.setAlignmentX(LEFT_ALIGNMENT);
    filaStock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

    filaStock.add(crearGrupoCampo("Stock actual", tfStock));
    filaStock.add(crearGrupoCampo("Stock mínimo", tfMinimo));
    filaStock.add(crearGrupoCampo("Stock máximo", tfMaximo));

    panel.add(filaStock);
    panel.add(Box.createVerticalStrut(12));

    List<ProveedorDTO> proveedores = control.obtenerProveedores();
    JComboBox<ProveedorDTO> cbProveedor = new JComboBox<>();
    cbProveedor.addItem(null);
    for (ProveedorDTO prov : proveedores) {
      cbProveedor.addItem(prov);
    }
    cbProveedor.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setText(value == null ? "Sin proveedor" : ((ProveedorDTO) value).getNombre());
        return this;
      }

    });
    cbProveedor.setFont(Fuentes.r(13));
    cbProveedor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    cbProveedor.setAlignmentX(LEFT_ALIGNMENT);

    panel.add(crearGrupoCombo("Proveedor", cbProveedor));
    panel.add(Box.createVerticalStrut(24));

    JLabel nota = new JLabel("* Campos obligatorios");
    nota.setFont(Fuentes.r(11));
    nota.setForeground(Colores.GRIS_TEXTO);
    nota.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(nota);
    panel.add(Box.createVerticalStrut(16));

    JButton btnGuardar = crearBoton("Guardar Producto");
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    btnGuardar.addActionListener(e -> guardar(tfCodigo, tfNombre, tfPrecio, tfStock, tfMinimo, tfMaximo, cbProveedor));

    panel.add(btnGuardar);

    JScrollPane scroll = new JScrollPane(panel);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.getVerticalScrollBar().setUnitIncrement(12);
    setContentPane(scroll);
  }

  private JPanel crearGrupoCampo(String labelTexto, JTextField campo) {
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

  private JPanel crearGrupoCombo(String labelTexto, JComboBox<?> combo) {
    JPanel grupo = new JPanel();
    grupo.setLayout(new BoxLayout(grupo, BoxLayout.Y_AXIS));
    grupo.setOpaque(false);
    grupo.setAlignmentX(LEFT_ALIGNMENT);
    grupo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

    JLabel lbl = new JLabel(labelTexto);
    lbl.setFont(Fuentes.b(12));
    lbl.setForeground(Colores.TEXTO_OSCURO);
    lbl.setAlignmentX(LEFT_ALIGNMENT);

    combo.setAlignmentX(LEFT_ALIGNMENT);

    grupo.add(lbl);
    grupo.add(Box.createVerticalStrut(5));
    grupo.add(combo);
    return grupo;
  }

  private void guardar(JTextField tfCodigo, JTextField tfNombre, JTextField tfPrecio,
    JTextField tfStock, JTextField tfMinimo, JTextField tfMaximo,
    JComboBox<ProveedorDTO> cbProveedor) {
    try {
      String codigo = tfCodigo.getText().trim();
      String nombre = tfNombre.getText().trim();

      if (codigo.isEmpty() || nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this,
          "Código y nombre son obligatorios.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      BigDecimal precio = tfPrecio.getText().trim().isEmpty()
        ? BigDecimal.ZERO
        : new BigDecimal(tfPrecio.getText().trim());

      int stock = tfStock.getText().trim().isEmpty() ? 0 : Integer.parseInt(tfStock.getText().trim());
      int min = tfMinimo.getText().trim().isEmpty() ? 0 : Integer.parseInt(tfMinimo.getText().trim());
      int max = tfMaximo.getText().trim().isEmpty() ? 0 : Integer.parseInt(tfMaximo.getText().trim());

      if (min > max && max != 0) {
        JOptionPane.showMessageDialog(this,
          "El stock mínimo no puede ser mayor al máximo.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      ProveedorDTO proveedor = (ProveedorDTO) cbProveedor.getSelectedItem();

      ProductoDTO nuevo = new ProductoDTO(codigo, nombre, precio, stock, min, max, proveedor);
      control.guardarProducto(nuevo);

      if (onSuccess != null) {
        onSuccess.run();
      }
      dispose();

    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,
        "Precio y stock deben ser valores numéricos válidos.", "Error", JOptionPane.WARNING_MESSAGE);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
        "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
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
    return new JButton(texto) {
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
  }

}
