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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.ui.parts.Sorting
import com.pako2k.banknotescatalog.ui.parts.StatsColumn
import com.pako2k.banknotescatalog.ui.parts.SummaryTable
import com.pako2k.banknotescatalog.ui.parts.SummaryTableColumn

private val cols = mutableListOf(
    SummaryTableColumn(0,"", width = 38.dp, isImage = true ),
    SummaryTableColumn(1,"", width = 44.dp ),
    SummaryTableColumn(2,"Name", width = 210.dp, align = Alignment.CenterStart, isSortable = true, isClickable = true, selectedSorting = Sorting.ASC),
    SummaryTableColumn(3,"From", width = 80.dp, isSortable = true),
    SummaryTableColumn(4,"To", width = 80.dp, isSortable = true),
)

private const val MIN_FIXED_COLS = 2

@Composable
fun Countries(
    screenWidth: Dp,
    territoriesData : List<Map<String,Any?>>,
    sortBy : Territory.SortableCol,
    sortingDir : Sorting,
    sortCallback: (sortBy: Territory.SortableCol)->Unit,
    onCountryClick: (territoryID: UInt)->Unit,
) {
    Log.d(stringResource(id = R.string.app_log_tag),"Start Countries")

    // Set the column which is sorted and the direction
    val sortByCol = when(sortBy){
            Territory.SortableCol.NAME -> 2
            Territory.SortableCol.START -> 3
            Territory.SortableCol.END -> 4
    }

    var subColIndex = 0
    cols.forEach{ col ->
        if (col.isStats){
            if (subColIndex == sortByCol || sortByCol == (subColIndex+1))
                col.selectedSorting = sortingDir
            else
                col.selectedSorting = null
            col.sortedBy = if (subColIndex == sortByCol) StatsColumn.CATALOG else StatsColumn.COLLECTION
            subColIndex++
        }
        else{
            if (subColIndex == sortByCol) col.selectedSorting = sortingDir
            else col.selectedSorting = null
        }
        subColIndex++
    }

    val data : MutableList<List<Any?>> = mutableListOf()

    for(ter in territoriesData){
        val terTypSuffix =  if (ter["type"] == "Ind") "" else " [${ter["type"]}]"
        data.add(
            listOf(
                ter["flag"],
                ter["iso3"] ?:"",
                Pair(ter["id"], ter["name"].toString() + terTypSuffix),
                ter["start"].toString(),
                ter["end"]?.toString()?:""
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
            onHeaderClick = {when(it){
                2 -> sortCallback(Territory.SortableCol.NAME)
                3 -> sortCallback(Territory.SortableCol.START)
                4 -> sortCallback(Territory.SortableCol.END)
                else -> sortCallback(Territory.SortableCol.NAME)
            }},
            onDataClick = onCountryClick,
            modifier = Modifier.padding(padding)
        )
    }
}


/*
private const val TEST_WIDTH = 400


@Preview (widthDp = TEST_WIDTH)
@Composable
fun CountriesPreview() {
    BanknotesCatalogTheme {
        val countries = listOf(
            Territory(1u, "Namibia", "NAM",5u,1u, 1913, uri = "uri1" ),
            Territory(2u, "Argentina", "ARG",1u,2u, 1923, uri = "uri1" ),
            Territory(3u, "United States", "USA",5u,1u, 1923, uri = "uri1" ),
            Territory(5u, "Laos", "LAO",4u,1u, 1923, uri = "uri1" ),
        )
        val territoryTypes = listOf(
            TerritoryType(1u, "Independent State", "Ind", "uri1" ),
            TerritoryType(2u, "Territory", "T", "uri1" ),
            TerritoryType(3u, "Not Recognized State", "NR", "uri1" ),
        )
        val flags = FlagsLocalDataSource(LocalContext.current.assets).getFlagsSync()
        Countries(
            screenWidth = TEST_WIDTH.dp,
            territories = countries,
            flags = flags,
            territoryTypes = territoryTypes.associateBy { it.id },
            continentFilter = null,
            sortBy = Territory.SortableCol.NAME,
            sortingDir = Sorting.ASC,
            onCountryClick = {},
            sortCallback = {}
        )
    }
}

 */