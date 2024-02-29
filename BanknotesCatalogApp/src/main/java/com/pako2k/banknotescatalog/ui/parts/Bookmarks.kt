package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.pako2k.banknotescatalog.ui.theme.color_menu_background


private const val DRAWER_WIDTH = 280


@Composable
fun Bookmarks (
    territories : List<Pair<UInt,String>>,
    currencies: List<Pair<UInt,String>>,
    historyTer : List<Pair<UInt,String>>,
    historyCur: List<Pair<UInt,String>>,
    state : DrawerState,
    drawerPadding : PaddingValues,
    onClick : (isTerritory : Boolean, id : UInt) -> Unit,
    onContent : @Composable () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurface

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl ) {
        ModalNavigationDrawer(
            scrimColor = Color.White.copy(alpha = 0.8f),
            gesturesEnabled = true,
            drawerState = state,
            modifier = Modifier.padding(drawerPadding),
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = color_menu_background,
                    drawerContentColor = contentColor,
                    modifier = Modifier
                        .width(DRAWER_WIDTH.dp)
                        .shadow(
                            4.dp,
                            clip = false,
                            shape = RoundedCornerShape(28.dp)// As per documentation
                        )
                        .verticalScroll(rememberScrollState())

                ) {
                    Text(
                        text = "Favourites",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(id = R.dimen.xl_padding))
                            .padding(top = dimensionResource(id = R.dimen.large_padding))
                            .padding(bottom = dimensionResource(id = R.dimen.small_padding))
                    )
                    HorizontalDivider(thickness = 2.dp)

                    if (territories.isEmpty() && currencies.isEmpty())
                        Text("Add your favorites by selecting the Star icon on the right of the Territories and Currencies",
                            textAlign = TextAlign.Justify,
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.large_padding)))

                    if (territories.isNotEmpty()) {
                        SubsectionTitle("Territories")

                        territories.forEach {
                            Item(
                                id = it.first,
                                text = it.second,
                                onClick = { id -> onClick(true, id) }
                            )
                        }
                    }

                    if (territories.isNotEmpty() && currencies.isNotEmpty())
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                                .padding(top = dimensionResource(id = R.dimen.medium_padding)),
                            thickness = Dp.Hairline
                        )

                    if(currencies.isNotEmpty()) {
                        SubsectionTitle("Currencies")
                        currencies.forEach {
                            Item(
                                id = it.first,
                                text = it.second,
                                onClick = {id -> onClick(false, id) }
                            )
                        }
                    }

                    Text(
                        text = "Recently visited",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(id = R.dimen.xl_padding))
                            .padding(top = dimensionResource(id = R.dimen.large_padding))
                            .padding(bottom = dimensionResource(id = R.dimen.small_padding))
                    )
                    HorizontalDivider(thickness = 2.dp)
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
                    historyTer.reversed().forEach {
                        LastVisitedItem(
                            id = it.first,
                            text = it.second,
                            onClick = { id -> onClick(true, id) }
                        )
                    }
                    historyCur.reversed().forEach {
                        LastVisitedItem(
                            id = it.first,
                            text = it.second,
                            onClick = { id -> onClick(false, id) }
                        )
                    }
                    val bottomPadding = if (historyCur.isEmpty() && historyTer.isEmpty())  R.dimen.xxl_padding else R.dimen.medium_padding
                    Spacer(modifier = Modifier.heightIn(min = dimensionResource(id = bottomPadding)))

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
private fun SubsectionTitle(
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
private fun Item(
    id : UInt,
    text : String,
    onClick : (UInt) -> Unit
){
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        maxLines = 2,
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.xl_padding),
                vertical = dimensionResource(id = R.dimen.medium_padding)
            )
            .clickable { onClick(id) }
    )
}

@Composable
private fun LastVisitedItem(
    id : UInt,
    text : String,
    onClick : (UInt) -> Unit
){
    Text(
        text = text,
        fontStyle = FontStyle.Italic,
        maxLines = 2,
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.xl_padding)
            )
            .padding(top = dimensionResource(id = R.dimen.small_padding),
                bottom = dimensionResource(id = R.dimen.medium_padding))
            .clickable { onClick(id) }
    )
}




private const val PREVIEW_WIDTH = 380
private const val PREVIEW_HEIGHT = 900

private val testTerritories = listOf(
    1u to "United States",
    2u to "France",
    3u to "European Union",
)

private val testCurrencies = listOf(
    1u to "Convertible Mark (BAM) - Bosnia and Herzegovina",
)

private val testHistoryTer = listOf<Pair<UInt,String>>(
    1u to "United States",
//    2u to "France",
)

private val testHistoryCur = listOf<Pair<UInt,String>>(
    3u to "Convertible Mark (BAM) - Bosnia and Herzegovina",
//    4u to "Dollar",
//    5u to "Sol",
)


@Preview (widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
fun BookmarksPreview() {
    BanknotesCatalogTheme {
        Bookmarks (
            testTerritories,
            testCurrencies,
            testHistoryTer,
            testHistoryCur,
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