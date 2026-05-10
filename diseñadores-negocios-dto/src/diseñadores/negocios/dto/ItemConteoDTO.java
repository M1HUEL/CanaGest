package diseñadores.negocios.dto;

public class ItemConteoDTO {

  private String codigo;
  private String nombre;
  private int stockSistema;
  private int stockFisico;
  private boolean verificado;

  public ItemConteoDTO() {
  }

  public ItemConteoDTO(String codigo, String nombre, int stockSistema, int stockFisico) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.stockSistema = stockSistema;
    this.stockFisico = stockFisico;
    this.verificado = (stockSistema == stockFisico);
  }

  public int getDiferencia() {
    return stockFisico - stockSistema;
  }

  public String getEstado() {
    return verificado ? "Verificado" : "Pendiente";
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public int getStockSistema() {
    return stockSistema;
  }

  public void setStockSistema(int stockSistema) {
    this.stockSistema = stockSistema;
    this.verificado = (this.stockSistema == this.stockFisico);
  }

  public int getStockFisico() {
    return stockFisico;
  }

  public void setStockFisico(int stockFisico) {
    this.stockFisico = stockFisico;
    this.verificado = (this.stockSistema == this.stockFisico);
  }

  public boolean isVerificado() {
    return verificado;
  }

  public void setVerificado(boolean verificado) {
    this.verificado = verificado;
  }

}
