package ui.check

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import models.Check
import models.OrderDetail
import ui.TextEntry
import utils.formatAmount

@Composable
fun CheckEntryFormDialog(
    viewModel: CheckViewModel,
    visible: Boolean,
    title: String,
    isEdit: Boolean = false,
    selectedCheck: Check,
    onDismiss: () -> Unit
){

    var checkNumber by remember { mutableStateOf("") }
    var tableNumber by remember { mutableStateOf("") }
    var serverId by remember { mutableStateOf("") }
    var taxAmount by remember { mutableStateOf("0.00") }
    var tipAmount by remember { mutableStateOf("0.00") }
    var baseAmount by remember { mutableStateOf("0.00") }
    var totalAmount by remember { mutableStateOf("0.00") }

    var newOrderDetails by remember { mutableStateOf(listOf<OrderDetail>()) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    if(isEdit){
        checkNumber = selectedCheck.checkNumber
        tableNumber = selectedCheck.tableNumber
        serverId = selectedCheck.serverId
        taxAmount = selectedCheck.taxAmount.formatAmount()
        tipAmount = selectedCheck.tipAmount.formatAmount()
        baseAmount = selectedCheck.baseAmount.formatAmount()
        totalAmount = selectedCheck.totalAmount.formatAmount()
        newOrderDetails = selectedCheck.orderDetails
    }

    // fun validate()
    fun saveCheckDetails() {
        val newCheck = Check()
        newCheck.checkNumber = checkNumber
        newCheck.tableNumber = tableNumber
        newCheck.serverId = serverId
        newCheck.status = "Open"
        newCheck.totalAmount = totalAmount.toDouble()
        newCheck.tipAmount = tipAmount.toDouble()
        newCheck.taxAmount = taxAmount.toDouble()
        newCheck.baseAmount = baseAmount.toDouble()
        //newCheck.orderDetails = newOrderDetails
        if(isEdit){
            viewModel.updateCheckDetail(newCheck, selectedCheck, newOrderDetails)
        }else {
            viewModel.addNewCheckDetail(newCheck, newOrderDetails)
        }
    }

    fun updateTotal() {
        try {
            val _tax = taxAmount.toDouble()
            val _tip = tipAmount.toDouble()
            val _base = baseAmount.toDouble()
            val _total = _tax + _tip + _base
            totalAmount = _total.toString()
        }
        catch (_: Exception){

        }
    }

    LaunchedEffect(viewModel.checkState) {
        viewModel.checkState.collect { orderState ->
            when(orderState){
                is CheckViewModel.CheckEntryState.OrderSuccess -> {
                    newOrderDetails = orderState.orders
                    baseAmount = orderState.total.toString()
                }
                else -> { }
            }

        }
    }

    val dialogState = rememberDialogState(size = DpSize(width = 500.dp, height = 600.dp))

    Dialog(
        visible = visible,
        onCloseRequest = onDismiss,
        title = title,
        state = dialogState
    ) {
        Column(
            modifier = Modifier.padding(10.dp).fillMaxSize()
        ) {

            Row(modifier = Modifier.fillMaxWidth()) {
                // Entry Form here
                Column(
                    modifier = Modifier.weight(0.5f)
                ) {

                    TextEntry(
                        value = checkNumber,
                        onValueChange = { checkNumber = it },
                        label = "Check No"
                    )
                    TextEntry(
                        value = tableNumber,
                        onValueChange = { tableNumber = it },
                        label = "Table No"
                    )

                    TextEntry(
                        value = serverId,
                        onValueChange = { serverId = it },
                        label = "Server ID"
                    )
                    TextEntry(
                        value = taxAmount,
                        onValueChange = {
                            taxAmount = it
                            //updateTotal()
                        },
                        label = "Tax Amount"
                    )
                    TextEntry(
                        value = tipAmount,
                        onValueChange = {
                            tipAmount = it
                            //updateTotal()
                        },
                        label = "Auto Gratuity"
                    )
                    TextEntry(
                        value = baseAmount,
                        onValueChange = { baseAmount = it },
                        label = "Base AMount"
                    )
                    TextEntry(
                        value = totalAmount,
                        onValueChange = { totalAmount = it },
                        label = "Total AMount"
                    )

                }

                // Items listing here
                Column(
                    modifier = Modifier.weight(0.5f).padding(horizontal = 10.dp)
                ) {
                    Button(onClick = { showAddItemDialog = true }) {
                        Text("Add Item")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        Text("Item", modifier = Modifier.weight( 0.30f ))
                        Text("Quantity", modifier = Modifier.weight( 0.30f ))
                        Text("Price", modifier = Modifier.weight( 0.30f ))
                        Text("", modifier = Modifier.weight( 0.10f ))
                    }
                    Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(newOrderDetails, key = { it.uuid }) { order ->
                            Column {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ){
                                        Text(order.item, modifier = Modifier.weight( 0.30f ))
                                        Text(order.quantity.toString(), modifier = Modifier.weight( 0.30f ))
                                        Text(order.totalPrice.toString(), modifier = Modifier.weight( 0.30f ))
                                        Box(modifier = Modifier.weight(0.10f)) {
                                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "",
                                                modifier = Modifier.clickable { viewModel.removeOrder(order.uuid)  })
                                        }
                                    }
                                    Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp)
                                }
                            }
                        }
                    }

                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick =  { saveCheckDetails() }) {
                    Text("Save")
                }
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    }

    LineItemEntryForm(
        showAddItemDialog = showAddItemDialog,
        onCloseLineItem = { showAddItemDialog = false },
        onAddItem = {
           viewModel.addNewOrder(it)
            showAddItemDialog = false
        }
    )
    // Can be a popup modal
    /*Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss
    ) {
        Column {
            Text("Hello Modal Dialog")
        }
    }*/

}

@Composable
fun LineItemEntryForm(showAddItemDialog: Boolean,
                      onCloseLineItem: () -> Unit,
                      onAddItem: (OrderDetail) -> Unit) {

    var item by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("0.00") }

    fun saveLineItem() {
        val orderDetail = OrderDetail()
        orderDetail.item = item
        orderDetail.quantity = quantity.toInt()
        orderDetail.totalPrice = price.toDouble()
        onAddItem(orderDetail)

        item = ""
        quantity = "1"
        price = "0.00"
    }

    Dialog(
        visible = showAddItemDialog,
        onCloseRequest = onCloseLineItem,
        title = "Add Line Item"
    ){
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            TextEntry(
                value = item,
                onValueChange = { item = it },
                label = "Item"
            )
            TextEntry(
                value = quantity,
                onValueChange = { quantity = it },
                label = "Quantity"
            )
            TextEntry(
                value = price,
                onValueChange = { price = it },
                label = "Price"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick =  { saveLineItem() }) {
                    Text("Add")
                }
                Button(onClick = onCloseLineItem) {
                    Text("Cancel")
                }
            }
        }
    }

}