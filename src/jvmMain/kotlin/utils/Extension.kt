package utils

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import models.Check
import models.OrderDetail
import models.Payment
import java.util.*


open class ViewModel {

    lateinit var viewModelScope: CoroutineScope

    fun provideRealm(): Realm {
        val config = RealmConfiguration.Builder(schema = setOf(Check::class, OrderDetail::class, Payment::class))
            .name("vpos-db").deleteRealmIfMigrationNeeded().build()
        return Realm.open(config)
    }


}

// some constants
const val CHECK_COLUMN_WIDTH = (100/8).toFloat()

fun Double.formatAmount(): String {
    return "%.2f".format(Locale.ENGLISH, this)
}