package com.example.registrox_proyecto.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

    var otpVerified by rememberSaveable { mutableStateOf(false) }

    val startDestination = when {
        user?.role == Role.TRABAJADOR && !otpVerified -> Routes.OTP
        user == null -> Routes.LOGIN
        else -> Routes.HOME
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(Routes.OTP) {
            OtpScreen(
                navController = navController,
                onOtpVerified = {
                    otpVerified = true
                    navController.navigate(Routes.TRABAJADOR) {
                        popUpTo(Routes.OTP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }

        composable(Routes.HOME) {
            HomeScreen(navController = navController, carritoViewModel = carritoViewModel)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController, viewModel = registerViewModel)
        }

        composable(Routes.ENTRADAS) {
            EntradasScreen(navController = navController, carritoViewModel = carritoViewModel)
        }

        composable(Routes.PROFILE) {
            val currentUser = user
            if (currentUser != null) {
                ProfileScreen(
                    user = currentUser,
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

        composable(Routes.TRABAJADOR) {
            HomeTrabajadorScreen(
                onBackClick = { navController.navigate(Routes.HOME) },
                carritoViewModel = carritoViewModel
            )
        }

        composable("${Routes.DETALLE}/{codigoQR}") { backStackEntry ->
            val codigoQR = backStackEntry.arguments?.getString("codigoQR") ?: ""
            DetalleEntradaScreen(navController = navController, codigoQR = codigoQR)
        }

        composable(Routes.COMPRAS) {
            ComprasScreen(navController = navController, viewModel = comprasViewModel)
        }
    }
}
