package com.pako2k.banknotescatalog.ui.parts

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.constraintlayout.compose.ConstraintLayout
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.color_table_header_bottom
import com.pako2k.banknotescatalog.ui.theme.color_table_header_text
import com.pako2k.banknotescatalog.ui.theme.color_table_header_top
import com.pako2k.banknotescatalog.ui.theme.color_table_link
import com.pako2k.banknotescatalog.ui.theme.color_table_row_even
import com.pako2k.banknotescatalog.ui.theme.color_table_row_odd
import com.pako2k.banknotescatalog.ui.theme.typographySans


// Non Composable Constants
private const val BORDER_ALPHA = 0.8f
private const val INACTIVE_SORT_ICON_ALPHA = 0.3f
private const val DISABLED_HEADER_BUTTON_ALPHA = 0.5f



enum class Sorting{
    ASC,
    DESC
}

enum class StatsColumn (val title : String){
    CATALOG (title = "Cat."),
    COLLECTION (title = "Col.")
}

data class SummaryTableColumn(
    val id : Int,
    val title : String,
    val align : Alignment = Alignment.Center,
    val width : Dp,
    val isImage : Boolean = false,
    val isStats : Boolean = false,
    val isClickable : Boolean = false,
    val isSortable : Boolean  = false,
    var selectedSorting : Sorting? = null,
    var sortedBy : StatsColumn? = null // Only used for Stats columns (isStats = true)
)



@Composable
fun SummaryTable(
    modifier: Modifier = Modifier,
    columns : List<SummaryTableColumn>,
    fixedColumns : Int,
    isLogged : Boolean = false,
    data : List<List<Any?>>,
    onHeaderClick: (Int) -> Unit,
    onDataClick: (UInt) -> Unit
) {
    val hScrollState = rememberScrollState()

    val fixedColumnsList =
        if (fixedColumns < columns.size) columns.subList(0, fixedColumns)
        else columns

    val scrollColumnsList =
        if (fixedColumns < columns.size) columns.drop(fixedColumns)
        else null


    Surface(
        shadowElevation = dimensionResource(id = R.dimen.shadowElevation),
        modifier = modifier
    ) {
        Box(
            //modifier = Modifier.height(IntrinsicSize.Min)
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
            if (scrollColumnsList != null && hScrollState.value < (hScrollState.maxValue - 40))
                Icon(
                    painter = painterResource(R.drawable.baseline_keyboard_double_arrow_right_16),
                    //tint = MaterialTheme.colorScheme.outlineVariant,
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(0.8f)
                        .offset(x = dimensionResource(id = R.dimen.small_padding))
                )
        }

    }
}

@Composable
fun SummaryTableHeader(
    fixedColumns: List<SummaryTableColumn>,
    scrollableColumns : List<SummaryTableColumn>?,
    isLogged : Boolean = false,
    horizontalScroll : ScrollState,
    onClick: (Int) -> Unit
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
    ) {
        // Fixed Columns
        for (col in fixedColumns)
            HeaderColumn(col, isLogged, onClick = onClick)

        // Scrollable Columns
        if (scrollableColumns != null)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .leftBorder(colorResource(id = R.color.header_border_color))
                    .horizontalScroll(horizontalScroll)
            ) {
                for (col in scrollableColumns){
                    HeaderColumn(col, isLogged, onClick = onClick)
                }
            }
    }
}


@Composable
fun HeaderColumn(
    col : SummaryTableColumn,
    isLogged: Boolean = false,
    onClick: (Int) -> Unit
){
    if (col.isStats){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .leftBorder(colorResource(id = R.color.header_border_color))
                .rightBorder(colorResource(id = R.color.header_border_color))
                .width(col.width * 2)
                .padding(dimensionResource(id = R.dimen.header_padding))
        ) {
            HeaderText(col.title)

            // Subtitles: Stats
            Row(
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.medium_padding))
            ){
                HeaderStatsColumn(
                    col = col,
                    statsCol = StatsColumn.CATALOG,
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )
                HeaderStatsColumn(
                    col = col,
                    statsCol = StatsColumn.COLLECTION,
                    enabled = isLogged,
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )
            }
        }
    }
    else { // Normal Column
        Box(
            contentAlignment = col.align,
            modifier = Modifier
                .width(col.width)
                .padding(dimensionResource(id = R.dimen.header_padding))
        ) {
            if (col.isSortable)
                SortableColumnButton(id = col.id, title = col.title, isSorted = col.selectedSorting, onClick = onClick)
            else
                HeaderText(col.title)
        }
    }
}

@Composable
fun HeaderStatsColumn(
    modifier : Modifier = Modifier,
    col : SummaryTableColumn,
    statsCol: StatsColumn,
    enabled: Boolean = true,
    onClick: (Int) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (col.isSortable)
            SortableColumnButton(
                id = col.id,
                title = statsCol.title,
                isSubtitle = true,
                isSorted =
                if (col.sortedBy == statsCol) col.selectedSorting
                else null,
                enabled = enabled,
                onClick = onClick
            )
        else
            HeaderText(statsCol.title, isSubtitle = true)
    }
}

@Composable
fun SortableColumnButton(
    id : Int,
    title: String,
    isSubtitle : Boolean = false,
    isSorted: Sorting?,
    enabled : Boolean = true,
    onClick : (Int) -> Unit
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(enabled = enabled) { onClick(id) }
            .alpha(if (enabled) 1f else DISABLED_HEADER_BUTTON_ALPHA)
    ){
        ConstraintLayout {
            HeaderText(title, isSubtitle = isSubtitle, modifier = Modifier.constrainAs(createRef()){
                centerVerticallyTo(parent)
            })
            if (isSorted != null)
                SortIcon(dir = isSorted, modifier = Modifier.constrainAs(createRef()){
                    top.linkTo(parent.top)
                    start.linkTo(parent.end)
                })
            else
                SortIcons(Modifier.constrainAs(createRef()){
                    top.linkTo(parent.top)
                    start.linkTo(parent.end)
                })
        }
    }
}

@Composable
fun HeaderText(text : String, modifier : Modifier = Modifier, isSubtitle: Boolean = false){
    val weight = if (isSubtitle) FontWeight.Normal else FontWeight.Bold
    Text(
        text,
        fontWeight = weight,
        style = typographySans.displaySmall,
        color = color_table_header_text,
        modifier = modifier
    )
}

@Composable
fun SortIcon(dir : Sorting, modifier : Modifier = Modifier, inactive : Boolean = false){
    val vertOffset = if (dir == Sorting.ASC) (-3).dp else 3.dp
    Icon(
        painter = painterResource(
            if (dir == Sorting.ASC) R.drawable.sort_asc
            else R.drawable.sort_desc
        ),
        contentDescription = "",
        tint = color_table_header_text,
        modifier = modifier
            .size(dimensionResource(id = R.dimen.sort_icon_size))
            .absoluteOffset(x = (-3).dp, y = vertOffset)
            .alpha(if (inactive) INACTIVE_SORT_ICON_ALPHA else 1f)
    )
}

@Composable
fun SortIcons(modifier : Modifier = Modifier){
    Box(
        modifier = modifier
    ) {
        SortIcon(dir = Sorting.ASC, inactive = true)
        SortIcon(dir = Sorting.DESC, inactive = true)
    }
}



@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun SummaryTableData(
    fixedColumns: List<SummaryTableColumn>,
    scrollableColumns : List<SummaryTableColumn>?,
    data : List<List<Any?>>,
    horizontalScroll : ScrollState,
    onClick: (UInt) -> Unit
){
    val vertScrollState1 = rememberLazyListState()
    val vertScrollState2 = rememberLazyListState()

    if (scrollableColumns != null) {
        LaunchedEffect(vertScrollState1.firstVisibleItemScrollOffset) {
            vertScrollState2.scrollToItem(
                vertScrollState1.firstVisibleItemIndex,
                vertScrollState1.firstVisibleItemScrollOffset
            )
        }
        LaunchedEffect(vertScrollState2.firstVisibleItemScrollOffset) {
            vertScrollState1.scrollToItem(
                vertScrollState2.firstVisibleItemIndex,
                vertScrollState2.firstVisibleItemScrollOffset
            )
        }
    }

    // Column index of the data row
    // (it does not match the column index of the header because of possible Stats columns in the fixed columns
    val scrollableDataIndex = fixedColumns.count{ it.isStats } + fixedColumns.size

    Row {
        // Fixed Columns
        LazyColumn(
            state = vertScrollState1
        ) {
            itemsIndexed(items = data) { index, item ->
                DataRow(
                    isEven = index % 2 == 0,
                    columns = fixedColumns,
                    data = item,
                    firstDataIndex = 0,
                    onClick = onClick
                )
            }
        }

        if (scrollableColumns != null)
            // Horizontal scrollable columns
            LazyColumn(
                state = vertScrollState2,
                modifier = Modifier
                    .leftBorder(colorResource(id = R.color.data_border_color))
                    .horizontalScroll(horizontalScroll)
            ) {
                itemsIndexed(items = data) { index, item ->
                    DataRow(
                        isEven = index % 2 == 0,
                        columns = scrollableColumns,
                        data = item,
                        firstDataIndex = scrollableDataIndex,
                        onClick = onClick
                    )
                }
            }
    }
}

@Composable
fun DataRow(
    isEven : Boolean,
    columns: List<SummaryTableColumn>,
    data : List<Any?>,
    firstDataIndex : Int,
    onClick: (UInt) -> Unit
) {
    var dataIndex = firstDataIndex
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(if (isEven) color_table_row_odd else color_table_row_even)
     ) {
        for (col in columns) {
            if (data[dataIndex] is ImageBitmap?)
                ImageDataCell(col, data[dataIndex] as ImageBitmap?)
            else if (data[dataIndex] is String)
                TextDataCell(col, data[dataIndex] as String, if (col.isStats) Modifier.leftBorder(colorResource(id = R.color.data_border_color)) else Modifier)
            else
                ClickableTextDataCell(col, data[dataIndex] as Pair<UInt, String> , onClick = onClick)

            if (col.isStats){
                dataIndex++
                TextDataCell(col, data[dataIndex] as String, Modifier.rightBorder(colorResource(id = R.color.data_border_color)))
            }
            dataIndex++
        }
    }
}

@Composable
fun ClickableTextDataCell(
    col : SummaryTableColumn,
    value : Pair<UInt,String>,
    modifier: Modifier = Modifier,
    onClick: (UInt) -> Unit
){
    Box(
        contentAlignment = col.align,
        modifier = modifier
            .width(col.width)
            .padding(dimensionResource(id = R.dimen.data_padding))
            .clickable {
                onClick(value.first)
            }
    ) {
        Text(
            text = value.second,
            style = typographySans.displayMedium,
            color = color_table_link,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.small_padding))
        )
    }
}

@Composable
fun TextDataCell(
    col : SummaryTableColumn,
    value : String,
    modifier: Modifier = Modifier,
){
    Box(
        contentAlignment = col.align,
        modifier = modifier
            .width(col.width)
            .padding(dimensionResource(id = R.dimen.data_padding))
    ) {
        Text(
            text = value,
            style = typographySans.displayMedium,
            fontWeight = if (col.width < dimensionResource(id = R.dimen.narrow_col_size)) FontWeight.Light else FontWeight.Normal,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.small_padding))
        )
    }
}

@Composable
fun ImageDataCell(
    col : SummaryTableColumn,
    value : ImageBitmap?,

    modifier: Modifier = Modifier,
){
    Box(
        contentAlignment = col.align,
        modifier = modifier
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



private fun Modifier.leftBorder(color : Color, strokeWidth : Float = 1f) =
    this.drawBehind {
        val y = size.height - strokeWidth / 2
        drawLine(
            color,
            Offset(0f, 0f),
            Offset(0f, y),
            strokeWidth,
            alpha = BORDER_ALPHA
        )
    }

private fun Modifier.rightBorder(color : Color, strokeWidth : Float = 1f) =
    this.drawBehind {
        val y = size.height - strokeWidth / 2
        drawLine(
            color,
            Offset(size.width, 0f),
            Offset(size.width, y),
            strokeWidth,
            alpha = BORDER_ALPHA
        )
    }





private const val TEST_WIDTH = 412
private const val TEST_HEIGHT = 892

private val cols = listOf(
    SummaryTableColumn(1,"", width = 40.dp, isImage = true ),
    SummaryTableColumn(2,"", width = 44.dp ),
    SummaryTableColumn(3,"Name", width = 220.dp, align = Alignment.CenterStart, isSortable = true, isClickable = true, selectedSorting = Sorting.ASC),
    SummaryTableColumn(4,"From", width = 80.dp, isSortable = true),
    SummaryTableColumn(5,"To", width = 80.dp, isSortable = true),
    SummaryTableColumn(6,"Currencies", width = 70.dp, isStats = true, isSortable = true, sortedBy = StatsColumn.CATALOG, selectedSorting = Sorting.DESC ),
    SummaryTableColumn(7,"Price", width = 100.dp, isSortable = true ),
    )

private val values = listOf(
    mutableListOf<Any?>(
        null, "NAM", "Namibia", "1970", "", "2", "1", "1.00 €"
    ),
    mutableListOf<Any?>(
        "usa", "USA", "United States", "1871", "", "1", "1", "234.44 €"
    ),
    mutableListOf<Any?>(
        "", "", "Taiwan (NR)", "1946", "", "1", "-", "-"
    ),
    mutableListOf<Any?>(
        "yug", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia, Democratic Republic of", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "esp", "ESP", "Spain", "1521", "", "3", "4", "1523.40 €"
    ),
    mutableListOf<Any?>(
        "npl", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    mutableListOf<Any?>(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    )
)


@Preview (device = "spec:width=${TEST_WIDTH}dp,height=${TEST_HEIGHT}dp,orientation=portrait")
@Composable
fun CountriesPreviewPortrait() {
    val flag = LocalContext.current.assets.open("usa.png").let {
        BitmapFactory.decodeStream(it).asImageBitmap()
    }
    values.forEach {
        it[0] = flag
    }

    BanknotesCatalogTheme {
        SummaryTable(
            columns = cols,
            fixedColumns = 2,
            data = values,
            onHeaderClick = { _ -> },
            onDataClick = { _ -> }
        )
    }
}

@Preview (device = "spec:width=${TEST_WIDTH}dp,height=${TEST_HEIGHT}dp,orientation=landscape")
@Composable
fun CountriesPreviewLandscape() {
    val flag = LocalContext.current.assets.open("usa.png").let {
        BitmapFactory.decodeStream(it).asImageBitmap()
    }
    values.forEach {
        it[0] = flag
    }

    BanknotesCatalogTheme {
        SummaryTable(columns = cols, fixedColumns = 3, data = values, onHeaderClick = {_ ->}, onDataClick = {_ -> }, isLogged = false)
    }
}