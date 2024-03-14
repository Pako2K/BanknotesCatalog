package com.pako2k.banknotescatalog.app

import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.repo.DenomFieldCurrencies
import com.pako2k.banknotescatalog.data.repo.DenomFieldNotes
import com.pako2k.banknotescatalog.data.repo.DenomFieldPrice
import com.pako2k.banknotescatalog.data.repo.DenomFieldTerritories
import com.pako2k.banknotescatalog.data.repo.DenomFieldValue
import com.pako2k.banknotescatalog.data.repo.DenomFieldVariants
import com.pako2k.banknotescatalog.data.repo.SortDirection



data class DenominationUIState(
    val state : ComponentState = ComponentState.DONE,

    val denominationsTable : SummaryTable = SummaryTable(
        columns = listOf(
            SummaryTableColumn(title = "Denomination", linkedField = DenomFieldValue, width = 195.dp, isSortable = true, isClickable = true),
            SummaryTableColumn(title = "Territories", linkedField = DenomFieldTerritories, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Currencies", linkedField = DenomFieldCurrencies, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Note Types", linkedField = DenomFieldNotes, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Variants", linkedField = DenomFieldVariants, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Price", linkedField = DenomFieldPrice, width = 74.dp, isSortable = true)
        ),
        sortedBy = 0,
        sortDirection = SortDirection.ASC,
        minFixedColumns = 1
    ),

    val showStats: Boolean = false,
    val showFilters: Boolean = false,

    val denominationsTableUpdateTrigger : Boolean = false,

    val filterShownIssueYear : FilterDates = FilterDates(null,null),
    val filterAppliedIssueYear : FilterDates = FilterDates(null,null),
)