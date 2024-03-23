package com.pako2k.banknotescatalog.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.repo.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.repo.CollectionFieldPrice
import com.pako2k.banknotescatalog.data.repo.CollectionFieldTerritory
import com.pako2k.banknotescatalog.data.repo.CollectionSortableField
import com.pako2k.banknotescatalog.data.repo.SortDirection
import com.pako2k.banknotescatalog.data.repo.UserCredentialsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat

private const val MAX_RETRIES = 3
private const val RETRY_INTERVAL = 1000L

class CollectionViewModel private constructor(
    ctx: Context,
    private val repository : BanknotesCatalogRepository,
    private val userCredentialsRepository: UserCredentialsRepository
) : ViewModel() {

    private var LOG_TAG = ctx.getString(R.string.app_log_tag)

    // Private so it cannot be updated outside this MainViewModel
    private val _collectionUIState = MutableStateFlow(CollectionUIState())

    // Public property to read the UI state
    val collectionUIState = _collectionUIState.asStateFlow()


    private fun setCollectionViewDataUI(selectedContinentId : UInt?) {
        val uiData : MutableList<List<Any>> = mutableListOf()
        val sortedColumn = collectionUIState.value.collectionTable.sortedBy
        val showAggregation = collectionUIState.value.collectionTable.columns[sortedColumn].linkedField != CollectionFieldPrice

        var sumPrice = 0f
        // First element
        val firstIndex = repository.collection.indexOfFirst {
            selectedContinentId == null || it.continent.id == selectedContinentId
        }
        val first = repository.collection[firstIndex]
        val colItem = listOf<Any>(
            Pair(first.territory.id, first.territory.name),
            Pair(first.item.variantId, first.catalogId),
            DecimalFormat("#,###.###").format(first.denomination),
            Pair(first.currency.id, first.currency.name),
            first.item.grade,
            first.item.quantity.toString(),
            DecimalFormat("#,###.###").format(first.item.price),
            first.item.seller ?: "",
            first.item.purchaseDate ?: "",
            first.item.description ?: ""
        )
        var sortedVal : Any = colItem[sortedColumn]
        sumPrice += first.item.price
        uiData.add(colItem)
        // Rest of the elements
        for(item in repository.collection.drop(firstIndex+1)){
            if (selectedContinentId == null || item.continent.id == selectedContinentId) {
                val record = listOf<Any>(
                    Pair(item.territory.id, item.territory.name),
                    Pair(item.item.variantId, item.catalogId),
                    DecimalFormat("#,###.###").format(item.denomination),
                    Pair(item.currency.id, item.currency.name),
                    item.item.grade,
                    item.item.quantity.toString(),
                    DecimalFormat("#,###.###").format(item.item.price),
                    item.item.seller ?: "",
                    item.item.purchaseDate ?: "",
                    item.item.description ?: ""
                )
                if (showAggregation){
                    if (sortedVal == record[sortedColumn]) {
                        sumPrice += item.item.price
                    } else {
                        // Add aggregatedPrice
                        val priceStr = DecimalFormat("#,###.###").format(sumPrice)
                        uiData.add(
                            listOf(
                                Pair(
                                    if (sortedVal is Pair<*, *>) sortedVal.second else sortedVal.toString(),
                                    "{$priceStr} €"
                                )
                            )
                        )
                        sortedVal = record[sortedColumn]
                        sumPrice = item.item.price
                    }
                }
                uiData.add(record)
            }
        }
        collectionViewDataUI =  uiData
    }

    var collectionViewDataUI : List<List<Any>> = listOf()
        private set

    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BanknotesCatalogApplication
                Log.d(application.getString(R.string.app_log_tag), "Create CollectionViewModel")

                CollectionViewModel(
                    application.applicationContext,
                    application.repository,
                    application.userCredentialsRepository
                )
            }
        }
    }

    fun initialize(selectedContinentId : UInt?) {
        Log.d(LOG_TAG, "Start INIT CollectionViewModel")

        var result = collectionUIState.value.state

        viewModelScope.launch {
            Log.d(LOG_TAG, "Start asynchronous getCollection")
            var retryCount = 0
            while (retryCount < MAX_RETRIES) {
                result = try {
                    if (userCredentialsRepository.userSession == null)
                        throw Exception("User Session not initialized!")

                    repository.fetchCollection(userCredentialsRepository.userSession!!.id)
                    repository.sortCollection(CollectionFieldTerritory, SortDirection.ASC)
                    // Set the data to be shown in UI
                    setCollectionViewDataUI(selectedContinentId)
                    retryCount = MAX_RETRIES
                    ComponentState.DONE
                } catch (exc: Exception) {
                    Log.e(LOG_TAG, exc.toString() + " - " + exc.stackTraceToString())
                    retryCount++
                    delay(RETRY_INTERVAL)
                    ComponentState.FAILED
                }
            }
            _collectionUIState.update {currentState ->
                currentState.copy(
                    state = result
                )
            }
            Log.d(LOG_TAG, "End asynchronous getCollection with $result")
        }
    }

    fun setCollectionFilter(selectedContinentId : UInt?) {
        setCollectionViewDataUI(selectedContinentId)
    }

    fun sortCollectionBy(sortBy : CollectionSortableField, selectedContinentId : UInt?) {
        collectionUIState.value.collectionTable.sortBy(collectionUIState.value.collectionTable.getCol(sortBy)?:0)
        val sortedColumn = collectionUIState.value.collectionTable.sortedBy
        val newSortingDir = collectionUIState.value.collectionTable.columns[sortedColumn].sortedDirection!!

        repository.sortCollection(sortBy, newSortingDir)

        setCollectionViewDataUI(selectedContinentId)

        _collectionUIState.update { currentState ->
            currentState.copy(
                collectionTableUpdateTrigger = !currentState.collectionTableUpdateTrigger
            )
        }
    }
}