package io.joelt.texttemplate.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.genTemplates

@Composable
fun TemplatePreview(template: Template) {
    TemplateViewLayout(contentScrollable = true, name = {
        Text(text = template.name)
    }) {
        SlotsPreview(slots = template.slots)
    }
}

@Preview
@Composable
private fun TemplatePreviewPreview() {
    val template = genTemplates(1)[0]
    TemplatePreview(template)
}
