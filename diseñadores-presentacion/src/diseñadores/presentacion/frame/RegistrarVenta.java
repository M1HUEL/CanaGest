package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrarVenta extends JFrame {

  private final IVentas ventasFachada;
  private final IUsuarios usuariosFachada;
  private final IInventario inventarioFachada;
  private final IProveedores proveedoresFachada;
  private final UsuarioDTO usuarioActivo;
  private VentaDTO ventaActual;
  private List<ProductoDTO> catalogoProductos = new ArrayList<>();

  private JPanel panelCarritoItems;
  private JPanel panelGrid;
  private JScrollPane scrollGrid;
  private JLabel lblTotal, lblCantItems, lblProductosCount;
  private JTextField campoBusqueda, campoEscanear;

  public RegistrarVenta(IVentas ventasFachada, UsuarioDTO usuarioActivo, IUsuarios usuariosFachada,
    IInventario inventarioFachada, IProveedores proveedoresFachada) {
    super("Punto de Venta");
    this.ventasFachada = ventasFachada;
    this.usuariosFachada = usuariosFachada;
    this.inventarioFachada = inventarioFachada;
    this.proveedoresFachada = proveedoresFachada;
    this.usuarioActivo = usuarioActivo;
    this.ventaActual = new VentaDTO();
    this.catalogoProductos = ventasFachada.obtenerCatalogo();

    configurarVentana();
    inicializarComponentes();
    actualizarVista();
  }

  private void configurarVentana() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1350, 780);
    setLocationRelativeTo(null);
    setResizable(true);
  }

  private void inicializarComponentes() {
    JPanel root = crearPanelRaiz();
    JPanel centro = crearPanelCentro();

    root.add(crearTopBar(), BorderLayout.NORTH);
    root.add(centro, BorderLayout.CENTER);
    setContentPane(root);
  }

  private JPanel crearPanelRaiz() {
    JPanel root = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);
    return root;
  }

  private JPanel crearPanelCentro() {
    JPanel centro = new JPanel(new GridBagLayout());
    centro.setOpaque(false);
    centro.setBorder(new EmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 0;
    gbc.weighty = 1.0;

    gbc.gridx = 0;
    gbc.weightx = 0.65;
    gbc.insets = new Insets(0, 0, 0, 16);
    centro.add(crearPanelIzquierdo(), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.35;
    gbc.insets = new Insets(0, 0, 0, 0);
    centro.add(panelDerecho(), gbc);

    return centro;
  }

  private JPanel crearTopBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));

    JButton btnMenu = crearBotonMenuPrincipal();
    bar.add(btnMenu);
    return bar;
  }

  private JButton crearBotonMenuPrincipal() {
    JButton btnMenu = new JButton("Menu Principal") {
      boolean ov = false;

      {
        configurarEstiloBotonBase(this);
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
        dibujarBotonRedondeado((Graphics2D) g2d, getWidth(), getHeight(), ov ? Colores.AMARILLO_BTN_HOVER : Colores.AMARILLO_BTN);
        super.paintComponent(g2d);
      }

    };
    btnMenu.setForeground(Colores.TEXTO_OSCURO);
    btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btnMenu.setPreferredSize(new Dimension(160, 38));
    btnMenu.addActionListener(e -> seleccionarMenuPrincipal());
    return btnMenu;
  }

  private void configurarEstiloBotonBase(JButton btn) {
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  private void dibujarBotonRedondeado(Graphics2D g, int w, int h, Color color) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(color);
    g.fill(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));
  }

  private JPanel crearPanelIzquierdo() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setOpaque(false);
    p.add(tarjetaEscanear());
    p.add(Box.createVerticalStrut(16));
    p.add(tarjetaBusqueda());
    return p;
  }

  private JPanel tarjetaEscanear() {
    JPanel card = crearTarjeta();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

    JLabel titulo = crearLabelTitulo("Escanear Producto");
    campoEscanear = crearCampoPill("Escanear codigo de barras");
    campoEscanear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    campoEscanear.addActionListener(e -> seleccionarEscanearCodigo());

    card.add(titulo);
    card.add(Box.createVerticalStrut(14));
    card.add(campoEscanear);
    return card;
  }

  private JPanel tarjetaBusqueda() {
    JPanel card = crearTarjeta();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

    JLabel titulo = crearLabelTitulo("Busqueda Rapida");
    campoBusqueda = crearCampoPill("Nombre del producto");
    campoBusqueda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

    JButton btnBuscar = crearBotonAccion("Buscar", Colores.AZUL, Colores.AZUL_HOVER);
    btnBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

    btnBuscar.addActionListener(e -> seleccionarBusqueda());
    campoBusqueda.addActionListener(e -> seleccionarBusqueda());

    inicializarGrid();

    card.add(titulo);
    card.add(Box.createVerticalStrut(12));
    card.add(campoBusqueda);
    card.add(Box.createVerticalStrut(10));
    card.add(btnBuscar);
    card.add(Box.createVerticalStrut(14));
    card.add(scrollGrid);
    return card;
  }

  private void inicializarGrid() {
    panelGrid = new JPanel(new GridLayout(0, 3, 10, 10)) {
      @Override
      public Dimension getPreferredSize() {
        int rows = (int) Math.ceil((double) getComponentCount() / 3);
        return new Dimension(getParent() != null ? getParent().getWidth() : 0, rows * 100);
      }

    };
    panelGrid.setOpaque(false);
    scrollGrid = new JScrollPane(panelGrid);
    configurarScroll(scrollGrid);
    construirGrid(catalogoProductos);
  }

  private void configurarScroll(JScrollPane scroll) {
    scroll.setAlignmentX(LEFT_ALIGNMENT);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setPreferredSize(new Dimension(0, 380));
  }

  private JLabel crearLabelTitulo(String texto) {
    JLabel l = new JLabel(texto);
    l.setFont(new Font("Segoe UI", Font.BOLD, 17));
    l.setForeground(Colores.TEXTO_OSCURO);
    l.setAlignmentX(LEFT_ALIGNMENT);
    return l;
  }

  private void construirGrid(List<ProductoDTO> lista) {
    panelGrid.removeAll();
    lista.forEach(p -> panelGrid.add(crearBotonProducto(p)));
    panelGrid.revalidate();
    panelGrid.repaint();
    if (scrollGrid != null) {
      scrollGrid.revalidate();
    }
  }

  private JPanel crearBotonProducto(ProductoDTO prod) {
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
            seleccionarProducto(prod);
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarBotonRedondeado((Graphics2D) g2d, getWidth(), getHeight(), hover ? Colores.AZUL_HOVER : Colores.AZUL);
        super.paintComponent(g2d);
      }

    };
    return configurarContenidoBotonProducto(btn, prod);
  }

  private JPanel configurarContenidoBotonProducto(JPanel btn, ProductoDTO prod) {
    btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
    btn.setBorder(new EmptyBorder(12, 10, 12, 10));
    btn.setPreferredSize(new Dimension(0, 90));

    JLabel lCod = crearLabelProducto(prod.getCodigo(), 10, Colores.AZUL_MUY_SUTIL, Font.PLAIN);
    JLabel lNom = crearLabelProducto(prod.getNombre(), 16, Colores.BLANCO, Font.BOLD);
    JLabel lPre = crearLabelProducto("$" + String.format("%.2f", prod.getPrecio()), 13, Colores.AZUL_MUY_SUTIL, Font.PLAIN);

    btn.add(lCod);
    btn.add(Box.createVerticalStrut(4));
    btn.add(lNom);
    btn.add(Box.createVerticalStrut(4));
    btn.add(lPre);
    return btn;
  }

  private JLabel crearLabelProducto(String t, int s, Color c, int style) {
    JLabel l = new JLabel(t);
    l.setFont(new Font("Segoe UI", style, s));
    l.setForeground(c);
    l.setAlignmentX(CENTER_ALIGNMENT);
    return l;
  }

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
    JPanel card = crearTarjeta();
    card.setLayout(new BorderLayout(0, 12));

    lblCantItems = crearBadgeCantidad();
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.add(crearLabelTitulo("Carrito"), BorderLayout.WEST);
    header.add(lblCantItems, BorderLayout.EAST);

    panelCarritoItems = new JPanel();
    panelCarritoItems.setLayout(new BoxLayout(panelCarritoItems, BoxLayout.Y_AXIS));
    panelCarritoItems.setOpaque(false);

    JScrollPane scroll = new JScrollPane(panelCarritoItems);
    configurarScroll(scroll);
    scroll.setPreferredSize(new Dimension(0, 320));

    card.add(header, BorderLayout.NORTH);
    card.add(scroll, BorderLayout.CENTER);
    return card;
  }

  private JLabel crearBadgeCantidad() {
    JLabel l = new JLabel("0 items");
    l.setFont(new Font("Segoe UI", Font.BOLD, 12));
    l.setForeground(Colores.AZUL);
    l.setOpaque(true);
    l.setBackground(Colores.AZUL_CLARO);
    l.setBorder(new EmptyBorder(3, 10, 3, 10));
    return l;
  }

  private JPanel tarjetaTotal() {
    JPanel card = crearTarjetaBlanca(18);
    card.setLayout(new BorderLayout(0, 12));
    card.setBorder(new EmptyBorder(20, 20, 20, 20));

    JLabel lblTitulo = new JLabel("Total a pagar", SwingConstants.CENTER);
    lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    lblTitulo.setForeground(Colores.GRIS_TEXTO);

    card.add(lblTitulo, BorderLayout.NORTH);
    card.add(cuadroTotalAzul(), BorderLayout.CENTER);
    card.add(botonesVenta(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel cuadroTotalAzul() {
    lblTotal = new JLabel("$0.00", SwingConstants.CENTER);
    lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 38));
    lblTotal.setForeground(Colores.BLANCO);

    lblProductosCount = new JLabel("0 productos", SwingConstants.CENTER);
    lblProductosCount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lblProductosCount.setForeground(Colores.AZUL_SUTIL);

    JPanel cuadro = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarBotonRedondeado((Graphics2D) g2d, getWidth(), getHeight(), Colores.AZUL);
        super.paintComponent(g2d);
      }

    };
    cuadro.setOpaque(false);
    cuadro.setBorder(new EmptyBorder(18, 14, 18, 14));
    cuadro.setPreferredSize(new Dimension(0, 110));
    cuadro.add(lblTotal);
    cuadro.add(lblProductosCount);
    return cuadro;
  }

  private JPanel botonesVenta() {
    JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
    row.setOpaque(false);
    row.setPreferredSize(new Dimension(0, 58));

    JButton btnCancelar = crearBotonAccion("Cancelar", Colores.ROJO, Colores.ROJO_HOVER);
    btnCancelar.addActionListener(e -> seleccionarCancelarVenta());

    JButton btnPagar = crearBotonAccion("Pagar", Colores.VERDE, Colores.VERDE_OSCURO);
    btnPagar.addActionListener(e -> seleccionarPagar());

    row.add(btnCancelar);
    row.add(btnPagar);
    return row;
  }

  private JPanel filaCarrito(ItemVentaDTO item) {
    JPanel fila = crearContenedorFila();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;

    JLabel lblNombre = new JLabel(item.getNombre());
    lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1.0;
    fila.add(lblNombre, c);

    JButton btnElim = crearBotonIcono("X", Colores.ROJO_BG, Colores.ROJO_BG_HOVER, Colores.ROJO_ICONO, 11);
    btnElim.addActionListener(e -> seleccionarEliminarItem(item));
    c.gridx = 1;
    c.weightx = 0;
    fila.add(btnElim, c);

    JLabel lblPrecio = new JLabel("Precio unitario: $" + String.format("%.2f", item.getPrecioUnitario()));
    lblPrecio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    fila.add(lblPrecio, c);

    fila.add(crearControlesCantidad(item), c);
    return fila;
  }

  private JPanel crearContenedorFila() {
    JPanel fila = new JPanel(new GridBagLayout());
    fila.setOpaque(true);
    fila.setBackground(Colores.BLANCO);
    fila.setBorder(BorderFactory.createCompoundBorder(
      new LineBorder(Colores.BORDE_GRIS, 1, true),
      new EmptyBorder(10, 12, 10, 12)));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
    return fila;
  }

  private JPanel crearControlesCantidad(ItemVentaDTO item) {
    JButton btnMenos = crearBotonIcono("-", Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO, 17);
    JLabel lblCant = new JLabel(String.valueOf(item.getCantidad()), SwingConstants.CENTER);
    JButton btnMas = crearBotonIcono("+", Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO, 17);

    btnMenos.addActionListener(e -> seleccionarDecrementarItem(item));
    btnMas.addActionListener(e -> seleccionarIncrementarItem(item));

    JPanel ctrlCant = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    ctrlCant.setOpaque(false);
    ctrlCant.add(btnMenos);
    ctrlCant.add(lblCant);
    ctrlCant.add(btnMas);

    JLabel lblSub = new JLabel(String.format("$%.2f", item.getSubtotal()));
    lblSub.setFont(new Font("Segoe UI", Font.BOLD, 15));
    lblSub.setForeground(Colores.AZUL);

    JPanel ctrlRow = new JPanel(new BorderLayout());
    ctrlRow.setOpaque(false);
    ctrlRow.add(ctrlCant, BorderLayout.WEST);
    ctrlRow.add(lblSub, BorderLayout.EAST);
    return ctrlRow;
  }

  private void seleccionarMenuPrincipal() {
    dispose();
    new MenuPrincipal(usuarioActivo, usuariosFachada, ventasFachada, inventarioFachada, proveedoresFachada).setVisible(true);
  }

  private void seleccionarEscanearCodigo() {
    String codigo = campoEscanear.getText().trim();
    if (!codigo.isEmpty()) {
      escanearProducto(codigo);
      campoEscanear.setText("");
    }
  }

  private void seleccionarBusqueda() {
    filtrarGrid(campoBusqueda.getText().trim());
  }

  private void seleccionarProducto(ProductoDTO prod) {
    escanearProducto(prod.getCodigo());
  }

  private void seleccionarIncrementarItem(ItemVentaDTO item) {
    escanearProducto(item.getCodigo());
  }

  private void seleccionarDecrementarItem(ItemVentaDTO item) {
    List<ItemVentaDTO> items = ventaActual.getItems();
    if (item.getCantidad() > 1) {
      int idx = items.indexOf(item);
      if (idx >= 0) {
        items.set(idx, item.conCantidad(item.getCantidad() - 1));
      }
    } else {
      items.removeIf(i -> i.getCodigo().equalsIgnoreCase(item.getCodigo()));
    }
    recalcularTotalesVenta();
    actualizarVista();
  }

  private void seleccionarEliminarItem(ItemVentaDTO item) {
    ventaActual.getItems().removeIf(i -> i.getCodigo().equalsIgnoreCase(item.getCodigo()));
    recalcularTotalesVenta();
    actualizarVista();
  }

  private void seleccionarCancelarVenta() {
    if (ventaActual.getItems().isEmpty()) {
      return;
    }
    int op = JOptionPane.showConfirmDialog(this, "¿Cancelar la venta?", "Confirmar", JOptionPane.YES_NO_OPTION);
    if (op == JOptionPane.YES_OPTION) {
      ventaActual.getItems().clear();
      recalcularTotalesVenta();
      actualizarVista();
    }
  }

  private void seleccionarPagar() {
    if (ventaActual.getItems().isEmpty()) {
      mostrarAviso("El carrito esta vacio.", "Aviso");
      return;
    }
    this.setVisible(false);
    new SeleccionarMetodoPago(this, ventaActual, ventaActual.getTotal(), () -> {
      ventaActual = new VentaDTO();
      actualizarVista();
      refrescarCatalogo();
    }, usuariosFachada, inventarioFachada, proveedoresFachada, ventasFachada, usuarioActivo);
  }

  private void escanearProducto(String codigo) {
    EscanearProductoDTO dto = new EscanearProductoDTO(codigo);
    if (!ventasFachada.existeProducto(dto)) {
      mostrarError("El producto no existe.", "Error");
      return;
    }
    if (!ventasFachada.tieneStock(dto)) {
      mostrarAviso("Sin stock disponible.", "Aviso");
      return;
    }
    ventasFachada.procesarProducto(ventaActual, dto);
    actualizarVista();
  }

  private void filtrarGrid(String query) {
    if (query.isEmpty()) {
      construirGrid(catalogoProductos);
      return;
    }
    String q = query.toLowerCase();
    construirGrid(catalogoProductos.stream()
      .filter(p -> p.getNombre().toLowerCase().contains(q) || p.getCodigo().toLowerCase().contains(q))
      .collect(Collectors.toList()));
  }

  private void recalcularTotalesVenta() {
    List<ItemVentaDTO> items = ventaActual.getItems();
    BigDecimal total = items.stream().map(ItemVentaDTO::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    BigDecimal subtotal = total.divide(BigDecimal.valueOf(1.16), 2, RoundingMode.HALF_UP);
    BigDecimal iva = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
    int totalUnidades = items.stream().mapToInt(ItemVentaDTO::getCantidad).sum();

    ventaActual.setTotal(total);
    ventaActual.setSubtotal(subtotal);
    ventaActual.setIva(iva);
    ventaActual.setTotalUnidades(totalUnidades);
  }

  private void actualizarVista() {
    panelCarritoItems.removeAll();
    ventaActual.getItems().forEach(item -> {
      panelCarritoItems.add(filaCarrito(item));
      panelCarritoItems.add(Box.createVerticalStrut(8));
    });
    panelCarritoItems.revalidate();
    panelCarritoItems.repaint();

    lblTotal.setText(String.format("$%.2f", ventaActual.getTotal()));
    lblCantItems.setText(ventaActual.getTotalUnidades() + " items");
    lblProductosCount.setText(ventaActual.getTotalUnidades() + " productos");
  }

  private void refrescarCatalogo() {
    catalogoProductos = ventasFachada.obtenerCatalogo();
    filtrarGrid(campoBusqueda != null ? campoBusqueda.getText().trim() : "");
  }

  private void mostrarError(String msg, String titulo) {
    JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.ERROR_MESSAGE);
  }

  private void mostrarAviso(String msg, String titulo) {
    JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.WARNING_MESSAGE);
  }

  private JPanel crearTarjetaBlanca(int radio) {
    JPanel p = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, radio, radio));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, radio, radio));
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    return p;
  }

  private JPanel crearTarjeta() {
    JPanel p = crearTarjetaBlanca(18);
    p.setBorder(new EmptyBorder(20, 20, 20, 20));
    return p;
  }

  private JTextField crearCampoPill(String placeholder) {
    JTextField tf = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarBotonRedondeado((Graphics2D) g2d, getWidth(), getHeight(), Colores.FONDO_INPUT);
        super.paintComponent(g2d);
      }

    };
    tf.setOpaque(false);
    tf.setBorder(new EmptyBorder(8, 14, 8, 14));
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

  private JButton crearBotonAccion(String texto, Color base, Color hover) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        configurarEstiloBotonBase(this);
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
        dibujarBotonRedondeado((Graphics2D) g2d, getWidth(), getHeight(), ov ? hover : base);
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(new Font("Segoe UI", Font.BOLD, 14));
    return b;
  }

  private JButton crearBotonIcono(String texto, Color baseBg, Color hoverBg, Color colorTexto, int fontSize) {
    JButton b = new JButton(texto) {
      boolean ov = false;

      {
        configurarEstiloBotonBase(this);
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
        g.setColor(ov ? hoverBg : baseBg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(colorTexto);
    b.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
    b.setPreferredSize(new Dimension(32, 32));
    return b;
  }

}
