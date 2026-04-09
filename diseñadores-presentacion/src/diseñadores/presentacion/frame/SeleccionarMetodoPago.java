package diseñadores.presentacion.frame;

import diseñadores.negocios.ventas.IVentas;
import diseñadores.negocios.ventas.dominio.Ticket;
import diseñadores.presentacion.util.Colores;
import diseñadores.presentacion.util.Fuentes;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SeleccionarMetodoPago extends JFrame {

  final JFrame owner;
  final IVentas facade;
  final List<ItemCarrito> carritoItems;

  SeleccionarMetodoPago(JFrame owner, IVentas facade, double total, int productos,
    List<ItemCarrito> carritoItems, Runnable onVentaConfirmada) {
    super("Método de pago");
    this.owner = owner;
    this.facade = facade;
    this.carritoItems = carritoItems;
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(owner.getWidth(), owner.getHeight());
    setLocation(owner.getLocation());

    JPanel root = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);

    JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    topBar.setBackground(Colores.BLANCO);
    topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));
    JButton btnCerrarSesion = botonTopBar("Cerrar sesión");
    btnCerrarSesion.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (op == JOptionPane.YES_OPTION) {
        System.exit(0);
      }
    });
    topBar.add(btnCerrarSesion);
    root.add(topBar, BorderLayout.NORTH);

    JPanel card = new JPanel(new BorderLayout(0, 20)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(4, 5, getWidth() - 5, getHeight() - 4, 22, 22));
        g.setColor(Colores.BLANCO);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 22, 22));
        super.paintComponent(g2d);
      }

    };
    card.setOpaque(false);
    card.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

    JLabel titulo = new JLabel("Seleccione el método de pago", SwingConstants.CENTER);
    titulo.setFont(Fuentes.b(22));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    card.add(titulo, BorderLayout.NORTH);

    JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
    grid.setOpaque(false);
    grid.add(botonMetodo("Efectivo", Colores.VERDE_METODO, Colores.VERDE_METODO_H, total, productos, onVentaConfirmada));
    grid.add(botonMetodo("Tarjeta", Colores.AZUL, Colores.AZUL_HOVER, total, productos, onVentaConfirmada));
    grid.add(botonMetodo("CoDi", Colores.MORADO, Colores.MORADO_HOVER, total, productos, onVentaConfirmada));
    grid.add(botonMetodo("Transferencia", Colores.NARANJA, Colores.NARANJA_HOVER, total, productos, onVentaConfirmada));
    card.add(grid, BorderLayout.CENTER);

    JPanel botones = new JPanel(new GridLayout(1, 2, 16, 0));
    botones.setOpaque(false);
    botones.setPreferredSize(new Dimension(0, 60));

    JButton btnCancelar = botonAccion("Cancelar", Colores.ROJO, Colores.ROJO_HOVER);
    btnCancelar.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this,
        "¿Cancelar la venta?", "Cancelar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (op == JOptionPane.YES_OPTION) {
        volver();
      }
    });

    JButton btnVolver = botonAccion("Volver", Colores.GRIS_BTN, Colores.GRIS_BTN_HOVER);
    btnVolver.addActionListener(e -> volver());

    botones.add(btnCancelar);
    botones.add(btnVolver);
    card.add(botones, BorderLayout.SOUTH);

    JPanel centro = new JPanel(new GridBagLayout());
    centro.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(30, 280, 30, 280);
    centro.add(card, gbc);

    root.add(centro, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  void volver() {
    dispose();
    owner.setVisible(true);
  }

  JPanel botonMetodo(String nombre, Color base, Color hover,
    double total, int productos, Runnable onConfirmado) {
    JPanel btn = new JPanel(new GridLayout(2, 1, 0, 8)) {
      boolean ov = false;

      {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
          public void mouseEntered(java.awt.event.MouseEvent e) {
            ov = true;
            repaint();
          }

          public void mouseExited(java.awt.event.MouseEvent e) {
            ov = false;
            repaint();
          }

          public void mouseClicked(java.awt.event.MouseEvent e) {
            if (nombre.equals("Efectivo")) {
              SeleccionarMetodoPago.this.setVisible(false);
              new RegistrarMetodoPagoEfectivo(SeleccionarMetodoPago.this, owner, facade,
                total, productos, carritoItems, onConfirmado);
            } else if (nombre.equals("Tarjeta")) {
              SeleccionarMetodoPago.this.setVisible(false);
//              new RegistrarMetodoPagoTarjeta(SeleccionarMetodoPago.this, owner, facade,
//                total, productos, carritoItems, onConfirmado);
            } else if (nombre.equals("Transferencia")) {
              SeleccionarMetodoPago.this.setVisible(false);
//              new RegistrarMetodoPagoTransferencia(SeleccionarMetodoPago.this, owner, facade,
//                total, carritoItems, onConfirmado);
            } else {
              facade.procesarFinalizarVenta();
              Ticket ticket = facade.generarTicket();
              String folio = ticket != null ? ticket.getFolio() : "N/A";
              JOptionPane.showMessageDialog(SeleccionarMetodoPago.this,
                String.format("Venta procesada via %s\nFolio: %s\nTotal: $%.2f",
                  nombre, folio, total), "Venta confirmada", JOptionPane.INFORMATION_MESSAGE);
              onConfirmado.run();
              dispose();
              owner.setVisible(true);
            }
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
    btn.setBorder(BorderFactory.createEmptyBorder(28, 20, 28, 20));

    JLabel lblNom = new JLabel(nombre, SwingConstants.CENTER);
    lblNom.setFont(Fuentes.b(20));
    lblNom.setForeground(Colores.BLANCO);

    btn.add(new JLabel("", SwingConstants.CENTER));
    btn.add(lblNom);
    return btn;
  }

  JButton botonAccion(String texto, Color base, Color hoverColor) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
          public void mouseEntered(java.awt.event.MouseEvent e) {
            ov = true;
            repaint();
          }

          public void mouseExited(java.awt.event.MouseEvent e) {
            ov = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? hoverColor : base);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(15));
    b.setHorizontalAlignment(SwingConstants.CENTER);
    return b;
  }

  JButton botonTopBar(String texto) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
          public void mouseEntered(java.awt.event.MouseEvent e) {
            ov = true;
            repaint();
          }

          public void mouseExited(java.awt.event.MouseEvent e) {
            ov = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(13));
    b.setPreferredSize(new Dimension(160, 38));
    return b;
  }

}
