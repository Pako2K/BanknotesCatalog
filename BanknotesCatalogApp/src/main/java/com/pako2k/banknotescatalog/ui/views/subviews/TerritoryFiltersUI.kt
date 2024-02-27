package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.TerritoryTypeEnum
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


private const val MAX_YEAR_LENGTH = 4

@Composable
private fun sectionTitleStyle() = MaterialTheme.typography.headlineSmall


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
    ElevatedCard(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
    ) {
        // Another Column to set the Card internal padding
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.small_padding))
                .width(IntrinsicSize.Min)
        ) {
            CardTitle(onClose)
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.medium_padding)))
            Filters(
                terTypeFilters, terStateFilters, terFoundedFilter, terExtinctFilter,
                onTerTypeChanged, onTerStateChanged, onTerFoundedChanged, onTerExtinctChanged)
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
        }
    }
}


@Composable
private fun CardTitle(
    onClose : () -> Unit
){
    val style = MaterialTheme.typography.headlineMedium
    Row {
        Text(
            text = "Territory Filters",
            style = style,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.small_padding))
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(style.fontSize.value.dp * 1.5f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close_icon),
                contentDescription = "Close Icon"
            )
        }
    }
}


@Composable
private fun Filters(
    terTypeFilters : Map<TerritoryTypeEnum, Boolean>,
    terStateFilters : Pair<Boolean,Boolean>,
    terFoundedFilter : FilterDates,
    terExtinctFilter : FilterDates,
    onTerTypeChanged : (TerritoryTypeEnum, Boolean) -> Unit,
    onTerStateChanged : (Pair<Boolean, Boolean>) -> Unit,
    onFoundedChanged: (FilterDates) -> Unit,
    onExtinctChanged: (FilterDates) -> Unit
){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    if (screenWidth < 350.dp){
        Column (
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
        ) {
            CheckGroupsFilter(terTypeFilters, terStateFilters, false, onTerTypeChanged, onTerStateChanged)
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.large_padding) + dimensionResource(id = R.dimen.small_padding)))
            FilterFoundedExtinct(terFoundedFilter, terExtinctFilter, onFoundedChanged, onExtinctChanged)
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
            FilterFoundedExtinct(terFoundedFilter, terExtinctFilter, onFoundedChanged, onExtinctChanged)
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
            FilterFoundedExtinct(terFoundedFilter, terExtinctFilter, onFoundedChanged, onExtinctChanged)
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.medium_padding)))
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
        InputYears("Founded", terFoundedFilter, onFoundedChanged)
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.large_padding)))
        InputYears("Extinct", terExtinctFilter, onExtinctChanged)
    }
}

@Composable
private fun InputYears(
    title : String,
    values : FilterDates,
    onChanged : (FilterDates) -> Unit,
){
    val valueFrom = values.from?.toString()?:""
    val valueTo = values.to?.toString()?:""

    Column {
        Text(
            text = title,
            style = sectionTitleStyle()
        )
        Row {
            InputYearField(title = "From ", valueFrom, values.isValid, onValueChange = { value ->
                onChanged(FilterDates(value.toIntOrNull(), valueTo.toIntOrNull()))
            })
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.medium_padding)))
            InputYearField(title = "To ", valueTo, values.isValid, onValueChange = { value ->
               onChanged(FilterDates(valueFrom.toIntOrNull(), value.toIntOrNull()))
            })
        }
    }
}

@Composable
private fun InputYearField(
    title : String,
    value : String,
    isValid : Boolean,
    onValueChange: (String) -> Unit
){
    val focus = LocalFocusManager.current
    TextField(
        value = value,
        textStyle = MaterialTheme.typography.bodyLarge,
        label = {
            Text(
                title,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodyMedium
            )},
        keyboardActions = KeyboardActions(onDone = {
            focus.clearFocus()
        }),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        singleLine = true,
        isError = !isValid,
        onValueChange = {
            if (it.isEmpty() || (it.toIntOrNull()!=null && it.length <= MAX_YEAR_LENGTH))
                onValueChange(it)
                        },
        modifier = Modifier
            .width(72.dp)
    )
}

@Composable
private fun CheckGroupsFilter(
    terTypeFilters : Map<TerritoryTypeEnum, Boolean>,
    terStateFilters: Pair<Boolean, Boolean>,
    horizontalAlignment : Boolean,
    onTerTypeChanged : (TerritoryTypeEnum, Boolean) -> Unit,
    onTerStateChanged : (Pair<Boolean, Boolean>) -> Unit,
){
    val setup : @Composable (@Composable()(() -> Unit))-> Unit = if (horizontalAlignment) {
        { Row {it()} }
    } else {
        { Column{it()}}
    }

    val spacerModifier =
        if (horizontalAlignment) Modifier.width(dimensionResource(id = R.dimen.large_padding))
        else Modifier.height(dimensionResource(id = R.dimen.large_padding) + dimensionResource(id = R.dimen.small_padding))

    setup {
        val modifier = if (horizontalAlignment) Modifier else Modifier.fillMaxWidth()
        CheckGroup("Territory Type", modifier = modifier) {
            terTypeFilters.forEach {
                CheckOption(it.key.value, it.value) { isSelected ->
                    onTerTypeChanged(it.key, isSelected)
                }
            }
        }

        Spacer(modifier = spacerModifier)

        CheckGroup("Current State", modifier = modifier) {
            CheckOption("Existing", terStateFilters.first) {
                onTerStateChanged(Pair(it, terStateFilters.second))
            }
            CheckOption("Extinct", terStateFilters.second) {
                onTerStateChanged(Pair(terStateFilters.first, it))
            }
        }
    }
}

@Composable
private fun CheckGroup(
    title : String,
    modifier : Modifier = Modifier,
    options : @Composable ()-> Unit
){
    Box{
        Column(
            modifier = modifier
                .offset(x = 0.dp, y = sectionTitleStyle().fontSize.value.dp / 2 + 1.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RectangleShape
                )
                .padding(
                    vertical = dimensionResource(id = R.dimen.small_padding),
                    horizontal = dimensionResource(id = R.dimen.medium_padding)
                )
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
            options()
        }
        Text(
            title,
            style = sectionTitleStyle(),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .background(Color.White)
                .padding(horizontal = 4.dp)
        )
    }

}

@Composable
private fun CheckOption(
    title : String,
    isSelected : Boolean,
    onCheckChanged : (Boolean) -> Unit
){

    
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onCheckChanged(!isSelected) },
            modifier = Modifier.size(width = 30.dp, height = 28.dp)
        )
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable { onCheckChanged(!isSelected) }
        )
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
