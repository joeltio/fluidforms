package io.joelt.texttemplate.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.joelt.texttemplate.R

private data class NavItem(
    val name: String,
    val icon: Painter,
    val route: String
)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem(
            stringResource(id = R.string.navbar_templates),
            painterResource(id = R.drawable.ic_baseline_description_24),
            "templates"
        ),
        NavItem(
            stringResource(id = R.string.navbar_drafts),
            painterResource(id = R.drawable.ic_baseline_drafts_24),
            "drafts"
        ),
        NavItem(
            stringResource(id = R.string.navbar_archived),
            painterResource(id = R.drawable.ic_baseline_inventory_24),
            "archived"
        )
    )

    NavigationBar(

    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.name
                    )
                },
                label = { Text(text = item.name) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}