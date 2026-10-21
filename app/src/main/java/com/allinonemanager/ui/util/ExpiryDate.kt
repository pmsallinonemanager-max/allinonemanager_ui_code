package com.allinonemanager.ui.util

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.allinonemanager.ui.user.screens.HintGray
import androidx.compose.material3.Text

@Composable
fun ExpiryDate(
    month: String,
    year: String,
    onMonthChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val yearFocus = remember { FocusRequester() }

    Row(
        modifier = modifier
            .size(width = 80.dp, height = FieldHeight)
            .background(Color.White, FieldShape)
            .border(1.dp, FieldBorder, FieldShape)
            .padding(horizontal = 6.dp),          // keeps MM / YY off the rounded edges
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExpiryPart(
            value = month,
            hint = "MM",
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(2)
                // reject months outside 01-12
                if (digits.length < 2 || digits.toInt() in 1..12) {
                    onMonthChange(digits)
                    if (digits.length == 2) yearFocus.requestFocus()
                }
            },
            modifier = Modifier.weight(1f)
        )

        Text("/", color = Color.Gray, fontSize = 15.sp)

        ExpiryPart(
            value = year,
            hint = "YY",
            onValueChange = { onYearChange(it.filter { c -> c.isDigit() }.take(2)) },
            modifier = Modifier
                .weight(1f)
                .focusRequester(yearFocus)
        )
    }
}

@Composable
private fun ExpiryPart(
    value: String,
    hint: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 15.sp,               // same as the other card fields
            color = Color.Black,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(Color.Black),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    Text(hint, color = HintGray, fontSize = 13.sp)
                }
                innerTextField()
            }
        }
    )
}