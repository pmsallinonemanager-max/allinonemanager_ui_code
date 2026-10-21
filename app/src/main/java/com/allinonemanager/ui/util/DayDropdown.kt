package com.allinonemanager.ui.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val days = (1..31).map { it.toString() }

/** Pick a day of the month, 1 - 31. [modifier] sets the field's width. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Day"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        CompactTextField(
            value = ordinalDay(value),          // shows "15th"; [value] stays "15"
            onValueChange = {},                 // user picks from the list, no typing
            readOnly = true,
            placeholder = placeholder,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 280.dp)     // about 6 days visible, the rest scroll
        ) {
            days.forEach { day ->
                DropdownMenuItem(
                    text = { Text(ordinalDay(day), fontSize = 14.sp) },
                    onClick = {
                        onValueChange(day)
                        expanded = false
                    }
                )
            }
        }
    }
}
