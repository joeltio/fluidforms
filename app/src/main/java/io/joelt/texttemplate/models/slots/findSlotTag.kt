package io.joelt.texttemplate.models.slots

private val regex = Regex("\\{%\\s*(.*?)\\s*%\\}")

fun findSlotTag(text: String): Triple<String, String?, String> {
    val match = regex.find(text) ?: return Triple(text, null, "")

    val matchRange = match.groups[0]!!.range
    val textBeforeTag = text.substring(0, matchRange.first)
    val tag = match.groupValues[1]
    val textAfterTag = text.substring(matchRange.last + 1)

    return Triple(textBeforeTag, tag, textAfterTag)
}