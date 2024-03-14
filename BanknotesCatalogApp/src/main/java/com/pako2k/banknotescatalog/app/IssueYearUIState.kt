package com.pako2k.banknotescatalog.app

import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldCurrencies
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldDenominations
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldIssues
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldNotes
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldPrice
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldTerritories
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldValue
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldVariants
import com.pako2k.banknotescatalog.data.repo.SortDirection


data class IssueYearUIState(
    val yearsTable : SummaryTable = SummaryTable(
        columns = listOf(
            SummaryTableColumn(title = "Issue Year", linkedField = IssueYearFieldValue, width = 90.dp, isSortable = true, isClickable = true),
            SummaryTableColumn(title = "Territories", linkedField = IssueYearFieldTerritories, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Currencies", linkedField = IssueYearFieldCurrencies, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Issues", linkedField = IssueYearFieldIssues, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Face Values", linkedField = IssueYearFieldDenominations, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Note Types", linkedField = IssueYearFieldNotes, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Variants", linkedField = IssueYearFieldVariants, isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
            SummaryTableColumn(title = "Price", linkedField = IssueYearFieldPrice, width = 74.dp, isSortable = true)
        ),
        sortedBy = 0,
        sortDirection = SortDirection.ASC,
        minFixedColumns = 1
    ),

    val yearsTableUpdateTrigger : Boolean = false,

    val filterShownIssueYear : FilterDates = FilterDates(null,null),
    val filterAppliedIssueYear : FilterDates = FilterDates(null,null),
)