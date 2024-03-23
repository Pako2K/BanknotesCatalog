package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.app.CollectionViewModel
import com.pako2k.banknotescatalog.app.ComponentState
import com.pako2k.banknotescatalog.data.repo.CollectionSortableField
import com.pako2k.banknotescatalog.ui.parts.CollectionTableUI
import com.pako2k.banknotescatalog.ui.parts.SubviewMenu
import com.pako2k.banknotescatalog.ui.parts.SubviewOptions


@Composable
fun CollectionView(
    selectedContinent : UInt?,
    viewModel: CollectionViewModel,
    onTerritoryClick: (territoryID: UInt)->Unit,
    onCurrencyClick: (currencyID: UInt)->Unit,
    onVariantClick: (variantID: UInt)->Unit
){
    Log.d(stringResource(id = R.string.app_log_tag), "Start Collection")

    val uiState by viewModel.collectionUIState.collectAsState()

    val collectionMenu = SubviewOptions(
        stringResource(R.string.col_menu_banknotes),
        stringResource(R.string.col_menu_date),
        stringResource(R.string.col_menu_seller)
    )

    var selectedOption by rememberSaveable {
        mutableStateOf(collectionMenu.options.first())
    }
    val enterAnimation = slideInVertically() + expandVertically(expandFrom = Alignment.Top) + fadeIn(initialAlpha = 0.3f)
    val exitAnimation = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()


    when (uiState.state) {
        ComponentState.LOADING -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally ,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .width(44.dp)
                )
            }
        }
        ComponentState.FAILED -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally ,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.connection_error),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        ComponentState.DONE -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally ,
                modifier = Modifier.fillMaxWidth()
            ) {
                SubviewMenu(collectionMenu, selectedOption) { selectedOption = it }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.medium_padding)))
                AnimatedVisibility(
                    visible = selectedOption == stringResource(R.string.col_menu_banknotes),
                    enter = enterAnimation,
                    exit = exitAnimation
                ) {
                    CollectionTableUI(
                        table = uiState.collectionTable,
                        data = viewModel.collectionViewDataUI,
                        onHeaderClick = {
                            viewModel.sortCollectionBy(
                                uiState.collectionTable.columns[it].linkedField as CollectionSortableField,
                                selectedContinent
                            )
                        },
                        onDataClick = { col, id ->
                            when (col) {
                                0 -> onTerritoryClick(id)
                                1 -> onVariantClick(id)
                                3 -> onCurrencyClick(id)
                                else -> Unit
                            }
                        }
                    )
                }
            }
        }
    }

}