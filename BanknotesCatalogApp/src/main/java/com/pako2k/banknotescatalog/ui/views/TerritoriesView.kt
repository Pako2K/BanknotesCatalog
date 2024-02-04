package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.SummaryTable
import com.pako2k.banknotescatalog.app.SummaryTableColumn
import com.pako2k.banknotescatalog.data.SortDirection
import com.pako2k.banknotescatalog.data.TerritoryFieldEnd
import com.pako2k.banknotescatalog.data.TerritoryFieldName
import com.pako2k.banknotescatalog.data.TerritoryFieldStart
import com.pako2k.banknotescatalog.data.TerritorySortableField
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


private const val MIN_FIXED_COLS = 2

@Composable
fun TerritoriesView(
    screenWidth: Dp,
    table : SummaryTable,
    data : List<Map<String,Any?>>,
    sortCallback: (sortBy: TerritorySortableField)->Unit,
    onCountryClick: (territoryID: UInt)->Unit,
) {
    Log.d(stringResource(id = R.string.app_log_tag),"Start Countries")

    val uiData : MutableList<List<Any?>> = mutableListOf()

    for(ter in data){
        val terTypSuffix =  if (ter["type"] == "Ind") "" else " [${ter["type"]}]"
        uiData.add(
            listOf(
                ter["flag"],
                ter["iso3"] ?:"",
                Pair(ter["id"], ter["name"].toString() + terTypSuffix),
                ter["start"].toString(),
                ter["end"]?.toString()?:""
            )
        )
    }

    val totalWidth = (table.columns.sumOf { it.width.value.toDouble() }).toFloat()
    val padding = dimensionResource(id = R.dimen.small_padding)
    val fixedColumns = if (totalWidth > (screenWidth - padding).value) MIN_FIXED_COLS else table.columns.size
    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxWidth()) {
        SummaryTableUI(
            table = table,
            fixedColumns = fixedColumns,
            data = uiData,
            onHeaderClick = {
                sortCallback(table.columns[it].linkedField as TerritorySortableField)
                            },
            onDataClick = { _, dataId -> onCountryClick(dataId)},
            modifier = Modifier.padding(padding)
        )
    }
}




private const val TEST_WIDTH = 400

private val summaryTablePreview = SummaryTable(
    columns = listOf(
        SummaryTableColumn(title = "", width = 38.dp, isImage = true ),
        SummaryTableColumn(title = "", width = 44.dp ),
        SummaryTableColumn(title = "Name", linkedField = TerritoryFieldName, width = 210.dp, align = Alignment.CenterStart, isSortable = true, isClickable = true),
        SummaryTableColumn(title = "Founded", linkedField = TerritoryFieldStart, width = 80.dp, isSortable = true),
        SummaryTableColumn(title = "Extinct", linkedField = TerritoryFieldEnd, width = 80.dp, isSortable = true),
    ),
    sortedBy = 3,
    sortDirection = SortDirection.ASC
)

private val dataPreview = listOf<Map<String,Any?>>(
    mapOf("id" to 289u, "iso3" to null, "flag" to null, "name" to "Namibia", "type" to "NR", "start" to 1976, "end" to null),
    mapOf("id" to 1u, "iso3" to "ARG", "flag" to null, "name" to "Argentina", "type" to "Ind", "start" to 1926, "end" to 1967),
    mapOf("id" to 11u, "iso3" to "LAO", "flag" to null, "name" to "Laos", "type" to "Ind", "start" to 926, "end" to null)
)

@Preview(widthDp = TEST_WIDTH)
@Composable
fun CountriesPreview() {
    BanknotesCatalogTheme {
        TerritoriesView(
            screenWidth = TEST_WIDTH.dp,
            table = summaryTablePreview,
            data = dataPreview,
            onCountryClick = {},
            sortCallback = {}
        )
    }
}
