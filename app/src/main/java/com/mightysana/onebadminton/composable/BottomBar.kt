package com.mightysana.onebadminton.composable

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mightysana.onebadminton.navigateAndPopUp

data class BottomNavBarItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelResId: Int,
    val route: String
)

@Composable
fun BottomNavBar(
    selectedTab: Int,
    tabs: List<BottomNavBarItem>,
    navController: NavHostController,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar {
        tabs.forEachIndexed { index, it ->
            val selected = index == selectedTab
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) it.selectedIcon else it.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(it.labelResId)) },
                selected = selected,
                onClick = {
                    val prevTab = tabs[selectedTab]
                    if(index != selectedTab) {
                        onTabSelected(index)
                        navController.navigateAndPopUp(it.route, prevTab.route)
                    }
                }
            )
        }
    }
}
