package io.joelt.fluidforms.models.slots

@JvmInline
value class EscapedString(val value: String)

const val ESCAPE_CHAR = '\\'
const val ESCAPE_CHAR_REGEX = "\\\\"
val charactersToEscape =
    listOf(
        '!',
        '"',
        '#',
        '$',
        '%',
        '&',
        '\'',
        '(',
        ')',
        '*',
        '+',
        ',',
        '-',
        '.',
        '/',
        ':',
        ';',
        '<',
        '=',
        '>',
        '?',
        '@',
        '[',
        '\\',
        ']',
        '^',
        '_',
        '`',
        '{',
        '|',
        '}',
        '~'
    )

fun escapeText(text: String): EscapedString {
    val builder = StringBuilder(text.length)

    for (c in text) {
        if (c in charactersToEscape) {
            builder.append(ESCAPE_CHAR)
        }
        builder.append(c)
    }

    return EscapedString(builder.toString())
}

fun unescapeText(escapedText: EscapedString): String {
    val builder = StringBuilder(escapedText.value.length)

    var wasEscape = false
    for (c in escapedText.value) {
        if (wasEscape) {
            wasEscape = false
            if (c !in charactersToEscape) {
                // If the character cannot have been escaped, the escape
                // character was used coincidentally
                builder.append(ESCAPE_CHAR)
            }
            builder.append(c)
            continue
        }

        if (c == ESCAPE_CHAR) {
            wasEscape = true
            continue
        }

        builder.append(c)
    }

    // If the last character is the escape character, just append it
    if (wasEscape) {
        builder.append(ESCAPE_CHAR)
    }

    return builder.toString()
}
