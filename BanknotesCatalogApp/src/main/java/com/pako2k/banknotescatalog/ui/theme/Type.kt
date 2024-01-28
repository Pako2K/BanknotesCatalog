package com.pako2k.banknotescatalog.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pako2k.banknotescatalog.R


val Girassol = FontFamily(Font(R.font.girassol))
val displayShadow = Shadow(
    color = md_theme_light_shadow,
    offset = Offset(3.0f, 3.0f),
    blurRadius = 2.0f
)

val displayDarkShadow = Shadow(
    color = Color.Black,
    offset = Offset(5.0f, 5.0f),
    blurRadius = 3.0f
)

// Set of Material typography styles to start with
val myTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Girassol,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        shadow = displayShadow
    ),
    displayMedium= TextStyle(
        fontFamily = Girassol,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        shadow = displayShadow
    ),
    displaySmall = TextStyle(
        fontFamily = Girassol,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        shadow = displayShadow
    ),
    headlineLarge = TextStyle(
        fontFamily = Girassol,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        shadow = displayShadow
    ),
    headlineMedium = TextStyle(
        fontFamily = Girassol,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        shadow = displayShadow
    ),
    headlineSmall = TextStyle(
        fontFamily = Girassol,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 12.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 11.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 10.sp
    )
)

val typographySans = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 18.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 10.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 8.sp
    )
)

val typographySerif = Typography(
    labelLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 12.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 10.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 8.sp
    )
)