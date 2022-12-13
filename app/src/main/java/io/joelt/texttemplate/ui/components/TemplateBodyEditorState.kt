package io.joelt.texttemplate.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.slots.Slot

// Cursors for indexing template body
private data class Cursor(val itemIndex: Int, val subIndex: Int)
private data class CursorRange(val start: Cursor, val end: Cursor)

// Template Body Extensions
private val Either<String, Slot>.text: String
    get() = when (this) {
        is Either.Left -> this.value
        is Either.Right -> this.value.displayLabel
    }

private fun Either<String, Slot>.withNewValue(value: (String) -> String): Either<String, Slot> =
    when (this) {
        is Either.Left -> Either.Left(value(this.text))
        is Either.Right -> Either.Right(this.value.makeCopy(displayLabel = value(this.text)))
    }

// Cursor extensions
private fun List<Either<String, Slot>>.cursorAt(index: Int): Cursor {
    if (this.isEmpty()) {
        throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")
    }

    var offset = 0
    this.forEachIndexed { itemIndex, item ->
        val str = item.text
        if (index in offset until offset + str.length) {
            return Cursor(itemIndex, index - offset)
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
private fun TextRange.toCursorRange(templateBody: List<Either<String, Slot>>): CursorRange =
    CursorRange(templateBody.cursorAt(this.start), templateBody.cursorAt(this.end - 1))

private fun String.insert(position: Int, value: String) =
    this.substring(0, position) + value + this.substring(position)

data class TemplateBodyEditorState(
    val templateBody: List<Either<String, Slot>>,
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

            return templateBody.annotateSlotsIndexed { index, slot ->
                val text = slot.displayLabel
                withSlotStyle(index == selectedSlotIndex) { append(text) }
            }
        }
    val text by lazy {
        annotatedStringCache?.let { return@lazy it.text }
        templateBody.annotateSlots {
            append(it.displayLabel)
        }.text
    }
    val textFieldValue by lazy { TextFieldValue(text, selection, composition) }

    constructor(
        body: List<Either<String, Slot>>,
        selection: TextRange = TextRange.Zero,
        composition: TextRange? = null,
        selectedSlotIndex: Int? = null,
        annotatedStringCache: AnnotatedString?
    ) : this(body, selection, composition, selectedSlotIndex) {
        this.annotatedStringCache = annotatedStringCache
    }

    // Insert, Delete, Replace
    private fun insert(
        body: List<Either<String, Slot>>,
        position: Int,
        value: String
    ): List<Either<String, Slot>> {
        require(body.isNotEmpty()) {
            "insert assumes that slots is not empty"
        }

        // If inserting into a slot
        if (selectedSlotIndex != null) {
            val item = body[selectedSlotIndex]
            val cursor = body.cursorAt(position)

            require(cursor.itemIndex == selectedSlotIndex || cursor.itemIndex == (selectedSlotIndex + 1)) {
                "Insert position should be at selected slot"
            }

            val newItem = item.withNewValue {
                if (cursor.itemIndex == selectedSlotIndex) {
                    it.insert(cursor.subIndex, value)
                } else {
                    // position == selectedSlotIndex + 1, meaning, insert at the
                    // end of the label
                    it + value
                }
            }

            return body.replace(selectedSlotIndex, newItem)
        }

        val cursor = body.cursorAt(position)
        val item = body[cursor.itemIndex]

        // If the either is a string, just insert it in
        if (item is Either.Left) {
            val newStr = item.value.insert(cursor.subIndex, value)
            return body.replace(cursor.itemIndex, Either.Left(newStr))
        }

        // If the item is a slot, and we're inserting behind it
        if (position == text.length) {
            return body.add(Either.Left(value))
        }

        require(cursor.subIndex == 0) {
            "Cannot insert inside a slot"
        }

        // The item at the current slotIndex is a Slot
        val precedingItem = body.getOrNull(cursor.itemIndex - 1)
        if (precedingItem == null) {
            // There are no preceding items, insert a string to the start
            return body.add(0, Either.Left(value))
        } else if (precedingItem is Either.Right) {
            // The preceding item is a slot, insert a string between the slots
            return body.add(cursor.itemIndex, Either.Left(value))
        }

        // The preceding item is a string
        return body.replace(cursor.itemIndex - 1, precedingItem.withNewValue { it + value })
    }

    private fun delete(selection: TextRange): List<Either<String, Slot>> {
        require(templateBody.isNotEmpty()) {
            "delete assumes slots is not empty"
        }

        require(!selection.collapsed) {
            "delete only deletes selections"
        }

        val newBody = templateBody.toMutableList()
        val (start, end) = selection.toCursorRange(templateBody)
        if (start.itemIndex == end.itemIndex) {
            val item = templateBody[start.itemIndex]
            val newItem = item.withNewValue {
                it.substring(0, start.subIndex) + it.substring(end.subIndex + 1)
            }

            newBody.removeAt(start.itemIndex)
            if (newItem.text.isNotEmpty()) {
                newBody.add(start.itemIndex, newItem)
            }
            return newBody
        }

        // Delete everything between the two slotIndexes
        for (index in (start.itemIndex + 1) until end.itemIndex) {
            newBody.removeAt(start.itemIndex + 1)
        }

        // Delete the subIndexes
        val newStartItem = templateBody[start.itemIndex].withNewValue {
            it.substring(0, start.subIndex)
        }
        val newEndItem = templateBody[end.itemIndex].withNewValue {
            it.substring(end.subIndex + 1)
        }

        newBody.removeAt(start.itemIndex + 1)
        newBody.removeAt(start.itemIndex)

        if (newStartItem is Either.Left && newEndItem is Either.Left) {
            // Combine the strings
            newBody.add(start.itemIndex, Either.Left(newStartItem.value + newEndItem.value))
            if (newBody[start.itemIndex].text.isEmpty()) {
                newBody.removeAt(start.itemIndex)
            }
        } else {
            newBody.add(start.itemIndex, newStartItem)
            newBody.add(start.itemIndex + 1, newEndItem)
            if (newBody[start.itemIndex + 1].text.isEmpty()) {
                newBody.removeAt(start.itemIndex + 1)
            }
            if (newBody[start.itemIndex].text.isEmpty()) {
                newBody.removeAt(start.itemIndex)
            }
        }

        return newBody
    }

    private fun replace(selection: TextRange, value: String): List<Either<String, Slot>> {
        require(templateBody.isNotEmpty()) {
            "replace assumes slots is not empty"
        }

        val newBody = delete(selection).toMutableList()
        val (start, end) = selection.toCursorRange(templateBody)
        if (templateBody[start.itemIndex] is Either.Right
            && start.itemIndex == end.itemIndex
            && start.subIndex == 0
            && end.subIndex == templateBody[start.itemIndex].text.lastIndex
        ) {
            // If the deleted portion is a whole slot, restore it
            val newSlot = (templateBody[start.itemIndex] as Either.Right).value.makeCopy(displayLabel = value)
            newBody.add(start.itemIndex, Either.Right(newSlot))
            return newBody
        } else if (newBody.isEmpty()) {
            return listOf(Either.Left(value))
        }

        return insert(newBody, selection.start, value)
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
                    start + slot.displayLabel.length,
                    slotIndex
                )
            ) {
                return slotIndex
            }

            slot.displayLabel.length
        }
        return null
    }

    private fun selectAdjacentSlot(): TemplateBodyEditorState? {
        if (!isCursor) {
            return null
        }

        val position = selection.start
        var cursor = templateBody.cursorAt(position)
        if (selectedSlotIndex == null) {
            if (position == text.length) {
                cursor = Cursor(cursor.itemIndex + 1, 0)
            } else if (cursor.subIndex != 0) {
                return null
            }

            // Try to select from unselected
            val precedingItem = templateBody.getOrNull(cursor.itemIndex - 1)
            if (precedingItem is Either.Right) {
                return TemplateBodyEditorState(templateBody, selection, composition, cursor.itemIndex - 1)
            }
            return null
        }

        // Try to unselect from selected
        if (cursor.itemIndex == selectedSlotIndex && cursor.subIndex == 0) {
            return TemplateBodyEditorState(templateBody, selection, composition, null)
        }
        return null
    }

    private fun unselectCurrentSlotWhenEnterPressed(newText: String): TemplateBodyEditorState? {
        if (selectedSlotIndex == null || newText != "\n") {
            return null
        }

        val cursor = templateBody.cursorAt(selection.start)
        val endOfSlot = selection.start + templateBody[cursor.itemIndex].text.length - cursor.subIndex
        return TemplateBodyEditorState(templateBody, TextRange(endOfSlot), null, null)
    }

    // Change state functions
    fun withNewTextFieldValue(newTextFieldValue: TextFieldValue): TemplateBodyEditorState {
        val textChanged = newTextFieldValue.text != text
        val lenDiff = newTextFieldValue.text.length - textFieldValue.text.length
        val newBody = if (templateBody.isEmpty()) {
            listOf(Either.Left(newTextFieldValue.text))
        } else if (!textChanged) {
            templateBody
        } else if (isCursor && lenDiff > 0) {
            // Insert
            val newText = newTextFieldValue.text.substring(cursor, cursor + lenDiff)
            unselectCurrentSlotWhenEnterPressed(newText)?.let { return it }
            insert(templateBody, cursor, newText)
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
            findSelectedSlot(newTextFieldValue.selection, selectedSlotIndex, newBody)
        } else {
            selectedSlotIndex
        }

        return TemplateBodyEditorState(
            newBody,
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

    fun insertSlotAtSelection(slot: Slot): TemplateBodyEditorState {
        // Don't insert any slots if there is already a selected slot
        if (selectedSlotIndex != null) {
            return this
        }

        if (templateBody.isEmpty()) {
            return TemplateBodyEditorState(
                listOf(Either.Right(slot)),
                TextRange(0, slot.displayLabel.length),
                null,
                0
            )
        }

        // Delete if there is a selection
        val newBody = if (isSelection) {
            delete(selection)
        } else {
            templateBody
        }.toMutableList()

        // Insert the slot
        val cursor = templateBody.cursorAt(selection.start)
        if (cursor.subIndex == 0) {
            // Insert on the left of the slotIndex
            newBody.add(cursor.itemIndex, Either.Right(slot))
            return TemplateBodyEditorState(
                newBody,
                TextRange(selection.start, selection.start + slot.displayLabel.length),
                null,
                cursor.itemIndex
            )
        }

        // Split the string at slotIndex
        val beforeStr = newBody[cursor.itemIndex].text.substring(0, cursor.subIndex)
        val afterStr = newBody[cursor.itemIndex].text.substring(cursor.subIndex)
        newBody.removeAt(cursor.itemIndex)
        newBody.add(cursor.itemIndex, Either.Left(beforeStr))
        newBody.add(cursor.itemIndex + 1, Either.Right(slot))
        newBody.add(cursor.itemIndex + 2, Either.Left(afterStr))

        return TemplateBodyEditorState(
            newBody,
            TextRange(selection.start, selection.start + slot.displayLabel.length),
            null,
            cursor.itemIndex + 1
        )
    }
}
