package com.pako2k.banknotescatalog.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.UserSession
import com.pako2k.banknotescatalog.data.repo.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.repo.UserCredentialsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException


class LoginViewModel private constructor(
    ctx: Context,
    private val repository: BanknotesCatalogRepository,
    private val userCredentials: UserCredentialsRepository
) : ViewModel() {

    var loginError : String? = null
        private set

    // Private so it cannot be updated outside this MainViewModel
    private val _loginUIState = MutableStateFlow(LoginUIState())
    // Public property to read the UI state
    val loginUIState = _loginUIState.asStateFlow()



    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BanknotesCatalogApplication
                Log.d(application.getString(R.string.app_log_tag), "Create LoginViewModel")

                LoginViewModel(
                    application.applicationContext,
                    application.repository,
                    application.userCredentialsRepository
                )
            }
        }
    }

    init {
        Log.d(ctx.getString(R.string.app_log_tag), "Start INIT LoginViewModel")

        viewModelScope.launch {
            val credentialsCache = userCredentials.userCredentialsFlow.first()
            _loginUIState.update { currentState ->
                currentState.copy(
                    username = credentialsCache.username,
                    password = credentialsCache.password
                )
            }
        }
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

    suspend fun logIn() : UserSession? {
        _loginUIState.update { currentState ->
            currentState.copy(
                loginState = ComponentState.LOADING
            )
        }

        var session : UserSession? = null
        try {
            session = repository.getUserSession(loginUIState.value.username, loginUIState.value.password)

            // Update data store
            userCredentials.updateCredentials(loginUIState.value.username, loginUIState.value.password, session)
        }
        catch (exc : HttpException){
            Log.e("Error", exc.toString())

            if (exc.code() == 401){
                loginError = null
                _loginUIState.update { currentState ->
                    currentState.copy(
                        isInvalidUserPwd = true
                    )
                }
            }
            else if (exc.code() in listOf(400, 404, 500)){
                loginError = "Error: " + exc.message.toString().take(25)
                _loginUIState.update { currentState ->
                    currentState.copy(
                        isInvalidUserPwd = false
                    )
                }
            }
            else {
                _loginUIState.update { currentState ->
                    loginError = "OMG!: " + exc.toString().take(25)
                    currentState.copy(
                        isInvalidUserPwd = false
                    )
                }
            }
        }
        catch (exc : Exception){
            Log.e("Error", exc.toString())
            Log.e("Error", exc.cause.toString())

            _loginUIState.update { currentState ->
                loginError = "OMG!!!: " + exc.toString().take(25)
                currentState.copy(
                    isInvalidUserPwd = false
                )
            }
        }

        _loginUIState.update { currentState ->
            currentState.copy(
                loginState = ComponentState.DONE
            )
        }

        return session
    }
}