package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme

data class CheckOption(
    val title : String,
    val isSelected : Boolean,
    val onCheckChanged : (Boolean) -> Unit
)

private const val TITLE_PADDING = 4

@Composable
fun CheckButtonGroup(
    modifier: Modifier = Modifier,
    title : String,
    color : Color,
    background : Color,
    vararg options : CheckOption
){
    val titleStyle = MaterialTheme.typography.headlineSmall
    val offset = titleStyle.fontSize.value.dp / 2 + 1.dp
    Box(
        modifier = modifier
            .background(background)
            .padding(bottom = offset + 1.dp)
            .padding(horizontal = 1.dp)
            .width(IntrinsicSize.Max)
    ){
        Column(
            modifier = Modifier
                .offset(x = 0.dp, y = offset)
                .border(
                    width = 1.dp,
                    color = color.copy(alpha = .75f),
                    shape = RectangleShape
                )
                .padding(
                    vertical = dimensionResource(id = R.dimen.small_padding),
                    horizontal = dimensionResource(id = R.dimen.medium_padding)
                )
                .fillMaxWidth()

        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.small_padding)))
            options.forEach {
                CheckOptionUI(it.title, color, it.isSelected, it.onCheckChanged)
            }
        }
        Text(
            title,
            color = color,
            style = titleStyle,
            modifier = Modifier
                .padding(horizontal = TITLE_PADDING.dp)
                .background(background)
                .padding(horizontal = TITLE_PADDING.dp)
        )
    }
}


@Composable
private fun CheckOptionUI(
    title : String,
    color : Color,
    isSelected : Boolean,
    onCheckChanged : (Boolean) -> Unit
){
    val style = MaterialTheme.typography.bodyLarge
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onCheckChanged(!isSelected) },
            modifier = Modifier.size( style.fontSize.value.dp * 1.8f)
        )
        Text(
            title,
            color = color,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable { onCheckChanged(!isSelected) }
        )
    }
}


@Preview//(widthDp = 300, heightDp = 200)
@Composable
private fun CheckButtonGroupPreview() {
    BanknotesCatalogTheme {
        CheckButtonGroup (Modifier,
            title = "Group Title 23 wer", color = Color.Black, background = Color.White,
            CheckOption("op 1",  true, {}),
            CheckOption("op 2", false, {}),
            CheckOption("op", false, {})
        )
    }
}