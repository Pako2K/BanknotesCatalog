package com.pako2k.banknotescatalog.data

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val id: String,
    val isAdmin : Boolean,
    val expiration : Int //Session expiration time in milliseconds
)