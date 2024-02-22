package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.pako2k.banknotescatalog.R
import kotlinx.coroutines.launch

@Composable
fun HScrollButton(
    hScrollState : ScrollState
) {
    val coroutineScope = rememberCoroutineScope()

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