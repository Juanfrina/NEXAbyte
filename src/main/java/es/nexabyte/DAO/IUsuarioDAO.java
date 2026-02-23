package es.nexabyte.DAO;

import es.nexabyte.beans.Usuario;

/**
 * Interfaz DAO para operaciones CRUD sobre la tabla usuarios.
 * Define los métodos de acceso a datos para la gestión de usuarios.
 * 
 * @author jfco1
 */
public interface IUsuarioDAO {

    /**
     * Inserta un nuevo usuario en la base de datos.
     * La contraseña debe estar ya encriptada en MD5.
     * 
     * @param usuario el objeto {@link Usuario} a insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    boolean insertar(Usuario usuario);

    /**
     * Obtiene un usuario por su correo electrónico.
     * 
     * @param email el correo electrónico del usuario.
     * @return el {@link Usuario} encontrado o null si no existe.
     */
    Usuario obtenerPorEmail(String email);

    /**
     * Comprueba si un correo electrónico ya está registrado.
     * 
     * @param email el correo electrónico a comprobar.
     * @return true si el email ya existe en la base de datos.
     */
    boolean existeEmail(String email);

    /**
     * Valida las credenciales de acceso de un usuario.
     * 
     * @param email el correo electrónico del usuario.
     * @param password la contraseña encriptada en MD5.
     * @return el {@link Usuario} si las credenciales son válidas, null en caso contrario.
     */
    Usuario validarCredenciales(String email, String password);

    /**
     * Actualiza los datos de un usuario existente.
     * No modifica el email ni el NIF.
     * 
     * @param usuario el objeto {@link Usuario} con los datos actualizados.
     * @return true si la actualización fue exitosa.
     */
    boolean actualizar(Usuario usuario);

    /**
     * Actualiza la fecha de último acceso del usuario.
     * 
     * @param idUsuario el identificador del usuario.
     */
    void actualizarUltimoAcceso(Short idUsuario);
}
