package com.example.registrox_proyecto.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.registrox_proyecto.ui.screens.*
import com.example.registrox_proyecto.ui.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    carritoViewModel: CarritoViewModel,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    profileViewModel: ProfileViewModel,
    entradasApiViewModel: EntradasApiViewModel,
    comprasViewModel: ComprasViewModel,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController, loginViewModel)
        }

        composable(Routes.OTP) {
            OtpScreen(
                navController = navController,
                viewModel = loginViewModel,
                onOtpVerified = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.OTP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(navController, carritoViewModel)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController, registerViewModel)
        }

        composable(Routes.PROFILE) {
            val user = loginViewModel.user.collectAsStateWithLifecycle().value
            if (user != null) {
                ProfileScreen(
                    user = user,
                    loginViewModel = loginViewModel,
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PROFILE) { inclusive = true }
                    }
                }
            }
        }

        composable(Routes.ENTRADAS) {
            EntradasScreen(navController, carritoViewModel)
        }

        composable("${Routes.DETALLE}/{codigoQR}") { backStackEntry ->
            val codigoQR = backStackEntry.arguments?.getString("codigoQR") ?: ""
            DetalleEntradaScreen(navController, codigoQR)
        }

        composable(Routes.COMPRAS) {
            ComprasScreen(navController, comprasViewModel)
        }
    }
}
