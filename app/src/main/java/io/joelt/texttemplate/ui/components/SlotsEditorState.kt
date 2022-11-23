package io.joelt.texttemplate.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.slots.Slot

// Cursors for indexing slots
private data class Cursor(val slotIndex: Int, val subIndex: Int)
private data class CursorRange(val start: Cursor, val end: Cursor)

// Slot Extensions
private val Either<String, Slot>.text: String
    get() = when (this) {
        is Either.Left -> this.value
        is Either.Right -> this.value.label
    }

private fun Either<String, Slot>.withNewValue(value: (String) -> String): Either<String, Slot> =
    when (this) {
        is Either.Left -> Either.Left(value(this.text))
        is Either.Right -> Either.Right(this.value.makeCopy(label = value(this.text)))
    }

// Cursor extensions
private fun List<Either<String, Slot>>.cursorAt(index: Int): Cursor {
    if (this.isEmpty()) {
        throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")
    }

    var offset = 0
    this.forEachIndexed { slotIndex, either ->
        val str = either.text
        if (index in offset until offset + str.length) {
            return Cursor(slotIndex, index - offset)
        }

        offset += str.length
    }

    if (index == offset) {
        return Cursor(this.lastIndex, this.last().text.length)
    }

    throw IndexOutOfBoundsException("Index: $index, Size: ${this.size}")
}

// List extensions
private fun <T> List<T>.replace(index: Int, item: T): List<T> {
    return this.toMutableList().apply {
        removeAt(index)
        add(index, item)
    }
}

private fun <T> List<T>.add(item: T): List<T> {
    return this.toMutableList().apply { add(item) }
}

private fun <T> List<T>.add(index: Int, item: T): List<T> {
    return this.toMutableList().apply { add(index, item) }
}

// Other extensions
private fun TextRange.toCursorRange(slots: List<Either<String, Slot>>): CursorRange =
    CursorRange(slots.cursorAt(this.start), slots.cursorAt(this.end - 1))

private fun String.insert(position: Int, value: String) =
    this.substring(0, position) + value + this.substring(position)

data class SlotsEditorState(
    val slots: List<Either<String, Slot>>,
    val selection: TextRange = TextRange.Zero,
    val composition: TextRange? = null,
    val selectedSlotIndex: Int? = null
) {
    // Properties
    val isCursor = selection.start == selection.end
    val isSelection = !isCursor

    // Convenience for when there is no selection
    val cursor = selection.start

    private var annotatedStringCache: AnnotatedString? = null
    val annotatedString: AnnotatedString
        @Composable
        get() {
            annotatedStringCache?.let { return it }

            return slots.annotateSlotsIndexed { index, slot ->
                val text = slot.label
                withSlotStyle(index == selectedSlotIndex) { append(text) }
            }
        }
    val text by lazy {
        annotatedStringCache?.let { return@lazy it.text }
        slots.annotateSlots {
            append(it.label)
        }.text
    }
    val textFieldValue by lazy { TextFieldValue(text, selection, composition) }

    constructor(
        slots: List<Either<String, Slot>>,
        selection: TextRange = TextRange.Zero,
        composition: TextRange? = null,
        selectedSlotIndex: Int? = null,
        annotatedStringCache: AnnotatedString?
    ) : this(slots, selection, composition, selectedSlotIndex) {
        this.annotatedStringCache = annotatedStringCache
    }

    // Insert, Delete, Replace
    private fun insert(
        slots: List<Either<String, Slot>>,
        position: Int,
        value: String
    ): List<Either<String, Slot>> {
        require(slots.isNotEmpty()) {
            "insert assumes that slots is not empty"
        }

        // If inserting into a slot
        if (selectedSlotIndex != null) {
            val either = slots[selectedSlotIndex]
            val cursor = slots.cursorAt(position)

            require(cursor.slotIndex == selectedSlotIndex || cursor.slotIndex == (selectedSlotIndex + 1)) {
                "Insert position should be at selected slot"
            }

            val newEither = either.withNewValue {
                if (cursor.slotIndex == selectedSlotIndex) {
                    it.insert(cursor.subIndex, value)
                } else {
                    // position == selectedSlotIndex + 1, meaning, insert at the
                    // end of the label
                    it + value
                }
            }

            return slots.replace(selectedSlotIndex, newEither)
        }

        val cursor = slots.cursorAt(position)
        val either = slots[cursor.slotIndex]

        // If the either is a string, just insert it in
        if (either is Either.Left) {
            val newStr = either.value.insert(cursor.subIndex, value)
            return slots.replace(cursor.slotIndex, Either.Left(newStr))
        }

        // If the either is a slot, and we're inserting behind it
        if (position == text.length) {
            return slots.add(Either.Left(value))
        }

        require(cursor.subIndex == 0) {
            "Cannot insert inside a slot"
        }

        // The item at the current slotIndex is a Slot
        val precedingEither = slots.getOrNull(cursor.slotIndex - 1)
        if (precedingEither == null) {
            // There are no preceding items, insert a string to the start
            return slots.add(0, Either.Left(value))
        } else if (precedingEither is Either.Right) {
            // The preceding item is a slot, insert a string between the slots
            return slots.add(cursor.slotIndex, Either.Left(value))
        }

        // The preceding item is a string
        return slots.replace(cursor.slotIndex - 1, precedingEither.withNewValue { it + value })
    }

    private fun delete(selection: TextRange): List<Either<String, Slot>> {
        require(slots.isNotEmpty()) {
            "delete assumes slots is not empty"
        }

        require(!selection.collapsed) {
            "delete only deletes selections"
        }

        val newSlots = slots.toMutableList()
        val (start, end) = selection.toCursorRange(slots)
        if (start.slotIndex == end.slotIndex) {
            val either = slots[start.slotIndex]
            val newEither = either.withNewValue {
                it.substring(0, start.subIndex) + it.substring(end.subIndex + 1)
            }

            newSlots.removeAt(start.slotIndex)
            if (newEither.text.isNotEmpty()) {
                newSlots.add(start.slotIndex, newEither)
            }
            return newSlots
        }

        // Delete everything between the two slotIndexes
        for (index in (start.slotIndex + 1) until end.slotIndex) {
            newSlots.removeAt(start.slotIndex + 1)
        }

        // Delete the subIndexes
        val newStartSlot = slots[start.slotIndex].withNewValue {
            it.substring(0, start.subIndex)
        }
        val newEndSlot = slots[end.slotIndex].withNewValue {
            it.substring(end.subIndex + 1)
        }

        newSlots.removeAt(start.slotIndex + 1)
        newSlots.removeAt(start.slotIndex)

        if (newStartSlot is Either.Left && newEndSlot is Either.Left) {
            // Combine the strings
            newSlots.add(start.slotIndex, Either.Left(newStartSlot.value + newEndSlot.value))
            if (newSlots[start.slotIndex].text.isEmpty()) {
                newSlots.removeAt(start.slotIndex)
            }
        } else {
            newSlots.add(start.slotIndex, newStartSlot)
            newSlots.add(start.slotIndex + 1, newEndSlot)
            if (newSlots[start.slotIndex + 1].text.isEmpty()) {
                newSlots.removeAt(start.slotIndex + 1)
            }
            if (newSlots[start.slotIndex].text.isEmpty()) {
                newSlots.removeAt(start.slotIndex)
            }
        }

        return newSlots
    }

    private fun replace(selection: TextRange, value: String): List<Either<String, Slot>> {
        require(slots.isNotEmpty()) {
            "replace assumes slots is not empty"
        }

        val newSlots = delete(selection).toMutableList()
        val (start, end) = selection.toCursorRange(slots)
        if (slots[start.slotIndex] is Either.Right
            && start.slotIndex == end.slotIndex
            && start.subIndex == 0
            && end.subIndex == slots[start.slotIndex].text.lastIndex
        ) {
            // If the deleted portion is a whole slot, restore it
            val newSlot = (slots[start.slotIndex] as Either.Right).value.makeCopy(label = value)
            newSlots.add(start.slotIndex, Either.Right(newSlot))
            return newSlots
        } else if (newSlots.isEmpty()) {
            return listOf(Either.Left(value))
        }

        return insert(newSlots, selection.start, value)
    }

    private fun shouldSelectSlot(
        selection: TextRange,
        selectedSlotIndex: Int?,
        slotStart: Int,
        slotEnd: Int,
        slotIndex: Int
    ): Boolean {
        val isSelection = selection.start != selection.end
        val cursor = selection.start
        if (isSelection) {
            // Only select if it is within the slot
            val slotRange = TextRange(slotStart, slotEnd + 1)
            return slotRange.contains(selection.start) && slotRange.contains(selection.end)
        }

        val len = slotEnd - slotStart
        val range = if (len == 1) {
            // If there is only one character, select when adjacent
            TextRange(slotStart, slotEnd + 1)
        } else if (selectedSlotIndex != slotIndex) {
            // If this is not the currently selected slot, be more strict
            // only select when cursor is one character within the edges
            TextRange(slotStart + 1, slotEnd)
        } else {
            // If there is a currently selected slot, be more lenient
            // select as long as the cursor is at the edges
            TextRange(slotStart, slotEnd + 1)
        }
        return range.contains(cursor)
    }

    private fun findSelectedSlot(
        selection: TextRange,
        selectedSlotIndex: Int?,
        slots: List<Either<String, Slot>>
    ): Int? {
        slots.annotateSlotsIndexed { start, slotIndex, slot ->
            if (
                shouldSelectSlot(
                    selection,
                    selectedSlotIndex,
                    start,
                    start + slot.label.length,
                    slotIndex
                )
            ) {
                return slotIndex
            }

            slot.label.length
        }
        return null
    }

    private fun selectAdjacentSlot(): SlotsEditorState? {
        if (!isCursor) {
            return null
        }

        val position = selection.start
        var cursor = slots.cursorAt(position)
        if (selectedSlotIndex == null) {
            if (position == text.length) {
                cursor = Cursor(cursor.slotIndex + 1, 0)
            } else if (cursor.subIndex != 0) {
                return null
            }

            // Try to select from unselected
            val precedingEither = slots.getOrNull(cursor.slotIndex - 1)
            if (precedingEither is Either.Right) {
                return SlotsEditorState(slots, selection, composition, cursor.slotIndex - 1)
            }
            return null
        }

        // Try to unselect from selected
        if (cursor.slotIndex == selectedSlotIndex && cursor.subIndex == 0) {
            return SlotsEditorState(slots, selection, composition, null)
        }
        return null
    }

    private fun unselectCurrentSlotWhenEnterPressed(newText: String): SlotsEditorState? {
        if (selectedSlotIndex == null || newText != "\n") {
            return null
        }

        val cursor = slots.cursorAt(selection.start)
        val endOfSlot = selection.start + slots[cursor.slotIndex].text.length - cursor.subIndex
        return SlotsEditorState(slots, TextRange(endOfSlot), null, null)
    }

    // Change state functions
    fun withNewTextFieldValue(newTextFieldValue: TextFieldValue): SlotsEditorState {
        val textChanged = newTextFieldValue.text != text
        val lenDiff = newTextFieldValue.text.length - textFieldValue.text.length
        val newSlots = if (slots.isEmpty()) {
            listOf(Either.Left(newTextFieldValue.text))
        } else if (!textChanged) {
            slots
        } else if (isCursor && lenDiff > 0) {
            // Insert
            val newText = newTextFieldValue.text.substring(cursor, cursor + lenDiff)
            unselectCurrentSlotWhenEnterPressed(newText)?.let { return it }
            insert(slots, cursor, newText)
        } else if ((isCursor && lenDiff < 0) || (isSelection && selection.length == -lenDiff)) {
            // Delete
            // Select the adjacent slot if that is an option
            selectAdjacentSlot()?.let { return it }
            if (isCursor) {
                delete(TextRange(selection.start + lenDiff, selection.end))
            } else {
                delete(selection)
            }
        } else {
            // Replace
            val insertValueLen = selection.length + lenDiff
            val newText = newTextFieldValue.text.substring(
                selection.start,
                selection.start + insertValueLen
            )
            unselectCurrentSlotWhenEnterPressed(newText)?.let { return it }
            replace(selection, newText)
        }

        val newSelectedSlotIndex = if (textChanged || selection != newTextFieldValue.selection) {
            findSelectedSlot(newTextFieldValue.selection, selectedSlotIndex, newSlots)
        } else {
            selectedSlotIndex
        }

        return SlotsEditorState(
            newSlots,
            newTextFieldValue.selection,
            newTextFieldValue.composition,
            newSelectedSlotIndex,
            if (textChanged) {
                null
            } else {
                annotatedStringCache
            }
        )
    }

    fun insertSlotAtSelection(slot: Slot): SlotsEditorState {
        // Don't insert any slots if there is already a selected slot
        if (selectedSlotIndex != null) {
            return this
        }

        if (slots.isEmpty()) {
            return SlotsEditorState(
                listOf(Either.Right(slot)),
                TextRange(0, slot.label.length),
                null,
                0
            )
        }

        // Delete if there is a selection
        val newSlots = if (isSelection) {
            delete(selection)
        } else {
            slots
        }.toMutableList()

        // Insert the slot
        val cursor = slots.cursorAt(selection.start)
        if (cursor.subIndex == 0) {
            // Insert on the left of the slotIndex
            newSlots.add(cursor.slotIndex, Either.Right(slot))
            return SlotsEditorState(
                newSlots,
                TextRange(selection.start, selection.start + slot.label.length),
                null,
                cursor.slotIndex
            )
        }

        // Split the string at slotIndex
        val beforeStr = newSlots[cursor.slotIndex].text.substring(0, cursor.subIndex)
        val afterStr = newSlots[cursor.slotIndex].text.substring(cursor.subIndex)
        newSlots.removeAt(cursor.slotIndex)
        newSlots.add(cursor.slotIndex, Either.Left(beforeStr))
        newSlots.add(cursor.slotIndex + 1, Either.Right(slot))
        newSlots.add(cursor.slotIndex + 2, Either.Left(afterStr))

        return SlotsEditorState(
            newSlots,
            TextRange(selection.start, selection.start + slot.label.length),
            null,
            cursor.slotIndex + 1
        )
    }
}