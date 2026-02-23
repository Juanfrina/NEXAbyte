<%-- 
    Document   : pedido
    Descripción: Detalle de un pedido finalizado con sus líneas
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
        <title>NEXAbyte - Pedido #${pedido.idPedido}</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <c:choose>
                <c:when test="${empty pedido}">
                    <div class="carrito-vacio">
                        <p>Pedido no encontrado.</p>
                        <a href="${contexto}/FrontController?op=verPedidos" 
                           class="boton boton-primario"
                           title="Ver todos mis pedidos">
                            Mis pedidos
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <section class="pedido-detalle-seccion">
                        <h2 class="seccion-titulo">
                            Pedido #${pedido.idPedido}
                            <small>
                                <fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy HH:mm"/>
                            </small>
                        </h2>
                        
                        <div class="carrito-tabla">
                            <div class="carrito-cabecera">
                                <span class="carrito-col-img">Imagen</span>
                                <span class="carrito-col-nombre">Producto</span>
                                <span class="carrito-col-precio">Precio ud.</span>
                                <span class="carrito-col-cantidad">Cantidad</span>
                                <span class="carrito-col-total">Subtotal</span>
                            </div>
                            
                            <c:forEach var="linea" items="${pedido.lineasPedidos}">
                                <div class="carrito-item">
                                    <div class="carrito-col-img">
                                        <img src="${contexto}/IMG/imagen/productos/${linea.producto.imagen}.jpg" 
                                             alt="${linea.producto.nombre}" 
                                             title="${linea.producto.nombre}"
                                             class="carrito-item-img"
                                             onerror="this.src='${contexto}/IMG/imagen/productos/default.jpg'">
                                    </div>
                                    <div class="carrito-col-nombre">
                                        ${linea.producto.nombre}
                                        <small>${linea.producto.marca}</small>
                                    </div>
                                    <div class="carrito-col-precio">
                                        <fmt:formatNumber value="${linea.producto.precio}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                    </div>
                                    <div class="carrito-col-cantidad">
                                        ${linea.cantidad}
                                    </div>
                                    <div class="carrito-col-total">
                                        <strong>
                                            <fmt:formatNumber value="${linea.producto.precio * linea.cantidad}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                        </strong>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <!-- Resumen -->
                        <div class="carrito-resumen">
                            <div class="carrito-resumen-fila">
                                <span>Base imponible:</span>
                                <span><fmt:formatNumber value="${pedido.importe}" type="currency" currencySymbol="€" maxFractionDigits="2"/></span>
                            </div>
                            <div class="carrito-resumen-fila">
                                <span>IVA (21%):</span>
                                <span><fmt:formatNumber value="${pedido.iva}" type="currency" currencySymbol="€" maxFractionDigits="2"/></span>
                            </div>
                            <div class="carrito-resumen-fila carrito-resumen-total">
                                <span>Total:</span>
                                <span><fmt:formatNumber value="${pedido.importe + pedido.iva}" type="currency" currencySymbol="€" maxFractionDigits="2"/></span>
                            </div>
                        </div>
                        
                        <div class="perfil-acciones" style="margin-top:1.5rem;">
                            <a href="${contexto}/FrontController?op=verPedidos" 
                               class="boton boton-secundario"
                               title="Volver a la lista de pedidos">
                                Volver a mis pedidos
                            </a>
                        </div>
                    </section>
                </c:otherwise>
            </c:choose>
        </main>
        
        <%@include file="/INC/pie.inc" %>
    </body>
</html>
