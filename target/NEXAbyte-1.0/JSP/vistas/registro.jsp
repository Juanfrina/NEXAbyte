<%-- 
    Document   : registro
    Descripción: Formulario de registro de nuevo usuario
    Author     : jfco1
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contexto" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <%@include file="/INC/metas.inc" %>
        <link rel="stylesheet" type="text/css" href="${contexto}/CSS/estilos.css"/>
        <title>NEXAbyte - Registro</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <section class="formulario-contenedor">
                <h2 class="formulario-titulo">Crear cuenta</h2>
                
                <c:if test="${not empty error}">
                    <div class="alerta alerta-error">
                        <span class="alerta-icono">&#9888;</span> ${error}
                    </div>
                </c:if>
                <c:if test="${not empty mensaje}">
                    <div class="alerta alerta-exito">
                        <span class="alerta-icono">&#10003;</span> ${mensaje}
                    </div>
                </c:if>
                
                <form action="${contexto}/RegistroController" method="POST" 
                      class="formulario" id="formRegistro"
                      enctype="multipart/form-data"
                      novalidate>
                    
                    <!-- Avatar -->
                    <div class="formulario-grupo">
                        <label class="formulario-etiqueta">Avatar</label>
                        <div class="avatar-upload-grupo">
                            <div class="avatar-preview" id="avatarPreview">
                                <img src="${contexto}/IMG/avatar/Otro.jpg" 
                                     alt="Vista previa del avatar" title="Vista previa"
                                     id="avatarPreviewImg">
                            </div>
                            <div class="avatar-opciones">
                                <label class="boton boton-secundario avatar-btn-subir" title="Subir tu propia foto">
                                    &#128247; Subir foto
                                    <input type="file" name="avatarFile" id="avatarFile" 
                                           accept="image/jpeg,image/png"
                                           style="display:none"
                                           onchange="validarYPrevisualizarAvatar(this)">
                                </label>
                                <span class="avatar-separador">o elige uno:</span>
                                <div class="avatar-selector-mini">
                                    <label class="avatar-opcion-mini" title="Avatar masculino">
                                        <input type="radio" name="avatarPredefinido" value="Hombre.jpg">
                                        <img src="${contexto}/IMG/avatar/Hombre.jpg" 
                                             alt="Avatar Hombre" title="Hombre">
                                    </label>
                                    <label class="avatar-opcion-mini" title="Avatar femenino">
                                        <input type="radio" name="avatarPredefinido" value="Mujer.jpg">
                                        <img src="${contexto}/IMG/avatar/Mujer.jpg" 
                                             alt="Avatar Mujer" title="Mujer">
                                    </label>
                                    <label class="avatar-opcion-mini" title="Avatar neutro">
                                        <input type="radio" name="avatarPredefinido" value="Otro.jpg">
                                        <img src="${contexto}/IMG/avatar/Otro.jpg" 
                                             alt="Avatar Otro" title="Otro">
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Nombre -->
                    <div class="formulario-grupo">
                        <label for="nombre" class="formulario-etiqueta">
                            Nombre <span class="obligatorio">*</span>
                        </label>
                        <input type="text" id="nombre" name="nombre" 
                               class="formulario-input" 
                               value="${param.nombre}" 
                               placeholder="Tu nombre"
                               required
                               title="Introduce tu nombre">
                    </div>
                    
                    <!-- Apellidos -->
                    <div class="formulario-grupo">
                        <label for="apellidos" class="formulario-etiqueta">
                            Apellidos <span class="obligatorio">*</span>
                        </label>
                        <input type="text" id="apellidos" name="apellidos" 
                               class="formulario-input" 
                               value="${param.apellidos}" 
                               placeholder="Tus apellidos"
                               required
                               title="Introduce tus apellidos">
                    </div>
                    
                    <!-- NIF -->
                    <div class="formulario-grupo">
                        <label for="nif" class="formulario-etiqueta">
                            NIF <span class="obligatorio">*</span>
                        </label>
                        <div style="display:flex; gap:0.5rem; align-items:center;">
                            <input type="text" id="nif" name="nif" 
                                   class="formulario-input" style="flex:1;"
                                   value="${param.nif}" 
                                   placeholder="12345678"
                                   maxlength="8" pattern="[0-9]{8}"
                                   required
                                   title="Introduce los 8 dígitos de tu NIF">
                            <span id="letraNif" class="nif-letra" 
                                  title="Letra calculada automáticamente">-</span>
                        </div>
                        <span class="formulario-feedback" id="nifFeedback"></span>
                    </div>
                    
                    <!-- Teléfono -->
                    <div class="formulario-grupo">
                        <label for="telefono" class="formulario-etiqueta">
                            Teléfono
                        </label>
                        <input type="tel" id="telefono" name="telefono" 
                               class="formulario-input" 
                               value="${param.telefono}" 
                               placeholder="612345678"
                               pattern="[6789][0-9]{8}" maxlength="9"
                               title="Teléfono de 9 dígitos que empiece por 6, 7, 8 o 9">
                        <span class="formulario-feedback" id="telefonoFeedback"></span>
                    </div>
                    
                    <!-- Email -->
                    <div class="formulario-grupo">
                        <label for="email" class="formulario-etiqueta">
                            Correo electrónico <span class="obligatorio">*</span>
                        </label>
                        <input type="email" id="email" name="email" 
                               class="formulario-input" 
                               value="${param.email}" 
                               placeholder="ejemplo@correo.com"
                               required
                               title="Introduce tu correo electrónico">
                        <span class="formulario-feedback" id="emailFeedback"></span>
                    </div>
                    
                    <!-- Dirección -->
                    <div class="formulario-grupo">
                        <label for="direccion" class="formulario-etiqueta">
                            Dirección <span class="obligatorio">*</span>
                        </label>
                        <input type="text" id="direccion" name="direccion" 
                               class="formulario-input" 
                               value="${param.direccion}" 
                               placeholder="Calle, número, piso..."
                               required
                               title="Introduce tu dirección postal">
                    </div>
                    
                    <!-- Código postal -->
                    <div class="formulario-grupo">
                        <label for="codigoPostal" class="formulario-etiqueta">
                            Código postal <span class="obligatorio">*</span>
                        </label>
                        <input type="text" id="codigoPostal" name="codigoPostal" 
                               class="formulario-input" 
                               value="${param.codigoPostal}" 
                               placeholder="28001"
                               maxlength="5" pattern="[0-9]{5}"
                               required
                               title="Introduce un código postal de 5 dígitos">
                        <span class="formulario-feedback" id="cpFeedback"></span>
                    </div>
                    
                    <!-- Localidad -->
                    <div class="formulario-grupo">
                        <label for="localidad" class="formulario-etiqueta">
                            Localidad <span class="obligatorio">*</span>
                        </label>
                        <input type="text" id="localidad" name="localidad" 
                               class="formulario-input" 
                               value="${param.localidad}" 
                               placeholder="Tu localidad"
                               required
                               title="Introduce tu localidad">
                    </div>
                    
                    <!-- Provincia -->
                    <div class="formulario-grupo">
                        <label for="provincia" class="formulario-etiqueta">
                            Provincia <span class="obligatorio">*</span>
                        </label>
                        <input type="text" id="provincia" name="provincia" 
                               class="formulario-input" 
                               value="${param.provincia}" 
                               placeholder="Tu provincia"
                               required
                               title="Introduce tu provincia">
                    </div>
                    
                    <!-- Contraseña -->
                    <div class="formulario-grupo">
                        <label for="password" class="formulario-etiqueta">
                            Contraseña <span class="obligatorio">*</span>
                        </label>
                        <div class="password-contenedor">
                            <input type="password" id="password" name="password" 
                                   class="formulario-input" 
                                   placeholder="Mínimo 6 caracteres"
                                   required minlength="6"
                                   title="Introduce una contraseña de al menos 6 caracteres">
                            <button type="button" class="password-toggle" 
                                    onclick="togglePassword('password', this)"
                                    title="Mostrar/ocultar contraseña"
                                    aria-label="Mostrar contraseña">
                                &#128065;
                            </button>
                        </div>
                        <span class="formulario-feedback" id="passwordFeedback"></span>
                    </div>
                    
                    <!-- Repetir contraseña -->
                    <div class="formulario-grupo">
                        <label for="password2" class="formulario-etiqueta">
                            Repetir contraseña <span class="obligatorio">*</span>
                        </label>
                        <div class="password-contenedor">
                            <input type="password" id="password2" name="password2" 
                                   class="formulario-input" 
                                   placeholder="Repite la contraseña"
                                   required
                                   title="Repite la contraseña para confirmar">
                            <button type="button" class="password-toggle" 
                                    onclick="togglePassword('password2', this)"
                                    title="Mostrar/ocultar contraseña"
                                    aria-label="Mostrar contraseña">
                                &#128065;
                            </button>
                        </div>
                        <span class="formulario-feedback" id="password2Feedback"></span>
                    </div>
                    
                    <!-- Botones -->
                    <div class="formulario-acciones">
                        <a href="${contexto}/FrontController?op=inicio" 
                           class="boton boton-secundario"
                           title="Volver a la página de inicio">
                            Cancelar
                        </a>
                        <button type="submit" class="boton boton-primario" 
                                title="Crear cuenta nueva">
                            Registrarse
                        </button>
                    </div>
                    
                    <p class="formulario-enlace">
                        ¿Ya tienes cuenta? 
                        <a href="${contexto}/GestionUsuario?op=formAcceder" 
                           title="Ir a la página de acceso">Acceder</a>
                    </p>
                </form>
            </section>
        </main>
        
        <%@include file="/INC/pie.inc" %>
        
        <script src="${contexto}/JS/validacion.js"></script>
    </body>
</html>
