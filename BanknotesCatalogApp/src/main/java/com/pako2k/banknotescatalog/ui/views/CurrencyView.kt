package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pako2k.banknotescatalog.R

@Composable
fun CurrencyView(
    currency : Map<String,Any?>,
){
    Log.d(stringResource(id = R.string.app_log_tag),"Start Currency")

    Text(text = "${currency["name"]} DETAILS")
}