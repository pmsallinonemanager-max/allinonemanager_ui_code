package com.allinonemanager.ui.cc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allinonemanager.ui.user.screens.Black
import com.allinonemanager.ui.user.screens.HintGray
import com.allinonemanager.ui.user.screens.White
import com.allinonemanager.ui.util.TopWaveShape

private val FigmaYellow4 = Color(0xFFFFC200)

@Composable
@Preview(showBackground = true)
fun CreditCardDetails(
    ccNumber: String = "",
    onHomeClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSaveClick: (limit: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var limit by remember { mutableStateOf("250000") }       // saved value
    var limitDraft by remember { mutableStateOf(limit) }     // value while editing
    var isEditing by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ---------- Header (fixed, does not scroll) ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FigmaYellow4, shape = TopWaveShape())
                    .height(140.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 5.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onAccountClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = "Credit Card Manager",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ---------- Scrollable body ----------
            // Scrolls on small screens / landscape, moves above the keyboard,
            // and is centered with a max width on tablets.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "CREDIT CARD DETAILS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FigmaYellow4,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Limit row (centered)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Limit: ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        if (isEditing) {
                            OutlinedTextField(
                                value = limitDraft,
                                onValueChange = { input ->
                                    limitDraft = input.filter { it.isDigit() }.take(9)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Black,
                                    unfocusedBorderColor = Black,
                                    focusedTextColor = Black,
                                    unfocusedTextColor = Black,
                                    cursorColor = Black
                                ),
                                textStyle = TextStyle(fontSize = 15.sp),
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .widthIn(min = 100.dp, max = 160.dp)
                                    .height(50.dp)
                            )
                        } else {
                            Text(
                                text = limit,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            IconButton(onClick = {
                                limitDraft = limit      // start editing from the saved value
                                isEditing = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Two equal columns; they resize with the screen width
                    DetailRow("CC Number:", ccNumber, "CC Bank:", "Indus")
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow("CC Type:", "Visa", "CC Name:", "Legend")
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow("Name on Card:", "Salma Shaik", "Statement Date:", "15-10-2026")
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow("CVV:", "234", "Due Date:", "04-11-2026")

                    // Save / Cancel only while editing
                    if (isEditing) {
                        Spacer(modifier = Modifier.height(30.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    limit = limitDraft          // keep the new value
                                    isEditing = false
                                    onSaveClick(limit)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow4),
                                modifier = Modifier.width(100.dp)
                            ) {
                                Text(
                                    text = "Save",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                            Button(
                                onClick = {
                                    limitDraft = limit          // throw away the changes
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HintGray),
                                modifier = Modifier.width(100.dp)
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                        }
                    }

                    // Space so the last content can scroll above the Home/Back buttons
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // ---------- Home / Back, bottom left ----------
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 10.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(FigmaYellow4, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onHomeClick) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(FigmaYellow4, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

/** One row with two label/value pairs that share the width equally. */
@Composable
private fun DetailRow(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailItem(leftLabel, leftValue, Modifier.weight(1f))
        DetailItem(rightLabel, rightValue, Modifier.weight(1f))
    }
}

@Composable
private fun DetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Black
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Black
        )
    }
}