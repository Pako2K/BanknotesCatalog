package com.pako2k.banknotescatalog.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.Dp
import com.pako2k.banknotescatalog.data.SortDirection
import com.pako2k.banknotescatalog.data.SortableField


enum class StatsSubColumn (val title : String){
    CATALOG (title = "Cat."),
    COLLECTION (title = "Col.")
}

data class SummaryTableColumn(
    val title: String,
    val align: Arrangement.Horizontal = Arrangement.Center,
    val width: Dp,
    val isImage: Boolean = false,
    val isStats: Boolean = false,
    val isClickable: Boolean = false,
    val isSortable: Boolean  = false,
    val linkedField: SortableField? = null
){
    var sortedDirection : SortDirection? = null
    var sortedStats : StatsSubColumn? = null
}


class SummaryTable(
    val columns : List<SummaryTableColumn>,
    sortedBy : Int,
    sortDirection : SortDirection,
    val minFixedColumns : Int
){
    //    private val columnsMap : Map<Int,SummaryTableColumnClass>
    var sortedBy : Int = sortedBy
        private set

    init {
        columns[sortedBy].sortedDirection = sortDirection
    }

    fun sortBy(colIndex : Int, statsCol : StatsSubColumn?){
        val col = columns[colIndex]
        if (col.isSortable) {
            if (!col.isStats ) {
                if (sortedBy == colIndex) {
                    col.sortedDirection = if (col.sortedDirection == SortDirection.ASC) SortDirection.DESC
                    else SortDirection.ASC
                } else {
                    columns[sortedBy].sortedStats = null
                    columns[sortedBy].sortedDirection = null
                    col.sortedDirection = SortDirection.ASC
                    sortedBy = colIndex
                }
            }
            else{
                // Logic for Stats column
                if (sortedBy == colIndex) {
                    if (col.sortedStats == statsCol)
                        col.sortedDirection = if (col.sortedDirection == SortDirection.ASC) SortDirection.DESC
                        else SortDirection.ASC
                    else{
                        col.sortedStats = statsCol
                        col.sortedDirection = SortDirection.DESC
                    }
                }
                else {
                    columns[sortedBy].sortedDirection = null
                    columns[sortedBy].sortedStats = null
                    sortedBy = colIndex
                    col.sortedDirection = SortDirection.DESC
                    col.sortedStats = statsCol
                }
            }
        }
    }

    fun getCol(linkedField : SortableField) : Int? {
        val index = columns.indexOfFirst { it.linkedField == linkedField }
        return if (index != -1) index
        else null
    }
}