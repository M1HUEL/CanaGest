package diseñadores.negocios.dto;

public class ItemConteoDTO {

  private String codigo;
  private String comentario;
  private int diferencia;
  private int cantidadContada;
  private boolean estado;

  private String nombreUsuario;
  private String rolUsuario;

  private String productoCodigo;
  private String productoNombre;
  private int productoStockSistema;

  public ItemConteoDTO() {
  }

  public ItemConteoDTO(String codigo, String nombreUsuario,
    String rolUsuario, String productoCodigo, String productoNombre,
    int stockSistema, int stockFisico) {
    this.codigo = codigo;
    this.nombreUsuario = nombreUsuario;
    this.rolUsuario = rolUsuario;
    this.productoCodigo = productoCodigo;
    this.productoNombre = productoNombre;
    this.productoStockSistema = stockSistema;
    this.cantidadContada = stockFisico;
    this.diferencia = stockFisico - stockSistema;
    this.estado = (stockSistema == stockFisico);
  }

  private void actualizarCalculos() {
    this.diferencia = this.cantidadContada - this.productoStockSistema;
    this.estado = (this.productoStockSistema == this.cantidadContada);
  }

  public String getEstado() {
    return this.estado ? "Verificado" : "Pendiente";
  }

  public boolean isVerificado() {
    return estado;
  }

  public void setVerificado(boolean verificado) {
    this.estado = verificado;
  }

  public int getDiferencia() {
    return diferencia;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getComentario() {
    return comentario;
  }

  public void setComentario(String comentario) {
    this.comentario = comentario;
  }

  public String getNombreUsuario() {
    return nombreUsuario;
  }

  public void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
  }

  public String getRolUsuario() {
    return rolUsuario;
  }

  public void setRolUsuario(String rolUsuario) {
    this.rolUsuario = rolUsuario;
  }

  public String getProductoCodigo() {
    return productoCodigo;
  }

  public void setProductoCodigo(String productoCodigo) {
    this.productoCodigo = productoCodigo;
  }

  public String getProductoNombre() {
    return productoNombre;
  }

  public void setProductoNombre(String productoNombre) {
    this.productoNombre = productoNombre;
  }

  public int getProductoStockSistema() {
    return productoStockSistema;
  }

  public void setProductoStockSistema(int stock) {
    this.productoStockSistema = stock;
    actualizarCalculos();
  }

  public int getProductoStockFisico() {
    return cantidadContada;
  }

  public void setProductoStockFisico(int stock) {
    this.cantidadContada = stock;
    actualizarCalculos();
  }
}
