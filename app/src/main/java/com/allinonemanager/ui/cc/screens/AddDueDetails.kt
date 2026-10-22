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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allinonemanager.ui.cc.screens.dto.AddDueDetailsRequest
import com.allinonemanager.ui.cc.screens.dto.CreditCard
import com.allinonemanager.ui.user.screens.Black
import com.allinonemanager.ui.user.screens.HintGray
import com.allinonemanager.ui.user.screens.White
import com.allinonemanager.ui.util.BOTTOM_BAR_HEIGHT
import com.allinonemanager.ui.util.HomeBackButtons
import com.allinonemanager.ui.util.TopWaveShape
import com.allinonemanager.ui.util.currentCycleDates
import com.allinonemanager.ui.util.monthLabel
import com.allinonemanager.ui.util.nextStatementLabel
import com.allinonemanager.ui.util.readableDate
import com.allinonemanager.ui.cc.screens.dto.DueDetail
import androidx.compose.foundation.clickable
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

private val FigmaYellow4 = Color(0xFFFFC200)
private val BlockRed = Color(0xFFD32F2F)
private val WarnOrange = Color(0xFFEF6C00)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun AddDueDetails(
    onHomeClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    cards: List<CreditCard> = emptyList(),
    // Card to start with, when opened from that card's Dues page; null = user picks
    preselectedCcId: Int? = null,
    // The user's saved dues: blocks a second bill for the same month and flags unpaid older bills
    dues: List<DueDetail> = emptyList(),
    onViewDuesClick: (ccId: Int) -> Unit = {},
    onSaveClick: (AddDueDetailsRequest) -> Unit = {},
    isSaving: Boolean = false,
    serverError: String? = null,
    modifier: Modifier = Modifier
) {
    var ccBank by remember { mutableStateOf("") }
    var selectedCard by remember { mutableStateOf<CreditCard?>(null) }
    // Only banks the user has cards for; picking one narrows the CC list
    val banks = cards.mapNotNull { it.ccBank }.distinct()
    val ccLists = if (ccBank.isBlank()) cards else cards.filter { it.ccBank == ccBank }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var bankExpanded by remember { mutableStateOf(false) }
    var ccListExpanded by remember { mutableStateOf(false) }
    var stmtDate by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueAmt by remember { mutableStateOf("") }
    var minimumDue by remember { mutableStateOf("") }
    var totalOutStanding by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Cards load after the screen opens, so pick the preselected one once they arrive
    LaunchedEffect(cards) {
        if (preselectedCcId != null && selectedCard == null) {
            cards.find { it.ccId == preselectedCcId }?.let {
                selectedCard = it
                ccBank = it.ccBank.orEmpty()
            }
        }
    }

    // Statement month typed so far ("2026-10"), compared with the card's saved dues
    val cardDues = selectedCard?.ccId?.let { id -> dues.filter { it.ccId == id } }.orEmpty()
    val stmtMonth = stmtDate.takeIf { it.length >= 6 }?.let { "${it.take(4)}-${it.substring(4, 6)}" }
    val sameMonthDue = stmtMonth?.let { month -> cardDues.find { it.statementDate?.take(7) == month } }
    val olderUnpaid = if (stmtMonth == null) emptyList() else
        cardDues.filter { it.isPaid != true && it.statementDate.orEmpty().take(7) < stmtMonth }

    // Picking a card fills in its current bill cycle; the dates can still be edited
    LaunchedEffect(selectedCard) {
        val card = selectedCard ?: return@LaunchedEffect
        val (statement, due) = currentCycleDates(card.statementDate, card.dueDate)
        val digits = SimpleDateFormat("yyyyMMdd", Locale.US)      // fields hold digits only
        statement?.let { stmtDate = digits.format(it.time) }
        due?.let { dueDate = digits.format(it.time) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()                 // 1) reserve space for the keyboard
            .padding(bottom = BOTTOM_BAR_HEIGHT)    // scroll area stops above Home/Back, so Save/Cancel never sit under them
            .verticalScroll(scrollState)) {

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

            Text(
                text = "ADD DUE DETAILS",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FigmaYellow4,
                modifier = Modifier
                    .padding(top = 30.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(30.dp))
            LabelPair("Choose Bank:", "CC List:", gap = 30.dp)
            Spacer(modifier = Modifier.height(3.dp))
            Row(
            ) {

                ExposedDropdownMenuBox(
                    expanded = bankExpanded,
                    onExpandedChange = { bankExpanded = !bankExpanded },
                    modifier = Modifier.padding(start = 30.dp)
                ) {
                    OutlinedTextField(
                        value = ccBank,
                        onValueChange = {},                 // user picks from the list, no typing
                        readOnly = true,
                        placeholder = { Text("Select bank", color = HintGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bankExpanded) },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Black,
                            unfocusedBorderColor = Black,
                            focusedTextColor = Black,
                            unfocusedTextColor = Black,
                            cursorColor = Black
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .width(140.dp)
                            .height(56.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = bankExpanded,
                        onDismissRequest = { bankExpanded = false }
                    ) {
                        banks.forEach { bank ->
                            DropdownMenuItem(
                                text = { Text(bank, fontSize = 13.sp) },
                                onClick = {
                                    ccBank = bank
                                    if (selectedCard?.ccBank != bank) selectedCard = null
                                    bankExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = ccListExpanded,
                    onExpandedChange = { ccListExpanded = !ccListExpanded },
                    modifier = Modifier.padding(start = 30.dp)
                ) {
                    OutlinedTextField(
                        value = selectedCard?.let(::cardLabel).orEmpty(),
                        onValueChange = {},                 // user picks from the list, no typing
                        readOnly = true,
                        placeholder = { Text("Select card", color = HintGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ccListExpanded) },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Black,
                            unfocusedBorderColor = Black,
                            focusedTextColor = Black,
                            unfocusedTextColor = Black,
                            cursorColor = Black
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .width(140.dp)
                            .height(56.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = ccListExpanded,
                        onDismissRequest = { ccListExpanded = false }
                    ) {
                        if (ccLists.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No cards. Add a credit card first.", fontSize = 13.sp) },
                                onClick = { ccListExpanded = false }
                            )
                        }
                        ccLists.forEach { card ->
                            DropdownMenuItem(
                                text = { Text(cardLabel(card), fontSize = 13.sp) },
                                onClick = {
                                    selectedCard = card
                                    ccListExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LabelPair("Statement Date:", "Due Date:", gap = 30.dp)
            Spacer(modifier = Modifier.height(3.dp))
            Row(
            ) {
                OutlinedTextField(
                    value = stmtDate,
                    onValueChange = { input ->
                        stmtDate = input.filter { it.isDigit() }.take(8)
                    },
                    placeholder = { Text("YYYY-MM-DD", color = HintGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = DateVisualTransformation(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Black,
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        cursorColor = Black
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier
                        .padding(start = 30.dp)   // padding first, then size, so the field keeps its full 330 x 50
                        .width(140.dp)
                        .height(56.dp)
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { input ->
                        dueDate = input.filter { it.isDigit() }.take(8)
                    },
                    placeholder = { Text("YYYY-MM-DD", color = HintGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = DateVisualTransformation(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Black,
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        cursorColor = Black
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier
                        .padding(start = 30.dp)   // padding first, then size, so the field keeps its full 330 x 50
                        .width(140.dp)
                        .height(56.dp)
                )

            }
            if (sameMonthDue != null) {
                val next = nextStatementLabel(selectedCard?.statementDate)?.let { " Next statement on $it." }.orEmpty()
                FormNote(
                    text = "${monthLabel(sameMonthDue.statementDate)} bill for this card is already added.$next",
                    color = BlockRed,
                    onViewDues = { sameMonthDue.ccId?.let(onViewDuesClick) }
                )
            } else if (olderUnpaid.isNotEmpty()) {
                val text = olderUnpaid.singleOrNull()?.let {
                    "${monthLabel(it.statementDate)} bill ${rupees(it.dueAmt)} (due ${readableDate(it.dueDate)}) " +
                        "is still unpaid. If you paid it, mark it paid in Dues."
                } ?: ("${olderUnpaid.size} earlier bills (${rupees(olderUnpaid.sumOf { it.dueAmt ?: 0.0 })}) " +
                    "are still unpaid. If you paid them, mark them paid in Dues.")
                FormNote(
                    text = text,
                    color = WarnOrange,
                    onViewDues = { selectedCard?.ccId?.let(onViewDuesClick) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LabelPair("Due Amount:", "Minimum Due:", gap = 30.dp)
            Spacer(modifier = Modifier.height(3.dp))
            Row(
            ) {
                OutlinedTextField(
                    value = dueAmt,
                    onValueChange = { input ->
                        dueAmt = amountInput(input)
                    },
                    placeholder = { Text("₹ 0.00", color = HintGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Black,
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        cursorColor = Black
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier
                        .padding(start = 30.dp)   // padding first, then size, so the field keeps its full 330 x 50
                        .width(140.dp)
                        .height(56.dp)
                )

                OutlinedTextField(
                    value = minimumDue,
                    onValueChange = { input ->
                        minimumDue = amountInput(input)
                    },
                    placeholder = { Text("₹ 0.00", color = HintGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Black,
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        cursorColor = Black
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier
                        .padding(start = 30.dp)   // padding first, then size, so the field keeps its full 330 x 50
                        .width(140.dp)
                        .height(56.dp)
                )

            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
            ) {
                Text(
                    text = "Total Outstanding:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    modifier = Modifier
                        .padding(start = 30.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(
            ) {
                OutlinedTextField(
                    value = totalOutStanding,
                    onValueChange = { input ->
                        totalOutStanding = amountInput(input)
                    },
                    placeholder = { Text("₹ 0.00", color = HintGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Black,
                        focusedTextColor = Black,
                        unfocusedTextColor = Black,
                        cursorColor = Black
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier
                        .padding(start = 30.dp)   // padding first, then size, so the field keeps its full 330 x 50
                        .width(140.dp)
                        .height(56.dp)
                )
            }
            (errorMessage ?: serverError)?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 30.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 16.dp, end = 25.dp)
                    .align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                        val card = selectedCard
                        errorMessage = validateDue(card, stmtDate, dueDate, dueAmt, minimumDue, totalOutStanding)
                        if (errorMessage == null && card?.ccId != null) {
                            onSaveClick(
                                AddDueDetailsRequest(
                                    ccId = card.ccId,
                                    dueAmt = dueAmt.toDouble(),
                                    minimumDue = minimumDue.toDouble(),
                                    totalOutstanding = totalOutStanding,
                                    statementDate = formatDate(stmtDate),
                                    dueDate = formatDate(dueDate)
                                )
                            )
                        }
                    },
                    enabled = !isSaving && sameMonthDue == null,     // one bill per card per month
                    modifier = Modifier
                        .width(100.dp)) {
                    Text(
                        text = if (isSaving) "Saving..." else "Save",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }

                Button(onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = HintGray),
                    modifier = Modifier
                        .width(100.dp)) {
                    Text(
                        text = "Cancel",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }

        }

        HomeBackButtons(
            onHomeClick = onHomeClick,
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.BottomStart)
        )

    }
}

// Coloured note under the dates with a "View dues" link to the card's Dues page
@Composable
private fun FormNote(text: String, color: Color, onViewDues: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(start = 30.dp, end = 30.dp, top = 10.dp)
            .fillMaxWidth()
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 13.sp, color = color)
        Text(
            text = "View dues",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable(onClick = onViewDues)
        )
    }
}

// 12000.0 -> "₹12,000"
private fun rupees(amount: Double?): String =
    "₹" + NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")).format(amount ?: 0.0)

private fun cardLabel(card: CreditCard): String =
    listOfNotNull(card.ccName?.takeIf { it.isNotBlank() }, card.maskedNumber).joinToString(" ")

// Digits with at most one decimal point and two decimals, e.g. "1234.50"
internal fun amountInput(input: String): String {
    val cleaned = input.filter { it.isDigit() || it == '.' }
    val dot = cleaned.indexOf('.')
    if (dot < 0) return cleaned.take(10)
    return cleaned.take(dot).take(10) + "." + cleaned.drop(dot + 1).replace(".", "").take(2)
}

// The date fields hold 8 digits (yyyyMMdd); this adds the dashes the backend expects
internal fun formatDate(digits: String): String =
    "${digits.take(4)}-${digits.substring(4, 6)}-${digits.substring(6, 8)}"

internal fun isValidDate(digits: String): Boolean {
    if (digits.length != 8) return false
    return runCatching {
        SimpleDateFormat("yyyyMMdd", Locale.US).apply { isLenient = false }.parse(digits)
    }.isSuccess
}

// Returns an error message, or null if the due details can be saved.
private fun validateDue(
    card: CreditCard?,
    stmtDate: String,
    dueDate: String,
    dueAmt: String,
    minimumDue: String,
    totalOutstanding: String
): String? = when {
    card?.ccId == null -> "Select a credit card"
    !isValidDate(stmtDate) -> "Enter a valid statement date (yyyy-mm-dd)"
    !isValidDate(dueDate) -> "Enter a valid due date (yyyy-mm-dd)"
    dueAmt.toDoubleOrNull() == null -> "Enter the due amount"
    minimumDue.toDoubleOrNull() == null -> "Enter the minimum due"
    totalOutstanding.toDoubleOrNull() == null -> "Enter the total outstanding"
    else -> null
}

// Shows 20261020 as 2026-10-20 while typing
internal class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(8)
        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                if (i == 4 || i == 6) append('-')
                append(c)
            }
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 4 -> offset
                offset <= 6 -> offset + 1
                else -> offset + 2
            }

            override fun transformedToOriginal(offset: Int): Int = when {
                offset <= 4 -> offset
                offset <= 7 -> offset - 1
                else -> offset - 2
            }.coerceAtMost(digits.length)
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}
