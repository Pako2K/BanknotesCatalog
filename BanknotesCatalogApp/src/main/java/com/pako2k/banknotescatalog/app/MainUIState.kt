package com.pako2k.banknotescatalog.app

import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.Currency
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryType
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

    val territoriesSortedBy : Int = 2,
    val territoriesSortingDir : Sorting = Sorting.ASC

)

