package io.joelt.texttemplate

import io.joelt.texttemplate.models.*
import io.joelt.texttemplate.models.slots.*
import org.junit.Test

import org.junit.Assert.*

class TemplateBodySerializerUnitTest {
    @Test
    fun serialize_without_slot() {
        val text = "Lorem ipsum dolor"
        val template = listOf<Either<String, Slot>>(
            Either.Left(text)
        )

        val serialized = serializeTemplateBody(template)
        assertEquals(text, serialized)
    }

    @Test
    fun serialize_with_slots() {
        var template = listOf<Either<String, Slot>>(
            Either.Left("hello"),
            Either.Right(PlainTextSlot("my label", EscapedString("text val  "))),
            Either.Left("world"),
        )

        var serialized = serializeTemplateBody(template)
        assertEquals("hello{%text|label=\"my label\"%}text val  {%end%}world", serialized)

        template = listOf(
            Either.Left("hello"),
            Either.Right(PlainTextSlot("  space ", EscapedString("text val  "))),
            Either.Left("world"),
            Either.Right(PlainTextSlot("", EscapedString(""))),
        )

        serialized = serializeTemplateBody(template)
        assertEquals(
            "hello{%text|label=\"  space \"%}text val  {%end%}world{%text%}{%end%}",
            serialized
        )
    }
}
