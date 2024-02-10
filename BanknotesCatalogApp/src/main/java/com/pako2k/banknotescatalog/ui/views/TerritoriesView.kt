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
    data : List<List<Any?>>,
    sortCallback: (sortBy: TerritorySortableField)->Unit,
    onCountryClick: (territoryID: UInt)->Unit,
) {
    Log.d(stringResource(id = R.string.app_log_tag),"Start Countries")

    val totalWidth = (table.columns.sumOf { it.width.value.toDouble() }).toFloat()
    val padding = dimensionResource(id = R.dimen.small_padding)
    val fixedColumns = if (totalWidth > (screenWidth - padding).value) MIN_FIXED_COLS else table.columns.size
    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxWidth()) {
        SummaryTableUI(
            table = table,
            fixedColumns = fixedColumns,
            data = data,
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

private val dataPreview = listOf(
    listOf(null, null, Pair(8u, "Namibia [NR]"), "1976", ""),
    listOf(null, "ARG",Pair(1u, "Argentina"), "1926", "1967"),
    listOf(null, "LAO",Pair(2u, "Laos"), "926", "")
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
