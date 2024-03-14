package com.pako2k.banknotescatalog.data.stats


class IssueYearSummaryStats(
    val total : Data,
){
    data class Data(
        val total : Int,
        val collection : Int = 0
    )
}
