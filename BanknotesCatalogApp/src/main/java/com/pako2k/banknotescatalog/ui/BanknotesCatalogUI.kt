package com.pako2k.banknotescatalog.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.CollectionViewModel
import com.pako2k.banknotescatalog.app.ComponentState
import com.pako2k.banknotescatalog.app.CurrencyViewModel
import com.pako2k.banknotescatalog.app.DenominationViewModel
import com.pako2k.banknotescatalog.app.IssueYearViewModel
import com.pako2k.banknotescatalog.app.LoginViewModel
import com.pako2k.banknotescatalog.app.MainViewModel
import com.pako2k.banknotescatalog.ui.parts.Bookmarks
import com.pako2k.banknotescatalog.ui.parts.ContinentFilter
import com.pako2k.banknotescatalog.ui.parts.FrontPage
import com.pako2k.banknotescatalog.ui.parts.Header
import com.pako2k.banknotescatalog.ui.parts.HeaderMenu
import com.pako2k.banknotescatalog.ui.parts.MainMenu
import com.pako2k.banknotescatalog.ui.parts.MenuOption
import com.pako2k.banknotescatalog.ui.views.CollectionView
import com.pako2k.banknotescatalog.ui.views.CurrenciesView
import com.pako2k.banknotescatalog.ui.views.CurrencyView
import com.pako2k.banknotescatalog.ui.views.DenominationsView
import com.pako2k.banknotescatalog.ui.views.IssueYearsView
import com.pako2k.banknotescatalog.ui.views.LoginView
import com.pako2k.banknotescatalog.ui.views.SettingsUI
import com.pako2k.banknotescatalog.ui.views.TerritoriesView
import com.pako2k.banknotescatalog.ui.views.TerritoryView
import com.pako2k.banknotescatalog.ui.views.subviews.TerritoryFiltersUI
import com.pako2k.banknotescatalog.ui.views.subviews.TerritoryStatsUI
import kotlinx.coroutines.launch


@Composable
fun BanknotesCatalogUI(
    windowSize : WindowSizeClass,
    mainViewModel : MainViewModel
){
    Log.d(stringResource(R.string.app_log_tag),"Start BanknotesCatalogUI")

    // initializationState as state, to trigger recompositions of the whole UI
    val initializationState by mainViewModel.initializationState.collectAsState()

    if ( initializationState.state == ComponentState.DONE){
        MainScreen(
            windowSize,
            mainViewModel,
            )
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
    mainViewModel : MainViewModel,
    currencyViewModel : CurrencyViewModel = viewModel(factory = CurrencyViewModel.Factory),
    denominationViewModel : DenominationViewModel = viewModel(factory = DenominationViewModel.Factory),
    issueYearViewModel : IssueYearViewModel = viewModel(factory = IssueYearViewModel.Factory),
    loginViewModel: LoginViewModel =  viewModel(factory = LoginViewModel.Factory),
    collectionViewModel: CollectionViewModel = viewModel(factory = CollectionViewModel.Factory),
    navController : NavHostController = rememberNavController()
){
    Log.d(stringResource(id = R.string.app_log_tag),"Start MainScreen")

    val screenWidth: Dp = LocalConfiguration.current.screenWidthDp.dp

    // userPreferences as state, to trigger recompositions
    val userPreferences by mainViewModel.userPreferencesState.collectAsState()

    val showPreferences by mainViewModel.showPreferencesState.collectAsState()

    // uiState as state, to trigger recompositions
    val uiState by mainViewModel.uiState.collectAsState()

    val currencyHasFilter = currencyViewModel.currencyHasFilterState.collectAsState()
    val denHasFilter = denominationViewModel.denominationHasFilterState.collectAsState()
    val yearHasFilter = issueYearViewModel.issueYearHasFilterState.collectAsState()


    val backStackEntry by navController.currentBackStackEntryAsState()

    val defaultRoute = stringResource(id = R.string.menu_not_selected)

    val routeStr = backStackEntry?.destination?.route ?: defaultRoute

    val mainMenuOption : MenuOption? =
        if (routeStr != defaultRoute && null != MenuOption.values().find { it.name == routeStr }) MenuOption.valueOf(routeStr)
        else null

    val headerMenuState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val bookmarksState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showSettings by rememberSaveable { mutableStateOf(false) }

    if (showSettings){
         SettingsUI(
             options = showPreferences,
             onClick = { pref, value ->
                 mainViewModel.updateSettings(pref, value)
                 currencyViewModel.updateSettings(pref, value)
                 denominationViewModel.updateSettings(pref, value)
                 issueYearViewModel.updateSettings(pref, value)
                       },
             onClose = {
                 showSettings = false
             })
    }

    val hasFilter = when(mainMenuOption){
        MenuOption.COUNTRIES ->
            (uiState.filterTerritoryState != Pair(true,true) ||
            uiState.filterTerritoryTypes.containsValue(false) ||
            (uiState.filterTerFounded.isValid && (uiState.filterTerFounded.from != null || uiState.filterTerFounded.to != null)) ||
            (uiState.filterTerExtinct.isValid && (uiState.filterTerExtinct.from != null || uiState.filterTerExtinct.to != null)))
        else -> false
    }

    // Screen content
    Scaffold(
        topBar = {
            Header(
                isMenuEnabled = mainMenuOption!=null && mainMenuOption!=MenuOption.LOG_IN,
                hasFilter = when(mainMenuOption){
                    MenuOption.CURRENCIES -> currencyHasFilter.value
                    MenuOption.DENOMINATIONS -> denHasFilter.value
                    MenuOption.YEARS -> yearHasFilter.value
                    else -> hasFilter
                },
                onMenuClicked = { scope.launch {
                    bookmarksState.apply { close() }
                    headerMenuState.apply { if(isOpen) close() else open() }
                }},
                onBookmarksClicked =  { scope.launch {
                    headerMenuState.apply { close() }
                    bookmarksState.apply { if(isOpen) close() else open() }
                }}
            )
        },
        bottomBar = {
            Column {
                if (mainMenuOption != null && mainMenuOption != MenuOption.LOG_IN) {
                    ContinentFilter(
                        windowWidth = windowSize.widthSizeClass,
                        continents = mainViewModel.continents.values.toList(),
                        selectedContinentId = uiState.selectedContinent,
                        onClick = {
                            scope.launch { headerMenuState.apply { close() } }
                            scope.launch { bookmarksState.apply { close() } }
                            mainViewModel.setContinentFilter(it)
                            currencyViewModel.setContinentFilter(it)
                            denominationViewModel.setContinentFilter(it)
                            issueYearViewModel.setContinentFilter(it)
                            collectionViewModel.setCollectionFilter(it)
                                  },
                    )
                }
                MainMenu(
                    windowWidth = windowSize.widthSizeClass,
                    isLoggedIn = uiState.userLoggedIn,
                    selectedOption = mainMenuOption,
                    onClick = {
                        scope.launch { headerMenuState.apply { close() } }
                        scope.launch { bookmarksState.apply { close() } }
                        navController.navigate(it.name)
                    }
                )
            }
        },
    ) { innerPadding ->
        HeaderMenu(
            state = headerMenuState,
            drawerPadding = innerPadding,
            onClickFilter = {
                scope.launch { headerMenuState.apply { close() } }
                when (mainMenuOption){
                    MenuOption.COUNTRIES ->
                        mainViewModel.showTerritoryFilters(true)
                    MenuOption.CURRENCIES ->
                        currencyViewModel.showFilters(true)
                    MenuOption.DENOMINATIONS ->
                        denominationViewModel.showFilters(true)
                    MenuOption.YEARS ->
                        issueYearViewModel.showFilters(true)
                    else -> Unit
                }
            },
            onClickStats = {
                scope.launch { headerMenuState.apply { close() } }
                when (mainMenuOption){
                    MenuOption.COUNTRIES ->
                        mainViewModel.showTerritoryStats(true)
                    MenuOption.CURRENCIES ->
                        currencyViewModel.showStats(true)
                    MenuOption.DENOMINATIONS ->
                        denominationViewModel.showStats(true)
                    MenuOption.YEARS ->
                        issueYearViewModel.showStats(true)
                    else -> Unit
                }
            },
            onClickSettings = {
                scope.launch { headerMenuState.apply { close() } }
                showSettings = true
            },
        ) {
            Bookmarks(
                territories = userPreferences.favouriteTerritories.map {
                    Pair(
                        it,
                        mainViewModel.territoryViewData(it)?.name ?: ""
                    )
                }.sortedBy { it.second },
                currencies = userPreferences.favouriteCurrencies.map {
                    Pair(
                        it,
                        mainViewModel.getCurrencyBookmark(it) ?: ""
                    )
                }.sortedBy { it.second },
                historyTer = userPreferences.historyTerritories.map {
                    Pair(
                        it,
                        mainViewModel.territoryViewData(it)?.name ?: ""
                    )
                },
                historyCur = userPreferences.historyCurrencies.map {
                    Pair(
                        it,
                        mainViewModel.getCurrencyBookmark(it) ?: ""
                    )
                },
                state = bookmarksState,
                onClick = { isTer, id ->
                    scope.launch { bookmarksState.apply { close() } }
                    if (isTer) navController.navigate("COUNTRY/$id") else navController.navigate("CURRENCY/$id")
                },
                drawerPadding = PaddingValues(0.dp)//innerPadding
            ) {
                val padding = dimensionResource(id = R.dimen.small_padding)
                NavHost(
                    navController = navController,
                    startDestination = defaultRoute,
                ) {
                    composable(defaultRoute) {
                        FrontPage(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                    composable(MenuOption.COUNTRIES.name) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally, 
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.surface)
                                .fillMaxWidth()
                                .padding(padding)
                        ) {
                            if (uiState.showTerritoryStats) {
                                TerritoryStatsUI(
                                    data = mainViewModel.getTerritoryStats(),
                                    continentName = uiState.selectedContinent?.let { mainViewModel.continents[it]?.name },
                                    isLoggedIn = uiState.userLoggedIn,
                                    onClose = {
                                        mainViewModel.showTerritoryStats(false)
                                    }
                                )
                                Spacer(modifier = Modifier.height(padding))
                            }
                            if (uiState.showTerritoryFilters) {
                                TerritoryFiltersUI (
                                    terTypeFilters = uiState.filterTerritoryTypes,
                                    terStateFilters = Pair(uiState.filterTerritoryState.first, uiState.filterTerritoryState.second),
                                    terFoundedFilter = uiState.filterTerFounded,
                                    terExtinctFilter = uiState.filterTerExtinct,
                                    onTerTypeChanged = { type, isSelected -> mainViewModel.updateFilterTerritoryType(type, isSelected)},
                                    onTerStateChanged = { mainViewModel.updateFilterTerritoryState(it)},
                                    onTerFoundedChanged = {mainViewModel.updateFilterTerritoryFoundedDates(it)},
                                    onTerExtinctChanged = {mainViewModel.updateFilterTerritoryExtinctDates(it)},
                                    onClose = { mainViewModel.showTerritoryFilters(false)}
                                )
                                Spacer(modifier = Modifier.height(padding))
                            }
                            if ((!uiState.showTerritoryStats && !uiState.showTerritoryFilters) || windowSize.heightSizeClass != WindowHeightSizeClass.Compact) {
                                TerritoriesView(
                                    width = screenWidth - 2 * padding,
                                    table = uiState.territoriesTable,
                                    data = mainViewModel.territoriesViewDataUI,
                                    onCountryClick = {
                                        navController.navigate("COUNTRY/$it")
                                    },
                                    isLogged = uiState.userLoggedIn,
                                    sortCallback = { field, statsCol ->
                                        mainViewModel.sortTerritoriesBy(
                                            field,
                                            statsCol
                                        )
                                    }
                                )
                            }
                        }
                    }
                    composable(MenuOption.CURRENCIES.name) {
                        CurrenciesView(
                            viewModel = currencyViewModel,
                            width = screenWidth - 2 * padding,
                            windowHeightClass = windowSize.heightSizeClass,
                            isLogged = uiState.userLoggedIn,
                            selectedContinent = uiState.selectedContinent?.let { mainViewModel.continents[it] },
                            onCurrencyClick = {
                                navController.navigate("CURRENCY/$it")
                            },
                            onCountryClick = {
                                navController.navigate("COUNTRY/$it")
                            }
                        )
                    }
                    composable(MenuOption.DENOMINATIONS.name) {
                        DenominationsView(
                            viewModel = denominationViewModel,
                            isLogged = uiState.userLoggedIn,
                            selectedContinent = uiState.selectedContinent?.let { mainViewModel.continents[it] },
                            width = screenWidth - 2 * padding
                        )
                    }
                    composable(MenuOption.YEARS.name) {
                        IssueYearsView(
                            viewModel = issueYearViewModel,
                            isLogged = uiState.userLoggedIn,
                            selectedContinent = uiState.selectedContinent?.let { mainViewModel.continents[it] },
                            width = screenWidth - 2 * padding
                        )
                    }
                    composable(MenuOption.COLLECTION.name) {
                        CollectionView(
                            selectedContinent = uiState.selectedContinent,
                            viewModel = collectionViewModel,
                            onTerritoryClick = {navController.navigate("COUNTRY/$it")},
                            onCurrencyClick = {navController.navigate("CURRENCY/$it")},
                            {}
                        )
                    }
                    composable(MenuOption.LOG_IN.name) {
                        // This can happen right after log in or when pressing the back button after logging in
                        if (uiState.userLoggedIn) {
                            navController.popBackStack()
                            navController.navigate(MenuOption.COLLECTION.name)
                        }
                        else
                            LoginView(loginViewModel){
                                mainViewModel.userLogged()
                                collectionViewModel.initialize(uiState.selectedContinent)
                            }
                    }
                    composable(
                        "COUNTRY/{id}",
                        arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                        })
                    ) { navBackStackEntry ->
                        val id = navBackStackEntry.arguments!!.getInt("id").toUInt()
                        mainViewModel.updateHistoryTer(id)
                        val data = mainViewModel.territoryViewData(id)
                        if (data != null)
                            TerritoryView(
                                windowWidth = windowSize.widthSizeClass,
                                data = data,
                                isFavourite = userPreferences.favouriteTerritories.contains(id),
                                onCountryClick = {
                                    navController.navigate("COUNTRY/$it")
                                },
                                onAddFavourite = { mainViewModel.updateFavouriteTer(id) }
                            )
                    }
                    composable(
                        "CURRENCY/{id}",
                        arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                        })
                    ) { navBackStackEntry ->
                        val id = navBackStackEntry.arguments!!.getInt("id").toUInt()
                        mainViewModel.updateHistoryCur(id)
                        val data = mainViewModel.currencyViewData(id)
                        if (data != null)
                            CurrencyView(
                                data = data,
                                windowWidth = windowSize.widthSizeClass,
                                isFavourite = userPreferences.favouriteCurrencies.contains(id),
                                onCountryClick = {
                                    navController.navigate("COUNTRY/$it")
                                },
                                onCurrencyClick = {
                                    navController.navigate("CURRENCY/$it")
                                },
                                onAddFavourite = {
                                    mainViewModel.updateFavouriteCur(id)
                                }
                            )
                    }
                }
            }
        }
    }
}

