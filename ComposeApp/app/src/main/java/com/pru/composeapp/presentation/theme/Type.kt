package com.pru.composeapp.presentation.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.pru.composeapp.R

val fonts = FontFamily(
    /*Font(R.font.opensans_bold),
    Font(R.font.opensans_extra_bold),
    Font(R.font.opensans_light),
    Font(R.font.opensans_medium),*/
    Font(R.font.opensans_regular),
//    Font(R.font.opensans_semibold),
)
val Typography = Typography(
    defaultFontFamily = fonts
)