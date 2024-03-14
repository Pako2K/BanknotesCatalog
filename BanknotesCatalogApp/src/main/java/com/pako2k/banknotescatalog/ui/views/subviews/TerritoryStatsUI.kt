package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.stats.TerritorySummaryStats
import com.pako2k.banknotescatalog.data.stats.plus
import com.pako2k.banknotescatalog.ui.common.rightBorder
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.StatsTableData
import com.pako2k.banknotescatalog.ui.parts.StatsTableFooterData
import com.pako2k.banknotescatalog.ui.parts.StatsTableHeader
import com.pako2k.banknotescatalog.ui.parts.StatsTableRowTitles
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import kotlinx.coroutines.launch


@Composable
fun TerritoryStatsUI (
    data : Map<String, TerritorySummaryStats>,
    continentName : String?,
    isLoggedIn : Boolean,
    onClose : () -> Unit
){
    val continentSuffix =
        if(continentName != null) " - $continentName"
        else ""

    CommonCard(
        title = "Territories in Catalog$continentSuffix",
        onClose = onClose
    ) {
        StatsTable(data, isLoggedIn)
    }
}


@Composable
private fun StatsTable(
    data : Map<String, TerritorySummaryStats>,
    isLoggedIn : Boolean
){
    val hScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val subtitles = setOf("Total", "Issuer", "Collec.")

    // Initial box just for the shadow
    Box(
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.small_padding))
            .shadow(elevation = 1.dp, ambientColor = Color.Black)
    ) {
        // Inside Box for the overall background
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .padding(1.dp)
                .border(Dp.Hairline, Color.Gray)
                .background(MaterialTheme.colorScheme.secondary)
                .verticalScroll(rememberScrollState())
        ){
            Row {
                // Fixed 1st Column
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ){
                    StatsTableHeader("", setOf(""), isLoggedIn)
                    StatsTableRowTitles(data.keys)
                    StatsTableHeader("TOTAL", setOf(), isLoggedIn, TextAlign.End)
                }
                // END Fixed First Column

                // Scrollable Columns
                Row (
                    modifier = Modifier.horizontalScroll(hScrollState)
                ){
                    Column(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .rightBorder(Color.White)
                    ){
                        StatsTableHeader("Existing", subtitles, isLoggedIn)
                        var sum = TerritorySummaryStats.Data(0,0)
                        data.onEachIndexed{ index, type ->
                            StatsTableData(
                                index,
                                listOf(type.value.current.total, type.value.current.issuer, type.value.current.collection),
                                isLoggedIn
                                )
                            sum += type.value.current
                        }
                        StatsTableFooterData(
                            listOf(sum.total, sum.issuer, sum.collection),
                            isLoggedIn
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .rightBorder(Color.White)
                    ){
                        StatsTableHeader("Extinct", subtitles, isLoggedIn)
                        var sum = TerritorySummaryStats.Data(0,0)
                        data.onEachIndexed{ index, type ->
                            StatsTableData(
                                index,
                                listOf(type.value.extinct.total, type.value.extinct.issuer, type.value.extinct.collection),
                                isLoggedIn
                            )
                            sum += type.value.extinct
                        }
                        StatsTableFooterData(
                            listOf(sum.total, sum.issuer, sum.collection),
                            isLoggedIn
                        )
                    }
                    Column(
                        modifier = Modifier.width(IntrinsicSize.Max)
                    ){
                        StatsTableHeader("Total", subtitles, isLoggedIn)
                        var sum = TerritorySummaryStats.Data(0,0)
                        data.onEachIndexed{ index, type ->
                            StatsTableData(
                                index,
                                listOf(type.value.total.total, type.value.total.issuer, type.value.total.collection),
                                isLoggedIn
                            )
                            sum += type.value.total
                        }
                        StatsTableFooterData(
                            listOf(sum.total, sum.issuer, sum.collection),
                            isLoggedIn
                        )
                    }
                }

            }
            if ( hScrollState.value < (hScrollState.maxValue - 20))
                Icon(
                    painter = painterResource(R.drawable.double_arrow_right_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(0.8f)
                        .offset(x = dimensionResource(id = R.dimen.small_padding))
                        .clickable {
                            coroutineScope.launch {
                                hScrollState.scrollTo(hScrollState.maxValue)
                            }
                        }
                )
        }
    }
}




private const val PREVIEW_WIDTH = 380
private const val PREVIEW_HEIGHT = 800

private val testData1 = TerritorySummaryStats(
    current = TerritorySummaryStats.Data(
        total = 122, issuer = 111, collection = 12
    ),
    extinct = TerritorySummaryStats.Data(
        total = 22, issuer = 11, collection = 1
    )
)

private val testData2 = TerritorySummaryStats(
    current = TerritorySummaryStats.Data(
        total = 22, issuer = 11, collection = 1
    ),
    extinct = TerritorySummaryStats.Data(
        total = 22, issuer = 11, collection = 1
    )
)

private val testData = mapOf(
    "Independent" to testData1,
    "Not recognized State" to testData2,
    "Territory" to testData2,
)

@Preview(widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun TerritoryStatsUIPreviewPortrait() {
    BanknotesCatalogTheme {
        TerritoryStatsUI (testData, "Africa",true) { }
    }
}

@Preview(widthDp = PREVIEW_HEIGHT, heightDp = 340)
@Composable
private fun TerritoryStatsUIPreviewLandscape() {
    BanknotesCatalogTheme {
        Surface {
            TerritoryStatsUI (testData, null,false) { }
        }
    }
}