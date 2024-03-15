package com.pako2k.banknotescatalog.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.data.FilterDates
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


data class CurrencyUIState(

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

    val showStats: Boolean = false,
    val showFilters: Boolean = false,

    val currenciesTableUpdateTrigger : Boolean = false,

    val filterShownIssueYear : FilterDates = FilterDates(null,null),
    val filterAppliedIssueYear : FilterDates = FilterDates(null,null),

    val filterCurrencyTypes : Pair<Boolean,Boolean> = Pair(true,true),
    val filterCurrencyState : Pair<Boolean,Boolean> = Pair(true,true),
    val filterCurFounded : FilterDates = FilterDates(null,null),
    val filterCurExtinct : FilterDates = FilterDates(null,null)
)