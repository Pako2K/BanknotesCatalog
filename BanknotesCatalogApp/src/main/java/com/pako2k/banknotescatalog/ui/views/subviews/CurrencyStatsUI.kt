package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.stats.CurrencySummaryStats
import com.pako2k.banknotescatalog.ui.common.rightBorder
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.StatsTableData
import com.pako2k.banknotescatalog.ui.parts.StatsTableHeader
import com.pako2k.banknotescatalog.ui.parts.StatsTableRowTitles
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme



@Composable
fun CurrencyStatsUI (
    data : Map<String, CurrencySummaryStats>,
    continentName : String?,
    isLoggedIn : Boolean,
    onClose : () -> Unit
){
    val continentSuffix =
        if(continentName != null) " - $continentName"
        else ""

    CommonCard(
        title = "Currencies in Catalog$continentSuffix",
        onClose = onClose
    ) {
        StatsTable(data, isLoggedIn)
    }
}

@Composable
private fun StatsTable(
    data : Map<String, CurrencySummaryStats>,
    isLoggedIn : Boolean
){
    val subtitles = setOf("Catalog","Collec.")

    // Initial box just for the shadow
    Box(
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
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
                // 1st Column
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ) {
                    StatsTableHeader("", setOf(""), isLoggedIn)
                    StatsTableRowTitles(data.keys)
                }

                // Data Columns
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ) {
                    StatsTableHeader("Existing", subtitles, isLoggedIn)
                    data.onEachIndexed { index, type ->
                        StatsTableData(
                            index,
                            listOf(type.value.current.total,type.value.current.collection),
                            isLoggedIn
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ) {
                    StatsTableHeader("Extinct", subtitles, isLoggedIn)
                    data.onEachIndexed { index, type ->
                        StatsTableData(
                            index,
                            listOf(type.value.extinct.total,type.value.extinct.collection),
                            isLoggedIn
                        )
                    }
                }
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max)
                ) {
                    StatsTableHeader("Total", subtitles, isLoggedIn)
                    data.onEachIndexed { index, type ->
                        StatsTableData(
                            index,
                            listOf(type.value.total.total,type.value.total.collection),
                            isLoggedIn
                        )
                    }
                }
            }
        }
    }
}




private const val PREVIEW_WIDTH = 380
private const val PREVIEW_HEIGHT = 800

private val testData1 = CurrencySummaryStats(
    current = CurrencySummaryStats.Data(
        total = 122, collection = 12
    ),
    extinct = CurrencySummaryStats.Data(
        total = 22, collection = 1
    )
)

private val testData2 = CurrencySummaryStats(
    current = CurrencySummaryStats.Data(
        total = 22, collection = 1
    ),
    extinct = CurrencySummaryStats.Data(
        total = 22, collection = 1
    )
)

private val testData = mapOf(
    "Total" to testData1,
    "Shared" to testData2,
)

@Preview(widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun TerritoryStatsUIPreviewPortrait() {
    BanknotesCatalogTheme {
        CurrencyStatsUI(testData, null,true) {}
    }
}

@Preview(widthDp = PREVIEW_HEIGHT, heightDp = 340)
@Composable
private fun TerritoryStatsUIPreviewLandscape() {
    BanknotesCatalogTheme {
        Surface {
            CurrencyStatsUI (testData, "Europe",false) { }
        }
    }
}