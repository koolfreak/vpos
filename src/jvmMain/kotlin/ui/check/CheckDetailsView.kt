package ui.check

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import models.Check
import utils.CHECK_COLUMN_WIDTH
import utils.formatAmount


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CheckDetailsView() {

    val coroutineScope = rememberCoroutineScope()
    val viewModel = CheckViewModel(coroutineScope)
    var searchText by remember { mutableStateOf("") }
    var checkEntries by remember { mutableStateOf(listOf<Check>()) }
    var selectedCheckUUID by remember { mutableStateOf("") }
    var selectedCheckObj by remember { mutableStateOf(Check()) }

    var isEditEntry by remember { mutableStateOf(false) }
    var showCheckEntry by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    var showConfirmClose by remember { mutableStateOf(false) }
    var showCheckDetails by remember { mutableStateOf(false) }
    var checkDetailsInfo by remember { mutableStateOf("") }

    Box {

        LaunchedEffect(viewModel) {
            viewModel.getAllChecks()
            viewModel.checkState.collect { state ->
                when(state) {
                    is CheckViewModel.CheckEntryState.CheckSuccess -> {
                        showCheckEntry = false
                        selectedCheckUUID = ""
                        viewModel.getAllChecks()
                    }
                    is CheckViewModel.CheckEntryState.CheckDeleted -> {
                        showConfirmDelete = false
                        selectedCheckUUID = ""
                        viewModel.getAllChecks()
                    }
                    is CheckViewModel.CheckEntryState.CheckClosed -> {
                        showConfirmClose = false
                        selectedCheckUUID = ""
                        viewModel.getAllChecks()
                    }
                    is CheckViewModel.CheckEntryState.CheckList -> {
                        checkEntries = state.checks
                    }
                    is CheckViewModel.CheckEntryState.CheckDetail -> {
                        checkDetailsInfo = state.detail
                        showCheckDetails = true
                    }
                    else -> {}
                }
            }
        }

        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {

            Row {
                OutlinedTextField(
                    value = searchText,
                    singleLine = true,
                    placeholder = { Text(text = "Search") },
                    onValueChange = {
                        searchText = it
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "search"
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.weight(0.70f)
                )
                Spacer(modifier = Modifier.weight(0.30f))
            }
                //
                Column (
                    modifier = Modifier.fillMaxWidth()
                        .height(300.dp).border(color = Color.Black, width = 1.dp, shape = RoundedCornerShape(5.dp))
                        .padding(vertical = 5.dp),
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Text("Status", textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                        Text("Table", textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                        Text("Check", textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                        Text("Server", textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                        Text("Total", textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                        Text("Tax", textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                        Text("Tip", textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                        Text("Base", textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                    }
                    Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(checkEntries, key = { it.uuid }) { check ->
                            Column(modifier = Modifier.selectableGroup()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().background(if (check.uuid == selectedCheckUUID) Color.Gray else Color.White)
                                        .selectable(
                                            selected = check.uuid == selectedCheckUUID,
                                            onClick = {
                                                selectedCheckObj = check
                                                selectedCheckUUID = if (selectedCheckUUID != check.uuid) check.uuid else ""
                                            }
                                        ),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Text(check.status, textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                                    Text(check.tableNumber, textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                                    Text(check.checkNumber, textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                                    Text(check.serverId, textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                                    Text(check.totalAmount.formatAmount(), textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                                    Text(check.taxAmount.formatAmount(), textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                                    Text(check.tipAmount.formatAmount(), textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                                    Text(check.baseAmount.formatAmount(), textAlign = TextAlign.Center, modifier = Modifier.weight(CHECK_COLUMN_WIDTH))
                                }
                                Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
                            }
                        }
                    }
                }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    if( selectedCheckUUID.isNotEmpty() ){
                        viewModel.convertTOJsonString(selectedCheckObj)
                    }
                }) {
                    Text("Details")
                }
                Button(onClick = {
                    showCheckEntry = true
                    isEditEntry = false
                }) {
                    Text("Add")
                }
                Button(onClick = {
                    if (selectedCheckUUID.isNotEmpty()) {
                        showCheckEntry = true
                        isEditEntry = true
                    }
                }) {
                    Text("Edit")
                }
                Button(onClick = {
                    if (selectedCheckUUID.isNotEmpty()) {
                        showConfirmClose = true
                    }
                }) {
                    Text("Close")
                }
                Button(onClick = {
                    if (selectedCheckUUID.isNotEmpty()) {
                        showConfirmDelete = true
                    }
                }) {
                    Text("Delete")
                }
            }
        }

        if( showConfirmDelete ) {
            AlertDialog(onDismissRequest = { showConfirmDelete = false },
                title = { Text("Delete Check") },
                confirmButton = {
                    Button(onClick = { viewModel.deleteCheck(selectedCheckUUID)}) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(onClick = { showConfirmDelete = false }) {
                        Text("Cancel")
                    }
                },
                text = { Text("Are you sure you want to delete Check No.: ${selectedCheckObj.checkNumber}") }
            )
        }

        if( showConfirmClose ) {
            AlertDialog(onDismissRequest = { showConfirmClose = false },
                title = { Text("Close Check") },
                confirmButton = {
                    Button(onClick = { viewModel.closeCheck(selectedCheckObj)}) {
                        Text("Close")
                    }
                },
                dismissButton = {
                    Button(onClick = { showConfirmClose = false }) {
                        Text("Cancel")
                    }
                },
                text = { Text("Are you sure you want to close Check No.: ${selectedCheckObj.checkNumber}") }
            )
        }

        CheckEntryFormDialog(
            viewModel = viewModel,
            visible = showCheckEntry,
            title = "Add Check Details",
            isEdit = isEditEntry,
            selectedCheck = selectedCheckObj,
            onDismiss = {
                showCheckEntry = false
                isEditEntry = false
                selectedCheckUUID = ""
                selectedCheckObj = Check()
            }
        )

        CheckDetailsDialog(
            visible = showCheckDetails,
            details = checkDetailsInfo,
            title = "Check details for check No.: ${selectedCheckObj.checkNumber}",
            onDismiss = {
                selectedCheckUUID = ""
                showCheckDetails = false
            }
        )
    }


}


// Resosurces
/* Accompanist */
// https://google.github.io/accompanist/
/* compose sample */
// https://github.com/android/compose-samples
// https://developer.android.com/jetpack/compose/designsystems/material3


// https://dev.to/tkuenneth/customize-a-compose-for-desktop-alertdialog-a6e
// https://stackoverflow.com/questions/58817399/how-can-i-show-the-indicator-in-a-verticalscroller-android-developer-compose
// https://stackoverflow.com/questions/70774033/compose-for-desktop-lazyrow-lazycolumn-not-scrolling-with-mouse-click
// https://stackoverflow.com/questions/66793855/compose-lazycolumn-select-one-item