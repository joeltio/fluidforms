package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

private const val SLOT_TAG = "slot"
private const val SELECTED_TAG = "selected"

fun List<Either<String, Slot>>.annotateSlotInfo(selection: TextRange) =
    this.annotateSlotsIndexed { start, _, slot ->
        val text = slot.toDisplayString()
        val selected = if (selection.start == selection.end) {
            TextRange(start, start + text.length).contains(selection.start)
        } else {
            false
        }

        pushStringAnnotation(SLOT_TAG, createSlotString(slot))
        if (selected) {
            pushStringAnnotation(SELECTED_TAG, "true")
        }
        withSlotStyle(selected) {
            append(text)
        }
        if (selected) {
            pop()
        }
        pop()

        return@annotateSlotsIndexed text.length
    }

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

private fun <T> removeSelection(
    annotation: AnnotatedString.Range<T>,
    selection: TextRange
): AnnotatedString.Range<T>? {
    val annotationRange = TextRange(annotation.start, annotation.end)
    // If the removed region does not affect the annotation
    if (!annotationRange.intersects(selection)) {
        return annotation
    }

    val containsStart = selection.contains(annotation.start)
    val containsEnd = selection.contains(annotation.end - 1)
    // If the annotation is within the removed region
    if (containsStart && containsEnd) {
        return null
    }

    // Only the end is within range
    return if (containsEnd) {
        annotation.copy(end = selection.start)
    } else if (containsStart) {
        // Only the start is within range
        annotation.copy(start = selection.end)
    } else {
        // The part that was deleted is within the annotation range
        return annotation
    }
}

fun shiftAnnotations(
    annotations: List<AnnotatedString.Range<String>>,
    oldTfv: TextFieldValue,
    newTextLength: Int,
): List<AnnotatedString.Range<String>> {
    val lenDiff = newTextLength - oldTfv.text.length
    val selectionEndShifter = { index: Int ->
        if (index >= oldTfv.selection.end) {
            index + lenDiff
        } else {
            index
        }
    }

    // Stage 1: Remove selection
    var result = mutableListOf<AnnotatedString.Range<String>>()
    if (oldTfv.selection.start != oldTfv.selection.end) {
        annotations.forEach { annotation ->
            removeSelection(annotation, oldTfv.selection)?.let {
                result.add(it)
            }
        }
    } else if (lenDiff < 0) {
        val deletionRange = TextRange(
            oldTfv.selection.start + lenDiff,
            oldTfv.selection.start
        )
        annotations.forEach { annotation ->
            removeSelection(annotation, deletionRange)?.let {
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

fun AnnotatedString.getAllStringAnnotations(tag: String): List<AnnotatedString.Range<String>> =
    this.getStringAnnotations(tag, 0, this.lastIndex)

fun <T> List<T>.insertSortAsc(newItem: T, getVal: (T) -> Int): List<T> {
    val newItemVal = getVal(newItem)
    var insertIndex = this.lastIndex + 1

    for (index in 0 until this.size) {
        val item = this[index]
        if (getVal(item) >= newItemVal) {
            insertIndex = index
            break
        }
    }

    val newList = this.toMutableList()
    newList.add(insertIndex, newItem)
    return newList
}

@Composable
fun SlotsEditor(
    slots: List<Either<String, Slot>>,
    modifier: Modifier = Modifier,
    onSlotsChange: (List<Either<String, Slot>>) -> Unit,
) {
    var tfvState by remember {
        mutableStateOf(TextFieldValue())
    }

    // Convert the slots to annotations
    val annotatedString = slots.annotateSlotInfo(tfvState.selection)
    val annotations = annotatedString.getAllStringAnnotations(SLOT_TAG)
    val selectedAnnotation =
        annotatedString.getAllStringAnnotations(SELECTED_TAG).firstOrNull()?.let {
            annotatedString.getStringAnnotations(SLOT_TAG, it.start, it.end)
        }

    // Update tfv with the latest value from recomposition
    // Use text instead of annotatedString because annotations make TextField stop working
    val tfv = tfvState.copy(text = annotatedString.text)

    Column(modifier = Modifier.fillMaxHeight()) {
        PlaceholderTextField(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .weight(1.0f),
            visualTransformation = {
                // Apply the styles from the annotatedString through visualTransformation instead
                TransformedText(annotatedString, OffsetMapping.Identity)
            },
            value = tfv,
            onValueChange = { newTfv ->
                tfvState = newTfv

                if (newTfv.text != tfv.text) {
                    val newAnnotations = shiftAnnotations(annotations, tfv, newTfv.text.length)
                    val newSlots = createSlotsFromAnnotations(newTfv.text, newAnnotations)
                    onSlotsChange(newSlots)
                }
            }
        )
        Box(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
        ) {
            if (selectedAnnotation == null && tfvState.selection.start == tfvState.selection.end) {
                val cursor = tfvState.selection.start
                // By default, create a new PlainTextSlot
                val newSlot = PlainTextSlot("")
                newSlot.label = stringResource(R.string.plain_text_slot_placeholder)
                val slotDisplayStr = newSlot.toDisplayString()
                IconButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = {
                    val newText = StringBuilder(tfv.text).insert(cursor, slotDisplayStr).toString()
                    val shiftedAnnotations = shiftAnnotations(annotations, tfv, newText.length)
                    val newAnnotations = shiftedAnnotations.insertSortAsc(
                        AnnotatedString.Range(
                            createSlotString(newSlot),
                            cursor,
                            cursor + slotDisplayStr.length,
                            SLOT_TAG
                        )
                    ) { it.start }
                    val newSlots = createSlotsFromAnnotations(newText, newAnnotations)
                    onSlotsChange(newSlots)
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}

@Preview
@Composable
private fun SlotsEditorPreview() {
    val template = genTemplates(1)[0]
    var slots by remember {
        mutableStateOf(template.slots)
    }

    Column {
        SlotsEditor(slots, onSlotsChange = {
            slots = it
        })
    }
}
