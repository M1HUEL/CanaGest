package diseñadores.presentacion.frame;

import diseñadores.negocios.ventas.dominio.Ticket;
import diseñadores.presentacion.util.Colores;
import diseñadores.presentacion.util.Fuentes;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PantallaTicket extends JFrame {

  PantallaTicket(JFrame mainFrame, Ticket ticket,
    List<ItemCarrito> items,
    double recibido, double cambio,
    Runnable onConfirmado) {
    super("Ticket de Venta");
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());

    double total = ticket != null ? ticket.getTotal() : items.stream().mapToDouble(ItemCarrito::subtotal).sum();
    String folio = ticket != null ? ticket.getFolio() : generarFolio();

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

    String fechaStr = ticket != null ? ticket.getFechaFormateada() : generarFechaHoy();
    String horaStr = ticket != null ? ticket.getHoraFormateada() : "";
    String cajeroStr = ticket != null ? ticket.getCajero() : "Cajero";
    String tiendaStr = ticket != null ? ticket.getNombreTienda() : "La Canasta";
    String rfcStr = ticket != null ? ticket.getRfc() : "";
    String dirStr = ticket != null ? ticket.getDireccion() : "";
    String telStr = ticket != null ? ticket.getTelefono() : "";

    JPanel ticketPanel = buildTicket(items, total, recibido, cambio, folio,
      fechaStr, horaStr, cajeroStr, tiendaStr, rfcStr, dirStr, telStr);
    JScrollPane scroll = new JScrollPane(ticketPanel);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    JPanel centrado = new JPanel(new GridBagLayout());
    centrado.setOpaque(false);
    centrado.setBorder(BorderFactory.createEmptyBorder(20, 0, 6, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 340, 0, 340);
    centrado.add(scroll, gbc);
    root.add(centrado, BorderLayout.CENTER);

    JPanel barraInf = new JPanel(new GridLayout(1, 2, 12, 0));
    barraInf.setOpaque(false);
    barraInf.setBorder(BorderFactory.createEmptyBorder(8, 340, 16, 340));
    barraInf.setPreferredSize(new Dimension(0, 66));

    JButton btnDescargar = accionBtn("Descargar Ticket", Colores.AZUL, Colores.AZUL_HOVER);
    btnDescargar.addActionListener(e
      -> JOptionPane.showMessageDialog(this, "Función de descarga pendiente de implementar.",
        "Descargar", JOptionPane.INFORMATION_MESSAGE));

    JButton btnFinalizar = accionBtn("Finalizar Venta", Colores.VERDE, Colores.VERDE_HOVER);
    btnFinalizar.addActionListener(e -> {
      onConfirmado.run();
      dispose();
      mainFrame.setVisible(true);
    });

    barraInf.add(btnDescargar);
    barraInf.add(btnFinalizar);
    root.add(barraInf, BorderLayout.SOUTH);

    setContentPane(root);
    setVisible(true);
  }

  JPanel buildTicket(List<ItemCarrito> items, double total,
    double recibido, double cambio, String folio,
    String fechaStr, String horaStr, String cajero,
    String tienda, String rfc, String direccion, String telefono) {
    JPanel p = new JPanel(new BorderLayout()) {
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
    p.setOpaque(false);

    JPanel cab = new JPanel(new GridLayout(2, 1, 0, 6)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.AZUL);
        g.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 20, 20);
        super.paintComponent(g2d);
      }

    };
    cab.setOpaque(false);
    cab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    cab.setPreferredSize(new Dimension(0, 112));
    JLabel icoLbl = new JLabel("TICKET DE VENTA", SwingConstants.CENTER);
    icoLbl.setFont(Fuentes.b(20));
    icoLbl.setForeground(Colores.BLANCO);
    JLabel subLbl = new JLabel("Sistema de Punto de Venta", SwingConstants.CENTER);
    subLbl.setFont(Fuentes.r(12));
    subLbl.setForeground(new Color(180, 210, 255));
    cab.add(icoLbl);
    cab.add(subLbl);
    p.add(cab, BorderLayout.NORTH);

    JPanel body = new JPanel(new GridBagLayout());
    body.setOpaque(false);
    body.setBorder(BorderFactory.createEmptyBorder(20, 28, 24, 28));

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.anchor = GridBagConstraints.WEST;
    int row = 0;

    c.gridy = row++;
    c.insets = new Insets(0, 0, 2, 0);
    body.add(fullLabel(tienda, 17, Font.BOLD, Colores.TEXTO_OSCURO, SwingConstants.CENTER), c);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 1, 0);
    body.add(fullLabel("RFC: " + rfc, 11, Font.PLAIN, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 1, 0);
    body.add(fullLabel(direccion, 11, Font.PLAIN, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 0, 0);
    body.add(fullLabel(telefono, 11, Font.PLAIN, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    c.insets = new Insets(14, 0, 12, 0);
    body.add(sepLine(), c);

    JPanel metaRow = new JPanel(new BorderLayout());
    metaRow.setOpaque(false);
    JLabel lFecha = new JLabel("Fecha: " + fechaStr);
    lFecha.setFont(Fuentes.r(12));
    lFecha.setForeground(Colores.TEXTO_OSCURO);
    JLabel lHora = new JLabel("Hora: " + horaStr);
    lHora.setFont(Fuentes.r(12));
    lHora.setForeground(Colores.TEXTO_OSCURO);
    metaRow.add(lFecha, BorderLayout.WEST);
    metaRow.add(lHora, BorderLayout.EAST);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 6, 0);
    body.add(metaRow, c);

    JLabel lCajero = new JLabel("Cajero: " + cajero);
    lCajero.setFont(Fuentes.r(12));
    lCajero.setForeground(Colores.TEXTO_OSCURO);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 12, 0);
    body.add(lCajero, c);

    JPanel folioBox = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_FOLIO);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    folioBox.setOpaque(false);
    folioBox.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    JLabel fTxt = new JLabel("Folio de venta");
    fTxt.setFont(Fuentes.r(11));
    fTxt.setForeground(Colores.GRIS_TEXTO);
    JLabel fVal = new JLabel(folio);
    fVal.setFont(Fuentes.b(15));
    fVal.setForeground(Colores.TEXTO_OSCURO);
    folioBox.add(fTxt);
    folioBox.add(fVal);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 14, 0);
    body.add(folioBox, c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 14, 0);
    body.add(sepLine(), c);

    JLabel lblP = new JLabel("Productos");
    lblP.setFont(Fuentes.b(15));
    lblP.setForeground(Colores.TEXTO_OSCURO);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 10, 0);
    body.add(lblP, c);

    for (ItemCarrito it : items) {
      c.gridy = row++;
      c.insets = new Insets(0, 0, 8, 0);
      body.add(filaProducto(it), c);
    }

    c.gridy = row++;
    c.insets = new Insets(4, 0, 12, 0);
    body.add(sepLine(), c);

    double subtotal = total / 1.16;
    double iva = total - subtotal;
    c.gridy = row++;
    c.insets = new Insets(0, 0, 6, 0);
    body.add(filaResumen("Subtotal", subtotal, false), c);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 8, 0);
    body.add(filaResumen("IVA (16%)", iva, false), c);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 6, 0);
    body.add(filaResumen("TOTAL", total, true), c);

    if (recibido > 0) {
      c.gridy = row++;
      c.insets = new Insets(0, 0, 6, 0);
      body.add(filaResumen("Efectivo recibido", recibido, false), c);
      c.gridy = row++;
      c.insets = new Insets(0, 0, 8, 0);
      body.add(filaResumen("Cambio", cambio, false), c);
    }

    c.gridy = row++;
    c.insets = new Insets(0, 0, 16, 0);
    body.add(sepLine(), c);

    c.gridy = row++;
    c.insets = new Insets(0, 0, 3, 0);
    body.add(fullLabel("¡Gracias por su compra!", 12, Font.PLAIN, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 3, 0);
    body.add(fullLabel("Conserve este ticket para cualquier aclaración", 11, Font.PLAIN, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 6, 0);
    body.add(fullLabel("*** TICKET VÁLIDO ***", 10, Font.PLAIN, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);

    p.add(body, BorderLayout.CENTER);
    return p;
  }

  JPanel filaProducto(ItemCarrito it) {
    JPanel row = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_ITEM);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

    GridBagConstraints c = new GridBagConstraints();
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.weightx = 1;
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(0, 0, 0, 0);
    JLabel lNom = new JLabel(it.nombre);
    lNom.setFont(Fuentes.b(13));
    lNom.setForeground(Colores.TEXTO_OSCURO);
    row.add(lNom, c);

    c.gridx = 1;
    c.weightx = 0;
    c.anchor = GridBagConstraints.EAST;
    JLabel lSub = new JLabel(String.format("$%.2f", it.subtotal()));
    lSub.setFont(Fuentes.b(13));
    lSub.setForeground(Colores.TEXTO_OSCURO);
    row.add(lSub, c);

    int hash = Math.abs(it.nombre.hashCode()) % 9000 + 1000;
    JLabel lCod = new JLabel("PROD-8342-" + hash);
    lCod.setFont(Fuentes.r(10));
    lCod.setForeground(Colores.GRIS_TEXTO);
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 1;
    c.gridwidth = 2;
    c.insets = new Insets(3, 0, 0, 0);
    row.add(lCod, c);

    JLabel lCant = new JLabel(it.cantidad + " x $" + String.format("%.2f", it.precio));
    lCant.setFont(Fuentes.r(11));
    lCant.setForeground(Colores.GRIS_TEXTO);
    c.gridy = 2;
    c.insets = new Insets(2, 0, 0, 0);
    row.add(lCant, c);

    return row;
  }

  JPanel filaResumen(String etiqueta, double valor, boolean esTotal) {
    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    JLabel lE = new JLabel(etiqueta);
    lE.setFont(esTotal ? Fuentes.b(15) : Fuentes.r(13));
    lE.setForeground(esTotal ? Colores.TEXTO_OSCURO : Colores.GRIS_TEXTO);
    JLabel lV = new JLabel(String.format("$%.2f", valor));
    lV.setFont(esTotal ? Fuentes.b(15) : Fuentes.r(13));
    lV.setForeground(esTotal ? Colores.AZUL : Colores.GRIS_TEXTO);
    row.add(lE, BorderLayout.WEST);
    row.add(lV, BorderLayout.EAST);
    return row;
  }

  JLabel fullLabel(String txt, int size, int style, Color color, int halign) {
    JLabel l = new JLabel(txt, halign);
    l.setFont(style == Font.BOLD ? Fuentes.b(size) : Fuentes.r(size));
    l.setForeground(color);
    return l;
  }

  JPanel sepLine() {
    JPanel s = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        super.paintComponent(g2d);
        Graphics2D g = (Graphics2D) g2d;
        g.setColor(Colores.BORDE_GRIS);
        float[] dash = {4f, 4f};
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash, 0));
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
      }

    };
    s.setOpaque(false);
    s.setPreferredSize(new Dimension(0, 2));
    return s;
  }

  String generarFolio() {
    java.time.LocalDate hoy = java.time.LocalDate.now();
    int r = (int) (Math.random() * 9000) + 1000;
    return String.format("VTA-%d-%02d%02d-%d", hoy.getYear(), hoy.getMonthValue(), hoy.getDayOfMonth(), r);
  }

  String generarFechaHoy() {
    return java.time.LocalDate.now().format(
      java.time.format.DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
        new java.util.Locale("es", "MX")));
  }

  JButton accionBtn(String texto, Color base, Color hov) {
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
        g.setColor(ov ? hov : base);
        g.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(15));
    b.setHorizontalAlignment(SwingConstants.CENTER);
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
