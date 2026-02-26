<%-- 
    Document   : notificacion
    Descripción: Página de notificación de éxito
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
        <title>NEXAbyte - Notificación</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <section class="formulario-contenedor formulario-estrecho">
                <c:if test="${not empty mensaje}">
                    <div class="alerta alerta-exito">
                        <span class="alerta-icono">&#10003;</span> ${mensaje}
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alerta alerta-error">
                        <span class="alerta-icono">&#9888;</span> ${error}
                    </div>
                </c:if>
                <c:if test="${empty mensaje and empty error}">
                    <div class="alerta alerta-exito">
                        <span class="alerta-icono">&#10003;</span> Operación completada correctamente.
                    </div>
                </c:if>
                <div class="formulario-acciones" style="margin-top:1.5rem;">
                    <a href="${contexto}/" 
                       class="boton boton-primario"
                       title="Volver al inicio">
                        Volver al inicio
                    </a>
                    <c:if test="${not empty sessionScope.usuario}">
                        <a href="${contexto}/FrontController?op=perfil" 
                           class="boton boton-secundario"
                           title="Ir a mi perfil">
                            Mi perfil
                        </a>
                    </c:if>
                </div>
            </section>
        </main>
        
        <%@include file="/INC/pie.inc" %>
    </body>
</html>
