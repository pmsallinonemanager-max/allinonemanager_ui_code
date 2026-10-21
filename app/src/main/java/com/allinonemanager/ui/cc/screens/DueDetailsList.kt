package com.allinonemanager.ui.cc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allinonemanager.ui.cc.screens.dto.DueDetail
import com.allinonemanager.ui.user.screens.Black
import com.allinonemanager.ui.user.screens.HintGray
import com.allinonemanager.ui.util.TopWaveShape
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val FigmaYellow4 = Color(0xFFFFC200)
private val OverdueRed = Color(0xFFD32F2F)
private val SoonOrange = Color(0xFFEF6C00)
private val OkGreen = Color(0xFF2E7D32)

private val statusFilters = listOf("All", "Unpaid", "Paid")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun DueDetailsList(
    dues: List<DueDetail> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    // false when opened for one card from Credit Card Details
    showCardFilter: Boolean = true,
    onHomeClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCcId by remember { mutableStateOf<Int?>(null) }   // null = all cards
    var cardExpanded by remember { mutableStateOf(false) }
    var statusFilter by remember { mutableStateOf(statusFilters.first()) }

    val cards = dues.distinctBy { it.ccId }
    val shown = dues
        .filter { selectedCcId == null || it.ccId == selectedCcId }
        .filter {
            when (statusFilter) {
                "Unpaid" -> it.isPaid != true
                "Paid" -> it.isPaid == true
                else -> true
            }
        }
        .sortedWith(compareBy(nullsLast()) { it.dueDate })     // yyyy-MM-dd sorts as text
    val unpaid = shown.filter { it.isPaid != true }

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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState()),
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
                        text = "DUE DETAILS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FigmaYellow4,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Summary of what is shown below, unpaid only
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(FigmaYellow4.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Total due: ${formatAmount(unpaid.sumOf { it.dueAmt ?: 0.0 })}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Black
                        )
                        Text(
                            text = "${unpaid.size} unpaid · minimum ${formatAmount(unpaid.sumOf { it.minimumDue ?: 0.0 })}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (showCardFilter) {
                        ExposedDropdownMenuBox(
                            expanded = cardExpanded,
                            onExpandedChange = { cardExpanded = !cardExpanded }
                        ) {
                            OutlinedTextField(
                                value = cards.find { it.ccId == selectedCcId }?.cardLabel ?: "All cards",
                                onValueChange = {},                 // user picks from the list, no typing
                                readOnly = true,
                                label = { Text("Card", fontSize = 13.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cardExpanded) },
                                singleLine = true,
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Black,
                                    unfocusedBorderColor = Black,
                                    focusedTextColor = Black,
                                    unfocusedTextColor = Black,
                                    focusedLabelColor = Black,
                                    unfocusedLabelColor = Black
                                ),
                                textStyle = TextStyle(fontSize = 14.sp),
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = cardExpanded,
                                onDismissRequest = { cardExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All cards", fontSize = 13.sp) },
                                    onClick = {
                                        selectedCcId = null
                                        cardExpanded = false
                                    }
                                )
                                cards.forEach { card ->
                                    DropdownMenuItem(
                                        text = { Text(card.cardLabel, fontSize = 13.sp) },
                                        onClick = {
                                            selectedCcId = card.ccId
                                            cardExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        statusFilters.forEach { status ->
                            FilterChip(
                                selected = statusFilter == status,
                                onClick = { statusFilter = status },
                                label = { Text(status, fontSize = 13.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FigmaYellow4,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val status = when {
                        isLoading -> "Loading due details..."
                        errorMessage != null -> errorMessage
                        dues.isEmpty() -> "No due details yet. Add them from Add Due Details."
                        shown.isEmpty() -> "Nothing matches this filter."
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
                                .padding(vertical = 16.dp)
                        )
                    }

                    shown.forEach { due ->
                        DueItem(due)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Space so the last item can scroll above the Home/Back buttons
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

@Composable
private fun DueItem(due: DueDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Black, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = due.cardLabel,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = due.ccBank.orEmpty(),
                fontSize = 13.sp,
                color = Color.DarkGray,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Due: ${due.dueDate.orEmpty()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.weight(1f)
            )
            DueBadge(due)
        }
        Spacer(modifier = Modifier.height(8.dp))
        AmountRow("Due Amount", due.dueAmt)
        AmountRow("Minimum Due", due.minimumDue)
        AmountRow("Total Outstanding", due.totalOutstanding)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Statement: ${due.statementDate.orEmpty()}",
            fontSize = 12.sp,
            color = HintGray
        )
    }
}

@Composable
private fun AmountRow(label: String, amount: Double?) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
        Text(text = label, fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.weight(1f))
        Text(text = formatAmount(amount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Black)
    }
}

// Paid, or how close the due date is: red when overdue / today, orange within 5 days, green otherwise
@Composable
private fun DueBadge(due: DueDetail) {
    val days = daysUntil(due.dueDate)
    val (text, color) = when {
        due.isPaid == true -> "Paid" to OkGreen
        days == null -> return
        days < 0 -> "Overdue ${-days} ${if (days == -1) "day" else "days"}" to OverdueRed
        days == 0 -> "Due today" to OverdueRed
        days <= 5 -> "$days ${if (days == 1) "day" else "days"} left" to SoonOrange
        else -> "$days days left" to OkGreen
    }
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    )
}

private val rupees = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

// 112630.0 -> "₹1,12,630.00"
private fun formatAmount(amount: Double?): String = amount?.let { rupees.format(it) } ?: "-"

// Whole days from today to [date] (yyyy-MM-dd); negative once it has passed, null if it can't be read.
// java.time needs API 26 and minSdk is 24, so this uses Calendar.
private fun daysUntil(date: String?): Int? {
    val due = date?.let {
        runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(it) }.getOrNull()
    } ?: return null
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    // round, not truncate, so a daylight-saving 23h day still counts as one day
    return Math.round((due.time - today) / 86_400_000.0).toInt()
}
