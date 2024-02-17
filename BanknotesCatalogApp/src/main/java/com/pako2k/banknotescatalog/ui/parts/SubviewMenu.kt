package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme

class SubviewOptions(
    vararg options: String
){
    val options : Set<String> = options.toSet()
}

@Composable
fun SubviewMenu(
    subviewOptions : SubviewOptions,
    selectedOption: String,
    onClick : (String) -> Unit
){
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val buttonMaxWidth = (screenWidth - (subviewOptions.options.size+1) * 10) / subviewOptions.options.size
    val buttonMinWidth = buttonMaxWidth - screenWidth*0.2f / subviewOptions.options.size

    Surface(modifier = Modifier.height(IntrinsicSize.Min)) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.background_header),
            contentScale = ContentScale.Crop,
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.medium_padding),
                    vertical = dimensionResource(id = R.dimen.small_padding)
                )
        ) {
            subviewOptions.options.forEach{ opt ->
                SubviewOption(
                    opt,
                    buttonMinWidth = buttonMinWidth.dp,
                    buttonMaxWidth = buttonMaxWidth.dp,
                    isSelected = opt == selectedOption,
                    onClick = onClick)
            }
        }
    }
}

@Composable
private fun SubviewOption(
    opt : String,
    isSelected : Boolean,
    buttonMaxWidth : Dp,
    buttonMinWidth : Dp,
    onClick : (String) -> Unit
) {
    val colors =
        if (isSelected) ButtonDefaults.buttonColors()
        else ButtonDefaults.elevatedButtonColors()

    ElevatedButton(
        elevation =
            if (!isSelected) ButtonDefaults.buttonElevation(defaultElevation = dimensionResource(id = R.dimen.small_padding))
            else null,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.medium_padding)),
        colors = colors,
        modifier = Modifier
            .widthIn(min = buttonMinWidth, max = buttonMaxWidth)
            .defaultMinSize(minHeight = 1.dp, minWidth = 0.dp),
        onClick = { onClick(opt)}
    ) {
        Text(
            text = opt,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.inverseSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}



private const val TEST_WIDTH = 380

@Preview(widthDp = TEST_WIDTH)
@Composable
fun SubviewMenuPreview() {
    BanknotesCatalogTheme {
        val menu = SubviewOptions("Issues", "Banknotes", "Stats", "Info")

        SubviewMenu(
            menu,
            "Issues",
            onClick = {}
        )
    }
}
