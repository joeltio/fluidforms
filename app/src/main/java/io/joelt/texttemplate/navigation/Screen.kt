package io.joelt.texttemplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController


data class ScreenContent(
    val scaffoldOptions: ScaffoldOptions = ScaffoldOptions(),
    val content: @Composable () -> Unit = {}
)

class ScreenContentBuilder {
    var scaffoldOptions = ScaffoldOptions()
    var composable: @Composable () -> Unit = {}

    fun scaffoldOptions(block: ScaffoldOptions.() -> Unit) {
        block(scaffoldOptions)
    }

    fun content(block: @Composable () -> Unit) {
        composable = block
    }
}

typealias ContentFactory = @Composable (NavBackStackEntry, NavHostController) -> ScreenContent

data class Screen(
    val route: String,
    val arguments: List<NamedNavArgument>,
    val contentFactory: ContentFactory,
)

class ScreenBuilder {
    lateinit var route: String
    var arguments: List<NamedNavArgument> = emptyList()
    var contentFactory: ContentFactory = { _, _ -> ScreenContent() }

    fun contentFactory(block: @Composable ScreenContentBuilder.(NavBackStackEntry, NavHostController) -> ScreenContent) {
        contentFactory = @Composable { backStack, nav ->
            val builder = ScreenContentBuilder()
            block(builder, backStack, nav)
        }
    }
}

fun buildScreen(block: ScreenBuilder.() -> Unit): Screen {
    val builder = ScreenBuilder()
    block(builder)

    return Screen(
        builder.route,
        builder.arguments,
        builder.contentFactory
    )
}

@Composable
fun buildScreenContent(block: @Composable ScreenContentBuilder.() -> Unit): ScreenContent {
    val builder = ScreenContentBuilder()
    block(builder)

    return ScreenContent(builder.scaffoldOptions, builder.composable)
}
