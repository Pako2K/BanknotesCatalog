package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme

@Composable
fun TerritoryView(
    windowWidth: WindowWidthSizeClass,
    data : Map<String,Any?>,
    onCountryClick: (territoryID: UInt)->Unit,
    ){
    Log.d(stringResource(id = R.string.app_log_tag),"Start Country")

    TerritoryHeader(windowWidth, data, onCountryClick)
    //TerritoryMenu()
   // TerritoryDataArea()
}

@Composable
fun TerritoryHeader(
    windowWidth: WindowWidthSizeClass,
    data : Map<String,Any?>,
    onCountryClick: (territoryID: UInt)->Unit,
){
    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_territory_header),
            contentScale = ContentScale.Crop,
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )
        Row {
            // Continent Icon and name
            Column {

            }
            // Territory Flag
            val value = data["flag"] as ImageBitmap?
            if (value != null)
                Image(
                    bitmap = value,
                    contentDescription = stringResource(id = R.string.content_description_flag),
                    modifier = Modifier.height(dimensionResource(id = R.dimen.territory_flag_size))
                )
            else
                Image(
                    painter = painterResource(id = R.drawable.m_flag_icon),
                    contentDescription = null,
                    modifier = Modifier.height(dimensionResource(id = R.dimen.territory_flag_size))
                )



            // Territory Names / Dates
            Column {
                Text(text = "${data["name"]} DETAILS")
            }

        }
    }

}

@Composable
fun TerritoryMenu(){
    Row {
        Text(text = "Currencies")
        Text(text = "Banknotes")
        Text(text = "Statistics")
    }

}

@Composable
fun TerritoryDataArea(){
    // Content to be determined by the selected menu option
}


@Preview
@Composable
fun TerritoryViewPreview() {
    BanknotesCatalogTheme {
        TerritoryView(
            windowWidth = WindowWidthSizeClass.Compact,
            data = mapOf("id" to 1u, "iso3" to "ARG", "flag" to null, "name" to "Argentina", "type" to "Ind", "start" to 1926, "end" to 1967),
            onCountryClick = { _ -> }
        )
    }
}
