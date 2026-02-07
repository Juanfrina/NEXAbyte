<%-- 
    Document   : index
    Created on : 5 feb. 2026, 11:46:08
    Author     : jfco1
--%>

<%@page import="java.util.List"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:directive.page contentType="text/html" pageEncoding="UTF-8"/>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="estilo" value="/CSS/estilos.css" scope="application" />
<c:set var="contexto" value="${pageContext.request.contextPath}" scope="application"/>
<!DOCTYPE html>
<html lang="es">
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <jsp:include page="/INC/metas.inc"/>
        <link rel="stylesheet" type="text/css" href="${contexto}/CSS/estilos.css"/>
        <title>NEXAbyte</title>
    </head>
    <body>
        <h1>Hello World!</h1>
    </body>
</html>
