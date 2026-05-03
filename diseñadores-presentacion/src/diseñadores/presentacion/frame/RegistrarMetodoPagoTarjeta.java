package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.VentaDTO;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;

public class RegistrarMetodoPagoTarjeta extends JFrame {

  public RegistrarMetodoPagoTarjeta(
    SeleccionarMetodoPago seleccionarMetodoPago, JFrame mainFrame,
    IVentas ventasFachada, IInventario inventarioFachada,
    IUsuarios usuariosFachada, IProveedores proveedoresFachada,
    VentaDTO ventaActual, BigDecimal total,
    Runnable onVentaFinalizada, UsuarioDTO usuarioActivo) {

    super("Registrar Pago con Tarjeta");
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());

    // ── Fondo amarillo ────────────────────────────────────────────────────
    JPanel root = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);

    // ── Top bar ───────────────────────────────────────────────────────────
    root.add(crearTopBar(mainFrame, usuarioActivo, usuariosFachada,
      ventasFachada, inventarioFachada, proveedoresFachada), BorderLayout.NORTH);

    // ── Cuerpo ────────────────────────────────────────────────────────────
    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));

    // Botón volver
    JButton btnVolver = btnTexto("← Volver a métodos de pago");
    btnVolver.addActionListener(e -> {
      dispose();
      seleccionarMetodoPago.setVisible(true);
    });
    JPanel volverRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    volverRow.setOpaque(false);
    volverRow.add(btnVolver);
    cuerpo.add(volverRow, BorderLayout.NORTH);

    // Tarjeta con scroll
    JPanel card = buildCard(total, mainFrame, onVentaFinalizada);
    JScrollPane scroll = new JScrollPane(card);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(12);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    JPanel centrado = crearCentrado(scroll, 240, 10);
    cuerpo.add(centrado, BorderLayout.CENTER);
    root.add(cuerpo, BorderLayout.CENTER);

    setContentPane(root);
    setVisible(true);
  }

  // ─────────────────────────────────────────────────────────────────────────
  //  Tarjeta de contenido
  // ─────────────────────────────────────────────────────────────────────────
  private JPanel buildCard(BigDecimal total, JFrame mainFrame, Runnable onVentaFinalizada) {
    JPanel card = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, 20, 20));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 20, 20));
        super.paintComponent(g2d);
      }

    };
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(28, 32, 32, 32));

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.anchor = GridBagConstraints.WEST;
    int row = 0;

    // ── Header ────────────────────────────────────────────────────────────
    c.gridy = row++;
    c.insets = new Insets(0, 0, 22, 0);
    card.add(crearHeader("TC", "Pago con Tarjeta", Colores.AZUL), c);

    // ── Caja total ────────────────────────────────────────────────────────
    c.gridy = row++;
    c.insets = new Insets(0, 0, 20, 0);
    card.add(crearCajaTotal(total, Colores.AZUL_CLARO, Colores.AZUL), c);

    // ── Instrucciones ─────────────────────────────────────────────────────
    String[] pasos = {
      "Inserte o deslice la tarjeta en el lector",
      "Ingrese el PIN cuando se le solicite",
      "Espere la confirmación de la transacción"
    };
    c.gridy = row++;
    c.insets = new Insets(0, 0, 20, 0);
    card.add(crearCajaInstrucciones(pasos, Colores.AZUL), c);

    // ── Número de tarjeta ─────────────────────────────────────────────────
    c.gridy = row++;
    c.insets = new Insets(0, 0, 6, 0);
    card.add(etiqueta("Número de tarjeta"), c);

    JTextField campoNum = campoTexto("**** **** **** ****");
    agregarFormateadorTarjeta(campoNum);
    c.gridy = row++;
    c.insets = new Insets(0, 0, 14, 0);
    card.add(campoNum, c);

    // ── Nombre del titular ────────────────────────────────────────────────
    c.gridy = row++;
    c.insets = new Insets(0, 0, 6, 0);
    card.add(etiqueta("Nombre del titular"), c);

    JTextField campoNom = campoTexto("Nombre como aparece en la tarjeta");
    c.gridy = row++;
    c.insets = new Insets(0, 0, 24, 0);
    card.add(campoNom, c);

    // ── Botón procesar ────────────────────────────────────────────────────
    JButton btnProcesar = crearBoton("Procesar Pago", Colores.AZUL, Colores.AZUL_HOVER);
    btnProcesar.setPreferredSize(new Dimension(0, 54));
    btnProcesar.addActionListener(e -> {
      String num = campoNum.getText().trim().replace(" ", "");
      String nom = campoNom.getText().trim();

      if (num.length() != 16 || !num.matches("[0-9]+")) {
        JOptionPane.showMessageDialog(this,
          "Ingrese un número de tarjeta válido (16 dígitos).",
          "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }
      if (nom.isEmpty() || nom.equals("Nombre como aparece en la tarjeta")) {
        JOptionPane.showMessageDialog(this,
          "Ingrese el nombre del titular.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      JOptionPane.showMessageDialog(this,
        String.format("Pago con tarjeta aprobado\n\nTitular: %s\nTarjeta: **** **** **** %s\nTotal: $%.2f",
          nom, num.substring(12), total),
        "Pago aprobado", JOptionPane.INFORMATION_MESSAGE);
      onVentaFinalizada.run();
      dispose();
      mainFrame.setVisible(true);
    });
    c.gridy = row++;
    c.insets = new Insets(0, 0, 0, 0);
    card.add(btnProcesar, c);

    return card;
  }

  // ─────────────────────────────────────────────────────────────────────────
  //  Formateador automático de número de tarjeta
  // ─────────────────────────────────────────────────────────────────────────
  private void agregarFormateadorTarjeta(JTextField campo) {
    campo.getDocument().addDocumentListener(new DocumentListener() {
      boolean editando = false;

      private void formatear() {
        if (editando) {
          return;
        }
        editando = true;
        String raw = campo.getText().replaceAll("[^0-9]", "");
        if (raw.length() > 16) {
          raw = raw.substring(0, 16);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
          if (i > 0 && i % 4 == 0) {
            sb.append(' ');
          }
          sb.append(raw.charAt(i));
        }
        campo.setText(sb.toString());
        editando = false;
      }

      public void insertUpdate(DocumentEvent e) {
        formatear();
      }

      public void removeUpdate(DocumentEvent e) {
        formatear();
      }

      public void changedUpdate(DocumentEvent e) {
      }

    });
  }

  // ─────────────────────────────────────────────────────────────────────────
  //  Métodos de utilidad UI
  // ─────────────────────────────────────────────────────────────────────────
  private JPanel crearTopBar(JFrame mainFrame, UsuarioDTO usuarioActivo,
    IUsuarios usuariosFachada, IVentas ventasFachada,
    IInventario inventarioFachada, IProveedores proveedoresFachada) {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));
    JButton btn = crearBoton("Menu Principal", Colores.AMARILLO_BTN, Colores.AMARILLO_BTN_HOVER);
    btn.setForeground(Colores.TEXTO_OSCURO);
    btn.setPreferredSize(new Dimension(160, 38));
    btn.addActionListener(e -> {
      dispose();
      new MenuPrincipal(usuarioActivo, usuariosFachada, ventasFachada,
        inventarioFachada, proveedoresFachada).setVisible(true);
    });
    bar.add(btn);
    return bar;
  }

  private JPanel crearCentrado(JComponent contenido, int margenH, int margenV) {
    JPanel p = new JPanel(new GridBagLayout());
    p.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(margenV, margenH, margenV, margenH);
    p.add(contenido, gbc);
    return p;
  }

  private JPanel crearHeader(String icono, String titulo, Color colorIcono) {
    JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    header.setOpaque(false);

    JPanel icoBox = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(colorIcono);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    icoBox.setOpaque(false);
    icoBox.setPreferredSize(new Dimension(52, 52));
    JLabel icoLbl = new JLabel(icono, SwingConstants.CENTER);
    icoLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
    icoLbl.setForeground(Colores.BLANCO);
    icoBox.add(icoLbl);

    JLabel titLbl = new JLabel(titulo);
    titLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titLbl.setForeground(Colores.TEXTO_OSCURO);

    header.add(icoBox);
    header.add(titLbl);
    return header;
  }

  private JPanel crearCajaTotal(BigDecimal total, Color bgColor, Color textColor) {
    JPanel caja = new JPanel(new GridLayout(2, 1, 0, 8)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bgColor);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    caja.setOpaque(false);
    caja.setBorder(new EmptyBorder(22, 20, 22, 20));
    JLabel lTxt = new JLabel("Total a pagar", SwingConstants.CENTER);
    lTxt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lTxt.setForeground(Colores.GRIS_TEXTO);
    JLabel lVal = new JLabel(String.format("$%,.2f", total), SwingConstants.CENTER);
    lVal.setFont(new Font("Segoe UI", Font.BOLD, 38));
    lVal.setForeground(textColor);
    caja.add(lTxt);
    caja.add(lVal);
    return caja;
  }

  private JPanel crearCajaInstrucciones(String[] pasos, Color colorNum) {
    JPanel box = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    box.setOpaque(false);
    box.setBorder(new EmptyBorder(16, 18, 16, 18));
    GridBagConstraints ci = new GridBagConstraints();
    ci.gridx = 0;
    ci.fill = GridBagConstraints.HORIZONTAL;
    ci.weightx = 1;

    JLabel tit = new JLabel("Instrucciones:");
    tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
    tit.setForeground(Colores.TEXTO_OSCURO);
    ci.gridy = 0;
    ci.insets = new Insets(0, 0, 10, 0);
    box.add(tit, ci);

    for (int i = 0; i < pasos.length; i++) {
      JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
      fila.setOpaque(false);
      JLabel num = new JLabel((i + 1) + ".");
      num.setFont(new Font("Segoe UI", Font.BOLD, 13));
      num.setForeground(colorNum);
      JLabel txt = new JLabel(pasos[i]);
      txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
      txt.setForeground(Colores.TEXTO_OSCURO);
      fila.add(num);
      fila.add(txt);
      ci.gridy = i + 1;
      ci.insets = new Insets(0, 0, i < pasos.length - 1 ? 6 : 0, 0);
      box.add(fila, ci);
    }
    return box;
  }

  private JLabel etiqueta(String texto) {
    JLabel l = new JLabel(texto);
    l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    l.setForeground(Colores.TEXTO_OSCURO);
    return l;
  }

  private JTextField campoTexto(String placeholder) {
    JTextField tf = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        g.setColor(Colores.BORDE_GRIS);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        super.paintComponent(g2d);
      }

    };
    tf.setOpaque(false);
    tf.setBorder(new EmptyBorder(12, 14, 12, 14));
    tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    tf.setPreferredSize(new Dimension(0, 50));
    tf.setText(placeholder);
    tf.setForeground(Colores.GRIS_TEXTO);
    tf.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        if (tf.getText().equals(placeholder)) {
          tf.setText("");
          tf.setForeground(Colores.TEXTO_OSCURO);
        }
      }

      public void focusLost(FocusEvent e) {
        if (tf.getText().isEmpty()) {
          tf.setText(placeholder);
          tf.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });
    return tf;
  }

  private JButton crearBoton(String texto, Color base, Color hover) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            ov = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            ov = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? hover : base);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(new Font("Segoe UI", Font.BOLD, 15));
    b.setHorizontalAlignment(SwingConstants.CENTER);
    return b;
  }

  private JButton btnTexto(String texto) {
    JButton b = new JButton(texto);
    b.setContentAreaFilled(false);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setForeground(Colores.TEXTO_OSCURO);
    b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return b;
  }

}
