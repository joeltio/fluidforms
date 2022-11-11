package io.joelt.texttemplate.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.slots.Slot

fun List<Either<String, Slot>>.annotateSlotsIndexed(block: AnnotatedString.Builder.(Int, Slot) -> Unit): AnnotatedString =
    buildAnnotatedString {
        this@annotateSlotsIndexed.forEachIndexed { index, it ->
            when (it) {
                is Either.Left -> {
                    append(it.value)
                }
                is Either.Right -> {
                    block(index, it.value)
                }
            }
        }
    }

fun List<Either<String, Slot>>.annotateSlots(block: AnnotatedString.Builder.(Slot) -> Unit): AnnotatedString =
    annotateSlotsIndexed { _, it ->
        block(it)
    }

fun <R: Any>AnnotatedString.Builder.withSlotStyle(
    selected: Boolean,
    block: AnnotatedString.Builder.() -> R
): R {
    val bgColor = if (selected) {
        Color.Yellow
    } else {
        Color.Gray
    }
    return withStyle(SpanStyle(background = bgColor)) {
        block()
    }
}
