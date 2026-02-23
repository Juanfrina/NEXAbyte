<header class="cabecera">
    <div class="cabecera-contenido">
        <%-- Logo --%>
        <a href="${contexto}/FrontController?op=inicio" class="logo" title="Ir al inicio de NEXAbyte">
            <img src="${contexto}/IMG/Logo.jpg" alt="NEXAbyte - Tienda de componentes" title="NEXAbyte">
        </a>

        <%-- Buscador rápido (en cabecera) --%>
        <form class="buscador-cabecera" action="${contexto}/FrontController" method="post">
            <input type="hidden" name="op" value="buscar">
            <input type="text" name="nombre" placeholder="Buscar productos..." 
                   title="Escribe el nombre del producto que buscas"
                   value="${filtroNombre}" autocomplete="off">
            <button type="submit" title="Buscar">&#128269;</button>
        </form>

        <%-- Navegación de usuario --%>
        <nav class="nav-usuario">
            <c:choose>
                <%-- Usuario registrado --%>
                <c:when test="${not empty sessionScope.usuario}">
                    <a href="${contexto}/FrontController?op=perfil" 
                       title="Ver mi perfil" class="btn-nav">
                        <span class="icono">&#128100;</span>
                        <span>${sessionScope.usuario.nombre}</span>
                    </a>
                    <form method="post" action="${contexto}/GestionUsuario" style="display:inline;">
                        <input type="hidden" name="op" value="salir">
                        <button type="submit" title="Cerrar sesi&oacute;n" class="btn-nav btn-nav-salir">
                            <span>Salir</span>
                        </button>
                    </form>
                </c:when>
                <%-- Usuario anónimo --%>
                <c:otherwise>
                    <a href="javascript:void(0)" onclick="abrirModalRegistro()"
                       title="Crear una cuenta nueva" class="btn-nav">
                        <span class="icono">&#128221;</span>
                        <span>Registrarse</span>
                    </a>
                    <a href="javascript:void(0)" onclick="abrirModalAcceder()"
                       title="Acceder a mi cuenta" class="btn-nav">
                        <span class="icono">&#128100;</span>
                        <span>Acceder</span>
                    </a>
                </c:otherwise>
            </c:choose>

            <%-- Cesta --%>
            <a href="${contexto}/FrontController?op=carrito" 
               title="Ver mi cesta" class="btn-nav carrito-badge">
                <span class="icono">&#128722;</span>
                <span>Mi cesta</span>
                <span class="contador" id="carrito-contador"
                      style="${empty sessionScope.numArticulos or sessionScope.numArticulos == 0 ? 'display:none' : ''}">
                    ${sessionScope.numArticulos != null ? sessionScope.numArticulos : 0}
                </span>
            </a>
        </nav>
    </div>
</header>
