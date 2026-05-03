package diseñadores.infraestructura.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SolicitudBancoDTO {

  private final BigDecimal monto;
  private final String referencia;
  private final Map<String, String> datos;

  private SolicitudBancoDTO(Builder b) {
    this.monto = b.monto;
    this.referencia = b.referencia;
    this.datos = Collections.unmodifiableMap(new HashMap<>(b.datos));
  }

  public BigDecimal getMonto() {
    return monto;
  }

  public String getReferencia() {
    return referencia;
  }

  public Map<String, String> getDatos() {
    return datos;
  }

  public String toJson() {
    return JsonUtil.build(
      "monto", monto,
      "referencia", referencia,
      "datos", datos
    );
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private BigDecimal monto;
    private String referencia = "";
    private Map<String, String> datos = new HashMap<>();

    public Builder monto(BigDecimal m) {
      this.monto = m;
      return this;
    }

    public Builder referencia(String ref) {
      this.referencia = ref;
      return this;
    }

    public Builder dato(String k, String v) {
      this.datos.put(k, v);
      return this;
    }

    public Builder datos(Map<String, String> map) {
      this.datos.putAll(map);
      return this;
    }

    public SolicitudBancoDTO build() {
      return new SolicitudBancoDTO(this);
    }

  }

  @Override
  public String toString() {
    return toJson();
  }

}
