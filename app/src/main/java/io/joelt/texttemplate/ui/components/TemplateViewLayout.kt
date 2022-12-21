package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.ui.theme.Typography

data class TemplateViewLayoutState(val listState: LazyListState) {
    suspend fun scrollBodyToTopOfBottomBar(relativeOffset: Float) {
        val visibleHeight = listState.layoutInfo.viewportSize.height
        // Calculation:
        // - Scroll up by name's height
        // - Scroll up by visible height
        // - Scroll down by relative offset
        listState.animateScrollToItem(1, (relativeOffset - visibleHeight).toInt())
    }
}

@Composable
fun rememberTemplateViewLayoutState() = TemplateViewLayoutState(rememberLazyListState(0))

@Composable
fun TemplateViewLayout(
    state: TemplateViewLayoutState = rememberTemplateViewLayoutState(),
    name: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    body: @Composable () -> Unit = {},
) {
    Column {
        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .weight(1f)
        ) {
            item {
                ProvideTextStyle(value = Typography.headlineLarge) {
                    name()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                ProvideTextStyle(value = Typography.bodyLarge) {
                    body()
                }
            }
        }

        Box(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
        ) {
            bottomBar()
        }
    }
}

@Preview(showBackground = true)
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
        val text = "hello world! This is the start." + "\n".repeat(30) + "This is the end."
        Text(text = text)
    }
}
