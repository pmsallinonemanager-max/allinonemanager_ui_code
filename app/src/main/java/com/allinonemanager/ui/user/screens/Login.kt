package com.allinonemanager.ui.user.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colors matched from the Figma export
val FigmaYellow = Color(0xFFFFC200)
val HintGray = Color(0xFFC3BFBF)
val OtpBlue = Color(0xFF3AB5EA)
val White = Color(0xFFFFFFFF)

val Black = Color(0xFF000000)

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview(showBackground = true)
fun LoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onSignUpClick: () -> Unit = {},
    onOtpLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .imePadding()
            .navigationBarsPadding()
    ) {
        // Yellow header section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(FigmaYellow, shape = RoundedCornerShape(topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Unlock",
                    tint = White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Welcome Back",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Login to continue",
                    fontSize = 16.sp,
                    color = White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .imeNestedScroll()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 24.dp)
        ) {
            // Mobile number field
            Text(
                text = "Mobile Number:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                placeholder = { Text("mobile number", color = HintGray, fontSize = 15.sp) },
                singleLine = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Black,
                    unfocusedBorderColor = Black,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    cursorColor = Black
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Password field
            Text(
                text = "Password:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("password", color = HintGray, fontSize = 15.sp) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = HintGray
                        )
                    }
                },
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Black,
                    unfocusedBorderColor = Black,
                    focusedTextColor = Black,      // text color while typing/focused
                    unfocusedTextColor = Black,
                    cursorColor = Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Login with OTP link
            Text(
                text = "Login with OTP",
                fontSize = 15.sp,
                color = OtpBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onOtpLoginClick() }
            )

            Spacer(modifier = Modifier.height(78.dp))

            // LOGIN button
            Button(
                onClick = { onLoginClick(mobileNumber, password) },
                colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(146.dp)
                    .height(48.dp)
            ) {
                Text("LOGIN", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SIGNUP button
            Button(
                onClick = onSignUpClick,
                colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(146.dp)
                    .height(48.dp)
            ) {
                Text("SIGNUP", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}