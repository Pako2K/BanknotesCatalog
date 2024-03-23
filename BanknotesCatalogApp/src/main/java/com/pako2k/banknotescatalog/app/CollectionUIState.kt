package com.pako2k.banknotescatalog.app

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.data.repo.CollectionFieldCurrency
import com.pako2k.banknotescatalog.data.repo.CollectionFieldDenomination
import com.pako2k.banknotescatalog.data.repo.CollectionFieldPrice
import com.pako2k.banknotescatalog.data.repo.CollectionFieldPurchaseDate
import com.pako2k.banknotescatalog.data.repo.CollectionFieldSeller
import com.pako2k.banknotescatalog.data.repo.CollectionFieldTerritory
import com.pako2k.banknotescatalog.data.repo.SortDirection

data class CollectionUIState(
    val collectionTable : CollectionTable = CollectionTable(
        columns = listOf(
            CollectionTableColumn(title = "Territory",  width = 130.dp, isSortable = true, linkedField = CollectionFieldTerritory,align = Alignment.CenterStart, isClickable = true),
            CollectionTableColumn(title = "Cat. Id", width = 65.dp, isSortable = false, align = Alignment.CenterStart, isClickable = true),
            CollectionTableColumn(title = "Denom.",  width = 130.dp, isSortable = true, linkedField = CollectionFieldDenomination ),
            CollectionTableColumn(title = "Currency",  width = 110.dp, isSortable = true, linkedField = CollectionFieldCurrency, align = Alignment.CenterStart, isClickable = true),
            CollectionTableColumn(title = "Grade", width = 45.dp, isSortable = false, isGrading = true),
            CollectionTableColumn(title = "Qty",  width = 28.dp, isSortable = false),
            CollectionTableColumn(title = "Price", width = 60.dp, isSortable = true, linkedField = CollectionFieldPrice),
            CollectionTableColumn(title = "Seller",  width = 90.dp, isSortable = true, linkedField = CollectionFieldSeller),
            CollectionTableColumn(title = "Purchased", width = 80.dp, isSortable = true, linkedField= CollectionFieldPurchaseDate),
            CollectionTableColumn(title = "Comments", width = 120.dp, isSortable = false, align = Alignment.CenterStart)
        ),
        sortedBy = 0,
        sortDirection = SortDirection.ASC,
        minFixedColumns = 2
    ),

    val state : ComponentState = ComponentState.LOADING,

    val collectionTableUpdateTrigger : Boolean = false
)