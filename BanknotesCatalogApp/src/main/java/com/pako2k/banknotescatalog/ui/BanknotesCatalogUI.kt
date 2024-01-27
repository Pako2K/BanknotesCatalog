package com.pako2k.banknotescatalog.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.ComponentState
import com.pako2k.banknotescatalog.app.MainViewModel
import com.pako2k.banknotescatalog.ui.parts.ContinentFilter
import com.pako2k.banknotescatalog.ui.parts.FrontPage
import com.pako2k.banknotescatalog.ui.parts.Header
import com.pako2k.banknotescatalog.ui.parts.MainMenu
import com.pako2k.banknotescatalog.ui.parts.MenuOption
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.views.Countries
import com.pako2k.banknotescatalog.ui.views.Country


@Composable
fun BanknotesCatalogUI(
    windowSize : WindowSizeClass,
    screenWidth: Dp,
    mainViewModel : MainViewModel
){
    Log.d(stringResource(R.string.app_log_tag),"Start BanknotesCatalogUI")

    // uiState as state, to trigger recompositions of the whole UI
    val uiState by mainViewModel.uiState.collectAsState()

    if ( uiState.mainInitialization == ComponentState.DONE){
        MainScreen(windowSize, screenWidth, mainViewModel)
    }
    else
        FrontPage(
            uiState.mainInitialization,
            modifier = Modifier.fillMaxSize()
        )
}

@Composable
fun MainScreen(
    windowSize : WindowSizeClass,
    screenWidth: Dp,
    mainViewModel : MainViewModel,
    navController : NavHostController = rememberNavController()
){
    Log.d(stringResource(id = R.string.app_log_tag),"Start MainScreen")

    val uiState by mainViewModel.uiState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()

    val defaultRoute = stringResource(id = R.string.menu_not_selected)

    val routeStr = backStackEntry?.destination?.route ?: defaultRoute

    val mainMenuOption : MenuOption? =
        if (routeStr != defaultRoute && null != MenuOption.values().find { it.name == routeStr }) MenuOption.valueOf(routeStr)
        else null

    Scaffold (
        topBar = {
                Header()
                },
        bottomBar = {
            Column {
                if (mainMenuOption != null) {
                    ContinentFilter(
                        windowWidth = windowSize.widthSizeClass,
                        continents = mainViewModel.repository.continents.values.toList(),
                        selectedContinentId = uiState.selectedContinent,
                        onclick = { mainViewModel.setContinentFilter(it) },
                    )
                }
                MainMenu(
                    windowSize = windowSize,
                    isLoggedIn = uiState.userLoggedIn,
                    selectedOption = mainMenuOption,
                    onClick = {
                        navController.navigate(it.name)
                    }
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = defaultRoute,
            modifier = Modifier
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        0.0f to if (mainMenuOption == null) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.background,
                        0.9f to MaterialTheme.colorScheme.background
                    )
                )
        ) {
            composable(defaultRoute){
                FrontPage(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                )
            }
            composable(MenuOption.COUNTRIES.name){
                Countries(
                    screenWidth = screenWidth,
                    territoriesData = mainViewModel.territoriesData,
                    sortBy = uiState.territoriesSortedBy,
                    sortingDir = uiState.territoriesSortingDir,
                    onCountryClick = {
                        navController.navigate("COUNTRY/$it")
                                     },
                    sortCallback = { mainViewModel.sortTerritoriesBy(it) }
                    )
            }
            composable(MenuOption.CURRENCIES.name){
                Text ("CURRENCIES")
            }
            composable(MenuOption.DENOMINATIONS.name){
                Text ("DENOMINATIONS")
            }
            composable(MenuOption.YEARS.name){
                Text ("YEARS")
            }
            composable(MenuOption.COLLECTION.name){
                Text ("COLLECTION")
            }
            composable(MenuOption.LOG_IN.name){
                Text ("SIGN IN / SIGN UP")
            }
            composable("COUNTRY/{id}", arguments = listOf(navArgument("id"){type = NavType.IntType} )){navBackStackEntry ->
                Country(
                    territory = mainViewModel.repository.territories.find { it.id == (navBackStackEntry.arguments?.getInt("id")?.toUInt() ?: 1u)} ?: mainViewModel.repository.territories[0]
                )
            }
        }

    }
}



@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun IMainScreenPreview() {
    BanknotesCatalogTheme {
        MainScreen(WindowSizeClass.calculateFromSize(size = DpSize(400.dp,800.dp)), 400.dp, viewModel(factory = MainViewModel.Factory) )
    }
}