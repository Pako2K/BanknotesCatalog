package com.pako2k.banknotescatalog.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pako2k.banknotescatalog.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class LoginViewModel private constructor(
    ctx: Context
) : ViewModel() {

    // Private so it cannot be updated outside this MainViewModel
    private val _loginUIState = MutableStateFlow(LoginUIState())
    // Public property to read the UI state
    val loginUIState = _loginUIState.asStateFlow()


    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BanknotesCatalogApplication
                Log.d(application.getString(R.string.app_log_tag), "Create DenominationViewModel")

                LoginViewModel(
                    application.applicationContext
                )
            }
        }
    }

    init {
        Log.d(ctx.getString(R.string.app_log_tag), "Start INIT CurrencyViewModel")
    }

    fun setUsername(username : String){
        _loginUIState.update { currentState ->
            currentState.copy(
                username = username,
                isInvalidUserPwd = false
            )
        }
    }

    fun setPassword (password : String){
        _loginUIState.update { currentState ->
            currentState.copy(
                password = password,
                isInvalidUserPwd = false
            )
        }
    }

    fun logIn() : String {
        var sessionId = ""

        if (loginUIState.value.username == "PACO" &&
            loginUIState.value.password == "XYZ"){
            sessionId = "session-id"
            _loginUIState.update { currentState ->
                currentState.copy(
                    isInvalidUserPwd = false
                )
            }
        } else{
            _loginUIState.update { currentState ->
                currentState.copy(
                    isInvalidUserPwd = true
                )
            }
        }
        return sessionId
    }

    fun logOut(sessionId : String) : Boolean {
        return sessionId.isNotEmpty()
    }

}