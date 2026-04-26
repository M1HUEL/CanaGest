package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.VentaDTO;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Componentes;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class SeleccionarMetodoPago extends JFrame {

  private final VentaDTO ventaActual;

  private final JFrame frame;

  private final IVentas fachada;

  private final double total;

  private final Runnable onVentaFinalizada;

  private static final Map<String, Color[]> COLORES_METODO = new LinkedHashMap<>();

  static {
    COLORES_METODO.put("Efectivo", new Color[]{Colores.VERDE_METODO, Colores.VERDE_METODO_H});
    COLORES_METODO.put("Tarjeta", new Color[]{Colores.AZUL, Colores.AZUL_HOVER});
    COLORES_METODO.put("CoDi", new Color[]{Colores.MORADO, Colores.MORADO_HOVER});
    COLORES_METODO.put("Transferencia", new Color[]{Colores.NARANJA, Colores.NARANJA_HOVER});
  }

  public SeleccionarMetodoPago(JFrame frame, IVentas fachada,
    VentaDTO ventaActual, double total,
    Runnable onVentaFinalizada) {
    super("Metodo de pago");

    this.ventaActual = ventaActual;
    this.frame = frame;
    this.fachada = fachada;
    this.total = total;
    this.onVentaFinalizada = onVentaFinalizada;

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(frame.getWidth(), frame.getHeight());
    setLocation(frame.getLocation());

    JPanel root = Componentes.fondoAmarillo();
    root.add(Componentes.topBar(this), BorderLayout.NORTH);
    root.add(Componentes.centrado(buildCard(), 280, 30), BorderLayout.CENTER);
    setContentPane(root);

    setVisible(true);
  }

  private JPanel buildCard() {
    JPanel card = Componentes.tarjetaBlanca(22);
    card.setLayout(new BorderLayout(0, 20));
    card.setBorder(new EmptyBorder(32, 32, 32, 32));
    card.add(Componentes.etiquetaCentrada("Seleccione el metodo de pago", 22, true, Colores.TEXTO_OSCURO), BorderLayout.NORTH);
    card.add(gridMetodos(), BorderLayout.CENTER);
    card.add(botonesInferiores(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel gridMetodos() {
    JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
    grid.setOpaque(false);
    COLORES_METODO.forEach((nombre, colores)
      -> grid.add(botonMetodo(nombre, colores[0], colores[1])));
    return grid;
  }

  private JPanel botonMetodo(String nombre, Color base, Color hover) {
    JPanel btn = new JPanel(new GridLayout(2, 1, 0, 8)) {
      boolean ov = false;

      {
        setOpaque(false);
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

          @Override
          public void mouseClicked(MouseEvent e) {
            seleccionarMetodo(nombre);
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? hover : base);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
        super.paintComponent(g2d);
      }

    };
    btn.setBorder(new EmptyBorder(28, 20, 28, 20));
    btn.add(new JLabel("", SwingConstants.CENTER));
    btn.add(Componentes.etiquetaCentrada(nombre, 20, true, Colores.BLANCO));
    return btn;
  }

  private JPanel botonesInferiores() {
    JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
    row.setOpaque(false);
    row.setPreferredSize(new Dimension(0, 60));

    JButton btnCancelar = Componentes.botonAccion("Cancelar", Colores.ROJO, Colores.ROJO_HOVER);
    btnCancelar.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this, "¿Cancelar la venta?", "Cancelar",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (op == JOptionPane.YES_OPTION) {
        volver();
      }
    });

    JButton btnVolver = Componentes.botonAccion("Volver", Colores.GRIS_BTN, Colores.GRIS_BTN_HOVER);
    btnVolver.addActionListener(e -> volver());

    row.add(btnCancelar);
    row.add(btnVolver);
    return row;
  }

  private void seleccionarMetodo(String nombre) {
    this.setVisible(false);
    switch (nombre) {
      case "Efectivo" ->
        new RegistrarMetodoPagoEfectivo(this, frame, fachada, ventaActual, total, onVentaFinalizada);
      default -> {
        JOptionPane.showMessageDialog(frame,
          "El metodo '" + nombre + "' no esta disponible aun.",
          "No disponible", JOptionPane.INFORMATION_MESSAGE);
        this.setVisible(true);
      }
    }
  }

  void volver() {
    dispose();
    frame.setVisible(true);
  }

}
