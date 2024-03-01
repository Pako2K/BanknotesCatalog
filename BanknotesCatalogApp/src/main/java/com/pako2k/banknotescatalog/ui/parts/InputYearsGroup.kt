package com.pako2k.banknotescatalog.ui.parts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.ui.theme.BanknotesCatalogTheme


private const val MAX_YEAR_LENGTH = 4


@Composable
fun InputYearsGroup(
    title : String,
    values : FilterDates,
    onChanged : (FilterDates) -> Unit,
){
    val valueFrom = values.from?.toString()?:""
    val valueTo = values.to?.toString()?:""

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
        Row {
            InputYearField(title = "From ", valueFrom, values.isValid, onValueChange = { value ->
                onChanged(FilterDates(value.toIntOrNull(), valueTo.toIntOrNull()))
            })
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.medium_padding)))
            InputYearField(title = "To ", valueTo, values.isValid, onValueChange = { value ->
                onChanged(FilterDates(valueFrom.toIntOrNull(), value.toIntOrNull()))
            })
        }
    }
}

@Composable
private fun InputYearField(
    title : String,
    value : String,
    isValid : Boolean,
    onValueChange: (String) -> Unit
){
    val focus = LocalFocusManager.current
    TextField(
        value = value,
        textStyle = MaterialTheme.typography.bodyLarge,
        label = {
            Text(
                title,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        keyboardActions = KeyboardActions(onDone = {
            focus.clearFocus()
        }),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        singleLine = true,
        isError = !isValid,
        onValueChange = {
            if (it.isEmpty() || (it.toIntOrNull()!=null && it.length <= MAX_YEAR_LENGTH))
                onValueChange(it)
        },
        modifier = Modifier
            .width(72.dp)
    )
}



@Preview//(widthDp = 300, heightDp = 200)
@Composable
private fun InputYearsGroupPreview() {
    BanknotesCatalogTheme {
        InputYearsGroup("Founded", FilterDates(1900,2000)) { }
    }
}