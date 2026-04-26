package diseñadores.presentacion.frame;

import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ExistenciaProductos extends JFrame {

  static class Producto {

    String codigo, nombre, ultimaAct;
    int stockActual, stockMin, stockMax;

    Producto(String codigo, String nombre, int stockActual, int stockMin, int stockMax, String ultimaAct) {
      this.codigo = codigo;
      this.nombre = nombre;
      this.stockActual = stockActual;
      this.stockMin = stockMin;
      this.stockMax = stockMax;
      this.ultimaAct = ultimaAct;
    }

    String getEstado() {
      if (stockActual <= stockMin) {
        return "Bajo";
      }
      if (stockActual >= stockMax) {
        return "Alto";
      }
      return "Normal";
    }

  }

  private final JFrame menuOrigen;
  private final List<Producto> productos = new ArrayList<>();
  private JPanel panelTabla;
  private JTextField campoBusqueda;

  public ExistenciaProductos(JFrame menuOrigen) {
    this.menuOrigen = menuOrigen;
    setTitle("La Canasta - Existencia de Productos");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    productos.add(new Producto("PROD-001", "Arroz 1kg", 45, 20, 100, "2026-04-22"));
    productos.add(new Producto("PROD-002", "Frijol 1kg", 12, 20, 80, "2026-04-21"));
    productos.add(new Producto("PROD-003", "Azúcar 1kg", 78, 30, 120, "2026-04-22"));
    productos.add(new Producto("PROD-004", "Aceite 1L", 35, 15, 60, "2026-04-20"));

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
    JLabel n = new JLabel("La Canasta");
    n.setFont(Fuentes.b(16));
    n.setForeground(Colores.TEXTO_OSCURO);
    JLabel s = new JLabel("Punto de Venta");
    s.setFont(Fuentes.r(12));
    s.setForeground(Colores.GRIS_TEXTO);
    izq.add(n);
    izq.add(s);

    JButton btnMenu = btnAmarillo("← Menú Principal");
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
    JLabel lblTitulo = new JLabel("Existencia de Productos");
    lblTitulo.setFont(Fuentes.b(26));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);
    JLabel lblDesc = new JLabel("Registra y actualiza el inventario de productos");
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
      new PantallaLogin.RoundedLineBorder(new Color(213, 218, 230), 1, 8),
      new EmptyBorder(8, 14, 8, 14)));
    campoBusqueda.setFont(Fuentes.r(14));
    campoBusqueda.setForeground(Colores.GRIS_TEXTO);
    campoBusqueda.setText("Buscar por nombre o código del producto...");
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
          campoBusqueda.setText("Buscar por nombre o código del producto...");
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
      }

    });
    barBusqueda.add(campoBusqueda, BorderLayout.CENTER);

    JPanel wrapTabla = new JPanel(new BorderLayout()) {
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
    wrapTabla.setOpaque(false);

    JPanel headerTabla = new JPanel(new GridLayout(1, 8));
    headerTabla.setOpaque(false);
    headerTabla.setBorder(new EmptyBorder(14, 20, 14, 20));
    String[] cols = {"Código", "Producto", "Stock Actual", "Stock Mín", "Stock Máx", "Estado", "Última Act.", "Acciones"};
    for (String col : cols) {
      JLabel lbl = new JLabel(col);
      lbl.setFont(Fuentes.b(13));
      lbl.setForeground(Colores.TEXTO_OSCURO);
      headerTabla.add(lbl);
    }

    panelTabla = new JPanel();
    panelTabla.setLayout(new BoxLayout(panelTabla, BoxLayout.Y_AXIS));
    panelTabla.setOpaque(false);
    construirTabla(productos);

    JScrollPane scroll = new JScrollPane(panelTabla);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    wrapTabla.add(headerTabla, BorderLayout.NORTH);
    wrapTabla.add(scroll, BorderLayout.CENTER);

    JPanel centro = new JPanel(new BorderLayout(0, 16));
    centro.setOpaque(false);
    centro.add(barBusqueda, BorderLayout.NORTH);
    centro.add(wrapTabla, BorderLayout.CENTER);

    contenido.add(header, BorderLayout.NORTH);
    contenido.add(centro, BorderLayout.CENTER);
    return contenido;
  }

  private void filtrar() {
    String q = campoBusqueda.getText();
    if (q.startsWith("Buscar") || q.isEmpty()) {
      construirTabla(productos);
      return;
    }
    String ql = q.toLowerCase();
    List<Producto> f = new ArrayList<>();
    for (Producto p : productos) {
      if (p.nombre.toLowerCase().contains(ql) || p.codigo.toLowerCase().contains(ql)) {
        f.add(p);
      }
    }
    construirTabla(f);
  }

  private void construirTabla(List<Producto> lista) {
    panelTabla.removeAll();
    for (Producto p : lista) {
      panelTabla.add(filaProducto(p));
      JPanel sep = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          g.setColor(Colores.BORDE_GRIS);
          g.drawLine(0, 0, getWidth(), 0);
        }

      };
      sep.setOpaque(false);
      sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
      sep.setPreferredSize(new Dimension(0, 1));
      panelTabla.add(sep);
    }
    if (lista.isEmpty()) {
      JLabel vacio = new JLabel("No se encontraron productos.", SwingConstants.CENTER);
      vacio.setFont(Fuentes.r(14));
      vacio.setForeground(Colores.GRIS_TEXTO);
      vacio.setAlignmentX(CENTER_ALIGNMENT);
      panelTabla.add(vacio);
    }
    panelTabla.revalidate();
    panelTabla.repaint();
  }

  private JPanel filaProducto(Producto p) {
    JPanel fila = new JPanel(new GridLayout(1, 8));
    fila.setOpaque(false);
    fila.setBorder(new EmptyBorder(14, 20, 14, 20));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

    JLabel lblCodigo = new JLabel(p.codigo);
    lblCodigo.setFont(Fuentes.r(13));
    lblCodigo.setForeground(Colores.GRIS_TEXTO);

    JLabel lblNombre = new JLabel(p.nombre);
    lblNombre.setFont(Fuentes.r(13));
    lblNombre.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblStock = new JLabel(String.valueOf(p.stockActual));
    lblStock.setFont(Fuentes.b(14));
    lblStock.setForeground(Colores.TEXTO_OSCURO);

    JLabel lblMin = new JLabel(String.valueOf(p.stockMin));
    lblMin.setFont(Fuentes.r(13));
    lblMin.setForeground(Colores.GRIS_TEXTO);

    JLabel lblMax = new JLabel(String.valueOf(p.stockMax));
    lblMax.setFont(Fuentes.r(13));
    lblMax.setForeground(Colores.GRIS_TEXTO);

    String estado = p.getEstado();
    Color estadoFg = estado.equals("Bajo") ? new Color(185, 28, 28)
      : (estado.equals("Alto") ? new Color(21, 128, 61) : new Color(60, 80, 120));
    Color estadoBg = estado.equals("Bajo") ? new Color(254, 226, 226)
      : (estado.equals("Alto") ? new Color(220, 252, 231) : new Color(239, 246, 255));
    JLabel lblEstado = new JLabel(estado, SwingConstants.CENTER);
    lblEstado.setFont(Fuentes.b(11));
    lblEstado.setForeground(estadoFg);
    lblEstado.setOpaque(true);
    lblEstado.setBackground(estadoBg);
    lblEstado.setBorder(new EmptyBorder(4, 10, 4, 10));
    JPanel wrapEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapEstado.setOpaque(false);
    wrapEstado.add(lblEstado);

    JLabel lblUlt = new JLabel(p.ultimaAct);
    lblUlt.setFont(Fuentes.r(13));
    lblUlt.setForeground(Colores.GRIS_TEXTO);

    JButton btnActualizar = new JButton("Actualizar") {
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
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g2d);
      }

    };
    btnActualizar.setForeground(Colores.BLANCO);
    btnActualizar.setFont(Fuentes.b(12));
    btnActualizar.setPreferredSize(new Dimension(100, 34));
    btnActualizar.addActionListener(e -> abrirDialogoActualizar(p));

    JPanel wrapBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapBtn.setOpaque(false);
    wrapBtn.add(btnActualizar);

    fila.add(lblCodigo);
    fila.add(lblNombre);
    fila.add(lblStock);
    fila.add(lblMin);
    fila.add(lblMax);
    fila.add(wrapEstado);
    fila.add(lblUlt);
    fila.add(wrapBtn);
    return fila;
  }

  private void abrirDialogoActualizar(Producto p) {
    JDialog dlg = new JDialog(this, "Actualizar Stock: " + p.nombre, true);
    dlg.setSize(400, 340);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Actualizar: " + p.nombre);
    titulo.setFont(Fuentes.b(18));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(20));

    String[] etqs = {"Stock Actual", "Stock Mínimo", "Stock Máximo"};
    String[] vals = {String.valueOf(p.stockActual), String.valueOf(p.stockMin), String.valueOf(p.stockMax)};
    JTextField[] campos = new JTextField[3];

    for (int i = 0; i < etqs.length; i++) {
      JLabel lbl = new JLabel(etqs[i]);
      lbl.setFont(Fuentes.b(12));
      lbl.setForeground(Colores.TEXTO_OSCURO);
      lbl.setAlignmentX(LEFT_ALIGNMENT);
      JTextField tf = new JTextField(vals[i]);
      tf.setFont(Fuentes.r(13));
      tf.setBorder(BorderFactory.createCompoundBorder(
        new PantallaLogin.RoundedLineBorder(Colores.BORDE_GRIS, 1, 8),
        new EmptyBorder(8, 12, 8, 12)));
      tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
      tf.setAlignmentX(LEFT_ALIGNMENT);
      campos[i] = tf;
      panel.add(lbl);
      panel.add(Box.createVerticalStrut(4));
      panel.add(tf);
      panel.add(Box.createVerticalStrut(10));
    }

    JButton btnGuardar = new JButton("Guardar") {
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
    btnGuardar.setForeground(Colores.BLANCO);
    btnGuardar.setFont(Fuentes.b(14));
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    btnGuardar.addActionListener(e -> {
      try {
        p.stockActual = Integer.parseInt(campos[0].getText().trim());
        p.stockMin = Integer.parseInt(campos[1].getText().trim());
        p.stockMax = Integer.parseInt(campos[2].getText().trim());
        p.ultimaAct = java.time.LocalDate.now().toString();
        construirTabla(productos);
        dlg.dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(dlg, "Ingrese valores numéricos válidos.", "Error", JOptionPane.WARNING_MESSAGE);
      }
    });
    panel.add(btnGuardar);
    dlg.setContentPane(panel);
    dlg.setVisible(true);
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

}
