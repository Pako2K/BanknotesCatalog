package com.pako2k.banknotescatalog.app

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import com.pako2k.banknotescatalog.data.repo.CollectionSortableField
import com.pako2k.banknotescatalog.data.repo.SortDirection
import com.pako2k.banknotescatalog.data.repo.SortableField


data class CollectionTableColumn(
    val title: String,
    val align: Alignment = Alignment.Center,
    val width: Dp,
    val isClickable: Boolean = false,
    val isSortable: Boolean  = false,
    val isGrading: Boolean = false,
    val linkedField: CollectionSortableField? = null
){
    var sortedDirection : SortDirection? = null
}


class CollectionTable(
    val columns : List<CollectionTableColumn>,
    sortedBy : Int,
    sortDirection : SortDirection,
    val minFixedColumns : Int
){
    var sortedBy : Int = sortedBy
        private set

    init {
        columns[sortedBy].sortedDirection = sortDirection
    }

    fun sortBy(colIndex : Int){
        val col = columns[colIndex]
        if (col.isSortable) {
            if (sortedBy == colIndex) {
                col.sortedDirection = if (col.sortedDirection == SortDirection.ASC) SortDirection.DESC
                else SortDirection.ASC
            } else {
                columns[sortedBy].sortedDirection = null
                col.sortedDirection = SortDirection.ASC
                sortedBy = colIndex
            }
        }
    }

    fun getCol(linkedField : SortableField) : Int? {
        val index = columns.indexOfFirst { it.linkedField == linkedField }
        return if (index != -1) index
        else null
    }
}