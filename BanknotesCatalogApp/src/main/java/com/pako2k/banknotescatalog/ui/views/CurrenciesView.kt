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
import com.pako2k.banknotescatalog.data.TerritoryLink
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI


private const val MIN_FIXED_COLS = 2


@Composable
fun Currencies(
    screenWidth: Dp,
    table : SummaryTable,
    currenciesData : List<Map<String,Any?>>,
    territoriesData : List<Map<String,Any?>>,
    territoriesIndexMap : Map<UInt,Int>,
    sortCallback: (sortBy: CurrencySortableField)->Unit,
    onCurrencyClick: (currencyID: UInt)->Unit,
    onCountryClick: (territoryID: UInt)->Unit
) {
    Log.d(stringResource(id = R.string.app_log_tag), "Start Currencies")

    // Retrieve and set data to be shown
    val dataUI : MutableList<List<Any?>> = mutableListOf()
    for(cur in currenciesData){
        val start = cur["start"].toString()
        val end = cur["end"]?.toString()?:""
        val ownedById = ((cur["ownedBy"] as Array<*>)[0] as TerritoryLink).id
        val ownedByTer = territoriesData[territoriesIndexMap[ownedById]!!]["name"].toString()
        dataUI.add(
            listOf(
                cur["iso3"] ?:"",
                Pair(cur["id"], cur["name"].toString()),
                Pair(ownedById, ownedByTer),
                start.substring(0, minOf(4, start.length)),
                end.substring(0, minOf(4, end.length)),
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
            data = dataUI,
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