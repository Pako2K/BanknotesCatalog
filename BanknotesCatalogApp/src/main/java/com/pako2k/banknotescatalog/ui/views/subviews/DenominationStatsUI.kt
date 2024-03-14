package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.stats.DenominationSummaryStats
import com.pako2k.banknotescatalog.ui.common.rightBorder
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.StatsTableData
import com.pako2k.banknotescatalog.ui.parts.StatsTableHeader
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme



@Composable
fun DenominationStatsUI(
    denominationDateFilter : FilterDates,
    continentName : String?,
    data : DenominationSummaryStats,
    isLogged : Boolean,
    onClose : () -> Unit
){
    var suffix = if(continentName != null) " - $continentName" else ""
    suffix += if (denominationDateFilter.from != null && denominationDateFilter.to != null)
        " (${denominationDateFilter.from} - ${denominationDateFilter.to})"
    else if (denominationDateFilter.from != null)
        " (from ${denominationDateFilter.from})"
    else if (denominationDateFilter.to != null)
        " (until ${denominationDateFilter.to})"
    else
        ""

    CommonCard(
        title = "Denominations $suffix",
        onClose = onClose
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatsTable(data, isLogged)
        }
    }
}


@Composable
private fun StatsTable(
    data : DenominationSummaryStats,
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
            modifier = Modifier
                .padding(1.dp)
                .border(Dp.Hairline, Color.Gray)
                .background(MaterialTheme.colorScheme.secondary)
                .verticalScroll(rememberScrollState())
        ){
            Row {
                // Data Columns
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ) {
                    StatsTableHeader("Existing", subtitles, isLoggedIn)
                    StatsTableData(0, listOf(data.current.total,data.current.collection), isLoggedIn)
                }
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ) {
                    StatsTableHeader("Extinct", subtitles, isLoggedIn)
                    StatsTableData(0, listOf(data.extinct.total,data.extinct.collection), isLoggedIn)
                }
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max)
                ) {
                    StatsTableHeader("Total", subtitles, isLoggedIn)
                    StatsTableData(0, listOf(data.total.total,data.total.collection), isLoggedIn)
                }
            }
        }
    }
}





private const val PREVIEW_WIDTH = 380
private const val PREVIEW_HEIGHT = 800

private val testData1 = DenominationSummaryStats(
    current = DenominationSummaryStats.Data(
        total = 122, collection = 12
    ),
    extinct = DenominationSummaryStats.Data(
        total = 22, collection = 1
    )
)



@Preview(widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun DenominationStatsUIPreviewPortrait() {
    BanknotesCatalogTheme {
        DenominationStatsUI (FilterDates(null, null), "Africa", testData1, isLogged = true) { }
    }
}

@Preview(widthDp = PREVIEW_HEIGHT, heightDp = 340)
@Composable
private fun DenominationStatsUIPreviewLandscape() {
    BanknotesCatalogTheme {
        Surface {
            DenominationStatsUI (FilterDates(null, null), "Africa", testData1, isLogged = true) { }
        }
    }
}
