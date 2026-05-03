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
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

public class RegistrarMetodoPagoTransferencia extends JFrame {

  public RegistrarMetodoPagoTransferencia(
    SeleccionarMetodoPago seleccionarMetodoPago, JFrame mainFrame,
    IVentas ventasFachada, IInventario inventarioFachada,
    IUsuarios usuariosFachada, IProveedores proveedoresFachada,
    VentaDTO ventaActual, BigDecimal total,
    Runnable onVentaFinalizada, UsuarioDTO usuarioActivo) {

    super("Registrar Transferencia Bancaria");
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

    root.add(crearTopBar(mainFrame, usuarioActivo, usuariosFachada,
      ventasFachada, inventarioFachada, proveedoresFachada), BorderLayout.NORTH);

    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(16, 40, 20, 40));

    JButton btnVolver = btnTexto("← Volver a métodos de pago");
    btnVolver.addActionListener(e -> {
      dispose();
      seleccionarMetodoPago.setVisible(true);
    });
    JPanel volverRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    volverRow.setOpaque(false);
    volverRow.add(btnVolver);
    cuerpo.add(volverRow, BorderLayout.NORTH);

    JPanel card = buildCard(total, mainFrame, onVentaFinalizada);
    JScrollPane scroll = new JScrollPane(card);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(12);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    cuerpo.add(crearCentrado(scroll, 240, 10), BorderLayout.CENTER);
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
    card.add(crearHeader("TR", "Transferencia Bancaria", Colores.NARANJA), c);

    // ── Caja total ────────────────────────────────────────────────────────
    c.gridy = row++;
    c.insets = new Insets(0, 0, 20, 0);
    card.add(crearCajaTotal(total), c);

    // ── Datos bancarios ───────────────────────────────────────────────────
    String referencia = generarReferencia();
    c.gridy = row++;
    c.insets = new Insets(0, 0, 20, 0);
    card.add(crearDatosBancarios(referencia), c);

    // ── Instrucciones ─────────────────────────────────────────────────────
    c.gridy = row++;
    c.insets = new Insets(0, 0, 20, 0);
    card.add(crearInstrucciones(), c);

    // ── Botón confirmar ───────────────────────────────────────────────────
    JButton btnConfirmar = crearBoton("Ya realicé la transferencia", Colores.NARANJA, Colores.NARANJA_HOVER);
    btnConfirmar.setPreferredSize(new Dimension(0, 54));
    btnConfirmar.addActionListener(e -> {
      JOptionPane.showMessageDialog(this,
        String.format("Transferencia registrada\n\nReferencia: %s\nTotal: $%,.2f\n\nSu pago será confirmado en breve.",
          referencia, total),
        "Transferencia registrada", JOptionPane.INFORMATION_MESSAGE);
      onVentaFinalizada.run();
      dispose();
      mainFrame.setVisible(true);
    });
    c.gridy = row++;
    c.insets = new Insets(0, 0, 0, 0);
    card.add(btnConfirmar, c);

    return card;
  }

  // ─────────────────────────────────────────────────────────────────────────
  //  Bloque de datos bancarios con botones "Copiar"
  // ─────────────────────────────────────────────────────────────────────────
  private JPanel crearDatosBancarios(String referencia) {
    JPanel box = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    box.setOpaque(false);
    box.setBorder(new EmptyBorder(16, 16, 16, 16));

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    int r = 0;

    JLabel tit = new JLabel("Datos para transferencia");
    tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
    tit.setForeground(Colores.TEXTO_OSCURO);
    c.gridy = r++;
    c.insets = new Insets(0, 0, 12, 0);
    box.add(tit, c);

    Object[][] datos = {
      {"Banco", "BBVA México", null, false},
      {"CLABE", "012180001234567890", "012180001234567890", true},
      {"Número de cuenta", "0123456789", "0123456789", true},
      {"Beneficiario", "La Canasta SA de CV", null, false},
      {"Referencia", referencia, referencia, true},};

    for (Object[] dato : datos) {
      c.gridy = r++;
      c.insets = new Insets(0, 0, 8, 0);
      boolean esReferencia = ((String) dato[0]).equals("Referencia");
      Color colorVal = esReferencia ? Colores.NARANJA : Colores.TEXTO_OSCURO;
      box.add(filaDato((String) dato[0], (String) dato[1],
        (String) dato[2], (boolean) dato[3], colorVal), c);
    }
    return box;
  }

  private JPanel filaDato(String etiqueta, String valor, String textoCopiar,
    boolean conCopia, Color colorVal) {
    JPanel row = new JPanel(new BorderLayout(10, 0)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        g.setColor(Colores.BORDE_GRIS);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(new EmptyBorder(12, 16, 12, 12));

    JPanel textos = new JPanel(new GridLayout(2, 1, 0, 4));
    textos.setOpaque(false);
    JLabel lEtq = new JLabel(etiqueta);
    lEtq.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    lEtq.setForeground(Colores.GRIS_TEXTO);
    JLabel lVal = new JLabel(valor);
    lVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lVal.setForeground(colorVal);
    textos.add(lEtq);
    textos.add(lVal);
    row.add(textos, BorderLayout.CENTER);

    if (conCopia) {
      JButton btnCopy = new JButton("Copiar") {
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
          g.setColor(ov ? Colores.NARANJA_HOVER : Colores.NARANJA);
          g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
          super.paintComponent(g2d);
        }

      };
      btnCopy.setForeground(Colores.BLANCO);
      btnCopy.setFont(new Font("Segoe UI", Font.BOLD, 12));
      btnCopy.setPreferredSize(new Dimension(64, 36));
      btnCopy.addActionListener(e -> {
        Toolkit.getDefaultToolkit().getSystemClipboard()
          .setContents(new StringSelection(textoCopiar), null);
        JOptionPane.showMessageDialog(this,
          "Copiado: " + textoCopiar, "Copiado", JOptionPane.INFORMATION_MESSAGE);
      });
      JPanel wrapper = new JPanel(new GridBagLayout());
      wrapper.setOpaque(false);
      wrapper.add(btnCopy);
      row.add(wrapper, BorderLayout.EAST);
    }
    return row;
  }

  // ─────────────────────────────────────────────────────────────────────────
  //  Bloque de instrucciones
  // ─────────────────────────────────────────────────────────────────────────
  private JPanel crearInstrucciones() {
    JPanel box = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.NARANJA_INST_BG);
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

    Object[][] pasos = {
      {"Realiza la transferencia desde tu banca en línea o app móvil", false},
      {"Usa la CLABE o número de cuenta proporcionados", false},
      {"No olvides incluir la referencia en tu transferencia", true},
      {"Espera la confirmación automática del pago", false},};
    for (int i = 0; i < pasos.length; i++) {
      JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
      fila.setOpaque(false);
      JLabel num = new JLabel((i + 1) + ".");
      num.setFont(new Font("Segoe UI", Font.BOLD, 13));
      num.setForeground(Colores.NARANJA);
      JLabel txt = new JLabel((String) pasos[i][0]);
      txt.setFont((boolean) pasos[i][1]
        ? new Font("Segoe UI", Font.BOLD, 13)
        : new Font("Segoe UI", Font.PLAIN, 13));
      txt.setForeground(Colores.TEXTO_OSCURO);
      fila.add(num);
      fila.add(txt);
      ci.gridy = i + 1;
      ci.insets = new Insets(0, 0, i < pasos.length - 1 ? 6 : 0, 0);
      box.add(fila, ci);
    }
    return box;
  }

  // ─────────────────────────────────────────────────────────────────────────
  //  Utilidades UI
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

  private JPanel crearCajaTotal(BigDecimal total) {
    JPanel caja = new JPanel(new GridLayout(2, 1, 0, 8)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.NARANJA_BG);
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
    lVal.setForeground(Colores.NARANJA);
    caja.add(lTxt);
    caja.add(lVal);
    return caja;
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

  private String generarReferencia() {
    LocalDate hoy = LocalDate.now();
    int r = new Random().nextInt(9000) + 1000;
    return String.format("TRANS-%d-%02d%02d-%d",
      hoy.getYear(), hoy.getMonthValue(), hoy.getDayOfMonth(), r);
  }

}
