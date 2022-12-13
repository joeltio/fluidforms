package io.joelt.texttemplate

import io.joelt.texttemplate.models.slots.EscapedString
import io.joelt.texttemplate.models.slots.deserializeTemplateBody
import io.joelt.texttemplate.models.slots.serializeTemplateBody
import org.junit.Test

import org.junit.Assert.*

class TemplateTextIntegrationTest {
    @Test
    fun convert_between_string_and_slots() {
        var text = "hello {%text%}James{%end%} Nice to meet you"
        var newText = serializeTemplateBody(deserializeTemplateBody(EscapedString(text)))
        assertEquals(text, newText)

        text = "{%text%}James{%end%}{%text%}{%end%} is going to school"
        newText = serializeTemplateBody(deserializeTemplateBody(EscapedString(text)))
        assertEquals(text, newText)
    }
}