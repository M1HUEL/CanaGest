package diseñadores.negocios.ventas.notificacion;

public interface IServicioNotificacion {

  boolean enviarNotificacionStock(String email, String mensaje);

}
