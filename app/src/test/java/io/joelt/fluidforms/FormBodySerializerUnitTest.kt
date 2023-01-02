package io.joelt.fluidforms

import io.joelt.fluidforms.models.*
import io.joelt.fluidforms.models.slots.*
import org.junit.Test

import org.junit.Assert.*

class FormBodySerializerUnitTest {
    @Test
    fun serialize_without_slot() {
        val text = "Lorem ipsum dolor"
        val form = listOf<Either<String, Slot>>(
            Either.Left(text)
        )

        val serialized = serializeFormBody(form)
        assertEquals(text, serialized)
    }

    @Test
    fun serialize_with_slots() {
        var form = listOf<Either<String, Slot>>(
            Either.Left("hello"),
            Either.Right(PlainTextSlot("my label", EscapedString("text val  "))),
            Either.Left("world"),
        )

        var serialized = serializeFormBody(form)
        assertEquals("hello{%text|label=\"my label\"%}text val  {%end%}world", serialized)

        form = listOf(
            Either.Left("hello"),
            Either.Right(PlainTextSlot("  space ", EscapedString("text val  "))),
            Either.Left("world"),
            Either.Right(PlainTextSlot("", EscapedString(""))),
        )

        serialized = serializeFormBody(form)
        assertEquals(
            "hello{%text|label=\"  space \"%}text val  {%end%}world{%text%}{%end%}",
            serialized
        )
    }
}
