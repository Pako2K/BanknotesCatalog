package com.pako2k.banknotescatalog.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.TerritoryTypeEnum
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldDenominations
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldEnd
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldIssues
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldName
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldNotes
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldOwnedBy
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldPrice
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldStart
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldVariants
import com.pako2k.banknotescatalog.data.repo.SortDirection
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldCurrencies
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldDenominations
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldEnd
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldIssues
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldName
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldNotes
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldPrice
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldStart
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldVariants


data class MainUiInitializationState (
    val state : ComponentState = ComponentState.LOADING,
)


data class MainUiState (
    val selectedContinent: UInt? = null,

    val userLoggedIn : Boolean = false,

    val summaryTableTriggerUpdateFlag : Boolean = false,

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
            SummaryTableColumn(title = "Name", linkedField = CurrencyFieldName, width = 115.dp, align = Arrangement.Start, isSortable = true, isClickable = true),
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
    val showCurrencyStats: Boolean = false,

    val showTerritoryFilters : Boolean = false,
    val showCurrencyFilters: Boolean = false,

    val filterTerritoryTypes : Map<TerritoryTypeEnum, Boolean> = TerritoryTypeEnum.values().associateWith { true },
    val filterTerritoryState : Pair<Boolean,Boolean> = Pair(true,true),
    val filterTerFounded : FilterDates = FilterDates(null,null),
    val filterTerExtinct : FilterDates = FilterDates(null,null),

    val filterCurrencyTypes : Pair<Boolean,Boolean> = Pair(true,true),
    val filterCurrencyState : Pair<Boolean,Boolean> = Pair(true,true),
    val filterCurFounded : FilterDates = FilterDates(null,null),
    val filterCurExtinct : FilterDates = FilterDates(null,null),
)
