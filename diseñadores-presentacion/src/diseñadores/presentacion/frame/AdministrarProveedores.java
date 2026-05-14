package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class AdministrarProveedores extends JFrame {

  private final JFrame frame;
  private final VentasControl ventasControl;
  private final List<ProveedorDTO> proveedores = new ArrayList<>();

  private JPanel panelGrid;
  private JLabel lblActivos;
  private JTextField campoBusqueda;

  public AdministrarProveedores(JFrame frame, VentasControl ventasControl) {
    this.frame = frame;
    this.ventasControl = ventasControl;

    configurarVentana();
    cargarDatosIniciales();
    inicializarComponentes();
  }

  private void configurarVentana() {
    setTitle("La Canasta - Administrar Proveedores");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);
  }

  private void cargarDatosIniciales() {
    proveedores.addAll(ventasControl.obtenerProveedores());
  }

  private void inicializarComponentes() {
    JPanel root = crearPanelRaiz();
    root.add(crearBarraSuperior(), BorderLayout.NORTH);
    root.add(crearContenido(), BorderLayout.CENTER);
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

  private JPanel crearBarraSuperior() {
    JPanel bar = new JPanel(new BorderLayout());
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS),
      new EmptyBorder(0, 24, 0, 24)));
    bar.setPreferredSize(new Dimension(0, 66));

    bar.add(crearPanelBotonMenu(), BorderLayout.EAST);
    return bar;
  }

  private JPanel crearPanelBotonMenu() {
    JButton btnMenu = crearBotonAmarillo("Menu Principal");
    btnMenu.addActionListener(e -> {
      dispose();
      frame.setVisible(true);
    });

    JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
    der.setOpaque(false);
    der.add(btnMenu);
    return der;
  }

  private JPanel crearContenido() {
    JPanel contenido = new JPanel(new BorderLayout());
    contenido.setOpaque(false);
    contenido.setBorder(new EmptyBorder(28, 32, 28, 32));

    contenido.add(crearEncabezado(), BorderLayout.NORTH);
    contenido.add(crearCentroBusquedaYGrid(), BorderLayout.CENTER);
    return contenido;
  }

  private JPanel crearEncabezado() {
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.setBorder(new EmptyBorder(0, 0, 20, 0));

    header.add(crearTituloColumna(), BorderLayout.WEST);
    header.add(crearTarjetaActivos(), BorderLayout.EAST);
    return header;
  }

  private JPanel crearTituloColumna() {
    JPanel tituloCol = new JPanel();
    tituloCol.setLayout(new BoxLayout(tituloCol, BoxLayout.Y_AXIS));
    tituloCol.setOpaque(false);

    JLabel lblTitulo = new JLabel("Administrar Proveedores");
    lblTitulo.setFont(Fuentes.b(26));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblDesc = new JLabel("Gestiona la información de tus proveedores");
    lblDesc.setFont(Fuentes.r(14));
    lblDesc.setForeground(Colores.GRIS_TEXTO);

    tituloCol.add(lblTitulo);
    tituloCol.add(Box.createVerticalStrut(4));
    tituloCol.add(lblDesc);
    return tituloCol;
  }

  private JPanel crearTarjetaActivos() {
    JPanel cardActivos = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarFondoRedondeado(g2d, getWidth(), getHeight(), 14);
        super.paintComponent(g2d);
      }

    };
    configurarEstiloTarjetaActivos(cardActivos);

    JLabel lblActivosTxt = new JLabel("Proveedores Activos");
    lblActivosTxt.setFont(Fuentes.r(12));
    lblActivosTxt.setForeground(Colores.GRIS_TEXTO);
    lblActivosTxt.setAlignmentX(CENTER_ALIGNMENT);

    lblActivos = new JLabel(String.valueOf(ventasControl.contarProveedoresActivos()), SwingConstants.CENTER);
    lblActivos.setFont(Fuentes.b(36));
    lblActivos.setForeground(Colores.AZUL);
    lblActivos.setAlignmentX(CENTER_ALIGNMENT);

    cardActivos.add(lblActivosTxt);
    cardActivos.add(Box.createVerticalStrut(4));
    cardActivos.add(lblActivos);
    return cardActivos;
  }

  private void configurarEstiloTarjetaActivos(JPanel card) {
    card.setOpaque(false);
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(new EmptyBorder(14, 24, 14, 24));
    card.setPreferredSize(new Dimension(200, 90));
  }

  private JPanel crearCentroBusquedaYGrid() {
    JPanel centro = new JPanel(new BorderLayout(0, 12));
    centro.setOpaque(false);

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    topPanel.setOpaque(false);
    topPanel.add(crearBarraBusqueda());
    topPanel.add(Box.createVerticalStrut(8));
    topPanel.add(crearFiltroEstado());
    topPanel.add(Box.createVerticalStrut(4));

    centro.add(topPanel, BorderLayout.NORTH);
    centro.add(crearScrollGrid(), BorderLayout.CENTER);
    return centro;
  }

  private JPanel crearFiltroEstado() {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    row.setOpaque(false);

    String[] etiquetas = {"Todos", "Activos", "Inactivos"};
    ButtonGroup grupo = new ButtonGroup();

    for (String etiqueta : etiquetas) {
      JToggleButton tab = new JToggleButton(etiqueta) {
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
          if (isSelected()) {
            g.setColor(Colores.AZUL);
          } else {
            g.setColor(ov ? new Color(229, 231, 235) : new Color(245, 246, 248));
          }
          g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
          super.paintComponent(g2d);
        }

      };
      tab.setFont(Fuentes.b(13));
      tab.setForeground(Colores.TEXTO_OSCURO);
      tab.setPreferredSize(new Dimension(100, 36));
      tab.addActionListener(e -> aplicarFiltroEstado(etiqueta));
      grupo.add(tab);
      row.add(tab);
      if (etiqueta.equals("Todos")) {
        tab.setSelected(true);
      }
    }
    return row;
  }

  private void aplicarFiltroEstado(String filtro) {
    List<ProveedorDTO> base = switch (filtro) {
      case "Activos" ->
        proveedores.stream().filter(ProveedorDTO::isActivo).toList();
      case "Inactivos" ->
        proveedores.stream().filter(p -> !p.isActivo()).toList();
      default ->
        proveedores;
    };
    construirGrid(base);
  }

  private JPanel crearBarraBusqueda() {
    JPanel barBusqueda = new JPanel(new BorderLayout(12, 0)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarFondoRedondeado(g2d, getWidth(), getHeight(), 14);
        super.paintComponent(g2d);
      }

    };
    barBusqueda.setOpaque(false);
    barBusqueda.setBorder(new EmptyBorder(14, 20, 14, 20));
    barBusqueda.setPreferredSize(new Dimension(0, 68));

    campoBusqueda = crearCampoBusqueda();
    JButton btnNuevo = crearBotonAzul("Nuevo Proveedor", 170, 42);
    btnNuevo.addActionListener(e -> abrirFormulario(null));

    barBusqueda.add(campoBusqueda, BorderLayout.CENTER);
    barBusqueda.add(btnNuevo, BorderLayout.EAST);
    return barBusqueda;
  }

  private JTextField crearCampoBusqueda() {
    JTextField campo = new JTextField() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 249, 252));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    configurarEstiloCampoBusqueda(campo);
    agregarEventosBusqueda(campo);
    return campo;
  }

  private void configurarEstiloCampoBusqueda(JTextField campo) {
    campo.setOpaque(false);
    campo.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(new Color(213, 218, 230), 1, 8),
      new EmptyBorder(8, 14, 8, 14)));
    campo.setFont(Fuentes.r(14));
    campo.setForeground(Colores.GRIS_TEXTO);
    campo.setText("Buscar por nombre, código o contacto...");
  }

  private void agregarEventosBusqueda(JTextField campo) {
    campo.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (campo.getText().startsWith("Buscar")) {
          campo.setText("");
          campo.setForeground(Colores.TEXTO_OSCURO);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (campo.getText().isEmpty()) {
          campo.setText("Buscar por nombre, código o contacto...");
          campo.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });

    campo.getDocument().addDocumentListener(new DocumentListener() {
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
  }

  private JScrollPane crearScrollGrid() {
    panelGrid = new JPanel(new GridLayout(0, 2, 16, 16));
    panelGrid.setOpaque(false);
    panelGrid.setBorder(new EmptyBorder(4, 0, 4, 0));
    construirGrid(proveedores);

    JScrollPane scroll = new JScrollPane(panelGrid);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    return scroll;
  }

  private void construirGrid(List<ProveedorDTO> lista) {
    panelGrid.removeAll();
    for (ProveedorDTO p : lista) {
      panelGrid.add(crearTarjetaProveedor(p));
    }
    if (lista.isEmpty()) {
      panelGrid.add(crearLabelVacio());
    }
    panelGrid.revalidate();
    panelGrid.repaint();
  }

  private JLabel crearLabelVacio() {
    JLabel vacio = new JLabel("No se encontraron proveedores.", SwingConstants.CENTER);
    vacio.setFont(Fuentes.r(15));
    vacio.setForeground(Colores.GRIS_TEXTO);
    return vacio;
  }

  private void filtrar() {
    String q = campoBusqueda.getText();
    if (q.startsWith("Buscar") || q.isEmpty()) {
      construirGrid(proveedores);
      return;
    }
    String ql = q.toLowerCase();
    List<ProveedorDTO> filtrados = proveedores.stream()
      .filter(p -> p.getNombre().toLowerCase().contains(ql)
      || (p.getCodigo() != null && p.getCodigo().toLowerCase().contains(ql))
      || (p.getContacto() != null && p.getContacto().toLowerCase().contains(ql)))
      .toList();
    construirGrid(filtrados);
  }

  private void recargarLista() {
    proveedores.clear();
    proveedores.addAll(ventasControl.obtenerProveedores());
    lblActivos.setText(String.valueOf(ventasControl.contarProveedoresActivos()));
    filtrar();
  }

  private JPanel crearPanelConSombra(int radio) {
    JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        dibujarFondoRedondeado(g2d, getWidth(), getHeight(), radio);
        super.paintComponent(g2d);
      }

    };
    panel.setOpaque(false);
    return panel;
  }

  private void dibujarFondoRedondeado(Graphics g2d, int w, int h, int radio) {
    Graphics2D g = (Graphics2D) g2d;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Colores.SOMBRA);
    g.fill(new RoundRectangle2D.Float(3, 3, w - 3, h - 3, radio, radio));
    g.setColor(Colores.BLANCO);
    g.fill(new RoundRectangle2D.Float(0, 0, w - 2, h - 2, radio, radio));
  }

  private JPanel crearFilaSuperiorProveedor(ProveedorDTO p) {
    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setOpaque(false);

    JPanel nombreCol = new JPanel();
    nombreCol.setLayout(new BoxLayout(nombreCol, BoxLayout.Y_AXIS));
    nombreCol.setOpaque(false);

    JLabel lblN = new JLabel(p.getNombre());
    lblN.setFont(Fuentes.b(16));
    lblN.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblC = new JLabel(p.getCodigo());
    lblC.setFont(Fuentes.r(12));
    lblC.setForeground(Colores.GRIS_TEXTO);

    nombreCol.add(lblN);
    nombreCol.add(Box.createVerticalStrut(3));
    nombreCol.add(lblC);

    topRow.add(nombreCol, BorderLayout.CENTER);
    topRow.add(crearBadgeActivo(p.isActivo()), BorderLayout.EAST);
    return topRow;
  }

  private JPanel crearBadgeActivo(boolean activo) {
    JLabel badge = new JLabel(activo ? "Activo" : "Inactivo", SwingConstants.CENTER);
    badge.setFont(Fuentes.b(11));
    badge.setForeground(activo ? new Color(21, 128, 61) : new Color(100, 100, 100));
    badge.setOpaque(true);
    badge.setBackground(activo ? new Color(220, 252, 231) : new Color(229, 231, 235));
    badge.setBorder(new EmptyBorder(4, 12, 4, 12));

    JPanel badgeW = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    badgeW.setOpaque(false);
    badgeW.add(badge);
    return badgeW;
  }

  private JPanel crearFilaDato(String etiqueta, String valor) {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    row.setOpaque(false);
    JLabel lbl = new JLabel(etiqueta + ": ");
    lbl.setFont(Fuentes.b(13));
    lbl.setForeground(Colores.TEXTO_OSCURO);
    JLabel val = new JLabel(valor != null ? valor : "-");
    val.setFont(Fuentes.r(13));
    val.setForeground(Colores.TEXTO_OSCURO);
    row.add(lbl);
    row.add(val);
    return row;
  }

  private JPanel crearPanelInferiorTarjeta(ProveedorDTO p) {
    JPanel bottom = new JPanel(new BorderLayout(0, 10));
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
    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

    bottom.add(sep, BorderLayout.NORTH);
    bottom.add(crearBotonesTarjeta(p), BorderLayout.SOUTH);
    return bottom;
  }

  private void abrirFormulario(ProveedorDTO prov) {
    if (prov == null) {
      new AgregarProveedor(this, ventasControl, this::recargarLista).setVisible(true);
    } else {
      new EditarProveedor(this, ventasControl, prov, this::recargarLista).setVisible(true);
    }
  }

  private void abrirDetalle(ProveedorDTO p) {
    new MostrarDetalleProveedor(this, p).setVisible(true);
  }

  private JButton crearBotonAmarillo(String texto) {
    return crearBotonAnimado(texto, new Color(255, 200, 0), new Color(240, 180, 0), new Color(30, 30, 30),
      new Dimension(180, 42));
  }

  private JButton crearBotonAzul(String texto, int ancho, int alto) {
    return crearBotonAnimado(texto, Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO,
      new Dimension(ancho, alto));
  }

  private JButton crearBotonCard(String texto, Color base, Color hov, boolean blanco) {
    Color fg = blanco ? Colores.BLANCO : Colores.TEXTO_OSCURO;
    JButton b = crearBotonAnimado(texto, base, hov, fg, null);
    b.setFont(Fuentes.b(13));
    return b;
  }

  private JButton crearBotonAnimado(String texto, Color base, Color hov, Color fg, Dimension size) {
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
    b.setForeground(fg);
    b.setFont(Fuentes.b(14));
    if (size != null) {
      b.setPreferredSize(size);
    }
    return b;
  }

  private JPanel crearBotonesTarjeta(ProveedorDTO p) {
    JPanel row = new JPanel(new GridLayout(1, 3, 10, 0));
    row.setOpaque(false);
    row.setPreferredSize(new Dimension(0, 40));

    JButton btnEditar = crearBotonCard("Editar", Colores.AZUL, Colores.AZUL_HOVER, true);
    JButton btnToggle = crearBotonCard(
      p.isActivo() ? "Desactivar" : "Activar",
      p.isActivo() ? new Color(245, 246, 248) : new Color(220, 252, 231),
      p.isActivo() ? new Color(229, 231, 235) : new Color(187, 247, 208),
      false);
    JButton btnEliminar = crearBotonCard("Eliminar", new Color(254, 226, 226), new Color(254, 200, 200), false);
    btnEliminar.setForeground(Colores.ROJO);

    btnEditar.addActionListener(e -> abrirFormulario(p));
    btnToggle.addActionListener(e -> toggleEstado(p));
    btnEliminar.addActionListener(e -> confirmarEliminar(p));

    row.add(btnEditar);
    row.add(btnToggle);
    row.add(btnEliminar);
    return row;
  }

  private void toggleEstado(ProveedorDTO p) {
    String accion = p.isActivo() ? "desactivar" : "activar";
    int op = JOptionPane.showConfirmDialog(this,
      "¿Deseas " + accion + " a " + p.getNombre() + "?",
      "Confirmar", JOptionPane.YES_NO_OPTION);
    if (op == JOptionPane.YES_OPTION) {
      p.setActivo(!p.isActivo());
      ventasControl.actualizarProveedor(p);
      recargarLista();
    }
  }

  private void confirmarEliminar(ProveedorDTO p) {
    int op = JOptionPane.showConfirmDialog(this,
      "¿Eliminar a " + p.getNombre() + "?\nEsta acción no se puede deshacer.",
      "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    if (op == JOptionPane.YES_OPTION) {
      ventasControl.eliminarProveedor(p);
      recargarLista();
    }
  }

  private JPanel crearTarjetaProveedor(ProveedorDTO p) {
    JPanel card = crearPanelConSombra(16);
    card.setLayout(new BorderLayout(0, 10));
    card.setBorder(new EmptyBorder(20, 22, 20, 22));
    card.setPreferredSize(new Dimension(0, 300));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

    card.add(crearFilaSuperiorProveedor(p), BorderLayout.NORTH);
    card.add(crearPanelDatosProveedor(p), BorderLayout.CENTER);
    card.add(crearPanelInferiorTarjeta(p), BorderLayout.SOUTH);
    return card;
  }

  private JPanel crearPanelDatosProveedor(ProveedorDTO p) {
    JPanel datos = new JPanel();
    datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
    datos.setOpaque(false);
    datos.setBorder(new EmptyBorder(8, 0, 8, 0));

    datos.add(crearFilaDato("Contacto", p.getContacto(), 22));
    datos.add(Box.createVerticalStrut(5));
    datos.add(crearFilaDato("Teléfono", p.getTelefono(), 20));
    datos.add(Box.createVerticalStrut(5));
    datos.add(crearFilaDato("Email", p.getEmail(), 32));
    datos.add(Box.createVerticalStrut(5));
    datos.add(crearFilaDato("Dirección", p.getDireccion(), 30));
    datos.add(Box.createVerticalStrut(5));
    datos.add(crearFilaDato("Términos de pago", p.getTerminosPago(), 24));
    return datos;
  }

  private JPanel crearFilaDato(String etiqueta, String valor, int maxCaracteres) {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    row.setOpaque(false);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

    JLabel lbl = new JLabel(etiqueta + ": ");
    lbl.setFont(Fuentes.b(13));
    lbl.setForeground(Colores.TEXTO_OSCURO);

    String textoMostrar = truncar(valor, maxCaracteres);
    JLabel val = new JLabel(textoMostrar);
    val.setFont(Fuentes.r(13));
    val.setForeground(Colores.GRIS_TEXTO);

    if (valor != null && valor.length() > maxCaracteres) {
      val.setToolTipText(valor);
    }

    row.add(lbl);
    row.add(val);
    return row;
  }

  private String truncar(String texto, int max) {
    if (texto == null || texto.isBlank()) {
      return "—";
    }
    return texto.length() > max ? texto.substring(0, max) + "…" : texto;
  }

}
