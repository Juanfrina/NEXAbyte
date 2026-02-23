<%-- 
    Document   : aviso
    Descripción: Página de aviso genérico (error/advertencia)
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
        <title>NEXAbyte - Aviso</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <section class="formulario-contenedor formulario-estrecho">
                <c:if test="${not empty error}">
                    <div class="alerta alerta-error">
                        <span class="alerta-icono">&#9888;</span> ${error}
                    </div>
                </c:if>
                <c:if test="${not empty aviso}">
                    <div class="alerta alerta-advertencia">
                        <span class="alerta-icono">&#9888;</span> ${aviso}
                    </div>
                </c:if>
                <c:if test="${empty error and empty aviso}">
                    <div class="alerta alerta-advertencia">
                        <span class="alerta-icono">&#9888;</span> Ha ocurrido algo inesperado.
                    </div>
                </c:if>
                <div class="formulario-acciones" style="margin-top:1.5rem;">
                    <a href="${contexto}/FrontController?op=inicio" 
                       class="boton boton-primario"
                       title="Volver al inicio">
                        Volver al inicio
                    </a>
                </div>
            </section>
        </main>
        
        <%@include file="/INC/pie.inc" %>
    </body>
</html>
