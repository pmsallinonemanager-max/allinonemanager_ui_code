package com.allinonemanager.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Table matching the Figma design: black outer border, yellow header,
 * vertical lines between columns AND a horizontal line after every row.
 */
@Composable
fun GridTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier,
    columnWeights: List<Float> = List(headers.size) { 1f },
    headerBackground: Color = Color(0xFFFFC200),
    headerTextColor: Color = Color.White,
    lineColor: Color = Color.Black,
    textColor: Color = Color.Black,
    showRowLines: Boolean = true,
    selectedRowColor: Color = Color(0xFFFFF3C4),   // light yellow for the tapped row
    onRowClick: ((index: Int, row: List<String>) -> Unit)? = null
) {
    var selectedIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
            .border(1.dp, lineColor)
            .background(Color.White)
    ) {
        // Header
        GridRow(
            cells = headers,
            weights = columnWeights,
            lineColor = lineColor,
            textColor = headerTextColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.background(headerBackground)
        )
        HLine(lineColor)

        // Data rows, line after each row
        LazyColumn {
            itemsIndexed(rows) { index, row ->
                GridRow(
                    cells = row,
                    weights = columnWeights,
                    lineColor = lineColor,
                    textColor = textColor,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(if (index == selectedIndex) selectedRowColor else Color.Transparent)
                        .clickable {
                            selectedIndex = index
                            onRowClick?.invoke(index, row)
                        }
                )
                if (showRowLines && index != rows.lastIndex) HLine(lineColor)
            }
        }
    }
}

@Composable
private fun HLine(color: Color) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

@Composable
private fun GridRow(
    cells: List<String>,
    weights: List<Float>,
    lineColor: Color,
    textColor: Color,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        cells.forEachIndexed { index, cell ->
            Text(
                text = cell,
                color = textColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                modifier = Modifier
                    .weight(weights.getOrElse(index) { 1f })
                    .padding(horizontal = 6.dp, vertical = 10.dp)
            )
            if (index != cells.lastIndex) {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(lineColor)
                )
            }
        }
    }
}

/* ---------- Usage (Credit Card screen) ----------

GridTable(
    headers = listOf("cc number", "stmt date", "due date", "due amount"),
    rows = listOf(
        listOf("1234", "05 Oct", "25 Oct", "₹12,500"),
        listOf("5678", "10 Oct", "30 Oct", "₹4,200"),
        listOf("9012", "15 Oct", "04 Nov", "₹8,900")
    ),
    columnWeights = listOf(1f, 1f, 1f, 1.1f),
    modifier = Modifier.padding(top = 16.dp)
)

*/