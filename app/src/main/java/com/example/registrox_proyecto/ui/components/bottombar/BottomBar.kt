package com.example.registrox_proyecto.ui.components.bottombar

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.registrox_proyecto.data.model.Role
import com.example.registrox_proyecto.navigation.BottomNavItem
import com.example.registrox_proyecto.navigation.Routes
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun BottomBar(
    navController: NavHostController,
    items: List<BottomNavItem>,
    userRole: Role? = null
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar (containerColor = MaterialTheme.colorScheme.primary){
        items.forEach { item ->
            if (item.route == Routes.TRABAJADOR && userRole != Role.TRABAJADOR){
                return@forEach
            }

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {Text(item.label)},
                selected = currentRoute == item.route,
                onClick = {
                    if(currentRoute != item.route){
                        navController.navigate(item.route){
                            popUpTo(Routes.HOME){saveState = true}
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}