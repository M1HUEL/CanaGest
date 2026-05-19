package diseñadores.presentacion.frame;

import diseñadores.negocios.dto.ConteoInventarioGeneralDTO;
import diseñadores.negocios.dto.ItemConteoDTO;
import diseñadores.presentacion.control.VentasControl;
import diseñadores.presentacion.utilidad.Bordes;
import diseñadores.presentacion.utilidad.Colores;
import diseñadores.presentacion.utilidad.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ConteoInventario extends JDialog {

  private final VentasControl control;
  private List<ItemConteoDTO> items; 
  private final Runnable onSuccess;
  private ConteoInventarioGeneralDTO sesionActual;

  public ConteoInventario(JFrame parent, VentasControl control, Runnable onSuccess) {
    super(parent, "Iniciar Auditoría General de Inventario", true);
    this.control = control;
    this.onSuccess = onSuccess;

    setSize(520, 550);
    setLocationRelativeTo(parent);
    
    // 1. Sincronizar dinámicamente con el flujo por lotes de VentasControl
    sincronizarSesionExistente();
    construirContenido();
  }

  /**
   * Conecta con el Controlador de Ventas para verificar si continuamos un 
   * borrador abierto en MongoDB o generamos un lote totalmente nuevo desde cero.
   */
  private void sincronizarSesionExistente() {
      // Intentamos recuperar una auditoría que haya quedado abierta (verificadoGlobal == false)
      ConteoInventarioGeneralDTO auditoriaActiva = control.obtenerAuditoriaActiva();
      
      if (auditoriaActiva != null) {
          // ESTADO: EDICIÓN - Recuperamos el progreso para que el usuario visualice lo que lleva
          this.sesionActual = auditoriaActiva;
          this.items = this.sesionActual.getTodosLosConteos();
      } else {
          // ESTADO: NUEVO CONTEO - Se genera el lote congelando el stock relacional actual
          this.sesionActual = control.inicializarNuevoConteoGeneral();
          if (this.sesionActual != null) {
              this.items = this.sesionActual.getTodosLosConteos();
          }
      }
  }

  private void construirContenido() {
    JPanel mainPanel = new JPanel(new BorderLayout());

    JPanel listPanel = new JPanel();
    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
    listPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
    listPanel.setBackground(Colores.BLANCO);

    if (items == null || items.isEmpty()) {
        JLabel lblVacio = new JLabel("No hay productos disponibles en el catálogo para auditar.");
        lblVacio.setFont(Fuentes.r(14));
        lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
        listPanel.add(lblVacio);
    } else {
        JTextField[] campos = new JTextField[items.size()];
        for (int i = 0; i < items.size(); i++) {
          ItemConteoDTO item = items.get(i);
          JPanel filaForm = new JPanel(new BorderLayout(15, 0));
          filaForm.setOpaque(false);
          filaForm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

          JLabel lbl = new JLabel(item.getProductoNombre());
          lbl.setFont(Fuentes.r(13));
          lbl.setPreferredSize(new Dimension(240, 30));

          // Muestra lo capturado físicamente. Si es nuevo lote, por defecto se asume el del sistema.
          JTextField tf = new JTextField(String.valueOf(item.getProductoStockFisico()));
          tf.setBorder(BorderFactory.createCompoundBorder(
            new Bordes(Colores.BORDE_GRIS, 1, 6),
            new EmptyBorder(4, 8, 4, 8)));
          campos[i] = tf;

          filaForm.add(lbl, BorderLayout.WEST);
          filaForm.add(tf, BorderLayout.CENTER);
          listPanel.add(filaForm);
          listPanel.add(Box.createVerticalStrut(10));
        }
        
        JButton btnGuardar = crearBoton("Cargar Lote de Conteo");
        btnGuardar.addActionListener(e -> procesarFormulario(campos));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setBackground(Colores.BLANCO);
        south.setBorder(new EmptyBorder(10, 0, 15, 0));
        south.add(btnGuardar);
        mainPanel.add(south, BorderLayout.SOUTH);
    }

    JScrollPane scroll = new JScrollPane(listPanel);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    mainPanel.add(scroll, BorderLayout.CENTER);

    setContentPane(mainPanel);
  }

  /**
   * Recoge las cantidades físicas iniciales del conteo masivo rápido, las inyecta 
   * al DTO y actualiza la persistencia en MongoDB sin disparar firmas automáticas ni comentarios.
   */
  private void procesarFormulario(JTextField[] campos) {
      try {
          if (sesionActual == null) return;

          // Guardamos las lecturas físicas iniciales que digitó el usuario en el formulario rápido
          for (int i = 0; i < items.size(); i++) {
              int stockFisicoDigitado = Integer.parseInt(campos[i].getText().trim());
              ItemConteoDTO item = items.get(i);
              
              item.setProductoStockFisico(stockFisicoDigitado);
              
              // Si coincide el stock físico con el sistema en esta primera revisión,
              // se marca automáticamente verificado para que no pida ventana de ajuste manual.
              if (item.getProductoStockSistema() == stockFisicoDigitado) {
                  item.setVerificado(true);
                  item.setComentario("Coincide correctamente.");
              } else {
                  // Queda pendiente para resolverse en la ventana de Ajuste de la tabla de consolidación
                  item.setVerificado(false);
              }
          }

          // Vinculamos y recalculamos métricas globales de la sesión (totales desfasados, etc.)
          sesionActual.setTodosLosConteos(this.items);
          
          // Guardamos de manera segura el progreso inicial en MongoDB (sin impactar stock relacional aún)
          control.guardarProgresoAuditoria(sesionActual);

          JOptionPane.showMessageDialog(this, 
              "Lote de conteo general cargado con éxito.\nUse la tabla de consolidación para resolver diferencias.", 
              "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);

          if (onSuccess != null) {
              onSuccess.run();
          }
          dispose();
          
      } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(this,
            "Por favor, ingresa solo valores numéricos válidos en las cantidades físicas.", 
            "Error de Formato", JOptionPane.WARNING_MESSAGE);
      }
  }

  private JButton crearBoton(String texto) {
    JButton btn = new JButton(texto) {
      boolean over = false;
      {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) { over = true; repaint(); }
          public void mouseExited(MouseEvent e) { over = false; repaint(); }
        });
      }

      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(over ? Colores.AZUL_HOVER : Colores.AZUL);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }
    };
    
    btn.setForeground(Colores.BLANCO);
    btn.setFont(Fuentes.b(14));
    btn.setPreferredSize(new Dimension(260, 42));
    return btn;
  }
}