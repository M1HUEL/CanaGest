package diseñadores.infraestructura.banco;

import java.io.IOException;

public class Main {

  public static void main(String[] args) {
    try {
      BancoServidor.iniciar(BancoCliente.PUERTO);
    } catch (IOException ex) {
      System.getLogger(Main.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
    }
  }

}
