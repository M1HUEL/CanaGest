package diseñadores.presentacion.frame;

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

public class AdministrarProveedores extends JFrame {

  private final JFrame menuOrigen;
  private final ProveedoresFacade facade;
  private final List<ProveedorDTO> proveedores = new ArrayList<>();
  private JPanel panelGrid;
  private JLabel lblActivos;
  private JTextField campoBusqueda;

  public AdministrarProveedores(JFrame menuOrigen) {
    this.menuOrigen = menuOrigen;
    this.facade = new ProveedoresFacade();
    setTitle("La Canasta - Administrar Proveedores");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setSize(1500, 900);
    setLocationRelativeTo(null);
    setResizable(true);

    proveedores.addAll(facade.obtenerProveedores());

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

    JButton btnMenu = crearBotonAmarillo("Menu Principal");
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
    JLabel lblTitulo = new JLabel("Administrar Proveedores");
    lblTitulo.setFont(Fuentes.b(26));
    lblTitulo.setForeground(Colores.TEXTO_OSCURO);
    JLabel lblDesc = new JLabel("Gestiona la información de tus proveedores");
    lblDesc.setFont(Fuentes.r(14));
    lblDesc.setForeground(Colores.GRIS_TEXTO);
    tituloCol.add(lblTitulo);
    tituloCol.add(Box.createVerticalStrut(4));
    tituloCol.add(lblDesc);

    JPanel cardActivos = new JPanel() {
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
    cardActivos.setOpaque(false);
    cardActivos.setLayout(new BoxLayout(cardActivos, BoxLayout.Y_AXIS));
    cardActivos.setBorder(new EmptyBorder(14, 24, 14, 24));
    cardActivos.setPreferredSize(new Dimension(200, 90));

    JLabel lblActivosTxt = new JLabel("Proveedores Activos");
    lblActivosTxt.setFont(Fuentes.r(12));
    lblActivosTxt.setForeground(Colores.GRIS_TEXTO);
    lblActivosTxt.setAlignmentX(CENTER_ALIGNMENT);
    lblActivos = new JLabel(String.valueOf(facade.contarProveedoresActivos()), SwingConstants.CENTER);
    lblActivos.setFont(Fuentes.b(36));
    lblActivos.setForeground(Colores.AZUL);
    lblActivos.setAlignmentX(CENTER_ALIGNMENT);
    cardActivos.add(lblActivosTxt);
    cardActivos.add(Box.createVerticalStrut(4));
    cardActivos.add(lblActivos);

    header.add(tituloCol, BorderLayout.WEST);
    header.add(cardActivos, BorderLayout.EAST);

    JPanel barBusqueda = new JPanel(new BorderLayout(12, 0)) {
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
    barBusqueda.setPreferredSize(new Dimension(0, 68));

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
    campoBusqueda.setText("Buscar por nombre, código o contacto...");
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
          campoBusqueda.setText("Buscar por nombre, código o contacto...");
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

    JButton btnNuevo = crearBotonAzul("Nuevo Proveedor", 170, 42);
    btnNuevo.addActionListener(e -> abrirFormulario(null));

    barBusqueda.add(campoBusqueda, BorderLayout.CENTER);
    barBusqueda.add(btnNuevo, BorderLayout.EAST);

    panelGrid = new JPanel(new GridLayout(0, 2, 16, 16));
    panelGrid.setOpaque(false);
    construirGrid(proveedores);

    JScrollPane scroll = new JScrollPane(panelGrid);
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

  private void construirGrid(List<ProveedorDTO> lista) {
    panelGrid.removeAll();
    for (ProveedorDTO p : lista) {
      panelGrid.add(cardProveedor(p));
    }
    if (lista.isEmpty()) {
      JLabel vacio = new JLabel("No se encontraron proveedores.", SwingConstants.CENTER);
      vacio.setFont(Fuentes.r(15));
      vacio.setForeground(Colores.GRIS_TEXTO);
      panelGrid.add(vacio);
    }
    panelGrid.revalidate();
    panelGrid.repaint();
  }

  private void filtrar() {
    String q = campoBusqueda.getText();
    if (q.startsWith("Buscar") || q.isEmpty()) {
      construirGrid(proveedores);
      return;
    }
    String ql = q.toLowerCase();
    List<ProveedorDTO> f = new ArrayList<>();
    for (ProveedorDTO p : proveedores) {
      if (p.getNombre().toLowerCase().contains(ql) || p.getCodigo().toLowerCase().contains(ql)
        || p.getContacto().toLowerCase().contains(ql)) {
        f.add(p);
      }
    }
    construirGrid(f);
  }

  private JPanel cardProveedor(ProveedorDTO p) {
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
    card.setBorder(new EmptyBorder(20, 22, 20, 22));

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

    JLabel badge = new JLabel(p.isActivo() ? "Activo" : "Inactivo", SwingConstants.CENTER);
    badge.setFont(Fuentes.b(11));
    badge.setForeground(p.isActivo() ? new Color(21, 128, 61) : new Color(100, 100, 100));
    badge.setOpaque(true);
    badge.setBackground(p.isActivo() ? new Color(220, 252, 231) : new Color(229, 231, 235));
    badge.setBorder(new EmptyBorder(4, 12, 4, 12));
    JPanel badgeW = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    badgeW.setOpaque(false);
    badgeW.add(badge);
    topRow.add(nombreCol, BorderLayout.CENTER);
    topRow.add(badgeW, BorderLayout.EAST);

    JPanel datos = new JPanel();
    datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
    datos.setOpaque(false);
    datos.setBorder(new EmptyBorder(8, 0, 8, 0));
    datos.add(filaDato("Contacto", p.getContacto()));
    datos.add(Box.createVerticalStrut(5));
    datos.add(filaDato("Teléfono", p.getTelefono()));
    datos.add(Box.createVerticalStrut(5));
    datos.add(filaDato("Email", p.getEmail()));
    datos.add(Box.createVerticalStrut(5));
    datos.add(filaDato("Dirección", p.getDireccion()));
    datos.add(Box.createVerticalStrut(5));
    datos.add(filaDato("Términos de pago", p.getTerminosPago()));

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

    JPanel botonesRow = new JPanel(new GridLayout(1, 2, 10, 0));
    botonesRow.setOpaque(false);
    botonesRow.setPreferredSize(new Dimension(0, 40));

    JButton btnEditar = crearBotonCard("Editar", Colores.AZUL, Colores.AZUL_HOVER, true);
    btnEditar.addActionListener(e -> abrirFormulario(p));
    JButton btnDetalle = crearBotonCard("Ver Detalle", new Color(245, 246, 248), new Color(229, 231, 235), false);
    btnDetalle.addActionListener(e -> abrirDetalle(p));
    botonesRow.add(btnEditar);
    botonesRow.add(btnDetalle);

    JPanel bottom = new JPanel(new BorderLayout(0, 10));
    bottom.setOpaque(false);
    bottom.add(sep, BorderLayout.NORTH);
    bottom.add(botonesRow, BorderLayout.SOUTH);

    card.add(topRow, BorderLayout.NORTH);
    card.add(datos, BorderLayout.CENTER);
    card.add(bottom, BorderLayout.SOUTH);
    return card;
  }

  private JPanel filaDato(String etiqueta, String valor) {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    row.setOpaque(false);
    JLabel lbl = new JLabel(etiqueta + ": ");
    lbl.setFont(Fuentes.b(13));
    lbl.setForeground(Colores.TEXTO_OSCURO);
    JLabel val = new JLabel(valor);
    val.setFont(Fuentes.r(13));
    val.setForeground(Colores.TEXTO_OSCURO);
    row.add(lbl);
    row.add(val);
    return row;
  }

  private void abrirFormulario(ProveedorDTO prov) {
    boolean esNuevo = prov == null;
    JDialog dlg = new JDialog(this, esNuevo ? "Nuevo Proveedor" : "Editar Proveedor", true);
    dlg.setSize(540, 680);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(24, 28, 24, 28));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel(esNuevo ? "Nuevo Proveedor" : "Editar: " + (prov != null ? prov.getNombre() : ""));
    titulo.setFont(Fuentes.b(20));
    titulo.setForeground(Colores.TEXTO_OSCURO);
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(4));

    JLabel subtitulo = new JLabel("Ingrese la información del proveedor");
    subtitulo.setFont(Fuentes.r(13));
    subtitulo.setForeground(Colores.GRIS_TEXTO);
    subtitulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(subtitulo);
    panel.add(Box.createVerticalStrut(18));

    JLabel sep1 = new JLabel("DATOS GENERALES");
    sep1.setFont(Fuentes.b(11));
    sep1.setForeground(Colores.GRIS_TEXTO);
    sep1.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(sep1);
    panel.add(Box.createVerticalStrut(10));

    JTextField[] campos = new JTextField[7];

    JTextField tfNombre = crearCampo(esNuevo ? "" : prov.getNombre(), 50);
    campos[0] = tfNombre;
    panel.add(crearFilaCampo("Nombre *", tfNombre));
    panel.add(Box.createVerticalStrut(8));

    JTextField tfCodigo = crearCampo(esNuevo ? "" : prov.getCodigo(), 20);
    campos[1] = tfCodigo;
    panel.add(crearFilaCampo("Código", tfCodigo));
    panel.add(Box.createVerticalStrut(8));

    JTextField tfTerminos = crearCampo(esNuevo ? "" : prov.getTerminosPago(), 100);
    campos[6] = tfTerminos;
    panel.add(crearFilaCampo("Términos de pago", tfTerminos));
    panel.add(Box.createVerticalStrut(16));

    JLabel sep2 = new JLabel("CONTACTO");
    sep2.setFont(Fuentes.b(11));
    sep2.setForeground(Colores.GRIS_TEXTO);
    sep2.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(sep2);
    panel.add(Box.createVerticalStrut(10));

    JTextField tfContacto = crearCampo(esNuevo ? "" : prov.getContacto(), 100);
    campos[2] = tfContacto;
    panel.add(crearFilaCampo("Contacto", tfContacto));
    panel.add(Box.createVerticalStrut(8));

    JTextField tfTelefono = crearCampo(esNuevo ? "" : prov.getTelefono(), 20);
    campos[3] = tfTelefono;
    panel.add(crearFilaCampo("Teléfono", tfTelefono));
    panel.add(Box.createVerticalStrut(8));

    JTextField tfEmail = crearCampo(esNuevo ? "" : prov.getEmail(), 100);
    campos[4] = tfEmail;
    panel.add(crearFilaCampo("Email", tfEmail));
    panel.add(Box.createVerticalStrut(8));

    JTextField tfDireccion = crearCampo(esNuevo ? "" : prov.getDireccion(), 200);
    campos[5] = tfDireccion;
    panel.add(crearFilaCampo("Dirección", tfDireccion));
    panel.add(Box.createVerticalStrut(16));

    JPanel estadoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    estadoRow.setOpaque(false);
    estadoRow.setAlignmentX(LEFT_ALIGNMENT);
    estadoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
    JCheckBox chk = new JCheckBox("Proveedor activo", esNuevo || (prov != null && prov.isActivo()));
    chk.setFont(Fuentes.r(13));
    chk.setForeground(Colores.TEXTO_OSCURO);
    chk.setOpaque(false);
    estadoRow.add(chk);
    panel.add(estadoRow);
    panel.add(Box.createVerticalStrut(20));

    JButton btnGuardar = crearBotonAzul(esNuevo ? "Agregar Proveedor" : "Guardar Cambios", Integer.MAX_VALUE, 44);
    btnGuardar.setAlignmentX(LEFT_ALIGNMENT);
    btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    btnGuardar.addActionListener(e -> {
      if (campos[0].getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(dlg, "El nombre es obligatorio.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
      }
      if (esNuevo) {
        ProveedorDTO nuevo = new ProveedorDTO(
          campos[0].getText().trim(),
          campos[1].getText().trim().isEmpty() ? null : campos[1].getText().trim(),
          campos[2].getText().trim(), campos[3].getText().trim(),
          campos[4].getText().trim(), campos[5].getText().trim(),
          campos[6].getText().trim(), chk.isSelected());
        facade.guardarProveedor(nuevo);
        proveedores.clear();
        proveedores.addAll(facade.obtenerProveedores());
      } else {
        prov.setNombre(campos[0].getText().trim());
        prov.setCodigo(campos[1].getText().trim());
        prov.setContacto(campos[2].getText().trim());
        prov.setTelefono(campos[3].getText().trim());
        prov.setEmail(campos[4].getText().trim());
        prov.setDireccion(campos[5].getText().trim());
        prov.setTerminosPago(campos[6].getText().trim());
        prov.setActivo(chk.isSelected());
        facade.actualizarProveedor(prov);
      }
      lblActivos.setText(String.valueOf(facade.contarProveedoresActivos()));
      construirGrid(proveedores);
      dlg.dispose();
    });
    panel.add(btnGuardar);

    JScrollPane sp = new JScrollPane(panel);
    sp.setBorder(BorderFactory.createEmptyBorder());
    sp.getVerticalScrollBar().setUnitIncrement(12);
    sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    dlg.setContentPane(sp);
    dlg.setVisible(true);
  }

  private void abrirDetalle(ProveedorDTO p) {
    JDialog dlg = new JDialog(this, "Detalle del Proveedor", true);
    dlg.setSize(600, 520);
    dlg.setLocationRelativeTo(this);
    dlg.setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(28, 32, 28, 32));
    panel.setBackground(Colores.BLANCO);

    JPanel headerRow = new JPanel(new BorderLayout(10, 0));
    headerRow.setOpaque(false);
    JLabel lblN = new JLabel(p.getNombre());
    lblN.setFont(Fuentes.b(22));
    lblN.setForeground(Colores.TEXTO_OSCURO);
    JLabel badge = new JLabel(p.isActivo() ? "Activo" : "Inactivo", SwingConstants.CENTER);
    badge.setFont(Fuentes.b(11));
    badge.setForeground(p.isActivo() ? new Color(21, 128, 61) : new Color(100, 100, 100));
    badge.setOpaque(true);
    badge.setBackground(p.isActivo() ? new Color(220, 252, 231) : new Color(229, 231, 235));
    badge.setBorder(new EmptyBorder(4, 12, 4, 12));
    headerRow.add(lblN, BorderLayout.WEST);
    headerRow.add(badge, BorderLayout.EAST);

    JLabel lblCod = new JLabel("Código: " + p.getCodigo());
    lblCod.setFont(Fuentes.r(13));
    lblCod.setForeground(Colores.GRIS_TEXTO);

    panel.add(headerRow);
    panel.add(Box.createVerticalStrut(6));
    panel.add(lblCod);
    panel.add(Box.createVerticalStrut(24));

    JLabel sep = new JLabel("INFORMACIÓN DE CONTACTO");
    sep.setFont(Fuentes.b(12));
    sep.setForeground(Colores.GRIS_TEXTO);
    sep.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(sep);
    panel.add(Box.createVerticalStrut(12));

    panel.add(crearFilaInfo("Contacto", p.getContacto()));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Teléfono", p.getTelefono()));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Email", p.getEmail()));
    panel.add(Box.createVerticalStrut(8));
    panel.add(crearFilaInfo("Dirección", p.getDireccion()));
    panel.add(Box.createVerticalStrut(20));

    JLabel sep2 = new JLabel("TÉRMINOS COMERCIALES");
    sep2.setFont(Fuentes.b(12));
    sep2.setForeground(Colores.GRIS_TEXTO);
    sep2.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(sep2);
    panel.add(Box.createVerticalStrut(12));

    panel.add(crearFilaInfo("Términos de pago", p.getTerminosPago()));

    dlg.setContentPane(panel);
    dlg.setVisible(true);
  }

  private JTextField crearCampo(String valor, int limite) {
    JTextField tf = new JTextField(valor);
    tf.setFont(Fuentes.r(13));
    tf.setForeground(Colores.TEXTO_OSCURO);
    tf.setBorder(BorderFactory.createCompoundBorder(
      new Bordes(Colores.BORDE_GRIS, 1, 8),
      new EmptyBorder(8, 12, 8, 12)));
    tf.setAlignmentX(LEFT_ALIGNMENT);
    tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    tf.setPreferredSize(new Dimension(0, 40));
    if (limite > 0) {
      tf.setDocument(new javax.swing.text.PlainDocument() {
        @Override
        public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
          if (getLength() + str.length() <= limite) {
            super.insertString(offs, str, a);
          }
        }

      });
      try {
        tf.getDocument().insertString(0, valor, null);
      } catch (javax.swing.text.BadLocationException ignored) {
      }
    }
    return tf;
  }

  private JPanel crearFilaCampo(String label, JTextField campo) {
    JPanel row = new JPanel();
    row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);
    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
    JLabel l = new JLabel(label);
    l.setFont(Fuentes.b(12));
    l.setForeground(Colores.TEXTO_OSCURO);
    l.setAlignmentX(LEFT_ALIGNMENT);
    row.add(l);
    row.add(Box.createVerticalStrut(5));
    row.add(campo);
    return row;
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

  private JButton crearBotonAmarillo(String texto) {
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

  private JButton crearBotonAzul(String texto, int ancho, int alto) {
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
    b.setPreferredSize(new Dimension(ancho, alto));
    return b;
  }

  private JButton crearBotonCard(String texto, Color base, Color hov, boolean blanco) {
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
