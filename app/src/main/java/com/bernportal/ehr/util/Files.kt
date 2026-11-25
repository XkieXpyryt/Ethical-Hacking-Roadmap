package com.bernportal.ehr.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Save a generated file through the Android system file picker (Storage Access Framework).
 * The picker lets the learner choose local storage OR Google Drive (if the Drive app is installed) - no extra libraries or API keys needed.
 */
class FileSaver(val launch: (fileName: String, produce: suspend () -> ByteArray) -> Unit)

@Composable
fun rememberFileSaver(mime: String): FileSaver {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var producer by remember { mutableStateOf<(suspend () -> ByteArray)?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(mime)) { uri: Uri? ->
        val p = producer
        if (uri != null && p != null) scope.launch {
            val ok = withContext(Dispatchers.IO) {
                try { val bytes = p(); ctx.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }; true } catch (e: Exception) { false }
            }
            toast(ctx, if (ok) "Saved" else "Could not save the file")
        }
    }
    return remember { FileSaver { name, produce -> producer = produce; launcher.launch(name) } }
}

@Composable
fun rememberFileOpener(mimes: Array<String>, onText: suspend (Uri) -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) scope.launch { onText(uri) }
    }
    return { launcher.launch(mimes) }
}

fun toast(ctx: Context, msg: String) = Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()

fun shareText(ctx: Context, title: String, text: String) {
    val i = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_SUBJECT, title).putExtra(Intent.EXTRA_TEXT, text)
    ctx.startActivity(Intent.createChooser(i, title))
}

fun openUrl(ctx: Context, url: String) {
    try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (e: Exception) { toast(ctx, "No browser available") }
}

fun copyText(ctx: Context, text: String) {
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    cm.setPrimaryClip(android.content.ClipData.newPlainText("Ethical Hacking Roadmap", text))
    Toast.makeText(ctx, "Copied", Toast.LENGTH_SHORT).show()
}
