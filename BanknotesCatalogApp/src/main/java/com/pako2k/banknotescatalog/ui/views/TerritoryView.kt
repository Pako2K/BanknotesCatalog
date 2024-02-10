package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import com.pako2k.banknotescatalog.data.TerritoryTypeKey
import com.pako2k.banknotescatalog.data.TerritoryTypes
import com.pako2k.banknotescatalog.ui.parts.TerritoryBadge
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.color_is_favourite
import com.pako2k.banknotescatalog.ui.theme.typographySans



enum class TerMenuOption (@StringRes val textId : Int){
    CURRENCIES (textId = R.string.ter_menu_currencies),
    BANKNOTES (textId = R.string.ter_menu_banknotes),
    STATS (textId = R.string.ter_menu_stats),
    INFO (textId = R.string.ter_menu_info),
}


@Composable
fun TerritoryView(
    windowWidth: WindowWidthSizeClass,
    data : Territory,
    onCountryClick: (territoryID: UInt)->Unit,
    ){
    Log.d(stringResource(id = R.string.app_log_tag),"Start Country")

    val selectedOption = rememberSaveable { mutableIntStateOf(TerMenuOption.CURRENCIES.textId) }

    Surface {
        Column {
            TerritoryHeader(windowWidth, data, false , {})
            Menu(selectedOption.intValue) { selectedOption.intValue = it.textId }
            when (selectedOption.intValue){
                TerMenuOption.CURRENCIES.textId -> {}
                TerMenuOption.BANKNOTES.textId -> {}
                TerMenuOption.STATS.textId -> {}
                TerMenuOption.INFO.textId -> TerritoryInfo(data) {
                    onCountryClick(it)
                }
            }
        }
    }
}

@Composable
fun TerritoryHeader(
    windowWidth: WindowWidthSizeClass,
    data : Territory,
    isFavourite : Boolean,
    onAddFavourite : (Boolean) -> Unit
){
    Surface(
        modifier = Modifier.height(IntrinsicSize.Min)
    ){
        // Background
        Image(
            painter = painterResource(id = R.drawable.background_territory_header),
            contentScale = ContentScale.Crop,
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )

        // Favourite button
        Row (
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.xs_padding))
        ) {
            IconButton(
                onClick = { onAddFavourite(!isFavourite) }
            ) {
                Icon(
                    Icons.Outlined.Star,
                    contentDescription = "Favourite Icon",
                    tint = if (isFavourite) color_is_favourite else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(28.dp)
                )
            }
        }


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

                    val terTitle = data.name
                    val iso3 = if (data.iso3 != null) " - ${data.iso3}" else null
                    Row(
                        modifier = Modifier.padding(end = dimensionResource(id = R.dimen.xxl_padding))
                    ) {
                        Text(
                            text = terTitle,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.displayLarge,
                            maxLines = 1,
                            modifier = Modifier
                                .alignByBaseline()
                                .padding(start = dimensionResource(id = R.dimen.medium_padding))
                        )
                        if (iso3 != null)
                            Text(
                                text = iso3,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.displaySmall,
                                maxLines = 1,
                                modifier = Modifier.alignByBaseline()
                            )
                    }
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
fun Menu(
    selectedOption: Int,
    onClick : (TerMenuOption) -> Unit
){
    Surface(modifier = Modifier.height(IntrinsicSize.Min)) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.background_territory_header),
            contentScale = ContentScale.Crop,
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                .padding(bottom = dimensionResource(id = R.dimen.small_padding))
        ) {
            TerMenuOption.values().forEach{ opt ->
                MenuOption(opt, opt.textId == selectedOption) { onClick(it) }
            }
        }
    }
}

@Composable
fun MenuOption(
    opt : TerMenuOption,
    isSelected : Boolean,
    onClick : (TerMenuOption) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    val buttonWidth = if(screenWidth > 450) 110.dp else if(screenWidth > 370) 80.dp else 1.dp

    val colors = if (isSelected) ButtonDefaults.buttonColors()
        else ButtonDefaults.elevatedButtonColors()

    ElevatedButton(
        elevation = if (!isSelected) ButtonDefaults.buttonElevation(defaultElevation = 4.dp) else null,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.medium_padding)),
        colors = colors,
        modifier = Modifier.defaultMinSize(minHeight = 1.dp, minWidth = buttonWidth),
        onClick = { onClick(opt)}
    ) {
        Text(
            text = stringResource(id = opt.textId),
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.inverseSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun FlagImage(flag : ImageBitmap?, height : Dp){
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

@Composable
fun TerritoryInfo(
    data : Territory,
    onClick : (UInt) -> Unit
){
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Surface (
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.small_padding))
            .verticalScroll(rememberScrollState())
    ){
        Column {
            if (data.description != null)
                ElevatedCard (
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.small_padding))
                        .fillMaxWidth()
                ) {
                    Text(
                        text = data.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier
                            .padding(vertical = dimensionResource(id = R.dimen.medium_padding))
                            .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                    )
                }

            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.medium_padding)))

            // Relations
            val maxItemWidth =
                if (screenWidth < 450) 180.dp
                else 260.dp
            if (data.territoryType.name == TerritoryTypes.TER.value) {
                LinkedTerritories("Belongs to", listOf(data.parentExt!!), maxItemWidth, onClick)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
            }
            if (data.territoryType.name == TerritoryTypes.NR.value) {
                LinkedTerritories("Claimed by", listOf(data.parentExt!!), maxItemWidth, onClick)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
            }

            if (data.predecessorsExt.isNotEmpty()) {
                LinkedTerritories("Preceded by ", data.predecessorsExt, maxItemWidth, onClick)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
            }
            if (data.successorsExt.isNotEmpty()) {
                LinkedTerritories("Succeeded by ", data.successorsExt, maxItemWidth, onClick)
            }

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LinkedTerritories(
    title : String,
    territories: List<Territory>,
    maxItemWidth : Dp,
    onClick: (territoryID: UInt)->Unit,
){
    ElevatedCard (
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.small_padding))
            .fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                .padding(top = dimensionResource(id = R.dimen.medium_padding))
                .padding(bottom = dimensionResource(id = R.dimen.small_padding))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.xs_padding))
            )

            territories.forEach {
                TerritoryBadge(territory = it, maxTextWidth = maxItemWidth, onClick = onClick)
            }
        }
    }
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
    territoryType = TerritoryTypeKey(2u, TerritoryTypes.NR.name),
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
            onCountryClick = { _ -> }
        )
    }
}



