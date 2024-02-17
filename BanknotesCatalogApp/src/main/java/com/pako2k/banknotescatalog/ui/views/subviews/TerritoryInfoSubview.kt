package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryTypes
import com.pako2k.banknotescatalog.ui.parts.TerritoryBadge

@Composable
fun TerritoryInfoSubview(
    data : Territory,
    onClick : (UInt) -> Unit
){
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Surface (
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.small_padding))
            .verticalScroll(rememberScrollState())
    ){
        Column {
            if (data.description != null)
                ElevatedCard (
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.small_padding))
                        .fillMaxWidth()
                ) {
                    Row {
                        val textStyle = MaterialTheme.typography.bodySmall
                        val textColor = MaterialTheme.colorScheme.onTertiaryContainer
                        Text(
                            text = "History: ",
                            style = textStyle,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            modifier = Modifier
                                .padding(top = dimensionResource(id = R.dimen.medium_padding))
                                .padding(start = dimensionResource(id = R.dimen.large_padding))
                        )
                        Text(
                            text = data.description,
                            style = textStyle,
                            color = textColor,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier
                                .padding(vertical = dimensionResource(id = R.dimen.medium_padding))
                                .padding(end = dimensionResource(id = R.dimen.large_padding))
                        )
                    }
                }

            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.medium_padding)))

            // Relations
            val maxItemWidth =
                if (screenWidth < 450) 180.dp
                else 260.dp
            if (data.territoryType.name == TerritoryTypes.TER.value) {
                LinkedTerritories("Belongs to", listOf(data.parentExt!!), maxItemWidth, onClick)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
            }
            if (data.territoryType.name == TerritoryTypes.NR.value) {
                LinkedTerritories("Claimed by", listOf(data.parentExt!!), maxItemWidth, onClick)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
            }

            if (data.predecessorsExt.isNotEmpty()) {
                LinkedTerritories("Preceded by ", data.predecessorsExt, maxItemWidth, onClick)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
            }
            if (data.successorsExt.isNotEmpty()) {
                LinkedTerritories("Succeeded by ", data.successorsExt, maxItemWidth, onClick)
            }

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinkedTerritories(
    title : String,
    territories: List<Territory>,
    maxItemWidth : Dp,
    onClick: (territoryID: UInt)->Unit,
){
    ElevatedCard (
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.small_padding))
            .fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                .padding(top = dimensionResource(id = R.dimen.medium_padding))
                .padding(bottom = dimensionResource(id = R.dimen.small_padding))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.xs_padding))
            )

            territories.forEach {
                TerritoryBadge(territory = it, maxTextWidth = maxItemWidth, onClick = onClick)
            }
        }
    }
}