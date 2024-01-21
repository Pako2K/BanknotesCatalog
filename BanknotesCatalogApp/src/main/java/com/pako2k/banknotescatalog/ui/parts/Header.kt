package com.pako2k.banknotescatalog.ui.parts


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


@Composable
fun Header(
    modifier: Modifier = Modifier
){
    Log.d(stringResource(id = R.string.app_log_tag),"Start Header")

    Column {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .height(intrinsicSize = IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.small_padding))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.boc),
                        contentDescription = "Banknote Icon",
                        modifier = Modifier.size(
                            width = dimensionResource(id = R.dimen.header_height) * 2,
                            height = dimensionResource(id = R.dimen.header_height)
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.header_title),
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier
                            .padding(start = dimensionResource(id = R.dimen.medium_padding))
                    )
                }
            }
        }

        //Add shadow
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Black,
                        1f to Color.LightGray
                    )
                )
                .height(4.dp)
                .fillMaxWidth()
                .alpha(0.2f)
        )
    }
}


@Preview
@Composable
fun HeaderPreview() {
    BanknotesCatalogTheme {
        Header()
    }
}