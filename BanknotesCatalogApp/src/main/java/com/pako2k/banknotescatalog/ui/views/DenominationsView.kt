package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.ComponentState
import com.pako2k.banknotescatalog.app.DenominationViewModel
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.repo.DenomSortableField
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI
import com.pako2k.banknotescatalog.ui.views.subviews.DenominationFiltersUI
import com.pako2k.banknotescatalog.ui.views.subviews.DenominationStatsUI


@Composable
fun DenominationsView(
    viewModel : DenominationViewModel,
    isLogged : Boolean,
    selectedContinent : Continent?,
    width: Dp
) {
    Log.d(stringResource(id = R.string.app_log_tag), "Start Denominations")

    // initializationState as state, to trigger recompositions of the whole UI
    val uiState by viewModel.denominationUIState.collectAsState()
    val padding = dimensionResource(id = R.dimen.small_padding)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(padding)
    ) {
        if (uiState.showStats) {
            DenominationStatsUI(
                denominationDateFilter = uiState.filterAppliedIssueYear,
                data = viewModel.getDenominationStats(),
                continentName = selectedContinent?.name,
                isLogged = isLogged,
                onClose = {
                    viewModel.showStats(false)
                }
            )
            Spacer(modifier = Modifier.height(padding))
        }
        if (uiState.showFilters) {
            DenominationFiltersUI(
                dates = uiState.filterShownIssueYear,
                onChangedDates = {
                    viewModel.updateFilterIssueYearDates(it, selectedContinent?.id)
                },
                onClose = { viewModel.showFilters(false) }
            )
            Spacer(modifier = Modifier.height(padding))
        }
        when (uiState.state) {
            ComponentState.LOADING -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .width(44.dp)
                    )
                }
            }
            ComponentState.DONE -> {
                SummaryTableUI(
                    table = uiState.denominationsTable,
                    availableWidth = width,
                    data = viewModel.denominationsViewDataUI,
                    isLogged = isLogged,
                    onHeaderClick = { colId, statsCol ->
                        viewModel.sortDenominationsBy(
                            uiState.denominationsTable.columns[colId].linkedField as DenomSortableField,
                            statsCol,
                            selectedContinent?.id
                        )
                    },
                    onDataClick = { _, _ -> }
                )
            }

            ComponentState.FAILED -> {
                Text(
                    text = stringResource(id = R.string.connection_error),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.medium_padding))
                )
            }
        }
    }
}