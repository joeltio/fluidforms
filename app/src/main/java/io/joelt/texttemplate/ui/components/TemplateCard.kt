package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import io.joelt.texttemplate.ui.theme.Typography
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateCard(
    template: Template,
    dateTime: LocalDateTime? = null,
    onClick: () -> Unit = {}
) {
    ElevatedCard(modifier = Modifier.padding(4.dp), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = template.name,
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            SlotsPreview(
                slots = template.slots,
                style = Typography.bodyMedium,
                maxLines = 3
            )

            dateTime?.let {
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = dateTime.format(dateTimeFormat),
                    style = Typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateCardExample() {
    val template = Template(
        0,
        "My Template",
        """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris
            {% text | label="Name" %}nisi ut aliquip ex{% end %} ea commodo
            consequat. Duis aute irure dolor in reprehenderit in voluptate velit
            esse cillum dolore eu
            {% text | label="Location" %}fugiat nulla pariatur.{% end %}
        """.trimIndent().replace("\n", " ")
    )

    TextTemplateTheme {
        Column(modifier = Modifier.padding(10.dp)) {
            TemplateCard(template = template) {}
            TemplateCard(template = template, dateTime = LocalDateTime.now()) {}
        }
    }
}
