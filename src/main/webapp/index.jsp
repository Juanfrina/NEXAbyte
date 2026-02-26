<%-- 
    Document   : index
    Created on : 5 feb. 2026, 11:46:08
    Author     : jfco1
    Descripción: Landing page / Página principal de NEXAbyte.
                 Muestra el catálogo de productos con filtros laterales (aside).
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contexto" value="${pageContext.request.contextPath}" scope="application"/>

<%-- Si no hay productos cargados (acceso directo), redirigir al FrontController --%>
<c:if test="${productos == null}">
    <jsp:forward page="/FrontController"/>
</c:if>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="/INC/metas.inc" %>
        <link rel="stylesheet" type="text/css" href="${contexto}/CSS/estilos.css"/>
        <title>NEXAbyte - Componentes Informáticos</title>
    </head>
    <body>
        <%-- CABECERA --%>
        <%@include file="/INC/cabecera.inc" %>

        <%-- CONTENIDO PRINCIPAL --%>
        <main class="contenedor-principal">
            
            <%-- ASIDE IZQUIERDO - FILTROS (estilo PCComponentes) --%>
            <aside class="aside-filtros">
                <form class="filtros-contenedor" action="${contexto}/FrontController" method="post">
                    <input type="hidden" name="op" value="buscar">
                    
                    <h3 class="filtros-titulo">&#9881; Filtros</h3>

                    <%-- Filtro: Categoría --%>
                    <div class="filtro-grupo">
                        <label for="filtroCategoria">Categoría</label>
                        <select id="filtroCategoria" name="idCategoria" 
                                title="Selecciona una categoría de producto">
                            <option value="">Todas las categorías</option>
                            <c:forEach var="cat" items="${applicationScope.categorias}">
                                <option value="${cat.idCategoria}" 
                                    ${filtroCategoria == cat.idCategoria ? 'selected' : ''}>
                                    <c:out value="${cat.nombre}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <%-- Filtro: Marca --%>
                    <div class="filtro-grupo">
                        <label for="filtroMarca">Marca</label>
                        <select id="filtroMarca" name="marca"
                                title="Selecciona una marca">
                            <option value="">Todas las marcas</option>
                            <c:forEach var="m" items="${marcas}">
                                <option value="${m}" 
                                    ${filtroMarca == m ? 'selected' : ''}>
                                    <c:out value="${m}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <%-- Filtro: Nombre / Modelo --%>
                    <div class="filtro-grupo">
                        <label for="filtroNombre">Modelo / Nombre</label>
                        <input type="text" id="filtroNombre" name="nombre" 
                               placeholder="Ej: RTX 4070, i7-14700K..."
                               title="Busca por nombre o modelo del producto"
                               value="${filtroNombre}">
                    </div>

                    <%-- Filtro: Precio (slider dual) --%>
                    <div class="filtro-grupo">
                        <label>Precio (&euro;)</label>
                        <div class="filtro-rango-slider">
                            <div class="slider-valores">
                                <span id="sliderMinLabel">0 &euro;</span>
                                <span id="sliderMaxLabel">6000 &euro;</span>
                            </div>
                            <div class="slider-track">
                                <div class="slider-track-base"></div>
                                <div class="slider-track-relleno" id="sliderRelleno"></div>
                                <input type="range" id="rangoMin" name="precioMin" min="0"
                                       max="6000"
                                       value="${not empty filtroPrecioMin ? filtroPrecioMin : 0}"
                                       step="10" class="slider-input">
                                <input type="range" id="rangoMax" name="precioMax" min="0"
                                       max="6000"
                                       value="${not empty filtroPrecioMax ? filtroPrecioMax : 6000}"
                                       step="10" class="slider-input">
                            </div>
                        </div>
                    </div>

                    <%-- Botones --%>
                    <button type="submit" class="btn-buscar" title="Aplicar filtros de búsqueda">
                        Buscar
                    </button>
                    <a href="${contexto}/" class="btn-limpiar" 
                       title="Eliminar todos los filtros" style="display:block; text-align:center;">
                        Limpiar filtros
                    </a>
                </form>
            </aside>

            <%-- CONTENIDO - GRID DE PRODUCTOS --%>
            <section class="productos-contenido">
                <div class="productos-info">
                    <h2><c:choose><c:when test="${empty filtroNombre && empty filtroCategoria && empty filtroMarca && empty filtroPrecioMin && empty filtroPrecioMax}">Productos destacados</c:when><c:otherwise>Resultados</c:otherwise></c:choose></h2>
                    <span>
                        <c:choose>
                            <c:when test="${not empty productos}">
                                ${productos.size()} producto<c:if test="${productos.size() != 1}">s</c:if> encontrado<c:if test="${productos.size() != 1}">s</c:if>
                            </c:when>
                            <c:otherwise>
                                No se encontraron productos
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <div class="productos-grid">
                    <c:choose>
                        <c:when test="${not empty productos}">
                            <c:forEach var="prod" items="${productos}">
                                <article class="tarjeta-producto" 
                                         data-id="${prod.idProducto}"
                                         data-nombre="${prod.nombre}"
                                         data-marca="${prod.marca}"
                                         data-precio="${prod.precio}"
                                         data-descripcion="${prod.descripcion}"
                                         data-imagen="${prod.imagen}"
                                         data-categoria="${prod.categoria.nombre}"
                                         data-cat-imagen="${prod.categoria.imagen}"
                                         data-cat-id="${prod.categoria.idCategoria}">
                                    <a href="javascript:void(0)" class="enlace-producto-modal"
                                       title="Ver detalles de ${prod.nombre}">
                                        <div class="tarjeta-producto-img">
                                            <img src="${contexto}/IMG/imagen/productos/${prod.imagen}.jpg" 
                                                 alt="Imagen de ${prod.nombre}" 
                                                 title="${prod.nombre}"
                                                 onerror="this.src='${contexto}/IMG/imagen/productos/default.jpg'">
                                        </div>
                                        <div class="tarjeta-producto-info">
                                            <span class="tarjeta-producto-categoria">
                                                <c:out value="${prod.categoria.nombre}"/>
                                            </span>
                                            <h3 class="tarjeta-producto-nombre">
                                                <c:out value="${prod.nombre}"/>
                                            </h3>
                                            <span class="tarjeta-producto-marca">
                                                <c:out value="${prod.marca}"/>
                                            </span>
                                            <span class="tarjeta-producto-precio">
                                                <fmt:formatNumber value="${prod.precio}" type="currency" 
                                                                  currencySymbol="&euro;" maxFractionDigits="2"/>
                                            </span>
                                        </div>
                                    </a>
                                    <div class="tarjeta-producto-acciones">
                                        <button type="button" class="btn-anadir-carrito" 
                                                data-id="${prod.idProducto}"
                                                title="Añadir ${prod.nombre} a la cesta">
                                            &#128722; Añadir a la cesta
                                        </button>
                                    </div>
                                </article>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="sin-resultados">
                                <span class="icono-grande">&#128270;</span>
                                <p>No se han encontrado productos con los filtros seleccionados.</p>
                                <a href="${contexto}/" class="error-btn" 
                                   title="Ver todos los productos" style="margin-top:1rem; display:inline-block;">
                                    Ver todos los productos
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>

        <%-- PIE DE PÁGINA --%>
        <%@include file="/INC/pie.inc" %>

        <%-- MODAL DE DETALLE DE PRODUCTO --%>
        <div id="modalProducto" class="modal-overlay" style="display:none">
            <div class="modal-contenido producto-detalle">
                <button type="button" class="modal-cerrar" title="Cerrar">&times;</button>
                <div class="producto-detalle-img">
                    <img id="modalImg" src="" alt="Producto" title="">
                </div>
                <div class="producto-detalle-info">
                    <span class="producto-detalle-categoria" id="modalCategoria">
                        <img id="modalCatImg" src="" alt="" title="" 
                             class="categoria-icono-sm" onerror="this.style.display='none'">
                        <span id="modalCatNombre"></span>
                    </span>
                    <h2 class="producto-detalle-nombre" id="modalNombre"></h2>
                    <span class="producto-detalle-marca" id="modalMarca"></span>
                    <p class="producto-detalle-descripcion" id="modalDescripcion"></p>
                    <div class="producto-detalle-precio">
                        <span class="precio-valor" id="modalPrecio"></span>
                        <small class="precio-iva">IVA incluido</small>
                    </div>
                    <div class="producto-detalle-acciones">
                        <button type="button" class="boton boton-primario boton-grande btn-anadir-carrito"
                                id="modalBtnAnadir" data-id=""
                                title="Añadir al carrito">
                            &#128722; Añadir al carrito
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <script>
        document.addEventListener("DOMContentLoaded", function() {
            var contexto = "${contexto}";
            var modal = document.getElementById("modalProducto");
            var modalImg = document.getElementById("modalImg");
            var modalNombre = document.getElementById("modalNombre");
            var modalMarca = document.getElementById("modalMarca");
            var modalPrecio = document.getElementById("modalPrecio");
            var modalDescripcion = document.getElementById("modalDescripcion");
            var modalCatNombre = document.getElementById("modalCatNombre");
            var modalCatImg = document.getElementById("modalCatImg");
            var modalBtnAnadir = document.getElementById("modalBtnAnadir");

            /* Abrir modal al clicar en la tarjeta del producto */
            document.querySelectorAll(".enlace-producto-modal").forEach(function(enlace) {
                enlace.addEventListener("click", function(e) {
                    e.preventDefault();
                    var tarjeta = this.closest(".tarjeta-producto");
                    var d = tarjeta.dataset;

                    modalImg.src = contexto + "/IMG/imagen/productos/" + d.imagen + ".jpg";
                    modalImg.alt = d.nombre;
                    modalImg.title = d.nombre;
                    modalImg.onerror = function() { this.src = contexto + '/IMG/imagen/productos/default.jpg'; };
                    modalNombre.textContent = d.nombre;
                    modalMarca.textContent = d.marca;
                    modalDescripcion.textContent = d.descripcion;
                    modalPrecio.textContent = parseFloat(d.precio).toFixed(2) + ' \u20ac';
                    modalCatNombre.textContent = d.categoria;
                    modalCatImg.src = contexto + "/IMG/imagen/categorias/" + d.catImagen;
                    modalCatImg.alt = d.categoria;
                    modalCatImg.title = d.categoria;
                    modalCatImg.style.display = '';
                    modalBtnAnadir.setAttribute("data-id", d.id);

                    modal.style.display = "flex";
                    document.body.style.overflow = "hidden";
                });
            });

            /* Cerrar modal */
            document.querySelector("#modalProducto .modal-cerrar").addEventListener("click", cerrarModal);
            modal.addEventListener("click", function(e) {
                if (e.target === modal) cerrarModal();
            });
            document.addEventListener("keydown", function(e) {
                if (e.key === "Escape" && modal.style.display === "flex") cerrarModal();
            });

            function cerrarModal() {
                modal.style.display = "none";
                document.body.style.overflow = "";
            }

            /* Añadir al carrito (cubre tarjetas + modal) */
            document.addEventListener("click", function(e) {
                var boton = e.target.closest(".btn-anadir-carrito");
                if (!boton) return;
                e.preventDefault();
                var idProducto = boton.getAttribute("data-id");
                if (!idProducto) return;
                boton.disabled = true;
                var textoOriginal = boton.innerHTML;
                boton.innerHTML = "&#10003; A\u00f1adido";

                var xhr = new XMLHttpRequest();
                xhr.open("POST", contexto + "/GestionPedido?op=anadir&idProducto=" + idProducto, true);
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
                        }, 1200);
                    }
                };
                xhr.send();
            });

            /* ===== Slider dual de precio ===== */
            var rangoMin = document.getElementById("rangoMin");
            var rangoMax = document.getElementById("rangoMax");
            var lblMin   = document.getElementById("sliderMinLabel");
            var lblMax   = document.getElementById("sliderMaxLabel");
            var relleno  = document.getElementById("sliderRelleno");

            if (rangoMin && rangoMax) {
                function actualizarSlider() {
                    var min = parseInt(rangoMin.value, 10);
                    var max = parseInt(rangoMax.value, 10);
                    /* Evitar que se crucen */
                    if (min > max) {
                        rangoMin.value = max;
                        min = max;
                    }
                    lblMin.textContent = min + " \u20ac";
                    lblMax.textContent = max + " \u20ac";
                    /* Calcular posiciones del relleno */
                    var total = parseInt(rangoMax.max, 10);
                    var pctMin = (min / total) * 100;
                    var pctMax = (max / total) * 100;
                    relleno.style.left  = pctMin + "%";
                    relleno.style.width = (pctMax - pctMin) + "%";
                }
                rangoMin.addEventListener("input", actualizarSlider);
                rangoMax.addEventListener("input", actualizarSlider);
                actualizarSlider();
            }
        });
        </script>
    </body>
</html>
