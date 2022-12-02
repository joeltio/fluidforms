package io.joelt.texttemplate

import io.joelt.texttemplate.models.serializeTemplate
import org.junit.Test
import io.joelt.texttemplate.models.toTemplateBody

import org.junit.Assert.*

class TemplateTextIntegrationTest {
    @Test
    fun convert_between_string_and_slots() {
        var text = "hello, {% text %}James{% end %}! Nice to meet you"
        var newText = serializeTemplate(text.toTemplateBody())
        assertEquals(text, newText)

        text = "{% text %}James{% end %}{% text %}{% end %} is going to school"
        newText = serializeTemplate(text.toTemplateBody())
        assertEquals(text, newText)
    }
}