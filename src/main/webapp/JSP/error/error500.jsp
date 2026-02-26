<%-- 
    Document   : error500
    Descripción: Página personalizada de error 500 - Error del servidor
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
        <title>NEXAbyte - Error del servidor</title>
    </head>
    <body>
        <div class="error-pagina">
            <div class="error-logo">
                <img src="${contexto}/IMG/Error500.jpg" 
                     alt="Error 500 - Error del servidor" 
                     title="Error 500">
            </div>
            <h1 class="error-titulo">ERROR DEL SERVIDOR</h1>
            <p class="error-mensaje">Algo salió mal en nuestro lado. Por favor, inténtelo más tarde.</p>
            <p class="error-disculpa">Disculpa las molestias.</p>
            <a href="${contexto}/" class="error-btn" 
               title="Volver a la página de inicio">
                Volver al inicio
            </a>
        </div>
    </body>
</html>
