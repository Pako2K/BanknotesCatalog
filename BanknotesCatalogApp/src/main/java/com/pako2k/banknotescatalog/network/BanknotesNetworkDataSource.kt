package com.pako2k.banknotescatalog.network

class BanknotesNetworkDataSource (
    private val client: BanknotesAPIClient,
) {
    suspend fun getContinents() = client.getContinents()
    suspend fun getTerritoryTypes()  = client.getTerritoryTypes()
    suspend fun getTerritories() = client.getTerritories()
    suspend fun getTerritoryStats() = client.getTerritoryStats()
    suspend fun getCurrencies() = client.getCurrencies()
    suspend fun getCurrencyStats() = client.getCurrencyStats()
}