package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.Territory

@Composable
fun TerritoryView(
    territory : Territory
){
    Log.d(stringResource(id = R.string.app_log_tag),"Start Country")

    Text(text = "${territory.name} DETAILS")
}