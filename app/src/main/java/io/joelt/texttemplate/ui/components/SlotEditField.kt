package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.models.slots.Slot



@Composable
fun SwitchSlotButton(isLeft: Boolean, onClick: (() -> Unit)?) {
    var icon = Icons.Filled.KeyboardArrowLeft
    var description = "previous slot"
    if (!isLeft) {
        icon = Icons.Filled.KeyboardArrowRight
        description = "next slot"
    }

    var tint = Color.Black
    if (onClick == null) {
        tint = Color.Gray
    }

    IconButton(onClick = onClick ?: {}) {
        Icon(icon, description, tint = tint)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotEditField(
    slot: Slot,
    onLeft: (() -> Unit)? = null,
    onRight: (() -> Unit)? = null,
    onChange: (newSlot: Slot) -> Unit
) {
    Row {
        SwitchSlotButton(isLeft = true, onLeft)
        when (slot) {
            is PlainTextSlot -> {
                TextField(value = slot.value, onValueChange = {
                    onChange(slot.copy(value = it))
                })
            }
        }
        SwitchSlotButton(isLeft = false, onRight)
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
