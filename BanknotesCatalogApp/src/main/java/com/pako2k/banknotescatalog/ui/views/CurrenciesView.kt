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
import androidx.compose.ui.unit.Dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.SummaryTable
import com.pako2k.banknotescatalog.data.CurrencySortableField
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI


private const val MIN_FIXED_COLS = 2


@Composable
fun CurrenciesView(
    screenWidth: Dp,
    table : SummaryTable,
    data : List<List<Any?>>,
    sortCallback: (sortBy: CurrencySortableField)->Unit,
    onCurrencyClick: (currencyID: UInt)->Unit,
    onCountryClick: (territoryID: UInt)->Unit
) {
    Log.d(stringResource(id = R.string.app_log_tag), "Start Currencies")

    val totalWidth = (table.columns.sumOf { it.width.value.toDouble() }).toFloat()
    val padding = dimensionResource(id = R.dimen.small_padding)
    val fixedColumns = if (totalWidth > (screenWidth - padding).value) MIN_FIXED_COLS else table.columns.size
    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxWidth()) {
        SummaryTableUI(
            table = table,
            fixedColumns = fixedColumns,
            data = data,
            onHeaderClick = {
                sortCallback(table.columns[it].linkedField as CurrencySortableField)
            },
            onDataClick = { colId, dataId ->
                if (colId == 1)
                    onCurrencyClick(dataId)
                else
                    onCountryClick(dataId)
            },
            modifier = Modifier.padding(padding)
        )
    }
}