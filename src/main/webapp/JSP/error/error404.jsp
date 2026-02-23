<%-- 
    Document   : error404
    Descripción: Página personalizada de error 404 - Página no encontrada
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
        <title>NEXAbyte - Página no encontrada</title>
    </head>
    <body>
        <div class="error-pagina">
            <div class="error-logo">
                <img src="${contexto}/IMG/Error404.jpg" 
                     alt="Error 404 - Pagina no encontrada" 
                     title="Error 404">
            </div>
            <h1 class="error-titulo">PÁGINA NO ENCONTRADA</h1>
            <p class="error-mensaje">Lo sentimos, la página que buscas no existe o se ha movido.</p>
            <p class="error-disculpa">Disculpa las molestias.</p>
            <a href="${contexto}/FrontController?op=inicio" class="error-btn" 
               title="Volver a la página de inicio">
                Volver al inicio
            </a>
        </div>
    </body>
</html>
