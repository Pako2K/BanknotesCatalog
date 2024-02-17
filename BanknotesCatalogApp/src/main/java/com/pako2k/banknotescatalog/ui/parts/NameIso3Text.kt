package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import com.pako2k.banknotescatalog.R

@Composable
fun NameIso3Text(
    name : String,
    iso3 : String?,
    modifier : Modifier = Modifier
) {
    Row (modifier){
        Text(
            text = name,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.displayLarge,
            maxLines = 1,
            modifier = Modifier
                .alignByBaseline()
                .padding(start = dimensionResource(id = R.dimen.medium_padding))
        )
        if (iso3 != null)
            Text(
                text = iso3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.displaySmall,
                maxLines = 1,
                modifier = Modifier.alignByBaseline()
            )
    }

}