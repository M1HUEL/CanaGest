package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ItemConteoDTO;
import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ConsolidarInventario extends JFrame {

  private final JFrame menuOrigen;
  private final VentasControl control;
  private final List<ItemConteoDTO> items = new ArrayList<>();
  private JPanel panelTabla;
  private JLabel lblAuditados, lblPendientes, lblDiferencias;

  public ConsolidarInventario(JFrame menuOrigen, VentasControl control) {
    this.menuOrigen = menuOrigen;
    this.control = control;

    initFrame();
    cargarItemsDesdeSistema();
    initComponents();
  }

  private void initFrame() {
    setTitle("La Canasta - Consolidar Inventario");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        regresarAlMenu();
      }

    });
  }

  private void cargarItemsDesdeSistema() {
    items.clear();
    List<ProductoDTO> productos = control.obtenerProductosInventario();
    for (ProductoDTO p : productos) {
      // Inicializamos el stock físico igual al del sistema por defecto
      items.add(new ItemConteoDTO(p.getCodigo(), p.getNombre(), p.getStock(), p.getStock()));
    }
  }

  private void initComponents() {
    JPanel root = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);
    root.add(buildTopBar(), BorderLayout.NORTH);
    root.add(buildContenido(), BorderLayout.CENTER);
    setContentPane(root);
  }

  private JPanel buildTopBar() {
    JPanel bar = new JPanel(new BorderLayout());
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS),
      new EmptyBorder(0, 24, 0, 24)));
    bar.setPreferredSize(new Dimension(0, 66));

    JButton btnMenu = btnAmarillo("Menú Principal");
    btnMenu.addActionListener(e -> regresarAlMenu());

    JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
    der.setOpaque(false);
    der.add(btnMenu);

    bar.add(der, BorderLayout.EAST);
    return bar;
  }

  private void regresarAlMenu() {
    dispose();
    menuOrigen.setVisible(true);
  }

  private JPanel buildContenido() {
    JPanel contenido = new JPanel(new BorderLayout());
    contenido.setOpaque(false);
    contenido.setBorder(new EmptyBorder(28, 32, 28, 32));

    JPanel topSection = new JPanel(new BorderLayout());
    topSection.setOpaque(false);

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.setBorder(new EmptyBorder(0, 0, 20, 0));

    JPanel tituloCol = new JPanel();
    tituloCol.setLayout(new BoxLayout(tituloCol, BoxLayout.Y_AXIS));
    tituloCol.setOpaque(false);

    JLabel lblTitulo = new JLabel("Consolidar Inventario");
    lblTitulo.setFont(Fuentes.b(26));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblDesc = new JLabel("Compara el stock del sistema con el físico y realiza ajustes");
    lblDesc.setFont(Fuentes.r(14));
    lblDesc.setForeground(Colores.GRIS_TEXTO);

    tituloCol.add(lblTitulo);
    tituloCol.add(Box.createVerticalStrut(4));
    tituloCol.add(lblDesc);
    header.add(tituloCol, BorderLayout.WEST);

    JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
    statsRow.setOpaque(false);
    statsRow.setBorder(new EmptyBorder(0, 0, 18, 0));

    lblAuditados = new JLabel();
    lblPendientes = new JLabel();
    lblDiferencias = new JLabel();

    lblAuditados.setFont(Fuentes.b(32));
    lblAuditados.setForeground(Colores.AZUL);
    lblPendientes.setFont(Fuentes.b(32));
    lblPendientes.setForeground(new Color(217, 119, 6));
    lblDiferencias.setFont(Fuentes.b(32));
    lblDiferencias.setForeground(Colores.ROJO);

    actualizarStats();

    statsRow.add(cardStat("Productos Auditados", lblAuditados));
    statsRow.add(cardStat("Ajustes Pendientes", lblPendientes));
    statsRow.add(cardStat("Diferencias Totales", lblDiferencias));

    JButton btnNuevoConteo = btnAzul("Iniciar Nuevo Conteo");
    btnNuevoConteo.addActionListener(e -> iniciarNuevoConteo());

    JPanel accionesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    accionesRow.setOpaque(false);
    accionesRow.setBorder(new EmptyBorder(0, 0, 16, 0));
    accionesRow.add(btnNuevoConteo);

    topSection.add(header, BorderLayout.NORTH);
    topSection.add(statsRow, BorderLayout.CENTER);
    topSection.add(accionesRow, BorderLayout.SOUTH);

    contenido.add(topSection, BorderLayout.NORTH);
    contenido.add(buildTablaWrap(), BorderLayout.CENTER);

    return contenido;
  }

  private JPanel buildTablaWrap() {
    JPanel wrapTabla = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, 14, 14));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 14, 14));
        super.paintComponent(g2d);
      }

    };
    wrapTabla.setOpaque(false);

    JPanel headerTabla = new JPanel(new GridLayout(1, 7));
    headerTabla.setOpaque(false);
    headerTabla.setBorder(new EmptyBorder(16, 24, 16, 24));
    String[] cols = {"Código", "Producto", "Stock Sistema", "Stock Físico", "Diferencia", "Estado", "Acciones"};
    for (String col : cols) {
      JLabel lbl = new JLabel(col);
      lbl.setFont(Fuentes.b(13));
      lbl.setForeground(Colores.TEXTO_OSCURO);
      headerTabla.add(lbl);
    }

    JPanel sepHeader = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.BORDE_GRIS);
        g.drawLine(0, 0, getWidth(), 0);
      }

    };
    sepHeader.setOpaque(false);
    sepHeader.setPreferredSize(new Dimension(0, 1));

    JPanel headerWrap = new JPanel(new BorderLayout());
    headerWrap.setOpaque(false);
    headerWrap.add(headerTabla, BorderLayout.CENTER);
    headerWrap.add(sepHeader, BorderLayout.SOUTH);

    panelTabla = new JPanel();
    panelTabla.setLayout(new BoxLayout(panelTabla, BoxLayout.Y_AXIS));
    panelTabla.setOpaque(false);
    construirTabla();

    JScrollPane scroll = new JScrollPane(panelTabla);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    wrapTabla.add(headerWrap, BorderLayout.NORTH);
    wrapTabla.add(scroll, BorderLayout.CENTER);

    return wrapTabla;
  }

  private void construirTabla() {
    panelTabla.removeAll();
    for (ItemConteoDTO item : items) {
      panelTabla.add(filaConteo(item));
      panelTabla.add(crearSeparador());
    }
    panelTabla.revalidate();
    panelTabla.repaint();
  }

  private JPanel filaConteo(ItemConteoDTO item) {
    JPanel fila = new JPanel(new GridLayout(1, 7));
    fila.setOpaque(false);
    fila.setBorder(new EmptyBorder(16, 24, 16, 24));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

    fila.add(new JLabel(item.getCodigo()) {
      {
        setFont(Fuentes.r(13));
        setForeground(Colores.GRIS_TEXTO);
      }

    });

    fila.add(new JLabel(item.getNombre()) {
      {
        setFont(Fuentes.r(13));
        setForeground(Colores.TEXTO_OSCURO);
      }

    });

    fila.add(new JLabel(String.valueOf(item.getStockSistema())) {
      {
        setFont(Fuentes.b(14));
      }

    });

    fila.add(new JLabel(String.valueOf(item.getStockFisico())) {
      {
        setFont(Fuentes.b(14));
      }

    });

    int diff = item.getDiferencia();
    String diffText = diff == 0 ? "0" : (diff > 0 ? "+" + diff : String.valueOf(diff));
    Color diffFg = diff == 0 ? new Color(21, 128, 61) : (diff > 0 ? new Color(217, 119, 6) : new Color(185, 28, 28));
    Color diffBg = diff == 0 ? new Color(220, 252, 231) : (diff > 0 ? new Color(254, 243, 199) : new Color(254, 226, 226));

    JLabel lblDiff = new JLabel(diffText, SwingConstants.CENTER);
    lblDiff.setFont(Fuentes.b(13));
    lblDiff.setForeground(diffFg);
    lblDiff.setOpaque(true);
    lblDiff.setBackground(diffBg);
    lblDiff.setBorder(new EmptyBorder(4, 12, 4, 12));

    JPanel wrapDiff = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapDiff.setOpaque(false);
    wrapDiff.add(lblDiff);
    fila.add(wrapDiff);

    boolean verificado = item.isVerificado();
    Color estadoFg = verificado ? new Color(21, 128, 61) : new Color(161, 110, 0);
    Color estadoBg = verificado ? new Color(220, 252, 231) : new Color(254, 243, 199);

    JLabel lblEstado = new JLabel(item.getEstado(), SwingConstants.CENTER);
    lblEstado.setFont(Fuentes.b(11));
    lblEstado.setForeground(estadoFg);
    lblEstado.setOpaque(true);
    lblEstado.setBackground(estadoBg);
    lblEstado.setBorder(new EmptyBorder(4, 10, 4, 10));

    JPanel wrapEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapEstado.setOpaque(false);
    wrapEstado.add(lblEstado);
    fila.add(wrapEstado);

    JPanel wrapAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapAccion.setOpaque(false);
    if (verificado) {
      JLabel sinDiff = new JLabel("Verificado");
      sinDiff.setFont(Fuentes.r(13));
      sinDiff.setForeground(new Color(21, 128, 61));
      wrapAccion.add(sinDiff);
    } else {
      JButton btnAjustar = btnAzulTabla("Ajustar");
      btnAjustar.addActionListener(e -> abrirDialogoAjuste(item));
      wrapAccion.add(btnAjustar);
    }
    fila.add(wrapAccion);

    return fila;
  }

  private void abrirDialogoAjuste(ItemConteoDTO item) {
    JDialog dlg = new JDialog(this, "Ajustar Inventario", true);
    dlg.setSize(440, 350);
    dlg.setLocationRelativeTo(this);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Ajustar: " + item.getNombre());
    titulo.setFont(Fuentes.b(18));
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(18));

    JPanel infoRow = new JPanel(new GridLayout(1, 3, 12, 0));
    infoRow.setOpaque(false);
    infoRow.add(miniCard("Sistema", String.valueOf(item.getStockSistema()), Colores.AZUL));
    infoRow.add(miniCard("Físico", String.valueOf(item.getStockFisico()), new Color(217, 119, 6)));

    int d = item.getDiferencia();
    infoRow.add(miniCard("Diff", (d > 0 ? "+" + d : String.valueOf(d)), d < 0 ? Colores.ROJO : new Color(21, 128, 61)));
    panel.add(infoRow);
    panel.add(Box.createVerticalStrut(18));

    JTextField campoFisico = new JTextField(String.valueOf(item.getStockFisico()));
    campoFisico.setFont(Fuentes.r(14));
    campoFisico.setBorder(BorderFactory.createCompoundBorder(new Bordes(Colores.BORDE_GRIS, 1, 8), new EmptyBorder(8, 12, 8, 12)));
    campoFisico.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

    panel.add(new JLabel("Corregir Stock Físico Actual:"));
    panel.add(Box.createVerticalStrut(6));
    panel.add(campoFisico);
    panel.add(Box.createVerticalStrut(18));

    JButton btnConfirmar = btnAzulDialog("Confirmar Ajuste");
    btnConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    btnConfirmar.addActionListener(e -> {
      try {
        int nuevoFisico = Integer.parseInt(campoFisico.getText().trim());
        control.ajustarStock(item.getCodigo(), nuevoFisico);
        item.setStockFisico(nuevoFisico); // El DTO actualiza su estado de verificado automáticamente

        actualizarStats();
        construirTabla();
        dlg.dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(dlg, "Ingrese un número válido.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });
    panel.add(btnConfirmar);

    dlg.setContentPane(panel);
    dlg.setVisible(true);
  }

  private void iniciarNuevoConteo() {
    JDialog dlg = new JDialog(this, "Iniciar Nuevo Conteo", true);
    dlg.setSize(500, 500);
    dlg.setLocationRelativeTo(this);

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
      tf.setBorder(BorderFactory.createCompoundBorder(new Bordes(Colores.BORDE_GRIS, 1, 6), new EmptyBorder(4, 8, 4, 8)));
      campos[i] = tf;

      filaForm.add(lbl, BorderLayout.WEST);
      filaForm.add(tf, BorderLayout.CENTER);
      listPanel.add(filaForm);
      listPanel.add(Box.createVerticalStrut(10));
    }

    JScrollPane scroll = new JScrollPane(listPanel);
    scroll.setBorder(BorderFactory.createEmptyBorder());

    JButton btnGuardar = btnAzulDialog("Guardar Todo el Conteo");
    btnGuardar.addActionListener(e -> {
      try {
        for (int i = 0; i < items.size(); i++) {
          int nf = Integer.parseInt(campos[i].getText().trim());
          ItemConteoDTO item = items.get(i);
          control.ajustarStock(item.getCodigo(), nf);
          item.setStockFisico(nf);
        }
        actualizarStats();
        construirTabla();
        dlg.dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(dlg, "Hay valores inválidos en el formulario.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });

    JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
    south.setBackground(Colores.BLANCO);
    south.setBorder(new EmptyBorder(10, 0, 15, 0));
    south.add(btnGuardar);

    mainPanel.add(scroll, BorderLayout.CENTER);
    mainPanel.add(south, BorderLayout.SOUTH);

    dlg.setContentPane(mainPanel);
    dlg.setVisible(true);
  }

  private void actualizarStats() {
    lblAuditados.setText(String.valueOf(items.size()));
    lblPendientes.setText(String.valueOf(items.stream().filter(i -> !i.isVerificado()).count()));
    lblDiferencias.setText(String.valueOf(items.stream().mapToInt(i -> Math.abs(i.getDiferencia())).sum()));
  }

  private JPanel cardStat(String etiqueta, JLabel valorLabel) {
    JPanel card = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, 14, 14));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 14, 14));
        super.paintComponent(g2d);
      }

    };
    card.setOpaque(false);
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(18, 22, 18, 22));

    JLabel lblEtq = new JLabel(etiqueta);
    lblEtq.setFont(Fuentes.r(13));
    lblEtq.setForeground(Colores.GRIS_TEXTO);
    lblEtq.setAlignmentX(LEFT_ALIGNMENT);
    valorLabel.setAlignmentX(LEFT_ALIGNMENT);

    card.add(lblEtq);
    card.add(Box.createVerticalStrut(6));
    card.add(valorLabel);
    return card;
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
    JLabel lV = new JLabel(valor);
    lV.setFont(Fuentes.b(18));
    lV.setForeground(colorVal);

    p.add(lE);
    p.add(Box.createVerticalStrut(3));
    p.add(lV);
    return p;
  }

  private JPanel crearSeparador() {
    JPanel sep = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.BORDE_GRIS);
        g.drawLine(0, 0, getWidth(), 0);
      }

    };
    sep.setOpaque(false);
    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    sep.setPreferredSize(new Dimension(0, 1));
    return sep;
  }

  private JButton btnAmarillo(String texto) {
    JButton b = createStyledButton(texto, new Color(255, 200, 0), new Color(240, 180, 0), new Color(30, 30, 30));
    b.setPreferredSize(new Dimension(180, 42));
    return b;
  }

  private JButton btnAzul(String texto) {
    JButton b = createStyledButton(texto, Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO);
    b.setPreferredSize(new Dimension(200, 42));
    return b;
  }

  private JButton btnAzulTabla(String texto) {
    JButton b = createStyledButton(texto, Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO);
    b.setPreferredSize(new Dimension(88, 30));
    b.setFont(Fuentes.b(11));
    return b;
  }

  private JButton btnAzulDialog(String texto) {
    return createStyledButton(texto, Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO);
  }

  private JButton createStyledButton(String texto, Color bg, Color hover, Color fg) {
    JButton b = new JButton(texto) {
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
        g.setColor(over ? hover : bg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(fg);
    b.setFont(Fuentes.b(14));
    return b;
  }

}
