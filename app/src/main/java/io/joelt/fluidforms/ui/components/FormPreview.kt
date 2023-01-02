package io.joelt.fluidforms.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.fluidforms.models.Either
import io.joelt.fluidforms.models.Form
import io.joelt.fluidforms.models.genForms
import io.joelt.fluidforms.models.slots.Slot

@Composable
fun FormPreview(form: Form) {
    FormPreview(name = form.name, body = form.body)
}

@Composable
fun FormPreview(name: String, body: List<Either<String, Slot>>) {
    FormViewLayout(name = {
        Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }) {
        FormBodyPreview(body = body)
    }
}

@Preview
@Composable
private fun FormPreviewPreview() {
    val form = genForms(1)[0]
    FormPreview(form)
}
