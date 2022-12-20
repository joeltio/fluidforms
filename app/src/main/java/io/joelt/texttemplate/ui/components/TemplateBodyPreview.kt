package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.createTemplateExample
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.ui.theme.TextTemplateTheme

private const val SLOT_TAG = "Slot"

@Composable
fun TemplateBodyPreview(
    body: List<Either<String, Slot>>,
    selectedSlotIndex: Int? = null,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    onSlotClick: ((slotIndex: Int) -> Unit)? = null
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
        } else { TextOverflow.Ellipsis }
        if (onSlotClick == null) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = annotatedString,
                style = style,
                maxLines = maxLines,
                overflow = overflow
            )
        } else {
            ClickableText(
                modifier = Modifier.fillMaxWidth(),
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
                overflow = overflow
            )
        }
    }
}

@Preview
@Composable
private fun SlotsPreviewExample() {
    val template = createTemplateExample(
        name = "My Sample Template",
        body = """
            Be kind to your {% text %}{% end %}-footed {% text %}{% end %}
            For a duck may be somebody's {% text %}{% end %},
            Be kind to your {% text %}{% end %} in {% text %}{% end %}
            Where the weather is always {% text %}{% end %}.

            You may think that this is the {% text %}{% end %},
            Well it is.
        """.trimIndent()
    )

    val body by remember { mutableStateOf(template.body) }
    var currentSlot by remember {
        mutableStateOf<Int?>(null)
    }
    TextTemplateTheme {
        Column {
            TemplateBodyPreview(body, currentSlot) {
                currentSlot = it
            }
        }
    }
}
