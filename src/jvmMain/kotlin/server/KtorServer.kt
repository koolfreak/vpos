package server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Check
import ui.check.CheckResponse
import utils.ViewModel

// https://ktor.io/docs/intellij-idea.html#create_ktor_project
// https://github.com/ktorio/ktor-documentation/tree/2.1.3/codeSnippets/snippets/tutorial-http-api
class KtorServer(coroutineScope: CoroutineScope) : ViewModel() {

    private val _serverState = MutableStateFlow<ServerState>(ServerState.Empty)
    val serverState: StateFlow<ServerState> = _serverState
    var logBuffer = StringBuffer()
    private val realm: Realm = provideRealm()
    private val gson: Gson = GsonBuilder().create()
    init {
        viewModelScope = coroutineScope
    }

    fun init() {
        _serverState.value = ServerState.Init
    }
    fun startKtorHttpServer() {
        embeddedServer(Netty, port = 8090) {
            routing {
                get("/") {
                    call.respondText("Hello, World!")
                }
            }
        }.start(wait = true)
    }

    // https://ktor.io/docs/servers-raw-sockets.html#se.(Dispatchers.IO)cure
// https://sylhare.github.io/2021/06/08/Kotlin-tcp-with-ktor.html
    fun startKtorTcpServer() {

        CoroutineScope(Dispatchers.IO).launch {

            val selectorManager = SelectorManager(Dispatchers.IO)
            val serverSocket = aSocket(selectorManager).tcp().bind("0.0.0.0", 9002) /* 0.0.0.0 - bind to all address to this machine */

            logBuffer.append("Server is listening at ${serverSocket.localAddress}").append("\n")
            _serverState.value = ServerState.Logging(logBuffer.toString())
            while (true) {
                val socket = serverSocket.accept()

                launch {
                    val receiveChannel = socket.openReadChannel()
                    val sendChannel = socket.openWriteChannel(autoFlush = true)

                    val request = receiveChannel.readUTF8Line()
                    val receiveReq = "[REQUEST] \n\t$request"
                    logBuffer.append(receiveReq).append("\n\n")
                    _serverState.value = ServerState.Logging(logBuffer.toString())

                    val response = getServerResponse("GetChecks")
                    val sendResponse = "[RESPONSE] \n\t$response"
                    logBuffer.append(sendResponse).append("\n\n")
                    _serverState.value = ServerState.Logging(logBuffer.toString())
                    sendChannel.writeStringUtf8("$response\n\n")
                    try {
                        while (true) {
                            val name = receiveChannel.readUTF8Line()
                            sendChannel.writeStringUtf8("Hello, $name!\n")
                        }
                    } catch (e: Throwable) {
                        withContext(Dispatchers.IO) {
                            socket.close()
                        }
                    }
                }
            }

            /*val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
                .bind(InetSocketAddress("0.0.0.0", 9002))
            println("Server running: ${server.localAddress}")
            val socket = server.accept()
            println("Socket accepted: ${socket.remoteAddress}")
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = true)
            val line = input.readUTF8Line()
            //println("Server received '$line' from ${socket.remoteAddress}")
            val response = getServerResponse("GetChecks")
            output.writeFully("$response\r\n".toByteArray())*/
        }
    }

    private fun getServerResponse(command: String): String {
        return when(command) {
            "GetChecks" -> {
                val checks = realm.query<Check>().find()
                val checkResponses = mutableListOf<CheckResponse>()
                checks.forEach {
                    checkResponses.add(CheckResponse(it))
                }
                gson.toJson(checkResponses)
            }
            else -> { gson.toJson(emptyList<Check>()) }
        }
    }

    sealed class ServerState {
        object Empty: ServerState()
        object Init: ServerState()
        data class Logging(var log: String): ServerState()
    }

}