package com.pako2k.banknotescatalog.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color


fun Modifier.bottomBorder(color : Color, strokeWidth : Float = 1f) =
    this.drawBehind {
        val x = size.width
        val y = size.height
        drawLine(
            color,
            Offset(0f, y),
            Offset(x, y),
            strokeWidth
        )
    }

fun Modifier.leftBorder(color : Color, strokeWidth : Float = 1f) =
    this.drawBehind {
        val y = size.height
        drawLine(
            color,
            Offset(1f, 0f),
            Offset(1f, y),
            strokeWidth
        )
    }

fun Modifier.rightBorder(color : Color, strokeWidth : Float = 1f) =
    this.drawBehind {
        val y = size.height
        drawLine(
            color,
            Offset(size.width-1, 0f),
            Offset(size.width-1, y),
            strokeWidth
        )
    }
