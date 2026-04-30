package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.negocios.proveedores.ProveedoresFacade;
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

public class OrdenesCompras extends JFrame {

  private final JFrame menuOrigen;
  private final ProveedoresFacade facade;
  private final List<OrdenCompraDTO> ordenes = new ArrayList<>();
  private JPanel panelOrdenes;
  private String filtroActual = "Todas";
  private JPanel tabsPanel;

  public OrdenesCompras(JFrame menuOrigen) {
    this.menuOrigen = menuOrigen;
    this.facade = new ProveedoresFacade();
    setTitle("La Canasta - Órdenes de Compra");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    ordenes.addAll(facade.obtenerOrdenesCompra());

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
    JLabel lblTitulo = new JLabel("Órdenes de Compra");
    lblTitulo.setFont(Fuentes.b(26));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);
    JLabel lblDesc = new JLabel("Gestiona y da seguimiento a las órdenes de compra");
    lblDesc.setFont(Fuentes.r(14));
    lblDesc.setForeground(Colores.GRIS_TEXTO);
    tituloCol.add(lblTitulo);
    tituloCol.add(Box.createVerticalStrut(4));
    tituloCol.add(lblDesc);

    JButton btnNueva = btnAzul("Nueva Orden");
    btnNueva.addActionListener(e -> abrirFormularioNueva());

    JPanel derH = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
    derH.setOpaque(false);
    derH.add(btnNueva);

    header.add(tituloCol, BorderLayout.WEST);
    header.add(derH, BorderLayout.EAST);

    JPanel barFiltros = new JPanel(new BorderLayout()) {
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
    barFiltros.setOpaque(false);
    barFiltros.setBorder(new EmptyBorder(12, 16, 12, 16));
    barFiltros.setPreferredSize(new Dimension(0, 62));

    tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    tabsPanel.setOpaque(false);
    for (String label : new String[]{"Todas", "Pendientes", "Aprobadas", "Recibidas"}) {
      JButton tab = crearTab(label, label.equals("Todas"));
      tab.addActionListener(e -> {
        filtroActual = label;
        construirOrdenes(filtrar());
        actualizarTabs(label);
      });
      tabsPanel.add(tab);
    }
    barFiltros.add(tabsPanel, BorderLayout.WEST);

    panelOrdenes = new JPanel(new GridLayout(0, 3, 16, 16));
    panelOrdenes.setOpaque(false);
    construirOrdenes(ordenes);

    JScrollPane scroll = new JScrollPane(panelOrdenes);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    JPanel centro = new JPanel(new BorderLayout(0, 16));
    centro.setOpaque(false);
    centro.add(barFiltros, BorderLayout.NORTH);
    centro.add(scroll, BorderLayout.CENTER);

    contenido.add(header, BorderLayout.NORTH);
    contenido.add(centro, BorderLayout.CENTER);
    return contenido;
  }

  private void actualizarTabs(String activo) {
    for (Component c : tabsPanel.getComponents()) {
      if (c instanceof JButton) {
        JButton b = (JButton) c;
        b.putClientProperty("selected", b.getText().equals(activo));
        b.repaint();
      }
    }
  }

  private List<OrdenCompraDTO> filtrar() {
    if (filtroActual.equals("Todas")) {
      return ordenes;
    }
    List<OrdenCompraDTO> r = new ArrayList<>();
    for (OrdenCompraDTO o : ordenes) {
      if (filtroActual.equals("Pendientes") && o.getEstado().equals("Pendiente")) {
        r.add(o);
      } else if (filtroActual.equals("Aprobadas") && o.getEstado().equals("Aprobada")) {
        r.add(o);
      } else if (filtroActual.equals("Recibidas") && o.getEstado().equals("Recibida")) {
        r.add(o);
      }
    }
    return r;
  }

  private void construirOrdenes(List<OrdenCompraDTO> lista) {
    panelOrdenes.removeAll();
    for (OrdenCompraDTO o : lista) {
      panelOrdenes.add(cardOrden(o));
    }
    if (lista.isEmpty()) {
      JLabel vacio = new JLabel("No hay órdenes.", SwingConstants.CENTER);
      vacio.setFont(Fuentes.r(15));
      vacio.setForeground(Colores.GRIS_TEXTO);
      panelOrdenes.add(vacio);
    }
    panelOrdenes.revalidate();
    panelOrdenes.repaint();
  }

  private JPanel cardOrden(OrdenCompraDTO o) {
    JPanel card = new JPanel(new BorderLayout(0, 10)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, 16, 16));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 16, 16));
        super.paintComponent(g2d);
      }

    };
    card.setOpaque(false);
    card.setBorder(new EmptyBorder(18, 20, 18, 20));

    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setOpaque(false);
    JPanel numCol = new JPanel();
    numCol.setLayout(new BoxLayout(numCol, BoxLayout.Y_AXIS));
    numCol.setOpaque(false);
    JLabel lblNum = new JLabel(o.getNumero());
    lblNum.setFont(Fuentes.b(16));
    lblNum.setForeground(Colores.TEXTO_OSCURO);
    JLabel lblFecha = new JLabel(o.getFecha());
    lblFecha.setFont(Fuentes.r(12));
    lblFecha.setForeground(Colores.GRIS_TEXTO);
    numCol.add(lblNum);
    numCol.add(Box.createVerticalStrut(3));
    numCol.add(lblFecha);

    Color badgeColor, badgeBg;
    if (o.getEstado().equals("Pendiente")) {
      badgeColor = new Color(161, 110, 0);
      badgeBg = new Color(254, 243, 199);
    } else if (o.getEstado().equals("Aprobada")) {
      badgeColor = new Color(30, 80, 180);
      badgeBg = new Color(219, 234, 254);
    } else {
      badgeColor = new Color(21, 128, 61);
      badgeBg = new Color(220, 252, 231);
    }
    JLabel badge = new JLabel(o.getEstado(), SwingConstants.CENTER);
    badge.setFont(Fuentes.b(11));
    badge.setForeground(badgeColor);
    badge.setOpaque(true);
    badge.setBackground(badgeBg);
    badge.setBorder(new EmptyBorder(4, 10, 4, 10));
    JPanel badgeW = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    badgeW.setOpaque(false);
    badgeW.add(badge);
    topRow.add(numCol, BorderLayout.CENTER);
    topRow.add(badgeW, BorderLayout.EAST);

    JPanel datos = new JPanel();
    datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
    datos.setOpaque(false);
    datos.setBorder(new EmptyBorder(8, 0, 4, 0));

    JPanel filaP = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    filaP.setOpaque(false);
    JLabel lP = new JLabel("Proveedor: ");
    lP.setFont(Fuentes.b(13));
    lP.setForeground(Colores.TEXTO_OSCURO);
    JLabel vP = new JLabel(o.getProveedorNombre());
    vP.setFont(Fuentes.r(13));
    vP.setForeground(Colores.TEXTO_OSCURO);
    filaP.add(lP);
    filaP.add(vP);

    JPanel filaQ = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    filaQ.setOpaque(false);
    JLabel lQ = new JLabel("Productos: ");
    lQ.setFont(Fuentes.b(13));
    lQ.setForeground(Colores.TEXTO_OSCURO);
    JLabel vQ = new JLabel(o.getProductos() + " items");
    vQ.setFont(Fuentes.r(13));
    vQ.setForeground(Colores.TEXTO_OSCURO);
    filaQ.add(lQ);
    filaQ.add(vQ);

    datos.add(filaP);
    datos.add(Box.createVerticalStrut(5));
    datos.add(filaQ);

    JLabel lblTotal = new JLabel(String.format("$%.2f", o.getTotal().doubleValue()));
    lblTotal.setFont(Fuentes.b(24));
    lblTotal.setForeground(Colores.AZUL);
    lblTotal.setBorder(new EmptyBorder(6, 0, 10, 0));

    JPanel sep = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colores.BORDE_GRIS);
        g.drawLine(0, 0, getWidth(), 0);
      }

    };
    sep.setOpaque(false);
    sep.setPreferredSize(new Dimension(0, 1));
    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

    JPanel botonesRow = new JPanel(new GridLayout(1, 2, 8, 0));
    botonesRow.setOpaque(false);
    botonesRow.setPreferredSize(new Dimension(0, 40));

    JButton btnDetalle = crearBotonCardOrdenes("Ver Detalle", new Color(245, 246, 248), new Color(229, 231, 235), false);
    btnDetalle.addActionListener(e -> JOptionPane.showMessageDialog(this,
        "Orden: " + o.getNumero() + "\nProveedor: " + o.getProveedorNombre()
          + "\nProductos: " + o.getProductos() + "\nTotal: $" + String.format("%.2f", o.getTotal().doubleValue())
          + "\nEstado: " + o.getEstado(), "Detalle de Orden", JOptionPane.INFORMATION_MESSAGE));
    botonesRow.add(btnDetalle);

    if (o.getEstado().equals("Pendiente")) {
      JButton btnAprobar = crearBotonCardOrdenes("Aprobar", Colores.VERDE, Colores.VERDE_HOVER, true);
      btnAprobar.addActionListener(e -> {
        o.setEstado("Aprobada");
        facade.cambiarEstadoOrden(o.getNumero(), "Aprobada");
        construirOrdenes(filtrar());
      });
      botonesRow.add(btnAprobar);
    } else if (o.getEstado().equals("Aprobada")) {
      JButton btnRecibir = crearBotonCardOrdenes("Recibir", Colores.AZUL, Colores.AZUL_HOVER, true);
      btnRecibir.addActionListener(e -> {
        o.setEstado("Recibida");
        facade.cambiarEstadoOrden(o.getNumero(), "Recibida");
        construirOrdenes(filtrar());
      });
      botonesRow.add(btnRecibir);
    } else {
      botonesRow.setLayout(new GridLayout(1, 1, 0, 0));
    }

    JPanel bottom = new JPanel(new BorderLayout(0, 8));
    bottom.setOpaque(false);
    bottom.add(sep, BorderLayout.NORTH);
    bottom.add(botonesRow, BorderLayout.SOUTH);

    JPanel mid = new JPanel(new BorderLayout());
    mid.setOpaque(false);
    mid.add(datos, BorderLayout.NORTH);
    mid.add(lblTotal, BorderLayout.SOUTH);

    card.add(topRow, BorderLayout.NORTH);
    card.add(mid, BorderLayout.CENTER);
    card.add(bottom, BorderLayout.SOUTH);
    return card;
  }

  private void abrirFormularioNueva() {
    JDialog dlg = new JDialog(this, "Nueva Orden de Compra", true);
    dlg.setSize(520, 520);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(true);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Nueva Orden de Compra");
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(20));

    List<ProveedorDTO> proveedores = facade.obtenerProveedores();
    String[] nombresProveedores = proveedores.stream()
      .filter(ProveedorDTO::isActivo)
      .map(ProveedorDTO::getNombre)
      .toArray(String[]::new);

    JLabel lblProv = new JLabel("Proveedor");
    lblProv.setFont(Fuentes.b(12));
    lblProv.setForeground(Colores.TEXTO_OSCURO);
    lblProv.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(lblProv);
    panel.add(Box.createVerticalStrut(4));

    JComboBox<String> comboProveedor = new JComboBox<>(nombresProveedores);
    comboProveedor.setFont(Fuentes.r(13));
    comboProveedor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    comboProveedor.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(comboProveedor);
    panel.add(Box.createVerticalStrut(10));

    String[] etqs = {"Cantidad de productos", "Total ($)"};
    JTextField[] campos = new JTextField[etqs.length];
    for (int i = 0; i < etqs.length; i++) {
      JLabel lbl = new JLabel(etqs[i]);
      lbl.setFont(Fuentes.b(12));
      lbl.setForeground(Colores.TEXTO_OSCURO);
      lbl.setAlignmentX(LEFT_ALIGNMENT);
      JTextField tf = new JTextField();
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

    JButton btnCrear = btnAzul("Crear Orden");
    btnCrear.setAlignmentX(LEFT_ALIGNMENT);
    btnCrear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    btnCrear.addActionListener(e -> {
      try {
        String nombreProvSeleccionado = (String) comboProveedor.getSelectedItem();
        if (nombreProvSeleccionado == null) {
          JOptionPane.showMessageDialog(dlg, "Seleccione un proveedor.", "Error", JOptionPane.WARNING_MESSAGE);
          return;
        }
        ProveedorDTO proveedorSeleccionado = facade.obtenerProveedores().stream()
          .filter(p -> p.getNombre().equals(nombreProvSeleccionado))
          .findFirst()
          .orElse(null);
        int cantidad = Integer.parseInt(campos[0].getText().trim());
        java.math.BigDecimal total = new java.math.BigDecimal(campos[1].getText().trim());
        if (cantidad <= 0 || total.compareTo(java.math.BigDecimal.ZERO) <= 0) {
          JOptionPane.showMessageDialog(dlg, "Ingrese valores positivos.", "Error", JOptionPane.WARNING_MESSAGE);
          return;
        }
        OrdenCompraDTO nuevaOrden = new OrdenCompraDTO(null, null, proveedorSeleccionado, "Pendiente", cantidad, total);
        facade.guardarOrdenCompra(nuevaOrden);
        ordenes.clear();
        ordenes.addAll(facade.obtenerOrdenesCompra());
        construirOrdenes(filtrar());
        dlg.dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(dlg, "Ingrese valores numéricos válidos.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });
    panel.add(btnCrear);

    JScrollPane sp = new JScrollPane(panel);
    sp.setBorder(BorderFactory.createEmptyBorder());
    sp.getVerticalScrollBar().setUnitIncrement(12);
    dlg.setContentPane(sp);
    dlg.setVisible(true);
  }

  private JButton crearTab(String texto, boolean seleccionado) {
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
        Boolean sel = (Boolean) getClientProperty("selected");
        if (Boolean.TRUE.equals(sel)) {
          g.setColor(Colores.AZUL);
          g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
          setForeground(Colores.BLANCO);
        } else {
          g.setColor(ov ? new Color(235, 236, 240) : new Color(245, 246, 248));
          g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
          setForeground(Colores.TEXTO_OSCURO);
        }
        super.paintComponent(g2d);
      }

    };
    b.putClientProperty("selected", seleccionado);
    b.setFont(Fuentes.b(13));
    b.setPreferredSize(new Dimension(120, 36));
    b.setForeground(seleccionado ? Colores.BLANCO : Colores.TEXTO_OSCURO);
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
    b.setPreferredSize(new Dimension(150, 42));
    return b;
  }

  private JButton crearBotonCardOrdenes(String texto, Color base, Color hov, boolean blanco) {
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
        g.setColor(ov ? hov : base);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(blanco ? Colores.BLANCO : Colores.TEXTO_OSCURO);
    b.setFont(Fuentes.b(13));
    return b;
  }

}
