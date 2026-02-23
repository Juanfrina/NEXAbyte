<%-- 
    Document   : producto
    Descripción: Vista de detalle de un producto individual
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
        <title>NEXAbyte - ${producto.nombre}</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <c:choose>
                <c:when test="${empty producto}">
                    <div class="carrito-vacio">
                        <p>Producto no encontrado</p>
                        <a href="${contexto}/FrontController?op=inicio" 
                           class="boton boton-primario"
                           title="Volver a la lista de productos">
                            Ver productos
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <section class="producto-detalle">
                        <div class="producto-detalle-img">
                            <img src="${contexto}/IMG/imagen/productos/${producto.imagen}.jpg" 
                                 alt="${producto.nombre} - ${producto.marca}" 
                                 title="${producto.nombre}"
                                 onerror="this.src='${contexto}/IMG/imagen/productos/default.jpg'">
                        </div>
                        <div class="producto-detalle-info">
                            <nav class="producto-breadcrumb">
                                <a href="${contexto}/FrontController?op=inicio" 
                                   title="Ir al inicio">Inicio</a> &rsaquo;
                                <a href="${contexto}/FrontController?op=buscar&categoria=${producto.categoria.idCategoria}" 
                                   title="Ver productos de ${producto.categoria.nombre}">
                                    ${producto.categoria.nombre}
                                </a> &rsaquo;
                                <span>${producto.nombre}</span>
                            </nav>
                            
                            <h1 class="producto-detalle-nombre">${producto.nombre}</h1>
                            
                            <span class="producto-detalle-marca">${producto.marca}</span>
                            
                            <span class="producto-detalle-categoria">
                                <img src="${contexto}/IMG/imagen/categorias/${producto.categoria.imagen}" 
                                     alt="${producto.categoria.nombre}" 
                                     title="${producto.categoria.nombre}"
                                     class="categoria-icono-sm"
                                     onerror="this.style.display='none'">
                                ${producto.categoria.nombre}
                            </span>
                            
                            <p class="producto-detalle-descripcion">${producto.descripcion}</p>
                            
                            <div class="producto-detalle-precio">
                                <span class="precio-valor">
                                    <fmt:formatNumber value="${producto.precio}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                </span>
                                <small class="precio-iva">IVA incluido</small>
                            </div>
                            
                            <div class="producto-detalle-acciones">
                                <button type="button" class="boton boton-primario boton-grande btn-anadir-carrito"
                                        data-id="${producto.idProducto}"
                                        title="Añadir ${producto.nombre} al carrito">
                                    &#128722; Añadir al carrito
                                </button>
                                <a href="${contexto}/FrontController?op=inicio" 
                                   class="boton boton-secundario"
                                   title="Volver a la lista de productos">
                                    Seguir comprando
                                </a>
                            </div>
                        </div>
                    </section>
                </c:otherwise>
            </c:choose>
        </main>
        
        <%@include file="/INC/pie.inc" %>

        <script>
        document.addEventListener("DOMContentLoaded", function() {
            document.querySelectorAll(".btn-anadir-carrito").forEach(function(btn) {
                btn.addEventListener("click", function(e) {
                    e.preventDefault();
                    var idProducto = this.getAttribute("data-id");
                    var boton = this;
                    boton.disabled = true;
                    var textoOriginal = boton.innerHTML;
                    boton.innerHTML = "&#10003; A\u00f1adido al carrito";

                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", "${contexto}/GestionPedido?op=anadir&idProducto=" + idProducto, true);
                    xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                    xhr.onreadystatechange = function() {
                        if (xhr.readyState === 4) {
                            if (xhr.status === 200) {
                                var data = JSON.parse(xhr.responseText);
                                var contador = document.getElementById("carrito-contador");
                                if (contador) {
                                    contador.textContent = data.totalArticulos;
                                    contador.style.display = "flex";
                                }
                            }
                            setTimeout(function() {
                                boton.innerHTML = textoOriginal;
                                boton.disabled = false;
                            }, 1500);
                        }
                    };
                    xhr.send();
                });
            });
        });
        </script>
    </body>
</html>
