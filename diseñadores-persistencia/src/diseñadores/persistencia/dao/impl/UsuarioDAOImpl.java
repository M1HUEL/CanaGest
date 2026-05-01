package diseñadores.persistencia.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import diseñadores.negocios.dto.UsuarioDTO;
import diseñadores.negocios.dto.UsuarioRol;
import diseñadores.persistencia.conexion.Conexion;
import diseñadores.persistencia.dao.IUsuarioDAO;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements IUsuarioDAO {

  private static final String COLECCION = "usuarios";
  private final MongoCollection<Document> coleccion;

  public UsuarioDAOImpl() {
    this.coleccion = Conexion.getInstancia()
      .getDatabase()
      .getCollection(COLECCION);
  }

  @Override
  public List<UsuarioDTO> obtenerTodos() {
    List<UsuarioDTO> lista = new ArrayList<>();
    for (Document doc : coleccion.find()) {
      lista.add(toDTO(doc));
    }
    return lista;
  }

  @Override
  public UsuarioDTO obtenerPorNombre(String nombre) {
    Document doc = coleccion.find(Filters.eq("nombre", nombre.toLowerCase().trim())).first();
    return (doc != null) ? toDTO(doc) : null;
  }

  @Override
  public void guardar(UsuarioDTO usuario) {
    coleccion.insertOne(toDocument(usuario));
  }

  @Override
  public void actualizar(UsuarioDTO usuario) {
    coleccion.replaceOne(
      Filters.eq("nombre", usuario.getNombre()),
      toDocument(usuario),
      new ReplaceOptions().upsert(true)
    );
  }

  @Override
  public void eliminar(String nombre) {
    coleccion.deleteOne(Filters.eq("nombre", nombre));
  }

  private UsuarioDTO toDTO(Document doc) {
    return new UsuarioDTO(
      doc.getString("nombre"),
      doc.getString("contrasena"),
      UsuarioRol.valueOf(doc.getString("rol"))
    );
  }

  private Document toDocument(UsuarioDTO dto) {
    return new Document()
      .append("nombre", dto.getNombre())
      .append("contrasena", dto.getContrasena())
      .append("rol", dto.getRol().name());
  }

}
