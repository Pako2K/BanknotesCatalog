package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


private const val DRAWER_WIDTH = 250


@Composable
fun Bookmarks (
    territories : List<Pair<UInt,String>>,
    currencies: List<Pair<UInt,String>>,
    state : DrawerState,
    drawerPadding : PaddingValues,
    onClick : (isTerritory : Boolean, id : UInt) -> Unit,
    onContent : @Composable () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl ) {
        ModalNavigationDrawer(
            scrimColor = backgroundColor.copy(alpha = 0.5f),
            gesturesEnabled = true,
            drawerState = state,
            modifier = Modifier.padding(drawerPadding),
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = backgroundColor,
                    drawerContentColor = contentColor,
                    modifier = Modifier
                        .width(DRAWER_WIDTH.dp)
                        .shadow(
                            4.dp,
                            clip = false,
                            shape = RoundedCornerShape(28.dp)// As per documentation
                        )

                ) {
                    Text(
                        text = "Favourites",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                            .padding(top = dimensionResource(id = R.dimen.large_padding))
                            .padding(bottom = dimensionResource(id = R.dimen.small_padding))
                    )
                    Divider(thickness = 2.dp)

                    if (territories.isEmpty() && currencies.isEmpty())
                        Text("Add your favorites by selecting the Star icon on the right of the Territories and Currencies",
                            textAlign = TextAlign.Justify,
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding)))

                    if (territories.isNotEmpty()) {
                        SectionTitle("Territories")

                        territories.forEach {
                            Item(
                                id = it.first,
                                text = it.second,
                                backgroundColor = backgroundColor,
                                onClick = { id -> onClick(true, id) }
                            )
                        }
                    }

                    if (territories.isNotEmpty() && currencies.isNotEmpty())
                        Divider(
                            thickness = Dp.Hairline,
                            modifier = Modifier
                                .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                                .padding(top = dimensionResource(id = R.dimen.medium_padding))
                        )

                    if(currencies.isNotEmpty()) {
                        SectionTitle("Currencies")
                        currencies.forEach {
                            Item(
                                id = it.first,
                                text = it.second,
                                backgroundColor = backgroundColor,
                                onClick = {id -> onClick(false, id) }
                            )
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr ) {
                onContent()
            }
        }
    }
}

@Composable
fun SectionTitle(
    text : String,
){
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
            .padding(top = dimensionResource(id = R.dimen.large_padding))
            .padding(bottom = dimensionResource(id = R.dimen.small_padding))
    )
}

@Composable
fun Item(
    id : UInt,
    text : String,
    backgroundColor : Color,
    onClick : (UInt) -> Unit
){
    NavigationDrawerItem(
        shape = RectangleShape,
        label = {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                maxLines = 1,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = dimensionResource(id = R.dimen.large_padding))
            )
        },
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = backgroundColor),
        selected = false,
        onClick = { onClick(id) },
        modifier = Modifier
            .heightIn(max = MaterialTheme.typography.bodyLarge.fontSize.value.dp * 3)
    )
}


private const val PREVIEW_WIDTH = 400
private const val PREVIEW_HEIGHT = 900

private val testTerritories = listOf(
    1u to "United States",
    2u to "France",
    3u to "European Union",
)

private val testCurrencies = listOf(
    1u to "Peseta",
)

@Preview (widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
fun BookmarksPreview() {
    BanknotesCatalogTheme {
        Bookmarks (
            testTerritories,
            testCurrencies,
            state = DrawerState(DrawerValue.Open),
            PaddingValues(vertical = 20.dp),
            {_,_ ->}
        ){
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
            ) {
                repeat(20) { Text("Example") }
            }
        }
    }
}