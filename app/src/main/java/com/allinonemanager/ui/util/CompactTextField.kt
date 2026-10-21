package com.allinonemanager.ui.util

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allinonemanager.ui.user.screens.Black
import com.allinonemanager.ui.user.screens.HintGray

val FieldHeight = 46.dp
val FieldShape = RoundedCornerShape(12.dp)
val FieldBorder = Color(0xFFBDBDBD)
val FieldFocusBorder = Color(0xFFFFC200)

@Composable
fun compactFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = FieldFocusBorder,
    unfocusedBorderColor = FieldBorder,
    focusedTextColor = Black,
    unfocusedTextColor = Black,
    cursorColor = Black
)

/**
 * OutlinedTextField look, but [FieldHeight] tall instead of Material's fixed 56dp minimum.
 * The stock field clips its text when made shorter, so this uses BasicTextField
 * with the outlined decoration and smaller inner padding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false,
    fontSize: TextUnit = 15.sp,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = compactFieldColors()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        singleLine = true,
        textStyle = TextStyle(fontSize = fontSize, color = Black),
        cursorBrush = SolidColor(Black),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        modifier = modifier.height(FieldHeight)
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = true,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            placeholder = { Text(placeholder, color = HintGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            trailingIcon = trailingIcon,
            colors = colors,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            container = {
                OutlinedTextFieldDefaults.Container(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = colors,
                    shape = FieldShape
                )
            }
        )
    }
}
