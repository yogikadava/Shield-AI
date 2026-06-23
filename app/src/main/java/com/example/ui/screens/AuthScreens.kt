package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ShieldViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthOverlayPortal(
    viewModel: ShieldViewModel,
    activeScreen: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CyberDarkBg, CyberDeepNavy)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Space telemetry background blur element
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopEnd)
                .offset(y = (-60).dp, x = 60.dp)
                .background(CyberBluePrimary.copy(alpha = 0.12f), RoundedCornerShape(100.dp))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            when (activeScreen) {
                "LOGIN" -> LoginScreenNative(viewModel)
                "REGISTER" -> RegisterScreenNative(viewModel)
                "FORGOT" -> ForgotPasswordScreenNative(viewModel)
                "RESET" -> ResetPasswordScreenNative(viewModel)
            }
        }
    }
}

@Composable
fun LoginScreenNative(viewModel: ShieldViewModel) {
    val email by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AdminPanelSettings,
            contentDescription = "Shield Admin Portal",
            tint = CyberBlueSecondary,
            modifier = Modifier
                .size(72.dp)
                .background(CyberBluePrimary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "SHIELD SECURE LOGIN",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )

        Text(
            text = "Authenticate local physical sandbox node",
            color = CyberTextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Email Input
            OutlinedTextField(
                value = email.authEmailInput,
                onValueChange = { viewModel.onEmailInputChanged(it) },
                label = { Text("Agent Email", color = CyberTextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace) },
                placeholder = { Text("agent.doe@shield.ai", fontSize = 12.sp, color = CyberTextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("username_input"),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyberAccentCyan) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberAccentCyan,
                    unfocusedBorderColor = CyberCardBg,
                    focusedContainerColor = CyberCardBg,
                    unfocusedContainerColor = CyberCardBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Password Input
            OutlinedTextField(
                value = email.authPasswordInput,
                onValueChange = { viewModel.onPasswordInputChanged(it) },
                label = { Text("Access Keycode", color = CyberTextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().testTag("password_input"),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyberAccentCyan) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Visibility",
                            tint = CyberTextSecondary
                        )
                    }
                },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberAccentCyan,
                    unfocusedBorderColor = CyberCardBg,
                    focusedContainerColor = CyberCardBg,
                    unfocusedContainerColor = CyberCardBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { viewModel.setAuthScreen("FORGOT") }) {
                    Text(
                        text = "Forgot Passcode?",
                        color = CyberBlueSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = { viewModel.performLogin() },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_button")
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AUTHORIZE CONSOLE HANDSHAKE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New agent deployment?", color = CyberTextSecondary, fontSize = 12.sp)
                TextButton(onClick = { viewModel.setAuthScreen("REGISTER") }) {
                    Text(
                        text = "Register Node",
                        color = CyberAccentCyan,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(
                onClick = { viewModel.setAuthScreen("NONE") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "BYPASS AND SKIP TO BASELINE SHIELD",
                    color = CyberTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RegisterScreenNative(viewModel: ShieldViewModel) {
    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SensorOccupied,
            contentDescription = "Register Station",
            tint = CyberAccentCyan,
            modifier = Modifier
                .size(72.dp)
                .background(CyberBluePrimary.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "REGISTER THREAT NODE",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )

        Text(
            text = "Deploy custom protective credentials",
            color = CyberTextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Username Input
            OutlinedTextField(
                value = state.authUsernameInput,
                onValueChange = { viewModel.onUsernameInputChanged(it) },
                label = { Text("Agent Name", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                placeholder = { Text("John Doe", fontSize = 12.sp, color = CyberTextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyberAccentCyan) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberAccentCyan,
                    unfocusedBorderColor = CyberCardBg,
                    focusedContainerColor = CyberCardBg,
                    unfocusedContainerColor = CyberCardBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Email Input
            OutlinedTextField(
                value = state.authEmailInput,
                onValueChange = { viewModel.onEmailInputChanged(it) },
                label = { Text("Intel Email", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                placeholder = { Text("agent@shield.ai", fontSize = 12.sp, color = CyberTextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyberAccentCyan) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberAccentCyan,
                    unfocusedBorderColor = CyberCardBg,
                    focusedContainerColor = CyberCardBg,
                    unfocusedContainerColor = CyberCardBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Password Input
            OutlinedTextField(
                value = state.authPasswordInput,
                onValueChange = { viewModel.onPasswordInputChanged(it) },
                label = { Text("Access Passcode", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyberAccentCyan) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberAccentCyan,
                    unfocusedBorderColor = CyberCardBg,
                    focusedContainerColor = CyberCardBg,
                    unfocusedContainerColor = CyberCardBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Confirm Password Input
            OutlinedTextField(
                value = state.authConfirmPasswordInput,
                onValueChange = { viewModel.onConfirmPasswordInputChanged(it) },
                label = { Text("Confirm Passcode", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.GppGood, contentDescription = null, tint = CyberAccentCyan) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberAccentCyan,
                    unfocusedBorderColor = CyberCardBg,
                    focusedContainerColor = CyberCardBg,
                    unfocusedContainerColor = CyberCardBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel.performRegistration() },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.AppRegistration, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DEPLOY SECURED NODE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already registered?", color = CyberTextSecondary, fontSize = 12.sp)
                TextButton(onClick = { viewModel.setAuthScreen("LOGIN") }) {
                    Text(
                        text = "Access Console",
                        color = CyberAccentCyan,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordScreenNative(viewModel: ShieldViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LockReset,
            contentDescription = "Forgot Password",
            tint = CyberWarning,
            modifier = Modifier
                .size(72.dp)
                .background(CyberWarning.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "DECRYPT PASSCODE",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )

        Text(
            text = "Receive localized emergency bypass credential token",
            color = CyberTextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = state.authEmailInput,
            onValueChange = { viewModel.onEmailInputChanged(it) },
            label = { Text("intel.doe@shield.ai", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyberWarning) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberWarning,
                unfocusedBorderColor = CyberCardBg,
                focusedContainerColor = CyberCardBg,
                unfocusedContainerColor = CyberCardBg
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = { viewModel.performForgotPassword() },
            colors = ButtonDefaults.buttonColors(containerColor = CyberWarning),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = CyberDarkBg)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "REQUEST DECRYPTION DISPATCH",
                color = CyberDarkBg,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { viewModel.setAuthScreen("LOGIN") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Abort decryption, return to auth lock",
                color = CyberTextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun ResetPasswordScreenNative(viewModel: ShieldViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ChangeCircle,
            contentDescription = "Reset token",
            tint = CyberGreenSafe,
            modifier = Modifier
                .size(72.dp)
                .background(CyberGreenSafe.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "REKEY ACCESS SYSTEM",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Generate new local hardware secure signatures",
            color = CyberTextSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = state.authPasswordInput,
            onValueChange = { viewModel.onPasswordInputChanged(it) },
            label = { Text("Generate Cryptographic Keycode", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyberGreenSafe) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberGreenSafe,
                unfocusedBorderColor = CyberCardBg,
                focusedContainerColor = CyberCardBg,
                unfocusedContainerColor = CyberCardBg
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = { viewModel.setAuthScreen("LOGIN") },
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreenSafe),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.DoneOutline, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "COMMIT SECURE SIGNATURES",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
