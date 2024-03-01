package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.pako2k.banknotescatalog.data.TerritoryTypeEnum
import com.pako2k.banknotescatalog.ui.parts.CheckButtonGroup
import com.pako2k.banknotescatalog.ui.parts.CheckOption
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.parts.InputYearsGroup
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


@Composable
fun TerritoryFiltersUI(
    terTypeFilters : Map<TerritoryTypeEnum, Boolean>,
    terStateFilters : Pair<Boolean,Boolean>,
    terFoundedFilter : FilterDates,
    terExtinctFilter : FilterDates,
    onTerTypeChanged : (TerritoryTypeEnum, Boolean) -> Unit,
    onTerStateChanged : (Pair<Boolean, Boolean>) -> Unit,
    onTerFoundedChanged : (FilterDates) -> Unit,
    onTerExtinctChanged : (FilterDates) -> Unit,
    onClose : () -> Unit
){
    CommonCard(
        title = "Territory Filters",
        onClose = onClose) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        if (screenWidth < 350.dp){
            Column (
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
            ) {
                CheckGroupsFilter(terTypeFilters, terStateFilters, false, onTerTypeChanged, onTerStateChanged)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.large_padding) + dimensionResource(id = R.dimen.small_padding)))
                FilterFoundedExtinct(terFoundedFilter, terExtinctFilter, onTerFoundedChanged, onTerExtinctChanged)
            }
        }
        else if (screenWidth < 700.dp){
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                    .width(IntrinsicSize.Max)
            ) {
                CheckGroupsFilter(terTypeFilters, terStateFilters, true, onTerTypeChanged, onTerStateChanged)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.large_padding) + dimensionResource(id = R.dimen.small_padding)))
                FilterFoundedExtinct(terFoundedFilter, terExtinctFilter, onTerFoundedChanged, onTerExtinctChanged)
            }
        }
        else{
            Row (
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                    .width(IntrinsicSize.Max)
            ) {
                CheckGroupsFilter(terTypeFilters, terStateFilters, true, onTerTypeChanged, onTerStateChanged)
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.large_padding)))
                FilterFoundedExtinct(terFoundedFilter, terExtinctFilter, onTerFoundedChanged, onTerExtinctChanged)
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.medium_padding)))
        }
    }
}

@Composable
private fun CheckGroupsFilter(
    terTypeFilters : Map<TerritoryTypeEnum, Boolean>,
    terStateFilters: Pair<Boolean, Boolean>,
    horizontalAlignment : Boolean,
    onTerTypeChanged : (TerritoryTypeEnum, Boolean) -> Unit,
    onTerStateChanged : (Pair<Boolean, Boolean>) -> Unit,
){
    val setup : @Composable (@Composable (() -> Unit))-> Unit = if (horizontalAlignment) {
        { Row {it()} }
    } else {
        { Column{it()}}
    }

    val optionsArray = terTypeFilters.map{
        CheckOption(
            title = it.key.value,
            isSelected = it.value,
            onCheckChanged = {isSelected -> onTerTypeChanged(it.key,isSelected)}
        )
    }.toTypedArray()

    val spacerModifier =
        if (horizontalAlignment) Modifier.width(dimensionResource(id = R.dimen.large_padding))
        else Modifier.height(dimensionResource(id = R.dimen.large_padding))

    val groupModifier = if (horizontalAlignment) Modifier else Modifier.fillMaxWidth()

    setup {
        CheckButtonGroup(
            modifier = groupModifier,
            title = "Territory Type",
            color = Color.Black,
            background = Color.White,
            *optionsArray
            )

        Spacer(modifier = spacerModifier)
        CheckButtonGroup(
            modifier = groupModifier,
            title = "Current State",
            color = Color.Black,
            background = Color.White,
            CheckOption("Existing", terStateFilters.first) {
                onTerStateChanged(Pair(it, terStateFilters.second))
            },
            CheckOption("Extinct", terStateFilters.second) {
                onTerStateChanged(Pair(terStateFilters.first, it))
            }
        )
    }
}


@Composable
private fun FilterFoundedExtinct(
    terFoundedFilter : FilterDates,
    terExtinctFilter : FilterDates,
    onFoundedChanged: (FilterDates) -> Unit,
    onExtinctChanged: (FilterDates) -> Unit
){
    Row {
        InputYearsGroup("Founded", terFoundedFilter, onFoundedChanged)
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.large_padding)))
        InputYearsGroup("Extinct", terExtinctFilter, onExtinctChanged)
    }
}



private const val PREVIEW_WIDTH = 349
private const val PREVIEW_HEIGHT = 800

private val filterTerritoryTypes = TerritoryTypeEnum.values().associateWith { true }
private val filterTerritoryExisting : Boolean = true
private val filterTerritoryExtinct : Boolean = true
private val filterTerFoundedFrom : Int? = null
private val filterTerFoundedTo : Int? = null
private val filterTerExtinctFrom : Int? = null
private val filterTerExtinctTo : Int? = null
@Preview(widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun TerritoryFiltersPreviewPortrait1() {
    BanknotesCatalogTheme {
        TerritoryFiltersUI (
            filterTerritoryTypes,
            Pair(filterTerritoryExisting, filterTerritoryExtinct),
            FilterDates(filterTerFoundedFrom, filterTerFoundedTo),
            FilterDates(filterTerExtinctFrom, filterTerExtinctTo),
            {_,_ ->},{_ ->},{_ ->}, {_ ->},{}
        )
    }
}


private const val PREVIEW_WIDTH_2 = 489
private const val PREVIEW_HEIGHT_2 = 350

@Preview(widthDp = PREVIEW_WIDTH_2, heightDp = PREVIEW_HEIGHT_2)
@Composable
private fun TerritoryFiltersPreviewLandscape1() {
    BanknotesCatalogTheme {
        TerritoryFiltersUI (
            filterTerritoryTypes,
            Pair(filterTerritoryExisting, filterTerritoryExtinct),
            FilterDates(filterTerFoundedFrom, filterTerFoundedTo),
            FilterDates(filterTerExtinctFrom, filterTerExtinctTo),
            {_,_ ->},{_ ->},{_ ->}, {_ ->},{}
        )
    }
}


@Preview(widthDp = PREVIEW_WIDTH_2+500, heightDp = PREVIEW_HEIGHT_2)
@Composable
private fun TerritoryFiltersPreviewLandscape2() {
    BanknotesCatalogTheme {
        TerritoryFiltersUI (
            filterTerritoryTypes,
            Pair(filterTerritoryExisting, filterTerritoryExtinct),
            FilterDates(filterTerFoundedFrom, filterTerFoundedTo),
            FilterDates(filterTerExtinctFrom, filterTerExtinctTo),
            {_,_ ->},{_ ->},{_ ->}, {_ ->},{}
        )
    }
}
