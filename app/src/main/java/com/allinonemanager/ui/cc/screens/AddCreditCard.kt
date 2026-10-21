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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allinonemanager.ui.cc.screens.dto.AddCreditCardRequest
import com.allinonemanager.ui.cc.screens.dto.CreditCard
import com.allinonemanager.ui.user.screens.Black
import com.allinonemanager.ui.user.screens.HintGray
import com.allinonemanager.ui.user.screens.White
import com.allinonemanager.ui.util.CompactTextField
import com.allinonemanager.ui.util.DayDropdown
import com.allinonemanager.ui.util.ExpiryDate
import com.allinonemanager.ui.util.BOTTOM_BAR_HEIGHT
import com.allinonemanager.ui.util.HomeBackButtons
import com.allinonemanager.ui.util.TopWaveShape

private val FigmaYellow4 = Color(0xFFFFC200)

// Shared with the in-place edit on Credit Card Details
internal val CARD_TYPES = listOf("Rupay", "Visa", "Master")
internal val CARD_BANKS = listOf(
    "State Bank of India", "HDFC Bank", "ICICI Bank", "Axis Bank",
    "Kotak Mahindra Bank", "IndusInd Bank", "Yes Bank", "IDFC FIRST Bank",
    "RBL Bank", "Federal Bank", "AU Small Finance Bank", "Bank of Baroda",
    "Punjab National Bank", "Canara Bank", "Union Bank of India",
    "Standard Chartered", "HSBC", "American Express", "Other"
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun AddCreditCard(
    onHomeClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSaveClick: (AddCreditCardRequest) -> Unit = {},
    isSaving: Boolean = false,
    serverError: String? = null,
    // Editing an existing card: fields start with its values. Null = adding a new card.
    card: CreditCard? = null,
    title: String = "ADD CREDIT CARD",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
            .padding(bottom = BOTTOM_BAR_HEIGHT)    // scroll area stops above Home/Back, so Save/Cancel never sit under them
            .verticalScroll(rememberScrollState())) {

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
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FigmaYellow4,
                modifier = Modifier
                    .padding(top = 30.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(30.dp))
            CreditCardForm(
                card = card,
                onSaveClick = onSaveClick,
                onCancelClick = onBackClick,
                isSaving = isSaving,
                serverError = serverError
            )
        }

        HomeBackButtons(
            onHomeClick = onHomeClick,
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.BottomStart)
        )

    }
}

/** Credit card fields with Save / Cancel. Fields start 30dp from the left edge of [modifier]. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreditCardForm(
    // Editing an existing card: fields start with its values. Null = adding a new card.
    card: CreditCard?,
    onSaveClick: (AddCreditCardRequest) -> Unit,
    onCancelClick: () -> Unit,
    isSaving: Boolean,
    serverError: String?,
    modifier: Modifier = Modifier
) {
    // Keyed on card so the fields fill in once the card has loaded
    var ccNumber by remember(card) { mutableStateOf(card?.ccNumber.orEmpty()) }
    var ccName by remember(card) { mutableStateOf(card?.ccName.orEmpty()) }
    var ccLimit by remember(card) { mutableStateOf(card?.ccLimit.orEmpty()) }
    var statementDay by remember(card) { mutableStateOf(card?.statementDate.orEmpty()) }
    var dueDay by remember(card) { mutableStateOf(card?.dueDate.orEmpty()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var ccBank by remember(card) { mutableStateOf(card?.ccBank.orEmpty()) }
    var ccType by remember(card) { mutableStateOf(card?.ccType.orEmpty()) }
    val ccTypes = listOf("Select Type") + CARD_TYPES
    val banks = listOf("Select Bank") + CARD_BANKS
    var bankExpanded by remember { mutableStateOf(false) }
    var ccTypeExpanded by remember { mutableStateOf(false) }
    var nameOnCard by remember(card) { mutableStateOf(card?.nameOnCard.orEmpty()) }
    // expDate is stored as "MM/YY"
    var expMonth by remember(card) { mutableStateOf(card?.expDate.orEmpty().substringBefore("/")) }
    var expYear by remember(card) { mutableStateOf(card?.expDate.orEmpty().substringAfter("/", "")) }
    var cvv by remember(card) { mutableStateOf(card?.cvv.orEmpty()) }
    Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = "Credit Card Number:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier
                    .padding(start = 30.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            CompactTextField(
                value = ccNumber,
                onValueChange = { input ->
                    ccNumber = input.filter { it.isDigit() }.take(16)
                },
                placeholder = "1234 5678 9012 3456",
                keyboardType = KeyboardType.Number,
                visualTransformation = CreditCardVisualTransformation(),
                modifier = Modifier
                    .padding(start = 30.dp)   // padding first, then size, so the field keeps its full width
                    .width(300.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LabelPair("CC Bank:", "CC Type:")
            Spacer(modifier = Modifier.height(3.dp))
            Row(
            ) {

                ExposedDropdownMenuBox(
                    expanded = bankExpanded,
                    onExpandedChange = { bankExpanded = !bankExpanded },
                    modifier = Modifier.padding(start = 30.dp)
                ) {
                    CompactTextField(
                        value = ccBank,
                        onValueChange = {},                 // user picks from the list, no typing
                        readOnly = true,
                        placeholder = "Select bank",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bankExpanded) },
                        fontSize = 13.sp,
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .width(140.dp)
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
                                    bankExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = ccTypeExpanded,
                    onExpandedChange = { ccTypeExpanded = !ccTypeExpanded },
                    modifier = Modifier.padding(start = 20.dp)
                ) {
                    CompactTextField(
                        value = ccType,
                        onValueChange = {},                 // user picks from the list, no typing
                        readOnly = true,
                        placeholder = "Select type",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ccTypeExpanded) },
                        fontSize = 13.sp,
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .width(140.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = ccTypeExpanded,
                        onDismissRequest = { ccTypeExpanded = false }
                    ) {
                        ccTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, fontSize = 13.sp) },
                                onClick = {
                                    ccType = type
                                    ccTypeExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Widths match the fields below: name 130dp, 10dp gap + expiry 80dp, 10dp gap + CVV 70dp
            Row(
            ) {
                Text(
                    text = "Name on card:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(start = 30.dp)
                        .width(130.dp)
                )
                Text(
                    text = "Exp Dt:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .width(80.dp)
                )
                Text(
                    text = "CVV:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .width(70.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(
            ) {
                CompactTextField(
                    value = nameOnCard,
                    onValueChange = { input ->
                        nameOnCard = input.filter { it.isLetter() || it == ' ' }.take(26)
                    },
                    placeholder = "Name on card",
                    modifier = Modifier
                        .padding(start = 30.dp)   // padding first, then size, so the field keeps its full width
                        .width(130.dp)
                )
                ExpiryDate(
                    month = expMonth,
                    year = expYear,
                    onMonthChange = { expMonth = it },
                    onYearChange = { expYear = it },
                    modifier = Modifier.padding(start = 10.dp)
                )
                CompactTextField(
                    value = cvv,
                    onValueChange = { input ->
                        cvv = input.filter { it.isDigit() }.take(4)
                    },
                    placeholder = "123",
                    keyboardType = KeyboardType.NumberPassword,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .padding(start = 10.dp)   // padding first, then size, so the field keeps its full width
                        .width(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            LabelPair("CC Name:", "Limit:")
            Spacer(modifier = Modifier.height(3.dp))
            Row {
                CcTextField(
                    value = ccName,
                    onValueChange = { ccName = it.take(30) },
                    placeholder = "e.g. Legend",
                    modifier = Modifier.padding(start = 30.dp)
                )
                CcTextField(
                    value = ccLimit,
                    onValueChange = { input -> ccLimit = input.filter { it.isDigit() }.take(9) },
                    placeholder = "e.g. 200000",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            LabelPair("Statement Day:", "Due Day:")
            Spacer(modifier = Modifier.height(3.dp))
            Row {
                DayDropdown(
                    value = statementDay,
                    onValueChange = { statementDay = it },
                    placeholder = "Select day",
                    modifier = Modifier.padding(start = 30.dp).width(140.dp)
                )
                DayDropdown(
                    value = dueDay,
                    onValueChange = { dueDay = it },
                    placeholder = "Select day",
                    modifier = Modifier.padding(start = 20.dp).width(140.dp)
                )
            }

            (errorMessage ?: serverError)?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 30.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 16.dp, end = 35.dp)
                    .align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                        errorMessage = validateCard(
                            ccNumber, ccBank, ccType, ccName, nameOnCard, expMonth, expYear,
                            cvv, statementDay, dueDay, ccLimit
                        )
                        if (errorMessage == null) {
                            onSaveClick(
                                AddCreditCardRequest(
                                    userId = 0,     // filled in by CreditCardViewModel from the session
                                    ccNumber = ccNumber,
                                    ccBank = ccBank,
                                    ccName = ccName.trim(),
                                    ccType = ccType,
                                    expDate = "$expMonth/$expYear",
                                    cvv = cvv,
                                    nameOnCard = nameOnCard.trim(),
                                    statementDate = statementDay.toInt().toString(),
                                    dueDate = dueDay.toInt().toString(),
                                    ccLimit = ccLimit
                                )
                            )
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .width(100.dp)) {
                    Text(
                        text = if (isSaving) "Saving..." else "Save",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }

                Button(onClick = onCancelClick,
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
}

// Two labels sitting above two 140dp fields (30dp start, [gap] between them)
@Composable
internal fun LabelPair(left: String, right: String, gap: Dp = 20.dp) {
    Row {
        Text(
            text = left,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Black,
            modifier = Modifier.padding(start = 30.dp).width(140.dp)
        )
        Text(
            text = right,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Black,
            modifier = Modifier.padding(start = gap).width(140.dp)
        )
    }
}

@Composable
private fun CcTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    CompactTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        keyboardType = keyboardType,
        modifier = modifier.width(140.dp)
    )
}

// Returns an error message, or null if the card can be saved.
internal fun validateCard(
    ccNumber: String,
    ccBank: String,
    ccType: String,
    ccName: String,
    nameOnCard: String,
    expMonth: String,
    expYear: String,
    cvv: String,
    statementDay: String,
    dueDay: String,
    ccLimit: String
): String? = when {
    ccNumber.length !in 13..16 -> "Enter a valid credit card number"
    ccBank.isBlank() || ccBank == "Select Bank" -> "Select a bank"
    ccType.isBlank() || ccType == "Select Type" -> "Select a card type"
    nameOnCard.isBlank() -> "Enter the name on card"
    expMonth.toIntOrNull() !in 1..12 || expYear.length != 2 -> "Enter a valid expiry date (MM/YY)"
    cvv.length !in 3..4 -> "Enter a valid CVV"
    ccName.isBlank() -> "Enter the card name"
    ccLimit.isBlank() -> "Enter the credit limit"
    statementDay.toIntOrNull() !in 1..31 -> "Statement day must be between 1 and 31"
    dueDay.toIntOrNull() !in 1..31 -> "Due day must be between 1 and 31"
    else -> null
}

class CreditCardVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(16)
        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                if (i > 0 && i % 4 == 0) append(' ')
                append(c)
            }
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                offset + (offset - 1).coerceAtLeast(0) / 4

            override fun transformedToOriginal(offset: Int): Int =
                offset - offset / 5
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}