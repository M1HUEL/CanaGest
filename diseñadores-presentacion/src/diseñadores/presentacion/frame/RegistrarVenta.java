package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class RegistrarVenta extends JFrame {

  private final IVentas facade;
  private Venta ventaActual;
  private final List<ItemVentaDTO> carritoDisplay = new ArrayList<>();
  private List<ProductoDTO> catalogoProductos;

  private JPanel panelCarritoItems;
  private JLabel lblTotal, lblCantItems, lblProductosCount;
  private JTextField campoBusqueda, campoEscanear;
  private JPanel panelGrid;

  public RegistrarVenta(IVentas facade) {
    super("Punto de Venta");
    this.facade = facade;
    inicializar();
  }

  private void inicializar() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1350, 780);
    setLocationRelativeTo(null);
    setResizable(true);

    ventaActual = facade.iniciarNuevaVenta();
    catalogoProductos = facade.obtenerCatalogo();

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
    topBar.add(botonCerrarSesion());
    root.add(topBar, BorderLayout.NORTH);

    JPanel centro = new JPanel(new GridBagLayout());
    centro.setOpaque(false);
    centro.setBorder(new EmptyBorder(20, 20, 20, 20));
    GridBagConstraints g = new GridBagConstraints();
    g.fill = GridBagConstraints.BOTH;
    g.gridy = 0;
    g.weighty = 1.0;
    g.gridx = 0;
    g.weightx = 0.65;
    g.insets = new Insets(0, 0, 0, 16);
    centro.add(panelIzquierdo(), g);
    g.gridx = 1;
    g.weightx = 0.35;
    g.insets = new Insets(0, 0, 0, 0);
    centro.add(panelDerecho(), g);

    root.add(centro, BorderLayout.CENTER);
    setContentPane(root);
    actualizarVista();
  }

  // ── Panel izquierdo ───────────────────────────────────────
  private JPanel panelIzquierdo() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setOpaque(false);
    p.add(tarjetaEscanear());
    p.add(Box.createVerticalStrut(16));
    p.add(tarjetaBusqueda());
    return p;
  }

  private JPanel tarjetaEscanear() {
    JPanel card = tarjeta();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

    JLabel titulo = new JLabel("Escanear Producto");
    titulo.setFont(Fuentes.b(17));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);

    campoEscanear = campoPill("Escanear código del producto");
    campoEscanear.setAlignmentX(LEFT_ALIGNMENT);
    campoEscanear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    campoEscanear.addActionListener(e -> {
      String code = campoEscanear.getText().trim();
      if (!code.isEmpty()) {
        agregarAlCarrito(code);
        campoEscanear.setText("");
      }
    });

    card.add(titulo);
    card.add(Box.createVerticalStrut(14));
    card.add(campoEscanear);
    return card;
  }

  private JPanel tarjetaBusqueda() {
    JPanel card = tarjeta();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

    JLabel titulo = new JLabel("Busqueda Rapida");
    titulo.setFont(Fuentes.b(17));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);

    campoBusqueda = campoPill("Nombre del producto");
    campoBusqueda.setAlignmentX(LEFT_ALIGNMENT);
    campoBusqueda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

    JButton btnBuscar = botonAzul("Buscar");
    btnBuscar.setAlignmentX(LEFT_ALIGNMENT);
    btnBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    btnBuscar.addActionListener(e -> filtrarGrid(campoBusqueda.getText().trim()));
    campoBusqueda.addActionListener(e -> filtrarGrid(campoBusqueda.getText().trim()));

    panelGrid = new JPanel(new GridLayout(0, 3, 10, 10));
    panelGrid.setOpaque(false);
    panelGrid.setAlignmentX(LEFT_ALIGNMENT);
    construirGrid(catalogoProductos);

    card.add(titulo);
    card.add(Box.createVerticalStrut(12));
    card.add(campoBusqueda);
    card.add(Box.createVerticalStrut(10));
    card.add(btnBuscar);
    card.add(Box.createVerticalStrut(14));
    card.add(panelGrid);
    return card;
  }

  private void construirGrid(List<ProductoDTO> lista) {
    panelGrid.removeAll();
    for (ProductoDTO p : lista) {
      panelGrid.add(botonProducto(p));
    }
    panelGrid.revalidate();
    panelGrid.repaint();
  }

  private void filtrarGrid(String query) {
    if (query.isEmpty()) {
      construirGrid(catalogoProductos);
      return;
    }
    List<ProductoDTO> filtrados = new ArrayList<>();
    for (ProductoDTO p : catalogoProductos) {
      if (p.getNombre().toLowerCase().contains(query.toLowerCase())
        || p.getCodigo().toLowerCase().contains(query.toLowerCase())) {
        filtrados.add(p);
      }
    }
    construirGrid(filtrados);
  }

  private JPanel botonProducto(ProductoDTO prod) {
    String codigo = prod.getCodigo();
    String nombre = prod.getNombre();
    String precio = String.format("%.2f", prod.getPrecio());

    JPanel btn = new JPanel() {
      boolean hover = false;

      {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            hover = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            hover = false;
            repaint();
          }

          public void mouseClicked(MouseEvent e) {
            agregarAlCarrito(codigo);
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
    btn.setBorder(new EmptyBorder(12, 10, 12, 10));
    btn.setPreferredSize(new Dimension(0, 80));

    JLabel lCod = etiqueta(codigo, 10, false, Colores.AZUL_MUY_SUTIL);
    JLabel lNom = etiqueta(nombre, 16, true, Colores.BLANCO);
    JLabel lPre = etiqueta("$" + precio, 13, false, Colores.AZUL_MUY_SUTIL);

    for (JLabel l : new JLabel[]{lCod, lNom, lPre}) {
      l.setAlignmentX(CENTER_ALIGNMENT);
      btn.add(l);
      if (l == lCod) {
        btn.add(Box.createVerticalStrut(4));
      }
      if (l == lNom) {
        btn.add(Box.createVerticalStrut(4));
      }
    }
    return btn;
  }

  // ── Panel derecho ─────────────────────────────────────────
  private JPanel panelDerecho() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setOpaque(false);
    p.add(tarjetaCarrito());
    p.add(Box.createVerticalStrut(14));
    p.add(tarjetaTotal());
    return p;
  }

  private JPanel tarjetaCarrito() {
    JPanel card = tarjeta();
    card.setLayout(new BorderLayout(0, 12));

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    JLabel titulo = new JLabel("Carrito");
    titulo.setFont(Fuentes.b(17));
    titulo.setForeground(Colores.TEXTO_OSCURO);

    lblCantItems = new JLabel("0 items");
    lblCantItems.setFont(Fuentes.b(12));
    lblCantItems.setForeground(Colores.AZUL);
    lblCantItems.setOpaque(true);
    lblCantItems.setBackground(Colores.AZUL_CLARO);
    lblCantItems.setBorder(new EmptyBorder(3, 10, 3, 10));
    header.add(titulo, BorderLayout.WEST);
    header.add(lblCantItems, BorderLayout.EAST);

    panelCarritoItems = new JPanel();
    panelCarritoItems.setLayout(new BoxLayout(panelCarritoItems, BoxLayout.Y_AXIS));
    panelCarritoItems.setOpaque(false);

    JScrollPane scroll = new JScrollPane(panelCarritoItems);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(12);
    scroll.setPreferredSize(new Dimension(0, 320));

    card.add(header, BorderLayout.NORTH);
    card.add(scroll, BorderLayout.CENTER);
    return card;
  }

  private JPanel tarjetaTotal() {
    JPanel card = new JPanel(new BorderLayout(0, 12)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, 18, 18));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 18, 18));
        super.paintComponent(g2d);
      }

    };
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(20, 20, 20, 20));

    JLabel lbl = new JLabel("Total a pagar", SwingConstants.CENTER);
    lbl.setFont(Fuentes.r(15));
    lbl.setForeground(Colores.GRIS_TEXTO);
    card.add(lbl, BorderLayout.NORTH);

    lblTotal = new JLabel("$0.00", SwingConstants.CENTER);
    lblTotal.setFont(Fuentes.b(38));
    lblTotal.setForeground(Colores.BLANCO);

    lblProductosCount = new JLabel("0 productos", SwingConstants.CENTER);
    lblProductosCount.setFont(Fuentes.r(13));
    lblProductosCount.setForeground(Colores.AZUL_SUTIL);

    JPanel cuadroAzul = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        super.paintComponent(g2d);
      }

    };
    cuadroAzul.setOpaque(false);
    cuadroAzul.setBorder(new EmptyBorder(18, 14, 18, 14));
    cuadroAzul.setPreferredSize(new Dimension(0, 110));
    cuadroAzul.add(lblTotal);
    cuadroAzul.add(lblProductosCount);
    card.add(cuadroAzul, BorderLayout.CENTER);

    JPanel botonesRow = new JPanel(new GridLayout(1, 2, 10, 0));
    botonesRow.setOpaque(false);
    botonesRow.setPreferredSize(new Dimension(0, 58));

    JButton btnCancelar = botonAccion("Cancelar", Colores.ROJO, Colores.ROJO_HOVER);
    btnCancelar.addActionListener(e -> {
      if (carritoDisplay.isEmpty()) {
        return;
      }
      int op = JOptionPane.showConfirmDialog(this,
        "¿Cancelar la venta y vaciar el carrito?", "Cancelar venta",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (op == JOptionPane.YES_OPTION) {
        carritoDisplay.clear();
        ventaActual = facade.iniciarNuevaVenta();
        actualizarVista();
      }
    });

    JButton btnContinuar = botonAccion("Continuar", Colores.VERDE, Colores.VERDE_OSCURO);
    btnContinuar.addActionListener(e -> {
      if (carritoDisplay.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El carrito esta vacio.",
          "Sin productos", JOptionPane.WARNING_MESSAGE);
        return;
      }
      double total = carritoDisplay.stream().mapToDouble(ItemVentaDTO::getSubtotal).sum();
      this.setVisible(false);
      new SeleccionarMetodoPago(this, facade, ventaActual, total, () -> {
        carritoDisplay.clear();
        ventaActual = facade.iniciarNuevaVenta();
        actualizarVista();
      });
    });

    botonesRow.add(btnCancelar);
    botonesRow.add(btnContinuar);
    card.add(botonesRow, BorderLayout.SOUTH);
    return card;
  }

  // ── Lógica de carrito ─────────────────────────────────────
  private void agregarAlCarrito(String codigo) {
    EscanearProductoDTO dto = new EscanearProductoDTO(codigo);

    if (!facade.existeProducto(dto)) {
      JOptionPane.showMessageDialog(this,
        "<html>El producto <b>" + codigo + "</b> no existe en el catalogo.</html>",
        "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (!facade.tieneStock(dto)) {
      JOptionPane.showMessageDialog(this,
        "<html>El producto <b>" + codigo + "</b> no tiene unidades disponibles.</html>",
        "Sin stock", JOptionPane.WARNING_MESSAGE);
      return;
    }

    ProductoDTO p = facade.procesarProducto(ventaActual, dto);
    if (p == null) {
      JOptionPane.showMessageDialog(this,
        "<html>No se pudo agregar <b>" + codigo + "</b>. Verifique el stock.</html>",
        "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    for (int i = 0; i < carritoDisplay.size(); i++) {
      if (carritoDisplay.get(i).getCodigo().equalsIgnoreCase(p.getCodigo())) {
        ItemVentaDTO actual = carritoDisplay.get(i);
        carritoDisplay.set(i, actual.conCantidad(actual.getCantidad() + 1));
        actualizarVista();
        return;
      }
    }
    carritoDisplay.add(new ItemVentaDTO(p.getCodigo(), p.getNombre(), p.getPrecio(), 1));
    actualizarVista();
  }

  private void actualizarVista() {
    panelCarritoItems.removeAll();
    double total = 0;
    for (ItemVentaDTO item : new ArrayList<>(carritoDisplay)) {
      panelCarritoItems.add(filaCarrito(item));
      panelCarritoItems.add(Box.createVerticalStrut(8));
      total += item.getSubtotal();
    }
    panelCarritoItems.revalidate();
    panelCarritoItems.repaint();

    lblTotal.setText(String.format("$%.2f", total));
    int totalUnidades = carritoDisplay.stream().mapToInt(ItemVentaDTO::getCantidad).sum();
    lblCantItems.setText(totalUnidades + " items");
    lblProductosCount.setText(totalUnidades + " productos");
  }

  private JPanel filaCarrito(ItemVentaDTO item) {
    JPanel fila = new JPanel(new GridBagLayout());
    fila.setOpaque(true);
    fila.setBackground(Colores.BLANCO);
    fila.setBorder(BorderFactory.createCompoundBorder(
      new LineBorder(Colores.BORDE_GRIS, 1, true),
      new EmptyBorder(10, 12, 10, 12)));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

    GridBagConstraints c = new GridBagConstraints();

    JLabel lblNombre = etiqueta(item.getNombre(), 14, true, Colores.TEXTO_OSCURO);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    fila.add(lblNombre, c);

    JButton btnElim = botonEliminar();
    btnElim.addActionListener(e -> {
      ventaActual.removerTodas(item.getCodigo());
      carritoDisplay.removeIf(i -> i.getCodigo().equalsIgnoreCase(item.getCodigo()));
      actualizarVista();
    });
    c.gridx = 1;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    fila.add(btnElim, c);

    JLabel lblPreU = etiqueta("Precio unitario: $" + String.format("%.2f", item.getPrecioUnitario()), 11, false, Colores.GRIS_TEXTO);
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 1;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 2;
    fila.add(lblPreU, c);

    JPanel ctrlRow = new JPanel(new BorderLayout(8, 0));
    ctrlRow.setOpaque(false);
    JPanel ctrlCant = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    ctrlCant.setOpaque(false);

    JButton btnMenos = botonCantidad("-");
    JLabel lblCant = new JLabel(String.valueOf(item.getCantidad()));
    lblCant.setFont(Fuentes.b(15));
    lblCant.setForeground(Colores.TEXTO_OSCURO);
    lblCant.setPreferredSize(new Dimension(32, 32));
    lblCant.setHorizontalAlignment(SwingConstants.CENTER);
    JButton btnMas = botonCantidad("+");

    btnMenos.addActionListener(e -> {
      ventaActual.removerUnaUnidad(item.getCodigo());
      int idx = indexEnCarrito(item.getCodigo());
      if (idx < 0) {
        return;
      }
      if (item.getCantidad() > 1) {
        carritoDisplay.set(idx, item.conCantidad(item.getCantidad() - 1));
      } else {
        carritoDisplay.remove(idx);
      }
      actualizarVista();
    });

    btnMas.addActionListener(e -> {
      EscanearProductoDTO dto = new EscanearProductoDTO(item.getCodigo());
      if (!facade.tieneStock(dto)) {
        JOptionPane.showMessageDialog(fila,
          "<html>No hay mas stock de <b>" + item.getNombre() + "</b>.</html>",
          "Sin stock", JOptionPane.WARNING_MESSAGE);
        return;
      }
      ProductoDTO extra = facade.procesarProducto(ventaActual, dto);
      if (extra != null) {
        int idx = indexEnCarrito(item.getCodigo());
        if (idx >= 0) {
          carritoDisplay.set(idx, item.conCantidad(item.getCantidad() + 1));
        }
        actualizarVista();
      }
    });

    ctrlCant.add(btnMenos);
    ctrlCant.add(lblCant);
    ctrlCant.add(btnMas);

    JPanel subPanel = new JPanel(new BorderLayout());
    subPanel.setOpaque(false);
    JLabel lblSubTxt = etiqueta("Subtotal", 10, false, Colores.GRIS_TEXTO);
    lblSubTxt.setHorizontalAlignment(SwingConstants.RIGHT);
    JLabel lblSub = etiqueta(String.format("$%.2f", item.getSubtotal()), 15, true, Colores.AZUL);
    lblSub.setHorizontalAlignment(SwingConstants.RIGHT);
    subPanel.add(lblSubTxt, BorderLayout.NORTH);
    subPanel.add(lblSub, BorderLayout.SOUTH);

    ctrlRow.add(ctrlCant, BorderLayout.WEST);
    ctrlRow.add(subPanel, BorderLayout.EAST);

    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.insets = new Insets(6, 0, 0, 0);
    fila.add(ctrlRow, c);
    return fila;
  }

  private int indexEnCarrito(String codigo) {
    for (int i = 0; i < carritoDisplay.size(); i++) {
      if (carritoDisplay.get(i).getCodigo().equalsIgnoreCase(codigo)) {
        return i;
      }
    }
    return -1;
  }

  // ── Helpers de UI ─────────────────────────────────────────
  private JPanel tarjeta() {
    JPanel p = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, 18, 18));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 18, 18));
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    p.setBorder(new EmptyBorder(20, 20, 20, 20));
    return p;
  }

  private JTextField campoPill(String placeholder) {
    JTextField tf = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_INPUT);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    tf.setOpaque(false);
    tf.setBorder(new EmptyBorder(8, 14, 8, 14));
    tf.setFont(Fuentes.r(14));
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

  private JButton botonAzul(String texto) {
    return botonAccion(texto, Colores.AZUL, Colores.AZUL_HOVER);
  }

  private JButton botonAccion(String texto, Color base, Color hover) {
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
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(14));
    return b;
  }

  private JButton botonCantidad(String sym) {
    JButton b = new JButton(sym) {
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
        g.setColor(ov ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(17));
    b.setPreferredSize(new Dimension(32, 32));
    return b;
  }

  private JButton botonEliminar() {
    JButton b = new JButton("X") {
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
        g.setColor(ov ? Colores.ROJO_BG_HOVER : Colores.ROJO_BG);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.ROJO_ICONO);
    b.setFont(Fuentes.b(11));
    b.setPreferredSize(new Dimension(32, 32));
    return b;
  }

  private JButton botonCerrarSesion() {
    JButton b = new JButton("Cerrar sesion") {
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
        g.setColor(ov ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(13));
    b.setPreferredSize(new Dimension(160, 38));
    b.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this, "¿Cerrar sesion?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (op == JOptionPane.YES_OPTION) {
        System.exit(0);
      }
    });
    return b;
  }

  private JLabel etiqueta(String txt, int size, boolean bold, Color color) {
    JLabel l = new JLabel(txt);
    l.setFont(bold ? Fuentes.b(size) : Fuentes.r(size));
    l.setForeground(color);
    return l;
  }

}
