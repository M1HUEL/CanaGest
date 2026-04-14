package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.PagoEfectivoDTO;
import diseñadores.negocios.dto.ResultadoPagoDTO;
import diseñadores.negocios.dto.TicketDTO;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.util.Colores;
import diseñadores.presentacion.util.Fuentes;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RegistrarMetodoPagoEfectivo extends JFrame {

  double recibido = 0.0;
  final double totalAPagar;
  final IVentas facade;
  final List<ItemCarrito> carritoItems;
  JLabel lblRecibido, lblCambio;
  JButton btnCompletar;

  RegistrarMetodoPagoEfectivo(SeleccionarMetodoPago pantallaPago, JFrame mainFrame,
    IVentas facade, double total, int productos,
    List<ItemCarrito> carritoItems, Runnable onConfirmado) {
    super("Pago en Efectivo");
    this.totalAPagar = total;
    this.facade = facade;
    this.carritoItems = carritoItems;
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());

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
    JButton btnCS = topBarBtn("Cerrar sesión");
    btnCS.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (op == JOptionPane.YES_OPTION) {
        System.exit(0);
      }
    });
    topBar.add(btnCS);
    root.add(topBar, BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 40, 20, 40));

    JButton btnVolver = new JButton("Volver a métodos de pago");
    btnVolver.setContentAreaFilled(false);
    btnVolver.setBorderPainted(false);
    btnVolver.setFocusPainted(false);
    btnVolver.setForeground(Colores.TEXTO_OSCURO);
    btnVolver.setFont(Fuentes.r(14));
    btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnVolver.addActionListener(e -> {
      dispose();
      pantallaPago.setVisible(true);
    });
    JPanel volverRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    volverRow.setOpaque(false);
    volverRow.add(btnVolver);
    cuerpo.add(volverRow, BorderLayout.NORTH);

    JPanel card = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, 20, 20));
        g.setColor(Colores.BLANCO);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 20, 20));
        super.paintComponent(g2d);
      }

    };
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setOpaque(false);
    card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

    JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    header.setOpaque(false);
    header.setAlignmentX(LEFT_ALIGNMENT);

    JPanel icoBox = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.VERDE);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    icoBox.setOpaque(false);
    icoBox.setPreferredSize(new Dimension(52, 52));
    JLabel icoLbl = new JLabel("$", SwingConstants.CENTER);
    icoLbl.setFont(Fuentes.b(22));
    icoLbl.setForeground(Colores.BLANCO);
    icoBox.add(icoLbl);

    JLabel tituloLbl = new JLabel("Pago en Efectivo");
    tituloLbl.setFont(Fuentes.b(24));
    tituloLbl.setForeground(Colores.TEXTO_OSCURO);

    header.add(icoBox);
    header.add(tituloLbl);
    card.add(header);
    card.add(Box.createVerticalStrut(22));

    JPanel cajasRow = new JPanel(new GridLayout(1, 3, 12, 0));
    cajasRow.setOpaque(false);
    cajasRow.setAlignmentX(LEFT_ALIGNMENT);
    cajasRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

    cajasRow.add(cajaInfo("Total a pagar", String.format("$%.2f", total), Colores.TEXTO_OSCURO, Colores.BLANCO, Colores.BORDE_GRIS));

    lblRecibido = new JLabel(String.format("$%.2f", recibido), SwingConstants.CENTER);
    lblRecibido.setFont(Fuentes.b(22));
    lblRecibido.setForeground(Colores.AZUL);
    cajasRow.add(cajaInfoConLabel("Recibido", lblRecibido, Colores.AZUL_CLARO, Colores.BORDE_GRIS));

    lblCambio = new JLabel("$0.00", SwingConstants.CENTER);
    lblCambio.setFont(Fuentes.b(22));
    lblCambio.setForeground(Colores.GRIS_TEXTO);
    cajasRow.add(cajaInfoConLabel("Cambio", lblCambio, Colores.BLANCO, Colores.BORDE_GRIS));

    card.add(cajasRow);
    card.add(Box.createVerticalStrut(22));

    JLabel lblDenom = new JLabel("Denominaciones rápidas");
    lblDenom.setFont(Fuentes.b(14));
    lblDenom.setForeground(Colores.TEXTO_OSCURO);
    lblDenom.setAlignmentX(LEFT_ALIGNMENT);
    card.add(lblDenom);
    card.add(Box.createVerticalStrut(10));

    JPanel gridDenom = new JPanel(new GridLayout(2, 3, 10, 10));
    gridDenom.setOpaque(false);
    gridDenom.setAlignmentX(LEFT_ALIGNMENT);
    gridDenom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
    for (int v : new int[]{20, 50, 100, 200, 500, 1000}) {
      gridDenom.add(botonDenom("$" + v, v));
    }
    card.add(gridDenom);
    card.add(Box.createVerticalStrut(18));

    JLabel lblCustom = new JLabel("Cantidad personalizada");
    lblCustom.setFont(Fuentes.b(14));
    lblCustom.setForeground(Colores.TEXTO_OSCURO);
    lblCustom.setAlignmentX(LEFT_ALIGNMENT);
    card.add(lblCustom);
    card.add(Box.createVerticalStrut(8));

    JPanel customRow = new JPanel(new BorderLayout(10, 0));
    customRow.setOpaque(false);
    customRow.setAlignmentX(LEFT_ALIGNMENT);
    customRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

    JTextField campoCustom = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_INPUT);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    campoCustom.setOpaque(false);
    campoCustom.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    campoCustom.setFont(Fuentes.r(14));
    campoCustom.setText("Ingrese cantidad");
    campoCustom.setForeground(Colores.GRIS_TEXTO);
    campoCustom.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent e) {
        if (campoCustom.getText().equals("Ingrese cantidad")) {
          campoCustom.setText("");
          campoCustom.setForeground(Colores.TEXTO_OSCURO);
        }
      }

      public void focusLost(java.awt.event.FocusEvent e) {
        if (campoCustom.getText().isEmpty()) {
          campoCustom.setText("Ingrese cantidad");
          campoCustom.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });

    JButton btnAgregar = new JButton("Agregar") {
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
        g.setColor(ov ? Colores.VERDE_HOVER : Colores.VERDE);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    btnAgregar.setForeground(Colores.BLANCO);
    btnAgregar.setFont(Fuentes.b(14));
    btnAgregar.setPreferredSize(new Dimension(110, 44));
    btnAgregar.addActionListener(e -> {
      try {
        String txt = campoCustom.getText().trim().replace(",", ".");
        double val = Double.parseDouble(txt);
        if (val > 0) {
          agregarRecibido(val);
          campoCustom.setText("");
        }
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });
    campoCustom.addActionListener(e -> btnAgregar.doClick());

    customRow.add(campoCustom, BorderLayout.CENTER);
    customRow.add(btnAgregar, BorderLayout.EAST);
    card.add(customRow);
    card.add(Box.createVerticalStrut(20));

    JPanel accionesRow = new JPanel(new GridLayout(1, 2, 12, 0));
    accionesRow.setOpaque(false);
    accionesRow.setAlignmentX(LEFT_ALIGNMENT);
    accionesRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

    JButton btnLimpiar = new JButton("Limpiar") {
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
        g.setColor(ov ? Colores.GRIS_BTN_HOVER : Colores.GRIS_BTN);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    btnLimpiar.setForeground(Colores.BLANCO);
    btnLimpiar.setFont(Fuentes.b(15));
    btnLimpiar.addActionListener(e -> {
      recibido = 0;
      actualizarUI();
    });

    btnCompletar = new JButton("Completar Pago") {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean habilitado = recibido >= totalAPagar;
        if (habilitado) {
          g.setColor(ov ? Colores.VERDE_HOVER : Colores.VERDE);
          setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
          g.setColor(Colores.GRIS_DISABLED);
          setCursor(Cursor.getDefaultCursor());
        }
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    btnCompletar.setForeground(Colores.BLANCO);
    btnCompletar.setFont(Fuentes.b(15));
    btnCompletar.addMouseListener(new java.awt.event.MouseAdapter() {
      boolean ov = false;

      public void mouseEntered(java.awt.event.MouseEvent e) {
        ov = true;
        btnCompletar.repaint();
      }

      public void mouseExited(java.awt.event.MouseEvent e) {
        ov = false;
        btnCompletar.repaint();
      }

      public void mouseClicked(java.awt.event.MouseEvent e) {
        if (recibido < totalAPagar) {
          return;
        }

        PagoEfectivoDTO pagoEfectivoDto = new PagoEfectivoDTO(recibido);

        ResultadoPagoDTO resultadoCambio = facade.procesarPagoEfectivo(pagoEfectivoDto);
        double cambio = resultadoCambio.getCambio();
        if (cambio < 0) {
          JOptionPane.showMessageDialog(RegistrarMetodoPagoEfectivo.this,
            "El monto recibido es insuficiente para cubrir el total.",
            "Monto insuficiente", JOptionPane.WARNING_MESSAGE);
          return;
        }

        facade.procesarFinalizarVenta();

        TicketDTO ticket = facade.generarTicket();

        RegistrarMetodoPagoEfectivo.this.setVisible(false);
        new PantallaTicket(mainFrame, ticket, carritoItems, recibido, cambio, onConfirmado);
      }

    });

    accionesRow.add(btnLimpiar);
    accionesRow.add(btnCompletar);
    card.add(accionesRow);

    JPanel centrado = new JPanel(new GridBagLayout());
    centrado.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(12, 240, 12, 240);
    centrado.add(card, gbc);

    cuerpo.add(centrado, BorderLayout.CENTER);
    root.add(cuerpo, BorderLayout.CENTER);
    setContentPane(root);
    setVisible(true);
  }

  void agregarRecibido(double val) {
    recibido += val;
    actualizarUI();
  }

  void actualizarUI() {
    lblRecibido.setText(String.format("$%.2f", recibido));
    double cambio = facade.calcularCambio(recibido);
    lblCambio.setText(String.format("$%.2f", Math.max(cambio, 0)));
    lblCambio.setForeground(cambio >= 0 ? Colores.VERDE : Colores.GRIS_TEXTO);
    btnCompletar.repaint();
  }

  JPanel cajaInfo(String etiqueta, String valor, Color colorVal, Color bg, Color borde) {
    JPanel p = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        g.setColor(borde);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    JLabel lEtq = new JLabel(etiqueta, SwingConstants.CENTER);
    lEtq.setFont(Fuentes.r(12));
    lEtq.setForeground(Colores.GRIS_TEXTO);
    JLabel lVal = new JLabel(valor, SwingConstants.CENTER);
    lVal.setFont(Fuentes.b(22));
    lVal.setForeground(colorVal);
    p.add(lEtq);
    p.add(lVal);
    return p;
  }

  JPanel cajaInfoConLabel(String etiqueta, JLabel valorLabel, Color bg, Color borde) {
    JPanel p = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        g.setColor(borde);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    JLabel lEtq = new JLabel(etiqueta, SwingConstants.CENTER);
    lEtq.setFont(Fuentes.r(12));
    lEtq.setForeground(Colores.GRIS_TEXTO);
    p.add(lEtq);
    p.add(valorLabel);
    return p;
  }

  JButton botonDenom(String texto, int valor) {
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
        g.setColor(ov ? Colores.VERDE_HOVER : Colores.VERDE);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(16));
    b.addActionListener(e -> agregarRecibido(valor));
    return b;
  }

  JButton topBarBtn(String texto) {
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
