package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ItemConteoDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ConteoInventario extends JDialog {

  private final VentasControl control;
  private final List<ItemConteoDTO> items;
  private final Runnable onSuccess;

  public ConteoInventario(JFrame parent, VentasControl control,
    List<ItemConteoDTO> items, Runnable onSuccess) {
    super(parent, "Iniciar Nuevo Conteo", true);
    this.control = control;
    this.items = items;
    this.onSuccess = onSuccess;

    setSize(500, 500);
    setLocationRelativeTo(parent);
    construirContenido();
  }

  private void construirContenido() {
    JPanel mainPanel = new JPanel(new BorderLayout());

    JPanel listPanel = new JPanel();
    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
    listPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
    listPanel.setBackground(Colores.BLANCO);

    JTextField[] campos = new JTextField[items.size()];
    for (int i = 0; i < items.size(); i++) {
      ItemConteoDTO item = items.get(i);
      JPanel filaForm = new JPanel(new BorderLayout(15, 0));
      filaForm.setOpaque(false);
      filaForm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

      JLabel lbl = new JLabel(item.getNombre());
      lbl.setFont(Fuentes.r(13));
      lbl.setPreferredSize(new Dimension(220, 30));

      JTextField tf = new JTextField(String.valueOf(item.getStockFisico()));
      tf.setBorder(BorderFactory.createCompoundBorder(
        new Bordes(Colores.BORDE_GRIS, 1, 6),
        new EmptyBorder(4, 8, 4, 8)));
      campos[i] = tf;

      filaForm.add(lbl, BorderLayout.WEST);
      filaForm.add(tf, BorderLayout.CENTER);
      listPanel.add(filaForm);
      listPanel.add(Box.createVerticalStrut(10));
    }

    JScrollPane scroll = new JScrollPane(listPanel);
    scroll.setBorder(BorderFactory.createEmptyBorder());

    JButton btnGuardar = crearBoton("Guardar Todo el Conteo");
    btnGuardar.addActionListener(e -> {
      try {
        for (int i = 0; i < items.size(); i++) {
          int nf = Integer.parseInt(campos[i].getText().trim());
          ItemConteoDTO item = items.get(i);
          control.ajustarStock(item.getCodigo(), nf);
          item.setStockFisico(nf);
        }
        if (onSuccess != null) {
          onSuccess.run();
        }
        dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,
          "Hay valores inválidos en el formulario.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });

    JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
    south.setBackground(Colores.BLANCO);
    south.setBorder(new EmptyBorder(10, 0, 15, 0));
    south.add(btnGuardar);

    mainPanel.add(scroll, BorderLayout.CENTER);
    mainPanel.add(south, BorderLayout.SOUTH);

    setContentPane(mainPanel);
  }

  private JButton crearBoton(String texto) {
    JButton btn = new JButton(texto) {
      boolean over = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            over = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            over = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(over ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    btn.setForeground(Colores.BLANCO);
    btn.setFont(Fuentes.b(14));
    return btn;
  }

}
