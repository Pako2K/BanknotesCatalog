package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.LoginViewModel
import com.pako2k.banknotescatalog.ui.theme.md_theme_light_shadow
import kotlinx.coroutines.launch

@Composable
fun LoginView(
    viewModel: LoginViewModel,
    onLoggedIn : (sessionId : String?, username : String, password : String) -> Unit
){
    Log.d(stringResource(id = R.string.app_log_tag), "Start Login")

    val loginUiState = viewModel.loginUIState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val invalidMsg = stringResource(id = R.string.login_invalid)


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = dimensionResource(id = R.dimen.xl_padding))
            .verticalScroll(rememberScrollState())

    ){
        Text(
            text = stringResource(id = R.string.login_title),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.xl_padding))
        )

        InputField(
            text = loginUiState.value.username,
            defaultText = stringResource(id = R.string.login_username),
            icon = Icons.Filled.AccountCircle,
            isInvalid = loginUiState.value.isInvalidUserPwd,
            onValueChange = {viewModel.setUsername(it)},
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.xl_padding),
                    end = dimensionResource(id = R.dimen.xl_padding),
                    bottom = dimensionResource(id = R.dimen.large_padding)
                )

        )
        InputField(
            text = loginUiState.value.password,
            defaultText = stringResource(id = R.string.login_password),
            icon = Icons.Filled.Lock,
            isPassword = true,
            isInvalid = loginUiState.value.isInvalidUserPwd,
            onValueChange = {viewModel.setPassword(it)},
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.xl_padding),
                    end = dimensionResource(id = R.dimen.xl_padding),
                    bottom = dimensionResource(id = R.dimen.xl_padding)
                )
        )
        ElevatedButton(
            onClick = {
                val prevState = loginUiState.value.isInvalidUserPwd
                val sessionId = viewModel.logIn()
                if (sessionId.isNotEmpty())
                    onLoggedIn(sessionId, loginUiState.value.username, loginUiState.value.password)
                else
                    if(!prevState)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = invalidMsg,
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
            contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.xxl_padding)),
            modifier = Modifier.focusable(true)
        ) {
            Text(
                text = stringResource(id = R.string.login_button),
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        TextButton(onClick = { /*TODO*/ }) {
            Text(
                text = stringResource(id = R.string.login_signup_button),
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        TextButton(onClick = { /*TODO*/ }) {
            Text(
                text = stringResource(id = R.string.login_change_pwd),
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        TextButton(onClick = { /*TODO*/ }) {
            Text(
                text = stringResource(id = R.string.login_reset_pwd),
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }


    Box(
        modifier = Modifier
            .padding(top = dimensionResource(id = R.dimen.medium_padding))
             .fillMaxWidth()
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .align(Alignment.TopCenter)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                .border(width = Dp.Hairline, color = Color.Black)

        ) {
            Text(
                it.visuals.message,
                color = Color.White,
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.large_padding),
                        horizontal = dimensionResource(id = R.dimen.xl_padding))
            )
        }
    }
}


@Composable
fun InputField(
    text : String,
    defaultText : String,
    icon : ImageVector,
    isPassword : Boolean = false,
    isInvalid : Boolean,
    modifier: Modifier = Modifier,
    onValueChange : (String) -> Unit,
) {
    OutlinedTextField(
        value = text,
        placeholder = {Text(defaultText)},
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.medium_padding))
            )
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraSmall,
        onValueChange = {
            onValueChange(it)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedPlaceholderColor = Color.Transparent,
            unfocusedPlaceholderColor = md_theme_light_shadow
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation('*') } else VisualTransformation.None,
        isError = isInvalid,
        modifier = modifier
    )
}
