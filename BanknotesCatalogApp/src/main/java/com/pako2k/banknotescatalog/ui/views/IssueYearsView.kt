package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.IssueYearViewModel
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.repo.IssueYearSortableField
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI
import com.pako2k.banknotescatalog.ui.views.subviews.IssueYearFiltersUI
import com.pako2k.banknotescatalog.ui.views.subviews.IssueYearStatsUI


@Composable
fun IssueYearsView(
    viewModel: IssueYearViewModel,
    selectedContinent : Continent?,
    isLogged : Boolean,
    width: Dp,
) {
    Log.d(stringResource(id = R.string.app_log_tag), "Start Issue Years")

    // initializationState as state, to trigger recompositions of the whole UI
    val uiState by viewModel.issueYearUIState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.small_padding))
    ) {

        if (uiState.showIssueYearStats) {
            IssueYearStatsUI(
                issueYearDateFilter = uiState.filterAppliedIssueYear,
                data = viewModel.issueYearSummaryStats,
                continentName = selectedContinent?.name,
                isLogged = isLogged,
                onClose = {
                    viewModel.showStats(false)
                }
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
        }
        if (uiState.showIssueYearFilters) {
            IssueYearFiltersUI(
                dates = uiState.filterShownIssueYear,
                onChangedDates = {
                    viewModel.updateFilterIssueYearDates(it, selectedContinent?.id)
                },
                onClose = { viewModel.showFilters(false) }
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
        }

        SummaryTableUI(
            table = uiState.yearsTable,
            availableWidth = width,
            data = viewModel.issueYearsViewDataUI,
            isLogged = isLogged,
            onHeaderClick = { colId, statsCol ->
                viewModel.sortIssueYearsBy(
                    uiState.yearsTable.columns[colId].linkedField as IssueYearSortableField,
                    statsCol,
                    selectedContinent?.id
                )
            },
            onDataClick = { _, _ -> }
        )
    }
}