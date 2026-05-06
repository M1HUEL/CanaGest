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
      lista.add(convertirADTO(doc));
    }
    return lista;
  }

  @Override
  public UsuarioDTO obtenerPorNombre(String nombre) {
    validarNombreRequerido(nombre);
    Document doc = buscarDocumentoPorNombre(nombre);
    return (doc != null) ? convertirADTO(doc) : null;
  }

  @Override
  public void guardar(UsuarioDTO usuario) {
    validarDatosUsuario(usuario);
    validarNombreDisponible(usuario.getNombre());
    ejecutarInsercion(usuario);
  }

  @Override
  public void actualizar(UsuarioDTO usuario) {
    validarDatosUsuario(usuario);
    validarUsuarioExiste(usuario.getNombre());
    ejecutarReemplazo(usuario);
  }

  @Override
  public void eliminar(String nombre) {
    validarNombreRequerido(nombre);
    validarUsuarioExiste(nombre);
    ejecutarEliminacion(nombre);
  }

  private void validarNombreRequerido(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de usuario es obligatorio");
    }
  }

  private void validarDatosUsuario(UsuarioDTO usuario) {
    if (usuario == null) {
      throw new IllegalArgumentException("El usuario no puede ser nulo");
    }
    validarNombreRequerido(usuario.getNombre());
    if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
      throw new IllegalArgumentException("La contraseña es obligatoria");
    }
    if (usuario.getRol() == null) {
      throw new IllegalArgumentException("El rol del usuario es obligatorio");
    }
  }

  private void validarNombreDisponible(String nombre) {
    if (buscarDocumentoPorNombre(nombre) != null) {
      throw new IllegalStateException("El nombre de usuario ya está registrado");
    }
  }

  private void validarUsuarioExiste(String nombre) {
    if (buscarDocumentoPorNombre(nombre) == null) {
      throw new IllegalStateException("El usuario no existe");
    }
  }

  private Document buscarDocumentoPorNombre(String nombre) {
    return coleccion.find(Filters.eq("nombre", nombre.toLowerCase().trim())).first();
  }

  private void ejecutarInsercion(UsuarioDTO usuario) {
    coleccion.insertOne(convertirADocumento(usuario));
  }

  private void ejecutarReemplazo(UsuarioDTO usuario) {
    coleccion.replaceOne(
      Filters.eq("nombre", usuario.getNombre()),
      convertirADocumento(usuario),
      new ReplaceOptions().upsert(true)
    );
  }

  private void ejecutarEliminacion(String nombre) {
    coleccion.deleteOne(Filters.eq("nombre", nombre));
  }

  private UsuarioDTO convertirADTO(Document doc) {
    return new UsuarioDTO(
      doc.getString("nombre"),
      doc.getString("contrasena"),
      UsuarioRol.valueOf(doc.getString("rol"))
    );
  }

  private Document convertirADocumento(UsuarioDTO dto) {
    return new Document()
      .append("nombre", dto.getNombre().toLowerCase().trim())
      .append("contrasena", dto.getContrasena())
      .append("rol", dto.getRol().name());
  }

}
