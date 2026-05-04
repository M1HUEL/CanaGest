package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.negocios.inventario.IInventario;
import diseñadores.negocios.proveedores.IProveedores;
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

public class AsociarProductosProveedores extends JFrame {

  private final JFrame frame;

  private final IInventario inventarioFachada;
  private final IProveedores proveedoresFachada;

  private final List<ProductoDTO> productos = new ArrayList<>();

  private JPanel panelLista;
  private JTextField campoBusqueda;

  public AsociarProductosProveedores(JFrame frame, IInventario inventarioFachada, IProveedores proveedoresFachada) {
    this.frame = frame;
    this.inventarioFachada = inventarioFachada;
    this.proveedoresFachada = proveedoresFachada;

    setTitle("La Canasta - Asociar Productos con Proveedores");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    for (ProductoDTO p : inventarioFachada.obtenerTodos()) {
      productos.add(p);
    }

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
      frame.setVisible(true);
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
    header.setBorder(new EmptyBorder(0, 0, 18, 0));

    JPanel tituloCol = new JPanel();
    tituloCol.setLayout(new BoxLayout(tituloCol, BoxLayout.Y_AXIS));
    tituloCol.setOpaque(false);

    JLabel lblTitulo = new JLabel("Asociar Productos con Proveedores");
    lblTitulo.setFont(Fuentes.b(26));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblDesc = new JLabel("Vincula proveedores a productos para facilitar la reposición");
    lblDesc.setFont(Fuentes.r(14));
    lblDesc.setForeground(Colores.GRIS_TEXTO);

    tituloCol.add(lblTitulo);
    tituloCol.add(Box.createVerticalStrut(4));
    tituloCol.add(lblDesc);
    header.add(tituloCol, BorderLayout.WEST);

    JPanel barBusqueda = new JPanel(new BorderLayout()) {
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

    barBusqueda.setOpaque(false);
    barBusqueda.setBorder(new EmptyBorder(14, 20, 14, 20));
    barBusqueda.setPreferredSize(new Dimension(0, 66));

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

    campoBusqueda.setOpaque(false);
    campoBusqueda.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(new Color(213, 218, 230), 1, 8),
      new EmptyBorder(8, 14, 8, 14)));
    campoBusqueda.setFont(Fuentes.r(14));
    campoBusqueda.setForeground(Colores.GRIS_TEXTO);
    campoBusqueda.setText("Buscar producto por nombre o código...");

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
          campoBusqueda.setText("Buscar producto por nombre o código...");
          campoBusqueda.setForeground(Colores.GRIS_TEXTO);
        }
      }

    });

    campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        filtrar();
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        filtrar();
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        filtrar();
      }

    });

    barBusqueda.add(campoBusqueda, BorderLayout.CENTER);

    panelLista = new JPanel();
    panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
    panelLista.setOpaque(false);
    construirLista(productos);

    JScrollPane scroll = new JScrollPane(panelLista);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    JPanel centro = new JPanel(new BorderLayout(0, 16));
    centro.setOpaque(false);
    centro.add(barBusqueda, BorderLayout.NORTH);
    centro.add(scroll, BorderLayout.CENTER);

    contenido.add(header, BorderLayout.NORTH);
    contenido.add(centro, BorderLayout.CENTER);

    return contenido;
  }

  private void filtrar() {
    String q = campoBusqueda.getText();
    if (q.startsWith("Buscar") || q.isEmpty()) {
      construirLista(productos);
      return;
    }
    String ql = q.toLowerCase();
    List<ProductoDTO> f = new ArrayList<>();
    for (ProductoDTO p : productos) {
      if (p.getNombre().toLowerCase().contains(ql) || p.getCodigo().toLowerCase().contains(ql)) {
        f.add(p);
      }
    }
    construirLista(f);
  }

  private void construirLista(List<ProductoDTO> lista) {
    panelLista.removeAll();
    for (ProductoDTO p : lista) {
      panelLista.add(cardProducto(p));
      panelLista.add(Box.createVerticalStrut(14));
    }
    panelLista.revalidate();
    panelLista.repaint();
  }

  private JPanel cardProducto(ProductoDTO prod) {
    JPanel card = new JPanel(new BorderLayout(0, 14)) {
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
    card.setBorder(new EmptyBorder(20, 22, 20, 22));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    card.setAlignmentX(LEFT_ALIGNMENT);

    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setOpaque(false);

    JPanel infoCol = new JPanel();
    infoCol.setLayout(new BoxLayout(infoCol, BoxLayout.Y_AXIS));
    infoCol.setOpaque(false);

    JLabel lblNombre = new JLabel(prod.getNombre());
    lblNombre.setFont(Fuentes.b(18));
    lblNombre.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblCodigo = new JLabel(prod.getCodigo());
    lblCodigo.setFont(Fuentes.r(12));
    lblCodigo.setForeground(Colores.GRIS_TEXTO);

    infoCol.add(lblNombre);
    infoCol.add(Box.createVerticalStrut(2));
    infoCol.add(lblCodigo);

    JPanel derTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    derTop.setOpaque(false);
    if (prod.getProveedor() == null) {
      JButton btnAgregar = btnAzul("Agregar Proveedor");
      btnAgregar.addActionListener(e -> abrirDialogoAgregarProveedor(prod));
      derTop.add(btnAgregar);
    }

    topRow.add(infoCol, BorderLayout.WEST);
    topRow.add(derTop, BorderLayout.EAST);

    JLabel lblTitSec = new JLabel("Proveedor asociado:");
    lblTitSec.setFont(Fuentes.r(13));
    lblTitSec.setForeground(Colores.GRIS_TEXTO);

    JPanel proveedorArea = new JPanel(new BorderLayout(0, 8));
    proveedorArea.setOpaque(false);
    proveedorArea.add(lblTitSec, BorderLayout.NORTH);

    if (prod.getProveedor() == null) {
      JPanel vacioBorder = new JPanel(new GridBagLayout()) {
        @Override
        protected void paintComponent(Graphics g2d) {
          super.paintComponent(g2d);
          Graphics2D g = (Graphics2D) g2d;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          float[] dash = {6f, 4f};
          g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash, 0));
          g.setColor(Colores.BORDE_GRIS);
          g.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
        }

      };
      vacioBorder.setOpaque(false);
      vacioBorder.setPreferredSize(new Dimension(0, 72));

      JPanel vacioCentro = new JPanel();
      vacioCentro.setLayout(new BoxLayout(vacioCentro, BoxLayout.Y_AXIS));
      vacioCentro.setOpaque(false);

      JLabel lV1 = new JLabel("No hay proveedor asociado a este producto");
      lV1.setFont(Fuentes.r(14));
      lV1.setForeground(Colores.GRIS_TEXTO);
      lV1.setAlignmentX(CENTER_ALIGNMENT);

      JLabel lV2 = new JLabel("Haz clic en \"Agregar Proveedor\" para vincular uno");
      lV2.setFont(Fuentes.r(12));
      lV2.setForeground(new Color(180, 183, 189));
      lV2.setAlignmentX(CENTER_ALIGNMENT);

      vacioCentro.add(lV1);
      vacioCentro.add(Box.createVerticalStrut(4));
      vacioCentro.add(lV2);
      vacioBorder.add(vacioCentro);
      proveedorArea.add(vacioBorder, BorderLayout.CENTER);
    } else {
      JPanel wrapProv = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      wrapProv.setOpaque(false);
      wrapProv.add(cardProveedorUnico(prod.getProveedor(), prod));
      proveedorArea.add(wrapProv, BorderLayout.CENTER);
    }

    card.add(topRow, BorderLayout.NORTH);
    card.add(proveedorArea, BorderLayout.CENTER);
    return card;
  }

  private JPanel cardProveedorUnico(ProveedorDTO pv, ProductoDTO prod) {
    JPanel card = new JPanel(new BorderLayout(0, 8)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(239, 246, 255));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        g.setColor(new Color(191, 219, 254));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        super.paintComponent(g2d);
      }

    };

    card.setOpaque(false);
    card.setBorder(new EmptyBorder(14, 16, 14, 16));
    card.setPreferredSize(new Dimension(330, 130));

    JPanel headerCard = new JPanel(new BorderLayout());
    headerCard.setOpaque(false);

    JLabel lblNomProv = new JLabel(pv.getNombre());
    lblNomProv.setFont(Fuentes.b(14));
    lblNomProv.setForeground(Colores.TEXTO_OSCURO);

    JLabel badgePrin = new JLabel("Principal", SwingConstants.CENTER);
    badgePrin.setFont(Fuentes.b(11));
    badgePrin.setForeground(Colores.BLANCO);
    badgePrin.setOpaque(true);
    badgePrin.setBackground(Colores.AZUL);
    badgePrin.setBorder(new EmptyBorder(3, 10, 3, 10));

    JPanel bw = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    bw.setOpaque(false);
    bw.add(badgePrin);

    headerCard.add(lblNomProv, BorderLayout.WEST);
    headerCard.add(bw, BorderLayout.EAST);

    JPanel datosCol = new JPanel();
    datosCol.setLayout(new BoxLayout(datosCol, BoxLayout.Y_AXIS));
    datosCol.setOpaque(false);

    JLabel lblPrecio = new JLabel("Precio: $" + String.format("%.2f", pv.getPrecioProveedor()));
    lblPrecio.setFont(Fuentes.r(13));
    lblPrecio.setForeground(Colores.TEXTO_OSCURO);

    JPanel rowTE = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    rowTE.setOpaque(false);
    JLabel lblTELbl = new JLabel("Tiempo entrega: ");
    lblTELbl.setFont(Fuentes.b(13));
    lblTELbl.setForeground(Colores.TEXTO_OSCURO);
    JLabel lblTEVal = new JLabel(pv.getTiempoEntregaProveedor());
    lblTEVal.setFont(Fuentes.r(13));
    lblTEVal.setForeground(Colores.TEXTO_OSCURO);
    rowTE.add(lblTELbl);
    rowTE.add(lblTEVal);

    datosCol.add(lblPrecio);
    datosCol.add(Box.createVerticalStrut(3));
    datosCol.add(rowTE);

    JPanel botonesRow = new JPanel(new GridLayout(1, 2, 8, 0));
    botonesRow.setOpaque(false);
    botonesRow.setPreferredSize(new Dimension(0, 36));

    JButton btnEditar = new JButton("Editar") {
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
        g.setColor(ov ? new Color(235, 236, 240) : Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        g.setColor(Colores.BORDE_GRIS);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
        super.paintComponent(g2d);
      }

    };
    btnEditar.setForeground(Colores.TEXTO_OSCURO);
    btnEditar.setFont(Fuentes.r(12));
    btnEditar.addActionListener(e -> abrirDialogoEditarProveedor(pv, prod));

    JButton btnRemover = new JButton("Remover") {
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
        g.setColor(ov ? new Color(254, 200, 200) : new Color(254, 226, 226));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        g.setColor(new Color(252, 165, 165));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
        super.paintComponent(g2d);
      }

    };
    btnRemover.setForeground(Colores.ROJO);
    btnRemover.setFont(Fuentes.r(12));
    btnRemover.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(this,
        "¿Remover a " + pv.getNombre() + " como proveedor de " + prod.getNombre() + "?",
        "Confirmar", JOptionPane.YES_NO_OPTION);
      if (op == JOptionPane.YES_OPTION) {
        prod.setProveedor(null);
        construirLista(productos);
      }
    });

    botonesRow.add(btnEditar);
    botonesRow.add(btnRemover);

    card.add(headerCard, BorderLayout.NORTH);
    card.add(datosCol, BorderLayout.CENTER);
    card.add(botonesRow, BorderLayout.SOUTH);
    return card;
  }

  private boolean proveedorYaUsado(String nombre, ProductoDTO excluir) {
    for (ProductoDTO p : productos) {
      if (p != excluir && p.getProveedor() != null && p.getProveedor().getNombre().equalsIgnoreCase(nombre)) {
        return true;
      }
    }
    return false;
  }

  private void abrirDialogoAgregarProveedor(ProductoDTO prod) {
    JDialog dlg = new JDialog(this, "Agregar Proveedor a " + prod.getNombre(), true);
    dlg.setSize(460, 420);
    dlg.setLocationRelativeTo(this);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Asociar Proveedor");
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);

    panel.add(Box.createVerticalStrut(6));
    JLabel sub = new JLabel("Producto: " + prod.getNombre() + " (" + prod.getCodigo() + ")");
    sub.setFont(Fuentes.r(13));
    sub.setForeground(Colores.GRIS_TEXTO);
    sub.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(sub);

    panel.add(Box.createVerticalStrut(20));
    JLabel lblProv = new JLabel("Seleccionar Proveedor");
    lblProv.setFont(Fuentes.b(12));
    lblProv.setForeground(Colores.TEXTO_OSCURO);
    lblProv.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(lblProv);

    String[] nombresProv = proveedoresFachada.obtenerProveedores().stream()
      .map(ProveedorDTO::getNombre)
      .toArray(String[]::new);
    JComboBox<String> comboProveedor = new JComboBox<>(nombresProv);
    comboProveedor.setFont(Fuentes.r(13));
    comboProveedor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    comboProveedor.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(comboProveedor);

    panel.add(Box.createVerticalStrut(10));
    JLabel lblPrecio = new JLabel("Precio ($)");
    lblPrecio.setFont(Fuentes.b(12));
    panel.add(lblPrecio);

    JTextField campoPrecio = new JTextField();
    campoPrecio.setBorder(BorderFactory.createCompoundBorder(new Bordes(Colores.BORDE_GRIS, 1, 8), new EmptyBorder(8, 12, 8, 12)));
    campoPrecio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    campoPrecio.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(campoPrecio);

    panel.add(Box.createVerticalStrut(10));
    JLabel lblTiempo = new JLabel("Tiempo de Entrega");
    lblTiempo.setFont(Fuentes.b(12));
    panel.add(lblTiempo);

    JTextField campoTiempo = new JTextField();
    campoTiempo.setBorder(BorderFactory.createCompoundBorder(new Bordes(Colores.BORDE_GRIS, 1, 8), new EmptyBorder(8, 12, 8, 12)));
    campoTiempo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    campoTiempo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(campoTiempo);

    panel.add(Box.createVerticalStrut(18));
    JButton btnGuardar = btnAzulDialog("Asociar Proveedor");
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    btnGuardar.addActionListener(e -> {
      String nombre = (String) comboProveedor.getSelectedItem();
      String precio = campoPrecio.getText().trim();
      String tiempo = campoTiempo.getText().trim();

      if (nombre == null || precio.isEmpty() || tiempo.isEmpty()) {
        JOptionPane.showMessageDialog(dlg, "Campos obligatorios.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      if (proveedorYaUsado(nombre, prod)) {
        JOptionPane.showMessageDialog(dlg, "Proveedor ya ocupado.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      ProveedorDTO provDto = proveedoresFachada.obtenerProveedores().stream()
        .filter(p -> p.getNombre().equals(nombre))
        .findFirst().orElse(null);

      provDto.setPrecioProveedor(new java.math.BigDecimal(precio.replace("$", "")));
      provDto.setTiempoEntregaProveedor(tiempo.contains("día") ? tiempo : tiempo + " días");
      prod.setProveedor(provDto);
      construirLista(productos);
      dlg.dispose();
    });

    panel.add(btnGuardar);
    dlg.setContentPane(panel);
    dlg.setVisible(true);
  }

  private void abrirDialogoEditarProveedor(ProveedorDTO pv, ProductoDTO prod) {
    JDialog dlg = new JDialog(this, "Editar Proveedor: " + pv.getNombre(), true);
    dlg.setSize(460, 380);
    dlg.setLocationRelativeTo(this);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Editar Proveedor Asociado");
    titulo.setFont(Fuentes.b(20));
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(20));

    String[] etqs = {"Nombre", "Precio ($)", "Tiempo Entrega"};
    String[] vals = {pv.getNombre(), pv.getPrecioProveedor() != null ? pv.getPrecioProveedor().toString() : "", pv.getTiempoEntregaProveedor()};
    JTextField[] campos = new JTextField[3];

    for (int i = 0; i < 3; i++) {
      JLabel lbl = new JLabel(etqs[i]);
      lbl.setFont(Fuentes.b(12));
      panel.add(lbl);
      campos[i] = new JTextField(vals[i]);
      campos[i].setBorder(BorderFactory.createCompoundBorder(new Bordes(Colores.BORDE_GRIS, 1, 8), new EmptyBorder(8, 12, 8, 12)));
      campos[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
      campos[i].setAlignmentX(LEFT_ALIGNMENT);
      panel.add(campos[i]);
      panel.add(Box.createVerticalStrut(10));
    }

    JButton btnGuardar = btnAzulDialog("Guardar Cambios");
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    btnGuardar.addActionListener(e -> {
      pv.setNombre(campos[0].getText().trim());
      pv.setPrecioProveedor(new java.math.BigDecimal(campos[1].getText().trim()));
      pv.setTiempoEntregaProveedor(campos[2].getText().trim());
      prod.setProveedor(pv);
      construirLista(productos);
      dlg.dispose();
    });

    panel.add(btnGuardar);
    dlg.setContentPane(panel);
    dlg.setVisible(true);
  }

  private JButton btnAmarillo(String texto) {
    JButton b = createBaseButton(texto, new Color(255, 200, 0), new Color(240, 180, 0), new Color(30, 30, 30));
    b.setPreferredSize(new Dimension(180, 42));
    return b;
  }

  private JButton btnAzul(String texto) {
    JButton b = createBaseButton(texto, Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO);
    b.setPreferredSize(new Dimension(170, 40));
    return b;
  }

  private JButton btnAzulDialog(String texto) {
    return createBaseButton(texto, Colores.AZUL, Colores.AZUL_HOVER, Colores.BLANCO);
  }

  private JButton createBaseButton(String texto, Color bg, Color hover, Color fg) {
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
        g.setColor(ov ? hover : bg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(fg);
    b.setFont(Fuentes.b(13));
    return b;
  }

}
