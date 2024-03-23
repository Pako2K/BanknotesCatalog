package com.pako2k.banknotescatalog.ui.parts

import android.graphics.BitmapFactory
import androidx.annotation.ColorRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.StatsSubColumn
import com.pako2k.banknotescatalog.app.SummaryTable
import com.pako2k.banknotescatalog.app.SummaryTableColumn
import com.pako2k.banknotescatalog.data.repo.SortDirection
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldEnd
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldName
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldStart
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.color_table_header_bottom
import com.pako2k.banknotescatalog.ui.theme.color_table_header_text
import com.pako2k.banknotescatalog.ui.theme.color_table_header_top
import com.pako2k.banknotescatalog.ui.theme.color_table_link_prim
import com.pako2k.banknotescatalog.ui.theme.color_table_link_sec
import com.pako2k.banknotescatalog.ui.theme.color_table_row_even
import com.pako2k.banknotescatalog.ui.theme.color_table_row_odd
import com.pako2k.banknotescatalog.ui.theme.typographySans



// Non Composable Constants
private const val INACTIVE_SORT_ICON_ALPHA = 0.3f
private const val DISABLED_HEADER_BUTTON_ALPHA = 0.5f


@Composable
fun SummaryTableUI(
    table : SummaryTable,
    availableWidth : Dp,
    isLogged : Boolean,
    data : List<List<Any?>>,
    onHeaderClick: (Int, StatsSubColumn?) -> Unit,
    onDataClick: (colIndex: Int, dataId : UInt) -> Unit
) {
    val hScrollState = rememberScrollState()
    val columns = table.columns

    val totalWidth = (columns.filter { it.isVisible }.sumOf { if(it.isStats) 2 * it.width.value.toDouble() else it.width.value.toDouble() }).toFloat()

    val fixedColumnsList : List<SummaryTableColumn>
    val scrollColumnsList : List<SummaryTableColumn>?
    if (totalWidth > availableWidth.value){
        val fixedColumns = table.minFixedColumns
        fixedColumnsList = columns.subList(0, fixedColumns)
        scrollColumnsList = columns.drop(fixedColumns)
    } else {
        fixedColumnsList = columns
        scrollColumnsList = null
    }


    // Box used to draw the double arrow scroll button
    Box(
        contentAlignment = Alignment.CenterEnd
    ){
        Column{
            // Header
            SummaryTableHeader(
                fixedColumns = fixedColumnsList,
                scrollableColumns = scrollColumnsList,
                isLogged = isLogged,
                horizontalScroll = hScrollState,
                onClick = onHeaderClick
            )

            // Rows
            SummaryTableData(
                fixedColumns = fixedColumnsList,
                scrollableColumns = scrollColumnsList,
                data = data,
                horizontalScroll = hScrollState,
                onClick = onDataClick
            )
        }

        // Scroll button
        if (scrollColumnsList != null && hScrollState.value < (hScrollState.maxValue - 40))
            HScrollButton(hScrollState)
    }
}

@Composable
private fun SummaryTableHeader(
    fixedColumns: List<SummaryTableColumn>,
    scrollableColumns : List<SummaryTableColumn>?,
    isLogged : Boolean = false,
    horizontalScroll : ScrollState,
    onClick: (Int, StatsSubColumn?) -> Unit
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    0f to color_table_header_top,
                    1f to color_table_header_bottom
                )
            )
            .height(IntrinsicSize.Min)
    ) {
        // Fixed Columns
        fixedColumns.forEachIndexed{ index, col ->
            if (col.isVisible && (scrollableColumns == null || col.title.isNotEmpty()))
                HeaderColumn(index, col, isLogged, onClick = onClick)
        }

        // Scrollable Columns
        if (scrollableColumns != null) {
            BorderDivider(R.color.header_border_color)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .horizontalScroll(horizontalScroll)
            ) {
                scrollableColumns.forEachIndexed { index, col ->
                    if (col.isStats && index > 0 && !scrollableColumns[index - 1].isStats)
                        BorderDivider(R.color.header_border_color)
                    if (col.isVisible)
                        HeaderColumn(fixedColumns.size + index, col, isLogged, onClick = onClick)
                }
            }
        }
    }
}


@Composable
private fun HeaderColumn(
    id : Int,
    col : SummaryTableColumn,
    isLogged: Boolean = false,
    onClick: (Int, StatsSubColumn?) -> Unit
){
    if (col.isStats){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.header_padding))
        ) {
            HeaderText(col.title)

            // Subtitles: Stats
            Row(
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.medium_padding))
            ){
                StatsSubColumn.values().forEach {
                    HeaderStatsColumn(
                        id = id,
                        col = col,
                        statsCol = it,
                        enabled = it == StatsSubColumn.CATALOG || isLogged,
                        onClick = onClick
                    )
                }
            }
        }
        BorderDivider(R.color.header_border_color)
    }
    else { // Normal Column
        Row(
            horizontalArrangement = col.align,
            modifier = Modifier
                .width(col.width)
                .padding(start = if (col.align == Arrangement.Start) dimensionResource(id = R.dimen.header_padding) else 0.dp)
        ) {
            if (col.isSortable)
                SortableColumnButton(
                    id = id,
                    title = col.title,
                    isSorted = col.sortedDirection,
                    enabled = col.title != "Price" || isLogged,
                    onClick = {onClick(it, null)})
            else
                HeaderText(col.title)
        }
    }
}

@Composable
private fun HeaderStatsColumn(
    id : Int,
    col : SummaryTableColumn,
    statsCol: StatsSubColumn,
    enabled: Boolean,
    onClick: (Int, StatsSubColumn) -> Unit
) {
    Row(
        horizontalArrangement = col.align,
        modifier = Modifier
            .width(col.width)
    ) {
        if (col.isSortable)
            SortableColumnButton(
                id = id,
                title = statsCol.title,
                isSubtitle = true,
                isSorted =
                if (col.sortedStats == statsCol) col.sortedDirection
                else null,
                enabled = enabled,
                onClick = {onClick(it, statsCol)}
            )
        else
            HeaderText(statsCol.title, isSubtitle = true)
    }
}

@Composable
private fun SortableColumnButton(
    id : Int,
    title: String,
    isSubtitle : Boolean = false,
    isSorted: SortDirection?,
    enabled : Boolean,
    onClick : (Int) -> Unit
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(enabled = enabled) { onClick(id) }
            .alpha(if (enabled) 1f else DISABLED_HEADER_BUTTON_ALPHA)
    ){
        HeaderText(title, isSubtitle = isSubtitle)
        if (isSorted != null)
            SortIcon(dir = isSorted, inactive = false)
        else
            SortIcons()

    }
}

@Composable
private fun HeaderText(text : String, isSubtitle: Boolean = false){
    val weight = if (isSubtitle) FontWeight.Normal else FontWeight.Bold
    Text(
        text,
        fontWeight = weight,
        style = typographySans.displaySmall,
        color = color_table_header_text,
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
private fun SummaryTableData(
    fixedColumns: List<SummaryTableColumn>,
    scrollableColumns : List<SummaryTableColumn>?,
    data : List<List<Any?>>,
    horizontalScroll : ScrollState,
    onClick:  (colIndex: Int, dataId : UInt) -> Unit
) {
    LazyColumn {
        itemsIndexed(items = data) {index, item ->
            Row {
                DataRow(
                    isEven = index % 2 == 0,
                    columns = fixedColumns,
                    data = item,
                    firstDataIndex = 0,
                    hasMoreColumns = scrollableColumns != null,
                    isCompactWidth = scrollableColumns != null,
                    onClick = onClick
                )
                if (scrollableColumns != null)
                    DataRow(
                        modifier =Modifier.horizontalScroll(horizontalScroll),
                        isEven = index % 2 == 0,
                        columns = scrollableColumns,
                        data = item,
                        firstDataIndex = fixedColumns.count { it.isStats } + fixedColumns.size,
                        hasMoreColumns = false,
                        isCompactWidth = true,
                        onClick = onClick
                    )
            }
        }
    }
}


@Composable
private fun DataRow(
    modifier : Modifier = Modifier,
    isEven : Boolean,
    columns: List<SummaryTableColumn>,
    data : List<Any?>,
    firstDataIndex : Int,
    hasMoreColumns: Boolean,
    isCompactWidth : Boolean,
    onClick: (colIndex: Int, dataId : UInt) -> Unit
) {
    var dataIndex = firstDataIndex
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(if (isEven) color_table_row_odd else color_table_row_even)
            .height(IntrinsicSize.Min)
     ) {
        var clickableTextColor = color_table_link_prim
        columns.forEachIndexed { colIndex, col ->
            if (!isCompactWidth || col.title.isNotEmpty()) {
                when (val value = data[dataIndex]) {
                    is ImageBitmap? -> ImageDataCell(col, value)
                    is Pair<*, *> -> {
                        ClickableTextDataCell(
                            col,
                            Pair(value.first as UInt, value.second as String),
                            color = clickableTextColor,
                            onClick = { onClick(colIndex, it) })
                        clickableTextColor = color_table_link_sec
                    }

                    else -> {
                        if (col.isStats && colIndex > 0 && !columns[colIndex - 1].isStats) {
                            BorderDivider(R.color.data_border_color)
                        }
                        if (col.isVisible)
                            TextDataCell(col, value.toString())
                    }
                }
            }

            if (col.isStats){
                dataIndex++
                if (col.isVisible) {
                    TextDataCell(col, data[dataIndex] as String)
                    BorderDivider(R.color.data_border_color)
                }
            }
            dataIndex++
        }
        if(hasMoreColumns)
            BorderDivider(R.color.data_border_color)
    }
}

@Composable
private fun ClickableTextDataCell(
    col : SummaryTableColumn,
    value : Pair<UInt,String>,
    color : Color,
    onClick: (UInt) -> Unit
){
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .width(col.width)
            .padding(vertical = dimensionResource(id = R.dimen.data_padding))
            .clickable {
                onClick(value.first)
            }
    ) {
        Text(
            text = value.second,
            style = typographySans.displaySmall,
            color = color,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.small_padding))
        )
    }
}

@Composable
private fun TextDataCell(
    col : SummaryTableColumn,
    value : String,
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(col.width)
            .padding(vertical = dimensionResource(id = R.dimen.data_padding))
    ) {
        Text(
            text = value,
            style = typographySans.displaySmall,
            fontWeight = if (col.width < dimensionResource(id = R.dimen.narrow_col_size)) FontWeight.Light else FontWeight.Normal,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.small_padding))
        )
    }
}

@Composable
private fun ImageDataCell(
    col : SummaryTableColumn,
    value : ImageBitmap?,
){
    Box(
        modifier = Modifier
            .width(col.width)
            .padding(dimensionResource(id = R.dimen.data_padding))
    ) {
        if (value != null)
            Image(
                bitmap = value,
                contentDescription = stringResource(id = R.string.content_description_flag),
                modifier = Modifier.height(dimensionResource(id = R.dimen.flag_size))
            )
        else
            Image(
                painter = painterResource(id = R.drawable.m_flag_icon),
                contentDescription = null,
                modifier = Modifier.height(dimensionResource(id = R.dimen.flag_size))
            )
    }
}

@Composable
private fun BorderDivider(@ColorRes color : Int) {
    VerticalDivider(
        color = colorResource(id = color),
        modifier = Modifier
            .width(1.dp)
            .alpha(0.25f)
    )
}





private const val TEST_WIDTH = 380
private const val TEST_HEIGHT = 300

private const val STATS_COL_WIDTH = 52

private val summaryTablePreview = SummaryTable(
    columns = listOf(
        SummaryTableColumn(title = "", width = 36.dp, isImage = true ),
        SummaryTableColumn(title = "", width = 44.dp ),
        SummaryTableColumn(title = "Name", linkedField = TerritoryFieldName, width = 200.dp, align = Arrangement.Start, isSortable = true, isClickable = true),
        SummaryTableColumn(title = "Founded", linkedField = TerritoryFieldStart, width = 74.dp, isSortable = true),
        SummaryTableColumn(title = "Extinct", linkedField = TerritoryFieldEnd, width = 70.dp, isSortable = true),
        SummaryTableColumn(title = "Currencies", isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
        SummaryTableColumn(title = "Issues", isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
        SummaryTableColumn(title = "Face Value", isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
        SummaryTableColumn(title = "Note Types", isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
        SummaryTableColumn(title = "Variants", isStats = true, width = STATS_COL_WIDTH.dp, isSortable = true),
        SummaryTableColumn(title = "Price", width = 80.dp, isSortable = true)
    ),
    sortedBy = 3,
    sortDirection = SortDirection.ASC,
    minFixedColumns = 3
)

private val dataPreview = listOf<MutableList<Any?>>(
    mutableListOf(null, "", Pair(289u,"Namibia [NR]"), "1976", "","1","-","10","-","12","-","100","-","1000","-","-"),
    mutableListOf(null, "ARG", Pair(28u,"Argentina [NR]"), "1976", "2002","1","-","1","-","1","-","1","-","1","-","-"),
    mutableListOf(null, "LAO", Pair(2u,"Laos"), "1976", "","1","-","1","-","1","-","1","-","1","-","-"),
    mutableListOf(null, "LAO", Pair(2u,"Laos"), "1976", "","1","-","1","-","1","-","1","-","1","-","-"),
    mutableListOf(null, "", Pair(9u,"Algeria [T]"), "276", "","1","-","1","-","1","-","1","-","1","-","-"),
    mutableListOf(null, "", Pair(29u,"Algeria [T]"), "276", "","9","-","11","-","10","-","122","-","1222","-","-"),
    mutableListOf(null, "LAO", Pair(244u,"Laos3"), "1976", "","1","-","1","-","1","-","1","-","1","-","-"),
    mutableListOf(null, "LAO", Pair(233u,"Laos4"), "1976", "","1","-","1","-","1","-","1","-","1","-","-"),
    mutableListOf(null, "LAO", Pair(222u,"Laos5"), "1976", "","1","-","1","-","1","-","1","-","1","-","-"),
    mutableListOf(null, "LAO", Pair(211u,"Laos6"), "1976", "","1","-","1","-","1","-","1","-","1","-","-"),
)


@Preview(heightDp = TEST_HEIGHT)//(device = "spec:width=${TEST_WIDTH}dp,height=${TEST_HEIGHT}dp,orientation=portrait")
@Composable
fun SummaryTablePreviewPortrait() {
    val flag = LocalContext.current.assets.open("usa.png").let {
        BitmapFactory.decodeStream(it).asImageBitmap()
    }
    dataPreview.forEach {
        it[0] = flag
    }

    BanknotesCatalogTheme {
        SummaryTableUI(
            table = summaryTablePreview,
            availableWidth = TEST_WIDTH.dp,
            data = dataPreview,
            isLogged = true,
            onHeaderClick = { _, _ -> },
            onDataClick = { _ , _ -> }
        )
    }
}


@Preview (device = "spec:width=${TEST_WIDTH}dp,height=${TEST_HEIGHT}dp,orientation=landscape")
@Composable
fun SummaryTablePreviewLandscape() {
    val flag = LocalContext.current.assets.open("usa.png").let {
        BitmapFactory.decodeStream(it).asImageBitmap()
    }
    dataPreview.forEach {
        it[0] = flag
    }

    SummaryTableUI(
        table = summaryTablePreview,
        availableWidth = TEST_HEIGHT.dp,
        data = dataPreview,
        isLogged = false,
        onHeaderClick = { _,_ -> },
        onDataClick = { _ , _ -> }
    )
}
