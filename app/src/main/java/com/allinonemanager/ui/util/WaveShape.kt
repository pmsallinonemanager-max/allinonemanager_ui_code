package com.allinonemanager.ui.util

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Used for the TOP yellow header.
 * The flat/rectangular part is at the top, the wave dips down as the
 * bottom edge of this shape (so the header appears taller in the middle
 * and shorter towards the corners — matching the reference image).
 */
class TopWaveShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, size.height * 0.75f)
            cubicTo(
                size.width * 0.25f, size.height * 1.15f,
                size.width * 0.75f, size.height * 0.55f,
                size.width, size.height * 0.85f
            )
            lineTo(size.width, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Used for the BOTTOM yellow footer.
 * The flat/rectangular part is at the bottom, the wave rises up as the
 * top edge of this shape.
 */
class BottomWaveShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, size.height * 0.25f)
            cubicTo(
                size.width * 0.25f, size.height * -0.15f,
                size.width * 0.75f, size.height * 0.45f,
                size.width, size.height * 0.15f
            )
            lineTo(size.width, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}