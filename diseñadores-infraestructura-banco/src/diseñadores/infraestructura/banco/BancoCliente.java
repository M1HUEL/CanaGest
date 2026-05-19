package diseñadores.infraestructura.banco;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import diseñadores.infraestructura.dto.SolicitudBancoDTO;
import diseñadores.infraestructura.dto.RespuestaBancoDTO;

public final class BancoCliente implements IBanco {

  public static final int PUERTO = 8081;

  public static final String BASE_URL = "http://localhost:" + PUERTO + "/api";

  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  private final HttpClient http;

  public BancoCliente() {
    this.http = HttpClient.newBuilder()
      .connectTimeout(TIMEOUT)
      .build();
  }

  @Override
  public RespuestaBancoDTO procesarTarjeta(SolicitudBancoDTO solicitud) {
    return post(BASE_URL + "/tarjeta", solicitud);
  }

  @Override
  public RespuestaBancoDTO procesarTransferencia(SolicitudBancoDTO solicitud) {
    return post(BASE_URL + "/transferencia", solicitud);
  }

  @Override
  public RespuestaBancoDTO procesarQR(SolicitudBancoDTO solicitud) {
    return post(BASE_URL + "/qr", solicitud);
  }

  private RespuestaBancoDTO post(String url, SolicitudBancoDTO solicitud) {
    try {
      String cuerpo = solicitud.toJson();
      log("→ POST " + url);
      log("  Body: " + cuerpo);

      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(TIMEOUT)
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
        .build();

      HttpResponse<String> response
        = http.send(request, HttpResponse.BodyHandlers.ofString());

      log("← HTTP " + response.statusCode() + " " + response.body());

      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        return RespuestaBancoDTO.fromJson(response.body());
      } else {
        return RespuestaBancoDTO.error(
          "Error HTTP " + response.statusCode() + " del banco");
      }

    } catch (java.net.ConnectException ex) {
      log("✗ No se pudo conectar con el banco en " + url);
      log("  Asegúrate de haber iniciado BancoServidor.");
      return RespuestaBancoDTO.error("No se pudo conectar con el banco");

    } catch (Exception ex) {
      log("✗ Excepción al llamar al banco: " + ex.getMessage());
      return RespuestaBancoDTO.error("Error inesperado: " + ex.getMessage());
    }
  }

  private static void log(String msg) {
    System.out.println("[BancoCliente] " + msg);
  }

}
