package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.IssueYearViewModel
import com.pako2k.banknotescatalog.data.repo.IssueYearSortableField
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI


@Composable
fun IssueYearsView(
    viewModel: IssueYearViewModel,
    selectedContinent : UInt?,
    width: Dp,
) {
    Log.d(stringResource(id = R.string.app_log_tag), "Start Issue Years")

    // initializationState as state, to trigger recompositions of the whole UI
    val uiState by viewModel.issueYearUIState.collectAsState()

    SummaryTableUI(
        table = uiState.yearsTable,
        availableWidth = width,
        data = viewModel.issueYearsViewDataUI,
        onHeaderClick = { colId, statsCol ->
            viewModel.sortIssueYearsBy(uiState.yearsTable.columns[colId].linkedField as IssueYearSortableField, statsCol, selectedContinent)
        },
        onDataClick = {_,_ ->}
    )
}