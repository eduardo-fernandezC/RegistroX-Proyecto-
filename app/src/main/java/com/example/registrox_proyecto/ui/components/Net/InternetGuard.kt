package com.example.registrox_proyecto.ui.components.Net

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.registrox_proyecto.utils.NetworkUtils

@Composable
fun InternetGuard(content: @Composable () -> Unit) {
    val context = LocalContext.current
    var hasInternet by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        hasInternet = NetworkUtils.isNetworkAvailable(context)
    }

    if (!hasInternet) {
        NoInternetOverlay()
    } else {
        content()
    }
}