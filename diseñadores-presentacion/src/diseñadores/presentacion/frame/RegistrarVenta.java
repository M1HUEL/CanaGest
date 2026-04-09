package diseñadores.presentacion.frame;

import diseñadores.negocios.ventas.IVentas;
import diseñadores.negocios.ventas.VentasFacade;
import diseñadores.negocios.ventas.dominio.Producto;
import diseñadores.presentacion.util.Colores;
import diseñadores.presentacion.util.Fuentes;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class RegistrarVenta extends JFrame {

  private IVentas facade;

  List<ItemCarrito> carrito = new ArrayList<>();
  JPanel panelCarritoItems;
  JLabel lblTotal, lblCantItems, lblProductosCount;

  String[][] productos = {
    {"PROD-8342-2323", "Arroz", "28.00"},
    {"PROD-8342-2324", "Frijol", "32.00"},
    {"PROD-8342-2325", "Azúcar", "26.00"},
    {"PROD-8342-2326", "Aceite", "48.00"},
    {"PROD-8342-2327", "Atún", "18.00"},
    {"PROD-8342-2328", "Leche", "30.00"},
    {"PROD-8342-2329", "Sal", "8.00"},
    {"PROD-8342-2330", "Café", "55.00"},
    {"PROD-8342-2331", "Jabón", "22.00"},};

  JTextField campoBusqueda, campoEscanear;
  JPanel panelGrid;

  public RegistrarVenta() {
    setTitle("Punto de Venta");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1350, 780);
    setLocationRelativeTo(null);
    setResizable(true);

    facade = new VentasFacade();
    facade.nuevaVenta();

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
    g.insets = new Insets(0, 0, 0, 16);
    g.gridy = 0;
    g.weighty = 1.0;

    g.gridx = 0;
    g.weightx = 0.65;
    centro.add(panelIzquierdo(), g);

    g.gridx = 1;
    g.weightx = 0.35;
    g.insets = new Insets(0, 0, 0, 0);
    centro.add(panelDerecho(), g);

    root.add(centro, BorderLayout.CENTER);
    setContentPane(root);
    actualizarTotal();
  }

  JPanel panelIzquierdo() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setOpaque(false);
    p.add(tarjetaEscanear());
    p.add(Box.createVerticalStrut(16));
    p.add(tarjetaBusqueda());
    return p;
  }

  JPanel tarjetaEscanear() {
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

  JPanel tarjetaBusqueda() {
    JPanel card = tarjeta();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

    JLabel titulo = new JLabel("Búsqueda Rápida");
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
    construirGrid(productos);

    card.add(titulo);
    card.add(Box.createVerticalStrut(12));
    card.add(campoBusqueda);
    card.add(Box.createVerticalStrut(10));
    card.add(btnBuscar);
    card.add(Box.createVerticalStrut(14));
    card.add(panelGrid);
    return card;
  }

  void construirGrid(String[][] lista) {
    panelGrid.removeAll();
    for (String[] p : lista) {
      panelGrid.add(botonProducto(p[0], p[1], p[2]));
    }
    panelGrid.revalidate();
    panelGrid.repaint();
  }

  void filtrarGrid(String query) {
    if (query.isEmpty()) {
      construirGrid(productos);
      return;
    }
    List<String[]> filtrados = new ArrayList<>();
    for (String[] p : productos) {
      if (p[1].toLowerCase().contains(query.toLowerCase()) || p[0].toLowerCase().contains(query.toLowerCase())) {
        filtrados.add(p);
      }
    }
    construirGrid(filtrados.toArray(new String[0][]));
  }

  JPanel botonProducto(String codigo, String nombre, String precio) {
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

    JLabel lCod = label(codigo, 10, Font.PLAIN, Colores.AZUL_MUY_SUTIL);
    JLabel lNom = label(nombre, 16, Font.BOLD, Colores.BLANCO);
    JLabel lPre = label("$" + precio, 13, Font.PLAIN, Colores.AZUL_MUY_SUTIL);

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

  JPanel panelDerecho() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setOpaque(false);
    p.add(tarjetaCarrito());
    p.add(Box.createVerticalStrut(14));
    p.add(tarjetaTotal());
    return p;
  }

  JPanel tarjetaCarrito() {
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

  JPanel tarjetaTotal() {
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

    JButton btnCancelar = new JButton("<html><center><br>Cancelar</center></html>") {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? Colores.ROJO_HOVER : Colores.ROJO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    btnCancelar.setForeground(Colores.BLANCO);
    btnCancelar.setFont(Fuentes.b(13));
    btnCancelar.setHorizontalAlignment(SwingConstants.CENTER);
    btnCancelar.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this,
        "¿Cancelar la venta y vaciar el carrito?", "Cancelar venta",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (op == JOptionPane.YES_OPTION) {
        carrito.clear();
        facade.nuevaVenta();
        actualizarTotal();
      }
    });

    JButton btnContinuar = new JButton("<html><center><br>Continuar</center></html>") {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? Colores.VERDE_OSCURO : Colores.VERDE);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    btnContinuar.setForeground(Colores.BLANCO);
    btnContinuar.setFont(Fuentes.b(13));
    btnContinuar.setHorizontalAlignment(SwingConstants.CENTER);
    btnContinuar.addActionListener(e -> {
      if (carrito.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Sin productos", JOptionPane.WARNING_MESSAGE);
        return;
      }
      double total = facade.obtenerVentaActual().getSubtotalVenta();
      int totalItems = carrito.stream().mapToInt(i -> i.cantidad).sum();
      this.setVisible(false);
      new SeleccionarMetodoPago(this, facade, total, totalItems, new ArrayList<>(carrito), () -> {
        carrito.clear();
        facade.nuevaVenta();
        actualizarTotal();
      });
    });

    botonesRow.add(btnCancelar);
    botonesRow.add(btnContinuar);
    card.add(botonesRow, BorderLayout.SOUTH);
    return card;
  }

  void agregarAlCarrito(String codigo) {
    // 1. Verificar si el producto existe en el catálogo local
    boolean existeEnCatalogo = false;
    for (String[] prod : productos) {
      if (prod[0].equalsIgnoreCase(codigo) || prod[1].equalsIgnoreCase(codigo)) {
        existeEnCatalogo = true;
        break;
      }
    }
    if (!existeEnCatalogo) {
      JOptionPane.showMessageDialog(this,
        "<html>El producto <b>" + codigo + "</b> no existe en el catálogo.</html>",
        "Producto no encontrado",
        JOptionPane.ERROR_MESSAGE);
      return;
    }

    // 2. Delegar al subsistema de ventas (valida stock y agrega a la entidad Venta)
    Producto p = facade.procesarProducto(codigo);
    if (p == null) {
      JOptionPane.showMessageDialog(this,
        "<html>El producto <b>" + codigo + "</b> no tiene unidades disponibles en inventario.</html>",
        "Sin stock disponible",
        JOptionPane.WARNING_MESSAGE);
      return;
    }

    // 3. Actualizar carrito visual
    for (ItemCarrito it : carrito) {
      if (it.nombre.equalsIgnoreCase(p.getNombre())) {
        it.cantidad++;
        actualizarTotal();
        return;
      }
    }
    carrito.add(new ItemCarrito(p.getNombre(), p.getPrecio(), 1));
    actualizarTotal();
  }

  void actualizarTotal() {
    panelCarritoItems.removeAll();
    double total = 0;
    for (ItemCarrito it : new ArrayList<>(carrito)) {
      panelCarritoItems.add(filaCarrito(it));
      panelCarritoItems.add(Box.createVerticalStrut(8));
      total += it.subtotal();
    }
    panelCarritoItems.revalidate();
    panelCarritoItems.repaint();

    lblTotal.setText(String.format("$%.2f", total));
    int totalItems = carrito.stream().mapToInt(i -> i.cantidad).sum();
    lblCantItems.setText(totalItems + " items");
    if (lblProductosCount != null) {
      lblProductosCount.setText(totalItems + " productos");
    }
  }

  JPanel filaCarrito(ItemCarrito it) {
    JPanel fila = new JPanel(new GridBagLayout());
    fila.setOpaque(true);
    fila.setBackground(Colores.BLANCO);
    fila.setBorder(BorderFactory.createCompoundBorder(
      new LineBorder(Colores.BORDE_GRIS, 1, true),
      new EmptyBorder(10, 12, 10, 12)
    ));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

    GridBagConstraints c = new GridBagConstraints();

    JLabel lblNombre = label(it.nombre, 14, Font.BOLD, Colores.TEXTO_OSCURO);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    fila.add(lblNombre, c);

    JButton btnElim = botonEliminar();
    btnElim.addActionListener(e -> {
      carrito.remove(it);
      actualizarTotal();
    });
    c.gridx = 1;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    fila.add(btnElim, c);

    JLabel lblPreU = label("Precio unitario: $" + String.format("%.2f", it.precio), 11, Font.PLAIN, Colores.GRIS_TEXTO);
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
    JLabel lblCant = new JLabel(String.valueOf(it.cantidad));
    lblCant.setFont(Fuentes.b(15));
    lblCant.setForeground(Colores.TEXTO_OSCURO);
    lblCant.setPreferredSize(new Dimension(32, 32));
    lblCant.setHorizontalAlignment(SwingConstants.CENTER);
    JButton btnMas = botonCantidad("+");

    btnMenos.addActionListener(e -> {
      if (it.cantidad > 1) {
        it.cantidad--;
      } else {
        carrito.remove(it);
      }
      actualizarTotal();
    });
    btnMas.addActionListener(e -> {
      String cod = codigoParaNombre(it.nombre);
      Producto extra = facade.procesarProducto(cod);
      if (extra != null) {
        it.cantidad++;
        actualizarTotal();
      } else {
        JOptionPane.showMessageDialog(fila,
          "<html>El producto <b>" + it.nombre + "</b> no tiene más unidades disponibles.</html>",
          "Sin stock disponible",
          JOptionPane.WARNING_MESSAGE);
      }
    });

    ctrlCant.add(btnMenos);
    ctrlCant.add(lblCant);
    ctrlCant.add(btnMas);

    JPanel subPanel = new JPanel(new BorderLayout());
    subPanel.setOpaque(false);
    JLabel lblSubTxt = label("Subtotal", 10, Font.PLAIN, Colores.GRIS_TEXTO);
    lblSubTxt.setHorizontalAlignment(SwingConstants.RIGHT);
    JLabel lblSub = label(String.format("$%.2f", it.subtotal()), 15, Font.BOLD, Colores.AZUL);
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

  String codigoParaNombre(String nombre) {
    for (String[] p : productos) {
      if (p[1].equalsIgnoreCase(nombre)) {
        return p[0];
      }
    }
    return nombre;
  }

  JPanel tarjeta() {
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

  JTextField campoPill(String placeholder) {
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
    tf.setForeground(Colores.TEXTO_OSCURO);
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

  JButton botonAzul(String texto) {
    JButton b = new JButton(texto) {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(14));
    b.setPreferredSize(new Dimension(200, 46));
    return b;
  }

  JButton botonCantidad(String sym) {
    JButton b = new JButton(sym) {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(17));
    b.setPreferredSize(new Dimension(32, 32));
    return b;
  }

  JButton botonEliminar() {
    JButton b = new JButton("") {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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

        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(hover ? Colores.ROJO_BG_HOVER : Colores.ROJO_BG);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.ROJO_ICONO);
    b.setFont(Fuentes.r(14));
    b.setPreferredSize(new Dimension(32, 32));
    return b;
  }

  JButton botonCerrarSesion() {
    JButton b = new JButton("Cerrar sesión") {
      boolean hover = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
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
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(13));
    b.setPreferredSize(new Dimension(160, 38));
    b.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
      if (op == JOptionPane.YES_OPTION) {
        System.exit(0);
      }
    });
    return b;
  }

  JLabel label(String txt, int size, int style, Color color) {
    JLabel l = new JLabel(txt);
    l.setFont((style == Font.BOLD ? Fuentes.b(size) : Fuentes.r(size)));
    l.setForeground(color);
    return l;
  }

  public static void main(String[] args) {
    Fuentes.cargar();
    SwingUtilities.invokeLater(() -> new RegistrarVenta().setVisible(true));
  }

}
