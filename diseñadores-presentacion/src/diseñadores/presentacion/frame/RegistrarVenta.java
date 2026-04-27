package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.negocios.ventas.IVentas;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Componentes;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrarVenta extends JFrame {

  private VentaDTO ventaActual;

  private final IVentas fachada;

  private final List<ItemVentaDTO> carritoDisplay = new ArrayList<>();

  private List<ProductoDTO> catalogoProductos = new ArrayList<>();

  private JPanel panelCarritoItems;
  private JLabel lblTotal, lblCantItems, lblProductosCount;
  private JTextField campoBusqueda, campoEscanear;
  private JPanel panelGrid;
  private JScrollPane scrollGrid;

  public RegistrarVenta(IVentas facade) {
    super("Punto de Venta");
    this.fachada = facade;

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1350, 780);
    setLocationRelativeTo(null);
    setResizable(true);

    ventaActual = fachada.iniciarNuevaVenta();
    catalogoProductos = fachada.obtenerCatalogo();

    JPanel root = Componentes.fondoAmarillo();
    root.add(Componentes.topBar(this), BorderLayout.NORTH);

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

    root.add(centro, BorderLayout.CENTER);
    setContentPane(root);
    actualizarVista();
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
    JPanel card = Componentes.tarjeta();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

    JLabel titulo = Componentes.etiqueta("Escanear Producto", 17, true, Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);

    campoEscanear = Componentes.campoPill("Escanear codigo de barras");
    campoEscanear.setAlignmentX(LEFT_ALIGNMENT);
    campoEscanear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    campoEscanear.addActionListener(e -> {
      String codigo = campoEscanear.getText().trim();
      if (!codigo.isEmpty()) {
        escanearProducto(codigo);
        campoEscanear.setText("");
      }
    });

    card.add(titulo);
    card.add(Box.createVerticalStrut(14));
    card.add(campoEscanear);
    return card;
  }

  private JPanel tarjetaBusqueda() {
    JPanel card = Componentes.tarjeta();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

    JLabel titulo = Componentes.etiqueta("Busqueda Rapida", 17, true, Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);

    campoBusqueda = Componentes.campoPill("Nombre del producto");
    campoBusqueda.setAlignmentX(LEFT_ALIGNMENT);
    campoBusqueda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

    JButton btnBuscar = Componentes.botonAccion("Buscar", Colores.AZUL, Colores.AZUL_HOVER);
    btnBuscar.setAlignmentX(LEFT_ALIGNMENT);
    btnBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

    Runnable buscar = () -> filtrarGrid(campoBusqueda.getText().trim());
    btnBuscar.addActionListener(e -> buscar.run());
    campoBusqueda.addActionListener(e -> buscar.run());

    panelGrid = new JPanel(new GridLayout(0, 3, 10, 10)) {
      @Override
      public Dimension getPreferredSize() {
        int cols = 3;
        int count = getComponentCount();
        int rows = (int) Math.ceil((double) count / cols);
        int btnH = 90;
        int gap = 10;
        int height = rows * btnH + Math.max(0, rows - 1) * gap;
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

    construirGrid(catalogoProductos);

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
    if (scrollGrid != null) {
      scrollGrid.revalidate();
    }
  }

  private void refrescarCatalogo() {
    catalogoProductos = fachada.obtenerCatalogo();
    filtrarGrid(campoBusqueda != null ? campoBusqueda.getText().trim() : "");
  }

  private void filtrarGrid(String query) {
    if (query.isEmpty()) {
      construirGrid(catalogoProductos);
      return;
    }
    String q = query.toLowerCase();
    construirGrid(catalogoProductos.stream()
      .filter(p -> p.getNombre().toLowerCase().contains(q)
      || p.getCodigo().toLowerCase().contains(q))
      .collect(Collectors.toList()));
  }

  private JPanel botonProducto(ProductoDTO prod) {
    JPanel btn = new JPanel() {
      boolean hover = false;

      {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
          @Override
          public void mouseEntered(java.awt.event.MouseEvent e) {
            hover = true;
            repaint();
          }

          @Override
          public void mouseExited(java.awt.event.MouseEvent e) {
            hover = false;
            repaint();
          }

          @Override
          public void mouseClicked(java.awt.event.MouseEvent e) {
            escanearProducto(prod.getCodigo());
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
    btn.setPreferredSize(new Dimension(0, 90));

    JLabel lCod = Componentes.etiqueta(prod.getCodigo(), 10, false, Colores.AZUL_MUY_SUTIL);
    JLabel lNom = Componentes.etiqueta(prod.getNombre(), 16, true, Colores.BLANCO);
    JLabel lPre = Componentes.etiqueta("$" + String.format("%.2f", prod.getPrecio()), 13, false, Colores.AZUL_MUY_SUTIL);

    for (JLabel l : new JLabel[]{lCod, lNom, lPre}) {
      l.setAlignmentX(CENTER_ALIGNMENT);
      btn.add(l);
      if (l == lCod || l == lNom) {
        btn.add(Box.createVerticalStrut(4));
      }
    }
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
    JPanel card = Componentes.tarjeta();
    card.setLayout(new BorderLayout(0, 12));

    lblCantItems = Componentes.etiqueta("0 items", 12, true, Colores.AZUL);
    lblCantItems.setOpaque(true);
    lblCantItems.setBackground(Colores.AZUL_CLARO);
    lblCantItems.setBorder(new EmptyBorder(3, 10, 3, 10));

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.add(Componentes.etiqueta("Carrito", 17, true, Colores.TEXTO_OSCURO), BorderLayout.WEST);
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
    JPanel card = Componentes.tarjetaBlanca(18);
    card.setLayout(new BorderLayout(0, 12));
    card.setBorder(new EmptyBorder(20, 20, 20, 20));
    card.add(Componentes.etiquetaCentrada("Total a pagar", 15, false, Colores.GRIS_TEXTO), BorderLayout.NORTH);
    card.add(cuadroTotalAzul(), BorderLayout.CENTER);
    card.add(botonesVenta(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel cuadroTotalAzul() {
    lblTotal = Componentes.etiquetaCentrada("$0.00", 38, true, Colores.BLANCO);
    lblProductosCount = Componentes.etiquetaCentrada("0 productos", 13, false, Colores.AZUL_SUTIL);

    JPanel cuadro = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
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

    JButton btnCancelar = Componentes.botonAccion("Cancelar", Colores.ROJO, Colores.ROJO_HOVER);
    btnCancelar.addActionListener(e -> {
      if (ventaActual.getItems().isEmpty()) {
        return;
      }
      int op = JOptionPane.showConfirmDialog(this,
        "¿Cancelar la venta y vaciar el carrito?", "Cancelar venta",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (op == JOptionPane.YES_OPTION) {
        cancelarVenta();
      }
    });

    JButton btnContinuar = Componentes.botonAccion("Pagar", Colores.VERDE, Colores.VERDE_OSCURO);
    btnContinuar.addActionListener(e -> continuarAPago());

    row.add(btnCancelar);
    row.add(btnContinuar);
    return row;
  }

  private void escanearProducto(String codigo) {
    EscanearProductoDTO dto = new EscanearProductoDTO(codigo);

    if (!fachada.existeProducto(dto)) {
      mostrarError("<html>El producto <b>" + codigo + "</b> no existe en el catalogo.</html>",
        "Producto no encontrado");
      return;
    }
    if (!fachada.tieneStock(dto)) {
      mostrarAviso("<html>El producto <b>" + codigo + "</b> no tiene unidades disponibles.</html>",
        "Sin stock");
      return;
    }

    ProductoDTO p = fachada.procesarProducto(ventaActual, dto);
    if (p == null) {
      return;
    }

    actualizarVista();
  }

  private void incrementarItem(ItemVentaDTO item) {
    EscanearProductoDTO dto = new EscanearProductoDTO(item.getCodigo());
    if (!fachada.tieneStock(dto)) {
      mostrarAviso("<html>No hay mas stock de <b>" + item.getNombre() + "</b>.</html>", "Sin stock");
      return;
    }
    fachada.procesarProducto(ventaActual, dto);
    actualizarVista();
  }

  private void decrementarItem(ItemVentaDTO item) {
    if (item.getCantidad() > 1) {
      ItemVentaDTO itemEnVenta = ventaActual.getItems().stream()
        .filter(i -> i.getCodigo().equals(item.getCodigo()))
        .findFirst().orElse(null);

      if (itemEnVenta != null) {
        int index = ventaActual.getItems().indexOf(itemEnVenta);
        ventaActual.getItems().set(index, itemEnVenta.conCantidad(itemEnVenta.getCantidad() - 1));
      }
    } else {
      ventaActual.getItems().removeIf(i -> i.getCodigo().equals(item.getCodigo()));
    }
    actualizarVista();
  }

  private void eliminarItem(ItemVentaDTO item) {
    ventaActual.getItems().removeIf(i -> i.getCodigo().equalsIgnoreCase(item.getCodigo()));
    actualizarVista();
  }

  private void cancelarVenta() {
    ventaActual = fachada.iniciarNuevaVenta();
    actualizarVista();
  }

  private void continuarAPago() {
    if (ventaActual.getItems().isEmpty()) {
      mostrarAviso("El carrito esta vacio.", "Sin productos");
      return;
    }
    double total = ventaActual.getTotal();
    this.setVisible(false);
    SeleccionarMetodoPago seleccionarMetodoPago = new SeleccionarMetodoPago(this, fachada, ventaActual, total, () -> {
      ventaActual = fachada.iniciarNuevaVenta();
      actualizarVista();
      refrescarCatalogo();
    });
  }

  private void actualizarVista() {
    panelCarritoItems.removeAll();
    for (ItemVentaDTO item : ventaActual.getItems()) {
      panelCarritoItems.add(filaCarrito(item));
      panelCarritoItems.add(Box.createVerticalStrut(8));
    }
    panelCarritoItems.revalidate();
    panelCarritoItems.repaint();

    lblTotal.setText(String.format("$%.2f", ventaActual.getTotal()));
    lblCantItems.setText(ventaActual.getTotalUnidades() + " items");
    lblProductosCount.setText(ventaActual.getTotalUnidades() + " productos");
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

    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    fila.add(Componentes.etiqueta(item.getNombre(), 14, true, Colores.TEXTO_OSCURO), c);

    JButton btnElim = Componentes.botonIcono("X", Colores.ROJO_BG, Colores.ROJO_BG_HOVER, Colores.ROJO_ICONO, 11);
    btnElim.addActionListener(e -> eliminarItem(item));
    c.gridx = 1;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    fila.add(btnElim, c);

    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 1;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 2;
    fila.add(Componentes.etiqueta("Precio unitario: $" + String.format("%.2f", item.getPrecioUnitario()), 11, false, Colores.GRIS_TEXTO), c);

    JButton btnMenos = Componentes.botonIcono("-", Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO, 17);
    JLabel lblCant = Componentes.etiquetaCentrada(String.valueOf(item.getCantidad()), 15, true, Colores.TEXTO_OSCURO);
    lblCant.setPreferredSize(new Dimension(32, 32));
    JButton btnMas = Componentes.botonIcono("+", Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO, 17);

    btnMenos.addActionListener(e -> decrementarItem(item));
    btnMas.addActionListener(e -> incrementarItem(item));

    JPanel ctrlCant = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    ctrlCant.setOpaque(false);
    ctrlCant.add(btnMenos);
    ctrlCant.add(lblCant);
    ctrlCant.add(btnMas);

    JLabel lblSubTxt = Componentes.etiqueta("Subtotal", 10, false, Colores.GRIS_TEXTO);
    lblSubTxt.setHorizontalAlignment(SwingConstants.RIGHT);
    JLabel lblSub = Componentes.etiqueta(String.format("$%.2f", item.getSubtotal()), 15, true, Colores.AZUL);
    lblSub.setHorizontalAlignment(SwingConstants.RIGHT);

    JPanel subPanel = new JPanel(new BorderLayout());
    subPanel.setOpaque(false);
    subPanel.add(lblSubTxt, BorderLayout.NORTH);
    subPanel.add(lblSub, BorderLayout.SOUTH);

    JPanel ctrlRow = new JPanel(new BorderLayout(8, 0));
    ctrlRow.setOpaque(false);
    ctrlRow.add(ctrlCant, BorderLayout.WEST);
    ctrlRow.add(subPanel, BorderLayout.EAST);

    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(6, 0, 0, 0);
    fila.add(ctrlRow, c);
    return fila;
  }

  private void mostrarError(String msg, String titulo) {
    JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.ERROR_MESSAGE);
  }

  private void mostrarAviso(String msg, String titulo) {
    JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.WARNING_MESSAGE);
  }

}
