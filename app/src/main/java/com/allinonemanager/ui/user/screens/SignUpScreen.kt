package com.allinonemanager.ui.user.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FigmaYellow3 = Color(0xFFFFC200)
private val TextGray3 = Color(0xFF716F6F)
private val HintGray3 = Color(0xFFC3BFBF)
private val BorderGray3 = Color(0xFFD0CBCB)

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview(showBackground = true)
fun SignUpScreen(
    onSignUpClick: (fullName: String, email: String, mobile: String, password: String, confirmPassword: String) -> Unit = { _, _, _, _, _ -> },
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
            .navigationBarsPadding()
    ) {
        // Yellow header with avatar circle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(FigmaYellow3),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Create account",
                        tint = FigmaYellow3,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Create account",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "sign up to get started",
                    fontSize = 15.sp,
                    color = Color.White
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
                .padding(top = 32.dp, bottom = 32.dp)
        ) {
            // Full Name
            Text("Full Name:", fontSize = 16.sp, color = Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            CompactOutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholderText = "full name",
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = HintGray3) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mobile Number
            Text("Mobile Number:", fontSize = 16.sp, color = Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            CompactOutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                placeholderText = "mobile number",
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = HintGray3) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email
            Text("Email:", fontSize = 16.sp, color = Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            CompactOutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholderText = "email",
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = HintGray3) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password
            Text("Password:", fontSize = 16.sp, color = Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            CompactOutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholderText = "password",
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = HintGray3) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = HintGray3
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm Password
            Text("Confirm Password:", fontSize = 16.sp, color = Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            CompactOutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholderText = "confirm password",
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = HintGray3) },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = HintGray3
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // SIGNUP button
            Button(
                onClick = { onSignUpClick(fullName, email, mobileNumber, password, confirmPassword) },
                colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow3),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally).width(186.dp).height(48.dp)
            ) {
                Text("SIGNUP", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Already have an account? Login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ", color = TextGray3, fontSize = 15.sp)
                Text(
                    text = "Login",
                    color = TextGray3,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp), // Set low custom height here
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black,
            fontSize = 14.sp
        ),
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            placeholder = { Text(placeholderText, color = HintGray3, fontSize = 14.sp) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FigmaYellow3,
                unfocusedBorderColor = BorderGray3
            ),
            // Override M3 paddings to keep everything centered at a low height
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            container = {
                OutlinedTextFieldDefaults.Container(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Black
                    ),
                    shape = RoundedCornerShape(25.dp)
                )
            }
        )
    }
}