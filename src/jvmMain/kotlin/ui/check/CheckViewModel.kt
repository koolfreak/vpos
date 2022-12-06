package ui.check

import com.google.gson.GsonBuilder
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import models.Check
import models.OrderDetail
import utils.ViewModel
import java.nio.ByteBuffer
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

class CheckViewModel(coroutineScope: CoroutineScope) : ViewModel() {

    init {
       viewModelScope = coroutineScope
    }

    private var realm: Realm = provideRealm()
    var orders = mutableListOf<OrderDetail>()

    private val _checkState = MutableStateFlow<CheckEntryState>(CheckEntryState.Empty)
    val checkState: StateFlow<CheckEntryState> = _checkState

    fun addNewCheckDetail(check: Check, orders: List<OrderDetail>) {
       viewModelScope.launch {
           realm.write {
                val checkObj = copyToRealm(check)
               if(orders.isNotEmpty()) {
                   orders.forEach { order ->
                       checkObj.orderDetails.add(order)
                   }
                   copyToRealm(checkObj)
               }
                _checkState.value = CheckEntryState.CheckSuccess
            }
        }
    }

    fun updateCheckDetail(newCheck: Check, oldCheck: Check, orders: List<OrderDetail>) {
        viewModelScope.launch {
            realm.write {
                val check = findLatest(oldCheck)
                check?.tableNumber = newCheck.tableNumber
                check?.checkNumber = newCheck.checkNumber
                check?.serverId = newCheck.serverId
                check?.totalAmount = newCheck.totalAmount
                check?.tipAmount = newCheck.tipAmount
                check?.taxAmount = newCheck.taxAmount
                check?.baseAmount = newCheck.baseAmount

                /*if( orders.isNotEmpty() ){
                    check?.orderDetails?.clear()
                    orders.forEach {
                        val upOrder = findLatest(it)
                        if (upOrder != null) {
                            check?.orderDetails?.add(upOrder)
                        }
                    }
                }*/
                _checkState.value = CheckEntryState.CheckSuccess
            }
        }
    }

    fun addNewOrder(orderDetail: OrderDetail) {
        orders.add(orderDetail)
        val total = orders.sumOf { it.totalPrice }
        val updatedOrder = ArrayList(orders)
        _checkState.value = CheckEntryState.OrderSuccess(updatedOrder, total)
    }

    fun removeOrder(uuid: String) {
        orders.removeIf { it.uuid == uuid }
        val total = orders.sumOf { it.totalPrice }
        val updatedOrder = ArrayList(orders)
        _checkState.value = CheckEntryState.OrderSuccess(updatedOrder, total)
    }

    fun deleteCheck(uuid: String) {
        viewModelScope.launch {
            realm.write {
                val _check = this.query<Check>("uuid == $0", uuid).find().first()
                this.delete(_check)
                _checkState.value = CheckEntryState.CheckDeleted
            }
        }
    }

    fun closeCheck(check: Check) {
        viewModelScope.launch {
            realm.write {
                findLatest(check)?.status = "Close"
                _checkState.value = CheckEntryState.CheckClosed
            }
        }
    }

    fun getAllChecks() {
        viewModelScope.launch {
            val results = realm.query<Check>().find()
            _checkState.value = CheckEntryState.CheckList(results.toList())
        }
    }

    @Deprecated("For testing of tcp/ip only")
    fun testTcpConnection() {
        GlobalScope.launch(Dispatchers.Default) {
            val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
                .connect(InetSocketAddress("192.168.1.7", 9002))
            //val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = true)

            val randomMessage = returnRandomString()
            val buffer = ByteBuffer.wrap(randomMessage.toByteArray())
            output.writeFully(buffer)
            //println("Server said: '${input.readUTF8Line(5000)}'")
        }
    }

    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    @Deprecated("For testing of tcp/ip only")
    fun returnRandomString(): String {
        return ThreadLocalRandom.current()
            .ints(100.toLong(), 0, charPool.size)
            .asSequence()
            .map(charPool::get)
            .joinToString("").plus("\r\n")
    }
    fun convertTOJsonString(check: Check) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val wrapper = CheckWrapper(check)
        _checkState.value = CheckEntryState.CheckDetail(gson.toJson(wrapper))
    }

    sealed interface CheckEntryState {
        object Empty: CheckEntryState
        object CheckSuccess: CheckEntryState
        object CheckDeleted: CheckEntryState
        object CheckClosed: CheckEntryState
        data class CheckDetail(var detail: String): CheckEntryState
        data class CheckList(var checks: List<Check>): CheckEntryState
        data class OrderSuccess(var orders: MutableList<OrderDetail>, var total: Double): CheckEntryState
    }
}