package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.ui.parts.CheckButtonGroup
import com.pako2k.banknotescatalog.ui.parts.CheckOption
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.InputYearsGroup
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


@Composable
fun CurrencyFiltersUI(
    curTypeFilters : Pair<Boolean,Boolean>,
    curStateFilters : Pair<Boolean,Boolean>,
    curFoundedFilter : FilterDates,
    curExtinctFilter : FilterDates,
    onCurTypeChanged : (Pair<Boolean, Boolean>) -> Unit,
    onCurStateChanged : (Pair<Boolean, Boolean>) -> Unit,
    onCurFoundedChanged : (FilterDates) -> Unit,
    onCurExtinctChanged : (FilterDates) -> Unit,
    onClose : () -> Unit
){
    CommonCard("Currency Filters", onClose){
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        if (screenWidth < 650.dp){
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                    .width(IntrinsicSize.Max)
            ) {
                CheckGroupsFilter(curTypeFilters, curStateFilters, onCurTypeChanged, onCurStateChanged)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.large_padding) + dimensionResource(id = R.dimen.small_padding)))
                FilterFoundedExtinct(curFoundedFilter, curExtinctFilter, onCurFoundedChanged, onCurExtinctChanged)
            }
        }
        else{
            Row (
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                    .width(IntrinsicSize.Max)
            ) {
                CheckGroupsFilter(curTypeFilters, curStateFilters, onCurTypeChanged, onCurStateChanged)
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.large_padding)))
                FilterFoundedExtinct(curFoundedFilter, curExtinctFilter, onCurFoundedChanged, onCurExtinctChanged)
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.medium_padding)))
        }
    }
}



@Composable
private fun CheckGroupsFilter(
    curTypeFilters : Pair<Boolean,Boolean>,
    curStateFilters : Pair<Boolean,Boolean>,
    onCurTypeChanged : (Pair<Boolean, Boolean>) -> Unit,
    onCurStateChanged : (Pair<Boolean, Boolean>) -> Unit,
){
    Row {
        CheckButtonGroup(
            modifier = Modifier,
            title = "Currency Type",
            color = Color.Black ,
            background = Color.White,
            CheckOption("Not shared", curTypeFilters.first) {
                onCurTypeChanged(Pair(it,curTypeFilters.second))
            },
            CheckOption("Shared", curTypeFilters.second) {
                onCurTypeChanged(Pair(curTypeFilters.first,it))
            }
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.large_padding)))
        CheckButtonGroup(
            modifier = Modifier,
            title = "Current State",
            color = Color.Black ,
            background = Color.White,
            CheckOption("Existing", curStateFilters.first) {
                onCurStateChanged(Pair(it,curStateFilters.second))
            },
            CheckOption("Extinct", curStateFilters.second) {
                onCurStateChanged(Pair(curStateFilters.first,it))
            }
        )
    }
}

@Composable
private fun FilterFoundedExtinct(
    curFoundedFilter : FilterDates,
    curExtinctFilter : FilterDates,
    onFoundedChanged: (FilterDates) -> Unit,
    onExtinctChanged: (FilterDates) -> Unit
){
    Row {
        InputYearsGroup("Created", curFoundedFilter, onFoundedChanged)
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.large_padding)))
        InputYearsGroup("Finished", curExtinctFilter, onExtinctChanged)
    }
}




private const val PREVIEW_WIDTH = 360
private const val PREVIEW_HEIGHT = 800

private val filterCurrencyTypes = Pair(true,true)
private val filterCurrencyState = Pair(true,true)
private val filterCurFoundedFrom : Int? = null
private val filterCurFoundedTo : Int? = null
private val filterCurExtinctFrom : Int? = null
private val filterCurExtinctTo : Int? = null
@Preview(widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun TerritoryFiltersPreviewPortrait1() {
    BanknotesCatalogTheme {
        CurrencyFiltersUI (
            filterCurrencyTypes,
            filterCurrencyState,
            FilterDates(filterCurFoundedFrom, filterCurFoundedTo),
            FilterDates(filterCurExtinctFrom, filterCurExtinctTo),
            {_ ->},{_ ->},{_ ->}, {_ ->},{}
        )
    }
}



@Preview(widthDp = 1000, heightDp = PREVIEW_HEIGHT)
@Composable
private fun CurrencyFiltersUIPreviewLandscape2() {
    BanknotesCatalogTheme {
        CurrencyFiltersUI (
            filterCurrencyTypes,
            filterCurrencyState,
            FilterDates(filterCurFoundedFrom, filterCurFoundedTo),
            FilterDates(filterCurExtinctFrom, filterCurExtinctTo),
            {_ ->},{_ ->},{_ ->}, {_ ->},{}
        )
    }
}
