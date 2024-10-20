package com.mightysana.onebadminton.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.navigateAndPopUp
import com.mightysana.onebadminton.screens.league.LEADERBOARD
import com.mightysana.onebadminton.screens.league.MATCHES
import com.mightysana.onebadminton.screens.league.PLAYER
import com.mightysana.onebadminton.screens.league.TABS

@Composable
fun BottomNavBar(
    selectedTab: Int,
    navController: NavHostController,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Filled.DateRange else Icons.Outlined.DateRange,
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.matches)) },
            selected = selectedTab == 0,
            onClick = {
                val prevTab = TABS[selectedTab]
                if(selectedTab != 0) {
                    onTabSelected(0)
                    navController.navigateAndPopUp(MATCHES, prevTab)
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(if(selectedTab == 1) Icons.AutoMirrored.Filled.List else Icons.AutoMirrored.Outlined.List, contentDescription = null) },
            label = { Text(stringResource(R.string.leaderbord)) },
            selected = selectedTab == 1,
            onClick = {
                val prevTab = TABS[selectedTab]
                if(selectedTab != 1) {
                    onTabSelected(1)
                    navController.navigateAndPopUp(LEADERBOARD, prevTab)
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(if(selectedTab == 2) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = null) },
            label = { Text(stringResource(R.string.player)) },
            selected = selectedTab == 2,
            onClick = {
                val prevTab = TABS[selectedTab]
                if(selectedTab != 2) {
                    onTabSelected(2)
                    navController.navigateAndPopUp(PLAYER, prevTab)
                }
            }
        )
    }
}
