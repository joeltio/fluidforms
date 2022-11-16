package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.models.slots.createSlotString
import io.joelt.texttemplate.models.toTemplateSlot

private const val SLOT_TAG = "slot"

fun List<Either<String, Slot>>.annotateSlotInfo() = this.annotateSlots {
    pushStringAnnotation(SLOT_TAG, createSlotString(it))
    withSlotStyle(false) {
        append(it.toDisplayString())
    }
    pop()
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
    if (!annotationRange.intersects(selection)) { // or the other way around?
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
    newTfv: TextFieldValue
): List<AnnotatedString.Range<String>> {
    val lenDiff = newTfv.text.length - oldTfv.text.length
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

@Composable
fun SlotsEditor(
    slots: List<Either<String, Slot>>,
    modifier: Modifier = Modifier,
    onSlotsChange: (List<Either<String, Slot>>) -> Unit,
) {
    // Convert the slots to annotations
    val annotatedString = slots.annotateSlotInfo()
    val annotations = annotatedString.getStringAnnotations(SLOT_TAG, 0, annotatedString.lastIndex)

    var tfvState by remember {
        mutableStateOf(TextFieldValue(annotatedString))
    }
    // Update tfv with the latest value from recomposition
    // Use text instead of annotatedString because annotations make TextField stop working
    val tfv = tfvState.copy(text = annotatedString.text)

    Box {
        PlaceholderTextField(modifier = modifier, visualTransformation = {
            // Apply the styles from the annotatedString throuhg visualTransformation instead
            TransformedText(annotatedString, OffsetMapping.Identity)
        }, value = tfv, onValueChange = { newTfv ->
            tfvState = newTfv

            if (newTfv.text != tfv.text) {
                val newAnnotations = shiftAnnotations(annotations, tfv, newTfv)
                val newSlots = createSlotsFromAnnotations(newTfv.text, newAnnotations)
                onSlotsChange(newSlots)
            }
        })
        Box(modifier = Modifier.imePadding()) {
            // TODO: Editor toolbar
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
