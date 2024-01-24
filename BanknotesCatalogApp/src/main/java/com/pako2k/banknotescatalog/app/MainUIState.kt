package com.pako2k.banknotescatalog.app

import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.ui.parts.Sorting

enum class ComponentState {
    LOADING,
    DONE,
    FAILED
}

data class MainUiState (
    val mainInitialization : ComponentState = ComponentState.LOADING,

    val selectedContinent: UInt? = null,

    val userLoggedIn : Boolean = false,

    val territoriesSortedBy : Territory.SortableCol = Territory.SortableCol.NAME,
    val territoriesSortingDir : Sorting = Sorting.ASC,

)

