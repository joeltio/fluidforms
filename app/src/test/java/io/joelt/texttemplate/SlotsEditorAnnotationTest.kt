package io.joelt.texttemplate

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.ui.components.SlotsEditorState.Companion.createSlotsFromAnnotations
import io.joelt.texttemplate.ui.components.SlotsEditorState.Companion.shiftAnnotations
import org.junit.Assert.*
import org.junit.Test

// This has to be the same tag used in SlotsEditor.kt
private const val SLOT_TAG = "slot"

class SlotsEditorAnnotationTest {
    fun asLeft(value: Either<String, Slot>): String = (value as Either.Left).value
    fun asRight(value: Either<String, Slot>): Slot = (value as Either.Right).value

    @Test
    fun create_slots_from_annotations() {
        val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque vitae ligula ullamcorper, tristique orci nec, ullamcorper ante. Aenean at urna nisi."
        val annotations = listOf(
            AnnotatedString.Range("{% text | label = 'ipsum' %}{% end %}", 6, 11, SLOT_TAG),
            AnnotatedString.Range("{% text | label = 'Quisque' %}{% end %}", 57, 64, SLOT_TAG),
            AnnotatedString.Range("{% text | label = 'tristique' %}{% end %}", 91, 100, SLOT_TAG),
        )

        val slots = createSlotsFromAnnotations(text, annotations)
        assertEquals(7, slots.size)
        assertEquals("Lorem ", asLeft(slots[0]))
        assertEquals("ipsum", asRight(slots[1]).label)
        assertEquals(" dolor sit amet, consectetur adipiscing elit. ", asLeft(slots[2]))
        assertEquals("Quisque", asRight(slots[3]).label)
        assertEquals(" vitae ligula ullamcorper, ", asLeft(slots[4]))
        assertEquals("tristique", asRight(slots[5]).label)
        assertEquals(" orci nec, ullamcorper ante. Aenean at urna nisi.", asLeft(slots[6]))
    }

    @Test
    fun create_slots_from_annotations_with_wrong_labels() {
        val text = "Lorem ips dolor sit amet, consectetur adipiscing elit. Quisqu vitae ligula ullamcorper, tristique orci nec, ullamcorper ante. Aenean at urna nisi."
        val annotations = listOf(
            AnnotatedString.Range("{% text | label = 'ipsum' %}{% end %}", 6, 9, SLOT_TAG),
            AnnotatedString.Range("{% text | label = 'Quisque' %}{% end %}", 55, 61, SLOT_TAG),
            AnnotatedString.Range("{% text | label = 'tristique' %}{% end %}", 88, 97, SLOT_TAG),
        )

        val slots = createSlotsFromAnnotations(text, annotations)
        assertEquals(7, slots.size)
        assertEquals("Lorem ", asLeft(slots[0]))
        assertEquals("ips", asRight(slots[1]).label)
        assertEquals(" dolor sit amet, consectetur adipiscing elit. ", asLeft(slots[2]))
        assertEquals("Quisqu", asRight(slots[3]).label)
        assertEquals(" vitae ligula ullamcorper, ", asLeft(slots[4]))
        assertEquals("tristique", asRight(slots[5]).label)
        assertEquals(" orci nec, ullamcorper ante. Aenean at urna nisi.", asLeft(slots[6]))
    }

    @Test
    fun craete_slots_from_annotations_odd_indexes() {
        // Slot at start of text
        var text = "hello world"
        var annotations = listOf(
            AnnotatedString.Range("{% text | label = 'hello' %}{% end %}", 0, 5, SLOT_TAG),
        )
        var slots = createSlotsFromAnnotations(text, annotations)
        assertEquals(2, slots.size)
        assertEquals("hello", asRight(slots[0]).label)
        assertEquals(" world", asLeft(slots[1]))

        // Slot at end of text
        text = "hello world"
        annotations = listOf(
            AnnotatedString.Range("{% text | label = 'hello' %}{% end %}", 6, 11, SLOT_TAG),
        )
        slots = createSlotsFromAnnotations(text, annotations)
        assertEquals(2, slots.size)
        assertEquals("hello ", asLeft(slots[0]))
        assertEquals("world", asRight(slots[1]).label)
    }

    @Test
    fun shift_annotations_backspace() {
        val oldTfv = TextFieldValue("hello world", TextRange(5, 5))
        val newTfv = TextFieldValue("hell world", TextRange(4, 4))
        val annotations = listOf(
            AnnotatedString.Range("{% text | label = 'hello' %}{% end %}", 0, 5, SLOT_TAG),
        )
        val newAnnotations = shiftAnnotations(annotations, oldTfv, newTfv)
        assertEquals(0, newAnnotations[0].start)
        assertEquals(4, newAnnotations[0].end)
    }

    @Test
    fun shift_annotations_delete_many() {
        val oldTfv = TextFieldValue("hello world", TextRange(5, 5))
        val newTfv = TextFieldValue("he world", TextRange(2, 2))
        val annotations = listOf(
            AnnotatedString.Range("{% text | label = 'hello' %}{% end %}", 0, 5, SLOT_TAG),
        )
        val newAnnotations = shiftAnnotations(annotations, oldTfv, newTfv)
        assertEquals(0, newAnnotations[0].start)
        assertEquals(2, newAnnotations[0].end)
    }

    @Test
    fun shift_annotations_paste() {
        val oldTfv = TextFieldValue("hello world", TextRange(5, 5))
        val newTfv = TextFieldValue("hellohello world! world", TextRange(3, 3))
        val annotations = listOf(
            AnnotatedString.Range("{% text | label = 'hello' %}{% end %}", 0, 5, SLOT_TAG),
            AnnotatedString.Range("{% text | label = 'ld' %}{% end %}", 9, 11, SLOT_TAG),
        )
        val newAnnotations = shiftAnnotations(annotations, oldTfv, newTfv)
        assertEquals(0, newAnnotations[0].start)
        assertEquals(5, newAnnotations[0].end)
        assertEquals(21, newAnnotations[1].start)
        assertEquals(23, newAnnotations[1].end)
    }

    @Test
    fun shift_annotations_replace() {
        val oldTfv = TextFieldValue("hello world", TextRange(2, 7))
        val newTfv = TextFieldValue("hesomething neworld", TextRange(14, 14))
        val annotations = listOf(
            AnnotatedString.Range("{% text | label = 'hello' %}{% end %}", 0, 5, SLOT_TAG),
        )
        val newAnnotations = shiftAnnotations(annotations, oldTfv, newTfv)
        assertEquals(0, newAnnotations[0].start)
        assertEquals(2, newAnnotations[0].end)
    }

    @Test
    fun shift_annotations_delete_in_slot() {
        val oldTfv = TextFieldValue("hello world", TextRange(3, 3))
        val newTfv = TextFieldValue("helo world", TextRange(2, 2))
        val annotations = listOf(
            AnnotatedString.Range("{% text | label = 'hello' %}{% end %}", 0, 5, SLOT_TAG),
        )
        val newAnnotations = shiftAnnotations(annotations, oldTfv, newTfv)
        assertEquals(0, newAnnotations[0].start)
        assertEquals(4, newAnnotations[0].end)
    }
}
