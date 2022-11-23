package io.joelt.texttemplate.models.slots

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.joelt.texttemplate.R

data class PlainTextSlot(var value: String) : Slot() {
    override fun valueToDisplayString(): String = value
    override fun serializeValue() = value
    override fun loadSerializedValue(s: String) {
        value = s
    }

    @Composable
    override fun typeName(): String = stringResource(R.string.plain_text_slot_type_name)

    override fun makeCopy(label: String): Slot {
        return copy().apply { this.label = label }
    }
}