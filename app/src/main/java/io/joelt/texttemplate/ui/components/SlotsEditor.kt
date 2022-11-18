package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.models.slots.createSlotString
import io.joelt.texttemplate.models.toTemplateSlot
import io.joelt.texttemplate.ui.theme.Typography

data class SlotsEditorState(
    val slots: List<Either<String, Slot>>,
    val selection: TextRange = TextRange.Zero,
    val composition: TextRange? = null,
    val selectedSlotIndex: Int? = null
) {
    companion object {
        private const val SLOT_TAG = "slot"
        private const val SLOT_INDEX_TAG = "slot_index"

        private fun getSlotDisplayText(slot: Slot) = slot.toDisplayString()

        /**
         * Adjusts an annotation range as though a part of the text was removed.
         * @param annotation the annotation range to adjust
         * @param range the range that was removed
         * @return the adjusted annotation range or null if the annotation range was
         * deleted
         */
        fun <T> removeRangeFromAnnotation(
            annotation: AnnotatedString.Range<T>,
            range: TextRange
        ): AnnotatedString.Range<T>? {
            val annotationRange = TextRange(annotation.start, annotation.end)
            // If the removed region does not affect the annotation
            if (!annotationRange.intersects(range)) {
                return annotation
            }

            val containsStart = range.contains(annotation.start)
            val containsEnd = range.contains(annotation.end - 1)
            // If the annotation is within the removed region
            if (containsStart && containsEnd) {
                return null
            }

            // Only the end is within range
            return if (containsEnd) {
                annotation.copy(end = range.start)
            } else if (containsStart) {
                // Only the start is within range
                annotation.copy(start = range.end)
            } else {
                // The part that was deleted is within the annotation range
                return annotation
            }
        }

        /**
         * Moves the annotation ranges based on the change in text. The change
         * in text is inferred from the change in lengths and whether or not the
         * previous selection is a selection or a cursor.
         *
         * @param annotations the annotation ranges to shift
         * @param selection the current selection or cursor
         * @param textLength the current text's length
         * @param newTextLength the new text's length
         */
        fun shiftAnnotations(
            annotations: List<AnnotatedString.Range<String>>,
            selection: TextRange,
            textLength: Int,
            newTextLength: Int,
        ): List<AnnotatedString.Range<String>> {
            val lenDiff = newTextLength - textLength
            val selectionEndShifter = { index: Int ->
                if (index >= selection.end) {
                    index + lenDiff
                } else {
                    index
                }
            }

            // Stage 1: Remove selection
            var result = mutableListOf<AnnotatedString.Range<String>>()
            if (selection.start != selection.end) {
                annotations.forEach { annotation ->
                    removeRangeFromAnnotation(annotation, selection)?.let {
                        result.add(it)
                    }
                }
            } else if (lenDiff < 0) {
                val deletionRange = TextRange(
                    selection.start + lenDiff,
                    selection.start
                )
                annotations.forEach { annotation ->
                    removeRangeFromAnnotation(annotation, deletionRange)?.let {
                        result.add(it)
                    }
                }
            } else {
                result = annotations.toMutableList()
            }

            // Stage 2: Shift indexes after selection end
            return result.map {
                it.copy(
                    start = selectionEndShifter(it.start),
                    // Ends are exclusive, so deduct one first
                    end = selectionEndShifter(it.end - 1) + 1
                )
            }
        }

        fun shiftAnnotations(
            annotations: List<AnnotatedString.Range<String>>,
            oldTextFieldValue: TextFieldValue,
            newTextFieldValue: TextFieldValue,
        ): List<AnnotatedString.Range<String>> = shiftAnnotations(
            annotations,
            oldTextFieldValue.selection,
            oldTextFieldValue.text.length,
            newTextFieldValue.text.length
        )

        /**
         * Reconciles the difference in annotations and the underlying text.
         * The underlying text is used as the final label for the slot.
         *
         * @param text the underlying text
         * @param annotations the annotations on the text, should include
         * annotations for the slot's serialized value
         */
        fun createSlotsFromAnnotations(
            text: String,
            annotations: List<AnnotatedString.Range<String>>
        ): List<Either<String, Slot>> {
            if (annotations.isEmpty()) {
                return listOf(Either.Left(text))
            }

            val slots = mutableListOf<Either<String, Slot>>()
            var nextSlotIndex = 0
            annotations.forEach {
                if (it.start != nextSlotIndex) {
                    slots.add(Either.Left(text.substring(nextSlotIndex, it.start)))
                }

                val label = text.substring(it.start, it.end)
                val slot = (it.item.toTemplateSlot()[0] as Either.Right).value
                slot.label = label
                slots.add(Either.Right(slot))

                nextSlotIndex = it.end
            }

            if (nextSlotIndex != text.length) {
                // Add the last string
                slots.add(Either.Left(text.substring(nextSlotIndex, text.length)))
            }

            return slots
        }

        /**
         * Determines if a slot should be selected.
         * @param slotStart the start of the slot string in the text, inclusive
         * @param slotEnd the end of the slot string in the text, exclusive
         * @param slotIndex the index of the slot in `slots`
         * @return whether or not the slot should be selected
         */
        fun isSlotSelected(
            selection: TextRange,
            selectedSlotIndex: Int?,
            slotStart: Int,
            slotEnd: Int,
            slotIndex: Int
        ): Boolean {
            val isSelection = selection.start != selection.end
            val cursor = selection.start
            if (isSelection) {
                return false
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

        /**
         * Determines the slot to select from the slots given.
         *
         * @param selection the selected text or cursor position
         * @param selectedSlotIndex the previously selected slot
         * @param slots the slots
         */
        fun findSelectedSlot(
            selection: TextRange,
            selectedSlotIndex: Int?,
            slots: List<Either<String, Slot>>
        ): Int? {
            slots.annotateSlotsIndexed { start, index, slot ->
                val text = getSlotDisplayText(slot)
                if (isSlotSelected(
                        selection,
                        selectedSlotIndex,
                        start,
                        start + text.length,
                        index
                    )
                ) {
                    return index
                }
                text.length
            }

            return null
        }
    }

    // Properties
    val isCursor = selection.start == selection.end
    val isSelection = !isCursor

    // Convenience for when there is no selection
    val cursor = selection.start

    private var annotatedStringCache: AnnotatedString? = null
    val annotatedString: AnnotatedString
        get() {
            annotatedStringCache?.let { return it }

            return slots.annotateSlotsIndexed { index, slot ->
                val text = getSlotDisplayText(slot)
                pushStringAnnotation(SLOT_TAG, createSlotString(slot))
                pushStringAnnotation(SLOT_INDEX_TAG, index.toString())
                withSlotStyle(index == selectedSlotIndex) { append(text) }
                pop()
                pop()
            }
        }
    val annotations by lazy {
        annotatedString.getStringAnnotations(
            SLOT_TAG,
            0,
            annotatedString.lastIndex
        )
    }
    val indexAnnotations by lazy {
        annotatedString.getStringAnnotations(
            SLOT_INDEX_TAG,
            0,
            annotatedString.lastIndex
        )
    }
    val text by lazy { annotatedString.text }
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


    // Utility functions
    private fun selectAdjacentSlot(newTextFieldValue: TextFieldValue): SlotsEditorState? {
        val lenDiff = newTextFieldValue.text.length - text.length
        if (!(isCursor && lenDiff == -1 && selectedSlotIndex == null)) {
            return null
        }

        val adjacentSlotIndex = (indexAnnotations.firstOrNull { it.end == cursor })?.item?.toInt()
        if (adjacentSlotIndex != null) {
            return SlotsEditorState(slots, selection, composition, adjacentSlotIndex)
        }
        return null
    }

    // Change state functions
    fun withNewTextFieldValue(newTextFieldValue: TextFieldValue): SlotsEditorState {
        // Select the adjacent slot if that is an option
        selectAdjacentSlot(newTextFieldValue)?.let { return it }

        val textChanged = newTextFieldValue.text != text
        val newSlots = if (textChanged) {
            val newAnnotations = shiftAnnotations(annotations, textFieldValue, newTextFieldValue)
            createSlotsFromAnnotations(newTextFieldValue.text, newAnnotations)
        } else {
            slots
        }
        val newSelectedSlotIndex =
            findSelectedSlot(newTextFieldValue.selection, selectedSlotIndex, newSlots)

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

        val slotText = getSlotDisplayText(slot)
        val newText = text.substring(0, selection.start) + slotText + text.substring(selection.end)
        val annotationsWithText = shiftAnnotations(
            annotations,
            textFieldValue,
            TextFieldValue(newText, TextRange(selection.start), null)
        )

        // Insert the annotation for the slot
        // Find where to insert in the list of annotations (annotation list is
        // assumed to be sorted)
        val insertIndex = annotationsWithText.let {
            for (index in it.indices) {
                if (it[index].start >= selection.start) {
                    return@let index
                }
            }
            it.size
        }
        // Insert the new annotation
        val newAnnotations = annotationsWithText.toMutableList()
        newAnnotations.add(
            insertIndex,
            AnnotatedString.Range(
                createSlotString(slot),
                selection.start,
                selection.start + slotText.length,
                SLOT_TAG
            )
        )

        val newSlots = createSlotsFromAnnotations(newText, newAnnotations)
        return SlotsEditorState(
            newSlots,
            TextRange(selection.start, selection.start + slotText.length),
            null
        )
    }
}

@Composable
fun SlotsEditor(
    state: SlotsEditorState,
    modifier: Modifier = Modifier,
    onStateChange: (SlotsEditorState) -> Unit,
) {
    Column(modifier = Modifier.fillMaxHeight()) {
        PlaceholderTextField(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .weight(1.0f),
            textStyle = Typography.bodyLarge,
            visualTransformation = {
                // Apply the styles from the annotatedString through visualTransformation instead
                TransformedText(state.annotatedString, OffsetMapping.Identity)
            },
            value = state.textFieldValue,
            onValueChange = {
                val newState = state.withNewTextFieldValue(it)
                onStateChange(newState)
            }
        )
        Box(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
        ) {
            BottomAppBar {
                Spacer(modifier = Modifier.weight(1f))
                if (state.selectedSlotIndex == null) {
                    val slotLabel = stringResource(R.string.plain_text_slot_placeholder)
                    IconButton(onClick = {
                        val slot = PlainTextSlot("")
                        slot.label = slotLabel
                        onStateChange(state.insertSlotAtSelection(slot))
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SlotsEditorPreview() {
    val template = genTemplates(1)[0]
    var state by remember {
        mutableStateOf(SlotsEditorState(template.slots))
    }

    Column {
        SlotsEditor(state, onStateChange = {
            state = it
        })
    }
}
