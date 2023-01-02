package io.joelt.fluidforms.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.fluidforms.models.Either
import io.joelt.fluidforms.models.createFormExample
import io.joelt.fluidforms.models.slots.Slot
import io.joelt.fluidforms.ui.theme.FluidFormsTheme

private const val SLOT_TAG = "Slot"

@Composable
fun FormBodyPreview(
    modifier: Modifier = Modifier,
    body: List<Either<String, Slot>>,
    selectedSlotIndex: Int? = null,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    onScrollOffsetCalculated: (offsets: Map<Int, Float>) -> Unit = {},
    onSlotClick: ((slotIndex: Int) -> Unit)? = null,
) {
    val annotatedString = body.annotateSlotsIndexed { index, it ->
        pushStringAnnotation(SLOT_TAG, index.toString())
        withSlotStyle(selectedSlotIndex == index) {
            append(it.displayDefault())
        }
        pop()
    }

    Column {
        // This is needed as ClickableText will not respond to parent clicks but
        // Text will. For example, when this ClickableText is used in a row
        // item, clicks events on the ClickableText will not propagate to the
        // parent
        val overflow = if (maxLines == Int.MAX_VALUE) {
            TextOverflow.Clip
        } else {
            TextOverflow.Ellipsis
        }
        if (onSlotClick == null) {
            Text(
                modifier = modifier
                    .fillMaxWidth(),
                text = annotatedString,
                style = style,
                maxLines = maxLines,
                overflow = overflow,
            )
        } else {
            ClickableText(
                modifier = modifier.fillMaxWidth(),
                text = annotatedString,
                style = style.merge(TextStyle(color = LocalContentColor.current)),
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = SLOT_TAG, offset, offset
                    ).firstOrNull()?.let { annotation ->
                        onSlotClick(annotation.item.toInt())
                    }
                },
                maxLines = maxLines,
                overflow = overflow,
                onTextLayout = { layoutResult ->
                    val annotations = annotatedString.getStringAnnotations(
                        tag = SLOT_TAG,
                        0,
                        annotatedString.length
                    )
                    val offsets = mutableMapOf<Int, Float>()

                    annotations.forEach { range ->
                        val lineIndex = layoutResult.getLineForOffset(range.end)
                        offsets[range.item.toInt()] = layoutResult.getLineBottom(lineIndex)
                    }
                    onScrollOffsetCalculated(offsets)
                }
            )
        }
    }
}

@Preview
@Composable
private fun SlotsPreviewExample() {
    val form = createFormExample(
        name = "My Sample Form",
        body = """
            Be kind to your {% text %}{% end %}-footed {% text %}{% end %}
            For a duck may be somebody's {% text %}{% end %},
            Be kind to your {% text %}{% end %} in {% text %}{% end %}
            Where the weather is always {% text %}{% end %}.

            You may think that this is the {% text %}{% end %},
            Well it is.
        """.trimIndent()
    )

    val body by remember { mutableStateOf(form.body) }
    var currentSlot by remember {
        mutableStateOf<Int?>(null)
    }
    FluidFormsTheme {
        Column {
            FormBodyPreview(body = body, selectedSlotIndex = currentSlot) {
                currentSlot = it
            }
        }
    }
}
