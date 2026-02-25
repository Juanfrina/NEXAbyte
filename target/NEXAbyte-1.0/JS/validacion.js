/**
 * validacion.js - Validación del formulario de registro y funciones Ajax.
 * NEXAbyte - Tienda online de componentes informáticos.
 * 
 * Funciones:
 *   - Validación en tiempo real de campos del formulario de registro.
 *   - Comprobación Ajax de email duplicado.
 *   - Cálculo automático de la letra del NIF.
 *   - Validación de código postal.
 *   - Validación de contraseñas coincidentes.
 */
document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("formRegistro");
    if (!form) return;

    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const password2Input = document.getElementById("password2");
    const nifInput = document.getElementById("nif");
    const cpInput = document.getElementById("codigoPostal");
    const letraNif = document.getElementById("letraNif");

    const emailFeedback = document.getElementById("emailFeedback");
    const passwordFeedback = document.getElementById("passwordFeedback");
    const password2Feedback = document.getElementById("password2Feedback");
    const nifFeedback = document.getElementById("nifFeedback");
    const cpFeedback = document.getElementById("cpFeedback");

    // ==================== LETRA DEL NIF (Ajax al servidor) ====================
    if (nifInput) {
        nifInput.addEventListener("input", function () {
            const valor = this.value.trim();

            // Solo dígitos
            this.value = valor.replace(/[^0-9]/g, "");

            if (this.value.length === 8) {
                var nifValue = this.value;
                var xhr = new XMLHttpRequest();
                xhr.open("GET", "Ajax?op=letraNif&nif=" + encodeURIComponent(nifValue), true);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        var data = JSON.parse(xhr.responseText);
                        if (letraNif) letraNif.textContent = data.letra || "-";
                        if (data.letra) {
                            setValido(nifInput, nifFeedback, "NIF válido");
                        }
                    }
                };
                xhr.send();
            } else {
                if (letraNif) letraNif.textContent = "-";
                if (this.value.length > 0) {
                    setInvalido(this, nifFeedback, "Introduce 8 dígitos");
                } else {
                    limpiarFeedback(this, nifFeedback);
                }
            }
        });
    }

    // ==================== EMAIL (Ajax comprobación duplicado) ====================
    let emailTimer = null;
    if (emailInput) {
        emailInput.addEventListener("input", function () {
            const email = this.value.trim();
            clearTimeout(emailTimer);

            if (email === "") {
                limpiarFeedback(this, emailFeedback);
                return;
            }

            // Validar formato básico
            const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!regexEmail.test(email)) {
                setInvalido(this, emailFeedback, "Formato de email no válido");
                return;
            }

            // Esperar 500ms antes de hacer la petición Ajax
            emailTimer = setTimeout(function () {
                const xhr = new XMLHttpRequest();
                xhr.open("GET", "GestionUsuario?op=comprobarEmail&email=" + encodeURIComponent(email), true);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        var data = JSON.parse(xhr.responseText);
                        if (data.estado === "existe") {
                            setInvalido(emailInput, emailFeedback, "Este email ya está registrado");
                        } else if (data.estado === "disponible") {
                            setValido(emailInput, emailFeedback, "Email disponible");
                        }
                    }
                };
                xhr.send();
            }, 500);
        });
    }

    // ==================== TELÉFONO ====================
    const telefonoInput = document.getElementById("telefono");
    const telefonoFeedback = document.getElementById("telefonoFeedback");
    if (telefonoInput) {
        telefonoInput.addEventListener("input", function () {
            this.value = this.value.replace(/[^0-9]/g, "");
            if (this.value.length === 0) {
                limpiarFeedback(this, telefonoFeedback);
            } else if (!/^[6789]/.test(this.value)) {
                setInvalido(this, telefonoFeedback, "Debe empezar por 6, 7, 8 o 9");
            } else if (this.value.length < 9) {
                setInvalido(this, telefonoFeedback, "Debe tener 9 dígitos");
            } else {
                setValido(this, telefonoFeedback, "Teléfono válido");
            }
        });
    }

    // ==================== CONTRASEÑA ====================
    if (passwordInput) {
        passwordInput.addEventListener("input", function () {
            if (this.value.length === 0) {
                limpiarFeedback(this, passwordFeedback);
            } else if (this.value.length < 6) {
                setInvalido(this, passwordFeedback, "Mínimo 6 caracteres");
            } else {
                setValido(this, passwordFeedback, "Contraseña válida");
            }

            // Revalidar confirmación si ya tiene valor
            if (password2Input && password2Input.value.length > 0) {
                validarConfirmacion();
            }
        });
    }

    // ==================== CONFIRMAR CONTRASEÑA ====================
    if (password2Input) {
        password2Input.addEventListener("input", validarConfirmacion);
    }

    function validarConfirmacion() {
        if (password2Input.value.length === 0) {
            limpiarFeedback(password2Input, password2Feedback);
        } else if (password2Input.value !== passwordInput.value) {
            setInvalido(password2Input, password2Feedback, "Las contraseñas no coinciden");
        } else {
            setValido(password2Input, password2Feedback, "Las contraseñas coinciden");
        }
    }

    // ==================== CÓDIGO POSTAL ====================
    if (cpInput) {
        cpInput.addEventListener("input", function () {
            this.value = this.value.replace(/[^0-9]/g, "");

            if (this.value.length === 0) {
                limpiarFeedback(this, cpFeedback);
            } else if (this.value.length === 5) {
                const prov = parseInt(this.value.substring(0, 2), 10);
                if (prov >= 1 && prov <= 52) {
                    setValido(this, cpFeedback, "Código postal válido");
                } else {
                    setInvalido(this, cpFeedback, "Provincia no válida (01-52)");
                }
            } else {
                setInvalido(this, cpFeedback, "Debe tener 5 dígitos");
            }
        });
    }

    // ==================== VALIDACIÓN AL ENVIAR ====================
    form.addEventListener("submit", function (e) {
        let valido = true;

        // Email
        if (!emailInput.value.trim()) {
            setInvalido(emailInput, emailFeedback, "Campo obligatorio");
            valido = false;
        }

        // Password
        if (!passwordInput.value || passwordInput.value.length < 6) {
            setInvalido(passwordInput, passwordFeedback, "Mínimo 6 caracteres");
            valido = false;
        }

        // Confirmar password
        if (password2Input.value !== passwordInput.value) {
            setInvalido(password2Input, password2Feedback, "Las contraseñas no coinciden");
            valido = false;
        }

        // Nombre y apellidos
        const nombreInput = document.getElementById("nombre");
        const apellidosInput = document.getElementById("apellidos");
        if (nombreInput && !nombreInput.value.trim()) {
            nombreInput.classList.add("input-invalido");
            valido = false;
        }
        if (apellidosInput && !apellidosInput.value.trim()) {
            apellidosInput.classList.add("input-invalido");
            valido = false;
        }

        // NIF
        if (!nifInput.value || nifInput.value.length !== 8) {
            setInvalido(nifInput, nifFeedback, "Introduce 8 dígitos");
            valido = false;
        }

        // Dirección (obligatorio)
        var direccionInput = document.getElementById("direccion");
        if (direccionInput && !direccionInput.value.trim()) {
            direccionInput.classList.add("input-invalido");
            valido = false;
        } else if (direccionInput) {
            direccionInput.classList.remove("input-invalido");
        }

        // Código postal (obligatorio, 5 dígitos, provincia 01-52)
        if (cpInput) {
            var cpVal = cpInput.value.trim();
            if (!cpVal || cpVal.length !== 5) {
                setInvalido(cpInput, cpFeedback, "Campo obligatorio (5 dígitos)");
                valido = false;
            } else {
                var prov = parseInt(cpVal.substring(0, 2), 10);
                if (prov < 1 || prov > 52) {
                    setInvalido(cpInput, cpFeedback, "Provincia no válida (01-52)");
                    valido = false;
                }
            }
        }

        // Localidad (obligatorio)
        var localidadInput = document.getElementById("localidad");
        if (localidadInput && !localidadInput.value.trim()) {
            localidadInput.classList.add("input-invalido");
            valido = false;
        } else if (localidadInput) {
            localidadInput.classList.remove("input-invalido");
        }

        // Provincia (obligatorio)
        var provinciaInput = document.getElementById("provincia");
        if (provinciaInput && !provinciaInput.value.trim()) {
            provinciaInput.classList.add("input-invalido");
            valido = false;
        } else if (provinciaInput) {
            provinciaInput.classList.remove("input-invalido");
        }

        // Comprobar si email está marcado como "existe"
        if (emailFeedback && emailFeedback.textContent.includes("ya está registrado")) {
            valido = false;
        }

        if (!valido) {
            e.preventDefault();
            // Scroll al primer error
            const primerError = form.querySelector(".input-invalido");
            if (primerError) {
                primerError.scrollIntoView({ behavior: "smooth", block: "center" });
                primerError.focus();
            }
        }
    });

    // ==================== FUNCIONES AUXILIARES ====================

    /**
     * Marca un campo como válido con borde verde y mensaje de éxito.
     * Usa doble borde en lugar de colores rojos (requisito accesibilidad daltonismo).
     */
    function setValido(input, feedback, mensaje) {
        input.classList.remove("input-invalido");
        input.classList.add("input-valido");
        if (feedback) {
            feedback.textContent = "\u2713 " + mensaje;
            feedback.className = "formulario-feedback feedback-valido";
        }
    }

    /**
     * Marca un campo como inválido con doble borde y mensaje de advertencia.
     * NO usa rojo (requisito del profesor - daltonismo).
     */
    function setInvalido(input, feedback, mensaje) {
        input.classList.remove("input-valido");
        input.classList.add("input-invalido");
        if (feedback) {
            feedback.textContent = "\u26A0 " + mensaje;
            feedback.className = "formulario-feedback feedback-invalido";
        }
    }

    /**
     * Limpia el estado visual de un campo.
     */
    function limpiarFeedback(input, feedback) {
        input.classList.remove("input-valido", "input-invalido");
        if (feedback) {
            feedback.textContent = "";
            feedback.className = "formulario-feedback";
        }
    }
});
