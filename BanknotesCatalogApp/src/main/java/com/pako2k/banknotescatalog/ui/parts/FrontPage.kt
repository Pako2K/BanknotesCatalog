package com.pako2k.banknotescatalog.ui.parts

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.ComponentState
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme

@Composable
fun FrontPage(state : ComponentState = ComponentState.DONE, modifier: Modifier = Modifier){
    Log.d(stringResource(id = R.string.app_log_tag),"Start FrontPage")
    Column (
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    0.0f to MaterialTheme.colorScheme.primaryContainer,
                    0.9f to MaterialTheme.colorScheme.background
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val alphaFailed = if(state == ComponentState.FAILED) 0.5f else 1f
        Box (
            contentAlignment = Alignment.Center
        ){

            Image(
                painter = painterResource(id = R.drawable.boc_3d),
                contentDescription = null,
                alpha = alphaFailed * 0.4f
            )
            Text(
                text = stringResource(id = R.string.main_title),
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(alphaFailed)
            )
        }
        when(state) {
            ComponentState.LOADING -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .width(48.dp)
                )
            }
            ComponentState.DONE -> {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                ) {
                    val subtitles = stringArrayResource(id = R.array.main_subtitles)
                    for (subtitle in subtitles){
                        Text(text = subtitle,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(0.333f)
                                .padding(dimensionResource(id = R.dimen.small_padding))
                        )
                    }
                }
            }
            ComponentState.FAILED -> {
                Text(text = stringResource(id = R.string.connection_error),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}




@Preview (device = "spec: orientation=landscape, width=800dp, height=400dp")
@Composable
fun FrontPagePreviewLandscape() {
    BanknotesCatalogTheme {
        FrontPage(ComponentState.DONE, modifier = Modifier.fillMaxSize())
    }
}

@Preview (device = "spec: orientation=portrait, width=400dp, height=800dp")
@Composable
fun FrontPagePreviewPortrait() {
    BanknotesCatalogTheme {
        FrontPage(ComponentState.DONE, modifier = Modifier.fillMaxSize())
    }
}

@Preview (device = "spec: orientation=landscape, width=800dp, height=400dp")
@Composable
fun FrontPagePreview() {
    BanknotesCatalogTheme {
        FrontPage(ComponentState.FAILED, modifier = Modifier.fillMaxSize())
    }
}