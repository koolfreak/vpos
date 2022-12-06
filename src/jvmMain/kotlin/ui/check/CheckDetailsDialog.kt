package ui.check

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import models.Check
import models.OrderDetail
import models.Payment
import utils.formatAmount

@Composable
fun CheckDetailsDialog(visible: Boolean,
                       details: String,
                       title: String = "",
                       onDismiss: () -> Unit) {

    val dialogState = rememberDialogState(size = DpSize(width = 500.dp, height = 600.dp))
    val scrollState = rememberScrollState(0)

    Dialog(
        visible = visible,
        title = title,
        onCloseRequest = onDismiss,
        state = dialogState
    ) {
        Box {
            Column(
                modifier = Modifier.verticalScroll(scrollState).padding(10.dp).fillMaxSize()
            ) {
                Text(text = details)
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.CenterEnd),
                style = LocalScrollbarStyle.current.copy(
                    unhoverColor = LocalScrollbarStyle.current.hoverColor.copy(
                        alpha = 0.35f
                    )
                )
            )
        }

    }

}

// Gson cannot serialize RealmObject, so we need to wrapped it to a native class, tedious but it works!
class CheckWrapper(check: Check) {

    private var tableNumber: String = ""
    private var checkNumber: String = ""
    private var serverId: String = ""
    private var totalAmount: String = ""
    private var tipAmount: String = ""
    private var taxAmount: String = ""
    private var baseAmount: String = ""
    private var orderDetails: ArrayList<OrderDetailWrapper> = ArrayList()

    private var payments: ArrayList<PaymentWrapper> = ArrayList()
    private var status: String = ""

    init {
        tableNumber = check.tableNumber
        checkNumber = check.checkNumber
        serverId = check.serverId
        totalAmount = check.totalAmount.formatAmount()
        tipAmount = check.tipAmount.formatAmount()
        taxAmount = check.taxAmount.formatAmount()
        baseAmount = check.taxAmount.formatAmount()
        status = check.status
        check.payments.forEach {
            payments.add(PaymentWrapper(it))
        }
        check.orderDetails.forEach {
            orderDetails.add(OrderDetailWrapper(it))
        }
    }
}

class CheckResponse(check: Check) {
    private var tableNumber: String = ""
    private var checkNumber: String = ""
    private var serverId: String = ""
    private var totalAmount: String = ""
    private var tipAmount: String = ""
    private var taxAmount: String = ""
    private var baseAmount: String = ""
    private var orderDetails: ArrayList<OrderDetailWrapper> = ArrayList()

    init {
        tableNumber = check.tableNumber
        checkNumber = check.checkNumber
        serverId = check.serverId
        totalAmount = check.totalAmount.formatAmount()
        tipAmount = check.tipAmount.formatAmount()
        taxAmount = check.taxAmount.formatAmount()
        baseAmount = check.taxAmount.formatAmount()

        check.orderDetails.forEach {
            orderDetails.add(OrderDetailWrapper(it))
        }
    }
}

class OrderDetailWrapper(orderDetail: OrderDetail) {
    private var item: String = ""
    private var quantity: Int = 0
    private var totalPrice: Double = 0.00
    init {
        item = orderDetail.item
        quantity = orderDetail.quantity
        totalPrice = orderDetail.totalPrice
    }
}

class PaymentWrapper(payment: Payment) {
    private var tipAmount: String = ""
    private var paymentType: String = ""
    private var approvalCode: String = ""
    private var referenceNumber: String = ""
    private var dateTime: String = ""
    private var maskedAccountNumber: String = ""
    private var cardBrandName: String = ""
    private var signatureImage: String = ""
    private var cardHolderName: String = ""
    private var transactionName: String = ""
    private var verificationMethod: String = ""
    private var emvTags: String = ""
    private var totalAmount: String = ""
    private var invoiceNumber: String = ""
    private var partialApproval: String = ""
    private var taxAmount: String = ""
    private var storeAndForward: String = ""
    private var ebtType: String = ""
    private var surchargeAmount: String = ""
    private var cardEntryMethod: String = ""
    init {
        tipAmount  = payment.tipAmount
        paymentType  = payment.paymentType
        approvalCode  = payment.approvalCode
        referenceNumber  = payment.referenceNumber
        dateTime = payment.dateTime
        maskedAccountNumber  = payment.maskedAccountNumber
        cardBrandName  = payment.cardBrandName
        signatureImage  = payment.signatureImage
        cardHolderName  = payment.cardHolderName
        transactionName  = payment.transactionName
        verificationMethod  = payment.verificationMethod
        emvTags  = payment.emvTags
        totalAmount  = payment.totalAmount
        invoiceNumber  = payment.invoiceNumber
        partialApproval  = payment.partialApproval
        taxAmount  = payment.taxAmount
        storeAndForward  = payment.storeAndForward
        ebtType  = payment.ebtType
        surchargeAmount  = payment.surchargeAmount
        cardEntryMethod  = payment.cardEntryMethod
    }
}
