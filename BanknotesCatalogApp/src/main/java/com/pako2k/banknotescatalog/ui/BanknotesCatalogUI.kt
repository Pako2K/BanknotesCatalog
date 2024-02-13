package com.pako2k.banknotescatalog.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import com.pako2k.banknotescatalog.ui.parts.Bookmarks
import com.pako2k.banknotescatalog.ui.parts.ContinentFilter
import com.pako2k.banknotescatalog.ui.parts.FrontPage
import com.pako2k.banknotescatalog.ui.parts.Header
import com.pako2k.banknotescatalog.ui.parts.MainMenu
import com.pako2k.banknotescatalog.ui.parts.MenuOption
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme
import com.pako2k.banknotescatalog.ui.views.TerritoriesView
import com.pako2k.banknotescatalog.ui.views.CurrenciesView
import com.pako2k.banknotescatalog.ui.views.CurrencyView
import com.pako2k.banknotescatalog.ui.views.TerritoryView
import kotlinx.coroutines.launch


@Composable
fun BanknotesCatalogUI(
    windowSize : WindowSizeClass,
    screenWidth: Dp,
    mainViewModel : MainViewModel
){
    Log.d(stringResource(R.string.app_log_tag),"Start BanknotesCatalogUI")

    // initializationState as state, to trigger recompositions of the whole UI
    val initializationState by mainViewModel.initializationState.collectAsState()

    if ( initializationState.state == ComponentState.DONE){
        MainScreen(windowSize, screenWidth, mainViewModel)
    }
    else
        FrontPage(
            initializationState.state,
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

    // userPreferences as state, to trigger recompositions
    val userPreferences by mainViewModel.userPreferencesFlow.observeAsState()

    // uiState as state, to trigger recompositions
    val uiState by mainViewModel.uiState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()

    val defaultRoute = stringResource(id = R.string.menu_not_selected)

    val routeStr = backStackEntry?.destination?.route ?: defaultRoute

    val mainMenuOption : MenuOption? =
        if (routeStr != defaultRoute && null != MenuOption.values().find { it.name == routeStr }) MenuOption.valueOf(routeStr)
        else null

    val bookmarksState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Screen content
    Scaffold(
        topBar = {
            Header { scope.launch { bookmarksState.apply { if(isOpen) close() else open() } } }
        },
        bottomBar = {
            Column {
                if (mainMenuOption != null && mainMenuOption != MenuOption.LOG_IN) {
                    ContinentFilter(
                        windowWidth = windowSize.widthSizeClass,
                        continents = mainViewModel.continents.values.toList(),
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
        Bookmarks(
            territories = userPreferences?.favouriteTerritories?.map { Pair(it, mainViewModel.territoryViewData(it)?.name?:"") }?.sortedBy { it.second }?: listOf(),
            currencies = listOf(),
            historyTer = userPreferences?.historyTerritories?.map { Pair(it, mainViewModel.territoryViewData(it)?.name?:"") }?: listOf(),
            historyCur = listOf(),
            state = bookmarksState,
            onClick = { isTer, id ->
                scope.launch {bookmarksState.apply { close()}}
                if(isTer) navController.navigate("COUNTRY/$id") else navController.navigate("CURRENCY/$id")
                      },
            drawerPadding = innerPadding
        ) {

            NavHost(
                navController = navController,
                startDestination = defaultRoute,
                //modifier = Modifier.padding(innerPadding)
            ) {
                composable(defaultRoute) {
                    FrontPage(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    )
                }
                composable(MenuOption.COUNTRIES.name) {
                    TerritoriesView(
                        screenWidth = screenWidth,
                        table = uiState.territoriesTable,
                        data = mainViewModel.territoriesViewData(),
                        onCountryClick = {
                            navController.navigate("COUNTRY/$it")
                        },
                        sortCallback = { mainViewModel.sortTerritoriesBy(it) }
                    )
                }
                composable(MenuOption.CURRENCIES.name) {
                    CurrenciesView(
                        screenWidth = screenWidth,
                        table = uiState.currenciesTable,
                        data = mainViewModel.currenciesViewData(),
                        onCurrencyClick = {
                            navController.navigate("CURRENCY/$it")
                        },
                        onCountryClick = {
                            navController.navigate("COUNTRY/$it")
                        },
                        sortCallback = { mainViewModel.sortCurrenciesBy(it) }
                    )
                }
                composable(MenuOption.DENOMINATIONS.name) {
                    Text("DENOMINATIONS")
                }
                composable(MenuOption.YEARS.name) {
                    Text("YEARS")
                }
                composable(MenuOption.COLLECTION.name) {
                    Text("COLLECTION")
                }
                composable(MenuOption.LOG_IN.name) {
                    Text("SIGN IN / SIGN UP")
                }
                composable(
                    "COUNTRY/{id}",
                    arguments = listOf(navArgument("id") {
                        type = NavType.IntType
                    })
                ) { navBackStackEntry ->
                    val id = navBackStackEntry.arguments!!.getInt("id").toUInt()
                    LaunchedEffect(id){ mainViewModel.updateHistoryTer(id) }
                    val data = mainViewModel.territoryViewData(id)
                    if (data != null)
                        TerritoryView(
                            windowWidth = windowSize.widthSizeClass,
                            data = data,
                            isFavourite = userPreferences?.favouriteTerritories?.contains(id)?:false,
                            // uiState.favouriteTerritories.contains(id),
                            onCountryClick = {
                                navController.navigate("COUNTRY/$it")
                            },
                            onAddFavourite = { scope.launch {mainViewModel.updateFavouriteTer(id)} }
                        )
                }
                composable(
                    "CURRENCY/{id}",
                    arguments = listOf(navArgument("id") {
                        type = NavType.IntType
                    })
                ) { navBackStackEntry ->
                    val id = navBackStackEntry.arguments!!.getInt("id").toUInt()
                    val data = mainViewModel.currencyViewData(id)
                    if (data != null)
                        CurrencyView(
                            currency = data,
                        )
                }
            }
        }
    }
}




@Preview
@Composable
fun IMainScreenPreview() {
    BanknotesCatalogTheme {

        //MainScreen(WindowSizeClass.calculateFromSize(size = DpSize(400.dp,800.dp)), 400.dp, viewModel(factory = MainViewModel.Factory) )
    }
}