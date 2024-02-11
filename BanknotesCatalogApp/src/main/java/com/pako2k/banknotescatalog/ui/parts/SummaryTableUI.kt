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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.StatsSubColumn
import com.pako2k.banknotescatalog.app.SummaryTable
import com.pako2k.banknotescatalog.app.SummaryTableColumn
import com.pako2k.banknotescatalog.data.SortDirection
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.color_table_header_bottom
import com.pako2k.banknotescatalog.ui.theme.color_table_header_text
import com.pako2k.banknotescatalog.ui.theme.color_table_header_top
import com.pako2k.banknotescatalog.ui.theme.color_table_link_prim
import com.pako2k.banknotescatalog.ui.theme.color_table_link_sec
import com.pako2k.banknotescatalog.ui.theme.color_table_row_even
import com.pako2k.banknotescatalog.ui.theme.color_table_row_odd
import com.pako2k.banknotescatalog.ui.theme.typographySans
import kotlinx.coroutines.launch


// Non Composable Constants
private const val BORDER_ALPHA = 0.8f
private const val INACTIVE_SORT_ICON_ALPHA = 0.3f
private const val DISABLED_HEADER_BUTTON_ALPHA = 0.5f


@Composable
fun SummaryTableUI(
    modifier: Modifier = Modifier,
    table : SummaryTable,
    fixedColumns : Int,
    isLogged : Boolean = false,
    data : List<List<Any?>>,
    onHeaderClick: (Int) -> Unit,
    onDataClick: (colIndex: Int, dataId : UInt) -> Unit
) {
    val hScrollState = rememberScrollState()
    val columns = table.columns

    val fixedColumnsList =
        if (fixedColumns < columns.size) columns.subList(0, fixedColumns)
        else columns

    val scrollColumnsList =
        if (fixedColumns < columns.size) columns.drop(fixedColumns)
        else null

    val coroutineScope = rememberCoroutineScope()

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
                    painter = painterResource(R.drawable.double_arrow_right_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(0.8f)
                        .offset(x = dimensionResource(id = R.dimen.small_padding))
                        .clickable {
                            coroutineScope.launch{
                                hScrollState.scrollTo(hScrollState.maxValue)
                            }
                        }
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
        fixedColumns.forEachIndexed{ index, col ->
            HeaderColumn(index, col, isLogged, onClick = onClick)
        }

        // Scrollable Columns
        if (scrollableColumns != null)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .leftBorder(colorResource(id = R.color.header_border_color))
                    .horizontalScroll(horizontalScroll)
            ) {
                scrollableColumns.forEachIndexed{ index, col ->
                    HeaderColumn(fixedColumns.size+index, col, isLogged, onClick = onClick)
                }
            }
    }
}


@Composable
fun HeaderColumn(
    id : Int,
    //sortDirection: SortDirection?,
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
                    id = id,
                    col = col,
                    statsCol = StatsSubColumn.CATALOG,
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )
                HeaderStatsColumn(
                    id = id,
                    col = col,
                    statsCol = StatsSubColumn.COLLECTION,
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
                SortableColumnButton(id = id, title = col.title, isSorted = col.sortedDirection, onClick = onClick)
            else
                HeaderText(col.title)
        }
    }
}

@Composable
fun HeaderStatsColumn(
    id : Int,
    modifier : Modifier = Modifier,
    col : SummaryTableColumn,
    statsCol: StatsSubColumn,
    enabled: Boolean = true,
    onClick: (Int) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
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
    isSorted: SortDirection?,
    enabled : Boolean = true,
    onClick : (Int) -> Unit
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
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
fun SortIcon(dir : SortDirection, modifier : Modifier = Modifier, inactive : Boolean = false){
    val vertOffset = if (dir == SortDirection.ASC) (-3).dp else 3.dp
    Icon(
        painter = painterResource(
            if (dir == SortDirection.ASC) R.drawable.sort_asc
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
        SortIcon(dir = SortDirection.ASC, inactive = true)
        SortIcon(dir = SortDirection.DESC, inactive = true)
    }
}



@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun SummaryTableData(
    fixedColumns: List<SummaryTableColumn>,
    scrollableColumns : List<SummaryTableColumn>?,
    data : List<List<Any?>>,
    horizontalScroll : ScrollState,
    onClick:  (colIndex: Int, dataId : UInt) -> Unit
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
    onClick: (colIndex: Int, dataId : UInt) -> Unit
) {
    var dataIndex = firstDataIndex
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(if (isEven) color_table_row_odd else color_table_row_even)
     ) {
        var clickableTextColor = color_table_link_prim
        columns.forEachIndexed { colIndex, col ->
            when (val value = data[dataIndex]) {
                is ImageBitmap? -> ImageDataCell(col, value)
                is String -> TextDataCell(col, value, if (col.isStats) Modifier.leftBorder(colorResource(id = R.color.data_border_color)) else Modifier)
                is Pair<*,*> -> {
                    ClickableTextDataCell(col, Pair(value.first as UInt, value.second as String), color = clickableTextColor, onClick = {onClick(colIndex, it)})
                    clickableTextColor = color_table_link_sec
                }
                else -> TextDataCell(col, "")
            }

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
    color : Color,
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



/*



private val cols = listOf(

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
*/
private const val TEST_WIDTH = 412
private const val TEST_HEIGHT = 892


private val summaryTablePreview = SummaryTable(
    columns = listOf(
        SummaryTableColumn("", width = 40.dp, isImage = true ),
        SummaryTableColumn("", width = 44.dp ),
        SummaryTableColumn("Name", width = 220.dp, align = Alignment.CenterStart, isSortable = true, isClickable = true),
        SummaryTableColumn("From", width = 80.dp, isSortable = true),
        SummaryTableColumn("To", width = 80.dp, isSortable = true),
//        SummaryTableColumn("Currencies", width = 70.dp, isStats = true, isSortable = true),
//        SummaryTableColumn("Price", width = 100.dp, isSortable = true ),
    ),
    sortedBy = 3,
    sortDirection = SortDirection.ASC
)

private val dataPreview = listOf<MutableList<Any?>>(
    mutableListOf(null, "", Pair(289u,"Namibia [NR]"), "1976", ""),
    mutableListOf(null, "ARG", Pair(28u,"Argentina [NR]"), "1976", "2002"),
    mutableListOf(null, "LAO", Pair(2u,"Laos"), "1976", ""),
    mutableListOf(null, "", Pair(9u,"Algeria [T]"), "276", ""),
    mutableListOf(null, "", Pair(29u,"Algeria [T]"), "276", "")
)


@Preview(device = "spec:width=${TEST_WIDTH}dp,height=${TEST_HEIGHT}dp,orientation=portrait")
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
            fixedColumns = 2,
            data = dataPreview,
            onHeaderClick = { _ -> },
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
        fixedColumns = 2,
        data = dataPreview,
        onHeaderClick = { _ -> },
        onDataClick = { _ , _ -> }
    )
}
