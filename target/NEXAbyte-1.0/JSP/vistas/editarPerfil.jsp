<%-- 
    Document   : editarPerfil
    Descripción: Formulario para editar los datos del perfil del usuario
    Author     : jfco1
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contexto" value="${pageContext.request.contextPath}"/>
<c:set var="u" value="${sessionScope.usuario}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <%@include file="/INC/metas.inc" %>
        <link rel="stylesheet" type="text/css" href="${contexto}/CSS/estilos.css"/>
        <title>NEXAbyte - Editar perfil</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <c:if test="${empty u}">
                <div class="carrito-vacio">
                    <p>Debes acceder para editar tu perfil.</p>
                    <a href="${contexto}/GestionUsuario?op=formAcceder" 
                       class="boton boton-primario" title="Ir a acceder">Acceder</a>
                </div>
            </c:if>
            <c:if test="${not empty u}">
                <section class="formulario-contenedor formulario-contenedor-amplio">
                    <h2 class="formulario-titulo">Editar perfil</h2>
                    
                    <c:if test="${not empty error}">
                        <div class="alerta alerta-error">
                            <span class="alerta-icono">&#9888;</span> ${error}
                        </div>
                    </c:if>
                    
                    <form action="${contexto}/GestionUsuario" method="POST" 
                          class="formulario" enctype="multipart/form-data">
                        <input type="hidden" name="op" value="editarPerfil">
                        
                        <!-- Navegación de pestañas -->
                        <div class="pestanas-nav">
                            <button type="button" class="pestana-btn activa" data-tab="perfil">Perfil</button>
                            <button type="button" class="pestana-btn" data-tab="direccion">Dirección</button>
                            <button type="button" class="pestana-btn" data-tab="contrasena">Contraseña</button>
                        </div>
                        
                        <!-- Pestaña Perfil -->
                        <div class="pestana-panel activa" id="tab-perfil">
                        
                        <!-- Avatar -->
                        <div class="formulario-grupo">
                            <label class="formulario-etiqueta">Avatar</label>
                            <div class="avatar-upload-grupo">
                                <div class="avatar-preview" id="avatarPreview">
                                    <c:set var="avatarImg" value="${u.avatar == 'default.png' ? 'Otro.jpg' : u.avatar}"/>
                                    <img src="${contexto}/IMG/avatar/${avatarImg}" 
                                         alt="Avatar actual" title="Tu avatar actual"
                                         id="avatarPreviewImg"
                                         onerror="this.src='${contexto}/IMG/avatar/Otro.jpg'">
                                </div>
                                <div class="avatar-opciones">
                                    <label class="boton boton-secundario avatar-btn-subir" title="Subir tu propia foto">
                                        &#128247; Cambiar foto
                                        <input type="file" name="avatarFile" id="avatarFile" 
                                               accept="image/jpeg,image/png"
                                               style="display:none"
                                               onchange="validarYPrevisualizarAvatar(this)">
                                    </label>
                                    <span class="avatar-separador">o elige uno:</span>
                                    <div class="avatar-selector-mini">
                                        <label class="avatar-opcion-mini" title="Avatar masculino">
                                            <input type="radio" name="avatarPredefinido" value="Hombre.jpg"
                                                   ${u.avatar == 'Hombre.jpg' ? 'checked' : ''}>
                                            <img src="${contexto}/IMG/avatar/Hombre.jpg" 
                                                 alt="Avatar Hombre" title="Hombre">
                                        </label>
                                        <label class="avatar-opcion-mini" title="Avatar femenino">
                                            <input type="radio" name="avatarPredefinido" value="Mujer.jpg"
                                                   ${u.avatar == 'Mujer.jpg' ? 'checked' : ''}>
                                            <img src="${contexto}/IMG/avatar/Mujer.jpg" 
                                                 alt="Avatar Mujer" title="Mujer">
                                        </label>
                                        <label class="avatar-opcion-mini" title="Avatar neutro">
                                            <input type="radio" name="avatarPredefinido" value="Otro.jpg"
                                                   ${u.avatar == 'Otro.jpg' ? 'checked' : ''}>
                                            <img src="${contexto}/IMG/avatar/Otro.jpg" 
                                                 alt="Avatar Otro" title="Otro">
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Email (no editable) -->
                        <div class="formulario-grupo">
                            <label class="formulario-etiqueta">Correo electrónico</label>
                            <input type="email" class="formulario-input" 
                                   value="${u.email}" disabled
                                   title="El email no se puede modificar">
                        </div>
                        
                        <!-- Nombre -->
                        <div class="formulario-grupo">
                            <label for="nombre" class="formulario-etiqueta">
                                Nombre <span class="obligatorio">*</span>
                            </label>
                            <input type="text" id="nombre" name="nombre" 
                                   class="formulario-input" value="${u.nombre}" 
                                   required title="Tu nombre">
                        </div>
                        
                        <!-- Apellidos -->
                        <div class="formulario-grupo">
                            <label for="apellidos" class="formulario-etiqueta">
                                Apellidos <span class="obligatorio">*</span>
                            </label>
                            <input type="text" id="apellidos" name="apellidos" 
                                   class="formulario-input" value="${u.apellidos}" 
                                   required title="Tus apellidos">
                        </div>
                        
                        <!-- NIF (no editable) -->
                        <div class="formulario-grupo">
                            <label class="formulario-etiqueta">NIF</label>
                            <input type="text" class="formulario-input" 
                                   value="${u.nif}" disabled
                                   title="El NIF no se puede modificar">
                        </div>
                        
                        <!-- Teléfono -->
                        <div class="formulario-grupo">
                            <label for="telefono" class="formulario-etiqueta">Teléfono</label>
                            <input type="tel" id="telefono" name="telefono" 
                                   class="formulario-input" value="${u.telefono}" 
                                   pattern="[6789][0-9]{8}" maxlength="9"
                                   title="Teléfono de 9 dígitos que empiece por 6, 7, 8 o 9">
                            <span class="formulario-feedback" id="telefonoFeedback"></span>
                        </div>
                        
                        <!-- Botones pestaña Perfil -->
                        <div class="formulario-acciones">
                            <button type="submit" class="boton boton-primario" 
                                    title="Guardar los cambios">
                                Modificar
                            </button>
                            <a href="${contexto}/FrontController?op=perfil" 
                               class="boton boton-secundario"
                               title="Volver al perfil sin guardar">
                                Cancelar
                            </a>
                        </div>
                        </div>
                        
                        <!-- Pestaña Dirección -->
                        <div class="pestana-panel" id="tab-direccion">
                        
                        <!-- Dirección -->
                        <div class="formulario-grupo">
                            <label for="direccion" class="formulario-etiqueta">Dirección</label>
                            <input type="text" id="direccion" name="direccion" 
                                   class="formulario-input" value="${u.direccion}" 
                                   title="Tu dirección postal">
                        </div>
                        
                        <!-- Código postal -->
                        <div class="formulario-grupo">
                            <label for="codigoPostal" class="formulario-etiqueta">Código postal</label>
                            <input type="text" id="codigoPostal" name="codigoPostal" 
                                   class="formulario-input" value="${u.codigoPostal}" 
                                   maxlength="5" pattern="[0-9]{5}"
                                   title="Código postal de 5 dígitos">
                        </div>
                        
                        <!-- Localidad -->
                        <div class="formulario-grupo">
                            <label for="localidad" class="formulario-etiqueta">Localidad</label>
                            <input type="text" id="localidad" name="localidad" 
                                   class="formulario-input" value="${u.localidad}" 
                                   title="Tu localidad">
                        </div>
                        
                        <!-- Provincia -->
                        <div class="formulario-grupo">
                            <label for="provincia" class="formulario-etiqueta">Provincia</label>
                            <input type="text" id="provincia" name="provincia" 
                                   class="formulario-input" value="${u.provincia}" 
                                   title="Tu provincia">
                        </div>
                        
                        <!-- Botones pestaña Dirección -->
                        <div class="formulario-acciones">
                            <button type="submit" class="boton boton-primario" 
                                    title="Guardar los cambios">
                                Modificar
                            </button>
                            <a href="${contexto}/FrontController?op=perfil" 
                               class="boton boton-secundario"
                               title="Volver al perfil sin guardar">
                                Cancelar
                            </a>
                        </div>
                        </div>
                        
                        <!-- Pestaña Contraseña -->
                        <div class="pestana-panel" id="tab-contrasena">
                        
                        <!-- Cambio de contraseña -->
                        <fieldset class="formulario-fieldset">
                            <legend>Cambiar contraseña <small>(dejar vacío para no cambiar)</small></legend>
                            
                            <!-- Contraseña actual -->
                            <div class="formulario-grupo">
                                <label for="passwordActual" class="formulario-etiqueta">
                                    Contraseña actual
                                </label>
                                <div class="password-contenedor">
                                    <input type="password" id="passwordActual" name="passwordActual" 
                                           class="formulario-input"
                                           placeholder="Tu contraseña actual"
                                           title="Introduce tu contraseña actual para poder cambiarla">
                                    <button type="button" class="password-toggle" 
                                            onclick="togglePassword('passwordActual', this)"
                                            title="Mostrar/ocultar contraseña"
                                            aria-label="Mostrar contraseña">
                                        &#128065;
                                    </button>
                                </div>
                            </div>
                            
                            <!-- Nueva contraseña -->
                            <div class="formulario-grupo">
                                <label for="passwordNueva" class="formulario-etiqueta">
                                    Nueva contraseña
                                </label>
                                <div class="password-contenedor">
                                    <input type="password" id="passwordNueva" name="passwordNueva" 
                                           class="formulario-input" minlength="6"
                                           placeholder="Mínimo 6 caracteres"
                                           title="Introduce la nueva contraseña">
                                    <button type="button" class="password-toggle" 
                                            onclick="togglePassword('passwordNueva', this)"
                                            title="Mostrar/ocultar contraseña"
                                            aria-label="Mostrar contraseña">
                                        &#128065;
                                    </button>
                                </div>
                            </div>
                            
                            <!-- Repetir nueva contraseña -->
                            <div class="formulario-grupo">
                                <label for="passwordNueva2" class="formulario-etiqueta">
                                    Repetir nueva contraseña
                                </label>
                                <div class="password-contenedor">
                                    <input type="password" id="passwordNueva2" name="passwordNueva2" 
                                           class="formulario-input"
                                           placeholder="Repite la nueva contraseña"
                                           title="Repite la nueva contraseña para confirmar">
                                    <button type="button" class="password-toggle" 
                                            onclick="togglePassword('passwordNueva2', this)"
                                            title="Mostrar/ocultar contraseña"
                                            aria-label="Mostrar contraseña">
                                        &#128065;
                                    </button>
                                </div>
                                <span class="formulario-feedback" id="passwordNuevaFeedback"></span>
                            </div>
                        </fieldset>
                        
                        <!-- Botones pestaña Contraseña -->
                        <div class="formulario-acciones">
                            <button type="submit" class="boton boton-primario" 
                                    title="Guardar los cambios">
                                Modificar
                            </button>
                            <a href="${contexto}/FrontController?op=perfil" 
                               class="boton boton-secundario"
                               title="Volver al perfil sin guardar">
                                Cancelar
                            </a>
                        </div>
                        </div>
                    </form>
                </section>
            </c:if>
        </main>
        
        <%@include file="/INC/pie.inc" %>
        
        <script>
        document.addEventListener("DOMContentLoaded", function() {
            /* Pestañas */
            var tabs = document.querySelectorAll(".pestana-btn");
            var panels = document.querySelectorAll(".pestana-panel");
            for (var i = 0; i < tabs.length; i++) {
                tabs[i].addEventListener("click", function() {
                    for (var j = 0; j < tabs.length; j++) {
                        tabs[j].classList.remove("activa");
                        panels[j].classList.remove("activa");
                    }
                    this.classList.add("activa");
                    document.getElementById("tab-" + this.getAttribute("data-tab")).classList.add("activa");
                });
            }
            
            /* Validación teléfono */
            var telInput = document.getElementById("telefono");
            var telFb = document.getElementById("telefonoFeedback");
            if (telInput) {
                telInput.addEventListener("input", function() {
                    this.value = this.value.replace(/[^0-9]/g, "");
                    if (this.value.length === 0) {
                        telFb.textContent = ""; telFb.className = "formulario-feedback";
                    } else if (!/^[6789]/.test(this.value)) {
                        telFb.textContent = "\u26A0 Debe empezar por 6, 7, 8 o 9";
                        telFb.className = "formulario-feedback feedback-invalido";
                    } else if (this.value.length < 9) {
                        telFb.textContent = "\u26A0 Debe tener 9 dígitos";
                        telFb.className = "formulario-feedback feedback-invalido";
                    } else {
                        telFb.textContent = "\u2713 Teléfono válido";
                        telFb.className = "formulario-feedback feedback-valido";
                    }
                });
            }
            
            /* Validación contraseña */
            var passNueva = document.getElementById("passwordNueva");
            var passNueva2 = document.getElementById("passwordNueva2");
            var feedback = document.getElementById("passwordNuevaFeedback");
            
            if (passNueva2) {
                passNueva2.addEventListener("input", function() {
                    if (this.value.length === 0) {
                        feedback.textContent = "";
                        feedback.className = "formulario-feedback";
                    } else if (this.value !== passNueva.value) {
                        feedback.textContent = "\u26A0 Las contraseñas no coinciden";
                        feedback.className = "formulario-feedback feedback-invalido";
                    } else {
                        feedback.textContent = "\u2713 Las contraseñas coinciden";
                        feedback.className = "formulario-feedback feedback-valido";
                    }
                });
            }
        });
        </script>
    </body>
</html>
