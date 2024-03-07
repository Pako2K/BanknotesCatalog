package com.pako2k.banknotescatalog.ui.views.subviews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.ShowPreferenceEnum
import com.pako2k.banknotescatalog.data.ShowPreferences
import com.pako2k.banknotescatalog.ui.parts.CheckButtonGroup
import com.pako2k.banknotescatalog.ui.parts.CheckOption
import com.pako2k.banknotescatalog.ui.parts.CommonCard
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme

@Composable
fun SettingsUI (
    options : ShowPreferences,
    onClick : (ShowPreferenceEnum, Boolean) -> Unit,
    onClose : () -> Unit
){
    Dialog(onDismissRequest = onClose ) {
        CommonCard(title = "Settings", onClose = onClose ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
            ){
                CheckButtonGroup(
                    Modifier,
                    "Show in Summary",
                    MaterialTheme.colorScheme.onBackground,
                    MaterialTheme.colorScheme.onPrimary,
                    CheckOption("Dates",options.showDates) { onClick(ShowPreferenceEnum.KEY_SHOW_DATES, it) },
                    CheckOption("Currency Stats",options.showCurrencies) { onClick(ShowPreferenceEnum.KEY_SHOW_CUR, it) },
                    CheckOption("Issue Stats",options.showIssues) { onClick(ShowPreferenceEnum.KEY_SHOW_ISSUES, it) },
                    CheckOption("Face Value Stats",options.showFaceValues) { onClick(ShowPreferenceEnum.KEY_SHOW_VALUES, it) },
                    CheckOption("Note Types Stats",options.showNoteTypes) { onClick(ShowPreferenceEnum.KEY_SHOW_NOTES, it) },
                    CheckOption("Variants Stats",options.showVariants){ onClick(ShowPreferenceEnum.KEY_SHOW_VARIANTS, it) },
                    CheckOption("Collection Price",options.showPrice){ onClick(ShowPreferenceEnum.KEY_SHOW_PRICES, it) },
                )
            }
        }
    }
}



@Preview//(widthDp = 300, heightDp = 200)
@Composable
private fun SettingsUIPreview() {
    BanknotesCatalogTheme {
        SettingsUI(
            ShowPreferences(
                showDates = true,
                showCurrencies = true,
                showIssues = false,
                showFaceValues = false,
                showNoteTypes = true,
                showVariants = true,
                showPrice = true
            ),
            {_,_ ->},
            {}
        )
    }
}