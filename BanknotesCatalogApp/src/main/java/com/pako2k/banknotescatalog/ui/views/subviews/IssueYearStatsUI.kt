package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.stats.IssueYearSummaryStats
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.StatsTableData
import com.pako2k.banknotescatalog.ui.parts.StatsTableHeader


@Composable
fun IssueYearStatsUI(
    issueYearDateFilter : FilterDates,
    continentName : String?,
    data : IssueYearSummaryStats,
    isLogged : Boolean,
    onClose : () -> Unit
){
    var suffix = if(continentName != null) " - $continentName" else ""
    suffix += if (issueYearDateFilter.from != null && issueYearDateFilter.to != null)
        " (${issueYearDateFilter.from} - ${issueYearDateFilter.to})"
    else if (issueYearDateFilter.from != null)
        " (from ${issueYearDateFilter.from})"
    else if (issueYearDateFilter.to != null)
        " (until ${issueYearDateFilter.to})"
    else
        ""

    CommonCard(
        title = "Issue Years$suffix",
        onClose = onClose
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatsTable(data, isLogged)
        }
    }
}


@Composable
private fun StatsTable(
    data : IssueYearSummaryStats,
    isLogged : Boolean
){
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
                // Data Columns
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                ) {
                    StatsTableHeader("Total", setOf("Catalog","Collec."),isLogged)
                    StatsTableData(0, listOf(data.total.total,data.total.collection), isLogged)
                }
            }
        }
    }
}

