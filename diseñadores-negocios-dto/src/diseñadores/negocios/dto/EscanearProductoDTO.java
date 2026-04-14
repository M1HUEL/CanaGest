package diseñadores.negocios.dto;

public class EscanearProductoDTO {

  private final String codigo;

  public EscanearProductoDTO(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

}
