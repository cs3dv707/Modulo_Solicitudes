package com.example.modulo_solicitudes

import com.example.modulo_solicitudes.ui.solicitud.SolicitudValidation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SolicitudValidationTest {

    @Test
    fun emptyFields_reportAllRequiredMessages() {
        val result = SolicitudValidation.validate("", "", "", "")

        assertEquals("El título es obligatorio", result.tituloError)
        assertEquals("La descripción es obligatoria", result.descripcionError)
        assertEquals("La categoría es obligatoria", result.categoriaError)
        assertEquals("El servicio relacionado es obligatorio", result.servicioError)
        assertTrue(!result.isValid)
    }

    @Test
    fun title_withFourCharacters_isRejected() {
        val result = SolicitudValidation.validate(
            titulo = "Hola",
            descripcion = "Descripción válida de más de 10 caracteres.",
            categoria = "Soporte",
            servicio = "Red"
        )

        assertEquals("El título debe tener al menos 5 caracteres", result.tituloError)
        assertTrue(!result.isValid)
    }

    @Test
    fun title_exceedingSixtyCharacters_isRejected() {
        val longTitle = "A".repeat(61)
        val result = SolicitudValidation.validate(
            titulo = longTitle,
            descripcion = "Descripción válida de más de 10 caracteres.",
            categoria = "Soporte",
            servicio = "Red"
        )

        assertEquals("El título no puede exceder los 60 caracteres", result.tituloError)
        assertTrue(!result.isValid)
    }

    @Test
    fun category_withTwoCharacters_isRejected() {
        val result = SolicitudValidation.validate(
            titulo = "Título Válido",
            descripcion = "Descripción válida de más de 10 caracteres.",
            categoria = "IT",
            servicio = "Red"
        )

        assertEquals("La categoría debe tener al menos 3 caracteres", result.categoriaError)
        assertTrue(!result.isValid)
    }

    @Test
    fun service_withTwoCharacters_isRejected() {
        val result = SolicitudValidation.validate(
            titulo = "Título Válido",
            descripcion = "Descripción válida de más de 10 caracteres.",
            categoria = "Soporte",
            servicio = "TV"
        )

        assertEquals("El servicio relacionado debe tener al menos 3 caracteres", result.servicioError)
        assertTrue(!result.isValid)
    }

    @Test
    fun description_withNineCharacters_isRejected() {
        val result = SolicitudValidation.validate(
            titulo = "Internet Lento",
            descripcion = "123456789",
            categoria = "Soporte",
            servicio = "Red"
        )

        assertEquals("La descripción debe tener al menos 10 caracteres", result.descripcionError)
        assertTrue(!result.isValid)
    }

    @Test
    fun description_exceedingFiveHundredCharacters_isRejected() {
        val longDescription = "D".repeat(501)
        val result = SolicitudValidation.validate(
            titulo = "Título Válido",
            descripcion = longDescription,
            categoria = "Soporte",
            servicio = "Red"
        )

        assertEquals("La descripción no puede exceder los 500 caracteres", result.descripcionError)
        assertTrue(!result.isValid)
    }

    @Test
    fun description_withTenTrimmedCharacters_isAccepted() {
        val result = SolicitudValidation.validate(
            titulo = "Internet Lento",
            descripcion = "  1234567890  ",
            categoria = "Soporte",
            servicio = "Red"
        )

        assertTrue(result.isValid)
    }

    @Test
    fun whitespaceOnlyValues_areRejected() {
        val result = SolicitudValidation.validate("  ", "\t", "\n", " ")

        assertTrue(!result.isValid)
    }

    @Test
    fun validForm_returnsNoErrors() {
        val result = SolicitudValidation.validate(
            titulo = "Consulta de saldo",
            descripcion = "Necesito revisar el detalle del saldo acumulado en la cuenta.",
            categoria = "Facturación",
            servicio = "Móvil"
        )

        assertNull(result.tituloError)
        assertNull(result.descripcionError)
        assertNull(result.categoriaError)
        assertNull(result.servicioError)
        assertTrue(result.isValid)
    }
}
