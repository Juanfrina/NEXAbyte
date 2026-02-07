package es.nexabyte.beans;
import java.io.Serializable;
import java.sql.Timestamp;
/**
 * Clase que representa un usuario en el sistema.
 * Implementa la interfaz {@link Serializable} para permitir su serialización.
 * @author jfco1
 */
public class Usuario implements Serializable {

    /**
     * Identificador único del usuario.
     */
    private Short idUsuario;

    /**
     * Correo electrónico del usuario.
     */
    private String email;

    /**
     * Contraseña del usuario.
     */
    private String password;

    /**
     * Nombre del usuario.
     */
    private String nombre;

    /**
     * Apellidos del usuario.
     */
    private String apellidos;

    /**
     * Número de identificación fiscal (NIF) del usuario.
     */
    private String nif;

    /**
     * Teléfono de contacto del usuario.
     */
    private String telefono;

    /**
     * Dirección del usuario.
     */
    private String direccion;

    /**
     * Código postal de la dirección del usuario.
     */
    private String codigoPostal;

    /**
     * Localidad del usuario.
     */
    private String localidad;

    /**
     * Provincia del usuario.
     */
    private String provincia;

    /**
     * Último acceso del usuario en el sistema.
     */
    private Timestamp ultimoAcceso;

    /**
     * Avatar del usuario.
     */
    private String avatar;

    /**
     * Obtiene el identificador del usuario.
     *
     * @return un valor {@link Short} que representa el id del usuario.
     */
    public Short getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param idUsuario un valor {@link Short} que representa el id del usuario.
     */
    public void setIdUsuario(Short idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return un valor {@link String} que representa el correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email un valor {@link String} que representa el correo electrónico del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return un valor {@link String} que representa la contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     *
     * @param password un valor {@link String} que representa la contraseña del usuario.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @return un valor {@link String} que representa el nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre un valor {@link String} que representa el nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos del usuario.
     *
     * @return un valor {@link String} que representa los apellidos del usuario.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del usuario.
     *
     * @param apellidos un valor {@link String} que representa los apellidos del usuario.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el NIF del usuario.
     *
     * @return un valor {@link String} que representa el NIF del usuario.
     */
    public String getNif() {
        return nif;
    }

    /**
     * Establece el NIF del usuario.
     *
     * @param nif un valor {@link String} que representa el NIF del usuario.
     */
    public void setNif(String nif) {
        this.nif = nif;
    }

    /**
     * Obtiene el teléfono del usuario.
     *
     * @return un valor {@link String} que representa el teléfono del usuario.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono del usuario.
     *
     * @param telefono un valor {@link String} que representa el teléfono del usuario.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene la dirección del usuario.
     *
     * @return un valor {@link String} que representa la dirección del usuario.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección del usuario.
     *
     * @param direccion un valor {@link String} que representa la dirección del usuario.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Obtiene el código postal del usuario.
     *
     * @return un valor {@link String} que representa el código postal del usuario.
     */
    public String getCodigoPostal() {
        return codigoPostal;
    }

    /**
     * Establece el código postal del usuario.
     *
     * @param codigoPostal un valor {@link String} que representa el código postal del usuario.
     */
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    /**
     * Obtiene la localidad del usuario.
     *
     * @return un valor {@link String} que representa la localidad del usuario.
     */
    public String getLocalidad() {
        return localidad;
    }

    /**
     * Establece la localidad del usuario.
     *
     * @param localidad un valor {@link String} que representa la localidad del usuario.
     */
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    /**
     * Obtiene la provincia del usuario.
     *
     * @return un valor {@link String} que representa la provincia del usuario.
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * Establece la provincia del usuario.
     *
     * @param provincia un valor {@link String} que representa la provincia del usuario.
     */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    /**
     * Obtiene la fecha y hora del último acceso del usuario.
     *
     * @return un valor {@link Timestamp} que representa la fecha y hora del último acceso del usuario.
     */
    public Timestamp getUltimoAcceso() {
        return ultimoAcceso;
    }

    /**
     * Establece la fecha y hora del último acceso del usuario.
     *
     * @param ultimoAcceso un valor {@link Timestamp} que representa la fecha y hora del último acceso del usuario.
     */
    public void setUltimoAcceso(Timestamp ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    /**
     * Obtiene el avatar del usuario.
     *
     * @return un valor {@link String} que representa el avatar del usuario.
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Establece el avatar del usuario.
     *
     * @param avatar un valor {@link String} que representa el avatar del usuario.
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
}
