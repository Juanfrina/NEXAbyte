<%-- 
    Document   : carrito
    Descripción: Vista del carrito de compra con detalle de líneas de pedido
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
        <title>NEXAbyte - Carrito</title>
    </head>
    <body>
        <%@include file="/INC/cabecera.inc" %>
        
        <main class="contenedor" style="justify-content:center;">
            <section class="carrito-seccion">
                <h2 class="seccion-titulo">Mi carrito</h2>
                
                <c:if test="${not empty error}">
                    <div class="alerta alerta-error">
                        <span class="alerta-icono">&#9888;</span> ${error}
                    </div>
                </c:if>
                <c:if test="${not empty mensaje}">
                    <div class="alerta alerta-exito">
                        <span class="alerta-icono">&#10003;</span> ${mensaje}
                    </div>
                </c:if>
                
                <c:choose>
                    <c:when test="${empty lineasPedido or lineasPedido.size() == 0}">
                        <div class="carrito-vacio">
                            <span class="carrito-vacio-icono">&#128722;</span>
                            <p>Tu carrito está vacío</p>
                            <a href="${contexto}/FrontController?op=inicio" 
                               class="boton boton-primario"
                               title="Ver productos disponibles">
                                Ver productos
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="carrito-tabla">
                            <!-- Cabecera tabla -->
                            <div class="carrito-cabecera">
                                <span class="carrito-col-img">Imagen</span>
                                <span class="carrito-col-nombre">Producto</span>
                                <span class="carrito-col-precio">Precio ud.</span>
                                <span class="carrito-col-cantidad">Cantidad</span>
                                <span class="carrito-col-total">Total</span>
                                <span class="carrito-col-accion">Acción</span>
                            </div>
                            
                            <!-- Líneas del pedido -->
                            <c:set var="baseImponible" value="0"/>
                            <c:forEach var="linea" items="${lineasPedido}">
                                <c:set var="subtotal" value="${linea.producto.precio * linea.cantidad}"/>
                                <c:set var="baseImponible" value="${baseImponible + subtotal}"/>
                                <div class="carrito-item" id="linea-${linea.producto.idProducto}">
                                    <div class="carrito-col-img">
                                        <img src="${contexto}/IMG/imagen/productos/${linea.producto.imagen}.jpg" 
                                             alt="${linea.producto.nombre}" 
                                             title="${linea.producto.nombre}"
                                             class="carrito-item-img"
                                             onerror="this.src='${contexto}/IMG/imagen/productos/default.jpg'">
                                    </div>
                                    <div class="carrito-col-nombre">
                                        <a href="${contexto}/FrontController?op=verProducto&id=${linea.producto.idProducto}"
                                           title="Ver detalle de ${linea.producto.nombre}">
                                            ${linea.producto.nombre}
                                        </a>
                                        <small>${linea.producto.marca}</small>
                                    </div>
                                    <div class="carrito-col-precio">
                                        <fmt:formatNumber value="${linea.producto.precio}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                    </div>
                                    <div class="carrito-col-cantidad">
                                        <div class="cantidad-control">
                                            <button type="button" class="cantidad-btn cantidad-menos"
                                                    data-id="${linea.producto.idProducto}"
                                                    title="Disminuir cantidad"
                                                    ${linea.cantidad <= 1 ? 'disabled' : ''}>
                                                &minus;
                                            </button>
                                            <span class="cantidad-valor" id="cant-${linea.producto.idProducto}">
                                                ${linea.cantidad}
                                            </span>
                                            <button type="button" class="cantidad-btn cantidad-mas"
                                                    data-id="${linea.producto.idProducto}"
                                                    title="Aumentar cantidad">
                                                &plus;
                                            </button>
                                        </div>
                                    </div>
                                    <div class="carrito-col-total">
                                        <strong>
                                            <fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                        </strong>
                                    </div>
                                    <div class="carrito-col-accion">
                                        <button type="button" class="boton-icono boton-eliminar btn-eliminar-carrito"
                                                data-id="${linea.producto.idProducto}"
                                                title="Eliminar ${linea.producto.nombre} del carrito">
                                            &#10005;
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <!-- Resumen del pedido -->
                        <div class="carrito-resumen">
                            <div class="carrito-resumen-fila">
                                <span>Base imponible:</span>
                                <span><fmt:formatNumber value="${baseImponible}" type="currency" currencySymbol="€" maxFractionDigits="2"/></span>
                            </div>
                            <div class="carrito-resumen-fila">
                                <span>IVA (21%):</span>
                                <span><fmt:formatNumber value="${baseImponible * 0.21}" type="currency" currencySymbol="€" maxFractionDigits="2"/></span>
                            </div>
                            <div class="carrito-resumen-fila carrito-resumen-total">
                                <span>Total:</span>
                                <span><fmt:formatNumber value="${baseImponible * 1.21}" type="currency" currencySymbol="€" maxFractionDigits="2"/></span>
                            </div>
                            <div class="carrito-resumen-acciones">
                                <a href="${contexto}/FrontController?op=inicio" 
                                   class="boton boton-secundario"
                                   title="Volver a la tienda">
                                    Seguir comprando
                                </a>
                                <form method="post" action="${contexto}/GestionPedido" style="display:inline;"
                                      id="form-vaciar">
                                    <input type="hidden" name="op" value="vaciar">
                                    <button type="button" class="boton boton-secundario" id="btn-vaciar-carrito"
                                            title="Vaciar el carrito">
                                        Vaciar carrito
                                    </button>
                                </form>
                                <c:choose>
                                    <c:when test="${not empty sessionScope.usuario}">
                                        <form method="post" action="${contexto}/GestionPedido" style="display:inline;">
                                            <input type="hidden" name="op" value="finalizar">
                                            <button type="submit" class="boton boton-primario"
                                                    title="Confirmar y finalizar el pedido">
                                                Finalizar pedido
                                            </button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${contexto}/GestionUsuario?op=formAcceder" 
                                           class="boton boton-primario"
                                           title="Accede para finalizar tu pedido">
                                            Acceder para comprar
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </main>
        
        <%@include file="/INC/pie.inc" %>
        
        <script>
        document.addEventListener("DOMContentLoaded", function () {
            var contexto = "${contexto}";

            // ==================== AUMENTAR / DISMINUIR ====================

            document.querySelectorAll(".cantidad-mas").forEach(function (btn) {
                btn.addEventListener("click", function () {
                    actualizarCantidad(this.getAttribute("data-id"), "aumentar");
                });
            });

            document.querySelectorAll(".cantidad-menos").forEach(function (btn) {
                btn.addEventListener("click", function () {
                    actualizarCantidad(this.getAttribute("data-id"), "disminuir");
                });
            });

            function actualizarCantidad(idProducto, operacion) {
                var xhr = new XMLHttpRequest();
                xhr.open("POST", contexto + "/GestionPedido", true);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        var data = JSON.parse(xhr.responseText);
                        var totalArticulos = data.totalArticulos;
                        var spanCant = document.getElementById("cant-" + idProducto);
                        var lineaEl  = document.getElementById("linea-" + idProducto);

                        if (operacion === "aumentar") {
                            var nueva = parseInt(spanCant.textContent, 10) + 1;
                            spanCant.textContent = nueva;
                            // Habilitar botón menos si estaba deshabilitado
                            var btnMenos = lineaEl.querySelector(".cantidad-menos");
                            if (btnMenos) btnMenos.disabled = false;
                        } else {
                            var actual = parseInt(spanCant.textContent, 10) - 1;
                            if (actual <= 0) {
                                // El producto fue eliminado del carrito
                                lineaEl.remove();
                                comprobarCarritoVacio(totalArticulos);
                            } else {
                                spanCant.textContent = actual;
                                // Deshabilitar botón menos si llega a 1
                                if (actual <= 1) {
                                    var btnMenos = lineaEl.querySelector(".cantidad-menos");
                                    if (btnMenos) btnMenos.disabled = true;
                                }
                            }
                        }
                        recalcularTotales();
                        actualizarBadge(totalArticulos);
                    }
                };
                xhr.send("op=" + operacion + "&idProducto=" + idProducto);
            }

            // ==================== ELIMINAR PRODUCTO ====================

            document.querySelectorAll(".btn-eliminar-carrito").forEach(function (btn) {
                btn.addEventListener("click", function () {
                    var idProducto = this.getAttribute("data-id");
                    if (!confirm("¿Eliminar este producto del carrito?")) return;

                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", contexto + "/GestionPedido", true);
                    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                    xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState === 4 && xhr.status === 200) {
                            var data = JSON.parse(xhr.responseText);
                            var totalArticulos = data.totalArticulos;
                            var lineaEl = document.getElementById("linea-" + idProducto);
                            if (lineaEl) lineaEl.remove();
                            recalcularTotales();
                            actualizarBadge(totalArticulos);
                            comprobarCarritoVacio(totalArticulos);
                        }
                    };
                    xhr.send("op=eliminar&idProducto=" + idProducto);
                });
            });

            // ==================== VACIAR CARRITO ====================

            var btnVaciar = document.getElementById("btn-vaciar-carrito");
            if (btnVaciar) {
                btnVaciar.addEventListener("click", function () {
                    if (!confirm("¿Vaciar todo el carrito?")) return;

                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", contexto + "/GestionPedido", true);
                    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                    xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState === 4 && xhr.status === 200) {
                            actualizarBadge(0);
                            // Recargar para mostrar el carrito vacío
                            window.location.href = contexto + "/GestionPedido?op=verCarrito";
                        }
                    };
                    xhr.send("op=vaciar");
                });
            }

            // ==================== FUNCIONES AUXILIARES ====================

            /** Recalcula subtotales, base imponible, IVA y total en el DOM. */
            function recalcularTotales() {
                var baseImponible = 0;
                document.querySelectorAll(".carrito-item").forEach(function (item) {
                    var precioTxt = item.querySelector(".carrito-col-precio").textContent;
                    var cantidad  = parseInt(item.querySelector(".cantidad-valor").textContent, 10);
                    // Quitar todo excepto dígitos, coma y punto; eliminar puntos de miles; coma → punto decimal
                    var precio    = parseFloat(precioTxt.replace(/[^\d,.-]/g, "").replace(/\./g, "").replace(",", "."));
                    var subtotal  = precio * cantidad;
                    baseImponible += subtotal;
                    item.querySelector(".carrito-col-total strong").textContent = formatoEuro(subtotal);
                });

                var filas = document.querySelectorAll(".carrito-resumen-fila");
                if (filas.length >= 3) {
                    var iva   = baseImponible * 0.21;
                    var total = baseImponible + iva;
                    filas[0].querySelector("span:last-child").textContent = formatoEuro(baseImponible);
                    filas[1].querySelector("span:last-child").textContent = formatoEuro(iva);
                    filas[2].querySelector("span:last-child").textContent = formatoEuro(total);
                }
            }

            /**
             * Formatea un número al estilo español: punto de miles, coma decimal, símbolo €.
             * Ejemplo: 1880.00 → "1.880,00 €"
             */
            function formatoEuro(numero) {
                var partes = numero.toFixed(2).split(".");
                var entero = partes[0].replace(/\B(?=(\d{3})+(?!\d))/g, ".");
                return entero + "," + partes[1] + " \u20AC";
            }

            /** Actualiza el badge del carrito en la cabecera. */
            function actualizarBadge(total) {
                var badge = document.getElementById("carrito-contador");
                if (badge) {
                    badge.textContent = total;
                    badge.style.display = total > 0 ? "" : "none";
                }
            }

            /** Si no quedan productos, muestra mensaje de carrito vacío. */
            function comprobarCarritoVacio(totalArticulos) {
                if (totalArticulos <= 0 || document.querySelectorAll(".carrito-item").length === 0) {
                    var seccion = document.querySelector(".carrito-seccion");
                    // Eliminar tabla y resumen
                    var tabla   = seccion.querySelector(".carrito-tabla");
                    var resumen = seccion.querySelector(".carrito-resumen");
                    if (tabla)   tabla.remove();
                    if (resumen) resumen.remove();
                    // Insertar mensaje vacío
                    var vacio = document.createElement("div");
                    vacio.className = "carrito-vacio";
                    vacio.innerHTML = '<span class="carrito-vacio-icono">&#128722;</span>' +
                        '<p>Tu carrito está vacío</p>' +
                        '<a href="' + contexto + '/FrontController?op=inicio" ' +
                        'class="boton boton-primario" title="Ver productos disponibles">Ver productos</a>';
                    seccion.appendChild(vacio);
                }
            }
        });
        </script>
    </body>
</html>
