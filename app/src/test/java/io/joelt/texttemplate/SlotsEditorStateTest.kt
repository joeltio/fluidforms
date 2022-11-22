package io.joelt.texttemplate

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.ui.components.SlotsEditorState
import org.junit.Assert.*
import org.junit.Test

private class SlotBuilder {
    private var slots: MutableList<Either<String, Slot>> = mutableListOf()
    fun string(value: String) {
        slots.add(Either.Left(value))
    }

    fun plainSlot(label: String) {
        slots.add(Either.Right(PlainTextSlot("").apply { this.label = label }))
    }

    fun build(): List<Either<String, Slot>> = slots
}

private fun slotBuilder(block: SlotBuilder.() -> Unit): List<Either<String, Slot>> {
    val builder = SlotBuilder()
    block(builder)
    return builder.build()
}

class SlotsEditorStateTest {
    private fun asLeft(value: Either<String, Slot>): String = (value as Either.Left).value
    private fun asRight(value: Either<String, Slot>): Slot = (value as Either.Right).value
    private fun slotLabel(value: Either<String, Slot>): String = asRight(value).label

    // Text insertion, replace, delete
    @Test
    fun insert_from_empty() {
        val slots = slotBuilder {}
        val startState = SlotsEditorState(slots, TextRange(0))
        val input = TextFieldValue("h", TextRange(13))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("h", asLeft(newState.slots[0]))
    }

    @Test
    fun insert_text() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(12))
        val input = TextFieldValue("hello, Name! ", TextRange(13))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, ", asLeft(newState.slots[0]))
        assertEquals("Name", slotLabel(newState.slots[1]))
        assertEquals("! ", asLeft(newState.slots[2]))
    }

    @Test
    fun insert_into_slot_label() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("! ")
            plainSlot("Name")
        }
        val startState = SlotsEditorState(slots, TextRange(9), null, 1)
        val input = TextFieldValue("hello, Naame! Name", TextRange(10))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, ", asLeft(newState.slots[0]))
        assertEquals("Naame", slotLabel(newState.slots[1]))
        assertEquals("! ", asLeft(newState.slots[2]))
        assertEquals("Name", slotLabel(newState.slots[3]))
    }

    @Test
    fun insert_into_left_side_of_unselected_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("! ")
            plainSlot("Name")
        }
        val startState = SlotsEditorState(slots, TextRange(7), null, null)
        val input = TextFieldValue("hello, aName! Name", TextRange(8))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, a", asLeft(newState.slots[0]))
        assertEquals("Name", slotLabel(newState.slots[1]))
        assertEquals("! ", asLeft(newState.slots[2]))
        assertEquals("Name", slotLabel(newState.slots[3]))
    }

    @Test
    fun insert_into_right_side_of_unselected_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("! ")
            plainSlot("Name")
        }
        val startState = SlotsEditorState(slots, TextRange(11), null, null)
        val input = TextFieldValue("hello, Namea! Name", TextRange(12))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, ", asLeft(newState.slots[0]))
        assertEquals("Name", slotLabel(newState.slots[1]))
        assertEquals("a! ", asLeft(newState.slots[2]))
        assertEquals("Name", slotLabel(newState.slots[3]))
    }

    @Test
    fun insert_into_left_side_of_selected_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("! ")
            plainSlot("Name")
        }
        val startState = SlotsEditorState(slots, TextRange(7), null, 1)
        val input = TextFieldValue("hello, aName! Name", TextRange(8))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, ", asLeft(newState.slots[0]))
        assertEquals("aName", slotLabel(newState.slots[1]))
        assertEquals("! ", asLeft(newState.slots[2]))
        assertEquals("Name", slotLabel(newState.slots[3]))
    }

    @Test
    fun insert_into_right_side_of_selected_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("! ")
            plainSlot("Name")
        }
        val startState = SlotsEditorState(slots, TextRange(11), null, 1)
        val input = TextFieldValue("hello, Namea! Name", TextRange(12))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, ", asLeft(newState.slots[0]))
        assertEquals("Namea", slotLabel(newState.slots[1]))
        assertEquals("! ", asLeft(newState.slots[2]))
        assertEquals("Name", slotLabel(newState.slots[3]))
    }

    @Test
    fun insert_between_unselected_slots() {
        val slots = slotBuilder {
            plainSlot("Name")
            plainSlot("Name")
        }
        val startState = SlotsEditorState(slots, TextRange(4), null, null)
        val input = TextFieldValue("NameXName", TextRange(5))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("Name", slotLabel(newState.slots[0]))
        assertEquals("X", asLeft(newState.slots[1]))
        assertEquals("Name", slotLabel(newState.slots[2]))
    }

    @Test
    fun replace_text() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(0, 5))
        val input = TextFieldValue("goodbye, Name!", TextRange(7))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("goodbye, ", asLeft(newState.slots[0]))
        assertEquals("Name", slotLabel(newState.slots[1]))
        assertEquals("!", asLeft(newState.slots[2]))
    }

    @Test
    fun replace_part_of_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(3, 9))
        val input = TextFieldValue("helCHANGme!", TextRange(8))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("helCHANG", asLeft(newState.slots[0]))
        assertEquals("me", slotLabel(newState.slots[1]))
        assertEquals("!", asLeft(newState.slots[2]))
    }

    @Test
    fun replace_over_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(6, 12))
        val input = TextFieldValue("hello,there", TextRange(6))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello,there", asLeft(newState.slots[0]))
    }

    @Test
    fun replace_in_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(8, 10), null, 1)
        val input = TextFieldValue("hello, Nxe!", TextRange(9))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, ", asLeft(newState.slots[0]))
        assertEquals("Nxe", slotLabel(newState.slots[1]))
        assertEquals("!", asLeft(newState.slots[2]))
    }

    @Test
    fun replace_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(7, 11), null, 1)
        val input = TextFieldValue("hello, Age!", TextRange(10))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, ", asLeft(newState.slots[0]))
        assertEquals("Age", slotLabel(newState.slots[1]))
        assertEquals("!", asLeft(newState.slots[2]))
    }

    @Test
    fun replace_slot_at_start() {
        val slots = slotBuilder {
            plainSlot("Name")
        }
        val startState = SlotsEditorState(slots, TextRange(0, 4), null, 0)
        val input = TextFieldValue("X", TextRange(10))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("X", slotLabel(newState.slots[0]))
    }

    @Test
    fun replace_slot_left_edge() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(7, 9), null, 1)
        val input = TextFieldValue("hello, Xme!", TextRange(10))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, ", asLeft(newState.slots[0]))
        assertEquals("Xme", slotLabel(newState.slots[1]))
        assertEquals("!", asLeft(newState.slots[2]))
    }

    @Test
    fun replace_slot_right_edge() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(9, 11), null, 1)
        val input = TextFieldValue("hello, NaX!", TextRange(10))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("hello, ", asLeft(newState.slots[0]))
        assertEquals("NaX", slotLabel(newState.slots[1]))
        assertEquals("!", asLeft(newState.slots[2]))
    }

    @Test
    fun delete_text() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(0, 5))
        val input = TextFieldValue(", Name!", TextRange(0))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals(", ", asLeft(newState.slots[0]))
        assertEquals("Name", slotLabel(newState.slots[1]))
        assertEquals("!", asLeft(newState.slots[2]))
    }

    @Test
    fun delete_across_text_and_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("! Nice to meet you. I am ")
            plainSlot("Name")
            string(".")
        }
        val startState = SlotsEditorState(slots, TextRange(3, 15))
        val input = TextFieldValue("helce to meet you. I am Name.", TextRange(3))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals("helce to meet you. I am ", asLeft(newState.slots[0]))
        assertEquals("Name", slotLabel(newState.slots[1]))
        assertEquals(".", asLeft(newState.slots[2]))
    }

    // Cursor movement
    @Test
    fun cursor_in_slot_selects_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(0))
        val input = TextFieldValue("hello, Name!", TextRange(9))
        val newState = startState.withNewTextFieldValue(input)
        assertEquals(1, newState.selectedSlotIndex)
    }

    @Test
    fun cursor_at_edge_of_slot_does_not_select_when_no_slot_selected() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("! ")
            plainSlot("Age")
        }
        // Move cursor left and right of slot
        var startState = SlotsEditorState(slots, TextRange(0), null, null)
        var input = TextFieldValue("hello, Name! Age", TextRange(7))
        assertNull(startState.withNewTextFieldValue(input).selectedSlotIndex)
        input = TextFieldValue("hello, Name! Age", TextRange(12))
        assertNull(startState.withNewTextFieldValue(input).selectedSlotIndex)

        // Select other slot and move cursor left and right
        startState = SlotsEditorState(slots, TextRange(14), null, 3)
        input = TextFieldValue("hello, Name! Age", TextRange(7))
        assertNull(startState.withNewTextFieldValue(input).selectedSlotIndex)
        input = TextFieldValue("hello, Name! Age", TextRange(11))
        assertNull(startState.withNewTextFieldValue(input).selectedSlotIndex)
    }

    @Test
    fun cursor_at_edge_of_slot_stays_selected_when_slot_selected() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("! ")
            plainSlot("Age")
        }
        // Move cursor left and right of slot
        val startState = SlotsEditorState(slots, TextRange(9), null, 1)
        var input = TextFieldValue("hello, Name! Age", TextRange(7))
        assertEquals(1, startState.withNewTextFieldValue(input).selectedSlotIndex)
        input = TextFieldValue("hello, Name! Age", TextRange(11))
        assertEquals(1, startState.withNewTextFieldValue(input).selectedSlotIndex)
    }

    @Test
    fun selection_within_slot_selects_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(0), null, null)
        val input = TextFieldValue("hello, Name!", TextRange(8, 10))
        assertEquals(1, startState.withNewTextFieldValue(input).selectedSlotIndex)
    }

    @Test
    fun selection_over_multiple_does_not_select_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }
        val startState = SlotsEditorState(slots, TextRange(0), null, null)
        val input = TextFieldValue("hello, Name!", TextRange(6, 8))
        assertNull(startState.withNewTextFieldValue(input).selectedSlotIndex)
    }

    @Test
    fun deletion_changes_selection_of_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }

        // Unselected to selected
        var startState = SlotsEditorState(slots, TextRange(11), null, null)
        var input = TextFieldValue("hello, Nam!", TextRange(10))
        startState.withNewTextFieldValue(input).let {
            assertEquals(1, it.selectedSlotIndex)
            assertEquals("hello, ", asLeft(it.slots[0]))
            assertEquals("Name", slotLabel(it.slots[1]))
            assertEquals("!", asLeft(it.slots[2]))
        }

        // Selected to unselected
        startState = SlotsEditorState(slots, TextRange(7), null, 1)
        input = TextFieldValue("hello,Name!", TextRange(7))
        startState.withNewTextFieldValue(input).let {
            assertNull(it.selectedSlotIndex)
            assertEquals("hello, ", asLeft(it.slots[0]))
            assertEquals("Name", slotLabel(it.slots[1]))
            assertEquals("!", asLeft(it.slots[2]))
        }
    }

    @Test
    fun insert_slot() {
        val slots = slotBuilder {
            string("hello, ")
            plainSlot("Name")
            string("!")
        }

        val startState = SlotsEditorState(slots, TextRange(5), null, null)
        val newSlot = PlainTextSlot("").apply { label = "Text" }
        startState.insertSlotAtSelection(newSlot).let {
            assertEquals(it.selectedSlotIndex, 1)

            assertEquals(5, it.selection.start)
            assertEquals(9, it.selection.end)

            assertEquals("hello", asLeft(it.slots[0]))
            assertEquals("Text", slotLabel(it.slots[1]))
            assertEquals(", ", asLeft(it.slots[2]))
            assertEquals("Name", slotLabel(it.slots[3]))
            assertEquals("!", asLeft(it.slots[4]))
        }
    }
}
