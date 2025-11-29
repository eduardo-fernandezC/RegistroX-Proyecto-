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

@Composable
fun OtpScreen(
    navController: NavHostController,
    onOtpVerified: () -> Unit
) {
    val context = LocalContext.current

    var otpCode by rememberSaveable { mutableStateOf(generateOtp()) }
    var inputCode by rememberSaveable { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                sendOtpNotification(context, otpCode)
            }
        }
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Codigo de verificacion", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Digite el codigo",
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            OtpInputField(code = inputCode) {
                if (it.length <= 4) inputCode = it
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = {
                otpCode = generateOtp()
                inputCode = ""
                errorText = ""

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val isGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!isGranted) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        sendOtpNotification(context, otpCode)
                    }
                } else {
                    sendOtpNotification(context, otpCode)
                }
            }) {
                Text("Enviar codigo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (inputCode == otpCode) {
                        errorText = ""
                        onOtpVerified()
                    } else {
                        errorText = "incorrecto, intentelo denuevo"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("VERIFICAR")
            }

            if (errorText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorText, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}


@Composable
fun OtpInputField(code: String, onCodeChange: (String) -> Unit) {
    val focusRequesters = List(4) { FocusRequester() }
    val chars = remember(code) {
        List(4) { index -> code.getOrNull(index)?.toString() ?: "" }
    }

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
                        color = MaterialTheme.colorScheme.background,
                        tonalElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            innerTextField()
                        }
                    }
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}




fun generateOtp(): String {
    return (1000..9999).random().toString()
}

fun sendOtpNotification(context: Context, code: String) {
    val channelId = "otp_channel"
    val channelName = "OTP Notifications"

    val notificationManager = NotificationManagerCompat.from(context)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
        .setContentTitle("OTP Code")
        .setContentText("Tu codigo es: $code")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        == PackageManager.PERMISSION_GRANTED
    ) {
        notificationManager.notify(1, notification)
    }
}
