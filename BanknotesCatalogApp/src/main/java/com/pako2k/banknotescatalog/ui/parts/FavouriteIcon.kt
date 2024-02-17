package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.ui.theme.color_is_favourite


@Composable
fun FavouriteIcon(
    size : Dp,
    isFavourite : Boolean,
    onAddFavourite : (Boolean) -> Unit
){
    IconButton(
        onClick = { onAddFavourite(!isFavourite) }
    ) {
        // Add outline
        if (isFavourite)
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(size+5.dp)
            )

        Icon(
            Icons.Outlined.Star,
            contentDescription = "Favourite Icon",
            tint = if (isFavourite) color_is_favourite else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(size)
        )
    }
}