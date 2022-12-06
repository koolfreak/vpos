package models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.*

open class Check: RealmObject {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    var tableNumber: String = ""
    var checkNumber: String = ""
    var serverId: String = ""
    var totalAmount: Double = 0.00
    var tipAmount: Double = 0.00
    var taxAmount: Double = 0.00
    var baseAmount: Double = 0.00
    var orderDetails: RealmList<OrderDetail> = realmListOf()

    var payments: RealmList<Payment> = realmListOf()
    var status: String = ""
}