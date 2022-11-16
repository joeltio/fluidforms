package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.ui.theme.Typography

@Composable
fun EditorLayout(
    name: String,
    onNameChange: (String) -> Unit,
    content: @Composable () -> Unit
) {
    Column {
        PlaceholderTextField(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            value = name,
            textStyle = Typography.headlineLarge,
            singleLine = true,
            placeholder = stringResource(R.string.template_name),
            onValueChange = { onNameChange(it) })
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun EditorLayoutPreviewWithTemplate() {
    val template = genTemplates(1)[0]
    var name by remember { mutableStateOf(template.name) }
    EditorLayout(
        name = name,
        onNameChange = { name = it },
    ) {
        SlotsPreview(slots = template.slots)
    }
}

@Preview(showBackground = true)
@Composable
private fun EditorLayoutPreviewEmpty() {
    var name by remember { mutableStateOf("") }
    EditorLayout(
        name = name,
        onNameChange = { name = it },
    ) {
    }
}
