package ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker


@Composable
fun SettingsView() {

    var showFilePicker by remember { mutableStateOf(false) }
    var showDirPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        Column {
            Button(
                onClick = { showFilePicker = true }
            ){
                Text("Open a File")
            }

            Button(
                onClick = { showDirPicker = true }
            ){
                Text("Open a Directory")
            }
        }


        FilePicker(show = showFilePicker, fileExtension = "jpg") { filePath ->
            println("File path is => $filePath")
            showFilePicker = false
        }

        DirectoryPicker(show = showDirPicker) { dirPath ->
            println("Dir path is => $dirPath")
            showDirPicker = false
        }

    }

}