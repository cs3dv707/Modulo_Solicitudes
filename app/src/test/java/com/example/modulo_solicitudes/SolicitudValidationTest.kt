package com.example.modulo_solicitudes

import com.example.modulo_solicitudes.ui.solicitud.SolicitudValidation
import org.junit.Assert.assertEquals
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
    fun description_withNineCharacters_isRejected() {
        val result = SolicitudValidation.validate(
            titulo = "Internet",
            descripcion = "123456789",
            categoria = "Soporte",
            servicio = "Red"
        )

        assertEquals("La descripción debe tener al menos 10 caracteres", result.descripcionError)
    }

    @Test
    fun description_withTenTrimmedCharacters_isAccepted() {
        val result = SolicitudValidation.validate(
            titulo = "Internet",
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
            descripcion = "Necesito revisar el detalle del saldo acumulado.",
            categoria = "Facturación",
            servicio = "Móvil"
        )

        org.junit.Assert.assertNull(result.tituloError)
        org.junit.Assert.assertNull(result.descripcionError)
        org.junit.Assert.assertNull(result.categoriaError)
        org.junit.Assert.assertNull(result.servicioError)
        assertTrue(result.isValid)
    }
}
