package diseñadores.infraestructura.banco;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import diseñadores.infraestructura.dto.JsonUtil;
import diseñadores.infraestructura.dto.RespuestaBancoDTO;

public final class BancoServidor {

  private BancoServidor() {
  }

  private static final BigDecimal LIMITE_TARJETA = new BigDecimal("10000");
  private static final BigDecimal LIMITE_TRANSFERENCIA = new BigDecimal("50000");
  private static final BigDecimal LIMITE_QR = new BigDecimal("8000");

  public static void iniciar(int puerto) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
    server.setExecutor(Executors.newFixedThreadPool(4));

    server.createContext("/api/tarjeta", BancoServidor::handleTarjeta);
    server.createContext("/api/transferencia", BancoServidor::handleTransferencia);
    server.createContext("/api/qr", BancoServidor::handleQR);
    server.createContext("/api/status", BancoServidor::handleStatus);

    server.start();
    log("  Banco Servidor escuchando en http://localhost:" + puerto);
    log("  Endpoints disponibles:");
    log("    POST /api/tarjeta");
    log("    POST /api/transferencia");
    log("    POST /api/qr");
    log("    GET  /api/status");
    log("═══════════════════════════════════════════════");
  }

  private static void handleTarjeta(HttpExchange ex) throws IOException {
    if (!esPost(ex)) {
      responderMetodoNoPermitido(ex);
      return;
    }

    String json = leerCuerpo(ex);
    BigDecimal monto = parseMonto(json);
    Map<String, String> datos = JsonUtil.getSubMap(json, "datos");
    String numero = datos.getOrDefault("numero", "").replaceAll("\\s", "");
    String titular = datos.getOrDefault("titular", "");

    log("[TARJETA] Solicitud recibida");
    log("          Monto:   $" + monto);
    log("          Número:  " + enmascarar(numero));
    log("          Titular: " + titular);

    RespuestaBancoDTO respuesta = evaluarTarjeta(numero, monto);
    log("          Resultado: " + (respuesta.isAprobado() ? "✓ APROBADO" : "✗ RECHAZADO")
      + " [" + respuesta.getCodigo() + "] " + respuesta.getMensaje());

    responder(ex, 200, respuesta.toJson());
  }

  private static void handleTransferencia(HttpExchange ex) throws IOException {
    if (!esPost(ex)) {
      responderMetodoNoPermitido(ex);
      return;
    }

    String json = leerCuerpo(ex);
    BigDecimal monto = parseMonto(json);
    String referencia = JsonUtil.getString(json, "referencia");
    Map<String, String> datos = JsonUtil.getSubMap(json, "datos");
    String clabe = datos.getOrDefault("clabe", "").replaceAll("\\s", "");

    log("[TRANSFERENCIA] Solicitud recibida");
    log("                Monto:     $" + monto);
    log("                CLABE:     " + enmascarar(clabe));
    log("                Referencia: " + referencia);

    RespuestaBancoDTO respuesta = evaluarTransferencia(clabe, monto, referencia);
    log("                Resultado: " + (respuesta.isAprobado() ? "✓ APROBADO" : "✗ RECHAZADO")
      + " [" + respuesta.getCodigo() + "] " + respuesta.getMensaje());

    responder(ex, 200, respuesta.toJson());
  }

  private static void handleQR(HttpExchange ex) throws IOException {
    if (!esPost(ex)) {
      responderMetodoNoPermitido(ex);
      return;
    }

    String json = leerCuerpo(ex);
    BigDecimal monto = parseMonto(json);
    String referencia = JsonUtil.getString(json, "referencia");

    log("[QR/CoDi] Solicitud recibida");
    log("          Monto:     $" + monto);
    log("          Referencia: " + referencia);

    RespuestaBancoDTO respuesta = evaluarQR(monto, referencia);
    log("          Resultado: " + (respuesta.isAprobado() ? "✓ APROBADO" : "✗ RECHAZADO")
      + " [" + respuesta.getCodigo() + "] " + respuesta.getMensaje());

    responder(ex, 200, respuesta.toJson());
  }

  private static void handleStatus(HttpExchange ex) throws IOException {
    String body = JsonUtil.build("status", "UP", "banco", "Banco Servidor MX", "version", "1.0");
    responder(ex, 200, body);
  }

  private static RespuestaBancoDTO evaluarTarjeta(String numero, BigDecimal monto) {
    if ("4111111111111111".equals(numero)) {
      return RespuestaBancoDTO.aprobado(generarAuth("TARJ"));
    }

    if ("4000000000000002".equals(numero)) {
      return RespuestaBancoDTO.rechazado("51", "Fondos insuficientes");
    }

    if ("5555555555554444".equals(numero)) {
      return RespuestaBancoDTO.rechazado("54", "Tarjeta expirada");
    }

    if (numero.length() != 16 || !numero.matches("[0-9]+")) {
      return RespuestaBancoDTO.rechazado("14", "Número de tarjeta inválido");
    }

    if (monto.compareTo(LIMITE_TARJETA) > 0) {
      return RespuestaBancoDTO.rechazado("61",
        "Límite de compra excedido ($" + LIMITE_TARJETA + ")");
    }

    if (Math.random() > 0.85) {
      return RespuestaBancoDTO.rechazado("05", "No autorizado por el banco emisor");
    }

    return RespuestaBancoDTO.aprobado(generarAuth("TARJ"));
  }

  private static RespuestaBancoDTO evaluarTransferencia(
    String clabe, BigDecimal monto, String referencia) {

    if (clabe.length() != 18 || !clabe.matches("[0-9]+")) {
      return RespuestaBancoDTO.rechazado("14", "CLABE inválida (debe tener 18 dígitos)");
    }

    if (referencia == null || referencia.isBlank()) {
      return RespuestaBancoDTO.rechazado("05", "Referencia requerida");
    }

    if (monto.compareTo(LIMITE_TRANSFERENCIA) > 0) {
      return RespuestaBancoDTO.rechazado("61",
        "Monto excede el límite SPEI ($" + LIMITE_TRANSFERENCIA + ")");
    }

    return RespuestaBancoDTO.aprobado(generarAuth("SPEI"));
  }

  private static RespuestaBancoDTO evaluarQR(BigDecimal monto, String referencia) {
    if (monto.compareTo(LIMITE_QR) > 0) {
      return RespuestaBancoDTO.rechazado("61",
        "Monto excede el límite CoDi ($" + LIMITE_QR + ")");
    }

    if (referencia == null || referencia.isBlank()) {
      return RespuestaBancoDTO.rechazado("05", "Referencia QR inválida");
    }

    return RespuestaBancoDTO.aprobado(generarAuth("CODI"));
  }

  private static String leerCuerpo(HttpExchange ex) throws IOException {
    try (InputStream is = ex.getRequestBody()) {
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private static void responder(HttpExchange ex, int codigo, String json) throws IOException {
    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
    ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
    ex.sendResponseHeaders(codigo, bytes.length);
    try (OutputStream os = ex.getResponseBody()) {
      os.write(bytes);
    }
  }

  private static void responderMetodoNoPermitido(HttpExchange ex) throws IOException {
    responder(ex, 405, "{\"error\":\"Método no permitido\"}");
  }

  private static boolean esPost(HttpExchange ex) {
    return "POST".equalsIgnoreCase(ex.getRequestMethod());
  }

  private static BigDecimal parseMonto(String json) {
    try {
      String raw = JsonUtil.getString(json, "monto");
      if (raw != null) {
        return new BigDecimal(raw);
      }
      java.util.regex.Matcher m = java.util.regex.Pattern
        .compile("\"monto\"\\s*:\\s*([0-9]+\\.?[0-9]*)")
        .matcher(json);
      return m.find() ? new BigDecimal(m.group(1)) : BigDecimal.ZERO;
    } catch (Exception e) {
      return BigDecimal.ZERO;
    }
  }

  private static String generarAuth(String prefijo) {
    return prefijo + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private static String enmascarar(String valor) {
    if (valor == null || valor.length() < 8) {
      return "****";
    }
    return valor.substring(0, 4)
      + "*".repeat(valor.length() - 8)
      + valor.substring(valor.length() - 4);
  }

  private static void log(String msg) {
    System.out.println("[BancoServidor] " + msg);
  }

}
