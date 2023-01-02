package io.joelt.fluidforms

import io.joelt.fluidforms.models.slots.EscapedString
import io.joelt.fluidforms.models.slots.deserializeFormBody
import io.joelt.fluidforms.models.slots.serializeFormBody
import org.junit.Test

import org.junit.Assert.*

class FormTextIntegrationTest {
    @Test
    fun convert_between_string_and_slots() {
        var text = "hello {%text%}James{%end%} Nice to meet you"
        var newText = serializeFormBody(deserializeFormBody(EscapedString(text)))
        assertEquals(text, newText)

        text = "{%text%}James{%end%}{%text%}{%end%} is going to school"
        newText = serializeFormBody(deserializeFormBody(EscapedString(text)))
        assertEquals(text, newText)
    }
}