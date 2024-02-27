package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.TerritorySummaryStats
import com.pako2k.banknotescatalog.data.plus
import com.pako2k.banknotescatalog.ui.common.bottomBorder
import com.pako2k.banknotescatalog.ui.common.rightBorder
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.color_stats_collection
import com.pako2k.banknotescatalog.ui.theme.color_stats_issuer
import com.pako2k.banknotescatalog.ui.theme.color_table_row_even
import com.pako2k.banknotescatalog.ui.theme.color_table_row_odd
import kotlinx.coroutines.launch


@Composable
private fun getTitleColor() : Color = MaterialTheme.colorScheme.onSecondary

@Composable
private fun getTitleStyle() : TextStyle = MaterialTheme.typography.bodyMedium

@Composable
private fun getSubTitleStyle() : TextStyle = MaterialTheme.typography.bodySmall

@Composable
private fun getDataStyle() : TextStyle = MaterialTheme.typography.bodySmall

private val subColWidth = 44.dp

@Composable
fun TerritoryStatsUI (
    data : Map<String, TerritorySummaryStats>,
    continentName : String?,
    isLoggedIn : Boolean,
    onClose : () -> Unit
){
    ElevatedCard(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        modifier = Modifier
            .width(IntrinsicSize.Min)
    ) {
        // Another Column to set the Card internal padding
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.small_padding)
            )
        ) {
            CardTitle(continentName, onClose)
            HorizontalDivider(thickness = 2.dp)
            StatsTable(data, isLoggedIn)
        }

    }

}

@Composable
private fun CardTitle(
    continentName : String?,
    onClose : () -> Unit
){
    val style = MaterialTheme.typography.headlineMedium
    val continentSuffix =
        if(continentName != null) " - $continentName"
        else ""

    Row {
        Text(
            text = "Territories in Catalog$continentSuffix",
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
private fun StatsTable(
    data : Map<String, TerritorySummaryStats>,
    isLoggedIn : Boolean
){
    val hScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Initial box just for the shadow
    Box(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.small_padding))
            .shadow(elevation = 1.dp, ambientColor = Color.Black)
    ) {
        // Inside Box for the overall background
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .padding(1.dp)
                .border(Dp.Hairline, Color.Gray)
                .background(MaterialTheme.colorScheme.secondary)
                .verticalScroll(rememberScrollState())
        ){
            Row {
                // Fixed 1st Column
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ){
                    HeaderTitle("")
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .bottomBorder(Color.Black, 2f)
                    ){
                        HeaderSubtitle("")
                    }
                    RowTitles(data.keys)
                    HeaderTitle("TOTAL",TextAlign.End)
                }
                // END Fixed First Column

                // Scrollable Columns
                Row (
                    modifier = Modifier.horizontalScroll(hScrollState)
                ){
                    Column(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .rightBorder(Color.White)
                    ){
                        HeaderTitle("Existing")
                        SubtitlesRow(isLoggedIn)
                        var sum = TerritorySummaryStats.Data(0,0)
                        data.onEachIndexed{ index, type ->
                            RowData(index, type.value.current, isLoggedIn)
                            sum += type.value.current
                        }
                        RowDataTotal(sum, isLoggedIn)
                    }
                    Column(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .rightBorder(Color.White)
                    ){
                        HeaderTitle("Extinct")
                        SubtitlesRow(isLoggedIn)
                        var sum = TerritorySummaryStats.Data(0,0)
                        data.onEachIndexed{ index, type ->
                            RowData(index, type.value.extinct, isLoggedIn)
                            sum += type.value.extinct
                        }
                        RowDataTotal(sum, isLoggedIn)
                    }
                    Column(
                        modifier = Modifier.width(IntrinsicSize.Max)
                    ){
                        HeaderTitle("Total")
                        SubtitlesRow(isLoggedIn)
                        var sum = TerritorySummaryStats.Data(0,0)
                        data.onEachIndexed{ index, type ->
                            RowData(index, type.value.total,isLoggedIn)
                            sum += type.value.total
                        }
                        RowDataTotal(sum, isLoggedIn)
                    }
                }

            }
            if ( hScrollState.value < (hScrollState.maxValue - 20))
                Icon(
                    painter = painterResource(R.drawable.double_arrow_right_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(0.8f)
                        .offset(x = dimensionResource(id = R.dimen.small_padding))
                        .clickable {
                            coroutineScope.launch {
                                hScrollState.scrollTo(hScrollState.maxValue)
                            }
                        }
                )
        }
    }
}

@Composable
private fun HeaderTitle(
    title : String,
    align : TextAlign = TextAlign.Center
) {
    Text(
        title,
        style = getTitleStyle(),
        textAlign = align,
        fontWeight = FontWeight.Bold,
        color = getTitleColor(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.small_padding),
                vertical = dimensionResource(id = R.dimen.small_padding)
            )
    )
}

@Composable
private fun HeaderSubtitle(
    title : String,
    alpha: Float = 1f
) {
    Text(
        title,
        style = getSubTitleStyle(),
        color = getTitleColor().copy(alpha = alpha),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .widthIn(min = subColWidth)
            .padding(bottom = dimensionResource(id = R.dimen.small_padding))
    )
}

@Composable
private fun SubtitlesRow(isLoggedIn : Boolean){
    Row(
        modifier = Modifier
            .bottomBorder(Color.Black, 2f)
    ) {
        HeaderSubtitle("Total")
        HeaderSubtitle("Issuer")
        HeaderSubtitle("Collec.", if(isLoggedIn) 1f else 0.4f)
    }
}

@Composable
private fun RowTitles(
    data : Set<String>,
){
    data.onEachIndexed { index, it ->
        Text(
            it,
            style = getDataStyle(),
            modifier = Modifier
                .fillMaxWidth()
                .background(if (index % 2 == 0) color_table_row_even else color_table_row_odd)
                .rightBorder(Color.Black, 2f)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.small_padding),
                    vertical = dimensionResource(id = R.dimen.xs_padding)
                )
        )
    }
}

@Composable
private fun RowData(
    rowIndex : Int,
    data : TerritorySummaryStats.Data,
    isLoggedIn: Boolean
){
    Row (
        modifier = Modifier
            .background(if (rowIndex % 2 == 0) color_table_row_even else color_table_row_odd)
            .rightBorder(Color.Black)
            .padding(vertical = dimensionResource(id = R.dimen.xs_padding))
    ){
        TextData(data.total, Color.Black)
        TextData(data.issuer, color_stats_issuer)
        TextData(data.collection, color_stats_collection, if(isLoggedIn) 1f else 0.4f)
    }
}

@Composable
private fun TextData(
    value : Int,
    color: Color,
    alpha : Float = 1f
){
    Text(
       value.toString(),
        style = getDataStyle(),
        textAlign = TextAlign.Center,
        color = color,
        modifier = Modifier
            .widthIn(min = subColWidth)
            .alpha(alpha)
    )
}

@Composable
private fun RowDataTotal(
    data : TerritorySummaryStats.Data,
    isLoggedIn: Boolean
){
    Row(
        modifier = Modifier
            .padding(vertical = dimensionResource(id = R.dimen.small_padding))
    ) {
        TextDataTotal(data.total)
        TextDataTotal(data.issuer)
        TextDataTotal(data.collection, if(isLoggedIn) 1f else 0.4f)
    }
}


@Composable
private fun TextDataTotal(
    value : Int,
    alpha : Float = 1f
){
    Text(
        value.toString(),
        style = getTitleStyle(),
        textAlign = TextAlign.Center,
        color = getTitleColor().copy(alpha = alpha),
        modifier = Modifier
            .widthIn(min = subColWidth)
    )
}



private const val PREVIEW_WIDTH = 380
private const val PREVIEW_HEIGHT = 800

private val testData1 = TerritorySummaryStats(
    current = TerritorySummaryStats.Data(
        total = 122, issuer = 111, collection = 12
    ),
    extinct = TerritorySummaryStats.Data(
        total = 22, issuer = 11, collection = 1
    )
)

private val testData2 = TerritorySummaryStats(
    current = TerritorySummaryStats.Data(
        total = 22, issuer = 11, collection = 1
    ),
    extinct = TerritorySummaryStats.Data(
        total = 22, issuer = 11, collection = 1
    )
)

private val testData = mapOf(
    "Independent" to testData1,
    "Not recognized State" to testData2,
    "Territory" to testData2,
)

@Preview(widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun TerritoryStatsUIPreviewPortrait() {
    BanknotesCatalogTheme {
        TerritoryStatsUI (testData, "Africa",true) { }
    }
}

@Preview(widthDp = PREVIEW_HEIGHT, heightDp = 340)
@Composable
private fun TerritoryStatsUIPreviewLandscape() {
    BanknotesCatalogTheme {
        Surface {
            TerritoryStatsUI (testData, null,false) { }
        }
    }
}