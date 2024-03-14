package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.IssueYearViewModel
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.InputYearsGroup


@Composable
fun IssueYearFiltersUI(
    viewModel: IssueYearViewModel,
    selectedContinent : UInt?,
    onClose : () -> Unit
){
    // initializationState as state, to trigger recompositions of the whole UI
    val uiState by viewModel.issueYearUIState.collectAsState()

    CommonCard("Issue Year Filters", onClose){
        Column(
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                .width(IntrinsicSize.Max)
        ) {
            InputYearsGroup("Issued",uiState.filterShownIssueYear) {
                viewModel.updateFilterIssueYearDates(
                    it,
                    selectedContinent
                )
            }
        }
    }
}

