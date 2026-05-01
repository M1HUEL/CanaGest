package diseñadores.persistencia.conexion;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Conexion {

  private static Conexion instancia;
  private MongoClient mongoClient;
  private MongoDatabase database;

  private Conexion() {
    try {
      mongoClient = MongoClients.create("mongodb://localhost:27017");
      database = mongoClient.getDatabase("canagest");
    } catch (Exception e) {
      System.err.println("Error al conectar con MongoDB: " + e.getMessage());
    }
  }

  public static synchronized Conexion getInstancia() {
    if (instancia == null) {
      instancia = new Conexion();
    }
    return instancia;
  }

  public MongoDatabase getDatabase() {
    return database;
  }

  public void cerrarConexion() {
    if (mongoClient != null) {
      mongoClient.close();
      instancia = null;
    }
  }

}
