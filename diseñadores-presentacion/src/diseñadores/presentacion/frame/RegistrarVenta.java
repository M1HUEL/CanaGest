package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
import diseñadores.negocios.usuarios.IUsuarios;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class RegistrarVenta extends JFrame {

  private final IVentas ventasFachada;
  private final IUsuarios usuariosFachada;
  private final IInventario inventarioFachada;
  private final IProveedores proveedoresFachada;
  private final UsuarioDTO usuarioActivo;
  private final VentasControl control;

  private JPanel panelCarritoItems;
  private JPanel panelGrid;
  private JScrollPane scrollGrid;
  private JLabel lblTotal, lblCantItems, lblProductosCount;
  private JTextField campoBusqueda, campoEscanear;

  public RegistrarVenta(
    IVentas ventasFachada,
    UsuarioDTO usuarioActivo,
    IUsuarios usuariosFachada,
    IInventario inventarioFachada,
    IProveedores proveedoresFachada) {

    super("Punto de Venta");
    Fuentes.cargar();

    this.ventasFachada = ventasFachada;
    this.usuariosFachada = usuariosFachada;
    this.inventarioFachada = inventarioFachada;
    this.proveedoresFachada = proveedoresFachada;
    this.usuarioActivo = usuarioActivo;
    this.control = new VentasControl(ventasFachada);

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1350, 780);
    setLocationRelativeTo(null);
    setResizable(true);

    construirUI();
    actualizarVista();
  }

  private void construirUI() {
    JPanel root = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
    root.setOpaque(false);

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
    centro.add(panelIzquierdo(), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.35;
    gbc.insets = new Insets(0, 0, 0, 0);
    centro.add(panelDerecho(), gbc);

    root.add(topBar(), BorderLayout.NORTH);
    root.add(centro, BorderLayout.CENTER);
    setContentPane(root);
  }

  private JPanel topBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));

    JButton btn = boton("Menu Principal", Colores.AMARILLO_BTN, Colores.AMARILLO_BTN_HOVER);
    btn.setForeground(Colores.TEXTO_OSCURO);
    btn.setFont(Fuentes.b(13));
    btn.setPreferredSize(new Dimension(160, 38));
    btn.addActionListener(e -> {
      dispose();
      new MenuPrincipal(usuarioActivo, usuariosFachada, ventasFachada,
        inventarioFachada, proveedoresFachada).setVisible(true);
    });
    bar.add(btn);
    return bar;
  }

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

    JLabel titulo = labelTitulo("Escanear Producto");

    campoEscanear = campoPill("Escanear código de barras");
    campoEscanear.setAlignmentX(LEFT_ALIGNMENT);
    campoEscanear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    campoEscanear.addActionListener(e -> {
      String codigo = campoEscanear.getText().trim();
      if (!codigo.isEmpty()) {
        procesarEscaneoUI(codigo);
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

    JLabel titulo = labelTitulo("Búsqueda Rápida");

    campoBusqueda = campoPill("Nombre del producto");
    campoBusqueda.setAlignmentX(LEFT_ALIGNMENT);
    campoBusqueda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

    JButton btnBuscar = botonAccion("Buscar", Colores.AZUL, Colores.AZUL_HOVER);
    btnBuscar.setAlignmentX(LEFT_ALIGNMENT);
    btnBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

    Runnable buscar = () -> {
      String q = campoBusqueda.getText().trim();
      boolean esPlaceholder = q.equals("Nombre del producto");
      construirGrid(control.filtrarCatalogo(esPlaceholder ? "" : q));
    };
    btnBuscar.addActionListener(e -> buscar.run());
    campoBusqueda.addActionListener(e -> buscar.run());

    panelGrid = new JPanel(new GridLayout(0, 3, 10, 10)) {
      @Override
      public Dimension getPreferredSize() {
        int rows = (int) Math.ceil((double) getComponentCount() / 3);
        int height = Math.max(rows, 1) * 100 + Math.max(rows - 1, 0) * 10;
        return new Dimension(getParent() != null ? getParent().getWidth() : 0, height);
      }

    };
    panelGrid.setOpaque(false);
    panelGrid.setAlignmentX(LEFT_ALIGNMENT);

    scrollGrid = new JScrollPane(panelGrid);
    scrollGrid.setAlignmentX(LEFT_ALIGNMENT);
    scrollGrid.setBorder(BorderFactory.createEmptyBorder());
    scrollGrid.setOpaque(false);
    scrollGrid.getViewport().setOpaque(false);
    scrollGrid.getVerticalScrollBar().setUnitIncrement(16);
    scrollGrid.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollGrid.setPreferredSize(new Dimension(0, 380));

    construirGrid(control.getCatalogo());

    card.add(titulo);
    card.add(Box.createVerticalStrut(12));
    card.add(campoBusqueda);
    card.add(Box.createVerticalStrut(10));
    card.add(btnBuscar);
    card.add(Box.createVerticalStrut(14));
    card.add(scrollGrid);
    return card;
  }

  private void construirGrid(List<ProductoDTO> lista) {
    panelGrid.removeAll();
    lista.forEach(p -> panelGrid.add(botonProducto(p)));
    panelGrid.revalidate();
    panelGrid.repaint();
    scrollGrid.revalidate();
  }

  private JPanel botonProducto(ProductoDTO prod) {
    JPanel btn = new JPanel() {
      boolean hov = false;

      {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            hov = true;
            repaint();
          }

          public void mouseExited(MouseEvent e) {
            hov = false;
            repaint();
          }

          public void mouseClicked(MouseEvent e) {
            procesarEscaneoUI(prod.getCodigo());
          }

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        pintarRedondeado((Graphics2D) g2d, getWidth(), getHeight(), Colores.AZUL, 14);
        super.paintComponent(g2d);
      }

    };
    btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));
    btn.setBorder(new EmptyBorder(12, 10, 12, 10));
    btn.setPreferredSize(new Dimension(0, 90));

    JLabel lCod = labelProducto(prod.getCodigo(), 10, Colores.AZUL_MUY_SUTIL, Font.PLAIN);
    JLabel lNom = labelProducto(prod.getNombre(), 16, Colores.BLANCO, Font.BOLD);
    JLabel lPre = labelProducto("$" + String.format("%.2f", prod.getPrecio()), 13, Colores.AZUL_MUY_SUTIL, Font.PLAIN);

    btn.add(lCod);
    btn.add(Box.createVerticalStrut(4));
    btn.add(lNom);
    btn.add(Box.createVerticalStrut(4));
    btn.add(lPre);
    return btn;
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
    JPanel card = tarjeta();
    card.setLayout(new BorderLayout(0, 12));

    lblCantItems = new JLabel("0 items");
    lblCantItems.setFont(Fuentes.b(12));
    lblCantItems.setForeground(Colores.AZUL);
    lblCantItems.setOpaque(true);
    lblCantItems.setBackground(Colores.AZUL_CLARO);
    lblCantItems.setBorder(new EmptyBorder(3, 10, 3, 10));

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.add(labelTitulo("Carrito"), BorderLayout.WEST);
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
    JPanel card = tarjetaBlanca(18);
    card.setLayout(new BorderLayout(0, 12));
    card.setBorder(new EmptyBorder(20, 20, 20, 20));

    JLabel lblTitulo = new JLabel("Total a pagar", SwingConstants.CENTER);
    lblTitulo.setFont(Fuentes.r(15));
    lblTitulo.setForeground(Colores.GRIS_TEXTO);

    card.add(lblTitulo, BorderLayout.NORTH);
    card.add(cuadroTotalAzul(), BorderLayout.CENTER);
    card.add(botonesVenta(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel cuadroTotalAzul() {
    lblTotal = new JLabel("$0.00", SwingConstants.CENTER);
    lblTotal.setFont(Fuentes.b(38));
    lblTotal.setForeground(Colores.BLANCO);

    lblProductosCount = new JLabel("0 productos", SwingConstants.CENTER);
    lblProductosCount.setFont(Fuentes.r(13));
    lblProductosCount.setForeground(Colores.AZUL_SUTIL);

    JPanel cuadro = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        pintarRedondeado((Graphics2D) g2d, getWidth(), getHeight(), Colores.AZUL, 14);
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

    JButton btnCancelar = botonAccion("Cancelar", Colores.ROJO, Colores.ROJO_HOVER);
    btnCancelar.addActionListener(e -> {
      if (control.carritoVacio()) {
        return;
      }
      int op = JOptionPane.showConfirmDialog(this, "¿Cancelar la venta?",
        "Confirmar", JOptionPane.YES_NO_OPTION);
      if (op == JOptionPane.YES_OPTION) {
        control.cancelarVenta();
        actualizarVista();
      }
    });

    JButton btnPagar = botonAccion("Pagar", Colores.VERDE, Colores.VERDE_OSCURO);
    btnPagar.addActionListener(e -> irAPago());

    row.add(btnCancelar);
    row.add(btnPagar);
    return row;
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
    c.fill = GridBagConstraints.HORIZONTAL;

    JLabel lblNombre = new JLabel(item.getNombre());
    lblNombre.setFont(Fuentes.b(14));
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1.0;
    c.gridwidth = 1;
    fila.add(lblNombre, c);

    JButton btnElim = botonIcono("✕", Colores.ROJO_BG, Colores.ROJO_BG_HOVER, Colores.ROJO_ICONO, 11);
    btnElim.addActionListener(e -> {
      control.eliminarItem(item);
      actualizarVista();
    });
    c.gridx = 1;
    c.weightx = 0;
    fila.add(btnElim, c);

    JLabel lblPrecio = new JLabel("Precio unitario: $" + String.format("%.2f", item.getPrecioUnitario()));
    lblPrecio.setFont(Fuentes.r(11));
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    c.weightx = 1.0;
    c.insets = new Insets(3, 0, 0, 0);
    fila.add(lblPrecio, c);

    c.gridy = 2;
    c.insets = new Insets(6, 0, 0, 0);
    fila.add(controlesItem(item), c);

    return fila;
  }

  private JPanel controlesItem(ItemVentaDTO item) {
    JButton btnMenos = botonIcono("−", Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO, 17);
    JLabel lblCant = new JLabel(String.valueOf(item.getCantidad()), SwingConstants.CENTER);
    lblCant.setFont(Fuentes.b(15));
    lblCant.setPreferredSize(new Dimension(32, 32));
    JButton btnMas = botonIcono("+", Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO, 17);

    btnMenos.addActionListener(e -> {
      control.decrementarItem(item);
      actualizarVista();
    });
    btnMas.addActionListener(e -> {
      procesarEscaneoUI(item.getCodigo());
    });

    JPanel ctrlCant = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    ctrlCant.setOpaque(false);
    ctrlCant.add(btnMenos);
    ctrlCant.add(lblCant);
    ctrlCant.add(btnMas);

    JLabel lblSub = new JLabel(String.format("$%.2f", item.getSubtotal()));
    lblSub.setFont(Fuentes.b(12));
    lblSub.setForeground(Colores.AZUL);

    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    row.add(ctrlCant, BorderLayout.WEST);
    row.add(lblSub, BorderLayout.EAST);
    return row;
  }

  private void procesarEscaneoUI(String codigo) {
    VentasControl.ResultadoEscaneo resultado = control.procesarEscaneo(codigo);
    switch (resultado) {
      case NO_EXISTE ->
        JOptionPane.showMessageDialog(this,
          "El producto no existe.", "Error", JOptionPane.ERROR_MESSAGE);
      case SIN_STOCK ->
        JOptionPane.showMessageDialog(this,
          "Sin stock disponible.", "Aviso", JOptionPane.WARNING_MESSAGE);
      case OK ->
        actualizarVista();
    }
  }

  private void irAPago() {
    if (control.carritoVacio()) {
      JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Aviso",
        JOptionPane.WARNING_MESSAGE);
      return;
    }
    this.setVisible(false);
    new SeleccionarMetodoPago(
      this,
      control.getVentaActual(),
      control.getVentaActual().getTotal(),
      () -> {
        control.iniciarNuevaVenta();
        actualizarVista();
        List<ProductoDTO> catalogo = control.refrescarCatalogo();
        construirGrid(catalogo);
      },
      usuariosFachada,
      inventarioFachada,
      proveedoresFachada,
      ventasFachada,
      usuarioActivo
    );
  }

  private void actualizarVista() {
    VentaDTO venta = control.getVentaActual();

    panelCarritoItems.removeAll();
    venta.getItems().forEach(item -> {
      panelCarritoItems.add(filaCarrito(item));
      panelCarritoItems.add(Box.createVerticalStrut(8));
    });
    panelCarritoItems.revalidate();
    panelCarritoItems.repaint();

    lblTotal.setText(String.format("$%.2f", venta.getTotal()));
    lblCantItems.setText(venta.getTotalUnidades() + " items");
    lblProductosCount.setText(venta.getTotalUnidades() + " productos");
  }

  private JPanel tarjetaBlanca(int radio) {
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

  private JPanel tarjeta() {
    JPanel p = tarjetaBlanca(18);
    p.setBorder(new EmptyBorder(20, 20, 20, 20));
    return p;
  }

  private JTextField campoPill(String placeholder) {
    JTextField tf = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        pintarRedondeado((Graphics2D) g2d, getWidth(), getHeight(), Colores.FONDO_INPUT, 10);
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

  private JButton boton(String texto, Color base, Color hover) {
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

      protected void paintComponent(Graphics g2d) {
        pintarRedondeado((Graphics2D) g2d, getWidth(), getHeight(), ov ? hover : base, 10);
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(14));
    return b;
  }

  private JButton botonAccion(String texto, Color base, Color hover) {
    JButton b = boton(texto, base, hover);
    b.setFont(Fuentes.b(14));
    return b;
  }

  private JButton botonIcono(String texto, Color base, Color hover, Color colorTexto, int fs) {
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
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(colorTexto);
    b.setFont(Fuentes.b(fs));
    b.setPreferredSize(new Dimension(32, 32));
    return b;
  }

  private JLabel labelTitulo(String texto) {
    JLabel l = new JLabel(texto);
    l.setFont(Fuentes.b(17));
    l.setForeground(Colores.TEXTO_OSCURO);
    l.setAlignmentX(LEFT_ALIGNMENT);
    return l;
  }

  private JLabel labelProducto(String texto, int size, Color color, int style) {
    JLabel l = new JLabel(texto);
    l.setFont(style == Font.BOLD ? Fuentes.b(size) : Fuentes.r(size));
    l.setForeground(color);
    l.setAlignmentX(CENTER_ALIGNMENT);
    return l;
  }

  private void pintarRedondeado(Graphics2D g, int width, int height, Color color, int arc) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(color);
    g.fill(new RoundRectangle2D.Float(0, 0, width, height, arc, arc));
  }

}
