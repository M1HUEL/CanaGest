package diseñadores.infraestructura.notificaciones;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class NotificacionesControl {

  private final String host = "sandbox.smtp.mailtrap.io";
  private final String puerto = "2525";
  private final String usuario = "cece11e9abea53";
  private final String password = "c3502928aae23a";

  public boolean enviarNotificacionStock(String email, String mensaje) {
    validarDatos(email, mensaje);

    try {
      Session session = crearSesion();
      Message message = prepararMensaje(session, email, mensaje);
      Transport.send(message);

      System.out.println("✅ Correo enviado a Mailtrap con éxito.");
      return true;
    } catch (MessagingException e) {
      System.err.println("❌ Error al enviar a Mailtrap: " + e.getMessage());
      return false;
    }
  }

  public boolean enviarNotificacionStock(String email, String mensajeHtml, boolean esHtml) {
    validarDatos(email, mensajeHtml);
    try {
      Session session = crearSesion();
      Message message = prepararMensajeHtml(session, email, mensajeHtml);
      Transport.send(message);
      System.out.println("✅ Correo HTML enviado a Mailtrap con éxito.");
      return true;
    } catch (MessagingException e) {
      System.err.println("❌ Error al enviar a Mailtrap: " + e.getMessage());
      return false;
    }
  }

  private Message prepararMensajeHtml(Session session, String email, String mensajeHtml) throws MessagingException {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("sistema@lacanasta.com"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
    message.setSubject("⚠️ Alerta de Stock Bajo - Acción Requerida");
    message.setContent(mensajeHtml, "text/html; charset=utf-8");
    return message;
  }

  private void validarDatos(String email, String mensaje) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("El email de destino no puede estar vacío");
    }
    if (mensaje == null || mensaje.trim().isEmpty()) {
      throw new IllegalArgumentException("El mensaje de la notificación no puede estar vacío");
    }
    if (!email.contains("@")) {
      throw new IllegalArgumentException("Formato de email inválido");
    }
  }

  private Session crearSesion() {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", puerto);

    return Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(usuario, password);
      }

    });
  }

  private Message prepararMensaje(Session session, String email, String mensaje) throws MessagingException {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("sistema@lacanasta.com"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
    message.setSubject("⚠️ Alerta de Stock Bajo");
    message.setText(mensaje);
    return message;
  }

}
