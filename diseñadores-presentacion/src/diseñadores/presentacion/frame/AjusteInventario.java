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

public class AjusteInventario extends JDialog {

  private final VentasControl control;
  private final ItemConteoDTO item;
  private final Runnable onSuccess;

  public AjusteInventario(JFrame parent, VentasControl control, ItemConteoDTO item, Runnable onSuccess) {
    super(parent, "Ajustar Inventario", true);
    this.control = control;
    this.item = item;
    this.onSuccess = onSuccess;

    setSize(440, 350);
    setLocationRelativeTo(parent);
    construirContenido();
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Ajustar: " + item.getNombre());
    titulo.setFont(Fuentes.b(18));
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(18));

    JPanel infoRow = new JPanel(new GridLayout(1, 3, 12, 0));
    infoRow.setOpaque(false);
    infoRow.setAlignmentX(LEFT_ALIGNMENT);
    infoRow.add(miniCard("Sistema", String.valueOf(item.getStockSistema()), Colores.AZUL));
    infoRow.add(miniCard("Físico", String.valueOf(item.getStockFisico()), new Color(217, 119, 6)));

    int d = item.getDiferencia();
    String dTxt = d > 0 ? "+" + d : String.valueOf(d);
    infoRow.add(miniCard("Diff", dTxt, d < 0 ? Colores.ROJO : new Color(21, 128, 61)));

    panel.add(infoRow);
    panel.add(Box.createVerticalStrut(18));

    JTextField campoFisico = new JTextField(String.valueOf(item.getStockFisico()));
    campoFisico.setFont(Fuentes.r(14));
    campoFisico.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(Colores.BORDE_GRIS, 1, 8),
      new EmptyBorder(8, 12, 8, 12)));
    campoFisico.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    campoFisico.setAlignmentX(LEFT_ALIGNMENT);

    panel.add(new JLabel("Corregir Stock Físico Actual:"));
    panel.add(Box.createVerticalStrut(6));
    panel.add(campoFisico);
    panel.add(Box.createVerticalStrut(18));

    JButton btnConfirmar = crearBoton("Confirmar Ajuste");
    btnConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    btnConfirmar.setAlignmentX(LEFT_ALIGNMENT);
    btnConfirmar.addActionListener(e -> {
      try {
        int nuevoFisico = Integer.parseInt(campoFisico.getText().trim());
        control.ajustarStock(item.getCodigo(), nuevoFisico);
        item.setStockFisico(nuevoFisico);
        if (onSuccess != null) {
          onSuccess.run();
        }
        dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });

    panel.add(btnConfirmar);
    setContentPane(panel);
  }

  private JPanel miniCard(String etiqueta, String valor, Color colorVal) {
    JPanel p = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(new EmptyBorder(10, 14, 10, 14));

    JLabel lE = new JLabel(etiqueta);
    lE.setFont(Fuentes.r(11));
    lE.setForeground(Colores.GRIS_TEXTO);
    JLabel lV = new JLabel(valor);
    lV.setFont(Fuentes.b(18));
    lV.setForeground(colorVal);

    p.add(lE);
    p.add(Box.createVerticalStrut(3));
    p.add(lV);
    return p;
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
