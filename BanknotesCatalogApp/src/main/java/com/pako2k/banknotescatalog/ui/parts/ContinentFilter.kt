package com.pako2k.banknotescatalog.ui.parts

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContinentFilter (
    windowWidth: WindowWidthSizeClass,
    continents: List<Continent>,
    selectedContinentId: UInt? = null,
    onclick: (clickedContinent : UInt) -> Unit
){
    Log.d(stringResource(id = R.string.app_log_tag),"Start ContinentFilter")

    val columns = if (windowWidth == WindowWidthSizeClass.Compact) 3 else 6

    Column {
        Divider(
            thickness = 2.dp,
            color = Color.DarkGray
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_continents_filter),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
            FlowRow(
                horizontalArrangement = Arrangement.SpaceEvenly,
                maxItemsInEachRow = columns,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.medium_padding))
            ) {
                for (cont in continents) {
                    ContinentItem(
                        continent = cont,
                        selected = cont.id == selectedContinentId,
                        onClick = onclick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}



@Composable
fun ContinentItem(continent: Continent,
                  selected : Boolean,
                  modifier: Modifier = Modifier,
                  onClick: (clickedContinent: UInt) -> Unit
) {
    val selectedModifier : Modifier =
        if (selected)
            modifier
                .background(
                    brush = Brush.horizontalGradient(
                        0.0f to MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                        1f to MaterialTheme.colorScheme.secondary
                    ),
                    shape = MaterialTheme.shapes.small
                )
                .border(
                    border = BorderStroke(Dp.Hairline, color = Color.Black),
                    shape = MaterialTheme.shapes.small
                )
        else
            modifier

    Box (contentAlignment = Alignment.Center, modifier = modifier){
        TextButton(
            contentPadding = PaddingValues(0.dp),
            onClick = {
                onClick(continent.id)
            },
            modifier = selectedModifier
                .fillMaxWidth(0.9f)
                .height(dimensionResource(id = R.dimen.continent_item_height)),
        ) {
            Text(
                text = continent.name,
                overflow = TextOverflow.Ellipsis,
                color = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = if (selected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelLarge.copy(
                    shadow = null
                )
            )
        }
    }
}



private const val TEST_WIDTH = 400

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(widthDp = TEST_WIDTH)
@Composable
fun ContinentFilterPreview() {
    BanknotesCatalogTheme {
        ContinentFilter(WindowSizeClass.calculateFromSize(size = DpSize(TEST_WIDTH.dp,600.dp)).widthSizeClass,
            continents = listOf(
                Continent(1u,"Africa"),
                Continent(2u,"North America"),
                Continent(3u,"South America"),
                Continent(4u,"Asia"),
                Continent(5u,"Oceania"),
                Continent(6u, "Europe")
            ),
            onclick = {})
    }
}