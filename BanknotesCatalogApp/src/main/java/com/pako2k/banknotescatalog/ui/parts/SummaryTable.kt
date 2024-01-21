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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
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
import java.io.IOException
import java.io.InputStream


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
    val isFlag : Boolean = false, // Only the first Column can be a flag.
    val isStats : Boolean = false,
    val isClickable : Boolean = false,
    val isSortable : Boolean  = false,
    var selectedSorting : Sorting? = null,
    var sortedBy : StatsColumn? = null // Only used for Stats columns.
)


@Composable
fun SummaryTable(
    modifier: Modifier = Modifier,
    columns : List<SummaryTableColumn>,
    fixedColumns : Int,
    isLogged : Boolean = false,
    data : List<List<String>>,
    onHeaderClick: (Int) -> Unit,
    onDataClick: (Int, Int) -> Unit
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
    }

}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun SummaryTableData(
    fixedColumns: List<SummaryTableColumn>,
    scrollableColumns : List<SummaryTableColumn>?,
    data : List<List<String>>,
    horizontalScroll : ScrollState,
    onClick: (Int, Int) -> Unit
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

    var dataIndex = 0

    Row {
        // Fixed Columns
        LazyColumn(
            state = vertScrollState1
        ) {
            itemsIndexed(items = data) { index, item ->
                dataIndex = dataRow(
                    rowIndex = index,
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
                    .leftBorder(Color.DarkGray)
                    .horizontalScroll(horizontalScroll)
            ) {
                itemsIndexed(items = data) { index, item ->
                    dataRow(
                        rowIndex = index,
                        columns = scrollableColumns,
                        data = item,
                        firstDataIndex = dataIndex,
                        onClick = onClick
                    )
                }
            }
    }
}

@Composable
fun dataRow(
    rowIndex : Int,
    columns: List<SummaryTableColumn>,
    data : List<String>,
    firstDataIndex : Int,
    onClick: (Int, Int) -> Unit
) : Int {
    var dataIndex = firstDataIndex
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(if (rowIndex % 2 == 0) color_table_row_odd else color_table_row_even)
     ) {
        for (col in columns) {
            val modifier = if (col.isStats) Modifier.leftBorder(Color.DarkGray) else Modifier

            DataCell(col, data[dataIndex++], modifier, onClick = { onClick(rowIndex, dataIndex)})
            if (col.isStats){
                dataIndex++
                DataCell(col, data[dataIndex], Modifier.rightBorder(Color.DarkGray), onClick = { onClick(rowIndex, dataIndex)})
            }
        }
    }
    return dataIndex
}

@SuppressLint("DiscouragedApi")
@Composable
fun DataCell(
    col : SummaryTableColumn,
    value : String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
){
    Box(
        contentAlignment = col.align,
        modifier = modifier
            .width(col.width)
            .padding(dimensionResource(id = R.dimen.small_padding))
    ) {
        if (col.isFlag) {
            val assets =  LocalContext.current.assets
            var file : InputStream? = null
            try{
                file = assets.open("$value.png")
            }
            catch(_: IOException){
            }

            val desc = "flag"
            val height = 20.dp
            if (file != null) {
                Image(
                    BitmapFactory.decodeStream(file).asImageBitmap(),
                    contentDescription = desc,
                    modifier = Modifier.height(height)
                )
            }
            else
                Image(
                    painter = painterResource(id = R.drawable.m_flag_icon),
                    contentDescription = desc,
                    modifier = Modifier.height(height)
                )
        }
        else
            if (col.isClickable)
                ClickableDataText(value, onClick)
            else
                DataText(value, col.width < 50.dp)
    }
}

@Composable
fun DataText(value : String, isSmall : Boolean = false ){
    Text(
        value,
        style =  typographySans.displayMedium,
        fontWeight = if (isSmall) FontWeight.Light else FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = Modifier
            .padding(vertical = dimensionResource(id = R.dimen.small_padding))
    )
}

@Composable
fun ClickableDataText(value : String, onClick: (String) -> Unit){
    Text(
        value,
        style = typographySans.displayMedium,
        color = color_table_link,
        fontWeight = FontWeight.Bold,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = Modifier
            .clickable { onClick(value) }
            .padding(dimensionResource(id = R.dimen.small_padding))
    )
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
                    0.0f to color_table_header_top,
                    0.9f to color_table_header_bottom
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
                    .leftBorder()
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
                .leftBorder()
                .rightBorder()
                .width(col.width * 2)
        ) {
            HeaderText(col.title, modifier = Modifier.padding(top = dimensionResource(id = R.dimen.medium_padding)))

            // Subtitles: Stats
            Row(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.small_padding))
            ){
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    if (col.isSortable)
                        SortableColumnButton(
                            id = col.id,
                            title = StatsColumn.CATALOG.title,
                            isSubtitle = true,
                            isSorted =
                                if (col.sortedBy == StatsColumn.CATALOG) col.selectedSorting
                                else null,
                            onClick = onClick
                        )
                    else
                        HeaderText(StatsColumn.CATALOG.title, isSubtitle = true)
                }
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    if (col.isSortable)
                        SortableColumnButton(
                            id = col.id,
                            title = StatsColumn.COLLECTION.title,
                            isSubtitle = true,
                            isSorted =
                                if (col.sortedBy == StatsColumn.COLLECTION) col.selectedSorting
                                else null,
                            enabled = isLogged,
                            onClick = onClick
                        )
                    else
                        HeaderText(StatsColumn.COLLECTION.title, isSubtitle = true)
                }
            }
        }
    }
    else { // Normal Column
        Box(
            contentAlignment = col.align,
            modifier = Modifier
                .width(col.width)
                .padding(dimensionResource(id = R.dimen.medium_padding))
        ) {
            if (col.isSortable)
                SortableColumnButton(id = col.id, title = col.title, isSorted = col.selectedSorting, onClick = onClick)
            else
                HeaderText(col.title)
        }
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
            .alpha(if (enabled) 1f else 0.5f)
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
            .size(22.dp)
            .absoluteOffset(x = (-3).dp, y = vertOffset)
            .alpha(if (inactive) 0.25f else 1f)
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

fun Modifier.leftBorder(color : Color = Color.LightGray) =
    this.drawBehind {
        val strokeWidth = 1f
        val y = size.height - strokeWidth / 2
        drawLine(
            color,
            Offset(0f, 0f),
            Offset(0f, y),
            strokeWidth,
            alpha = 0.8f
        )
    }

fun Modifier.rightBorder(color : Color = Color.LightGray) =
    this.drawBehind {
        val strokeWidth = 1f
        val y = size.height - strokeWidth / 2
        drawLine(
            color,
            Offset(size.width, 0f),
            Offset(size.width, y),
            strokeWidth,
            alpha = 0.8f
        )
    }





private const val TEST_WIDTH = 412
private const val TEST_HEIGHT = 892

private val cols = listOf(
    SummaryTableColumn(1,"", width = 40.dp, isFlag = true ),
    SummaryTableColumn(2,"ISO", width = 40.dp ),
    SummaryTableColumn(3,"Name", width = 220.dp, align = Alignment.CenterStart, isSortable = true, isClickable = true, selectedSorting = Sorting.ASC),
    SummaryTableColumn(4,"From", width = 80.dp, isSortable = true),
    SummaryTableColumn(5,"To", width = 80.dp, isSortable = true),
    SummaryTableColumn(6,"Currencies", width = 70.dp, isStats = true, isSortable = true, sortedBy = StatsColumn.CATALOG, selectedSorting = Sorting.DESC ),
    SummaryTableColumn(7,"Price", width = 100.dp, isSortable = true ),
    )

private val values = listOf(
    listOf(
        "nam", "NAM", "Namibia", "1970", "", "2", "1", "1.00 €"
    ),
    listOf(
        "usa", "USA", "United States", "1871", "", "1", "1", "234.44 €"
    ),
    listOf(
        "", "", "Taiwan (NR)", "1946", "", "1", "-", "-"
    ),
    listOf(
        "yug", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia, Democratic Republic of", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "esp", "ESP", "Spain", "1521", "", "3", "4", "1523.40 €"
    ),
    listOf(
        "npl", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    ),
    listOf(
        "", "YUG", "Yugoslavia", "1921", "2006", "5", "4", "23.40 €"
    )
)


@Preview (device = "spec:width=${TEST_WIDTH}dp,height=${TEST_HEIGHT}dp,orientation=portrait")
@Composable
fun CountriesPreviewPortrait() {
    BanknotesCatalogTheme {
        SummaryTable(columns = cols, fixedColumns =  2, data = values, onHeaderClick = {_ ->}, onDataClick = {_, _ -> })
    }
}

@Preview (device = "spec:width=${TEST_WIDTH}dp,height=${TEST_HEIGHT}dp,orientation=landscape")
@Composable
fun CountriesPreviewLandscape() {
    BanknotesCatalogTheme {
        SummaryTable(columns = cols, fixedColumns = 3, data = values, onHeaderClick = {_ ->}, onDataClick = {_, _ -> }, isLogged = false)
    }
}