package diseñadores.negocios.ventas.notificacion;

public interface IServicioNotificacion {

  boolean enviarNotificacionStock(String destinatario, String mensaje);

}
