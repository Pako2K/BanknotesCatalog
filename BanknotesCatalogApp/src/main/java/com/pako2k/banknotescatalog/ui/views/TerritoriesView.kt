package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.StatsSubColumn
import com.pako2k.banknotescatalog.app.SummaryTable
import com.pako2k.banknotescatalog.app.SummaryTableColumn
import com.pako2k.banknotescatalog.data.repo.SortDirection
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldEnd
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldName
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldStart
import com.pako2k.banknotescatalog.data.repo.TerritorySortableField
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


@Composable
fun TerritoriesView(
    width: Dp,
    table : SummaryTable,
    isLogged : Boolean,
    data : List<List<Any?>>,
    sortCallback: (sortBy: TerritorySortableField, statCol : StatsSubColumn?)->Unit,
    onCountryClick: (territoryID: UInt)->Unit,
) {
    Log.d(stringResource(id = R.string.app_log_tag),"Start Countries")

    SummaryTableUI(
        table = table,
        availableWidth = width,
        data = data,
        isLogged = isLogged,
        onHeaderClick = { colId, statsCol ->
                sortCallback(table.columns[colId].linkedField as TerritorySortableField, statsCol)
                        },
        onDataClick = { _, dataId -> onCountryClick(dataId)}
    )
}




private const val TEST_WIDTH = 400

private val summaryTablePreview = SummaryTable(
    columns = listOf(
        SummaryTableColumn(title = "", width = 38.dp, isImage = true ),
        SummaryTableColumn(title = "", width = 44.dp ),
        SummaryTableColumn(title = "Name", linkedField = TerritoryFieldName, width = 210.dp, align = Arrangement.Start, isSortable = true, isClickable = true),
        SummaryTableColumn(title = "Founded", linkedField = TerritoryFieldStart, width = 80.dp, isSortable = true),
        SummaryTableColumn(title = "Extinct", linkedField = TerritoryFieldEnd, width = 80.dp, isSortable = true),
    ),
    sortedBy = 3,
    sortDirection = SortDirection.ASC,
    minFixedColumns = 3
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
            width = TEST_WIDTH.dp,
            table = summaryTablePreview,
            data = dataPreview,
            isLogged = false,
            onCountryClick = {},
            sortCallback = {_,_->}
        )
    }
}
