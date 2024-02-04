package com.pako2k.banknotescatalog.app

enum class ComponentState {
    LOADING,
    DONE,
    FAILED
}

data class MainUiInitializationState (
    val state : ComponentState = ComponentState.LOADING
)