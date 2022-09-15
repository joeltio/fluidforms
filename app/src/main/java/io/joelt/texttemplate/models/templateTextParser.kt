package io.joelt.texttemplate.models

import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.models.slots.createSlot

private val regex = Regex("\\{%\\s*(\\S*)\\s*%}")
private tailrec fun parse(text: String, currentTag: String?, acc: MutableList<Either<String, Slot>>): List<Either<String, Slot>> {
    // Default case
    if (text == "") {
        return acc
    }

    val match = regex.find(text)

    // No tags found
    if (match == null) {
        acc.add(Either.Left(text))
        return acc
    }

    val matchRange = match.groups[0]!!.range
    val textBeforeTag = text.substring(0, matchRange.first)
    val tag = match.groupValues[1]
    val textAfterTag = text.substring(matchRange.last + 1)

    // The tag found is an end tag
    if (tag == "end") {
        acc.add(Either.Right(createSlot(currentTag!!, textBeforeTag)))
        return parse(textAfterTag, null, acc)
    }

    // Tag found is a start tag
    if (matchRange.first != 0) {
        acc.add(Either.Left(textBeforeTag))
    }
    return parse(textAfterTag, tag, acc)
}

fun String.toTemplateSlot(): List<Either<String, Slot>> = parse(this, null, mutableListOf())
