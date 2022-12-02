package io.joelt.texttemplate

import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.models.toTemplateBody
import org.junit.Test

import org.junit.Assert.*

class TemplateTextParserUnitTest {
    fun lValue(value: Either<String, Slot>): String {
        return (value as Either.Left).value
    }

    fun rValue(value: Either<String, Slot>): Slot {
        return (value as Either.Right).value
    }

    @Test
    fun parse_text_without_slots() {
        val text = "Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem"
        val parsed = text.toTemplateBody()

        assertEquals(text, lValue(parsed[0]))
    }

    @Test
    fun parse_text_odd_slot_positions() {
        var parsed = "{% text %}xyz{% end %}Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem".toTemplateBody()
        assertEquals(2, parsed.size)
        assertEquals("xyz", (rValue(parsed[0]) as PlainTextSlot).value)
        assertEquals("Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem", lValue(parsed[1]))

        parsed = "Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem{% text %}xyz{% end %}".toTemplateBody()
        assertEquals(2, parsed.size)
        assertEquals("Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem", lValue(parsed[0]))
        assertEquals("xyz", (rValue(parsed[1]) as PlainTextSlot).value)


        parsed = "{% text %}xyz{% end %}".toTemplateBody()
        assertEquals(1, parsed.size)
        assertEquals("xyz", (rValue(parsed[0]) as PlainTextSlot).value)
    }

    @Test
    fun parse_text_with_label() {
        val parsed = "{% text | label=\"the label\" %} hello {% end %}".toTemplateBody()
        assertEquals(1, parsed.size)
        assertEquals(" hello ", (rValue(parsed[0]) as PlainTextSlot).value)
        assertEquals("the label", (rValue(parsed[0]) as PlainTextSlot).label)
    }

    @Test
    fun parse_text_with_label_trims_whitespace() {
        val parsed = "{%     text   |   label    =   \"  the label \"    %}hello{% end %}".toTemplateBody()
        assertEquals(1, parsed.size)
        assertEquals("hello", (rValue(parsed[0]) as PlainTextSlot).value)
        assertEquals("  the label ", (rValue(parsed[0]) as PlainTextSlot).label)
    }

    @Test
    fun parse_text_trims_whitespace_in_tag() {
        val parsed = "{%            text      %}xyz{%end      %}".toTemplateBody()
        assertEquals(1, parsed.size)
        assertEquals("xyz", (rValue(parsed[0]) as PlainTextSlot).value)
    }

    @Test
    fun parse_text_multiple_tags() {
        val parsed = "{% text %} hello {% end %} middle text {% text %} goodbye{% end %}".toTemplateBody()
        assertEquals(3, parsed.size)
        assertEquals(" hello ", (rValue(parsed[0]) as PlainTextSlot).value)
        assertEquals(" middle text ", lValue(parsed[1]))
        assertEquals(" goodbye", (rValue(parsed[2]) as PlainTextSlot).value)
    }
}