/* =============================================
   PROTECCIONES DE SEGURIDAD DEL CLIENTE
   =============================================
   Bloqueos disuasivos de DevTools y copia.
   La seguridad real está en el BACKEND.
   ============================================= */

(function() {
    "use strict";

    // 1. Bloquear clic derecho (menú contextual)
    document.addEventListener("contextmenu", function(e) {
        e.preventDefault();
        return false;
    });

    // 2. Bloquear atajos peligrosos
    document.addEventListener("keydown", function(e) {

        // F12
        if (e.key === "F12" || e.keyCode === 123) {
            e.preventDefault();
            return false;
        }

        // Ctrl+Shift+I (DevTools)
        if (e.ctrlKey && e.shiftKey && (e.key === "I" || e.key === "i" || e.keyCode === 73)) {
            e.preventDefault();
            return false;
        }

        // Ctrl+Shift+J (Consola)
        if (e.ctrlKey && e.shiftKey && (e.key === "J" || e.key === "j" || e.keyCode === 74)) {
            e.preventDefault();
            return false;
        }

        // Ctrl+Shift+C (Inspeccionar)
        if (e.ctrlKey && e.shiftKey && (e.key === "C" || e.key === "c" || e.keyCode === 67)) {
            e.preventDefault();
            return false;
        }

        // Ctrl+U (Ver código fuente)
        if (e.ctrlKey && (e.key === "U" || e.key === "u" || e.keyCode === 85)) {
            e.preventDefault();
            return false;
        }

        // Ctrl+S (Guardar)
        if (e.ctrlKey && (e.key === "S" || e.key === "s" || e.keyCode === 83)) {
            e.preventDefault();
            return false;
        }

        // Ctrl+P (Imprimir)
        if (e.ctrlKey && (e.key === "P" || e.key === "p" || e.keyCode === 80)) {
            e.preventDefault();
            return false;
        }
    });

    // 3. Bloquear selección de texto (excepto en inputs y textareas)
    document.addEventListener("selectstart", function(e) {
        const tag = e.target.tagName.toLowerCase();
        if (tag !== "input" && tag !== "textarea") {
            e.preventDefault();
            return false;
        }
    });

    // 4. Bloquear arrastrar imágenes y enlaces
    document.addEventListener("dragstart", function(e) {
        e.preventDefault();
        return false;
    });

    // 5. Mensaje de advertencia en consola
    console.log("%c⚠️ ALTO", "color: red; font-size: 40px; font-weight: bold;");
    console.log(
        "%cEsta zona está restringida. Si alguien te pidió pegar algo aquí, es una estafa.",
        "color: red; font-size: 16px;"
    );

})();