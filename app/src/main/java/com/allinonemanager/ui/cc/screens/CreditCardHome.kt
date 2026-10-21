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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.allinonemanager.ui.cc.screens.dto.CreditCard
import com.allinonemanager.ui.cc.screens.dto.DueDetail
import com.allinonemanager.ui.util.BOTTOM_BAR_HEIGHT
import com.allinonemanager.ui.util.HomeBackButtons
import com.allinonemanager.ui.util.TopWaveShape
import com.allinonemanager.ui.util.nextDateLabel
import com.allinonemanager.ui.util.ordinalDay
import java.text.NumberFormat
import java.util.Locale

private val FigmaYellow4 = Color(0xFFFFC200)
private val TableLine = Color(0xFFEEEEEE)
private val StripeGray = Color(0xFFFAFAFA)
private val PaidGreen = Color(0xFF2E7D32)
private val UnpaidOrange = Color(0xFFEF6C00)
private val ActionText = Color(0xFF8A6100)      // dark yellow, readable on the light yellow pills

@Composable
@Preview(showBackground = true)
fun CreditCardHome(
    onAddCreditCardClick: () -> Unit = {},
    onAddDueDetailsClick: () -> Unit = {},
    onDueDetailsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onCardClick: (CreditCard) -> Unit = {},
    cards: List<CreditCard> = emptyList(),
    // All the user's dues, for each card's Paid / Unpaid status
    dues: List<DueDetail> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top yellow header with wave bottom edge
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
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 0.dp)
                )
            }
            // Scrolls so a long card list can move above the Home/Back buttons
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = BOTTOM_BAR_HEIGHT)     // scroll area stops above Home/Back
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // ---------- Summary ----------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryTile("Total Cards", if (isLoading) "-" else cards.size.toString(), Modifier.weight(1f))
                    SummaryTile(
                        "Total Limit",
                        if (isLoading) "-" else formatRupees(cards.sumOf { it.ccLimit?.toLongOrNull() ?: 0L }),
                        Modifier.weight(1.4f)
                    )
                }

                // Three equal pill buttons on one line
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HomeAction("Add Card", Icons.Default.Add, onAddCreditCardClick, Modifier.weight(1f))
                    HomeAction("Add Due", Icons.Default.Add, onAddDueDetailsClick, Modifier.weight(1f))
                    HomeAction("Dues", Icons.AutoMirrored.Filled.List, onDueDetailsClick, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (cards.isNotEmpty()) {
                    Text(
                        text = "Statement and Due show the next upcoming dates.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                    )
                    // Latest due per card decides its status
                    val latestDue = dues.filter { it.ccId != null }
                        .groupBy { it.ccId }
                        .mapValues { (_, cardDues) -> cardDues.maxByOrNull { it.dueDate.orEmpty() } }
                    CardsTable(cards = cards, latestDue = { latestDue[it.ccId] }, onCardClick = onCardClick)
                }

                val status = when {
                    isLoading -> "Loading credit cards..."
                    errorMessage != null -> errorMessage
                    cards.isEmpty() -> "No credit cards yet. Tap Add Credit Card to add one."
                    else -> null
                }
                status?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = if (errorMessage != null) Color.Red else Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        HomeBackButtons(
            onHomeClick = onHomeClick,
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.BottomStart)
        )

    }
}

@Composable
private fun SummaryTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(FigmaYellow4.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Card column is wider: it holds the bank and the masked number on two lines
private val columnWeights = listOf(1.5f, 0.95f, 0.85f, 0.95f)

/** Rounded table of the user's cards; tapping a row opens the card. */
@Composable
private fun CardsTable(
    cards: List<CreditCard>,
    latestDue: (CreditCard) -> DueDetail?,
    onCardClick: (CreditCard) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, TableLine, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FigmaYellow4)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Card", "Statement", "Due", "Status").forEachIndexed { i, header ->
                Text(
                    text = header,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = if (i == 0) TextAlign.Start else TextAlign.Center,
                    modifier = Modifier.weight(columnWeights[i])
                )
            }
        }
        cards.forEachIndexed { index, card ->
            if (index > 0) HorizontalDivider(color = TableLine)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (index % 2 == 0) Color.White else StripeGray)
                    .clickable { onCardClick(card) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(columnWeights[0])) {
                    Text(
                        text = card.ccBank.orEmpty(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = card.maskedNumber, fontSize = 12.sp, color = Color.Gray)
                }
                DayCell(card.statementDate, Modifier.weight(columnWeights[1]))
                DayCell(card.dueDate, Modifier.weight(columnWeights[2]))
                Box(modifier = Modifier.weight(columnWeights[3]), contentAlignment = Alignment.Center) {
                    StatusBadge(latestDue(card))
                }
            }
        }
    }
}

// Shows the next date the card's statement / due day falls on
@Composable
private fun DayCell(day: String?, modifier: Modifier) {
    Text(
        text = nextDateLabel(day),          // day of month -> next date, e.g. "4" -> "4 Nov"
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

// Paid (green) / Unpaid (orange) from the card's latest due; "No due" when it has none
@Composable
private fun StatusBadge(due: DueDetail?) {
    val (text, color) = when {
        due == null -> "No due" to Color.Gray
        due.isPaid == true -> "Paid" to PaidGreen
        else -> "Unpaid" to UnpaidOrange
    }
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        maxLines = 1,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

// 850000 -> "₹8,50,000"
private fun formatRupees(amount: Long): String =
    "₹" + NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")).format(amount)

// Light yellow pill: small icon and label side by side
@Composable
private fun HomeAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(FigmaYellow4.copy(alpha = 0.18f))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ActionText,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ActionText,
            maxLines = 1
        )
    }
}
