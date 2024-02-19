package com.pako2k.banknotescatalog.ui.parts

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.color_menu_background


private const val DRAWER_WIDTH = 210


@Composable
fun HeaderMenu (
    state : DrawerState,
    drawerPadding : PaddingValues,
    onClickFilter : () -> Unit,
    onClickStats : () -> Unit,
    onClickSettings : () -> Unit,
    onContent : @Composable () -> Unit
) {
    ModalNavigationDrawer(
        scrimColor = Color.White.copy(alpha = 0.8f),
        gesturesEnabled = true,
        drawerState = state,
        modifier = Modifier.padding(drawerPadding),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(DRAWER_WIDTH.dp)
                    .height(IntrinsicSize.Min)
                    .shadow(
                        6.dp,
                        clip = false,
                        shape = RoundedCornerShape(28.dp)// As per documentation
                    )
            ) {
                OptionItem("Filter Options", R.drawable.filter_icon, onClickFilter)
                OptionItem("Statistics", R.drawable.stats_icon, onClickStats)
                OptionItem("Settings", R.drawable.settings_icon, onClickSettings)
            }
        }
    ) {
        onContent()
    }
}

@Composable
private fun OptionItem(
    text : String,
    @DrawableRes icon : Int,
    onClick: () -> Unit
){
    NavigationDrawerItem(
        icon = { OptionIcon(icon) },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.small_padding))
            )
                },
        selected = false,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = color_menu_background,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RectangleShape,
        onClick = onClick
    )
}

@Composable
private fun OptionIcon(
    @DrawableRes icon : Int
){
    val shape = RoundedCornerShape(8.dp)
    val color = MaterialTheme.colorScheme.onPrimaryContainer

    Icon(
        painter = painterResource(id = icon),
        contentDescription = "Menu Icon",
        tint = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier
            .size(dimensionResource(id = R.dimen.header_menu_icon_size_small))
            .background(color = MaterialTheme.colorScheme.primary, shape = shape)
            .border(1.dp, color = color, shape = shape)
    )
}




private const val PREVIEW_WIDTH = 380
private const val PREVIEW_HEIGHT = 900


@Preview (widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun HeaderMenuPreview() {
    BanknotesCatalogTheme {
        HeaderMenu (
            state = DrawerState(DrawerValue.Open),
            PaddingValues(vertical = 20.dp),
            {},
            {},
            {}
        ){
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                repeat(20) { Text("Example") }
            }
        }
    }
}