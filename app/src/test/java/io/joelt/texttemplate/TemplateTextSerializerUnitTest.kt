package io.joelt.texttemplate

import io.joelt.texttemplate.models.*
import io.joelt.texttemplate.models.slots.*
import org.junit.Test

import org.junit.Assert.*

class TemplateTextSerializerUnitTest {
    @Test
    fun serialize_without_slot() {
        val text = "Lorem ipsum dolor"
        val template = listOf<Either<String, Slot>>(
            Either.Left(text)
        )

        val serialized = serializeTemplate(template)
        assertEquals(serialized, text)
    }

    @Test
    fun serialize_with_slots() {
        var template = listOf<Either<String, Slot>>(
            Either.Left("hello"),
            Either.Right(PlainTextSlot("text val  ")),
            Either.Left("world"),
        )

        var serialized = serializeTemplate(template)
        assertEquals(serialized, "hello{% text %}text val  {% end %}world")

        template = listOf(
            Either.Left("hello"),
            Either.Right(PlainTextSlot("text val  ")),
            Either.Left("world"),
            Either.Right(PlainTextSlot("")),
        )

        serialized = serializeTemplate(template)
        assertEquals(serialized, "hello{% text %}text val  {% end %}world{% text %}{% end %}")
    }
}