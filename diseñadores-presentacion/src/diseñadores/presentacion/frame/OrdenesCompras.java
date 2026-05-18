package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class OrdenesCompras extends JFrame {

  private final VentasControl control;

  private final JFrame frame;

  private final List<OrdenCompraDTO> ordenes = new ArrayList<>();

  private JPanel panelOrdenes;
  private String filtroActual = "Todas";
  private JPanel tabsPanel;

  public OrdenesCompras(JFrame frame, VentasControl control) {
    this.frame = frame;
    this.control = control;

    configurarVentana();
    ordenes.addAll(control.obtenerOrdenesCompra());

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

  private void configurarVentana() {
    setTitle("La Canasta - Órdenes de Compra");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);
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
      frame.setVisible(true);
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
    contenido.add(crearHeader(), BorderLayout.NORTH);
    contenido.add(crearPanelCentro(), BorderLayout.CENTER);
    return contenido;
  }

  private JPanel crearHeader() {
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
    btnNueva.addActionListener(e -> new RegistrarNuevaOrdenCompra(this, control, this::recargarOrdenes).setVisible(true));

    JPanel derH = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
    derH.setOpaque(false);
    derH.add(btnNueva);

    header.add(tituloCol, BorderLayout.WEST);
    header.add(derH, BorderLayout.EAST);
    return header;
  }

  private JPanel crearPanelCentro() {
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
    return centro;
  }

  private void actualizarTabs(String activo) {
    for (Component c : tabsPanel.getComponents()) {
      if (c instanceof JButton b) {
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
      String estado = o.getEstado();
      if (filtroActual.equals("Pendientes") && estado.equals("Pendiente")) {
        r.add(o);
      } else if (filtroActual.equals("Aprobadas") && estado.equals("Aprobada")) {
        r.add(o);
      } else if (filtroActual.equals("Recibidas") && estado.equals("Recibida")) {
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

  private void recargarOrdenes() {
    ordenes.clear();
    ordenes.addAll(control.obtenerOrdenesCompra());
    construirOrdenes(filtrar());
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
    card.add(crearTopCard(o), BorderLayout.NORTH);
    card.add(crearMidCard(o), BorderLayout.CENTER);
    card.add(crearBottomCard(o), BorderLayout.SOUTH);
    return card;
  }

  private JPanel crearTopCard(OrdenCompraDTO o) {
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
    switch (o.getEstado()) {
      case "Pendiente" -> {
        badgeColor = new Color(161, 110, 0);
        badgeBg = new Color(254, 243, 199);
      }
      case "Aprobada" -> {
        badgeColor = new Color(30, 80, 180);
        badgeBg = new Color(219, 234, 254);
      }
      default -> {
        badgeColor = new Color(21, 128, 61);
        badgeBg = new Color(220, 252, 231);
      }
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
    return topRow;
  }

  private JPanel crearMidCard(OrdenCompraDTO o) {
    JPanel mid = new JPanel(new BorderLayout());
    mid.setOpaque(false);

    JPanel datos = new JPanel();
    datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
    datos.setOpaque(false);
    datos.setBorder(new EmptyBorder(8, 0, 4, 0));
    datos.add(crearFilaDato("Proveedor: ", o.getProveedorNombre()));
    datos.add(Box.createVerticalStrut(5));
    datos.add(crearFilaDato("Productos: ", o.getProductos() + " items"));

    JLabel lblTotal = new JLabel(String.format("$%.2f", o.getTotal().doubleValue()));
    lblTotal.setFont(Fuentes.b(24));
    lblTotal.setForeground(Colores.AZUL);
    lblTotal.setBorder(new EmptyBorder(6, 0, 10, 0));

    mid.add(datos, BorderLayout.NORTH);
    mid.add(lblTotal, BorderLayout.SOUTH);
    return mid;
  }

  private JPanel crearFilaDato(String titulo, String valor) {
    JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    fila.setOpaque(false);
    JLabel lT = new JLabel(titulo);
    lT.setFont(Fuentes.b(13));
    lT.setForeground(Colores.TEXTO_OSCURO);
    JLabel lV = new JLabel(valor != null ? valor : "-");
    lV.setFont(Fuentes.r(13));
    lV.setForeground(Colores.TEXTO_OSCURO);
    fila.add(lT);
    fila.add(lV);
    return fila;
  }

  private JPanel crearBottomCard(OrdenCompraDTO o) {
    JPanel bottom = new JPanel(new BorderLayout(0, 8));
    bottom.setOpaque(false);

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

    JButton btnDetalle = crearBotonCardOrdenes("Ver Detalle",
      new Color(245, 246, 248), new Color(229, 231, 235), false);
    btnDetalle.addActionListener(e -> new DetalleOrdenCompra(this, o).setVisible(true));

    JPanel botonesRow;

    if (o.getEstado().equals("Pendiente")) {
      botonesRow = new JPanel(new GridLayout(1, 2, 8, 0));
      botonesRow.setOpaque(false);
      JButton btnAprobar = crearBotonCardOrdenes("Aprobar", Colores.VERDE, Colores.VERDE_HOVER, true);
      btnAprobar.addActionListener(e -> {
        try {
          control.cambiarEstadoOrden(o.getNumero(), "Aprobada");
          recargarOrdenes();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
      });
      botonesRow.add(btnDetalle);
      botonesRow.add(btnAprobar);
    } else if (o.getEstado().equals("Aprobada")) {
      botonesRow = new JPanel(new GridLayout(1, 2, 8, 0));
      botonesRow.setOpaque(false);
      JButton btnRecibir = crearBotonCardOrdenes("Recibir", Colores.AZUL, Colores.AZUL_HOVER, true);
      btnRecibir.addActionListener(e -> {
        try {
          control.cambiarEstadoOrden(o.getNumero(), "Recibida");
          recargarOrdenes();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
      });
      botonesRow.add(btnDetalle);
      botonesRow.add(btnRecibir);
    } else {
      botonesRow = new JPanel(new GridLayout(1, 1, 0, 0));
      botonesRow.setOpaque(false);
      botonesRow.add(btnDetalle);
    }

    botonesRow.setPreferredSize(new Dimension(0, 40));
    bottom.add(sep, BorderLayout.NORTH);
    bottom.add(botonesRow, BorderLayout.SOUTH);
    return bottom;
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
          setForeground(Colores.BLANCO);
        } else {
          g.setColor(ov ? new Color(235, 236, 240) : new Color(245, 246, 248));
          setForeground(Colores.TEXTO_OSCURO);
        }
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    b.putClientProperty("selected", seleccionado);
    b.setFont(Fuentes.b(13));
    b.setPreferredSize(new Dimension(120, 36));
    return b;
  }

  private JButton btnAmarillo(String texto) {
    return crearBotonEstilizado(texto,
      new Color(255, 200, 0), new Color(240, 180, 0), new Color(30, 30, 30));
  }

  private JButton btnAzul(String texto) {
    return crearBotonEstilizado(texto, Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO);
  }

  private JButton crearBotonEstilizado(String texto, Color base, Color hov, Color fg) {
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
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(fg);
    b.setFont(Fuentes.b(14));
    b.setPreferredSize(new Dimension(180, 42));
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
