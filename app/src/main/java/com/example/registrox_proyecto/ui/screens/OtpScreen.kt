package com.example.registrox_proyecto.ui.screens

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.registrox_proyecto.ui.components.Net.InternetGuard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.registrox_proyecto.navigation.Routes
import com.example.registrox_proyecto.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    navController: NavHostController,
    viewModel: LoginViewModel,
    onOtpVerified: () -> Unit
) {

    val context = LocalContext.current

    var otpCode by rememberSaveable { mutableStateOf("") }
    var inputCode by rememberSaveable { mutableStateOf("") }
    var errorText by rememberSaveable { mutableStateOf("") }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) sendOtpNotification(context, otpCode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Codigo de verificacion") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.OTP) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver atras")
                    }
                }
            )
        }
    ) { padding ->

        InternetGuard {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Codigo de verificacion", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text("Digite el codigo", fontSize = 14.sp)
                Spacer(Modifier.height(32.dp))

                OtpInputField(code = inputCode) {
                    if (it.length <= 4) inputCode = it
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = {
                    otpCode = generateOtp()
                    inputCode = ""
                    errorText = ""

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val granted = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!granted) {
                            notificationPermissionLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        } else sendOtpNotification(context, otpCode)

                    } else sendOtpNotification(context, otpCode)

                }) { Text("Enviar codigo") }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (inputCode == otpCode && otpCode.isNotEmpty())
                            onOtpVerified()
                        else
                            errorText = "Codigo incorrecto"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("VERIFICAR") }

                if (errorText.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(errorText, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun OtpInputField(code: String, onCodeChange: (String) -> Unit) {
    val focusRequesters = List(4) { FocusRequester() }
    val chars = List(4) { index -> code.getOrNull(index)?.toString() ?: "" }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(4) { index ->
            BasicTextField(
                value = chars[index],
                onValueChange = { value ->
                    if (value.length <= 1 && value.all { it.isDigit() }) {
                        val newCode = buildString {
                            for (i in 0..3) {
                                append(
                                    when {
                                        i == index -> value
                                        code.length > i -> code[i]
                                        else -> ""
                                    }
                                )
                            }
                        }
                        onCodeChange(newCode)
                        if (value.isNotEmpty() && index < 3) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
                    .focusRequester(focusRequesters[index])
                    .focusable(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        tonalElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { innerTextField() }
                    }
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}

fun generateOtp(): String = (1000..9999).random().toString()

fun sendOtpNotification(context: Context, code: String) {
    val channelId = "otp_channel"
    val manager = NotificationManagerCompat.from(context)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        manager.createNotificationChannel(
            NotificationChannel(
                channelId,
                "OTP Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
        .setContentTitle("OTP Code")
        .setContentText("Tu codigo es: $code")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    val granted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    if (granted) manager.notify(1, notification)
}
