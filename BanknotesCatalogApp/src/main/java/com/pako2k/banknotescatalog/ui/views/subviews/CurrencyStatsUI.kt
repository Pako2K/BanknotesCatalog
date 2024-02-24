package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.pako2k.banknotescatalog.data.CurrencySummaryStats
import com.pako2k.banknotescatalog.ui.common.bottomBorder
import com.pako2k.banknotescatalog.ui.common.rightBorder
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.theme.color_stats_collection
import com.pako2k.banknotescatalog.ui.theme.color_table_row_even
import com.pako2k.banknotescatalog.ui.theme.color_table_row_odd


@Composable
private fun getTitleColor() : Color = MaterialTheme.colorScheme.onSecondary

@Composable
private fun getTitleStyle() : TextStyle = MaterialTheme.typography.bodyMedium

@Composable
private fun getSubTitleStyle() : TextStyle = MaterialTheme.typography.bodySmall

@Composable
private fun getDataStyle() : TextStyle = MaterialTheme.typography.bodySmall

private val subColWidth = 46.dp

@Composable
fun CurrencyStatsUI (
    data : Map<String, CurrencySummaryStats>,
    isLoggedIn : Boolean,
    onClose : () -> Unit
){
    ElevatedCard(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .padding(vertical = dimensionResource(id = R.dimen.medium_padding))
    ) {
        // Another Column to set the Card internal padding
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.small_padding)
                )
        ) {
            CardTitle(onClose)
            Divider(thickness = 2.dp)
            StatsTable(data, isLoggedIn)
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
            text = "Currencies in Catalog",
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
    data : Map<String, CurrencySummaryStats>,
    isLoggedIn : Boolean
){
    // Initial box just for the shadow
    Box(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.medium_padding))
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
                // 1st Column
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ) {
                    HeaderTitle("")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .bottomBorder(Color.Black, 2f)
                    ) {
                        HeaderSubtitle("")
                    }
                    RowTitles(data.keys)
                }

                // Data Columns
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ) {
                    HeaderTitle("Existing")
                    SubtitlesRow(isLoggedIn)
                    data.onEachIndexed { index, type ->
                        RowData(index, type.value.current, isLoggedIn)
                    }
                }
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .rightBorder(Color.White)
                ) {
                    HeaderTitle("Extinct")
                    SubtitlesRow(isLoggedIn)
                    data.onEachIndexed { index, type ->
                        RowData(index, type.value.extinct, isLoggedIn)
                    }
                }
                Column(
                    modifier = Modifier.width(IntrinsicSize.Max)
                ) {
                    HeaderTitle("Total")
                    SubtitlesRow(isLoggedIn)
                    data.onEachIndexed { index, type ->
                        RowData(index, type.value.total, isLoggedIn)
                    }
                }
            }
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
private fun SubtitlesRow(isLoggedIn : Boolean){
    Row(
        modifier = Modifier
            .bottomBorder(Color.Black, 2f)
    ) {
        HeaderSubtitle("Catalog")
        HeaderSubtitle("Collec.", if(isLoggedIn) 1f else 0.4f)
    }
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
                    horizontal = dimensionResource(id = R.dimen.medium_padding),
                    vertical = dimensionResource(id = R.dimen.small_padding)
                )
        )
    }
}

@Composable
private fun RowData(
    rowIndex : Int,
    data : CurrencySummaryStats.Data,
    isLoggedIn: Boolean
){
    Row (
        modifier = Modifier
            .background(if (rowIndex % 2 == 0) color_table_row_even else color_table_row_odd)
            .rightBorder(Color.Black)
            .padding(vertical = dimensionResource(id = R.dimen.small_padding))
    ){
        TextData(data.total, Color.Black)
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



private const val PREVIEW_WIDTH = 380
private const val PREVIEW_HEIGHT = 800

private val testData1 = CurrencySummaryStats(
    current = CurrencySummaryStats.Data(
        total = 122, collection = 12
    ),
    extinct = CurrencySummaryStats.Data(
        total = 22, collection = 1
    )
)

private val testData2 = CurrencySummaryStats(
    current = CurrencySummaryStats.Data(
        total = 22, collection = 1
    ),
    extinct = CurrencySummaryStats.Data(
        total = 22, collection = 1
    )
)

private val testData = mapOf(
    "Total" to testData1,
    "Shared" to testData2,
)

@Preview(widthDp = PREVIEW_WIDTH, heightDp = PREVIEW_HEIGHT)
@Composable
private fun TerritoryStatsUIPreviewPortrait() {
    BanknotesCatalogTheme {
        CurrencyStatsUI(testData, true) {}
    }
}

@Preview(widthDp = PREVIEW_HEIGHT, heightDp = 340)
@Composable
private fun TerritoryStatsUIPreviewLandscape() {
    BanknotesCatalogTheme {
        Surface {
            CurrencyStatsUI (testData, false) { }
        }
    }
}