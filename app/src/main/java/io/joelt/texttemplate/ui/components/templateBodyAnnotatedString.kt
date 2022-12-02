package io.joelt.texttemplate.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.slots.Slot

/**
 * Create an AnnotatedString from a template body. Iterate through the slots in
 * the list.
 * For each slot in the list, provides:
 * start: the starting index in the final AnnotatedString, inclusive
 * slotIndex: the index of the slot in the list
 * slot: the slot
 * return: the length of string added. Return 0 if you do not need the
 */
inline fun List<Either<String, Slot>>.annotateSlotsIndexed(
    block: AnnotatedString.Builder.(start: Int, slotIndex: Int, slot: Slot) -> Int
): AnnotatedString =
    buildAnnotatedString {
        var count = 0
        this@annotateSlotsIndexed.forEachIndexed { index, it ->
            when (it) {
                is Either.Left -> {
                    append(it.value)
                    count += it.value.length
                }
                is Either.Right -> {
                    count += block(count, index, it.value)
                }
            }
        }
    }

inline fun List<Either<String, Slot>>.annotateSlotsIndexed(
    block: AnnotatedString.Builder.(slotIndex: Int, slot: Slot) -> Unit
): AnnotatedString =
    annotateSlotsIndexed { _, slotIndex, slot ->
        block(slotIndex, slot)
        0
    }

inline fun List<Either<String, Slot>>.annotateSlots(block: AnnotatedString.Builder.(Slot) -> Unit): AnnotatedString =
    annotateSlotsIndexed { _, _, it ->
        block(it)
        0
    }

@Composable
fun <R : Any> AnnotatedString.Builder.withSlotStyle(
    selected: Boolean,
    block: AnnotatedString.Builder.() -> R
): R {
    val style = if (selected) {
        SpanStyle(
            background = MaterialTheme.colorScheme.secondaryContainer,
            fontWeight = FontWeight.Medium
        )
    } else {
        SpanStyle(
            background = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
        )
    }

    return withStyle(style) {
        block()
    }
}
