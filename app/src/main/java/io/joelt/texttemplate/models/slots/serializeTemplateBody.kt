package io.joelt.texttemplate.models.slots

import io.joelt.texttemplate.models.Either
import java.lang.StringBuilder

/**
 * (?<!\\)\{%\s*(.*?)\s*(?<!\\)%\}
 * (?<!\\)\{% - There is no "\" before the "{%"
 * \s* - Trim any leading whitespace at start of tag
 * (.*?) - Match anything after the first character conservatively
 * \s* - Trim any trailing whitespace at the end of tag
 * (?<!\\)%\} - There is no "\" before the "%}
 */
private val regex = Regex("(?<!$ESCAPE_CHAR_REGEX)\\{%\\s*(.*?)\\s*(?<!$ESCAPE_CHAR_REGEX)%\\}")
private fun findFirstTag(text: EscapedString): Triple<EscapedString, String?, EscapedString> {
    val match = regex.find(text.value) ?: return Triple(text, null, EscapedString(""))

    val matchRange = match.groups[0]!!.range
    val textBeforeTag = text.value.substring(0, matchRange.first)
    val tag = match.groupValues[1]
    val textAfterTag = text.value.substring(matchRange.last + 1)

    return Triple(EscapedString(textBeforeTag), tag, EscapedString(textAfterTag))
}

private tailrec fun tailRecDeserializeTemplateBody(text: EscapedString, currentTag: String?, acc: MutableList<Either<String, Slot>>): List<Either<String, Slot>> {
    // Default case
    if (text.value == "") {
        return acc
    }

    val (textBeforeTag, tag, textAfterTag) = findFirstTag(text)
    // No tags currently, add the string before the next start tag
    if (currentTag == null && textBeforeTag.value != "") {
        acc.add(Either.Left(unescapeText(textBeforeTag)))
    }

    if (currentTag != null && tag == null) {
        // Missing end tag
        throw DeserializeException("tag was not closed for tag $currentTag")
    }

    // Tag found is a start tag
    if (tag != END_TAG) {
        return tailRecDeserializeTemplateBody(textAfterTag, tag, acc)
    }

    // The tag found is an end tag
    acc.add(Either.Right(deserializeSlot(currentTag!!, textBeforeTag)))
    return tailRecDeserializeTemplateBody(textAfterTag, null, acc)
}

fun deserializeTemplateBody(serializedBody: EscapedString): List<Either<String, Slot>> =
    tailRecDeserializeTemplateBody(serializedBody, null, mutableListOf())

fun serializeTemplateBody(templateBody: List<Either<String, Slot>>): String {
    val sb = StringBuilder()
    for (item in templateBody) {
        when (item) {
            is Either.Left -> sb.append(escapeText(item.value).value)
            is Either.Right -> sb.append(serializeSlot(item.value))
        }
    }

    return sb.toString()
}
