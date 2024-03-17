package com.pako2k.banknotescatalog.app


data class LoginUIState (
    val username: String = "",
    val password: String = "",
    val sessionId : String? = null,
    val isInvalidUserPwd : Boolean = false,

    val loginState : ComponentState = ComponentState.DONE
)