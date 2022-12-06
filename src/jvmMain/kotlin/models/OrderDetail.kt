package models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.*

open class OrderDetail: RealmObject {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    var item: String = ""
    var quantity: Int = 0
    var totalPrice: Double = 0.00

}