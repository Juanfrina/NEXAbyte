<%-- 
    Document   : verPedidos
    Descripción: Lista de pedidos finalizados del usuario
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
        <title>NEXAbyte - Mis pedidos</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <c:if test="${empty sessionScope.usuario}">
                <div class="carrito-vacio">
                    <p>Debes acceder para ver tus pedidos.</p>
                    <a href="${contexto}/GestionUsuario?op=formAcceder" 
                       class="boton boton-primario" title="Ir a acceder">Acceder</a>
                </div>
            </c:if>
            <c:if test="${not empty sessionScope.usuario}">
                <section class="pedidos-seccion">
                    <h2 class="seccion-titulo">Mis pedidos</h2>
                    
                    <c:choose>
                        <c:when test="${empty pedidos}">
                            <div class="carrito-vacio">
                                <p>Aún no has realizado ningún pedido.</p>
                                <a href="${contexto}/FrontController?op=inicio" 
                                   class="boton boton-primario"
                                   title="Ver productos disponibles">
                                    Ver productos
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <table class="tabla-pedidos">
                                <thead>
                                    <tr>
                                        <th>N.º Pedido</th>
                                        <th>Fecha</th>
                                        <th>Base imponible</th>
                                        <th>IVA</th>
                                        <th>Total</th>
                                        <th>Detalle</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="pedido" items="${pedidos}">
                                        <tr>
                                            <td>${pedido.idPedido}</td>
                                            <td>
                                                <fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy"/>
                                            </td>
                                            <td>
                                                <fmt:formatNumber value="${pedido.importe}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                            </td>
                                            <td>
                                                <fmt:formatNumber value="${pedido.iva}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                            </td>
                                            <td>
                                                <strong>
                                                    <fmt:formatNumber value="${pedido.importe + pedido.iva}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                                </strong>
                                            </td>
                                            <td>
                                                <a href="${contexto}/FrontController?op=verPedido&id=${pedido.idPedido}" 
                                                   class="boton boton-sm"
                                                   title="Ver detalle del pedido ${pedido.idPedido}">
                                                    Ver
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                    
                    <div class="perfil-acciones" style="margin-top:1.5rem;">
                        <a href="${contexto}/FrontController?op=perfil" 
                           class="boton boton-secundario"
                           title="Volver a mi perfil">
                            Volver al perfil
                        </a>
                    </div>
                </section>
            </c:if>
        </main>
        
        <%@include file="/INC/pie.inc" %>
    </body>
</html>
