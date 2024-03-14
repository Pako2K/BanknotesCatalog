package com.pako2k.banknotescatalog.ui.views.subviews


import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.DenominationViewModel
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.InputYearsGroup
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


@Composable
fun DenominationFiltersUI(
    viewModel : DenominationViewModel,
    selectedContinent : UInt?,
    onClose : () -> Unit
){
    // initializationState as state, to trigger recompositions of the whole UI
    val uiState by viewModel.denominationUIState.collectAsState()

    CommonCard("Denomination Filters", onClose){
        Row (
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                .width(IntrinsicSize.Max)
        ) {
            InputYearsGroup("Issue Year", uiState.filterShownIssueYear) {
                viewModel.updateFilterIssueYearDates(it, selectedContinent)
            }
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
    }
}



private const val PREVIEW_WIDTH = 360
private const val PREVIEW_HEIGHT = 800


@Preview(widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun DenominationFiltersUIPreviewPortrait1() {
    BanknotesCatalogTheme {
        DenominationFiltersUI (
            viewModel(factory = DenominationViewModel.Factory),
            null
        ) {}
    }
}



@Preview(widthDp = 1000, heightDp = PREVIEW_HEIGHT)
@Composable
private fun DenominationFiltersUIPreviewLandscape2() {
    BanknotesCatalogTheme {
        DenominationFiltersUI (
            viewModel(factory = DenominationViewModel.Factory),
            null
        ) {}
    }
}