package com.pako2k.banknotescatalog.ui.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.pako2k.banknotescatalog.data.Territory

@Composable
fun Country(
    territory : Territory
){
    Text(text = "${territory.name} DETAILS")
}