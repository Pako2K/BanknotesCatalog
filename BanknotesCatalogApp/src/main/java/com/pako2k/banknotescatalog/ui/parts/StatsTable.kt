package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.common.bottomBorder
import com.pako2k.banknotescatalog.ui.common.rightBorder
import com.pako2k.banknotescatalog.ui.theme.color_stats_collection
import com.pako2k.banknotescatalog.ui.theme.color_stats_issuer
import com.pako2k.banknotescatalog.ui.theme.color_table_row_even
import com.pako2k.banknotescatalog.ui.theme.color_table_row_odd
import kotlin.math.max


@Composable
private fun getTitleColor() : Color = MaterialTheme.colorScheme.onSecondary

@Composable
private fun getTitleStyle() : TextStyle = MaterialTheme.typography.bodyMedium

@Composable
private fun getSubTitleStyle() : TextStyle = MaterialTheme.typography.bodySmall

@Composable
private fun getDataStyle() : TextStyle = MaterialTheme.typography.bodySmall

@Composable
private fun getDataColors() = listOf(
    Color.Black,
    color_stats_issuer,
    color_stats_collection
)

private val subColWidth = 46.dp



@Composable
fun StatsTableHeader(
    title : String,
    subtitles : Set<String>,
    isLoggedIn : Boolean,
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

    Row(
        modifier = Modifier
            .bottomBorder(Color.Black, 2f)
    ) {
        subtitles.take(max(0,subtitles.size-1)).forEach {
            HeaderSubtitle(it)
        }
        subtitles.lastOrNull()?.let{
            HeaderSubtitle(it, if (isLoggedIn) 1f else 0.4f)
        }
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
fun StatsTableRowTitles(
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
fun StatsTableData(
    rowIndex : Int,
    data : List<Int>,
    isLoggedIn: Boolean
){
    Row (
        modifier = Modifier
            .background(if (rowIndex % 2 == 0) color_table_row_even else color_table_row_odd)
            .rightBorder(Color.Black)
            .padding(vertical = dimensionResource(id = R.dimen.small_padding))
    ){
        data.take(max(0, data.size-1)).forEachIndexed { index, it ->
            DataText(it, getDataStyle(), getDataColors()[index])
        }
        data.lastOrNull()?.let {
            DataText(it, getDataStyle(), getDataColors()[data.size-1], if(isLoggedIn) 1f else 0.4f)
        }
    }
}


@Composable
fun StatsTableFooterData(
    data : List<Int>,
    isLoggedIn: Boolean
){
    Row(
        modifier = Modifier
            .padding(vertical = dimensionResource(id = R.dimen.small_padding))
    ) {
        data.take(max(0, data.size-1)).forEach {
            DataText(it, getTitleStyle(), getTitleColor())
        }
        data.lastOrNull()?.let {
            DataText(it, getTitleStyle(), getTitleColor(), if(isLoggedIn) 1f else 0.4f)
        }
    }
}


@Composable
private fun DataText(
    value : Int,
    style : TextStyle,
    color: Color,
    alpha : Float = 1f
){
    Text(
        value.toString(),
        style = style,
        textAlign = TextAlign.Center,
        color = color,
        modifier = Modifier
            .widthIn(min = subColWidth)
            .alpha(alpha)
    )
}

