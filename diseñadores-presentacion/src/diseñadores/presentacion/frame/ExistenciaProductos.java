package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.inventario.InventarioFacade;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ExistenciaProductos extends JFrame {

  private final JFrame menuOrigen;
  private final InventarioFacade facade;
  private final List<ProductoDTO> productos = new ArrayList<>();
  private JPanel panelTabla;
  private JTextField campoBusqueda;

  public ExistenciaProductos(JFrame menuOrigen) {
    this.menuOrigen = menuOrigen;
    this.facade = new InventarioFacade();
    setTitle("La Canasta - Existencia de Productos");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    productos.addAll(facade.obtenerTodos());

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

    JPanel izq = new JPanel(new GridLayout(2, 1, 0, 2));
    izq.setOpaque(false);

    JButton btnMenu = btnAmarillo("Menu Principal");
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
    JLabel lblTitulo = new JLabel("Existencia de Productos");
    lblTitulo.setFont(Fuentes.b(26));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);
    JLabel lblDesc = new JLabel("Control de inventario y niveles de stock");
    lblDesc.setFont(Fuentes.r(14));
    lblDesc.setForeground(Colores.GRIS_TEXTO);
    tituloCol.add(lblTitulo);
    tituloCol.add(Box.createVerticalStrut(4));
    tituloCol.add(lblDesc);
    header.add(tituloCol, BorderLayout.WEST);

    campoBusqueda = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 249, 252));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }
    };
    campoBusqueda.setOpaque(false);
    campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(new Color(213, 218, 230), 1, 8),
      new EmptyBorder(8, 14, 8, 14)));
    campoBusqueda.setFont(Fuentes.r(14));
    campoBusqueda.setForeground(Colores.GRIS_TEXTO);
    campoBusqueda.setText("Buscar producto...");
    campoBusqueda.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (campoBusqueda.getText().startsWith("Buscar")) {
          campoBusqueda.setText("");
          campoBusqueda.setForeground(Colores.TEXTO_OSCURO);
        }
      }
      @Override
      public void focusLost(FocusEvent e) {
        if (campoBusqueda.getText().isEmpty()) {
          campoBusqueda.setText("Buscar producto...");
          campoBusqueda.setForeground(Colores.GRIS_TEXTO);
        }
      }
    });
    campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {}
    });

    JPanel busquedaWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
    busquedaWrap.setOpaque(false);
    busquedaWrap.add(campoBusqueda);

    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setOpaque(false);
    topRow.add(header, BorderLayout.WEST);
    topRow.add(busquedaWrap, BorderLayout.EAST);

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

    JPanel headerTabla = new JPanel(new GridLayout(1, 8));
    headerTabla.setOpaque(false);
    headerTabla.setBorder(new EmptyBorder(16, 24, 16, 24));
    String[] cols = {"Código", "Producto", "Stock Actual", "Stock Mín", "Stock Máx", "Estado", "Última Act.", "Acciones"};
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
    construirTabla(productos);

    JScrollPane scroll = new JScrollPane(panelTabla);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    wrapTabla.add(headerWrap, BorderLayout.NORTH);
    wrapTabla.add(scroll, BorderLayout.CENTER);

    contenido.add(topRow, BorderLayout.NORTH);
    contenido.add(wrapTabla, BorderLayout.CENTER);
    return contenido;
  }

  private void filtrar() {
    String q = campoBusqueda.getText();
    if (q.startsWith("Buscar") || q.isEmpty()) {
      construirTabla(productos);
      return;
    }
    q = q.toLowerCase();
    List<ProductoDTO> f = new ArrayList<>();
    for (ProductoDTO p : productos) {
      if (p.getNombre().toLowerCase().contains(q) || p.getCodigo().toLowerCase().contains(q)) {
        f.add(p);
      }
    }
    construirTabla(f);
  }

  private void construirTabla(List<ProductoDTO> lista) {
    panelTabla.removeAll();
    for (ProductoDTO p : lista) {
      panelTabla.add(filaProducto(p));
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

  private JPanel filaProducto(ProductoDTO p) {
    JPanel fila = new JPanel(new GridLayout(1, 8));
    fila.setOpaque(false);
    fila.setBorder(new EmptyBorder(16, 24, 16, 24));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

    JLabel lblCodigo = new JLabel(p.getCodigo());
    lblCodigo.setFont(Fuentes.r(13));
    lblCodigo.setForeground(Colores.GRIS_TEXTO);

    JLabel lblNombre = new JLabel(p.getNombre());
    lblNombre.setFont(Fuentes.r(13));
    lblNombre.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblStock = new JLabel(String.valueOf(p.getStock()));
    lblStock.setFont(Fuentes.b(14));
    lblStock.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblMin = new JLabel(String.valueOf(p.getStockMinimo()));
    lblMin.setFont(Fuentes.r(13));
    lblMin.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblMax = new JLabel(String.valueOf(p.getStockMaximo()));
    lblMax.setFont(Fuentes.r(13));
    lblMax.setForeground(Colores.TEXTO_OSCURO);

    String estado = p.estaBajoMinimo() ? "Bajo" : (p.estaSobreMaximo() ? "Alto" : "Normal");
    Color estadoColor = p.estaBajoMinimo() ? new Color(185, 28, 28) : (p.estaSobreMaximo() ? new Color(161, 110, 0) : new Color(21, 128, 61));
    Color estadoBg = p.estaBajoMinimo() ? new Color(254, 226, 226) : (p.estaSobreMaximo() ? new Color(254, 243, 199) : new Color(220, 252, 231));
    JLabel lblEstado = new JLabel(estado, SwingConstants.CENTER);
    lblEstado.setFont(Fuentes.b(11));
    lblEstado.setForeground(estadoColor);
    lblEstado.setOpaque(true);
    lblEstado.setBackground(estadoBg);
    lblEstado.setBorder(new EmptyBorder(4, 10, 4, 10));
    JPanel wrapEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapEstado.setOpaque(false);
    wrapEstado.add(lblEstado);

    JLabel lblUlt = new JLabel(p.getFechaModificacion() != null ? p.getFechaModificacion() : "-");
    lblUlt.setFont(Fuentes.r(13));
    lblUlt.setForeground(Colores.TEXTO_OSCURO);

    JPanel wrapAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapAccion.setOpaque(false);
    JButton btnEditar = crearBotonTabla("Editar");
    btnEditar.addActionListener(e -> abrirDialogoActualizar(p));
    wrapAccion.add(btnEditar);

    fila.add(lblCodigo);
    fila.add(lblNombre);
    fila.add(lblStock);
    fila.add(lblMin);
    fila.add(lblMax);
    fila.add(wrapEstado);
    fila.add(lblUlt);
    fila.add(wrapAccion);
    return fila;
  }

  private JButton crearBotonTabla(String texto) {
    JButton b = new JButton(texto) {
      boolean ov = false;
      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent e) { ov = true; repaint(); }
          @Override
          public void mouseExited(MouseEvent e) { ov = false; repaint(); }
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
    b.setPreferredSize(new Dimension(100, 34));
    return b;
  }

  private void abrirDialogoActualizar(ProductoDTO p) {
    JDialog dlg = new JDialog(this, "Actualizar Stock: " + p.getNombre(), true);
    dlg.setSize(400, 340);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Actualizar: " + p.getNombre());
    titulo.setFont(Fuentes.b(18));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(20));

    String[] etqs = {"Stock Actual", "Stock Mínimo", "Stock Máximo"};
    String[] vals = {String.valueOf(p.getStock()), String.valueOf(p.getStockMinimo()), String.valueOf(p.getStockMaximo())};
    JTextField[] campos = new JTextField[3];

    for (int i = 0; i < etqs.length; i++) {
      JLabel lbl = new JLabel(etqs[i]);
      lbl.setFont(Fuentes.b(12));
      lbl.setForeground(Colores.TEXTO_OSCURO);
      lbl.setAlignmentX(LEFT_ALIGNMENT);
      JTextField tf = new JTextField(vals[i]);
      tf.setFont(Fuentes.r(13));
      tf.setBorder(BorderFactory.createCompoundBorder(
        new Bordes(Colores.BORDE_GRIS, 1, 8),
        new EmptyBorder(8, 12, 8, 12)));
      tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
      tf.setAlignmentX(LEFT_ALIGNMENT);
      campos[i] = tf;
      panel.add(lbl);
      panel.add(Box.createVerticalStrut(4));
      panel.add(tf);
      panel.add(Box.createVerticalStrut(10));
    }

    JButton btnGuardar = crearBotonGuardar();
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    btnGuardar.addActionListener(e -> {
      try {
        int nuevoStock = Integer.parseInt(campos[0].getText().trim());
        int nuevoMin = Integer.parseInt(campos[1].getText().trim());
        int nuevoMax = Integer.parseInt(campos[2].getText().trim());
        facade.actualizarStockCompleto(p.getCodigo(), nuevoStock, nuevoMin, nuevoMax);
        productos.clear();
        productos.addAll(facade.obtenerTodos());
        construirTabla(productos);
        dlg.dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(dlg, "Ingrese valores numéricos válidos.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });
    panel.add(btnGuardar);

    dlg.setContentPane(panel);
    dlg.setVisible(true);
  }

  private JButton crearBotonGuardar() {
    JButton b = new JButton("Guardar") {
      boolean ov = false;
      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent e) { ov = true; repaint(); }
          @Override
          public void mouseExited(MouseEvent e) { ov = false; repaint(); }
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
          public void mouseEntered(MouseEvent e) { ov = true; repaint(); }
          @Override
          public void mouseExited(MouseEvent e) { ov = false; repaint(); }
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

}