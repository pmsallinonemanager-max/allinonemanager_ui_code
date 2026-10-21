package com.allinonemanager.ui.cc.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.allinonemanager.BuildConfig
import com.allinonemanager.ui.cc.screens.dto.AddCreditCardRequest
import com.allinonemanager.ui.cc.screens.dto.CreditCard
import com.allinonemanager.ui.user.screens.Black
import com.allinonemanager.ui.user.screens.White
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.allinonemanager.ui.util.BOTTOM_BAR_HEIGHT
import com.allinonemanager.ui.util.CompactTextField
import com.allinonemanager.ui.util.DayDropdown
import com.allinonemanager.ui.util.ExpiryDate
import com.allinonemanager.ui.util.HomeBackButtons
import com.allinonemanager.ui.util.TopWaveShape
import com.allinonemanager.ui.util.nextDateLabel
import com.allinonemanager.ui.util.ordinalDay
import kotlinx.coroutines.delay

private val FigmaYellow4 = Color(0xFFFFC200)
private val CardYellowDark = Color(0xFFF59E0B)
private val PanelGray = Color(0xFFF6F6F6)
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
    // Editing happens on this page; the caller turns it off again after a successful save
    isEditing: Boolean = false,
    onCancelEdit: () -> Unit = {},
    onSaveClick: (AddCreditCardRequest) -> Unit = {},
    isSaving: Boolean = false,
    serverError: String? = null,
    modifier: Modifier = Modifier
) {
    // Full number / CVV are hidden again after 30s, and whenever the screen is left
    var showNumber by remember(card) { mutableStateOf(false) }
    var showCvv by remember(card) { mutableStateOf(false) }

    // Unsaved edits; starts again from the card each time editing begins
    var draft by remember(card, isEditing) { mutableStateOf(card ?: CreditCard()) }
    var editError by remember(card, isEditing) { mutableStateOf<String?>(null) }

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

    // Block screenshots / screen recording while card details are on screen.
    // Skipped in debug builds so the screen shows in Android Studio's Running Devices mirror.
    val window = (LocalContext.current as? Activity)?.window
    DisposableEffect(window) {
        if (!BuildConfig.DEBUG) window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
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
                    .navigationBarsPadding()
                    .padding(bottom = BOTTOM_BAR_HEIGHT)     // scroll area stops above Home/Back
                    .imePadding()       // before verticalScroll, so the visible area shrinks and the focused field scrolls into view
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    if (card != null && !isEditing) {
                        // Title on the left, actions on the right, so they are visible without scrolling
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CREDIT CARD DETAILS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FigmaYellow4,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.weight(1f)
                            )
                            // Icon only, so the title fits on one line
                            OutlinedIconButton(
                                onClick = onEditClick,
                                border = BorderStroke(1.5.dp, FigmaYellow4),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit card",
                                    tint = FigmaYellow4,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Button(
                                onClick = onViewDuesClick,
                                colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow4),
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Dues", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                            }
                        }
                    } else {
                        Text(
                            text = if (isEditing) "EDIT CREDIT CARD" else "CREDIT CARD DETAILS",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FigmaYellow4,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Loading / not found message instead of the details
                    if (card == null) {
                        Text(
                            text = statusMessage ?: "Loading...",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (isEditing) {

                    // ---------- Edit in place ----------
                    // Preview shows the unsaved values as they are typed
                    CardPreview(card = draft, showNumber = true, onToggleNumber = null)
                    Spacer(modifier = Modifier.height(20.dp))
                    EditPanel(draft = draft, onChange = { draft = it })

                    (editError ?: serverError)?.let {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(it, color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelEdit,
                            enabled = !isSaving,
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Button(
                            onClick = {
                                editError = draft.validate()
                                if (editError == null) onSaveClick(draft.toRequest())
                            },
                            enabled = !isSaving,
                            colors = ButtonDefaults.buttonColors(containerColor = FigmaYellow4),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Text(
                                text = if (isSaving) "Saving..." else "Save",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    }

                    } else {

                    // ---------- Card preview ----------
                    CardPreview(
                        card = card,
                        showNumber = showNumber,
                        onToggleNumber = { showNumber = !showNumber }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ---------- Other details ----------
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PanelGray, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        DetailRow("Limit", formatLimit(card.ccLimit.orEmpty()), "Card Name", card.ccName.orEmpty())
                        // Stored as a day of the month; shown with the next date it falls on
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            DetailItem(
                                label = "Statement Date",
                                value = monthlyDay(card.statementDate),
                                note = "Next: ${nextDateLabel(card.statementDate)}",
                                modifier = Modifier.weight(1f)
                            )
                            DetailItem(
                                label = "Due Date",
                                value = monthlyDay(card.dueDate),
                                note = "Next: ${nextDateLabel(card.dueDate)}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            val cvv = card.cvv.orEmpty()
                            DetailItem(
                                label = "CVV",
                                value = if (showCvv) cvv.ifBlank { "-" } else "•".repeat(cvv.length.coerceAtLeast(3)),
                                revealed = showCvv,
                                onToggle = { showCvv = !showCvv },
                                toggleName = "CVV",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
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

/** Yellow credit-card-shaped summary: bank, type, number (with reveal), name and expiry. */
@Composable
private fun CardPreview(
    card: CreditCard,
    showNumber: Boolean,
    onToggleNumber: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(listOf(FigmaYellow4, CardYellowDark)),
                RoundedCornerShape(18.dp)
            )
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = card.ccBank.orEmpty(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(
                text = card.ccType.orEmpty().uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (showNumber) formatCardNumber(card.ccNumber.orEmpty()) else card.maskedNumber,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = White,
                maxLines = 1,
                modifier = Modifier.weight(1f, fill = false)
            )
            // No eye icon while editing: the number is being typed below anyway
            if (onToggleNumber != null) {
                IconButton(onClick = onToggleNumber, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (showNumber) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showNumber) "Hide card number" else "Show card number",
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("CARD HOLDER", fontSize = 10.sp, color = White.copy(alpha = 0.8f))
                Text(
                    text = card.nameOnCard.orEmpty().uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("EXPIRES", fontSize = 10.sp, color = White.copy(alpha = 0.8f))
                Text(
                    text = card.expDate.orEmpty(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }
    }
}

/** The details panel with a field in place of each value. Every change gives back an updated copy of [draft]. */
@Composable
private fun EditPanel(draft: CreditCard, onChange: (CreditCard) -> Unit) {
    // expDate is stored as "MM/YY"
    val expMonth = draft.expDate.orEmpty().substringBefore("/")
    val expYear = draft.expDate.orEmpty().substringAfter("/", "")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PanelGray, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EditField("Bank", Modifier.weight(1.4f)) {
                OptionDropdown(draft.ccBank.orEmpty(), CARD_BANKS, "Select bank") { onChange(draft.copy(ccBank = it)) }
            }
            EditField("Card Type", Modifier.weight(1f)) {
                OptionDropdown(draft.ccType.orEmpty(), CARD_TYPES, "Select type") { onChange(draft.copy(ccType = it)) }
            }
        }
        EditField("Card Number") {
            CompactTextField(
                value = draft.ccNumber.orEmpty(),
                onValueChange = { input -> onChange(draft.copy(ccNumber = input.filter { it.isDigit() }.take(16))) },
                placeholder = "1234 5678 9012 3456",
                keyboardType = KeyboardType.Number,
                visualTransformation = CreditCardVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        EditField("Name on Card") {
            CompactTextField(
                value = draft.nameOnCard.orEmpty(),
                onValueChange = { input -> onChange(draft.copy(nameOnCard = input.filter { it.isLetter() || it == ' ' }.take(26))) },
                placeholder = "Name on card",
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EditField("Card Name", Modifier.weight(1f)) {
                CompactTextField(
                    value = draft.ccName.orEmpty(),
                    onValueChange = { onChange(draft.copy(ccName = it.take(30))) },
                    placeholder = "e.g. Legend",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            EditField("Limit", Modifier.weight(1f)) {
                CompactTextField(
                    value = draft.ccLimit.orEmpty(),
                    onValueChange = { input -> onChange(draft.copy(ccLimit = input.filter { it.isDigit() }.take(9))) },
                    placeholder = "e.g. 200000",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EditField("Expiry", Modifier.weight(1f)) {
                ExpiryDate(
                    month = expMonth,
                    year = expYear,
                    onMonthChange = { onChange(draft.copy(expDate = "$it/$expYear")) },
                    onYearChange = { onChange(draft.copy(expDate = "$expMonth/$it")) }
                )
            }
            EditField("CVV", Modifier.weight(1f)) {
                CompactTextField(
                    value = draft.cvv.orEmpty(),
                    onValueChange = { input -> onChange(draft.copy(cvv = input.filter { it.isDigit() }.take(4))) },
                    placeholder = "123",
                    keyboardType = KeyboardType.NumberPassword,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EditField("Statement Day", Modifier.weight(1f)) {
                DayDropdown(
                    value = draft.statementDate.orEmpty(),
                    onValueChange = { onChange(draft.copy(statementDate = it)) },
                    placeholder = "Select day",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            EditField("Due Day", Modifier.weight(1f)) {
                DayDropdown(
                    value = draft.dueDate.orEmpty(),
                    onValueChange = { onChange(draft.copy(dueDate = it)) },
                    placeholder = "Select day",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/** Small grey label above an edit field, matching the labels in view mode. */
@Composable
private fun EditField(label: String, modifier: Modifier = Modifier, field: @Composable () -> Unit) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 3.dp)
        )
        field()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionDropdown(value: String, options: List<String>, placeholder: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        CompactTextField(
            value = value,
            onValueChange = {},                 // user picks from the list, no typing
            readOnly = true,
            placeholder = placeholder,
            fontSize = 13.sp,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 13.sp) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Same checks as Add Credit Card
private fun CreditCard.validate(): String? {
    val expDate = expDate.orEmpty()
    return validateCard(
        ccNumber.orEmpty(), ccBank.orEmpty(), ccType.orEmpty(), ccName.orEmpty(), nameOnCard.orEmpty(),
        expDate.substringBefore("/"), expDate.substringAfter("/", ""), cvv.orEmpty(),
        statementDate.orEmpty(), dueDate.orEmpty(), ccLimit.orEmpty()
    )
}

// userId is filled in by CreditCardViewModel from the session
private fun CreditCard.toRequest() = AddCreditCardRequest(
    userId = 0,
    ccNumber = ccNumber.orEmpty(),
    ccBank = ccBank.orEmpty(),
    ccName = ccName.orEmpty().trim(),
    ccType = ccType.orEmpty(),
    expDate = expDate.orEmpty(),
    cvv = cvv.orEmpty(),
    nameOnCard = nameOnCard.orEmpty().trim(),
    statementDate = statementDate.orEmpty(),
    dueDate = dueDate.orEmpty(),
    ccLimit = ccLimit.orEmpty()
)

/** One row with two label/value pairs that share the width equally. */
@Composable
private fun DetailRow(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
    toggleName: String = "",
    note: String? = null            // small grey line under the value
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value.ifBlank { "-" },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (onToggle != null) {
                IconButton(onClick = onToggle, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (revealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (revealed) "Hide $toggleName" else "Show $toggleName",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        note?.let {
            Text(text = it, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

// "1234567890123456" -> "1234 5678 9012 3456"
private fun formatCardNumber(number: String): String = number.chunked(4).joinToString(" ")

// "15" -> "15th of every month"
private fun monthlyDay(day: String?): String =
    if (day.isNullOrBlank()) "-" else "${ordinalDay(day)} of every month"

// "200000" -> "₹2,00,000" (Indian grouping); anything non-numeric is shown as is
private fun formatLimit(limit: String): String {
    val amount = limit.toLongOrNull() ?: return limit
    return "₹" + NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")).format(amount)
}