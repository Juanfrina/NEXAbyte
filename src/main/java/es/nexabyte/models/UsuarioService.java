package es.nexabyte.models;

import es.nexabyte.DAO.IUsuarioDAO;
import es.nexabyte.DAOFactory.DAOFactory;
import es.nexabyte.beans.Usuario;

/**
 * Servicio de lógica de negocio para la gestión de usuarios.
 * Centraliza la autenticación, registro y edición de perfil.
 *
 * @author jfco1
 */
public class UsuarioService {

    /**
     * Valida las credenciales del usuario.
     *
     * @param email el correo electrónico.
     * @param password la contraseña en texto plano.
     * @return el Usuario si las credenciales son válidas, null si no.
     */
    public Usuario autenticar(String email, String password) {
        String passwordMD5 = SecurityUtils.encriptarMD5(password);
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        return usuarioDAO.validarCredenciales(email, passwordMD5);
    }

    /**
     * Registra el último acceso del usuario.
     *
     * @param idUsuario el id del usuario.
     */
    public void registrarUltimoAcceso(Short idUsuario) {
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        usuarioDAO.actualizarUltimoAcceso(idUsuario);
    }

    /**
     * Comprueba si un email ya existe en la BD.
     *
     * @param email el correo a comprobar.
     * @return true si ya existe.
     */
    public boolean existeEmail(String email) {
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        return usuarioDAO.existeEmail(email.trim());
    }

    /**
     * Actualiza los datos del perfil de un usuario.
     *
     * @param usuario el usuario con los datos actualizados.
     * @param nuevaPassword la nueva contraseña (null o vacía si no se cambia).
     * @return true si se actualizó correctamente.
     */
    public boolean actualizarPerfil(Usuario usuario, String nuevaPassword) {
        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            usuario.setPassword(SecurityUtils.encriptarMD5(nuevaPassword));
        }
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        return usuarioDAO.actualizar(usuario);
    }

    /**
     * Inserta un nuevo usuario en la BD.
     *
     * @param usuario el usuario a insertar.
     * @return true si se insertó correctamente.
     */
    public boolean registrar(Usuario usuario) {
        IUsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
        return usuarioDAO.insertar(usuario);
    }
}
