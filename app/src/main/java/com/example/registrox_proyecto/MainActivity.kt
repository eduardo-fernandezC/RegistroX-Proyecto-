package com.example.registrox_proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.registrox_proyecto.data.datastore.AuthDataStore
import com.example.registrox_proyecto.data.datastore.EntradasDataStore
import com.example.registrox_proyecto.data.repository.AuthRepository
import com.example.registrox_proyecto.navigation.BottomNavItem
import com.example.registrox_proyecto.navigation.Routes
import com.example.registrox_proyecto.ui.components.bottombar.BottomBar
import com.example.registrox_proyecto.ui.components.topbar.DefaultTopBar
import com.example.registrox_proyecto.ui.components.topbar.HomeTopBar
import com.example.registrox_proyecto.ui.components.topbar.TrabajadorTopBar
import com.example.registrox_proyecto.ui.screens.*
import com.example.registrox_proyecto.ui.theme.RegistroXProyectoTheme
import com.example.registrox_proyecto.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistroXProyectoTheme {
                RegistroXApp()
            }
        }
    }
}

@Composable
fun RegistroXApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as android.app.Application

    val authRepository = remember { AuthRepository() }
    val authDataStore = remember { AuthDataStore(context) }
    val entradasDataStore = remember { EntradasDataStore(context) }

    val loginViewModel: LoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                LoginViewModel(app, authRepository, authDataStore)
            }
        }
    )

    val registerViewModel: RegisterViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                RegisterViewModel(app, authRepository)
            }
        }
    )

    val carritoViewModel: CarritoViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                CarritoViewModel(app, entradasDataStore, authDataStore)
            }
        }
    )

    val profileViewModel: ProfileViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ProfileViewModel(app)
            }
        }
    )

    val entradasApiViewModel = remember { EntradasApiViewModel() }
    val comprasViewModel = remember { ComprasViewModel() }

    val user by loginViewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(user) {
        carritoViewModel.actualizarUsuario()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideTopBar = currentRoute in listOf(Routes.LOGIN, Routes.REGISTER, Routes.DETALLE)
    val hideBottomBar = currentRoute in listOf(Routes.LOGIN, Routes.REGISTER, Routes.DETALLE)

    val bottomItems = listOf(BottomNavItem.Home, BottomNavItem.QR, BottomNavItem.Profile) +
            if (user?.role == com.example.registrox_proyecto.data.model.Role.TRABAJADOR) {
                listOf(BottomNavItem.Scan)
            } else emptyList()

    Scaffold(
        topBar = {
            if (!hideTopBar && user != null) {
                when (currentRoute) {
                    Routes.HOME -> HomeTopBar(
                        carritoCount = carritoViewModel.carrito.size,
                        onCarritoClick = { navController.navigate(Routes.ENTRADAS) }
                    )
                    Routes.TRABAJADOR -> TrabajadorTopBar {
                        loginViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                    else -> DefaultTopBar(title = "RegistroX")
                }
            }
        },
        bottomBar = {
            if (!hideBottomBar && user != null) {
                BottomBar(
                    navController = navController,
                    items = bottomItems,
                    userRole = user!!.role
                )
            }
        }
    ) { padding ->
        NavGraph(
            navController = navController,
            carritoViewModel = carritoViewModel,
            loginViewModel = loginViewModel,
            registerViewModel = registerViewModel,
            profileViewModel = profileViewModel,
            entradasApiViewModel = entradasApiViewModel,
            comprasViewModel = comprasViewModel,
            modifier = Modifier.padding(padding)
        )
    }
}

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
