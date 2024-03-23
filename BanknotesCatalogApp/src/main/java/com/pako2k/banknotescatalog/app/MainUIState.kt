package com.pako2k.banknotescatalog.app


data class MainUiInitializationState (
    val state : ComponentState = ComponentState.LOADING,
)


data class MainUiState (
    val selectedContinent: UInt? = null,
    val userLoggedIn : Boolean = false
)
