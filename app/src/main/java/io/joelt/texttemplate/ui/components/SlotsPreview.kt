package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.nextSlot
import io.joelt.texttemplate.models.prevSlot
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.ui.theme.TextTemplateTheme

@Composable
fun SlotsPreview(
    slots: List<Either<String, Slot>>,
    selectedSlotIndex: Int? = null,
    style: TextStyle = TextStyle.Default,
    maxLines: Int = Int.MAX_VALUE,
    onSlotClick: (slotIndex: Int) -> Unit = {}
) {
    val annotatedString = buildAnnotatedString {
        slots.forEachIndexed { slotIndex, it ->
            when (it) {
                is Either.Left -> {
                    append(it.value)
                }
                is Either.Right -> {
                    // Annotate the string with the slot so that it can be
                    // retrieved later
                    pushStringAnnotation("Slot", slotIndex.toString())
                    append(createSlotText(
                        it.value,
                        selectedSlotIndex == slotIndex
                    ))
                    pop()
                }
            }
        }
    }

    Column {
        ClickableText(
            text = annotatedString,
            style = style,
            onClick = { offset ->
                annotatedString.getStringAnnotations(
                    tag = "Slot", offset, offset
                ).firstOrNull()?.let { annotation ->
                    onSlotClick(annotation.item.toInt())
                }
            },
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun SlotsPreviewExample() {
    val template = Template(
        name = "My Sample Template",
        text = """
            Be kind to your {% text %}{% end %}-footed {% text %}{% end %}
            For a duck may be somebody's {% text %}{% end %},
            Be kind to your {% text %}{% end %} in {% text %}{% end %}
            Where the weather is always {% text %}{% end %}.

            You may think that this is the {% text %}{% end %},
            Well it is.
        """.trimIndent()
    )

    var slots by remember { mutableStateOf(template.slots) }
    var currentSlot by remember {
        mutableStateOf<Int?>(null)
    }
    TextTemplateTheme {
        Column {
            SlotsPreview(slots, currentSlot) {
                currentSlot = it
            }

            currentSlot?.let { slotIndex ->
                val slot = (slots[slotIndex] as Either.Right).value
                val prevSlotIndex = slots.prevSlot(slotIndex)
                val nextSlotIndex = slots.nextSlot(slotIndex)
                var onLeft: (() -> Unit)? = { currentSlot = prevSlotIndex }
                var onRight: (() -> Unit)? = { currentSlot = nextSlotIndex }
                if (prevSlotIndex == -1) {
                    onLeft = null
                }
                if (nextSlotIndex == -1) {
                    onRight = null
                }

                SlotEditField(slot = slot, onLeft, onRight) {
                    val newSlots = slots.toMutableList()
                    newSlots[slotIndex] = Either.Right(it)
                    slots = newSlots.toList()
                }
            }
        }
    }
}