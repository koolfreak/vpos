package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.theme.AppGrayColor
import ui.theme.PrimaryColor


@Composable
fun SegmentedControl(selections: List<String>, selectedIndex: Int, onSelectedIndex: (Int) -> Unit) {
    TabRow(selectedTabIndex = selectedIndex,
        backgroundColor = Color.LightGray,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(50))
            .padding(1.dp),
        indicator = { tabPositions: List<TabPosition> -> Box {} }
    ) {
        selections.forEachIndexed { index, text ->
            val selected = selectedIndex == index
            Tab(
                modifier = if (selected) Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                else Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.LightGray),
                selected = selected,
                onClick = { onSelectedIndex(index) },
                text = { Text(text = text, color = if(selected) PrimaryColor else AppGrayColor) }
            )
        }
    }
}


@Composable
fun TextEntry(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    showError: Boolean = false,
    errorMessage: String = "",
    readOnly: Boolean = false
) {
    Column {
        OutlinedTextField(
            value = value,
            singleLine = true,
            label = { Text(text = label) },
            onValueChange = { onValueChange(it) },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth(),
            isError = showError,
            readOnly = readOnly
        )
        if(showError){
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.caption
            )
        }
    }
}
