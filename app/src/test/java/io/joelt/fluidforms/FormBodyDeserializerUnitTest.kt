package io.joelt.fluidforms

import io.joelt.fluidforms.models.Either
import io.joelt.fluidforms.models.slots.*
import org.junit.Test

import org.junit.Assert.*

class FormBodyDeserializerUnitTest {
    private fun lValue(value: Either<String, Slot>): String {
        return (value as Either.Left).value
    }

    private fun rValue(value: Either<String, Slot>): Slot {
        return (value as Either.Right).value
    }

    private fun Slot.value(): String = this.serializeValue().value
    private fun String.deserialize() = deserializeFormBody(EscapedString(this))

    @Test
    fun deserialize_text_without_slots() {
        val text = "Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem"
        val deserialized = text.deserialize()

        assertEquals(text, lValue(deserialized[0]))
    }

    @Test
    fun deserialize_text_odd_slot_positions() {
        var deserialized = "{% text %}xyz{% end %}Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem".deserialize()
        assertEquals(2, deserialized.size)
        assertEquals("xyz", (rValue(deserialized[0]) as PlainTextSlot).value())
        assertEquals("Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem", lValue(deserialized[1]))

        deserialized = "Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem{% text %}xyz{% end %}".deserialize()
        assertEquals(2, deserialized.size)
        assertEquals("Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem", lValue(deserialized[0]))
        assertEquals("xyz", (rValue(deserialized[1]) as PlainTextSlot).value())


        deserialized = "{% text %}xyz{% end %}".deserialize()
        assertEquals(1, deserialized.size)
        assertEquals("xyz", (rValue(deserialized[0]) as PlainTextSlot).value())
    }

    @Test
    fun deserialize_text_with_label() {
        val deserialized = "{% text | label=\"the label\" %} hello {% end %}".deserialize()
        assertEquals(1, deserialized.size)
        assertEquals(" hello ", (rValue(deserialized[0]) as PlainTextSlot).value())
        assertEquals("the label", (rValue(deserialized[0]) as PlainTextSlot).displayLabel)
    }

    @Test
    fun deserialize_text_with_label_trims_whitespace() {
        val deserialized = "{%     text   |   label    =   \"  the label \"    %}hello{% end %}".deserialize()
        assertEquals(1, deserialized.size)
        assertEquals("hello", (rValue(deserialized[0]) as PlainTextSlot).value())
        assertEquals("  the label ", (rValue(deserialized[0]) as PlainTextSlot).displayLabel)
    }

    @Test
    fun deserialize_text_trims_whitespace_in_tag() {
        val deserialized = "{%            text      %}xyz{%end      %}".deserialize()
        assertEquals(1, deserialized.size)
        assertEquals("xyz", (rValue(deserialized[0]) as PlainTextSlot).value())
    }

    @Test
    fun deserialize_text_multiple_tags() {
        val deserialized = "{% text %} hello {% end %} middle text {% text %} goodbye{% end %}".deserialize()
        assertEquals(3, deserialized.size)
        assertEquals(" hello ", (rValue(deserialized[0]) as PlainTextSlot).value())
        assertEquals(" middle text ", lValue(deserialized[1]))
        assertEquals(" goodbye", (rValue(deserialized[2]) as PlainTextSlot).value())
    }

    // Error handling
    @Test
    fun deserialize_missing_start_tag() {
        assertThrows(DeserializeException::class.java) {
            "hello{% end %}".deserialize()
        }
    }

    @Test
    fun deserialize_missing_end_tag() {
        assertThrows(DeserializeException::class.java) {
            "{% text %}hello".deserialize()
        }
    }

    @Test
    fun deserialize_missing_closing_tag() {
        // This is ok because the whole string is treated as normal text
        val deserialized = "{% text hello".deserialize()
        assertEquals(lValue(deserialized[0]), "{% text hello")

        assertThrows(DeserializeException::class.java) {
            "{% text %}hello{% end".deserialize()
        }
    }

    @Test
    fun deserialize_nonsense_tags() {
        assertThrows(DeserializeException::class.java) {
            "{% asdfgh %}hello{% end %}".deserialize()
        }
    }

    @Test
    fun deserialize_modifier_extra_or_too_few_quotes() {
        // This is ok, "label "" is the first modifier's name
        // We cannot confirm this because useless modifiers are dropped on slot creation
        "{% text | label \"= \"hello\" %}hello{% end %}".deserialize()

        // This is ok, because only the outermost quotes are used
        val deserialized = "{% text | label =\" \"hello\" %}hello{% end %}".deserialize()
        val slot = (rValue(deserialized[0]) as PlainTextSlot)
        assertEquals(" \"hello", slot.displayLabel)

        assertThrows(DeserializeException::class.java) {
            "{% text | label = \"hello %}hello{% end %}".deserialize()
        }
    }

    @Test
    fun deserialize_modifier_extra_pipe() {
        assertThrows(DeserializeException::class.java) {
            "{% text || label = \"hello\" %}hello{% end %}".deserialize()
        }
    }

    @Test
    fun deserialize_modifier_extra_equals() {
        assertThrows(DeserializeException::class.java) {
            "{% text | label == \"hello\" %}hello{% end %}".deserialize()
        }
    }
}
