package io.joelt.texttemplate

import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.models.toTemplateSlot
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
        val parsed = text.toTemplateSlot()

        assertEquals(lValue(parsed[0]), text)
    }

    @Test
    fun parse_text_odd_slot_positions() {
        var parsed = "{% text %}xyz{% end %}Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem".toTemplateSlot()
        assertEquals(parsed.size, 2)
        assertEquals((rValue(parsed[0]) as PlainTextSlot).value, "xyz")
        assertEquals(lValue(parsed[1]), "Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem")

        parsed = "Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem{% text %}xyz{% end %}".toTemplateSlot()
        assertEquals(parsed.size, 2)
        assertEquals(lValue(parsed[0]), "Lorem ipsum dolor sit amet\nLorem ipsum\nLorem Lorem")
        assertEquals((rValue(parsed[1]) as PlainTextSlot).value, "xyz")


        parsed = "{% text %}xyz{% end %}".toTemplateSlot()
        assertEquals(parsed.size, 1)
        assertEquals((rValue(parsed[0]) as PlainTextSlot).value, "xyz")
    }

    @Test
    fun parse_text_with_label() {
        val parsed = "{% text | label=\"the label\" %} hello {% end %}".toTemplateSlot()
        assertEquals(parsed.size, 1)
        assertEquals((rValue(parsed[0]) as PlainTextSlot).value, " hello ")
        assertEquals((rValue(parsed[0]) as PlainTextSlot).label, "the label")
    }

    @Test
    fun parse_text_with_label_trims_whitespace() {
        val parsed = "{%     text   |   label    =   \"  the label \"    %}hello{% end %}".toTemplateSlot()
        assertEquals(parsed.size, 1)
        assertEquals((rValue(parsed[0]) as PlainTextSlot).value, "hello")
        assertEquals((rValue(parsed[0]) as PlainTextSlot).label, "  the label ")
    }

    @Test
    fun parse_text_trims_whitespace_in_tag() {
        val parsed = "{%            text      %}xyz{%end      %}".toTemplateSlot()
        assertEquals(parsed.size, 1)
        assertEquals((rValue(parsed[0]) as PlainTextSlot).value, "xyz")
    }

    @Test
    fun parse_text_multiple_tags() {
        val parsed = "{% text %} hello {% end %} middle text {% text %} goodbye{% end %}".toTemplateSlot()
        assertEquals(parsed.size, 3)
        assertEquals((rValue(parsed[0]) as PlainTextSlot).value, " hello ")
        assertEquals(lValue(parsed[1]), " middle text ")
        assertEquals((rValue(parsed[2]) as PlainTextSlot).value, " goodbye")
    }
}