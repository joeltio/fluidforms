package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.models.Template

@Composable
private fun <T> TemplateListLayout(listItems: List<T>?, block: @Composable (T) -> Unit) {
    if (listItems == null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(listItems.size) {
            block(listItems[it])
            Divider()
        }
    }
}

@Composable
fun TemplateList(templates: List<Template>?, onItemClick: (Template) -> Unit) {
    TemplateListLayout(templates) {
        TemplateRow(
            name = it.name,
            slots = it.slots,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onItemClick(it) })
    }
}

@Composable
fun DraftList(drafts: List<Draft>?, onItemClick: (Draft) -> Unit) {
    TemplateListLayout(drafts) {
        TemplateRow(
            name = it.name,
            slots = it.slots,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onItemClick(it) },
            dateTime = it.lastEditedOn
        )
    }
}
