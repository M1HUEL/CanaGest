package diseñadores.infraestructura.notificaciones;

public class NotificacionesFacade implements INotificaciones {

  private final NotificacionesControl control;

  public NotificacionesFacade() {
    this.control = new NotificacionesControl();
  }

  public NotificacionesFacade(NotificacionesControl control) {
    this.control = control;
  }

  @Override
  public boolean enviarNotificacionStock(String email, String mensaje) {
    return control.enviarNotificacionStock(email, mensaje);
  }

}
