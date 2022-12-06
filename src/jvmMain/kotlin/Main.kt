// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.DelicateCoroutinesApi
import server.KtorServer
import ui.SegmentedControl
import ui.check.CheckDetailsView
import ui.settings.SettingsView

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun MainApp() {
    val segmentedSelections = listOf("Check Details", "Settings")
    var selectedTab by remember { mutableStateOf(0) }
    var serverLogText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState(0)
    val coroutineScope = rememberCoroutineScope()
    val ktorServer = KtorServer(coroutineScope)

    LaunchedEffect(ktorServer) {
        ktorServer.serverState.collect { uiState ->
            when(uiState) {
                is KtorServer.ServerState.Logging -> {
                    serverLogText = uiState.log
                }
                else -> { }
            }
        }
    }

    MaterialTheme {
        Column (
            modifier = Modifier.fillMaxSize()
        ){

            SegmentedControl(
                selections = segmentedSelections,
                selectedIndex = selectedTab,
                onSelectedIndex = { selectedTab = it }
            )

            if( selectedTab == 0) {
               CheckDetailsView()
            }else{
                SettingsView()
            }

            // SERVER PANEL
            Column (
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {

                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = serverLogText, modifier = Modifier.fillMaxWidth().height(200.dp).verticalScroll(scrollState).height(200.dp).fillMaxWidth()
                            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp)))
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

                Row(modifier = Modifier.padding(top = 10.dp)) {
                    Button(onClick = {
                        //GlobalScope.launch(Dispatchers.IO) {
                            ktorServer.startKtorTcpServer()
                        //}
                    }) {
                        Text("Start Server")
                    }
                }

            }

        }
    }
}

fun main() = application {
    val windowState = rememberWindowState(position = WindowPosition.Aligned(Alignment.Center))
    Window(onCloseRequest = ::exitApplication,
        state = windowState,
        title = "vPOS Simulator 1.0") {
        MainApp()
    }
}
