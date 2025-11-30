package com.example.registrox_proyecto

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.example.registrox_proyecto.ui.theme.RegistroXProyectoTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.registrox_proyecto.data.datastore.AuthDataStore
import com.example.registrox_proyecto.data.datastore.EntradasDataStore
import com.example.registrox_proyecto.data.repository.AuthRepository
import com.example.registrox_proyecto.navigation.BottomNavItem
import com.example.registrox_proyecto.navigation.NavGraph
import com.example.registrox_proyecto.navigation.Routes
import com.example.registrox_proyecto.ui.components.bottombar.BottomBar
import com.example.registrox_proyecto.ui.components.topbar.DefaultTopBar
import com.example.registrox_proyecto.ui.components.topbar.HomeTopBar
import com.example.registrox_proyecto.ui.components.topbar.TrabajadorTopBar
import com.example.registrox_proyecto.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    private val permisosNecesarios: Array<String> by lazy {
        val permisos = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        permisos.toTypedArray()
    }

    private val permisosLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultados ->
            resultados.forEach { (permiso, otorgado) ->
                android.util.Log.d("PERMISOS", "$permiso => $otorgado")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permisosLauncher.launch(permisosNecesarios)

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
            initializer { LoginViewModel(app, authRepository, authDataStore) }
        }
    )

    val registerViewModel: RegisterViewModel = viewModel(
        factory = viewModelFactory {
            initializer { RegisterViewModel(app, authRepository) }
        }
    )

    val carritoViewModel: CarritoViewModel = viewModel(
        factory = viewModelFactory {
            initializer { CarritoViewModel(app, entradasDataStore, authDataStore) }
        }
    )

    val profileViewModel: ProfileViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ProfileViewModel(app) }
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

    val hideTopBar = currentRoute in listOf(
        Routes.LOGIN, Routes.REGISTER, Routes.DETALLE, Routes.OTP
    )

    val hideBottomBar = currentRoute in listOf(
        Routes.LOGIN, Routes.REGISTER, Routes.DETALLE, Routes.OTP
    )

    val bottomItems = listOf(BottomNavItem.Home, BottomNavItem.QR, BottomNavItem.Profile) +
            if (user?.role == com.example.registrox_proyecto.data.model.Role.TRABAJADOR)
                listOf(BottomNavItem.Scan) else emptyList()

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
