package com.example.registrox_proyecto.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.registrox_proyecto.data.model.Role
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
    val user by loginViewModel.user.collectAsStateWithLifecycle()

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

        composable(Routes.TRABAJADOR) {
            HomeTrabajadorScreen(
                onBackClick = { navController.navigate(Routes.HOME) },
                carritoViewModel = carritoViewModel
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController, registerViewModel)
        }

        composable(Routes.PROFILE) {
            val current = user
            if (current != null) {
                ProfileScreen(
                    user = current,
                    loginViewModel = loginViewModel,
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }
        }

        composable(Routes.ENTRADAS) {
            EntradasScreen(navController, carritoViewModel)
        }

        composable("${Routes.DETALLE}/{codigoQR}") {
            val codigoQR = it.arguments?.getString("codigoQR") ?: ""
            DetalleEntradaScreen(navController, codigoQR)
        }

        composable(Routes.COMPRAS) {
            ComprasScreen(navController, comprasViewModel)
        }
    }
}

