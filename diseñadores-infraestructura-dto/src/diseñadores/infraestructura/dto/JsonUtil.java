package diseñadores.infraestructura.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonUtil {

  private JsonUtil() {
  }

  public static String build(Object... kvPairs) {
    if (kvPairs.length % 2 != 0) {
      throw new IllegalArgumentException("Pares incompletos key→value");
    }
    StringBuilder sb = new StringBuilder("{");
    for (int i = 0; i < kvPairs.length; i += 2) {
      if (i > 0) {
        sb.append(',');
      }
      String key = kvPairs[i].toString();
      Object val = kvPairs[i + 1];
      sb.append('"').append(escape(key)).append("\":");
      appendValue(sb, val);
    }
    sb.append('}');
    return sb.toString();
  }

  public static String mapToJson(Map<String, String> map) {
    StringBuilder sb = new StringBuilder("{");
    boolean first = true;
    for (Map.Entry<String, String> e : map.entrySet()) {
      if (!first) {
        sb.append(',');
      }
      sb.append('"').append(escape(e.getKey())).append("\":\"")
        .append(escape(e.getValue())).append('"');
      first = false;
    }
    sb.append('}');
    return sb.toString();
  }

  public static String getString(String json, String key) {
    Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"");
    Matcher m = p.matcher(json);
    return m.find() ? m.group(1) : null;
  }

  public static boolean getBoolean(String json, String key) {
    Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(true|false)");
    Matcher m = p.matcher(json);
    return m.find() && "true".equals(m.group(1));
  }

  public static Map<String, String> getSubMap(String json, String key) {
    Pattern outerP = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\{[^}]*\\})");
    Matcher outerM = outerP.matcher(json);
    Map<String, String> map = new HashMap<>();
    if (!outerM.find()) {
      return map;
    }
    String sub = outerM.group(1);
    Pattern entryP = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
    Matcher em = entryP.matcher(sub);
    while (em.find()) {
      map.put(em.group(1), em.group(2));
    }
    return map;
  }

  private static void appendValue(StringBuilder sb, Object val) {
    if (val == null) {
      sb.append("null");
    } else if (val instanceof Boolean) {
      sb.append(val);
    } else if (val instanceof Number) {
      sb.append(val);
    } else if (val instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, String> m = (Map<String, String>) val;
      sb.append(mapToJson(m));
    } else {
      sb.append('"').append(escape(val.toString())).append('"');
    }
  }

  private static String escape(String s) {
    return s.replace("\\", "\\\\").replace("\"", "\\\"")
      .replace("\n", "\\n").replace("\r", "\\r");
  }

}
