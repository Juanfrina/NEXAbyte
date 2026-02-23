<p align="center">
  <img src="src/main/webapp/IMG/imagen/categorias/NEXAbyte_logo.png" alt="NEXAbyte Logo" width="260"/>
</p>

<h1 align="center">NEXAbyte — Tienda Online de Componentes Informáticos</h1>

<p align="center">
  Aplicación web Java EE desarrollada como proyecto académico.<br/>
  Simula una tienda de hardware al estilo <em>PcComponentes</em> con gestión completa de usuarios, catálogo, carrito y pedidos.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java%20EE-7-orange?style=flat-square&logo=java" alt="Java EE 7"/>
  <img src="https://img.shields.io/badge/Servlets-3.1-blue?style=flat-square" alt="Servlets 3.1"/>
  <img src="https://img.shields.io/badge/JSP%20%2B%20JSTL-2.3-green?style=flat-square" alt="JSP + JSTL"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL 8"/>
  <img src="https://img.shields.io/badge/Maven-WAR-C71A36?style=flat-square&logo=apachemaven" alt="Maven"/>
  <img src="https://img.shields.io/badge/Tomcat-8.x%20%2F%209.x-F8DC75?style=flat-square&logo=apachetomcat" alt="Tomcat"/>
</p>

---

## 📋 Índice

1. [Descripción del Proyecto](#-descripción-del-proyecto)
2. [Funcionalidades](#-funcionalidades)
3. [Arquitectura y Patrones de Diseño](#-arquitectura-y-patrones-de-diseño)
4. [Tecnologías Utilizadas](#-tecnologías-utilizadas)
5. [Estructura del Proyecto](#-estructura-del-proyecto)
6. [Modelo de Datos](#-modelo-de-datos)
7. [Requisitos Previos](#-requisitos-previos)
8. [Instalación y Despliegue](#-instalación-y-despliegue)
9. [Configuración de la Base de Datos](#-configuración-de-la-base-de-datos)
10. [Capturas de Pantalla](#-capturas-de-pantalla)
11. [Autor](#-autor)

---

## 🛒 Descripción del Proyecto

**NEXAbyte** es una aplicación web de comercio electrónico especializada en componentes informáticos (procesadores, tarjetas gráficas, memorias RAM, discos duros, etc.). Ha sido desarrollada íntegramente con **Java EE 7** sin utilizar frameworks de alto nivel como Spring, siguiendo los requisitos del módulo de **Desarrollo Web en Entorno Servidor (DWES)**.

La idea es sencilla: un usuario entra en la web, puede navegar por el catálogo de productos, filtrarlos por categoría/marca/precio, añadirlos al carrito y, una vez registrado e identificado, tramitar sus pedidos. Todo ello con una interfaz moderna de temática oscura, responsiva y accesible.

### ¿Qué se puede hacer?

- **Sin registrarse**: navegar por el catálogo, buscar y filtrar productos, ver el detalle de cada producto en un modal, y añadir productos a un carrito temporal (guardado en cookies).
- **Registrado e identificado**: todo lo anterior, pero además el carrito se persiste en base de datos, se pueden tramitar pedidos, consultar el historial de compras, y gestionar el perfil (incluido subir un avatar personalizado).

---

## ✨ Funcionalidades

### Catálogo de Productos
- **Página principal** con 6 productos aleatorios destacados (se refrescan en cada visita).
- **Buscador** por nombre en la cabecera.
- **Filtros laterales** (_aside_): categoría, marca, rango de precio.
- **Detalle de producto** mediante ventana modal (sin cambiar de página).

### Gestión de Usuarios
- **Registro** con validación completa en cliente y servidor:
  - Campos: nombre, apellidos, NIF (con cálculo automático de letra), teléfono, email (comprobación de duplicados en tiempo real por AJAX), dirección, código postal, localidad, provincia y contraseña.
  - Avatar personalizado: subida de imagen (solo JPG/PNG, máximo 100 KB) o selección de avatares predefinidos.
- **Inicio de sesión** con autenticación por email y contraseña (hash MD5 + UTF-8).
- **Perfil de usuario**: visualización de datos y edición (incluido cambio de avatar y contraseña).
- **Modales de acceso**: tanto el formulario de registro como el de inicio de sesión se presentan como modales emergentes desde cualquier página, con envío AJAX y sin recargas innecesarias.

### Carrito de la Compra
- **Usuarios anónimos**: el carrito se almacena en una cookie (`carrito_nexabyte`) con formato `idProducto:cantidad~idProducto:cantidad`.
- **Usuarios registrados**: el carrito se persiste en la base de datos como un pedido en estado `c` (carrito).
- **Transferencia automática (primer login)**: al iniciar sesión por primera vez, los productos de la cookie se transfieren al carrito en base de datos. En accesos posteriores se descarta el carrito anónimo y se conserva el de la BD.
- Operaciones: añadir, incrementar/decrementar cantidad, eliminar producto y vaciar carrito.
- **Añadir al carrito vía AJAX** desde las tarjetas de producto y el modal de detalle.

### Pedidos
- **Tramitar pedido**: convierte el carrito activo en un pedido finalizado (estado `f`), aplicando un 21 % de IVA.
- **Historial**: listado de todos los pedidos realizados con fecha, importe total e IVA.
- **Detalle de pedido**: desglose de líneas con producto, cantidad y subtotal.

### Interfaz y UX
- **Tema oscuro** completo con acentos en cian y azul (inspirado en estéticas _gaming_).
- **Diseño responsive**: adaptado a escritorio, tablet y móvil.
- **Accesibilidad**: no se utiliza el color rojo para errores (daltonismo), en su lugar se usa naranja con iconos `⚠`.
- **Animaciones CSS** suaves: _fadeIn_, _slideUp_ en modales y transiciones en botones/tarjetas.
- **Páginas de error personalizadas**: 404 y 500 con diseño coherente.
- **Respuestas AJAX en JSON**: todas las peticiones asíncronas (login, registro, carrito, NIF) devuelven JSON estructurado mediante org.json y Gson.

---

## 🏗 Arquitectura y Patrones de Diseño

El proyecto sigue una arquitectura **MVC (Modelo-Vista-Controlador)** limpia con separación clara de responsabilidades:

```
┌─────────────────────────────────────────────────────────────────┐
│                        NAVEGADOR (Cliente)                      │
│   JSP + JSTL/EL + JavaScript + CSS                              │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTP (GET / POST / AJAX)
┌───────────────────────────▼─────────────────────────────────────┐
│                       CONTROLADORES (Servlets)                  │
│   FrontController · GestionUsuario · GestionPedido              │
│   RegistroController · VolverController · Ajax                  │
├─────────────────────────────────────────────────────────────────┤
│                     CAPA DE SERVICIO (models/)                  │
│   CarritoService · PedidoService · UsuarioService               │
│   SecurityUtils · Utils · EnumConverter                         │
├─────────────────────────────────────────────────────────────────┤
│                       DAOFactory (Fábrica)                      │
│   DAOFactory.getCategoriaDAO(), getProductoDAO(), etc.          │
├─────────────────────────────────────────────────────────────────┤
│                        DAO (Acceso a Datos)                     │
│   Interfaces: ICategoriaDAO, IProductoDAO, IUsuarioDAO...       │
│   Implementaciones: CategoriaDAO, ProductoDAO, UsuarioDAO...    │
│   ConnectionFactory (DataSource JNDI → Pool de conexiones)      │
├─────────────────────────────────────────────────────────────────┤
│                        BEANS (Entidades)                        │
│   Usuario · Producto · Categoria · Pedido · LineaPedido         │
└───────────────────────────┬─────────────────────────────────────┘
                            │ JDBC
                    ┌───────▼───────┐
                    │   MySQL 8.x   │
                    │  BD: nexabyte │
                    └───────────────┘
```

### Patrones aplicados

| Patrón | Dónde se aplica | Para qué |
|--------|-----------------|----------|
| **MVC** | Controladores + JSP + Beans | Separar lógica de negocio, presentación y control |
| **Front Controller** | `FrontController.java` | Punto de entrada único para la navegación del catálogo |
| **DAO** | Paquete `DAO/` con interfaces + implementaciones | Aislar el acceso a la base de datos del resto de la lógica |
| **Abstract Factory** | `DAOFactory.java` | Crear instancias de DAO sin acoplar el código al tipo concreto |
| **Service Layer** | `CarritoService`, `PedidoService`, `UsuarioService` | Centralizar la lógica de negocio compleja fuera de los Servlets |
| **Singleton (implícito)** | `ConnectionFactory` con bloque `static` | Pool de conexiones único vía JNDI/DataSource |
| **Filter** | `AUTF8.java` | Garantizar codificación UTF-8 en todas las peticiones |
| **Listener** | `SessionListener.java` | Cargar categorías en `ServletContext` al arrancar la aplicación |

---

## 🛠 Tecnologías Utilizadas

### Backend
| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java EE Web API | 7.0 | Servlets 3.1, JSP 2.3, Filters, Listeners |
| JSTL | 1.2 | Etiquetas `<c:forEach>`, `<c:if>`, `<c:out>`, `<fmt:formatNumber>`, etc. |
| Expression Language (EL) | 3.0 | Acceso a atributos y beans en las vistas JSP |
| Commons BeanUtils | 1.9.4 | Población automática de beans con `BeanUtils.populate()` |
| MySQL Connector/J | 8.0.33 | Driver JDBC para la conexión con MySQL |
| org.json | 20231013 | Respuestas JSON en `Ajax.java` y `GestionUsuario.java` |
| Gson (Google) | 2.10.1 | Respuestas JSON en `GestionPedido.java` y `RegistroController.java` |
| JNDI + DataSource | — | Pool de conexiones configurado en `context.xml` |

### Frontend
| Tecnología | Uso |
|------------|-----|
| HTML5 + CSS3 | Estructura semántica y estilos (tema oscuro ~1900 líneas) |
| JavaScript (ES5) | Validaciones en cliente, AJAX (XHR), modales, interactividad |
| CSS Variables | Sistema de diseño consistente con variables personalizadas |
| CSS Animations | Transiciones y animaciones para modales y componentes |

### Herramientas de Desarrollo
| Herramienta | Versión |
|-------------|---------|
| Apache Maven | 3.x |
| Apache Tomcat | 8.x / 9.x |
| NetBeans IDE | — |
| MySQL / MariaDB | 8.x / 10.x |

---

## 📁 Estructura del Proyecto

```
NEXAbyte/
├── pom.xml                          # Descriptor Maven (dependencias y plugins)
├── README.md                        # Este archivo
├── nb-configuration.xml             # Configuración de NetBeans
│
└── src/main/
    ├── java/es/nexabyte/
    │   ├── beans/                   # JavaBeans (entidades del modelo)
    │   │   ├── Categoria.java       #   Categoría de producto
    │   │   ├── LineaPedido.java     #   Línea individual de un pedido
    │   │   ├── Pedido.java          #   Pedido (carrito o finalizado)
    │   │   ├── Producto.java        #   Producto del catálogo
    │   │   └── Usuario.java         #   Usuario registrado
    │   │
    │   ├── controllers/             # Servlets (controladores MVC)
    │   │   ├── Ajax.java            #   Peticiones Ajax genéricas (NIF, etc.)
    │   │   ├── FrontController.java #   Controlador frontal (navegación)
    │   │   ├── GestionPedido.java   #   Operaciones del carrito y pedidos
    │   │   ├── GestionUsuario.java  #   Login, logout, perfil
    │   │   ├── RegistroController.java  # Registro de nuevos usuarios
    │   │   └── VolverController.java    # Redirección al inicio
    │   │
    │   ├── DAO/                     # Capa de acceso a datos
    │   │   ├── ConnectionFactory.java   # Pool de conexiones (JNDI)
    │   │   ├── ICategoriaDAO.java       # Interfaz DAO de categorías
    │   │   ├── CategoriaDAO.java        # Implementación JDBC
    │   │   ├── IProductoDAO.java        # Interfaz DAO de productos
    │   │   ├── ProductoDAO.java         # Implementación JDBC
    │   │   ├── IUsuarioDAO.java         # Interfaz DAO de usuarios
    │   │   ├── UsuarioDAO.java          # Implementación JDBC
    │   │   ├── IPedidoDAO.java          # Interfaz DAO de pedidos
    │   │   ├── PedidoDAO.java           # Implementación JDBC
    │   │   ├── ILineaPedidoDAO.java     # Interfaz DAO de líneas de pedido
    │   │   └── LineaPedidoDAO.java      # Implementación JDBC
    │   │
    │   ├── DAOFactory/              # Fábrica abstracta de DAOs
    │   │   └── DAOFactory.java
    │   │
    │   ├── filters/                 # Filtros del contenedor
    │   │   └── AUTF8.java           #   Filtro de codificación UTF-8
    │   │
    │   ├── listener/                # Listeners del contenedor
    │   │   └── SessionListener.java #   Carga categorías al iniciar la app
    │   │
    │   └── models/                  # Capa de servicio y utilidades
    │       ├── CarritoService.java  #   Lógica del carrito (BD + cookies)
    │       ├── PedidoService.java   #   Lógica de pedidos
    │       ├── UsuarioService.java  #   Lógica de usuarios
    │       ├── SecurityUtils.java   #   Encriptación MD5
    │       ├── EnumConverter.java   #   Converter para BeanUtils (enums)
    │       └── Utils.java           #   Constantes y utilidades generales
    │
    └── webapp/
        ├── index.jsp                # Página principal (catálogo)
        ├── CSS/
        │   └── estilos.css          # Hoja de estilos (~1900 líneas, tema oscuro)
        ├── IMG/                     # Recursos gráficos
        │   ├── avatar/              # Avatares predefinidos (Hombre, Mujer, Otro)
        │   ├── Error404.jpg         #   Imagen de error 404
        │   ├── Error500.jpg         #   Imagen de error 500
        │   ├── Logo.jpg             #   Logo de la aplicación
        │   └── imagen/
        │       ├── categorias/      #   Imágenes de categorías
        │       └── productos/       #   Imágenes de productos (por subcarpeta)
        ├── INC/                     # Fragmentos JSP incluidos
        │   ├── cabecera.inc         #   Header (logo, buscador, nav usuario)
        │   ├── metas.inc            #   Metaetiquetas comunes
        │   └── pie.inc              #   Footer + modales + JavaScript global
        ├── JSP/
        │   ├── avisos/              #   Páginas de notificación
        │   │   ├── aviso.jsp
        │   │   └── notificacion.jsp
        │   ├── error/               #   Páginas de error personalizadas
        │   │   ├── error.jsp
        │   │   ├── error404.jsp
        │   │   └── error500.jsp
        │   └── vistas/              #   Vistas principales
        │       ├── acceder.jsp      #     Formulario de login (página completa)
        │       ├── registro.jsp     #     Formulario de registro (página completa)
        │       ├── perfil.jsp       #     Perfil del usuario
        │       ├── editarPerfil.jsp #     Edición de perfil
        │       ├── producto.jsp     #     Detalle de producto (página completa)
        │       ├── productos.jsp    #     Listado de productos
        │       ├── carrito.jsp      #     Vista del carrito
        │       ├── pedido.jsp       #     Detalle de un pedido
        │       └── verPedidos.jsp   #     Historial de pedidos
        ├── META-INF/
        │   └── context.xml          # Configuración JNDI del DataSource
        └── WEB-INF/
            ├── default.png          # Avatar por defecto (fallback)
            └── web.xml              # Descriptor: welcome-file, error-pages, sesión
```

---

## 💾 Modelo de Datos

La base de datos `nexabyte` consta de 5 tablas principales:

```
┌──────────────┐       ┌───────────────────┐       ┌──────────────┐
│   CATEGORIA  │       │    PRODUCTO       │       │   USUARIO    │
├──────────────┤       ├───────────────────┤       ├──────────────┤
│ idCategoria  │◄──────│ idCategoria (FK)  │       │ idUsuario    │
│ nombre       │  1:N  │ idProducto        │       │ email        │
│ imagen       │       │ nombre            │       │ password     │
└──────────────┘       │ descripcion       │       │ nombre       │
                       │ precio            │       │ apellidos    │
                       │ marca             │       │ nif          │
                       │ imagen            │       │ telefono     │
                       └───────────────────┘       │ direccion    │
                                                   │ codigoPostal │
                       ┌──────────────────┐        │ localidad    │
                       │    PEDIDO        │        │ provincia    │
                       ├──────────────────┤        │ ultimoAcceso │
                       │ idPedido         │        │ avatar       │
                       │ fecha            │        └──────┬───────┘
                       │ estado (c/f)     │               │
                       │ idUsuario (FK) ──┼───────────────┘
                       │ importe          │         1:N
                       │ iva              │
                       └───────┬──────────┘
                               │ 1:N
                       ┌───────▼──────────┐
                       │  LINEA_PEDIDO    │
                       ├──────────────────┤
                       │ idLinea          │
                       │ idPedido (FK)    │
                       │ idProducto (FK)  │
                       │ cantidad         │
                       └──────────────────┘
```

- **Estado del pedido**: `c` = carrito (pedido en curso, solo puede haber uno por usuario) · `f` = finalizado (pedido tramitado).
- **IVA**: se calcula al 21 % sobre el importe base al tramitar el pedido.
- **Contraseñas**: almacenadas como hash MD5 (con codificación UTF-8).

---

## ⚙ Requisitos Previos

Antes de desplegar el proyecto, asegúrate de tener instalado:

| Requisito | Versión mínima | Notas |
|-----------|---------------|-------|
| **JDK** | 7+ | El proyecto compila con source/target 1.7 |
| **Apache Tomcat** | 8.x o 9.x | Servidor de aplicaciones |
| **MySQL** o **MariaDB** | 5.7+ / 10.x+ | Base de datos relacional |
| **Apache Maven** | 3.x | Para compilar y empaquetar el WAR |
| **NetBeans** (opcional) | 8.x+ | IDE recomendado (incluye configuración `.nb-configuration.xml`) |

---

## 🚀 Instalación y Despliegue

### 1. Clona el repositorio

```bash
git clone https://github.com/Juanfrina/NEXAbyte.git
cd NEXAbyte
```

### 2. Configura la base de datos

Crea la base de datos, el usuario y las tablas (ver sección siguiente).

### 3. Revisa la configuración de conexión

El archivo `src/main/webapp/META-INF/context.xml` contiene la configuración JNDI del pool de conexiones:

```xml
<Resource
    name="jdbc/nexabyte"
    type="javax.sql.DataSource"
    driverClassName="com.mysql.cj.jdbc.Driver"
    url="jdbc:mysql://localhost:3306/nexabyte?useSSL=false&amp;serverTimezone=Europe/Madrid&amp;allowPublicKeyRetrieval=true"
    username="nexabyte_user"
    password="NEXAbyte123"
    maxActive="100"
    maxIdle="30"
    maxWait="10000"/>
```

Ajusta `url`, `username` y `password` según tu entorno.

### 4. Compila el proyecto

```bash
mvn clean package
```

Esto generará el archivo `target/NEXAbyte-1.0.war`.

### 5. Despliega en Tomcat

- **Opción A**: Copia `NEXAbyte-1.0.war` en la carpeta `webapps/` de Tomcat.
- **Opción B**: Desde NetBeans, haz clic derecho en el proyecto → _Run_.

### 6. Accede a la aplicación

```
http://localhost:8080/NEXAbyte-1.0/
```

---

## 🗄 Configuración de la Base de Datos

### Crear la base de datos y el usuario

```sql
CREATE DATABASE IF NOT EXISTS nexabyte
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'nexabyte_user'@'localhost' IDENTIFIED BY 'NEXAbyte123';
GRANT ALL PRIVILEGES ON nexabyte.* TO 'nexabyte_user'@'localhost';
FLUSH PRIVILEGES;

USE nexabyte;
```

### Crear las tablas

```sql
CREATE TABLE categoria (
    idCategoria TINYINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    imagen      VARCHAR(120)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE producto (
    idProducto   SMALLINT AUTO_INCREMENT PRIMARY KEY,
    idCategoria  TINYINT NOT NULL,
    nombre       VARCHAR(150) NOT NULL,
    descripcion  TEXT,
    precio       DOUBLE       NOT NULL,
    marca        VARCHAR(60),
    imagen       VARCHAR(200),
    FOREIGN KEY (idCategoria) REFERENCES categoria(idCategoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE usuario (
    idUsuario    SMALLINT AUTO_INCREMENT PRIMARY KEY,
    email        VARCHAR(120) NOT NULL UNIQUE,
    password     CHAR(32)     NOT NULL,  -- hash MD5
    nombre       VARCHAR(60)  NOT NULL,
    apellidos    VARCHAR(80),
    nif          CHAR(9),
    telefono     VARCHAR(15),
    direccion    VARCHAR(150),
    codigoPostal VARCHAR(10),
    localidad    VARCHAR(80),
    provincia    VARCHAR(60),
    ultimoAcceso TIMESTAMP    NULL DEFAULT NULL,
    avatar       VARCHAR(200) DEFAULT 'Otro.jpg'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pedido (
    idPedido  SMALLINT AUTO_INCREMENT PRIMARY KEY,
    fecha     DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado    CHAR(1)  NOT NULL DEFAULT 'c',  -- 'c' = carrito, 'f' = finalizado
    idUsuario SMALLINT NOT NULL,
    importe   DOUBLE   DEFAULT 0.0,
    iva       DOUBLE   DEFAULT 0.0,
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE linea_pedido (
    idLinea    SMALLINT AUTO_INCREMENT PRIMARY KEY,
    idPedido   SMALLINT NOT NULL,
    idProducto SMALLINT NOT NULL,
    cantidad   INT      NOT NULL DEFAULT 1,
    FOREIGN KEY (idPedido)   REFERENCES pedido(idPedido),
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

> **Nota**: Después de crear las tablas, inserta categorías y productos de prueba para poder navegar por la tienda.

---

## 📸 Capturas de Pantalla

> _Las capturas se generarán al desplegar la aplicación. Aquí se indica qué muestra cada vista principal._

| Vista | Descripción |
|-------|-------------|
| **Inicio** | 6 productos aleatorios destacados, filtros laterales, buscador en cabecera |
| **Detalle de producto** | Modal con imagen, descripción, precio y botón de añadir al carrito |
| **Registro** | Modal con formulario completo, avatar upload, validación en tiempo real |
| **Acceder** | Modal con email/contraseña y toggle de visibilidad |
| **Carrito** | Lista de productos con controles de cantidad, resumen e importe total |
| **Perfil** | Datos del usuario con avatar, último acceso y acciones rápidas |
| **Historial de pedidos** | Tabla con todos los pedidos finalizados |

---

## 👨‍💻 Autor

**Juan Francisco** · [@Juanfrina](https://github.com/Juanfrina)

Proyecto desarrollado para el módulo de **Desarrollo Web en Entorno Servidor (DWES)** del ciclo formativo de **Desarrollo de Aplicaciones Web (DAW)**.

---

<p align="center">
  <sub>Hecho con ☕ y mucho hardware virtual · NEXAbyte © 2026</sub>
</p>
