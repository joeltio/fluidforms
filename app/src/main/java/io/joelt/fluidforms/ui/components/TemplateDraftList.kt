package io.joelt.fluidforms.ui.components

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
import io.joelt.fluidforms.models.Draft
import io.joelt.fluidforms.models.Form

@Composable
private fun <T> FormListLayout(listItems: List<T>?, block: @Composable (T) -> Unit) {
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
fun FormList(forms: List<Form>?, onItemClick: (Form) -> Unit) {
    FormListLayout(forms) {
        FormRow(
            name = it.name,
            body = it.body,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onItemClick(it) })
    }
}

@Composable
fun DraftList(drafts: List<Draft>?, onItemClick: (Draft) -> Unit) {
    FormListLayout(drafts) {
        FormRow(
            name = it.name,
            body = it.body,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onItemClick(it) },
            dateTime = it.lastEditedOn
        )
    }
}
