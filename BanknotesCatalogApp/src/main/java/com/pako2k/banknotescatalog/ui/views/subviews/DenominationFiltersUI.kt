package com.pako2k.banknotescatalog.ui.views.subviews


import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.InputYearsGroup
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


@Composable
fun DenominationFiltersUI(
    dates: FilterDates,
    onChangedDates : (FilterDates) -> Unit,
    onClose : () -> Unit
){
    CommonCard("Denomination Filters", onClose){
        Row (
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                .width(IntrinsicSize.Max)
        ) {
            InputYearsGroup("Issue Year", dates, onChangedDates)
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
            FilterDates(null, null), {}
        ) {}
    }
}



@Preview(widthDp = 1000, heightDp = PREVIEW_HEIGHT)
@Composable
private fun DenominationFiltersUIPreviewLandscape2() {
    BanknotesCatalogTheme {
        DenominationFiltersUI (
            FilterDates(null, null), {}
        ) {}
    }
}