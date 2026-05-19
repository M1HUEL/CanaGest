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

public class AjusteInventario extends JDialog {

  private final VentasControl control;
  private final ItemConteoDTO item;
  private final Runnable onSuccess;
  private ConteoInventarioGeneralDTO sesionActual;

  public AjusteInventario(JFrame parent, VentasControl control, ItemConteoDTO item, Runnable onSuccess) {
    super(parent, "Ajustar Inventario", true);
    this.control = control;
    this.item = item;
    this.onSuccess = onSuccess;

    setSize(440, 370); 
    setLocationRelativeTo(parent);
    
    recuperarSesionGlobal();
    construirContenido();
  }

  private void recuperarSesionGlobal() {
      ConteoInventarioGeneralDTO activa = control.obtenerAuditoriaActiva();
      if (activa != null) {
          this.sesionActual = activa;
      } else {
          this.sesionActual = control.inicializarNuevoConteoGeneral();
      }
  }

  private void construirContenido() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(24, 28, 24, 28));
    panel.setBackground(Colores.BLANCO);

    JLabel titulo = new JLabel("Resolver Incidente: " + item.getProductoNombre());
    titulo.setFont(Fuentes.b(18));
    titulo.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(titulo);
    panel.add(Box.createVerticalStrut(14));

    JPanel infoRow = new JPanel(new GridLayout(1, 3, 12, 0));
    infoRow.setOpaque(false);
    infoRow.setAlignmentX(LEFT_ALIGNMENT);
    infoRow.add(miniCard("Sistema", String.valueOf(item.getProductoStockSistema()), Colores.AZUL));
    infoRow.add(miniCard("Físico Conteado", String.valueOf(item.getProductoStockFisico()), new Color(217, 119, 6)));

    int d = item.getDiferencia();
    String dTxt = d > 0 ? "+" + d : String.valueOf(d);
    infoRow.add(miniCard("Diferencia", dTxt, d < 0 ? Colores.ROJO : new Color(21, 128, 61)));

    panel.add(infoRow);
    panel.add(Box.createVerticalStrut(14));

    JLabel infoAjuste = new JLabel("<html>Nota: Al confirmar, la justificación y firma se guardarán en el borrador. El stock cambiará a <b>" 
        + item.getProductoStockFisico() + "</b> unidades al cerrar la auditoría global.</html>");
    infoAjuste.setFont(Fuentes.r(12));
    infoAjuste.setForeground(Colores.GRIS_TEXTO);
    infoAjuste.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(infoAjuste);
    panel.add(Box.createVerticalStrut(14));

    JLabel lblComentario = new JLabel("Motivo o Justificación del Ajuste:");
    lblComentario.setFont(Fuentes.b(13));
    lblComentario.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(lblComentario);
    panel.add(Box.createVerticalStrut(6));

    JTextArea txtComentario = new JTextArea(3, 20);
    txtComentario.setLineWrap(true);
    txtComentario.setWrapStyleWord(true);
    txtComentario.setFont(Fuentes.r(14));
    
    JScrollPane scrollComentario = new JScrollPane(txtComentario);
    scrollComentario.setBorder(BorderFactory.createCompoundBorder(
        new Bordes(Colores.BORDE_GRIS, 1, 8),
        new EmptyBorder(4, 4, 4, 4)));
    scrollComentario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
    scrollComentario.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(scrollComentario);
    panel.add(Box.createVerticalStrut(18));

    JButton btnConfirmar = crearBoton("Autorizar Ajuste en Borrador");
    btnConfirmar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    btnConfirmar.setAlignmentX(LEFT_ALIGNMENT);
    btnConfirmar.addActionListener(e -> {
        String comentario = txtComentario.getText().trim();
        
        if (comentario.isBlank()) {
            JOptionPane.showMessageDialog(this, "Por favor, agregue un comentario explicando el motivo del ajuste.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean verificadoPrevio = item.isVerificado();
        String comentarioPrevio = item.getComentario();
        String codigoUsrPrevio = item.getCodigoUsuario();
        String nombreUsrPrevio = item.getNombreUsuario();
        String rolUsrPrevio = item.getRolUsuario();
        String fechaPrevio = item.getFecha();
        
        try {
            if (sesionActual == null) {
                throw new IllegalStateException("No hay ninguna sesión de auditoría activa en MongoDB.");
            }

            String nombreActivo = control.getUsuarioActivo().getNombre();
            item.setCodigoUsuario(nombreActivo); 
            item.setNombreUsuario(nombreActivo);
            
            if (control.getUsuarioActivo().getRol() != null) {
                item.setRolUsuario(control.getUsuarioActivo().getRol().toString());
            } else {
                item.setRolUsuario("ADMINISTRADOR");
            }
            
            String fechaModificacion = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
            item.setFecha(fechaModificacion); 

            item.setComentario(comentario);
            item.setVerificado(true);

            boolean itemEncontrado = false;
            if (sesionActual.getTodosLosConteos() != null) {
                for (int i = 0; i < sesionActual.getTodosLosConteos().size(); i++) {
                    ItemConteoDTO actual = sesionActual.getTodosLosConteos().get(i);
                    
                    if (actual.getProductoCodigo() != null && item.getProductoCodigo() != null) {
                        String idActual = actual.getProductoCodigo().trim();
                        String idBuscado = item.getProductoCodigo().trim();
                        
                        if (idActual.equalsIgnoreCase(idBuscado)) {
                            sesionActual.getTodosLosConteos().set(i, item);
                            itemEncontrado = true;
                            break;
                        }
                    }
                }
            }

            if (!itemEncontrado) {
                throw new IllegalStateException("El producto " + item.getProductoNombre() + " no pertenece a la auditoría general actual.");
            }

            sesionActual.recalcularMetricas();

            control.guardarProgresoAuditoria(sesionActual);

            control.actualizarAuditoriaGeneral(sesionActual);

            JOptionPane.showMessageDialog(this, 
                "Incidente resuelto y stock sincronizado correctamente en el sistema.", 
                "Ajuste Registrado", JOptionPane.INFORMATION_MESSAGE);

            if (onSuccess != null) {
                onSuccess.run(); 
            }
            dispose();

        } catch (HeadlessException | IllegalStateException ex) {
            item.setVerificado(verificadoPrevio);
            item.setComentario(comentarioPrevio);
            item.setCodigoUsuario(codigoUsrPrevio);
            item.setNombreUsuario(nombreUsrPrevio);
            item.setRolUsuario(rolUsrPrevio);
            item.setFecha(fechaPrevio);
            
            ex.printStackTrace(); 
            
            JOptionPane.showMessageDialog(this, 
                "No se pudo completar el ajuste de stock.\nError: " + ex.getMessage(), 
                "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
        }
    });

    panel.add(btnConfirmar);
    setContentPane(panel);
  }

  private JPanel miniCard(String etiqueta, String valor, Color colorVal) {
    JPanel p = new JPanel() {
      @Override
      protected void paintComponent(Graphics g2d) {
        Graphics2D g = (Graphics2D) g2d;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Colores.FONDO_GRIS_CLARO);
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2d);
      }
    };
    p.setOpaque(false);
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(new EmptyBorder(10, 14, 10, 14));

    JLabel lE = new JLabel(etiqueta);
    lE.setFont(Fuentes.r(11));
    lE.setForeground(Colores.GRIS_TEXTO);
    JLabel lV = new JLabel(valor);
    lV.setFont(Fuentes.b(18));
    lV.setForeground(colorVal);

    p.add(lE);
    p.add(Box.createVerticalStrut(3));
    p.add(lV);
    return p;
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
    return btn;
  }
}