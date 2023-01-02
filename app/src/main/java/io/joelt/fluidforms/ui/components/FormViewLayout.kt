package io.joelt.fluidforms.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.fluidforms.ui.theme.Typography

class FormViewLayoutState(val listState: LazyListState, nameHeightState: MutableState<Int>) {
    var nameHeight by nameHeightState

    suspend fun scrollBodyToTopOfBottomBar(relativeOffset: Float) {
        val visibleHeight = listState.layoutInfo.viewportSize.height
        // Calculation:
        // - Scroll up by name's height
        // - Scroll up by visible height
        // - Scroll down by relative offset
        // - Cap the scroll amount to -nameHeight. The scrolling should never be less than
        //   -nameHeight
        var scrollAmount = (relativeOffset - visibleHeight).toInt()
        if (nameHeight != -1) {
            scrollAmount = maxOf(scrollAmount, -nameHeight)
        }
        listState.animateScrollToItem(1, scrollAmount)
    }
}

@Composable
fun rememberFormViewLayoutState(): FormViewLayoutState {
    val nameHeight = remember { mutableStateOf(-1) }
    val listState = rememberLazyListState()
    return FormViewLayoutState(listState, nameHeight)
}

@Composable
fun FormViewLayout(
    state: FormViewLayoutState = rememberFormViewLayoutState(),
    name: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    body: @Composable () -> Unit = {},
) {
    Column {
        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            item {
                Column(modifier = Modifier.onGloballyPositioned {
                    state.nameHeight = it.size.height
                }) {
                    ProvideTextStyle(value = Typography.headlineLarge) {
                        name()
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
private fun FormViewLayoutPreview() {
    FormViewLayout(name = {
        Text(text = "Form Title")
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
