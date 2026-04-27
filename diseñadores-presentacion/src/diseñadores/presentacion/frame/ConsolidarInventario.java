package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.inventario.InventarioFacade;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ConsolidarInventario extends JFrame {

  static class ItemConteo {

    String codigo, nombre;
    int stockSistema, stockFisico;
    boolean verificado;

    ItemConteo(String codigo, String nombre, int stockSistema, int stockFisico) {
      this.codigo = codigo;
      this.nombre = nombre;
      this.stockSistema = stockSistema;
      this.stockFisico = stockFisico;
      this.verificado = stockSistema == stockFisico;
    }

    int diferencia() {
      return stockFisico - stockSistema;
    }

    String estado() {
      return verificado ? "Verificado" : "Pendiente";
    }

  }

  private final JFrame menuOrigen;
  private final InventarioFacade facade;
  private final List<ItemConteo> items = new ArrayList<>();
  private JPanel panelTabla;
  private JLabel lblAuditados, lblPendientes, lblDiferencias;

  public ConsolidarInventario(JFrame menuOrigen) {
    this.menuOrigen = menuOrigen;
    this.facade = new InventarioFacade();
    setTitle("La Canasta - Consolidar Inventario");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    cargarItemsDesdeSistema();

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

  private void cargarItemsDesdeSistema() {
    List<ProductoDTO> productos = facade.obtenerTodos();
    for (ProductoDTO p : productos) {
      items.add(new ItemConteo(p.getCodigo(), p.getNombre(), p.getStock(), p.getStock()));
    }
  }

  private JPanel buildTopBar() {
    JPanel bar = new JPanel(new BorderLayout());
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS),
      new EmptyBorder(0, 24, 0, 24)));
    bar.setPreferredSize(new Dimension(0, 66));

    JPanel izq = new JPanel(new GridLayout(2, 1, 0, 2));
    izq.setOpaque(false);
    JLabel n = new JLabel("La Canasta");
    n.setFont(Fuentes.b(16));
    n.setForeground(Colores.TEXTO_OSCURO);
    JLabel s = new JLabel("Punto de Venta");
    s.setFont(Fuentes.r(12));
    s.setForeground(Colores.GRIS_TEXTO);
    izq.add(n);
    izq.add(s);

    JButton btnMenu = btnAmarillo("← Menú Principal");
    btnMenu.addActionListener(e -> {
      dispose();
      menuOrigen.setVisible(true);
    });

    JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
    der.setOpaque(false);
    der.add(btnMenu);

    bar.add(izq, BorderLayout.WEST);
    bar.add(der, BorderLayout.EAST);
    return bar;
  }

  private JPanel buildContenido() {
    JPanel contenido = new JPanel(new BorderLayout());
    contenido.setOpaque(false);
    contenido.setBorder(new EmptyBorder(28, 32, 28, 32));

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

    lblAuditados = new JLabel(String.valueOf(items.size()));
    lblPendientes = new JLabel(String.valueOf(contarPendientes()));
    lblDiferencias = new JLabel(String.valueOf(sumaDiferencias()));

    lblAuditados.setFont(Fuentes.b(32));
    lblAuditados.setForeground(Colores.AZUL);
    lblPendientes.setFont(Fuentes.b(32));
    lblPendientes.setForeground(new Color(217, 119, 6));
    lblDiferencias.setFont(Fuentes.b(32));
    lblDiferencias.setForeground(Colores.ROJO);

    statsRow.add(cardStat("Productos Auditados", lblAuditados));
    statsRow.add(cardStat("Ajustes Pendientes", lblPendientes));
    statsRow.add(cardStat("Diferencias Totales", lblDiferencias));

    JButton btnNuevoConteo = btnAzul("Iniciar Nuevo Conteo");
    btnNuevoConteo.addActionListener(e -> iniciarNuevoConteo());

    JPanel accionesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    accionesRow.setOpaque(false);
    accionesRow.setBorder(new EmptyBorder(0, 0, 16, 0));
    accionesRow.add(btnNuevoConteo);

    JPanel topSection = new JPanel(new BorderLayout(0, 0));
    topSection.setOpaque(false);
    topSection.add(header, BorderLayout.NORTH);
    topSection.add(statsRow, BorderLayout.CENTER);
    topSection.add(accionesRow, BorderLayout.SOUTH);

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
    sepHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

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

    contenido.add(topSection, BorderLayout.NORTH);
    contenido.add(wrapTabla, BorderLayout.CENTER);
    return contenido;
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
    card.setPreferredSize(new Dimension(0, 90));

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

  private void construirTabla() {
    panelTabla.removeAll();
    for (ItemConteo item : items) {
      panelTabla.add(filaConteo(item));
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
      panelTabla.add(sep);
    }
    panelTabla.revalidate();
    panelTabla.repaint();
  }

  private JPanel filaConteo(ItemConteo item) {
    JPanel fila = new JPanel(new GridLayout(1, 7));
    fila.setOpaque(false);
    fila.setBorder(new EmptyBorder(16, 24, 16, 24));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

    JLabel lblCodigo = new JLabel(item.codigo);
    lblCodigo.setFont(Fuentes.r(13));
    lblCodigo.setForeground(Colores.GRIS_TEXTO);

    JLabel lblNombre = new JLabel(item.nombre);
    lblNombre.setFont(Fuentes.r(13));
    lblNombre.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblSistema = new JLabel(String.valueOf(item.stockSistema));
    lblSistema.setFont(Fuentes.b(14));
    lblSistema.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblFisico = new JLabel(String.valueOf(item.stockFisico));
    lblFisico.setFont(Fuentes.b(14));
    lblFisico.setForeground(Colores.TEXTO_OSCURO);

    int diff = item.diferencia();
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

    boolean verificado = item.verificado;
    Color estadoFg = verificado ? new Color(21, 128, 61) : new Color(161, 110, 0);
    Color estadoBg = verificado ? new Color(220, 252, 231) : new Color(254, 243, 199);
    JLabel lblEstado = new JLabel(item.estado(), SwingConstants.CENTER);
    lblEstado.setFont(Fuentes.b(11));
    lblEstado.setForeground(estadoFg);
    lblEstado.setOpaque(true);
    lblEstado.setBackground(estadoBg);
    lblEstado.setBorder(new EmptyBorder(4, 10, 4, 10));
    JPanel wrapEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapEstado.setOpaque(false);
    wrapEstado.add(lblEstado);

    JPanel wrapAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapAccion.setOpaque(false);
    if (item.verificado) {
      JLabel sinDiff = new JLabel("Sin diferencias");
      sinDiff.setFont(Fuentes.r(13));
      sinDiff.setForeground(new Color(21, 128, 61));
      wrapAccion.add(sinDiff);
    } else {
      JButton btnAjustar = btnAzulTabla("Ajustar");
      btnAjustar.addActionListener(e -> abrirDialogoAjuste(item));
      wrapAccion.add(btnAjustar);
    }

    fila.add(lblCodigo);
    fila.add(lblNombre);
    fila.add(lblSistema);
    fila.add(lblFisico);
    fila.add(wrapDiff);
    fila.add(wrapEstado);
    fila.add(wrapAccion);
    return fila;
  }

  private void abrirDialogoAjuste(ItemConteo item) {
    JDialog dlg = new JDialog(this, "Ajustar Inventario: " + item.nombre, true);
    dlg.setSize(440, 320);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Ajustar: " + item.nombre);
    titulo.setFont(Fuentes.b(18));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(18));

    JPanel infoRow = new JPanel(new GridLayout(1, 3, 12, 0));
    infoRow.setOpaque(false);
    infoRow.setAlignmentX(LEFT_ALIGNMENT);
    infoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
    infoRow.add(miniCard("Stock Sistema", String.valueOf(item.stockSistema), Colores.AZUL));
    infoRow.add(miniCard("Stock Físico", String.valueOf(item.stockFisico), new Color(217, 119, 6)));
    int d = item.diferencia();
    String dTxt = d > 0 ? "+" + d : String.valueOf(d);
    infoRow.add(miniCard("Diferencia", dTxt, d < 0 ? Colores.ROJO : new Color(21, 128, 61)));
    panel.add(infoRow);
    panel.add(Box.createVerticalStrut(18));

    JLabel lblStockFisico = new JLabel("Corregir Stock Físico");
    lblStockFisico.setFont(Fuentes.b(12));
    lblStockFisico.setForeground(Colores.TEXTO_OSCURO);
    lblStockFisico.setAlignmentX(LEFT_ALIGNMENT);

    JTextField campoFisico = new JTextField(String.valueOf(item.stockFisico));
    campoFisico.setFont(Fuentes.r(14));
    campoFisico.setBorder(BorderFactory.createCompoundBorder(
      new PantallaLogin.RoundedLineBorder(Colores.BORDE_GRIS, 1, 8),
      new EmptyBorder(8, 12, 8, 12)));
    campoFisico.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    campoFisico.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(lblStockFisico);
    panel.add(Box.createVerticalStrut(6));
    panel.add(campoFisico);
    panel.add(Box.createVerticalStrut(18));

    JButton btnConfirmar = btnAzulDialog("Confirmar Ajuste");
    btnConfirmar.setAlignmentX(LEFT_ALIGNMENT);
    btnConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    btnConfirmar.addActionListener(e -> {
      try {
        int nuevoFisico = Integer.parseInt(campoFisico.getText().trim());
        facade.ajustarStock(item.codigo, nuevoFisico);
        item.stockFisico = nuevoFisico;
        item.verificado = (item.stockSistema == item.stockFisico);
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
    lV.setFont(Fuentes.b(20));
    lV.setForeground(colorVal);
    p.add(lE);
    p.add(Box.createVerticalStrut(3));
    p.add(lV);
    return p;
  }

  private void iniciarNuevoConteo() {
    JDialog dlg = new JDialog(this, "Iniciar Nuevo Conteo", true);
    dlg.setSize(480, 420);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Registrar Conteo Físico");
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(6));
    JLabel sub = new JLabel("Ingresa el stock físico contado para cada producto");
    sub.setFont(Fuentes.r(13));
    sub.setForeground(Colores.GRIS_TEXTO);
    sub.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(sub);
    panel.add(Box.createVerticalStrut(20));

    JTextField[] campos = new JTextField[items.size()];
    for (int i = 0; i < items.size(); i++) {
      ItemConteo item = items.get(i);
      JPanel filaForm = new JPanel(new BorderLayout(12, 0));
      filaForm.setOpaque(false);
      filaForm.setAlignmentX(LEFT_ALIGNMENT);
      filaForm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

      JLabel lbl = new JLabel(item.nombre + " (Sistema: " + item.stockSistema + ")");
      lbl.setFont(Fuentes.r(13));
      lbl.setForeground(Colores.TEXTO_OSCURO);
      lbl.setPreferredSize(new Dimension(240, 40));

      JTextField tf = new JTextField(String.valueOf(item.stockFisico));
      tf.setFont(Fuentes.r(13));
      tf.setBorder(BorderFactory.createCompoundBorder(
        new PantallaLogin.RoundedLineBorder(Colores.BORDE_GRIS, 1, 8),
        new EmptyBorder(6, 10, 6, 10)));
      campos[i] = tf;

      filaForm.add(lbl, BorderLayout.WEST);
      filaForm.add(tf, BorderLayout.CENTER);
      panel.add(filaForm);
      panel.add(Box.createVerticalStrut(10));
    }

    panel.add(Box.createVerticalStrut(8));
    JButton btnGuardar = btnAzulDialog("Guardar Conteo");
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    btnGuardar.addActionListener(e -> {
      try {
        for (int i = 0; i < items.size(); i++) {
          ItemConteo item = items.get(i);
          int nf = Integer.parseInt(campos[i].getText().trim());
          facade.ajustarStock(item.codigo, nf);
          item.stockFisico = nf;
          item.verificado = (item.stockSistema == nf);
        }
        actualizarStats();
        construirTabla();
        dlg.dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(dlg, "Ingrese valores numéricos válidos.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });
    panel.add(btnGuardar);

    JScrollPane sp = new JScrollPane(panel);
    sp.setBorder(BorderFactory.createEmptyBorder());
    sp.getVerticalScrollBar().setUnitIncrement(12);
    dlg.setContentPane(sp);
    dlg.setVisible(true);
  }

  private void actualizarStats() {
    lblAuditados.setText(String.valueOf(items.size()));
    lblPendientes.setText(String.valueOf(contarPendientes()));
    lblDiferencias.setText(String.valueOf(sumaDiferencias()));
  }

  private int contarPendientes() {
    return (int) items.stream().filter(i -> !i.verificado).count();
  }

  private int sumaDiferencias() {
    return items.stream().mapToInt(i -> Math.abs(i.diferencia())).sum();
  }

  private JButton btnAmarillo(String texto) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? new Color(240, 180, 0) : new Color(255, 200, 0));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(new Color(30, 30, 30));
    b.setFont(Fuentes.b(13));
    b.setPreferredSize(new Dimension(180, 42));
    return b;
  }

  private JButton btnAzul(String texto) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(14));
    b.setPreferredSize(new Dimension(200, 42));
    return b;
  }

  private JButton btnAzulTabla(String texto) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(12));
    b.setPreferredSize(new Dimension(88, 34));
    return b;
  }

  private JButton btnAzulDialog(String texto) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(ov ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(14));
    return b;
  }

}
