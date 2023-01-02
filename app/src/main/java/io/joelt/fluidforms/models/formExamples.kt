package io.joelt.fluidforms.models

import io.joelt.fluidforms.models.slots.EscapedString
import io.joelt.fluidforms.models.slots.deserializeFormBody
import java.time.LocalDateTime

fun createFormExample(
    id: Long = 0,
    createdOn: LocalDateTime = LocalDateTime.now(),
    name: String,
    body: String,
) = Form(
    id,
    createdOn,
    name,
    deserializeFormBody(EscapedString(body))
)

private val forms = listOf(
    createFormExample(
        0,
        LocalDateTime.of(2022, 2, 23, 18, 12, 46, 0),
        "Server Maintenance",
        "Hi all, the server is going to be down from {% text | label = \"start\" %}{% end %} until {% text | label = \"end\" %}{% end %}. Sorry for the inconvenience caused."
    ),
    createFormExample(
        0,
        LocalDateTime.of(2022, 4, 9, 8, 30, 0, 0),
        "Calling in Sick",
        "Hey boss, I'm feeling quite unwell this morning, I think I'm down with {% text | label = \"sickness\" %}{% end %}. I'm going to see a doctor soon and will be back as soon as I'm well."
    ),
    createFormExample(
        0,
        LocalDateTime.of(2022, 4, 23, 18, 12, 46, 0),
        "Swimming Practice",
        "Hey swimmers, this week I would like you to practice {% text | label = \"strokes\" %}{% end %}. We will be doing {% text | label = \"X\"} laps."
    ),
    createFormExample(
        0,
        LocalDateTime.of(2022, 6, 1, 8, 57, 46, 0),
        "Work Weekly Meeting",
        "*{% text | label = \"Date\" %}{% end %} Weekly Meeting*\n" +
                "Scribe: {% text | label = \"Name\" %}{% end %}\n" +
                "Facilitator: {% text | label = \"Name\" %}{% end %}\n" +
                "Location: {% text %}{% end %}\n" +
                "Time: {% text %}{% end %}\n" +
                "Remember: Being on time is respecting everybody's time!"
    ),
    createFormExample(
        0,
        LocalDateTime.of(2022, 4, 23, 18, 12, 46, 0),
        "Weekly Forecast",
        "*{% text | label = \"Date\" %}{% end %} Weather Forecast*\n" +
                "Mon: {% text | label = \"Weather\" }{% end %}\n" +
                "Tue: {% text | label = \"Weather\" }{% end %}\n" +
                "Wed: {% text | label = \"Weather\" }{% end %}\n" +
                "Thu: {% text | label = \"Weather\" }{% end %}\n" +
                "Fri: {% text | label = \"Weather\" }{% end %}\n" +
                "Sat: {% text | label = \"Weather\" }{% end %}\n" +
                "Sun: {% text | label = \"Weather\" }{% end %}"
    ),
)

fun genForms(number: Int): List<Form> {
    if (number <= forms.size) {
        return forms.subList(0, number)
    }

    val randomList = mutableListOf<Form>()
    for (i in 0 until number) {
        randomList.add(forms.random())
    }
    return randomList
}
