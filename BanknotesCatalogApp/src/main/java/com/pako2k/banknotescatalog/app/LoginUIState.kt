package com.pako2k.banknotescatalog.app


data class LoginUIState (
    val username: String = "",
    val password: String = "",
    val isInvalidUserPwd : Boolean = false
)