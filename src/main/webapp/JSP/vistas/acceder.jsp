<%-- 
    Document   : acceder
    Descripción: Formulario de acceso (login) para usuarios registrados
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
        <title>NEXAbyte - Acceder</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <section class="formulario-contenedor formulario-estrecho">
                <h2 class="formulario-titulo">Acceder</h2>
                
                <c:if test="${not empty error}">
                    <div class="alerta alerta-error">
                        <span class="alerta-icono">&#9888;</span> ${error}
                    </div>
                </c:if>
                
                <form action="${contexto}/GestionUsuario?op=acceder" method="POST" 
                      class="formulario" novalidate autocomplete="off">
                    
                    <!-- Email -->
                    <div class="formulario-grupo">
                        <label for="email" class="formulario-etiqueta">
                            Correo electrónico
                        </label>
                        <input type="email" id="email" name="email" 
                               class="formulario-input" 
                               value="${emailAnterior}" 
                               placeholder="ejemplo@correo.com"
                               required autocomplete="username"
                               title="Introduce tu correo electrónico">
                    </div>
                    
                    <!-- Contraseña -->
                    <div class="formulario-grupo">
                        <label for="password" class="formulario-etiqueta">
                            Contraseña
                        </label>
                        <div class="password-contenedor">
                            <input type="password" id="password" name="password" 
                                   class="formulario-input" 
                                   placeholder="Tu contraseña"
                                   required autocomplete="current-password"
                                   title="Introduce tu contraseña">
                            <button type="button" class="password-toggle" 
                                    onclick="togglePassword('password', this)"
                                    title="Mostrar/ocultar contraseña"
                                    aria-label="Mostrar contraseña">
                                &#128065;
                            </button>
                        </div>
                    </div>
                    
                    <!-- Botones -->
                    <div class="formulario-acciones">
                        <a href="${contexto}/" 
                           class="boton boton-secundario"
                           title="Volver a la página de inicio">
                            Cancelar
                        </a>
                        <button type="submit" class="boton boton-primario" 
                                title="Acceder con tu cuenta">
                            Acceder
                        </button>
                    </div>
                    
                    <p class="formulario-enlace">
                        ¿No tienes cuenta? 
                        <a href="${contexto}/FrontController?op=registro" 
                           title="Crear una cuenta nueva">Registrarse</a>
                    </p>
                </form>
            </section>
        </main>
        
        <%@include file="/INC/pie.inc" %>
    </body>
</html>
