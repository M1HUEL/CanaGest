package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.*;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.print.*;
import java.io.File;
import java.math.BigDecimal;

public class PantallaTicket extends JFrame {

  private final VentasControl control;
  private JPanel panelTicket;
  private final TicketDTO ticket;

  public PantallaTicket(JFrame mainFrame, TicketDTO ticket, Runnable onConfirmado, VentasControl control) {
    super("Ticket de Venta");
    this.control = control;
    this.ticket = ticket;
    configurarVentana(mainFrame);
    inicializarInterfaz(mainFrame, ticket, onConfirmado);
  }

  private void configurarVentana(JFrame mainFrame) {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(mainFrame.getWidth(), mainFrame.getHeight());
    setLocation(mainFrame.getLocation());
  }

  private void inicializarInterfaz(JFrame mainFrame, TicketDTO ticket, Runnable onConfirmado) {
    JPanel root = crearFondoAmarillo();
    root.add(crearTopBar(), BorderLayout.NORTH);

    panelTicket = buildTicket(ticket);
    root.add(crearContenedorCentral(), BorderLayout.CENTER);
    root.add(crearBarraInferior(mainFrame, onConfirmado), BorderLayout.SOUTH);
    setContentPane(root);
    setVisible(true);
  }

  private JPanel crearContenedorCentral() {
    JScrollPane scroll = crearScroll(panelTicket);
    JPanel centrado = new JPanel(new GridBagLayout());
    centrado.setOpaque(false);
    centrado.setBorder(new EmptyBorder(20, 0, 6, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 340, 0, 340);
    centrado.add(scroll, gbc);
    return centrado;
  }

  private JScrollPane crearScroll(JPanel contenido) {
    JScrollPane scroll = new JScrollPane(contenido);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    return scroll;
  }

  private JPanel crearBarraInferior(JFrame mainFrame, Runnable onConfirmado) {
    JPanel barra = new JPanel(new GridBagLayout());
    barra.setOpaque(false);
    barra.setBorder(new EmptyBorder(12, 340, 20, 340));
    barra.setPreferredSize(new Dimension(0, 70));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.insets = new Insets(0, 6, 0, 6);

    gbc.gridx = 0;
    barra.add(crearBotonImprimir(), gbc);

    gbc.gridx = 1;
    barra.add(crearBotonDescargar(), gbc);

    gbc.gridx = 2;
    barra.add(crearBotonFinalizar(mainFrame, onConfirmado), gbc);

    return barra;
  }

  private JButton crearBotonImprimir() {
    JButton btn = accionBtn("Imprimir Ticket", Colores.AZUL, Colores.AZUL_HOVER);
    btn.addActionListener(e -> imprimirTicket());
    return btn;
  }

  private JButton crearBotonDescargar() {
    JButton btn = accionBtn("Exportar PDF", Colores.AZUL, Colores.AZUL_HOVER);
    btn.addActionListener(e -> exportarPDF());
    return btn;
  }

  private JButton crearBotonFinalizar(JFrame mainFrame, Runnable onConfirmado) {
    JButton btn = accionBtn("Finalizar Venta", Colores.VERDE, Colores.VERDE_HOVER);
    btn.addActionListener(e -> onFinalizarVenta(mainFrame, onConfirmado));
    return btn;
  }

  private void imprimirTicket() {
    PrinterJob job = PrinterJob.getPrinterJob();
    job.setJobName("Ticket de Venta");

    Printable printable = (graphics, pageFormat, pageIndex) -> {
      if (pageIndex > 0) {
        return Printable.NO_SUCH_PAGE;
      }

      Graphics2D g2d = (Graphics2D) graphics;
      Dimension size = panelTicket.getPreferredSize();
      double scaleX = pageFormat.getImageableWidth() / size.width;
      double scaleY = pageFormat.getImageableHeight() / size.height;
      double scale = Math.min(scaleX, scaleY);

      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      g2d.scale(scale, scale);
      panelTicket.printAll(g2d);
      return Printable.PAGE_EXISTS;
    };

    job.setPrintable(printable);
    if (job.printDialog()) {
      try {
        job.print();
      } catch (PrinterException ex) {
        JOptionPane.showMessageDialog(this, "Error de impresión: " + ex.getMessage());
      }
    }
  }

  private void exportarPDF() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Guardar Ticket PDF");
    chooser.setSelectedFile(new File("Ticket_" + ticket.getFolio() + ".pdf"));
    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
      return;
    }

    File file = chooser.getSelectedFile();
    if (!file.getName().toLowerCase().endsWith(".pdf")) {
      file = new File(file.getAbsolutePath() + ".pdf");
    }

    try {
      com.itextpdf.text.Document document = new com.itextpdf.text.Document(new com.itextpdf.text.Rectangle(226, 600), 10, 10, 10, 10);
      com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
      document.open();

      TicketDTO t = this.ticket;

      com.itextpdf.text.BaseColor azulItext = new com.itextpdf.text.BaseColor(33, 150, 243);
      com.itextpdf.text.BaseColor blancoItext = com.itextpdf.text.BaseColor.WHITE;
      com.itextpdf.text.BaseColor grisItext = com.itextpdf.text.BaseColor.GRAY;

      com.itextpdf.text.Font fontTitulo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, blancoItext);
      com.itextpdf.text.Font fontSub = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL, blancoItext);
      com.itextpdf.text.Font fontNegrita = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD);
      com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL);
      com.itextpdf.text.Font fontMini = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 7, com.itextpdf.text.Font.NORMAL, grisItext);

      com.itextpdf.text.pdf.PdfPTable headTable = new com.itextpdf.text.pdf.PdfPTable(1);
      headTable.setWidthPercentage(100);

      com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell();
      cell.setBackgroundColor(azulItext);
      cell.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
      cell.setPadding(8);

      com.itextpdf.text.Paragraph p1 = new com.itextpdf.text.Paragraph("TICKET DE VENTA", fontTitulo);
      p1.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
      com.itextpdf.text.Paragraph p2 = new com.itextpdf.text.Paragraph("Sistema de Punto de Venta", fontSub);
      p2.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);

      cell.addElement(p1);
      cell.addElement(p2);
      headTable.addCell(cell);
      document.add(headTable);

      com.itextpdf.text.Paragraph pTienda = new com.itextpdf.text.Paragraph(t.getNombreTienda(), fontNegrita);
      pTienda.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
      pTienda.setSpacingBefore(10f);
      document.add(pTienda);

      com.itextpdf.text.Paragraph pInfo = new com.itextpdf.text.Paragraph("RFC: " + t.getRfc() + "\n" + t.getDireccion() + "\nTel: " + t.getTelefono(), fontMini);
      pInfo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
      document.add(pInfo);

      document.add(new com.itextpdf.text.Paragraph("--------------------------------------------------", fontMini));

      com.itextpdf.text.pdf.PdfPTable metaTable = new com.itextpdf.text.pdf.PdfPTable(2);
      metaTable.setWidthPercentage(100);

      com.itextpdf.text.pdf.PdfPCell cFecha = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Fecha: " + t.getFechaFormateada(), fontNormal));
      cFecha.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
      metaTable.addCell(cFecha);

      com.itextpdf.text.pdf.PdfPCell cHora = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Hora: " + t.getHoraFormateada(), fontNormal));
      cHora.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
      cHora.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
      metaTable.addCell(cHora);

      document.add(metaTable);
      document.add(new com.itextpdf.text.Paragraph("Cajero: " + t.getCajero(), fontNormal));
      document.add(new com.itextpdf.text.Paragraph("Folio: " + t.getFolio(), fontNegrita));

      com.itextpdf.text.Paragraph pProd = new com.itextpdf.text.Paragraph("Productos", fontNegrita);
      pProd.setSpacingBefore(5f);
      document.add(pProd);

      com.itextpdf.text.pdf.PdfPTable prodTable = new com.itextpdf.text.pdf.PdfPTable(2);
      prodTable.setWidthPercentage(100);
      prodTable.setWidths(new float[]{3, 1});

      for (ItemVentaDTO item : t.getItems()) {
        com.itextpdf.text.pdf.PdfPCell cNom = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(item.getNombre(), fontNormal));
        cNom.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
        prodTable.addCell(cNom);

        com.itextpdf.text.pdf.PdfPCell cPre = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.format("$%.2f", item.getSubtotal().doubleValue()), fontNormal));
        cPre.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
        cPre.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
        prodTable.addCell(cPre);
      }
      document.add(prodTable);

      document.add(new com.itextpdf.text.Paragraph("--------------------------------------------------", fontMini));

      com.itextpdf.text.pdf.PdfPTable resTable = new com.itextpdf.text.pdf.PdfPTable(2);
      resTable.setWidthPercentage(100);

      com.itextpdf.text.pdf.PdfPCell clSub = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Subtotal", fontNormal));
      clSub.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
      resTable.addCell(clSub);
      com.itextpdf.text.pdf.PdfPCell crSub = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.format("$%.2f", t.getSubtotal().doubleValue()), fontNormal));
      crSub.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
      crSub.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
      resTable.addCell(crSub);

      com.itextpdf.text.pdf.PdfPCell clIva = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("IVA (16%)", fontNormal));
      clIva.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
      resTable.addCell(clIva);
      com.itextpdf.text.pdf.PdfPCell crIva = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.format("$%.2f", t.getIva().doubleValue()), fontNormal));
      crIva.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
      crIva.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
      resTable.addCell(crIva);

      com.itextpdf.text.Font fontTotal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, azulItext);
      com.itextpdf.text.pdf.PdfPCell cTL = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("TOTAL", fontTotal));
      cTL.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
      resTable.addCell(cTL);
      com.itextpdf.text.pdf.PdfPCell cTR = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(String.format("$%.2f", t.getTotal().doubleValue()), fontTotal));
      cTR.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
      cTR.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
      resTable.addCell(cTR);

      document.add(resTable);

      com.itextpdf.text.Paragraph pPago = new com.itextpdf.text.Paragraph("\nMétodo de pago: " + nombreTipoPago(t.getTipoPago()), fontNormal);
      pPago.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
      document.add(pPago);

      com.itextpdf.text.Paragraph pGracias = new com.itextpdf.text.Paragraph("¡Gracias por su compra!", fontNegrita);
      pGracias.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
      pGracias.setSpacingBefore(10f);
      document.add(pGracias);

      document.close();
      JOptionPane.showMessageDialog(this, "Ticket exportado correctamente.");
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
    }
  }

  private void onFinalizarVenta(JFrame mainFrame, Runnable onConfirmado) {
    onConfirmado.run();
    dispose();
    mainFrame.setVisible(true);
  }

  private JPanel crearTopBar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
    bar.setBackground(Colores.BLANCO);
    bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE_GRIS));
    bar.add(crearBotonMenuPrincipal());
    return bar;
  }

  private JButton crearBotonMenuPrincipal() {
    JButton btn = topBarBtn("Menu Principal");
    btn.addActionListener(e -> onMenuPrincipal());
    return btn;
  }

  private void onMenuPrincipal() {
    dispose();
    new MenuPrincipal(control.getUsuarioActivo(), this.control).setVisible(true);
  }

  private JPanel buildTicket(TicketDTO t) {
    JPanel p = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.SOMBRA);
        g.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, 20, 20));
        g.setColor(Colores.BLANCO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 20, 20));
        super.paintComponent(g2d);
      }

    };
    p.setOpaque(false);
    p.add(crearCabeceraTicket(), BorderLayout.NORTH);
    p.add(crearCuerpoTicket(t), BorderLayout.CENTER);
    return p;
  }

  private JPanel crearCabeceraTicket() {
    JPanel cab = new JPanel(new GridLayout(2, 1, 0, 6)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.AZUL);
        g.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 20, 20);
        super.paintComponent(g2d);
      }

    };
    cab.setOpaque(false);
    cab.setBorder(new EmptyBorder(20, 20, 20, 20));
    cab.add(crearLabelTituloTicket());
    cab.add(crearLabelSubtituloTicket());
    return cab;
  }

  private JLabel crearLabelTituloTicket() {
    JLabel lbl = new JLabel("TICKET DE VENTA", SwingConstants.CENTER);
    lbl.setFont(Fuentes.b(20));
    lbl.setForeground(Colores.BLANCO);
    return lbl;
  }

  private JLabel crearLabelSubtituloTicket() {
    JLabel lbl = new JLabel("Sistema de Punto de Venta", SwingConstants.CENTER);
    lbl.setFont(Fuentes.r(12));
    lbl.setForeground(new Color(180, 210, 255));
    return lbl;
  }

  private JPanel crearCuerpoTicket(TicketDTO t) {
    JPanel body = new JPanel(new GridBagLayout());
    body.setOpaque(false);
    body.setBorder(new EmptyBorder(20, 28, 24, 28));

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;

    int row = 0;
    row = agregarInfoTienda(body, t, c, row);
    row = agregarMetaDatos(body, t, c, row);
    row = agregarListaProductos(body, t, c, row);
    row = agregarResumenTotales(body, t, c, row);
    agregarPieTicket(body, t, c, row);

    return body;
  }

  private int agregarInfoTienda(JPanel body, TicketDTO t, GridBagConstraints c, int row) {
    c.gridy = row++;
    body.add(fullLabel(t.getNombreTienda(), 17, true, Colores.TEXTO_OSCURO, SwingConstants.CENTER), c);
    c.gridy = row++;
    body.add(fullLabel("RFC: " + t.getRfc(), 11, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    body.add(fullLabel(t.getDireccion(), 11, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    body.add(fullLabel(t.getTelefono(), 11, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    c.insets = new Insets(14, 0, 12, 0);
    body.add(sepLine(), c);
    return row;
  }

  private int agregarMetaDatos(JPanel body, TicketDTO t, GridBagConstraints c, int row) {
    c.insets = new Insets(0, 0, 6, 0);
    c.gridy = row++;
    body.add(crearFilaMeta(t), c);
    c.gridy = row++;
    body.add(crearLabelCajero(t), c);
    c.insets = new Insets(0, 0, 14, 0);
    c.gridy = row++;
    body.add(crearCajaFolio(t.getFolio()), c);
    c.insets = new Insets(0, 0, 0, 0);
    c.gridy = row++;
    body.add(sepLine(), c);
    return row;
  }

  private JLabel crearLabelCajero(TicketDTO t) {
    JLabel lbl = new JLabel("Cajero: " + t.getCajero());
    lbl.setFont(Fuentes.r(12));
    lbl.setForeground(Colores.TEXTO_OSCURO);
    return lbl;
  }

  private int agregarListaProductos(JPanel body, TicketDTO t, GridBagConstraints c, int row) {
    c.insets = new Insets(10, 0, 10, 0);
    c.gridy = row++;
    body.add(fullLabel("Productos", 15, true, Colores.TEXTO_OSCURO, SwingConstants.LEFT), c);
    for (ItemVentaDTO item : t.getItems()) {
      c.gridy = row++;
      c.insets = new Insets(0, 0, 8, 0);
      body.add(filaProducto(item), c);
    }
    c.gridy = row++;
    c.insets = new Insets(4, 0, 12, 0);
    body.add(sepLine(), c);
    return row;
  }

  private int agregarResumenTotales(JPanel body, TicketDTO t, GridBagConstraints c, int row) {
    c.insets = new Insets(0, 0, 6, 0);
    c.gridy = row++;
    body.add(filaResumen("Subtotal", t.getSubtotal(), false), c);
    c.gridy = row++;
    body.add(filaResumen("IVA (16%)", t.getIva(), false), c);
    c.insets = new Insets(0, 0, 8, 0);
    c.gridy = row++;
    body.add(filaResumen("TOTAL", t.getTotal(), true), c);

    if (t.getEfectivoRecibido().compareTo(BigDecimal.ZERO) > 0) {
      c.gridy = row++;
      body.add(filaResumen("Efectivo recibido", t.getEfectivoRecibido(), false), c);
      c.gridy = row++;
      body.add(filaResumen("Cambio", t.getCambio(), false), c);
    }
    return row;
  }

  private void agregarPieTicket(JPanel body, TicketDTO t, GridBagConstraints c, int row) {
    c.insets = new Insets(4, 0, 12, 0);
    c.gridy = row++;
    body.add(filaTipoPago(t.getTipoPago()), c);
    c.gridy = row++;
    body.add(sepLine(), c);
    c.gridy = row++;
    body.add(fullLabel("¡Gracias por su compra!", 12, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
    c.gridy = row++;
    body.add(fullLabel("Conserve este ticket para aclaraciones", 11, false, Colores.GRIS_TEXTO, SwingConstants.CENTER), c);
  }

  private JPanel filaTipoPago(TipoPago tipo) {
    JPanel row = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(245, 245, 245));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(new EmptyBorder(10, 14, 10, 14));
    row.add(new JLabel("Método de pago") {
      {
        setFont(Fuentes.r(12));
        setForeground(Colores.GRIS_TEXTO);
      }

    }, BorderLayout.WEST);
    row.add(new JLabel(nombreTipoPago(tipo)) {
      {
        setFont(Fuentes.b(13));
        setForeground(colorTextoTipoPago(tipo));
      }

    }, BorderLayout.EAST);
    return row;
  }

  private String nombreTipoPago(TipoPago tipo) {
    if (tipo == null) {
      return "—";
    }
    return switch (tipo) {
      case EFECTIVO ->
        "Efectivo";
      case TARJETA ->
        "Tarjeta";
      case TRANSACCION ->
        "Transferencia";
      case QR ->
        "QR / CoDi";
      default ->
        tipo.name();
    };
  }

  private Color colorTextoTipoPago(TipoPago tipo) {
    if (tipo == null) {
      return Colores.GRIS_TEXTO;
    }
    return switch (tipo) {
      case EFECTIVO ->
        Colores.VERDE;
      case TARJETA ->
        Colores.AZUL;
      case TRANSACCION ->
        Colores.NARANJA;
      case QR ->
        Colores.MORADO;
      default ->
        Colores.TEXTO_OSCURO;
    };
  }

  private JPanel crearFilaMeta(TicketDTO t) {
    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    row.add(new JLabel("Fecha: " + t.getFechaFormateada()) {
      {
        setFont(Fuentes.r(12));
      }

    }, BorderLayout.WEST);
    row.add(new JLabel("Hora: " + t.getHoraFormateada()) {
      {
        setFont(Fuentes.r(12));
      }

    }, BorderLayout.EAST);
    return row;
  }

  private JPanel crearCajaFolio(String folio) {
    JPanel box = new JPanel(new GridLayout(2, 1, 0, 4)) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(240, 240, 240));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    box.setOpaque(false);
    box.setBorder(new EmptyBorder(10, 14, 10, 14));
    box.add(fullLabel("Folio de venta", 11, false, Colores.GRIS_TEXTO, SwingConstants.LEFT));
    box.add(fullLabel(folio, 15, true, Colores.TEXTO_OSCURO, SwingConstants.LEFT));
    return box;
  }

  private JPanel filaProducto(ItemVentaDTO item) {
    JPanel row = new JPanel(new GridBagLayout()) {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(250, 250, 250));
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }

    };
    row.setOpaque(false);
    row.setBorder(new EmptyBorder(10, 12, 10, 12));
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;

    c.gridx = 0;
    c.weightx = 1;
    row.add(new JLabel(item.getNombre()) {
      {
        setFont(Fuentes.b(13));
      }

    }, c);

    c.gridx = 1;
    c.weightx = 0;
    row.add(new JLabel(String.format("$%.2f", item.getSubtotal().doubleValue())) {
      {
        setFont(Fuentes.b(13));
      }

    }, c);

    return row;
  }

  private JPanel filaResumen(String etiqueta, BigDecimal valor, boolean esTotal) {
    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    row.add(fullLabel(etiqueta, esTotal ? 15 : 13, esTotal, esTotal ? Colores.TEXTO_OSCURO : Colores.GRIS_TEXTO, SwingConstants.LEFT), BorderLayout.WEST);
    row.add(fullLabel(String.format("$%.2f", valor.doubleValue()), esTotal ? 15 : 13, esTotal, esTotal ? Colores.AZUL : Colores.GRIS_TEXTO, SwingConstants.RIGHT), BorderLayout.EAST);
    return row;
  }

  private JPanel crearFondoAmarillo() {
    return new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        g.setColor(Colores.FONDO_AMARILLO);
        g.fillRect(0, 0, getWidth(), getHeight());
      }

    };
  }

  private JLabel fullLabel(String txt, int size, boolean bold, Color color, int halign) {
    JLabel l = new JLabel(txt, halign);
    l.setFont(bold ? Fuentes.b(size) : Fuentes.r(size));
    l.setForeground(color);
    return l;
  }

  private JPanel sepLine() {
    return new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setColor(Colores.BORDE_GRIS);
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{4f, 4f}, 0));
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
      }

    };
  }

  private JButton accionBtn(String texto, Color base, Color hover) {
    JButton b = new JButton(texto) {
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
        g.setColor(ov ? hover : base);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g2d);
      }

    };
    b.setForeground(Colores.BLANCO);
    b.setFont(Fuentes.b(14));
    return b;
  }

  private JButton topBarBtn(String texto) {
    return accionBtn(texto, Colores.AZUL, Colores.AZUL_HOVER);
  }

}
