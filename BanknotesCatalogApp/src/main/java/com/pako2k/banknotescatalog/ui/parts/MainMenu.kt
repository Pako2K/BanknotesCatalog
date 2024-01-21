package com.pako2k.banknotescatalog.ui.parts

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


enum class MenuOption (@StringRes val textId : Int, @DrawableRes val iconId : Int){
    COUNTRIES (textId = R.string.menu_countries, iconId = R.drawable.m_flag_icon),
    CURRENCIES (textId = R.string.menu_currencies, iconId = R.drawable.m_currency_icon),
    DENOMINATIONS (textId = R.string.menu_values, iconId = R.drawable.m_value_icon),
    YEARS (textId = R.string.menu_years, iconId = R.drawable.m_calendar_icon),
    COLLECTION (textId = R.string.menu_my_collection, iconId = R.drawable.m_collection_icon),
    LOG_IN (textId = R.string.menu_login, iconId = R.drawable.login_icon)
}


@Composable
fun MainMenu(
    modifier: Modifier = Modifier,
    windowSize : WindowSizeClass,
    selectedOption : MenuOption? = null,
    isLoggedIn : Boolean,
    onClick: (clickedOption : MenuOption) -> Unit
){
    Log.d(stringResource(id = R.string.app_log_tag),"Start MainMenu")

    Column {
        // This Box is just a shadow
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        0.0f to MaterialTheme.colorScheme.background,
                        0.9f to MaterialTheme.colorScheme.outline
                    )
                )
                .height(4.dp)
                .fillMaxWidth()
                .alpha(0.25f)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            val showName = windowSize.widthSizeClass in listOf(WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded)
            for (option in MenuOption.values().dropLast(2)){
                MainMenuOption(
                    option = option,
                    showName = showName,
                    isSelected = option == selectedOption,
                    onClick = onClick
                )
            }
            val lastOption =  if (isLoggedIn) MenuOption.COLLECTION else MenuOption.LOG_IN

            MainMenuOption(option = lastOption, showName = showName, isSelected = lastOption == selectedOption, onClick = onClick)
        }
    }
}

@Composable
fun MainMenuOption(
    modifier: Modifier = Modifier,
    option : MenuOption,
    showName : Boolean,
    isSelected : Boolean,
    onClick: (clickedOption : MenuOption) -> Unit
) {
    Row (
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.small_padding))
    ){
        Button(
            onClick = { onClick(option) },
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White,
                disabledContainerColor = Color.White),
            shape = MaterialTheme.shapes.small
        ) {
            val iconColor = MaterialTheme.colorScheme.primary
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
            ) {
                val iconSize : Dp = when (showName) {
                    true -> 28.dp
                    false -> 36.dp
                }
                Icon(
                    painter = painterResource(option.iconId),
                    contentDescription = "${stringResource(id = option.textId)} Icon",
                    tint = iconColor,
                    modifier = Modifier.size(iconSize)
                )
                if(showName) {
                    Text(
                        text = stringResource(id = option.textId),
                        color = iconColor,
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.small_padding))
                    )
                }
            }
        }
    }
}



private const val TEST_WIDTH = 600

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview (widthDp = TEST_WIDTH)
@Composable
fun MainMenuPreview() {
    BanknotesCatalogTheme {
        MainMenu(isLoggedIn = false,
            windowSize = WindowSizeClass.calculateFromSize(size = DpSize(TEST_WIDTH.dp,600.dp)),
            onClick = {})
    }
}