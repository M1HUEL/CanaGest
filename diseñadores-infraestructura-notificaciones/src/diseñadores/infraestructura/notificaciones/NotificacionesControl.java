package diseñadores.infraestructura.notificaciones;

public class NotificacionesControl {

  public boolean enviarNotificacionStock(String email, String mensaje) {
    System.out.println("[MOCK] Enviando notificacion a: " + email);
    System.out.println("[MOCK] Mensaje: " + mensaje);
    return true;
  }

}
