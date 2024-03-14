package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.InputYearsGroup


@Composable
fun IssueYearFiltersUI(
    dates: FilterDates,
    onChangedDates : (FilterDates) -> Unit,
    onClose : () -> Unit
){
    CommonCard("Issue Year Filters", onClose){
        Column(
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                .width(IntrinsicSize.Max)
        ) {
            InputYearsGroup("Issued",dates, onChangedDates)
        }
    }
}

