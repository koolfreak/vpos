package models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.*

open class Payment: RealmObject {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    var tipAmount: String = ""
    var paymentType: String = ""
    var approvalCode: String = ""
    var referenceNumber: String = ""
    var dateTime: String = ""
    var maskedAccountNumber: String = ""
    var cardBrandName: String = ""
    var signatureImage: String = ""
    var cardHolderName: String = ""
    var transactionName: String = ""
    var verificationMethod: String = ""
    var emvTags: String = ""
    var totalAmount: String = ""
    var invoiceNumber: String = ""
    var partialApproval: String = ""
    var taxAmount: String = ""
    var storeAndForward: String = ""
    var ebtType: String = ""
    var surchargeAmount: String = ""
    var cardEntryMethod: String = ""

}