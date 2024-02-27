package com.pako2k.banknotescatalog.data


data class FilterDates(
    val from : Int?,
    val to : Int?
){
    val isValid = (from == null || to == null || from <= to)
}