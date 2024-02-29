package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.ContinentIconMap
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryId
import com.pako2k.banknotescatalog.data.TerritoryTypeEnum
import com.pako2k.banknotescatalog.data.TerritoryTypeKey
import com.pako2k.banknotescatalog.ui.parts.FavouriteIcon
import com.pako2k.banknotescatalog.ui.parts.NameIso3Text
import com.pako2k.banknotescatalog.ui.parts.SubviewMenu
import com.pako2k.banknotescatalog.ui.parts.SubviewOptions
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.typographySans
import com.pako2k.banknotescatalog.ui.views.subviews.TerritoryInfoSubview


@Composable
fun TerritoryView(
    windowWidth: WindowWidthSizeClass,
    data : Territory,
    isFavourite: Boolean,
    onCountryClick: (territoryID: UInt)->Unit,
    onAddFavourite: (Boolean) -> Unit
){
    Log.d(stringResource(id = R.string.app_log_tag),"Start Country")

    val currencyMenu = SubviewOptions(
        stringResource(R.string.ter_menu_currencies),
        stringResource(R.string.ter_menu_banknotes),
        stringResource(R.string.ter_menu_stats),
        stringResource(R.string.ter_menu_info))

    var selectedOption by rememberSaveable {
        mutableStateOf(currencyMenu.options.first())
    }

    val enterAnimation = slideInVertically() + expandVertically(expandFrom = Alignment.Top) + fadeIn(initialAlpha = 0.3f)
    val exitAnimation = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()

    Surface {
        Column {
            Box {
                TerritoryHeader(windowWidth, data)

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
            SubviewMenu(currencyMenu, selectedOption, onClick = {selectedOption = it})
            AnimatedVisibility(
                visible = selectedOption == stringResource(R.string.ter_menu_info),
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                TerritoryInfoSubview(data) {
                    onCountryClick(it)
                }
            }
            AnimatedVisibility(
                visible = selectedOption == stringResource(R.string.ter_menu_currencies),
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                Text("Currencies")
            }
            AnimatedVisibility(
                visible = selectedOption == stringResource(R.string.ter_menu_banknotes),
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                Text("Banknotes")
            }
            AnimatedVisibility(
                visible = selectedOption == stringResource(R.string.ter_menu_stats),
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                Text("Stats")
            }
        }
    }
}


@Composable
private fun TerritoryHeader(
    windowWidth: WindowWidthSizeClass,
    data : Territory
){
    Surface(
        modifier = Modifier.height(IntrinsicSize.Min)
    ){
        // Background
        Image(
            painter = painterResource(id = R.drawable.background_header),
            contentScale = ContentScale.Crop,
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )

        // CONTINENT, FLAG AND NAMES
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_padding))
        ) {
            // ONLY WIDE SCREENS: Continent Icon and name
            if (windowWidth != WindowWidthSizeClass.Compact) {
                val continentName = data.continent.name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = dimensionResource(id = R.dimen.small_padding))
                ) {
                    Icon(
                        painter = painterResource(id = ContinentIconMap[continentName] ?: 0),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(dimensionResource(id = R.dimen.territory_continent_icon_height))
                    )
                    Text(
                        text = continentName,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = typographySans.labelSmall,
                        modifier = Modifier.width(dimensionResource(id = R.dimen.territory_continent_text_width))
                    )
                }
            }

            // Flag and territory names
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Territory Flag
                    FlagImage(data.flag, dimensionResource(id = R.dimen.territory_flag_height))

                    val iso3 = if (data.iso3 != null) " - ${data.iso3}" else null
                    NameIso3Text(
                        name = data.name,
                        iso3 = iso3,
                        modifier = Modifier.padding(end = dimensionResource(id = R.dimen.xxl_padding))
                    )
                }

                var terSubtitle = data.officialName + ", ${data.start}"
                terSubtitle += if (data.end != null) " - ${data.end}  " else "  "
                Text(
                    text = terSubtitle,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
            }
        }
    }
}


@Composable
private fun FlagImage(flag : ImageBitmap?, height : Dp){
    if (flag != null)
        Image(
            bitmap = flag,
            contentDescription = stringResource(id = R.string.content_description_flag),
            alignment = Alignment.Center,
            modifier = Modifier.height(height)
        )
    else
        Image(
            painter = painterResource(id = R.drawable.m_flag_icon),
            contentDescription = stringResource(id = R.string.content_description_flag),
            alignment = Alignment.Center,
            modifier = Modifier.height(height)
        )
}






private const val TEST_WIDTH = 380
private const val TEST_HEIGHT = 680

private val territoriesTest = listOf(
    Territory(
        id = 3u,
        name = "United States 3",
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
        name = "nited States ofnited States of United States 7",
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
        id = 9u,
        name = "nited States ofnited States ofnited States of United States 9 ",
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
        name = "ofnited States of United States 9 ",
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

private val testData = Territory(
    id = 1u,
    name = "United States",
    iso3 = "USA",
    continent = Continent(3u, "South America"),
    territoryType = TerritoryTypeKey(2u, TerritoryTypeEnum.NR.name),
    iso2 = "US",
    officialName = "United States of America And America America And America",
    start = 1926,
    end = 1967,
    parentId = 3u,
    successors = arrayOf(TerritoryId(7u), TerritoryId(9u), TerritoryId(11u)),
    description = "The United States of America (USA or U.S.A.), commonly known as the United States (US or U.S.) or informally America, is a country primarily located in North America. The third-largest country in the world by land and total area, the U.S. is a federal republic of 50 states, with its capital in a separate federal district",
    uri = ""
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview (widthDp = TEST_WIDTH, heightDp = TEST_HEIGHT)
@Composable
fun TerritoryViewPreview() {
    BanknotesCatalogTheme {
        testData.extend(territoriesTest, mapOf())
        TerritoryView(
            windowWidth = WindowSizeClass.calculateFromSize(size = DpSize(TEST_WIDTH.dp,TEST_HEIGHT.dp)).widthSizeClass,
            data = testData,
            isFavourite = true,
            onCountryClick = { _ -> },
            onAddFavourite = {}
        )
    }
}



