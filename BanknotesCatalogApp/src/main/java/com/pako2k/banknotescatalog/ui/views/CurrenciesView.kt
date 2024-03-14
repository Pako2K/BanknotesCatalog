package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.StatsSubColumn
import com.pako2k.banknotescatalog.app.SummaryTable
import com.pako2k.banknotescatalog.data.repo.CurrencySortableField
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI


@Composable
fun CurrenciesView(
    width: Dp,
    table : SummaryTable,
    data : List<List<Any?>>,
    sortCallback: (sortBy: CurrencySortableField, statCol : StatsSubColumn?)->Unit,
    onCurrencyClick: (currencyID: UInt)->Unit,
    onCountryClick: (territoryID: UInt)->Unit
) {
    Log.d(stringResource(id = R.string.app_log_tag), "Start Currencies")

    SummaryTableUI(
        table = table,
        availableWidth = width,
        data = data,
        onHeaderClick = { colId, statsCol ->
            sortCallback(table.columns[colId].linkedField as CurrencySortableField, statsCol)
        },
        onDataClick = { colId, dataId ->
            if (colId == 1)
                onCurrencyClick(dataId)
            else
                onCountryClick(dataId)
        }
    )
}