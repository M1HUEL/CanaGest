package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.OrdenCompraDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
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

  private final JFrame menuOrigen;
  private final VentasControl control;
  private final List<OrdenCompraDTO> ordenes = new ArrayList<>();

  private JPanel panelOrdenes;
  private String filtroActual = "Todas";
  private JPanel tabsPanel;

  public OrdenesCompras(JFrame menuOrigen, VentasControl control) {
    this.menuOrigen = menuOrigen;
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
    btnNueva.addActionListener(e -> abrirFormularioNueva());

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
    btnDetalle.addActionListener(e -> abrirDetalleOrden(o));

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
          mostrarError(ex.getMessage());
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
          mostrarError(ex.getMessage());
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

  private void abrirDetalleOrden(OrdenCompraDTO o) {
    JDialog dlg = new JDialog(this, "Detalle de Orden de Compra", true);
    dlg.setSize(600, 500);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(true);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JPanel headerRow = new JPanel(new BorderLayout(10, 0));
    headerRow.setOpaque(false);

    JLabel lblNum = new JLabel(o.getNumero());
    lblNum.setFont(Fuentes.b(22));
    lblNum.setForeground(Colores.TEXTO_OSCURO);

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
    badge.setBorder(new EmptyBorder(4, 12, 4, 12));

    headerRow.add(lblNum, BorderLayout.WEST);
    headerRow.add(badge, BorderLayout.EAST);

    JLabel lblFecha = new JLabel("Fecha: " + o.getFecha());
    lblFecha.setFont(Fuentes.r(13));
    lblFecha.setForeground(Colores.GRIS_TEXTO);

    panel.add(headerRow);
    panel.add(Box.createVerticalStrut(6));
    panel.add(lblFecha);
    panel.add(Box.createVerticalStrut(24));

    JLabel sec1 = new JLabel("PROVEEDOR");
    sec1.setFont(Fuentes.b(12));
    sec1.setForeground(Colores.GRIS_TEXTO);
    sec1.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(sec1);
    panel.add(Box.createVerticalStrut(12));
    panel.add(crearFilaInfo("Nombre", o.getProveedorNombre()));
    panel.add(Box.createVerticalStrut(20));

    JLabel sec2 = new JLabel("DETALLE DE LA ORDEN");
    sec2.setFont(Fuentes.b(12));
    sec2.setForeground(Colores.GRIS_TEXTO);
    sec2.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(sec2);
    panel.add(Box.createVerticalStrut(12));
    panel.add(crearFilaInfo("Cantidad de productos", o.getProductos() + " items"));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Total", String.format("$%,.2f", o.getTotal().doubleValue())));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Estado", o.getEstado()));

    dlg.setContentPane(panel);
    dlg.setVisible(true);
  }

  private void abrirFormularioNueva() {
    JDialog dlg = new JDialog(this, "Nueva Orden de Compra", true);
    dlg.setSize(520, 520);
    dlg.setLocationRelativeTo(this);

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

    List<ProveedorDTO> proveedores = control.obtenerProveedores();
    List<ProveedorDTO> activos = proveedores.stream()
      .filter(ProveedorDTO::isActivo)
      .toList();

    String[] nombresProveedores = activos.stream()
      .map(ProveedorDTO::getNombre)
      .toArray(String[]::new);

    JComboBox<String> comboProveedor = new JComboBox<>(nombresProveedores);
    comboProveedor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    comboProveedor.setAlignmentX(LEFT_ALIGNMENT);

    panel.add(crearEtiquetaForm("Proveedor"));
    panel.add(Box.createVerticalStrut(4));
    panel.add(comboProveedor);
    panel.add(Box.createVerticalStrut(10));

    JTextField tfCant = crearCampoForm();
    JTextField tfTotal = crearCampoForm();

    panel.add(crearEtiquetaForm("Cantidad de productos"));
    panel.add(Box.createVerticalStrut(4));
    panel.add(tfCant);
    panel.add(Box.createVerticalStrut(10));
    panel.add(crearEtiquetaForm("Total ($)"));
    panel.add(Box.createVerticalStrut(4));
    panel.add(tfTotal);
    panel.add(Box.createVerticalStrut(20));

    JButton btnCrear = btnAzul("Crear Orden");
    btnCrear.setAlignmentX(LEFT_ALIGNMENT);
    btnCrear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

    btnCrear.addActionListener(e -> {
      String nombreProv = (String) comboProveedor.getSelectedItem();

      if (nombreProv == null || activos.isEmpty()) {
        mostrarError("No hay proveedores activos disponibles.");
        return;
      }

      ProveedorDTO prov = activos.stream()
        .filter(p -> p.getNombre().equals(nombreProv))
        .findFirst().orElse(null);

      if (prov == null) {
        mostrarError("No se encontró el proveedor seleccionado.");
        return;
      }

      try {
        int cant = Integer.parseInt(tfCant.getText().trim());
        java.math.BigDecimal tot = new java.math.BigDecimal(tfTotal.getText().trim());

        if (cant <= 0) {
          mostrarErrorEnDlg(dlg, "La cantidad de productos debe ser mayor a cero.");
          return;
        }
        if (tot.compareTo(java.math.BigDecimal.ZERO) <= 0) {
          mostrarErrorEnDlg(dlg, "El total debe ser mayor a cero.");
          return;
        }

        OrdenCompraDTO nueva = new OrdenCompraDTO(null, null, prov, "Pendiente", cant, tot);
        control.guardarOrdenCompra(nueva);
        recargarOrdenes();
        dlg.dispose();

      } catch (NumberFormatException ex) {
        mostrarErrorEnDlg(dlg, "Ingrese números válidos en cantidad y total.");
      } catch (IllegalArgumentException | IllegalStateException ex) {
        mostrarErrorEnDlg(dlg, ex.getMessage());
      } catch (Exception ex) {
        mostrarErrorEnDlg(dlg, "Error inesperado: " + ex.getMessage());
      }
    });

    panel.add(btnCrear);

    JScrollPane sp = new JScrollPane(panel);
    sp.setBorder(BorderFactory.createEmptyBorder());
    dlg.setContentPane(sp);
    dlg.setVisible(true);
  }

  private JPanel crearFilaInfo(String label, String valor) {
    JPanel row = new JPanel(new BorderLayout(10, 0)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(new EmptyBorder(12, 14, 12, 14));
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    row.setAlignmentX(LEFT_ALIGNMENT);

    JLabel l = new JLabel(label);
    l.setFont(Fuentes.r(12));
    l.setForeground(Colores.GRIS_TEXTO);

    JLabel v = new JLabel(valor != null ? valor : "-");
    v.setFont(Fuentes.r(14));
    v.setForeground(Colores.TEXTO_OSCURO);

    row.add(l, BorderLayout.WEST);
    row.add(v, BorderLayout.EAST);
    return row;
  }

  private JLabel crearEtiquetaForm(String texto) {
    JLabel lbl = new JLabel(texto);
    lbl.setFont(Fuentes.b(12));
    lbl.setForeground(Colores.TEXTO_OSCURO);
    lbl.setAlignmentX(LEFT_ALIGNMENT);
    return lbl;
  }

  private JTextField crearCampoForm() {
    JTextField tf = new JTextField();
    tf.setFont(Fuentes.r(13));
    tf.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(Colores.BORDE_GRIS, 1, 8),
      new EmptyBorder(8, 12, 8, 12)));
    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    tf.setAlignmentX(LEFT_ALIGNMENT);
    return tf;
  }

  private void mostrarError(String msg) {
    JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.WARNING_MESSAGE);
  }

  private void mostrarErrorEnDlg(JDialog dlg, String msg) {
    JOptionPane.showMessageDialog(dlg, msg, "Error", JOptionPane.WARNING_MESSAGE);
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
