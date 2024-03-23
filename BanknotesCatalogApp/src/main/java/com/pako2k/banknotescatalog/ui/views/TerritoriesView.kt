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
import com.pako2k.banknotescatalog.app.TerritoryViewModel
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.repo.TerritorySortableField
import com.pako2k.banknotescatalog.ui.parts.SummaryTableUI
import com.pako2k.banknotescatalog.ui.views.subviews.TerritoryFiltersUI
import com.pako2k.banknotescatalog.ui.views.subviews.TerritoryStatsUI


@Composable
fun TerritoriesView(
    viewModel : TerritoryViewModel,
    width: Dp,
    windowHeightClass : WindowHeightSizeClass,
    selectedContinent: Continent?,
    isLogged : Boolean,
    onCountryClick: (territoryID: UInt)->Unit,
) {
    Log.d(stringResource(id = R.string.app_log_tag),"Start Territories")

    val padding = dimensionResource(id = R.dimen.small_padding)

    // uiState as state, to trigger recompositions
    val uiState by viewModel.territoryUIState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(padding)
    ) {
        if (uiState.showTerritoryStats) {
            TerritoryStatsUI(
                data = viewModel.getTerritoryStats(),
                continentName = selectedContinent?.name,
                isLoggedIn = isLogged,
                onClose = {
                    viewModel.showTerritoryStats(false)
                }
            )
            Spacer(modifier = Modifier.height(padding))
        }
        if (uiState.showTerritoryFilters) {
            TerritoryFiltersUI(
                terTypeFilters = uiState.filterTerritoryTypes,
                terStateFilters = Pair(
                    uiState.filterTerritoryState.first,
                    uiState.filterTerritoryState.second
                ),
                terFoundedFilter = uiState.filterTerFounded,
                terExtinctFilter = uiState.filterTerExtinct,
                onTerTypeChanged = { type, isSelected ->
                    viewModel.updateFilterTerritoryType(
                        type,
                        isSelected,
                        selectedContinent?.id
                    )
                },
                onTerStateChanged = { viewModel.updateFilterTerritoryState(it, selectedContinent?.id) },
                onTerFoundedChanged = { viewModel.updateFilterTerritoryFoundedDates(it, selectedContinent?.id) },
                onTerExtinctChanged = { viewModel.updateFilterTerritoryExtinctDates(it, selectedContinent?.id) },
                onClose = { viewModel.showTerritoryFilters(false) }
            )
            Spacer(modifier = Modifier.height(padding))
        }
        if ((!uiState.showTerritoryStats && !uiState.showTerritoryFilters) || windowHeightClass != WindowHeightSizeClass.Compact) {
            SummaryTableUI(
                table = uiState.territoriesTable,
                availableWidth = width,
                data = viewModel.territoriesViewDataUI,
                isLogged = isLogged,
                onHeaderClick = { colId, statsCol ->
                    viewModel.sortTerritoriesBy(
                        uiState.territoriesTable.columns[colId].linkedField as TerritorySortableField,
                        statsCol,
                        selectedContinent?.id
                    )
                },
                onDataClick = { _, dataId -> onCountryClick(dataId) }
            )
        }
    }
}


