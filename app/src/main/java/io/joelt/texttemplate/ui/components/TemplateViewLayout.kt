package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.ui.theme.Typography

@Composable
fun TemplateViewLayout(
    name: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    body: @Composable () -> Unit = {},
) {
    Column {
        Column(modifier = Modifier.padding(16.dp)) {
            ProvideTextStyle(value = Typography.headlineLarge) {
                name()
            }
            Spacer(modifier = Modifier.height(16.dp))
            ProvideTextStyle(value = Typography.bodyLarge) {
                body()
            }
        }
        bottomBar()
    }
}

@Preview
@Composable
private fun TemplateViewLayoutPreview() {
    TemplateViewLayout(name = {
        Text(text = "Template Title")
    }, bottomBar = {
        BottomAppBar(actions = {}, floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        })
    }) {
        Text(text = "hello world! This is the body.")
    }
}
