package io.joelt.texttemplate.models.slots

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.joelt.texttemplate.R

val plainTextSlotInfo = object : SlotInfo {
    override val serializeTag: EscapedString
        get() = EscapedString("text")

    override fun deserialize(
        serializedLabel: EscapedString,
        serializedModifiers: Map<EscapedString, EscapedString>,
        serializedValue: EscapedString
    ): Slot = PlainTextSlot(unescapeText(serializedLabel), serializedValue)
}

data class PlainTextSlot(override val displayLabel: String, val serializedValue: EscapedString) : Slot() {
    override val info: SlotInfo
        get() = plainTextSlotInfo

    override fun serializeModifiers(): Map<EscapedString, EscapedString> = mapOf()

    override fun serializeValue(): EscapedString = serializedValue

    private val lazyDisplayValue by lazy { unescapeText(serializedValue) }
    override fun displayValue(): String = lazyDisplayValue

    @Composable
    override fun displaySlotType() = stringResource(R.string.plain_text_slot_type_name)

    override fun makeCopy(displayLabel: String) = copy(displayLabel = displayLabel)
}
