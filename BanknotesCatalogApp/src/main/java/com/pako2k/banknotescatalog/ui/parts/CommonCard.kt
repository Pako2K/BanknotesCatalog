package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme



@Composable
fun CommonCard(
    title : String,
    onClose : () -> Unit,
    content : @Composable ()-> Unit
){
    ElevatedCard(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
    ) {
        // Another Column to set the Card internal padding
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.small_padding))
                .width(IntrinsicSize.Max)
        ) {
            CardTitle(title, onClose)
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.medium_padding)))
            content()
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
        }
    }

}

@Composable
private fun CardTitle(
    title : String,
    onClose : () -> Unit
){
    val style = MaterialTheme.typography.headlineMedium
    Row (
        verticalAlignment = Alignment.CenterVertically,
    ){
        Text(
            text = title,
            style = style,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.small_padding))
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


@Preview
@Composable
private fun CommonCardPreviewPortrait1() {
    BanknotesCatalogTheme {
        CommonCard ("EXAMPLE TITLE", {}){
            Text("very long text to test the card")
        }
    }
}