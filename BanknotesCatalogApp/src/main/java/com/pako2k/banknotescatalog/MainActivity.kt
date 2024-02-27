package com.pako2k.banknotescatalog

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pako2k.banknotescatalog.app.MainViewModel
import com.pako2k.banknotescatalog.ui.BanknotesCatalogUI
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(getString(R.string.app_log_tag), "${this::class.simpleName} - ${{}.javaClass.enclosingMethod?.name}")
        setContent {
            Log.d(stringResource(id = R.string.app_log_tag),"${this::class.simpleName} - setContent")
            BanknotesCatalogTheme {
                // A surface container using the 'background' color from the theme
                val windowSizeClass : WindowSizeClass = calculateWindowSizeClass(this)

                val displayMetrics: DisplayMetrics = applicationContext.resources.displayMetrics
                val screenWidth = Dp(displayMetrics.widthPixels / displayMetrics.density)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.d(stringResource(id = R.string.app_log_tag),"Start Main Surface")

                    // Create the application ViewModel using the factory
                    val model: MainViewModel =  viewModel(factory = MainViewModel.Factory)
                    BanknotesCatalogUI(windowSizeClass, screenWidth, model)
                }
            }
        }
    }
}
