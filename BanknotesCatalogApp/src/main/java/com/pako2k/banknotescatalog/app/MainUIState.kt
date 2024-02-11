package com.pako2k.banknotescatalog.app

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.data.CurrencyFieldEnd
import com.pako2k.banknotescatalog.data.CurrencyFieldName
import com.pako2k.banknotescatalog.data.CurrencyFieldOwnedBy
import com.pako2k.banknotescatalog.data.CurrencyFieldStart
import com.pako2k.banknotescatalog.data.SortDirection
import com.pako2k.banknotescatalog.data.TerritoryFieldEnd
import com.pako2k.banknotescatalog.data.TerritoryFieldName
import com.pako2k.banknotescatalog.data.TerritoryFieldStart


data class MainUiState (
    val selectedContinent: UInt? = null,

    val userLoggedIn : Boolean = false,

    val favouriteTerritories : List<UInt> = listOf(),

    val summaryTableSortingFlag : Boolean = false,
    val territoriesTable : SummaryTable = SummaryTable(
        columns = listOf(
            SummaryTableColumn(title = "", width = 38.dp, isImage = true ),
            SummaryTableColumn(title = "", width = 44.dp ),
            SummaryTableColumn(title = "Name", linkedField = TerritoryFieldName, width = 210.dp, align = Alignment.CenterStart, isSortable = true, isClickable = true),
            SummaryTableColumn(title = "Founded", linkedField = TerritoryFieldStart, width = 80.dp, isSortable = true),
            SummaryTableColumn(title = "Extinct", linkedField = TerritoryFieldEnd, width = 80.dp, isSortable = true),
        ),
        sortedBy = 2,
        sortDirection = SortDirection.ASC
    ),

    val currenciesTable : SummaryTable = SummaryTable(
        columns = listOf(
            SummaryTableColumn(title = "", width = 44.dp ),
            SummaryTableColumn(title = "Name", linkedField = CurrencyFieldName, width = 150.dp, align = Alignment.CenterStart, isSortable = true, isClickable = true),
            SummaryTableColumn(title = "Territory", linkedField = CurrencyFieldOwnedBy, width = 180.dp, align = Alignment.CenterStart, isSortable = true, isClickable = true),
            SummaryTableColumn(title = "Created", linkedField = CurrencyFieldStart, width = 75.dp, isSortable = true),
            SummaryTableColumn(title = "Finished", linkedField = CurrencyFieldEnd, width = 75.dp, isSortable = true),
        ),
        sortedBy = 1,
        sortDirection = SortDirection.ASC
    ),

)
