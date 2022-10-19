package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.models.slots.Slot

fun createSlotText(slot: Slot, selected: Boolean = false): AnnotatedString {
    when (slot) {
        is PlainTextSlot -> {
            return buildAnnotatedString {
                val bgColor = if (selected) { Color.Yellow } else { Color.Gray }
                withStyle(SpanStyle(background = bgColor)) {
                    val str = slot.toDisplayString()
                    append(str)
                }
            }
        }
    }

    throw Exception("slot type not recognised")
}

@Composable
fun SlotEditField(slot: Slot, onChange: (newSlot: Slot) -> Unit) {
    when (slot) {
        is PlainTextSlot -> {
            TextField(value = slot.value, onValueChange = {
                onChange(slot.copy(value = it))
            })
        }
    }
}

@Preview
@Composable
private fun SlotEditorPreview() {
    val mySlot = PlainTextSlot("")
    var slot by remember { mutableStateOf<Slot>(mySlot) }
    Column {
        SlotEditField(slot = slot) {
            slot = it
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SlotTextSelected() {
    Text(text = createSlotText(PlainTextSlot("hello world"), true))
}

@Preview(showBackground = true)
@Composable
private fun SlotTextNotSelected() {
    Text(text = createSlotText(PlainTextSlot("hello world"), false))
}