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
        assertEquals(text, serialized)
    }

    @Test
    fun serialize_with_slots() {
        var template = listOf<Either<String, Slot>>(
            Either.Left("hello"),
            Either.Right(PlainTextSlot("text val  ").apply {
                label = "my label"
            }),
            Either.Left("world"),
        )

        var serialized = serializeTemplate(template)
        assertEquals("hello{% text | label = \"my label\" %}text val  {% end %}world", serialized)

        template = listOf(
            Either.Left("hello"),
            Either.Right(PlainTextSlot("text val  ").apply {
                label = "  space "
            }),
            Either.Left("world"),
            Either.Right(PlainTextSlot("")),
        )

        serialized = serializeTemplate(template)
        assertEquals("hello{% text | label = \"  space \" %}text val  {% end %}world{% text %}{% end %}", serialized)
    }
}