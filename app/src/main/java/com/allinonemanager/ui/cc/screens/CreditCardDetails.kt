package com.allinonemanager.ui.cc.screens

import android.app.Activity
import android.view.WindowManager
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allinonemanager.ui.cc.screens.dto.CreditCard
import com.allinonemanager.ui.user.screens.Black
import com.allinonemanager.ui.user.screens.White
import com.allinonemanager.ui.util.TopWaveShape
import kotlinx.coroutines.delay

private val FigmaYellow4 = Color(0xFFFFC200)
private const val REVEAL_MILLIS = 30_000L

@Composable
@Preview(showBackground = true)
fun CreditCardDetails(
    card: CreditCard? = null,
    statusMessage: String? = null,
    onHomeClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onViewDuesClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Full number / CVV are hidden again after 30s, and whenever the screen is left
    var showNumber by remember(card) { mutableStateOf(false) }
    var showCvv by remember(card) { mutableStateOf(false) }

    LaunchedEffect(showNumber) {
        if (showNumber) {
            delay(REVEAL_MILLIS)
            showNumber = false
        }
    }
    LaunchedEffect(showCvv) {
        if (showCvv) {
            delay(REVEAL_MILLIS)
            showCvv = false
        }
    }

    // Block screenshots / screen recording while card details are on screen
    val window = (LocalContext.current as? Activity)?.window
    DisposableEffect(window) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }

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

                    // Loading / not found message instead of the details
                    if (card == null) {
                        Text(
                            text = statusMessage ?: "Loading...",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {

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
                        Text(
                            text = card.ccLimit.orEmpty(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Card number gets the full width so "1234 5678 9012 3456" fits on one line
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                        DetailItem(
                            label = "CC Number:",
                            value = if (showNumber) formatCardNumber(card.ccNumber.orEmpty()) else card.maskedNumber,
                            revealed = showNumber,
                            onToggle = { showNumber = !showNumber },
                            toggleName = "card number",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Two equal columns; they resize with the screen width
                    DetailRow("CC Bank:", card.ccBank.orEmpty(), "CC Type:", card.ccType.orEmpty())
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow("CC Name:", card.ccName.orEmpty(), "Name on Card:", card.nameOnCard.orEmpty())
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow("Statement Date:", card.statementDate.orEmpty(), "Due Date:", card.dueDate.orEmpty())
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailItem("Exp Date:", card.expDate.orEmpty(), Modifier.weight(1f))
                        val cvv = card.cvv.orEmpty()
                        DetailItem(
                            label = "CVV:",
                            value = if (showCvv) cvv.ifBlank { "-" } else "•".repeat(cvv.length.coerceAtLeast(3)),
                            revealed = showCvv,
                            onToggle = { showCvv = !showCvv },
                            toggleName = "CVV",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onEditClick,
                            colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow4),
                            modifier = Modifier.width(130.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = " Edit Card",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                        Button(
                            onClick = onViewDuesClick,
                            colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow4),
                            modifier = Modifier.width(130.dp)
                        ) {
                            Text(
                                text = "View Dues",
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

/** Label above value; with [onToggle] an eye icon after the value shows / hides it. */
@Composable
private fun DetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    revealed: Boolean = false,
    onToggle: (() -> Unit)? = null,
    toggleName: String = ""
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Black
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (onToggle != null) {
                IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (revealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (revealed) "Hide $toggleName" else "Show $toggleName",
                        tint = Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// "1234567890123456" -> "1234 5678 9012 3456"
private fun formatCardNumber(number: String): String = number.chunked(4).joinToString(" ")