package io.joelt.texttemplate.models

import io.joelt.texttemplate.models.slots.*
import java.lang.StringBuilder

private tailrec fun parse(text: String, currentTag: String?, acc: MutableList<Either<String, Slot>>): List<Either<String, Slot>> {
    // Default case
    if (text == "") {
        return acc
    }

    val (textBeforeTag, tag, textAfterTag) = findSlotTag(text)
    // No tags currently, add the string before the next start tag
    if (currentTag == null && textBeforeTag != "") {
        acc.add(Either.Left(textBeforeTag))
    }

    // Tag found is a start tag
    if (tag != END_TAG) {
        return parse(textAfterTag, tag, acc)
    }

    // The tag found is an end tag
    acc.add(Either.Right(createSlot(currentTag!!, textBeforeTag)))
    return parse(textAfterTag, null, acc)
}

fun String.toTemplateSlot(): List<Either<String, Slot>> = parse(this, null, mutableListOf())

fun serializeTemplate(template: List<Either<String, Slot>>): String {
    val sb = StringBuilder()
    for (item in template) {
        when (item) {
            is Either.Left -> sb.append(item.value)
            is Either.Right -> sb.append(createSlotString(item.value))
        }
    }

    return sb.toString()
}