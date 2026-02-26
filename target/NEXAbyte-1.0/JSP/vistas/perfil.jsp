<%-- 
    Document   : perfil
    Descripción: Vista del perfil del usuario con sus datos
    Author     : jfco1
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contexto" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <%@include file="/INC/metas.inc" %>
        <link rel="stylesheet" type="text/css" href="${contexto}/CSS/estilos.css"/>
        <title>NEXAbyte - Mi perfil</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <c:choose>
                <c:when test="${empty sessionScope.usuario}">
                    <div class="carrito-vacio">
                        <p>Debes acceder para ver tu perfil.</p>
                        <a href="${contexto}/GestionUsuario?op=formAcceder" 
                           class="boton boton-primario"
                           title="Ir a acceder">Acceder</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:set var="u" value="${sessionScope.usuario}"/>
                    <section class="perfil-seccion">
                        <h2 class="seccion-titulo">Mi perfil</h2>
                        
                        <c:if test="${not empty sessionScope.mensajeFlash}">
                            <div class="alerta alerta-exito">
                                <span class="alerta-icono">&#10003;</span> ${sessionScope.mensajeFlash}
                            </div>
                            <c:remove var="mensajeFlash" scope="session"/>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alerta alerta-error">
                                <span class="alerta-icono">&#9888;</span> ${error}
                            </div>
                        </c:if>
                        
                        <div class="perfil-contenido">
                            <div class="perfil-avatar">
                                <c:set var="avatarImg" value="${u.avatar == 'default.png' ? 'Otro.jpg' : u.avatar}"/>
                                <img src="${contexto}/IMG/avatar/${avatarImg}" 
                                     alt="Avatar de ${u.nombre}" 
                                     title="Avatar de ${u.nombre}"
                                     onerror="this.src='${contexto}/IMG/avatar/Otro.jpg'">
                            </div>
                            <div class="perfil-datos">
                                <table class="tabla-datos">
                                    <tr>
                                        <th>Nombre:</th>
                                        <td>${u.nombre} ${u.apellidos}</td>
                                    </tr>
                                    <tr>
                                        <th>Email:</th>
                                        <td>${u.email}</td>
                                    </tr>
                                    <tr>
                                        <th>NIF:</th>
                                        <td>${u.nif}</td>
                                    </tr>
                                    <tr>
                                        <th>Teléfono:</th>
                                        <td>${not empty u.telefono ? u.telefono : '-'}</td>
                                    </tr>
                                    <tr>
                                        <th>Dirección:</th>
                                        <td>${not empty u.direccion ? u.direccion : '-'}</td>
                                    </tr>
                                    <tr>
                                        <th>Código postal:</th>
                                        <td>${not empty u.codigoPostal ? u.codigoPostal : '-'}</td>
                                    </tr>
                                    <tr>
                                        <th>Localidad:</th>
                                        <td>${not empty u.localidad ? u.localidad : '-'}</td>
                                    </tr>
                                    <tr>
                                        <th>Provincia:</th>
                                        <td>${not empty u.provincia ? u.provincia : '-'}</td>
                                    </tr>
                                    <tr>
                                        <th>Último acceso:</th>
                                        <td>
                                            <c:if test="${not empty u.ultimoAcceso}">
                                                <fmt:formatDate value="${u.ultimoAcceso}" pattern="dd/MM/yyyy HH:mm"/>
                                            </c:if>
                                            <c:if test="${empty u.ultimoAcceso}">-</c:if>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                        
                        <div class="perfil-acciones">
                            <a href="${contexto}/FrontController?op=editarPerfil" 
                               class="boton boton-primario"
                               title="Editar mis datos">
                                Editar perfil
                            </a>
                            <a href="${contexto}/FrontController?op=verPedidos" 
                               class="boton boton-secundario"
                               title="Ver mi historial de pedidos">
                                Ver mis pedidos
                            </a>
                            <a href="${contexto}/" 
                               class="boton boton-terciario"
                               title="Volver a la tienda">
                                &#128722; Seguir comprando
                            </a>
                        </div>
                    </section>
                </c:otherwise>
            </c:choose>
        </main>
        
        <%@include file="/INC/pie.inc" %>
    </body>
</html>
