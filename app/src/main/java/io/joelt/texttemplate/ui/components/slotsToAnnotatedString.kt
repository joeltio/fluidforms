package io.joelt.texttemplate.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.slots.Slot

fun List<Either<String, Slot>>.annotateSlots(
    annotationTag: String,
    selectedIndex: Int?
): AnnotatedString =
    buildAnnotatedString {
        this@annotateSlots.forEachIndexed { slotIndex, it ->
            when (it) {
                is Either.Left -> {
                    append(it.value)
                }
                is Either.Right -> {
                    // Annotate the string with the slot so that it can be
                    // retrieved later
                    pushStringAnnotation(annotationTag, slotIndex.toString())
                    append(
                        createSlotText(
                            it.value,
                            selectedIndex == slotIndex
                        )
                    )
                    pop()
                }
            }
        }
    }
