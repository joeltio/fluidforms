package io.joelt.fluidforms.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import io.joelt.fluidforms.R
import io.joelt.fluidforms.navigation.Route
import io.joelt.fluidforms.navigation.atRoute
import io.joelt.fluidforms.navigation.navigateClearStack
import io.joelt.fluidforms.ui.screens.archived.archived
import io.joelt.fluidforms.ui.screens.drafts.drafts
import io.joelt.fluidforms.ui.screens.forms.forms

private data class NavItem(
    val name: String,
    val icon: Painter,
    val route: String
)

@Composable
fun BottomNavBar(nav: NavHostController) {
    val items = listOf(
        NavItem(
            stringResource(id = R.string.home_nav_forms),
            painterResource(id = R.drawable.ic_baseline_description_24),
            Route.forms
        ),
        NavItem(
            stringResource(id = R.string.home_nav_drafts),
            painterResource(id = R.drawable.ic_baseline_drafts_24),
            Route.drafts
        ),
        NavItem(
            stringResource(id = R.string.home_nav_archived),
            painterResource(id = R.drawable.ic_baseline_inventory_24),
            Route.archived
        )
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.name
                    )
                },
                label = { Text(text = item.name) },
                selected = nav.atRoute(item.route),
                onClick = {
                    nav.navigateClearStack(item.route)
                }
            )
        }
    }
}
