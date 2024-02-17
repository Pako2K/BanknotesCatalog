package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.Currency
import com.pako2k.banknotescatalog.data.CurrencyUnit
import com.pako2k.banknotescatalog.data.SuccessorCurrency
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryKey
import com.pako2k.banknotescatalog.data.TerritoryLink
import com.pako2k.banknotescatalog.data.TerritoryLinkExt
import com.pako2k.banknotescatalog.data.TerritoryTypeKey
import com.pako2k.banknotescatalog.ui.parts.FavouriteIcon
import com.pako2k.banknotescatalog.ui.parts.NameIso3Text
import com.pako2k.banknotescatalog.ui.parts.SubviewMenu
import com.pako2k.banknotescatalog.ui.parts.SubviewOptions
import com.pako2k.banknotescatalog.ui.parts.TerritoryBadge
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.background_color_territory_badge
import com.pako2k.banknotescatalog.ui.views.subviews.CurrencyInfoSubview
import java.util.Locale


@Composable
fun CurrencyView(
    windowWidth: WindowWidthSizeClass,
    data : Currency,
    isFavourite: Boolean,
    onCountryClick: (territoryID: UInt)->Unit,
    onCurrencyClick: (currencyID: UInt)->Unit,
    onAddFavourite: (Boolean) -> Unit
){
    Log.d(stringResource(id = R.string.app_log_tag),"Start Currency")

    val currencyMenu = SubviewOptions(
        stringResource(R.string.cur_menu_issues),
        stringResource(R.string.cur_menu_banknotes),
        stringResource(R.string.cur_menu_stats),
        stringResource(R.string.cur_menu_info))

    var selectedOption by rememberSaveable {
        mutableStateOf(currencyMenu.options.first())
    }

    val enterAnimation = slideInVertically() + expandVertically(expandFrom = Alignment.Top) + fadeIn(initialAlpha = 0.3f)
    val exitAnimation = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()

    Surface{
        Column {
            Box{
                CurrencyHeader(windowWidth = windowWidth, data = data, onCountryClick = onCountryClick, onCurrencyClick = onCurrencyClick)

                // Favourite button
                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.xs_padding))
                ) {
                    FavouriteIcon(
                        size = dimensionResource(id = R.dimen.favourite_icon_size),
                        isFavourite = isFavourite,
                        onAddFavourite = onAddFavourite
                    )
                }
            }


            SubviewMenu(subviewOptions = currencyMenu, selectedOption, onClick = {selectedOption = it})
            AnimatedVisibility(
                visible = selectedOption == stringResource(R.string.ter_menu_info),
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                CurrencyInfoSubview(data) {
                    onCountryClick(it)
                }
            }
        }
    }
}


@Composable
private fun CurrencyHeader(
    windowWidth: WindowWidthSizeClass,
    data: Currency,
    onCountryClick: (territoryID: UInt)->Unit,
    onCurrencyClick: (currencyID: UInt)->Unit,
) {

    // TERRITORY, SYMBOL AND NAMES
    Box(
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_header),
            contentScale = ContentScale.Crop,
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )

        Column (
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.medium_padding),
                    vertical = dimensionResource(id = R.dimen.small_padding))
        ){
            // Symbol and name
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Currency Symbol
                if (!data.symbol.isNullOrEmpty())
                    Text(
                        data.symbol,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier
                            .border(
                                width = Dp.Hairline,
                                shape = RectangleShape,
                                color = Color.Black
                            )
                            .background(MaterialTheme.colorScheme.surfaceTint)
                            .padding(dimensionResource(id = R.dimen.small_padding))
                    )

                val iso3 = if (data.iso3 != null) " - ${data.iso3}" else null
                NameIso3Text(
                    name = data.name,
                    iso3 = iso3,
                    modifier = Modifier.padding(end = dimensionResource(id = R.dimen.xxl_padding))
                )
            }

            // Full Name + dates
            var fullName = data.fullName + " (${data.startDate}"
            fullName += if (data.endDate != null) " - ${data.endDate})" else ")"
            Text(
                text = fullName,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                modifier = Modifier
                    .padding(bottom = dimensionResource(id = R.dimen.small_padding))
            )

            // Units
            if (data.units != null){
                data.units.forEach {
                    val unitName = if((it.value ?: 1f) > 1f) it.namePlural else it.name
                    val abbreviation = it.abbreviation?.let {it2 ->"($it2)"} ?: ""
                    Text(
                        text= "1 ${data.symbol?:data.name} = ${it.value?.toInt()} $unitName $abbreviation",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(bottom = dimensionResource(id = R.dimen.medium_padding))
                        )
                }
            }

            // Owner territories
            if(windowWidth != WindowWidthSizeClass.Compact)
                Row {
                    ListTerritories(territories = data.ownedByExt, onClick = onCountryClick)
                }
            else
                ListTerritories(territories = data.ownedByExt, onClick = onCountryClick)

            if (data.successor != null) {
                var oldCur = data.iso3 ?: data.name
                var newCur = data.successorExt?.currency?.iso3?:data.successorExt?.currency?.name?:""
                if (oldCur == newCur){
                    oldCur ="(Old) $oldCur"
                    newCur ="(New) $newCur"
                }
                if (data.namePlural != null && data.successor.rate != null && data.successor.rate > 1f && data.iso3 == null)
                    oldCur.replace(data.name, data.namePlural)
                var rate = "%,.2f".format(Locale.getDefault(), data.successor.rate)
                rate = if (rate.endsWith("00")) rate.dropLast(3) else rate
                val exchangeRateStr = "1 $newCur = $rate $oldCur"

                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Replaced by: ",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    data.successorExt?.currency?.let {
                        var name = it.name
                        name += if (it.iso3 != null) " (${it.iso3})" else ""
                        Box(
                            modifier = Modifier
                                .padding(start = dimensionResource(id = R.dimen.small_padding))
                                .clickable {
                                    onCurrencyClick(it.id)
                                }
                                .background(
                                    background_color_territory_badge,
                                    shape = MaterialTheme.shapes.small
                                )
                                .border(
                                    width = Dp.Hairline,
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.outline
                                )
                        ){
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                                    .padding(vertical = dimensionResource(id = R.dimen.xs_padding))
                            )
                        }

                    }

                    Text(
                        text = exchangeRateStr,
                        style = if (windowWidth != WindowWidthSizeClass.Compact)
                            MaterialTheme.typography.labelLarge
                        else
                            MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(start = dimensionResource(id = R.dimen.medium_padding))
                            .padding(vertical = dimensionResource(id = R.dimen.xs_padding))
                    )
                }
            }
        }
    }
}

@Composable
private fun ListTerritories(
    territories : List<TerritoryLinkExt>,
    onClick : (territoryId : UInt) -> Unit
){
    territories.forEach {
        Row(
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.xs_padding))
        ) {
            TerritoryBadge(
                territory = it.territory,
                maxTextWidth = 200.dp,
                onClick =  onClick
            )
            if (territories.size > 1) {
                var dates = "(${it.start}"
                dates += if (it.end != null) " - ${it.end})" else ")"
                Text(
                    dates,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .alignByBaseline()
                        .padding(horizontal = dimensionResource(id = R.dimen.small_padding))
                        .padding(vertical = dimensionResource(id = R.dimen.xs_padding))

                )
            }
        }
    }
}

private const val TEST_WIDTH = 360
private const val TEST_HEIGHT = 800

private val territoriesTest = listOf(
    Territory(
        id = 33u,
        name = "United States 33",
        iso3 = "USA",
        continent = Continent(3u, "South America"),
        territoryType = TerritoryTypeKey(2u, "Territory"),
        iso2 = "US",
        officialName = "United States of America",
        start = 1926,
        end = null,
        parentId = null,
        successors = null,
        description = "The",
        uri = ""
    ),
    Territory(
        id = 7u,
        name = "States of United States 7",
        iso3 = "USA",
        continent = Continent(3u, "South America"),
        territoryType = TerritoryTypeKey(2u, "Territory"),
        iso2 = "US",
        officialName = "United States of America",
        start = 1926,
        end = null,
        parentId = null,
        successors = null,
        description = "The",
        uri = ""
    ),
    Territory(
        id = 8u,
        name = "United States 8",
        iso3 = "USA",
        continent = Continent(3u, "South America"),
        territoryType = TerritoryTypeKey(2u, "Territory"),
        iso2 = "US",
        officialName = "United States of America",
        start = 1926,
        end = null,
        parentId = null,
        successors = null,
        description = "The",
        uri = ""
    ),
    Territory(
        id = 11u,
        name = "States of United States 11",
        iso3 = "USA",
        continent = Continent(3u, "South America"),
        territoryType = TerritoryTypeKey(2u, "Territory"),
        iso2 = "US",
        officialName = "United States of America",
        start = 1926,
        end = null,
        parentId = null,
        successors = null,
        description = "The",
        uri = ""
    ),
)

private val currenciesTest = listOf(
    Currency(
        id = 67u,
        name = "Euro",
        namePlural = "Euros",
        symbol = "€",
        iso3 = "EUR",
        continent = Continent(3u, "South America"),
        fullName = "Euro",
        start = "1926.06.01",
        end = "2011.12.31",
        ownedBy = arrayOf(
            TerritoryLink( TerritoryKey(77u, "European Union"), "1926", "2011"),
        ),
        sharedBy = arrayOf(TerritoryLink( TerritoryKey(7u, "Portugal"), "1926", "2011")),
        usedBy = arrayOf(TerritoryLink( TerritoryKey(7u, "Equatorial Guinea"), "1926", "2011")),
        units = arrayOf(CurrencyUnit(77u, "Centimo", "Centimos", "Ct.", 100f)),
        description = "The United States of America (USA or U.S.A.), commonly known as the United States (US or U.S.) or informally America, is a country primarily located in North America. The third-largest country in the world by land and total area, the U.S. is a federal republic of 50 states, with its capital in a separate federal district",
        uri = ""
    )
)

private val testData = Currency(
    id = 1u,
    name = "Peseta",
    namePlural = "Pesetas",
    symbol = "Pta",
    iso3 = "PES",
    continent = Continent(3u, "South America"),
    fullName = "Spanish Peseta",
    start = "1926.06.01",
    end = "2011.12.31",
    ownedBy = arrayOf(
        TerritoryLink( TerritoryKey(7u, "Spain"), "1926", "2011"),
        TerritoryLink( TerritoryKey(8u, "Spain22"), "1826", "1925")
    ),
    sharedBy = arrayOf(TerritoryLink( TerritoryKey(7u, "Portugal"), "1926", "2011")),
    usedBy = arrayOf(TerritoryLink( TerritoryKey(7u, "Equatorial Guinea"), "1926", "2011")),
    successor = SuccessorCurrency(67u, 1000000000f),
    units = arrayOf(CurrencyUnit(77u, "Centimo", "Centimos", "Ct.", 100f)),
    description = "The United States of America (USA or U.S.A.), commonly known as the United States (US or U.S.) or informally America, is a country primarily located in North America. The third-largest country in the world by land and total area, the U.S. is a federal republic of 50 states, with its capital in a separate federal district",
    uri = ""
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(widthDp = TEST_WIDTH, heightDp = TEST_HEIGHT)
@Composable
fun CurrencyViewPreview() {
    BanknotesCatalogTheme {
        testData.extend(territoriesList = territoriesTest, mapOf(), currenciesTest)
        CurrencyView(
            windowWidth = WindowSizeClass.calculateFromSize(size = DpSize(TEST_WIDTH.dp,TEST_HEIGHT.dp)).widthSizeClass,
            data = testData,
            isFavourite = true,
            onCountryClick = { _ -> },
            onCurrencyClick = { _ -> },
            onAddFavourite = {}
        )
    }
}
