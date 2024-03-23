package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.CurrencyViewModel
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.repo.CurrencySortableField
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI
import com.pako2k.banknotescatalog.ui.views.subviews.CurrencyFiltersUI
import com.pako2k.banknotescatalog.ui.views.subviews.CurrencyStatsUI


@Composable
fun CurrenciesView(
    viewModel : CurrencyViewModel,
    isLogged : Boolean,
    selectedContinent : Continent?,
    width: Dp,
    windowHeightClass : WindowHeightSizeClass,
    onCurrencyClick: (currencyID: UInt)->Unit,
    onCountryClick: (territoryID: UInt)->Unit
) {
    Log.d(stringResource(id = R.string.app_log_tag), "Start Currencies")

    // initializationState as state, to trigger recompositions of the whole UI
    val uiState by viewModel.currencyUIState.collectAsState()
    val padding = dimensionResource(id = R.dimen.small_padding)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(padding)
    ) {
        if (uiState.showStats) {
            CurrencyStatsUI(
                data = viewModel.getCurrencyStats(),
                continentName = selectedContinent?.name,
                isLoggedIn = isLogged,
                onClose = {
                    viewModel.showStats(false)
                }
            )
            Spacer(modifier = Modifier.height(padding))
        }
        if (uiState.showFilters) {
            CurrencyFiltersUI(
                curTypeFilters = uiState.filterCurrencyTypes,
                curStateFilters = uiState.filterCurrencyState,
                curFoundedFilter = uiState.filterCurFounded,
                curExtinctFilter = uiState.filterCurExtinct,
                onCurTypeChanged = { viewModel.updateFilterCurrencyType(it, selectedContinent?.id) },
                onCurStateChanged = { viewModel.updateFilterCurrencyState(it, selectedContinent?.id) },
                onCurFoundedChanged = { viewModel.updateFilterCurrencyFoundedDates(it,selectedContinent?.id) },
                onCurExtinctChanged = { viewModel.updateFilterCurrencyExtinctDates(it, selectedContinent?.id) },
                onClose = { viewModel.showFilters(false) }
            )
            Spacer(modifier = Modifier.height(padding))
        }
        if ((!uiState.showStats && !uiState.showFilters) || windowHeightClass != WindowHeightSizeClass.Compact)
            SummaryTableUI(
                table = uiState.currenciesTable,
                availableWidth = width,
                data = viewModel.currenciesViewDataUI,
                isLogged = isLogged,
                onHeaderClick = { colId, statsCol ->
                    viewModel.sortCurrenciesBy(
                        uiState.currenciesTable.columns[colId].linkedField as CurrencySortableField,
                        statsCol,
                        selectedContinent?.id
                    )
                },
                onDataClick = { colId, dataId ->
                    if (colId == 1)
                        onCurrencyClick(dataId)
                    else
                        onCountryClick(dataId)
                }
            )
    }
}
