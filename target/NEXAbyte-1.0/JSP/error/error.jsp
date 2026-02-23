<%-- 
    Document   : error
    Descripción: Página genérica de error para excepciones no controladas
    Author     : jfco1
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contexto" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" type="text/css" href="${contexto}/CSS/estilos.css"/>
        <title>NEXAbyte - Error</title>
    </head>
    <body>
        <div class="error-pagina">
            <div class="error-logo">
                <img src="${contexto}/IMG/Logo.jpg" 
                     alt="NEXAbyte - Error" 
                     title="NEXAbyte">
            </div>
            <h1 class="error-titulo">HA OCURRIDO UN ERROR</h1>
            <p class="error-mensaje">Se ha producido un error inesperado. Por favor, inténtelo de nuevo.</p>
            <p class="error-disculpa">Disculpa por las molestias.</p>
            <a href="${contexto}/FrontController?op=inicio" class="error-btn" 
               title="Volver a la página de inicio">
                Volver al inicio
            </a>
        </div>
    </body>
</html>
