package com.pako2k.banknotescatalog.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.data.CurrencyFieldDenominations
import com.pako2k.banknotescatalog.data.CurrencyFieldEnd
import com.pako2k.banknotescatalog.data.CurrencyFieldIssues
import com.pako2k.banknotescatalog.data.CurrencyFieldName
import com.pako2k.banknotescatalog.data.CurrencyFieldNotes
import com.pako2k.banknotescatalog.data.CurrencyFieldOwnedBy
import com.pako2k.banknotescatalog.data.CurrencyFieldPrice
import com.pako2k.banknotescatalog.data.CurrencyFieldStart
import com.pako2k.banknotescatalog.data.CurrencyFieldVariants
import com.pako2k.banknotescatalog.data.SortDirection
import com.pako2k.banknotescatalog.data.TerritoryFieldCurrencies
import com.pako2k.banknotescatalog.data.TerritoryFieldDenominations
import com.pako2k.banknotescatalog.data.TerritoryFieldEnd
import com.pako2k.banknotescatalog.data.TerritoryFieldIssues
import com.pako2k.banknotescatalog.data.TerritoryFieldName
import com.pako2k.banknotescatalog.data.TerritoryFieldNotes
import com.pako2k.banknotescatalog.data.TerritoryFieldPrice
import com.pako2k.banknotescatalog.data.TerritoryFieldStart
import com.pako2k.banknotescatalog.data.TerritoryFieldVariants

private const val STATS_COL_WIDTH = 52


data class MainUiState (
    val selectedContinent: UInt? = null,

    val userLoggedIn : Boolean = false,

    val summaryTableSortingFlag : Boolean = false,

    // Summary tables (updated when sorting is changed)
    val territoriesTable : SummaryTable = SummaryTable(
        columns = listOf(
            SummaryTableColumn(title = "", width = 36.dp, isImage = true ),
            SummaryTableColumn(title = "", width = 44.dp ),
            SummaryTableColumn(title = "Name", linkedField = TerritoryFieldName, width = 200.dp, align = Arrangement.Start, isSortable = true, isClickable = true),
            SummaryTableColumn(title = "Founded", linkedField = TerritoryFieldStart, width = 74.dp, isSortable = true),
            SummaryTableColumn(title = "Extinct", linkedField = TerritoryFieldEnd, width = 70.dp, isSortable = true),
            SummaryTableColumn(title = "Currencies", linkedField = TerritoryFieldCurrencies, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Issues", linkedField = TerritoryFieldIssues, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Face Values", linkedField = TerritoryFieldDenominations, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Note Types", linkedField = TerritoryFieldNotes, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Variants", linkedField = TerritoryFieldVariants, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Price", linkedField = TerritoryFieldPrice, width = 74.dp, isSortable = true)
        ),
        sortedBy = 2,
        sortDirection = SortDirection.ASC,
        minFixedColumns = 3
    ),

    val currenciesTable : SummaryTable = SummaryTable(
        columns = listOf(
            SummaryTableColumn(title = "", width = 44.dp ),
            SummaryTableColumn(title = "Name", linkedField = CurrencyFieldName, width = 140.dp, align = Arrangement.Start, isSortable = true, isClickable = true),
            SummaryTableColumn(title = "Territory", linkedField = CurrencyFieldOwnedBy, width = 160.dp, align = Arrangement.Start, isSortable = true, isClickable = true),
            SummaryTableColumn(title = "Created", linkedField = CurrencyFieldStart, width = 74.dp, isSortable = true),
            SummaryTableColumn(title = "Finished", linkedField = CurrencyFieldEnd, width = 70.dp, isSortable = true),
            SummaryTableColumn(title = "Issues", linkedField = CurrencyFieldIssues, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Face Values", linkedField = CurrencyFieldDenominations, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Note Types", linkedField = CurrencyFieldNotes, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Variants", linkedField = CurrencyFieldVariants, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Price", linkedField = CurrencyFieldPrice, width = 74.dp, isSortable = true)
        ),
        sortedBy = 1,
        sortDirection = SortDirection.ASC,
        minFixedColumns = 2
    ),

    val showTerritoryStats: Boolean = false,
    val showCurrencyStats: Boolean = false

)
