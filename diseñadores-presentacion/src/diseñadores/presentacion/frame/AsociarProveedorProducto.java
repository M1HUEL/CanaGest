package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ProductoDTO;
import diseñadores.negocios.dto.ProveedorDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Botones;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class AsociarProveedorProducto extends JFrame {

  private final JFrame frame;
  private final VentasControl control;
  private final List<ProductoDTO> productos = new ArrayList<>();
  private JPanel panelLista;
  private JTextField campoBusqueda;

  public AsociarProveedorProducto(JFrame frame, VentasControl control) {
    this.frame = frame;
    this.control = control;

    setTitle("La Canasta - Asociar Productos con Proveedores");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    for (ProductoDTO p : control.obtenerProductosInventario()) {
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

    JButton btnMenu = Botones.amarillo("Menú Principal");
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

    campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
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

    JLabel lblCodigo = new JLabel("Código: " + prod.getCodigo());
    lblCodigo.setFont(Fuentes.r(12));
    lblCodigo.setForeground(Colores.GRIS_TEXTO);

    infoCol.add(lblNombre);
    infoCol.add(Box.createVerticalStrut(2));
    infoCol.add(lblCodigo);

    topRow.add(infoCol, BorderLayout.WEST);

    if (prod.getProveedor() == null) {
      JPanel derTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
      derTop.setOpaque(false);
      JButton btnAgregar = Botones.azul("+ Agregar Proveedor");
      btnAgregar.addActionListener(e -> new AgregarProveedorProducto(
        this, control, prod, productos, () -> construirLista(productos)).setVisible(true));
      derTop.add(btnAgregar);
      topRow.add(derTop, BorderLayout.EAST);
    }

    JPanel proveedorArea = new JPanel(new BorderLayout(0, 10));
    proveedorArea.setOpaque(false);

    JLabel lblTitSec = new JLabel("Proveedor asociado");
    lblTitSec.setFont(Fuentes.b(12));
    lblTitSec.setForeground(Colores.GRIS_TEXTO);
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

      JLabel lV1 = new JLabel("Este producto no tiene proveedor asociado");
      lV1.setFont(Fuentes.r(14));
      lV1.setForeground(Colores.GRIS_TEXTO);
      lV1.setAlignmentX(CENTER_ALIGNMENT);

      JLabel lV2 = new JLabel("Haz clic en \"+ Agregar Proveedor\" para vincular uno");
      lV2.setFont(Fuentes.r(12));
      lV2.setForeground(new Color(180, 183, 189));
      lV2.setAlignmentX(CENTER_ALIGNMENT);

      vacioCentro.add(lV1);
      vacioCentro.add(Box.createVerticalStrut(4));
      vacioCentro.add(lV2);
      vacioBorder.add(vacioCentro);
      proveedorArea.add(vacioBorder, BorderLayout.CENTER);
    } else {
      proveedorArea.add(cardProveedorUnico(prod.getProveedor(), prod), BorderLayout.CENTER);
    }

    card.add(topRow, BorderLayout.NORTH);
    card.add(proveedorArea, BorderLayout.CENTER);
    return card;
  }

  private JPanel cardProveedorUnico(ProveedorDTO pv, ProductoDTO prod) {
    JPanel card = new JPanel(new BorderLayout(0, 12)) {
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
    card.setBorder(new EmptyBorder(16, 18, 16, 18));

    JPanel cabecera = new JPanel(new BorderLayout());
    cabecera.setOpaque(false);

    JLabel lblNomProv = new JLabel(pv.getNombre());
    lblNomProv.setFont(Fuentes.b(16));
    lblNomProv.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblCodigo = new JLabel("Cód. " + (pv.getCodigo() != null ? pv.getCodigo() : "—"));
    lblCodigo.setFont(Fuentes.r(12));
    lblCodigo.setForeground(Colores.GRIS_TEXTO);

    JPanel nombreCol = new JPanel();
    nombreCol.setLayout(new BoxLayout(nombreCol, BoxLayout.Y_AXIS));
    nombreCol.setOpaque(false);
    nombreCol.add(lblNomProv);
    nombreCol.add(Box.createVerticalStrut(2));
    nombreCol.add(lblCodigo);

    cabecera.add(nombreCol, BorderLayout.WEST);

    boolean activo = pv.isActivo();
    JLabel badge = new JLabel(activo ? "Activo" : "Inactivo", SwingConstants.CENTER);
    badge.setFont(Fuentes.b(11));
    badge.setForeground(activo ? new Color(21, 128, 61) : new Color(185, 28, 28));
    badge.setOpaque(true);
    badge.setBackground(activo ? new Color(220, 252, 231) : new Color(254, 226, 226));
    badge.setBorder(new EmptyBorder(3, 10, 3, 10));

    JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    badgeWrap.setOpaque(false);
    badgeWrap.add(badge);
    cabecera.add(badgeWrap, BorderLayout.EAST);

    JPanel sep = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(191, 219, 254));
        g.drawLine(0, 0, getWidth(), 0);
      }

    };
    sep.setOpaque(false);
    sep.setPreferredSize(new Dimension(0, 1));

    JPanel cuerpo = new JPanel(new GridLayout(1, 2, 24, 0));
    cuerpo.setOpaque(false);

    JPanel colContacto = new JPanel();
    colContacto.setLayout(new BoxLayout(colContacto, BoxLayout.Y_AXIS));
    colContacto.setOpaque(false);

    JLabel lblSecContacto = new JLabel("CONTACTO");
    lblSecContacto.setFont(Fuentes.b(10));
    lblSecContacto.setForeground(new Color(100, 116, 139));

    colContacto.add(lblSecContacto);
    colContacto.add(Box.createVerticalStrut(8));
    colContacto.add(filaDato("Responsable", pv.getContacto()));
    colContacto.add(Box.createVerticalStrut(5));
    colContacto.add(filaDato("Teléfono", pv.getTelefono()));
    colContacto.add(Box.createVerticalStrut(5));
    colContacto.add(filaDato("Email", pv.getEmail()));

    JPanel colComercial = new JPanel();
    colComercial.setLayout(new BoxLayout(colComercial, BoxLayout.Y_AXIS));
    colComercial.setOpaque(false);

    JLabel lblSecComercial = new JLabel("CONDICIONES COMERCIALES");
    lblSecComercial.setFont(Fuentes.b(10));
    lblSecComercial.setForeground(new Color(100, 116, 139));

    String precioStr = pv.getPrecioProveedor() != null
      ? "$" + String.format("%.2f", pv.getPrecioProveedor())
      : "—";
    String entregaStr = pv.getTiempoEntregaProveedor() != null
      ? pv.getTiempoEntregaProveedor()
      : "—";
    String pagoStr = pv.getTerminosPago() != null ? pv.getTerminosPago() : "—";

    colComercial.add(lblSecComercial);
    colComercial.add(Box.createVerticalStrut(8));
    colComercial.add(filaDato("Precio proveedor", precioStr));
    colComercial.add(Box.createVerticalStrut(5));
    colComercial.add(filaDato("Tiempo de entrega", entregaStr));
    colComercial.add(Box.createVerticalStrut(5));
    colComercial.add(filaDato("Términos de pago", pagoStr));

    cuerpo.add(colContacto);
    cuerpo.add(colComercial);

    JPanel botonesRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    botonesRow.setOpaque(false);

    JButton btnEditar = crearBtnOutline("Editar", Colores.TEXTO_OSCURO, Colores.BLANCO, new Color(235, 236, 240));
    btnEditar.addActionListener(e -> new EditarProveedorProducto(
      this, pv, prod, () -> construirLista(productos)).setVisible(true));

    JButton btnRemover = crearBtnOutline("Remover", Colores.ROJO, new Color(254, 226, 226), new Color(254, 200, 200));
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

    card.add(cabecera, BorderLayout.NORTH);

    JPanel centro = new JPanel(new BorderLayout(0, 10));
    centro.setOpaque(false);
    centro.add(sep, BorderLayout.NORTH);
    centro.add(cuerpo, BorderLayout.CENTER);

    card.add(centro, BorderLayout.CENTER);
    card.add(filaBotones(pv, prod), BorderLayout.SOUTH);
    return card;
  }

  private JPanel filaBotones(ProveedorDTO pv, ProductoDTO prod) {
    JPanel fila = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    fila.setOpaque(false);

    JButton btnEditar = Botones.gris("Editar");
    JButton btnRemover = Botones.rojo("Remover");

    btnEditar.addActionListener(e -> new EditarProveedorProducto(
      AsociarProveedorProducto.this, pv, prod,
      () -> construirLista(productos)).setVisible(true));

    btnRemover.addActionListener(e -> {
      int op = JOptionPane.showConfirmDialog(AsociarProveedorProducto.this,
        "¿Remover a " + pv.getNombre() + " como proveedor de " + prod.getNombre() + "?",
        "Confirmar", JOptionPane.YES_NO_OPTION);
      if (op == JOptionPane.YES_OPTION) {
        prod.setProveedor(null);
        construirLista(productos);
      }
    });

    fila.add(btnEditar);
    fila.add(btnRemover);
    return fila;
  }

  private JPanel filaDato(String label, String valor) {
    JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    fila.setOpaque(false);
    fila.setAlignmentX(LEFT_ALIGNMENT);

    JLabel lbl = new JLabel(label + ": ");
    lbl.setFont(Fuentes.b(12));
    lbl.setForeground(Colores.TEXTO_OSCURO);

    JLabel val = new JLabel(valor != null && !valor.isBlank() ? valor : "—");
    val.setFont(Fuentes.r(12));
    val.setForeground(Colores.GRIS_TEXTO);

    fila.add(lbl);
    fila.add(val);
    return fila;
  }

  private JButton crearBtnOutline(String texto, Color fg, Color bg, Color hover) {
    JButton btn = new JButton(texto) {
      boolean ov = false;

      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(90, 34));
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
        g.setColor(ov ? hover : bg);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        g.setColor(fg.darker());
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
        super.paintComponent(g2d);
      }

    };
    btn.setForeground(fg);
    btn.setFont(Fuentes.b(12));
    return btn;
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
