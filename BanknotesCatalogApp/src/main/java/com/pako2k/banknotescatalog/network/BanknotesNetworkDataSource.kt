package com.pako2k.banknotescatalog.network

class BanknotesNetworkDataSource (
    private val client: BanknotesAPIClient,
) {
    suspend fun getContinents() = client.getContinents()
    suspend fun getTerritoryTypes()  = client.getTerritoryTypes()
    suspend fun getTerritories() = client.getTerritories()
}