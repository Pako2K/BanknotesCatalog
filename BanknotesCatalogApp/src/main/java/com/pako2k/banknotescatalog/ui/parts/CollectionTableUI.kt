package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.CollectionTable
import com.pako2k.banknotescatalog.app.CollectionTableColumn
import com.pako2k.banknotescatalog.data.Grades
import com.pako2k.banknotescatalog.data.repo.SortDirection
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.color_table_row_even
import com.pako2k.banknotescatalog.ui.theme.color_table_row_odd
import com.pako2k.banknotescatalog.ui.theme.typographySans


// Non Composable Constants
private const val INACTIVE_SORT_ICON_ALPHA = 0.3f
private const val FIXED_COLS = 2

@Composable
fun CollectionTableUI(
    table : CollectionTable,
    data : List<List<Any>>,
    onHeaderClick: (colIndex : Int) -> Unit,
    onDataClick: (colIndex: Int, dataId : UInt) -> Unit
){
    val hScrollState = rememberScrollState()

    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .shadow(3.dp)
            .padding(3.dp)
            .background(color_table_row_even)
    ){
        Column {
            TableHeader(table, hScrollState, onHeaderClick)
            TableData(table, data, hScrollState, onDataClick)
        }
        // Scroll button
        if (hScrollState.value < (hScrollState.maxValue - 40))
            HScrollButton(hScrollState)
    }
}


@Composable
private fun TableHeader(
    table : CollectionTable,
    horizontalScroll : ScrollState,
    onClick : (Int) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onSurface)
            .height(IntrinsicSize.Min)
    ) {
        table.columns.take(FIXED_COLS).forEachIndexed { index, col ->
            TableHeaderCell(col, index, onClick)
        }
        VerticalDivider(thickness = 1.dp, color = Color.Gray)
        Row(
            modifier = Modifier
                .horizontalScroll(horizontalScroll)
        ) {
            table.columns.drop(FIXED_COLS).forEachIndexed { index, col ->
                TableHeaderCell(col, FIXED_COLS+index, onClick)
            }
        }
    }
}

@Composable
private fun TableHeaderCell(
    col : CollectionTableColumn,
    index : Int,
    onClick : (Int) -> Unit
){
    Box(
        contentAlignment = col.align,
        modifier = Modifier
            .width(col.width)
    ){
        if (col.isSortable)
            SortableHeaderText(
                id = index,
                title = col.title,
                isSorted = col.sortedDirection,
                onClick = onClick
            )
        else
            HeaderText(col.title)
    }
}

@Composable
private fun SortableHeaderText(
    id : Int,
    title: String,
    isSorted: SortDirection?,
    onClick : (Int) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable{ onClick(id) }
    ){
        HeaderText(title)
        if (isSorted != null)
            SortIcon(dir = isSorted, inactive = false)
        else
            SortIcons()

    }
}

@Composable
private fun HeaderText(
    text : String
){
    Text(
        text,
        fontWeight = FontWeight.Bold,
        style = typographySans.labelLarge,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.small_padding))
    )
}


@Composable
private fun SortIcons(){
    Box{
        SortIcon(dir = SortDirection.ASC, inactive = true)
        SortIcon(dir = SortDirection.DESC, inactive = true)
    }
}


@Composable
private fun SortIcon(dir : SortDirection, inactive : Boolean){
    val vertOffset = if (dir == SortDirection.ASC) (-3).dp else 2.5.dp

    Image(
        painter = painterResource(
            if (dir == SortDirection.ASC) R.drawable.sort_asc
            else R.drawable.sort_desc
        ),
        contentDescription = "",
        colorFilter = ColorFilter.tint(Color.White),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(dimensionResource(id = R.dimen.sort_icon_size))
            .alpha(if (inactive) INACTIVE_SORT_ICON_ALPHA else 1f)
            .absoluteOffset(x = 0.dp, y = vertOffset)
    )
}


@Composable
private fun TableData(
    table : CollectionTable,
    data : List<List<Any>>,
    horizontalScroll : ScrollState,
    onClick: (colIndex: Int, dataId : UInt) -> Unit
){
    val totalWidth : Int = 1 + table.columns.sumOf { it.width.value.toInt() }
    LazyColumn {
        itemsIndexed(items = data) { _, item ->
            if (item.size > 1) {
                TableDataRow(
                    columns = table.columns,
                    data = item,
                    horizontalScroll = horizontalScroll,
                    onClick = onClick
                )
                HorizontalDivider(thickness = Dp.Hairline, color = Color.Gray, modifier = Modifier.width(totalWidth.dp))
            }
            else {
                val summary = item[0] as Pair<*, *>
                Text(
                    text = "Total (${summary.first}): ${summary.second}",
                    style = MaterialTheme.typography.labelSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(id = R.dimen.small_padding),
                            horizontal = dimensionResource(id = R.dimen.large_padding)
                        )

                )
                HorizontalDivider(thickness = 1.dp, color = Color.Black, modifier = Modifier.width(totalWidth.dp))
            }
        }
    }
}


@Composable
private fun TableDataRow(
    columns: List<CollectionTableColumn>,
    data : List<Any>,
    horizontalScroll : ScrollState,
    onClick: (colIndex: Int, dataId : UInt) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color_table_row_odd)
            .height(IntrinsicSize.Min)
    ) {
        columns.take(FIXED_COLS).forEachIndexed { colIndex, col ->
            TextDataCell(
                col,
                data[colIndex],
                onClick = { onClick(colIndex, it) }
            )
        }
        VerticalDivider(thickness = 1.dp, color = Color.Gray )
        Row(
            modifier = Modifier.horizontalScroll(horizontalScroll)
        ){
            columns.drop(FIXED_COLS).forEachIndexed { index, col ->
                val colIndex = FIXED_COLS + index
                TextDataCell(
                    col,
                    data[colIndex],
                    onClick = { onClick(colIndex, it) }
                )
            }
        }
    }
}

@Composable
private fun TextDataCell(
    col : CollectionTableColumn,
    value : Any,
    onClick: (UInt) -> Unit = {}
){
    var id : UInt? = null
    val text: String
    val isClickable : Boolean

    when (value) {
        is Pair<*, *> -> {
            id = value.first as UInt
            text = value.second as String
            isClickable = true
        }

        else -> {
            text = value.toString()
            isClickable = false
        }
    }

    var modifier = Modifier
        .width(col.width)
        .padding(vertical = dimensionResource(id = R.dimen.data_padding))
        .clickable {
            if (isClickable) onClick(id ?: 0u)
        }
    if (col.isGrading)
        modifier = modifier.background(Grades.valueOf(text).background)



    Box(
        contentAlignment = col.align,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = typographySans.labelLarge,
            fontWeight = if (isClickable) FontWeight.Bold else FontWeight.Normal,
            color = if (col.isGrading)  Grades.valueOf(text).color else Color.Black,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.small_padding))
        )
    }
}




private const val TEST_WIDTH = 340
private const val TEST_HEIGHT = 950

private val tablePreview = CollectionTable(
    columns = listOf(
        CollectionTableColumn(title = "Territory",  width = 130.dp, isSortable = true, align = Alignment.CenterStart, isClickable = true),
        CollectionTableColumn(title = "Cat. Id", width = 80.dp, isSortable = false, align = Alignment.CenterStart, isClickable = true),
        CollectionTableColumn(title = "Denom.",  width = 120.dp, isSortable = true),
        CollectionTableColumn(title = "Currency",  width = 110.dp, isSortable = true, align = Alignment.CenterStart, isClickable = true),
        CollectionTableColumn(title = "Grade", width = 45.dp, isSortable = false, isGrading = true),
        CollectionTableColumn(title = "Qty",  width = 28.dp, isSortable = false),
        CollectionTableColumn(title = "Price", width = 60.dp, isSortable = true),
        CollectionTableColumn(title = "Seller",  width = 90.dp, isSortable = true),
        CollectionTableColumn(title = "Purchased", width = 80.dp, isSortable = true),
        CollectionTableColumn(title = "Comments", width = 120.dp, isSortable = false, align = Alignment.CenterStart)
    ),
    sortedBy = 0,
    sortDirection = SortDirection.ASC,
    minFixedColumns = 2
)


private val dataPreview = listOf<List<Any>>(
    listOf(Pair(289u,"Namibia"),Pair(289u,"P-12a"), "10",Pair(289u,"Dollar"), "G", 1, 23.5, "Seller 12","11-12-2014", "Comments"),
    listOf(Pair("Albania", "5,18 €")),
    listOf(Pair(281u,"United Stated"),Pair(289u,"P-112a.12"), "50",Pair(289u,"Peseta"), "F", 1, 23.5, "Seller 12","11-12-2014", "Comments"),
    listOf(Pair(282u,"Germany"),Pair(289u,"P-122a"), "2.5",Pair(289u,"Dinar"), "UNC", 1, 23.5, "","11-12-2014", "Comments"),
    listOf(Pair(283u,"Soviet Union"),Pair(289u,"P-2a"), "10,000",Pair(289u,"Leu"), "VF", 1, "230.5 €", "Long Seller Name","11-12-2014", "Long Comments"),
    listOf(Pair("Albania", "5,18 €")),
    listOf(Pair(284u,"Congo, Democratic Republic"),Pair(289u,"P-32a"), "10",Pair(289u,"Bolivar Soberano"), "UNC", 1, "23.5 €", "Seller 12","11-12-2014", "Comments"),
    listOf(Pair(285u,"Laos"),Pair(289u,"P-12a"), "100,000",Pair(289u,"Pound"), "REP", 1, 23.5, "Seller 12","11-12-2014", ""),
    listOf(Pair("Albania", "5,18 €")),
    listOf(Pair(286u,"Spain"),Pair(289u,"P-12a"), "100,000,000,000,000",Pair(289u,"Euro"), "VG", 1, "2.5 €", "Seller 12","11-12-2014", "Comments"),
    listOf(Pair(287u,"Colombia"),Pair(289u,"P-12a"), "5,000",Pair(289u,"Dollar"), "P", 1, "0 €", "Seller 12","", "Comments"),
    listOf(Pair("Albania", "5,18 €")),
    listOf(Pair(287u,"Colombia"),Pair(289u,"P-12a"), "5,000",Pair(289u,"Dollar"), "P", 1, "0 €", "Seller 12","", "Comments"),
)

@Preview(device = "spec:width=${TEST_WIDTH}dp,height=${TEST_HEIGHT}dp,orientation=portrait")
@Composable
fun CollectionTableUIPreviewPortrait() {
    BanknotesCatalogTheme {
        CollectionTableUI(tablePreview, dataPreview, {},{_,_ ->})
    }
}


@Preview(device = "spec:width=${TEST_WIDTH}dp,height=${TEST_HEIGHT}dp,orientation=landscape")
@Composable
fun CollectionTableUILandscape() {
    BanknotesCatalogTheme {
        CollectionTableUI(tablePreview, dataPreview, {},{_,_ ->})
    }
}
