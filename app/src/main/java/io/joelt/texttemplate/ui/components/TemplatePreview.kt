package io.joelt.texttemplate.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.models.slots.Slot

@Composable
fun TemplatePreview(template: Template) {
    TemplatePreview(name = template.name, slots = template.slots)
}

@Composable
fun TemplatePreview(name: String, slots: List<Either<String, Slot>>) {
    TemplateViewLayout(contentScrollable = true, name = {
        Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }) {
        SlotsPreview(slots = slots)
    }
}

@Preview
@Composable
private fun TemplatePreviewPreview() {
    val template = genTemplates(1)[0]
    TemplatePreview(template)
}
