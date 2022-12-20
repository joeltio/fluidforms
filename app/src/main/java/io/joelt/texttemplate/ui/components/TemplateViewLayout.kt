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

class TemplateViewLayoutState(val listState: LazyListState) {
    suspend fun scrollBodyToTopOfBottomBar(relativeOffset: Float, bodyHeight: Int) {
        listState.scrollToItem(0, (bodyHeight - relativeOffset).toInt())
    }
}

@Composable
fun rememberTemplateViewLayoutState() = TemplateViewLayoutState(rememberLazyListState(1))

@Composable
fun TemplateViewLayout(
    state: TemplateViewLayoutState = rememberTemplateViewLayoutState(),
    name: @Composable ColumnScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    body: @Composable ColumnScope.() -> Unit = {},
) {
    Column {
        LazyColumn(
            // This column is reversed so that scrolling items to above the bottom bar can be
            // implemented. Since the layout is reversed, scroll offset starts from the bottom, so
            // it is easier to align things to the bottom
            reverseLayout = true,
            state = state.listState,
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
        ) {
            item {
                ProvideTextStyle(value = Typography.bodyLarge) {
                    body()
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProvideTextStyle(value = Typography.headlineLarge) {
                    name()
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
        Text(modifier = Modifier.weight(1f), text = text)
    }
}
