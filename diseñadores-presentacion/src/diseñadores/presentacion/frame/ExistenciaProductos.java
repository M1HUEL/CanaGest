package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ExistenciaProductos extends JFrame {

  private final JFrame menuOrigen;
  private final VentasControl control;
  private final List<ProductoDTO> productos = new ArrayList<>();
  private JPanel panelTabla;
  private JTextField campoBusqueda;

  public ExistenciaProductos(JFrame menuOrigen, VentasControl control) {
    this.menuOrigen = menuOrigen;
    this.control = control;

    configurarVentana();
    productos.addAll(control.obtenerProductosInventario());
    inicializarComponentes();
  }

  private void configurarVentana() {
    setTitle("La Canasta - Existencia de Productos");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);
  }

  private void inicializarComponentes() {
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

    JButton btnMenu = btnAmarillo("Menu Principal");
    btnMenu.addActionListener(e -> {
      dispose();
      menuOrigen.setVisible(true);
    });

    JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
    der.setOpaque(false);
    der.add(btnMenu);

    bar.add(der, BorderLayout.EAST);
    return bar;
  }

  private JPanel buildContenido() {
    JPanel contenido = new JPanel(new BorderLayout());
    contenido.setOpaque(false);
    contenido.setBorder(new EmptyBorder(28, 32, 28, 32));

    JPanel tituloPanel = crearTitulo();
    JPanel accionesPanel = crearBarraAcciones();

    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setOpaque(false);
    topRow.setBorder(new EmptyBorder(0, 0, 20, 0));
    topRow.add(tituloPanel, BorderLayout.WEST);
    topRow.add(accionesPanel, BorderLayout.EAST);

    JPanel wrapTabla = crearContenedorTabla();

    contenido.add(topRow, BorderLayout.NORTH);
    contenido.add(wrapTabla, BorderLayout.CENTER);
    return contenido;
  }

  private JPanel crearTitulo() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    JLabel lblTitulo = new JLabel("Existencia de Productos");
    lblTitulo.setFont(Fuentes.b(26));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblDesc = new JLabel("Control de inventario y niveles de stock");
    lblDesc.setFont(Fuentes.r(14));
    lblDesc.setForeground(Colores.GRIS_TEXTO);

    panel.add(lblTitulo);
    panel.add(Box.createVerticalStrut(4));
    panel.add(lblDesc);
    return panel;
  }

  private JPanel crearBarraAcciones() {
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
    campoBusqueda.setPreferredSize(new Dimension(240, 42));
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

    campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        filtrar();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        filtrar();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
      }

    });

    JButton btnNuevo = new JButton("+ Nuevo Producto") {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(Colores.BLANCO);
        setFont(Fuentes.b(14));
        setPreferredSize(new Dimension(180, 42));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            hover = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            hover = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    btnNuevo.addActionListener(e
      -> new AgregarExistenciaProducto(this, control, this::recargarProductos).setVisible(true));

    JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
    barra.setOpaque(false);
    barra.add(campoBusqueda);
    barra.add(btnNuevo);
    return barra;
  }

  private JPanel crearContenedorTabla() {
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

    JPanel headerWrap = crearEncabezadoTabla();

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
    return wrapTabla;
  }

  private JPanel crearEncabezadoTabla() {
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

    JPanel headerWrap = new JPanel(new BorderLayout());
    headerWrap.setOpaque(false);
    headerWrap.add(headerTabla, BorderLayout.CENTER);
    headerWrap.add(sepHeader, BorderLayout.SOUTH);
    return headerWrap;
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
      panelTabla.add(crearSeparador());
    }
    panelTabla.revalidate();
    panelTabla.repaint();
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

  private JPanel filaProducto(ProductoDTO p) {
    JPanel fila = new JPanel(new GridLayout(1, 8));
    fila.setOpaque(false);
    fila.setBorder(new EmptyBorder(16, 24, 16, 24));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

    fila.add(new JLabel(p.getCodigo()) {
      {
        setFont(Fuentes.r(13));
        setForeground(Colores.GRIS_TEXTO);
      }

    });
    fila.add(new JLabel(p.getNombre()) {
      {
        setFont(Fuentes.r(13));
        setForeground(Colores.TEXTO_OSCURO);
      }

    });
    fila.add(new JLabel(String.valueOf(p.getStock())) {
      {
        setFont(Fuentes.b(14));
        setForeground(Colores.TEXTO_OSCURO);
      }

    });
    fila.add(new JLabel(String.valueOf(p.getStockMinimo())) {
      {
        setFont(Fuentes.r(13));
        setForeground(Colores.TEXTO_OSCURO);
      }

    });
    fila.add(new JLabel(String.valueOf(p.getStockMaximo())) {
      {
        setFont(Fuentes.r(13));
        setForeground(Colores.TEXTO_OSCURO);
      }

    });

    fila.add(crearBadgeEstado(p));

    String fecha = p.getFechaModificacion() != null ? p.getFechaModificacion() : "-";
    fila.add(new JLabel(fecha) {
      {
        setFont(Fuentes.r(13));
        setForeground(Colores.TEXTO_OSCURO);
      }

    });

    JPanel wrapAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapAccion.setOpaque(false);
    JButton btnEditar = crearBotonTabla("Editar");
    btnEditar.addActionListener(e -> new EditarExistenciaProducto(this, control, p, this::recargarProductos).setVisible(true));
    wrapAccion.add(btnEditar);
    fila.add(wrapAccion);

    return fila;
  }

  private JPanel crearBadgeEstado(ProductoDTO p) {
    String estado = p.estaBajoMinimo() ? "Bajo" : (p.estaSobreMaximo() ? "Alto" : "Normal");
    Color color = p.estaBajoMinimo() ? new Color(185, 28, 28) : (p.estaSobreMaximo() ? new Color(161, 110, 0) : new Color(21, 128, 61));
    Color bg = p.estaBajoMinimo() ? new Color(254, 226, 226) : (p.estaSobreMaximo() ? new Color(254, 243, 199) : new Color(220, 252, 231));

    JLabel lbl = new JLabel(estado, SwingConstants.CENTER);
    lbl.setFont(Fuentes.b(11));
    lbl.setForeground(color);
    lbl.setOpaque(true);
    lbl.setBackground(bg);
    lbl.setBorder(new EmptyBorder(4, 10, 4, 10));

    JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrap.setOpaque(false);
    wrap.add(lbl);
    return wrap;
  }

  private void recargarProductos() {
    productos.clear();
    productos.addAll(control.obtenerProductosInventario());
    construirTabla(productos);
  }

  private JButton crearBotonTabla(String texto) {
    return new JButton(texto) {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(Colores.BLANCO);
        setFont(Fuentes.b(12));
        setPreferredSize(new Dimension(100, 34));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            hover = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            hover = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
  }

  private JButton btnAmarillo(String texto) {
    return new JButton(texto) {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(new Color(30, 30, 30));
        setFont(Fuentes.b(13));
        setPreferredSize(new Dimension(180, 42));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            hover = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            hover = false;
            repaint();
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? new Color(240, 180, 0) : new Color(255, 200, 0));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
  }

}
