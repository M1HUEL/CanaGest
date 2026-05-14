package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.presentacion.utilidad.Botones;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MostrarDetalleProveedor extends JDialog {

  private final ProveedorDTO proveedor;

  public MostrarDetalleProveedor(JFrame parent, ProveedorDTO proveedor) {
    super(parent, "Detalle del Proveedor", true);
    this.proveedor = proveedor;

    setSize(600, 520);
    setLocationRelativeTo(parent);
    setResizable(true);

    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    panel.add(crearEncabezado());
    panel.add(Box.createVerticalStrut(4));

    JLabel lblCodigo = new JLabel("Código: " + (proveedor.getCodigo() != null ? proveedor.getCodigo() : "—"));
    lblCodigo.setFont(Fuentes.r(12));
    lblCodigo.setForeground(Colores.GRIS_TEXTO);
    lblCodigo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(lblCodigo);

    panel.add(Box.createVerticalStrut(24));
    panel.add(crearSeccion("INFORMACIÓN DE CONTACTO"));
    panel.add(Box.createVerticalStrut(12));
    panel.add(crearFilaInfo("Contacto", proveedor.getContacto()));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Teléfono", proveedor.getTelefono()));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Email", proveedor.getEmail()));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Dirección", proveedor.getDireccion()));
    panel.add(Box.createVerticalStrut(20));
    panel.add(crearSeccion("TÉRMINOS COMERCIALES"));
    panel.add(Box.createVerticalStrut(12));
    panel.add(crearFilaInfo("Términos de pago", proveedor.getTerminosPago()));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Precio proveedor",
      proveedor.getPrecioProveedor() != null
      ? "$" + String.format("%.2f", proveedor.getPrecioProveedor()) : "—"));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Tiempo de entrega", proveedor.getTiempoEntregaProveedor()));
    panel.add(Box.createVerticalStrut(24));

    JButton btnCerrar = Botones.azulDialogo("Cerrar");
    btnCerrar.setAlignmentX(LEFT_ALIGNMENT);
    btnCerrar.addActionListener(e -> dispose());
    panel.add(btnCerrar);

    setContentPane(panel);
  }

  private JPanel crearEncabezado() {
    JPanel header = new JPanel(new BorderLayout(10, 0));
    header.setOpaque(false);

    JLabel lblNombre = new JLabel(proveedor.getNombre());
    lblNombre.setFont(Fuentes.b(22));
    lblNombre.setForeground(Colores.TEXTO_OSCURO);

    JLabel badge = new JLabel(proveedor.isActivo() ? "Activo" : "Inactivo", SwingConstants.CENTER);
    badge.setFont(Fuentes.b(11));
    badge.setForeground(proveedor.isActivo() ? new Color(21, 128, 61) : new Color(100, 100, 100));
    badge.setOpaque(true);
    badge.setBackground(proveedor.isActivo() ? new Color(220, 252, 231) : new Color(229, 231, 235));
    badge.setBorder(new EmptyBorder(4, 12, 4, 12));

    header.add(lblNombre, BorderLayout.WEST);
    header.add(badge, BorderLayout.EAST);
    return header;
  }

  private JLabel crearSeccion(String texto) {
    JLabel label = new JLabel(texto);
    label.setFont(Fuentes.b(12));
    label.setForeground(Colores.GRIS_TEXTO);
    label.setAlignmentX(LEFT_ALIGNMENT);
    return label;
  }

  private JPanel crearFilaInfo(String etiqueta, String valor) {
    JPanel row = new JPanel(new BorderLayout(10, 0)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(new EmptyBorder(12, 14, 12, 14));
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    row.setAlignmentX(LEFT_ALIGNMENT);

    JLabel lblEtq = new JLabel(etiqueta);
    lblEtq.setFont(Fuentes.r(12));
    lblEtq.setForeground(Colores.GRIS_TEXTO);

    JLabel lblVal = new JLabel(valor != null ? valor : "-");
    lblVal.setFont(Fuentes.r(14));
    lblVal.setForeground(Colores.TEXTO_OSCURO);

    row.add(lblEtq, BorderLayout.WEST);
    row.add(lblVal, BorderLayout.EAST);
    return row;
  }

}
