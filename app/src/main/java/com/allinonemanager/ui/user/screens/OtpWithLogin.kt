package com.allinonemanager.ui.user.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val FigmaYellow2 = Color(0xFFFFC200)
val HintGray2 = Color(0xFFC3BFBF)
val LinkBlue2 = Color(0xFF3AB5EA)

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview(showBackground = true)
fun OtpLoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onSignUpClick: () -> Unit = {},
    onLoginWithPasswordClick: () -> Unit = {},
    onSendOtpClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var mobileNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
            .navigationBarsPadding()
    ) {
        // Yellow header
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
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text("Welcome Back", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Login to continue", fontSize = 16.sp, color = Color.White)
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
            Text("Mobile Number:", fontSize = 15.sp, color = Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                placeholder = { Text("mobile number", color = HintGray2, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Black,
                    unfocusedBorderColor = Black,
                    focusedTextColor = Black,      // text color while typing/focused
                    unfocusedTextColor = Black,
                    cursorColor = Black
                ),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Send OTP link
            Text(
                text = "Send Otp",
                fontSize = 15.sp,
                color = LinkBlue2,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onSendOtpClick() }
            )


            // OTP field
            Text("Otp:", fontSize = 15.sp, color = Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                placeholder = { Text("enter otp", color = HintGray2, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Black,
                    unfocusedBorderColor = Black,
                    focusedTextColor = Black,      // text color while typing/focused
                    unfocusedTextColor = Black,
                    cursorColor = Black
                ),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Login with Password link
            Text(
                text = "Login with Password",
                fontSize = 15.sp,
                color = LinkBlue2,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onLoginWithPasswordClick() }
            )

            Spacer(modifier = Modifier.height(78.dp))

            // LOGIN button
            Button(
                onClick = { onLoginClick(mobileNumber, otp) },
                colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow2),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally).width(146.dp).height(48.dp)
            ) {
                Text("LOGIN", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SIGNUP button
            Button(
                onClick = onSignUpClick,
                colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow2),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally).width(146.dp).height(48.dp)
            ) {
                Text("SIGNUP", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}