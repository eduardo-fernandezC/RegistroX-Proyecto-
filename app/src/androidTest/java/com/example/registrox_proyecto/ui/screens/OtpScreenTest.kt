package com.example.registrox_proyecto.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

@Composable
fun OtpScreenTestable(
    otpCodeState: String,
    inputCodeState: String,
    errorTextState: String,
    onInputChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onVerify: () -> Unit
) {

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Text("Codigo de verificacion")

        Spacer(Modifier.height(16.dp))

        OtpInputFieldTestable(
            code = inputCodeState,
            onCodeChange = onInputChange
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSendOtp,
            modifier = Modifier.testTag("btn_enviar")
        ) {
            Text("Enviar codigo")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onVerify,
            modifier = Modifier.testTag("btn_verificar")
        ) {
            Text("VERIFICAR")
        }

        if (errorTextState.isNotEmpty()) {
            Text(
                errorTextState,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("error_msg")
            )
        }
    }
}

@Composable
fun OtpInputFieldTestable(code: String, onCodeChange: (String) -> Unit) {

    val chars = List(4) { index -> code.getOrNull(index)?.toString() ?: "" }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(4) { index ->
            BasicTextFieldTestable(
                value = chars[index],
                index = index,
                originalCode = code,
                onCodeChange = onCodeChange
            )
        }
    }
}

@Composable
fun BasicTextFieldTestable(
    value: String,
    index: Int,
    originalCode: String,
    onCodeChange: (String) -> Unit
) {

    BasicTextField(
        value = value,
        onValueChange = { newChar ->
            if (newChar.length <= 1 && newChar.all { it.isDigit() }) {

                val newCode = buildString {
                    for (i in 0..3) {
                        append(
                            when {
                                i == index -> newChar
                                originalCode.length > i -> originalCode[i]
                                else -> ""
                            }
                        )
                    }
                }
                onCodeChange(newCode)
            }
        },
        modifier = Modifier
            .width(52.dp)
            .height(52.dp)
            .testTag("otp_$index"),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    )
}

// ----------------------------
// 🔹 TESTS
// ----------------------------
class OtpScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun otpScreen_muestraCuatroCampos() {
        rule.setContent {
            OtpScreenTestable(
                otpCodeState = "",
                inputCodeState = "",
                errorTextState = "",
                onInputChange = {},
                onSendOtp = {},
                onVerify = {}
            )
        }

        repeat(4) { index ->
            rule.onNodeWithTag("otp_$index").assertIsDisplayed()
        }
    }

    @Test
    fun otpScreen_clickEnviarEjecutaCallback() {
        var enviado = false

        rule.setContent {
            OtpScreenTestable(
                otpCodeState = "",
                inputCodeState = "",
                errorTextState = "",
                onInputChange = {},
                onSendOtp = { enviado = true },
                onVerify = {}
            )
        }

        rule.onNodeWithTag("btn_enviar").performClick()

        assert(enviado)
    }

    @Test
    fun otpScreen_verificaCodigoIncorrectoMuestraError() {

        rule.setContent {
            OtpScreenTestable(
                otpCodeState = "9999",
                inputCodeState = "1234",
                errorTextState = "Código incorrecto",
                onInputChange = {},
                onSendOtp = {},
                onVerify = {}
            )
        }

        rule.onNodeWithTag("error_msg").assertIsDisplayed()
        rule.onNodeWithText("Código incorrecto").assertIsDisplayed()
    }

    @Test
    fun otpScreen_verificaCodigoCorrectoEjecutaCallback() {
        var verificado = false

        var input = "1234"
        var otpCode = "1234"

        rule.setContent {
            OtpScreenTestable(
                otpCodeState = otpCode,
                inputCodeState = input,
                errorTextState = "",
                onInputChange = { input = it },
                onSendOtp = {},
                onVerify = { verificado = true }
            )
        }

        rule.onNodeWithTag("btn_verificar").performClick()

        assert(verificado)
    }
}
