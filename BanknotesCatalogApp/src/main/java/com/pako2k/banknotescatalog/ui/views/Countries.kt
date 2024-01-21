package com.pako2k.banknotescatalog.ui.views

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryType
import com.pako2k.banknotescatalog.ui.parts.Sorting
import com.pako2k.banknotescatalog.ui.parts.StatsColumn
import com.pako2k.banknotescatalog.ui.parts.SummaryTable
import com.pako2k.banknotescatalog.ui.parts.SummaryTableColumn
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme

private val cols = mutableListOf(
    SummaryTableColumn(0,"", width = 38.dp, isFlag = true ),
    SummaryTableColumn(1,"ISO", width = 44.dp ),
    SummaryTableColumn(2,"Name", width = 210.dp, align = Alignment.CenterStart, isSortable = true, isClickable = true, selectedSorting = Sorting.ASC),
    SummaryTableColumn(3,"From", width = 80.dp, isSortable = true),
    SummaryTableColumn(4,"To", width = 80.dp, isSortable = true),
)

private const val MIN_FIXED_COLS = 2

@Composable
fun Countries(
    screenWidth: Dp,
    territories : List<Territory>,
    territoryTypes : List<TerritoryType>,
    continentFilter : UInt?,
    sortBy : Int,
    sortingDir : Sorting,
    sortCallback: (sortByCol : Int)->Unit,
    onCountryClick: (territoryID: UInt)->Unit,
) {
    Log.d(stringResource(id = R.string.app_log_tag),"Start Countries")

    // Set the column which is sorted and the direction
    var dataIndex = 0
    cols.forEach{ col ->
        if (col.isStats){
            if (dataIndex == sortBy || sortBy == (dataIndex+1)) col.selectedSorting = sortingDir
            else col.selectedSorting = null
            col.sortedBy = if (dataIndex == sortBy) StatsColumn.CATALOG else StatsColumn.COLLECTION
            dataIndex++
        }
        else{
            if (dataIndex == sortBy) col.selectedSorting = sortingDir
            else col.selectedSorting = null
        }
        dataIndex++
    }

    val data : MutableList<List<String>> = mutableListOf()

    // Sort data
    val sortedTerritories = when(sortBy){
        2 -> if (sortingDir == Sorting.DESC) territories.sortedByDescending { it.name } else territories
        3 -> if (sortingDir == Sorting.DESC) territories.sortedByDescending { it.start } else territories.sortedBy { it.start }
        4 -> if (sortingDir == Sorting.DESC) territories.sortedByDescending { it.end } else territories.sortedBy { it.end }
        else -> territories
    }

    for (ter in sortedTerritories){
        val territoryType = territoryTypes.find {it.id == ter.territoryTypeId}
        val terTypSuffix =   if (territoryType?.abbreviation == "Ind") ""
                            else " [${territoryType?.abbreviation}]"
        val flagName = ter.iso3?.lowercase()?:ter.name.lowercase().replace(",", "").replace(" ", "")

        if (continentFilter == null || ter.continentId == continentFilter)
            data.add(
                listOf(
                    flagName,
                    ter.iso3?:"",
                    ter.name + terTypSuffix,
                    ter.start.toString(),
                    ter.end?.toString()?:""
                )
            )
    }

    val totalWidth = (cols.sumOf { it.width.value.toDouble() }).toFloat()
    val padding = dimensionResource(id = R.dimen.small_padding)
    val fixedColumns = if (totalWidth > (screenWidth - padding).value) MIN_FIXED_COLS else cols.size
    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxWidth()) {
        SummaryTable(
            columns = cols,
            fixedColumns = fixedColumns,
            data = data,
            onHeaderClick = sortCallback,
            onDataClick = { rowIndex, dataColIndex -> if (dataColIndex == 2) onCountryClick(territories[rowIndex].id) },
            modifier = Modifier.padding(padding)
        )
    }
}



private const val TEST_WIDTH = 412


@Preview (widthDp = TEST_WIDTH)
@Composable
fun CountriesPreview() {
    BanknotesCatalogTheme {
        val countries = listOf(
            Territory(1u, "Namibia", "NAM",5u,1u, 1913, uri = "uri1" ),
            Territory(2u, "Argentina", "ARG",1u,2u, 1923, uri = "uri1" ),
            Territory(3u, "United States", "USA",5u,1u, 1923, uri = "uri1" ),
            Territory(4u, "Biafra", null,2u,3u, 1923, 2001, uri = "uri1" ),
            Territory(5u, "Laos", "LAO",4u,1u, 1923, uri = "uri1" ),
        )
        val territoryTypes = listOf(
            TerritoryType(1u, "Independent State", "Ind", "uri1" ),
            TerritoryType(2u, "Territory", "T", "uri1" ),
            TerritoryType(3u, "Not Recognized State", "NR", "uri1" ),
        )
        Countries(
            screenWidth = TEST_WIDTH.dp,
            territories = countries,
            territoryTypes = territoryTypes,
            continentFilter = null,
            sortBy = 2,
            sortingDir = Sorting.ASC,
            onCountryClick = {},
            sortCallback = {}
        )
    }
}