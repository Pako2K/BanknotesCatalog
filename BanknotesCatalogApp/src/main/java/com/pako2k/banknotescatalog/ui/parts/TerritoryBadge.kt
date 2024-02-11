package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.ui.theme.background_color_territory_badge
import com.pako2k.banknotescatalog.ui.theme.color_territory_badge

@Composable
fun TerritoryBadge(
    territory : Territory,
    textStyle : TextStyle = MaterialTheme.typography.bodySmall,
    maxTextWidth : Dp,
    onClick: (territoryID: UInt)->Unit,
){
    val flagHeight = textStyle.lineHeight.value.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = dimensionResource(id = R.dimen.small_padding))
            .padding(bottom = dimensionResource(id = R.dimen.small_padding))
            .clickable {
                onClick(territory.id)
            }
            .background(background_color_territory_badge)
            .border(
                width = Dp.Hairline,
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.outline
            )
    ) {
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs_padding)))
        if (territory.flag != null)
            Image(
                bitmap = territory.flag as ImageBitmap,
                contentDescription = stringResource(id = R.string.content_description_flag),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(flagHeight)
                    .widthIn(max=flagHeight * 1.4f)
                    .clip(RoundedCornerShape(40))
            )
        else
            Image(
                painter = painterResource(id = R.drawable.m_flag_icon),
                contentDescription = stringResource(id = R.string.content_description_flag),
                alignment = Alignment.Center,
                modifier = Modifier
                    .height(flagHeight)
                    .clip(RoundedCornerShape(80))
            )

        Text(
            text = territory.name,
            overflow = TextOverflow.Ellipsis,
            color = color_territory_badge,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.xs_padding),
                    end = dimensionResource(id = R.dimen.small_padding)
                )
                .padding(vertical = dimensionResource(id = R.dimen.xs_padding))
                .widthIn(max = maxTextWidth)
        )
    }
}

