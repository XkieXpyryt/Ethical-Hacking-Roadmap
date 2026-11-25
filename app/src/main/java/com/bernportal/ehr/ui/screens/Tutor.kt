@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.R
import com.bernportal.ehr.data.db.ChatMsg
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.util.copyText
import com.bernportal.ehr.util.toast

private val QUICK = listOf("Explain", "Give me a hint", "Show an example", "Quiz me", "Go deeper", "Summarise", "Create flashcards", "Help me troubleshoot")
private val SUGGESTED = listOf(
    "Explain the OSI model with a simple analogy", "What is the difference between hashing and encryption?", "How do I read Nmap port states?",
    "Explain IDOR and how developers fix it", "Help me plan my study week", "How do I write a good executive summary?",
    "Quiz me on subnetting", "What does a SOC analyst do day to day?"
)

@Composable
fun TutorScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val ctx = LocalContext.current
    val msgs by vm.messages.collectAsStateWithLifecycle()
    val busy by vm.tutorBusy.collectAsStateWithLifecycle()
    val online by vm.online.collectAsStateWithLifecycle()
    val s by vm.settings.collectAsStateWithLifecycle()
    val lessonId by vm.tutorLesson.collectAsStateWithLifecycle()
    val draft by vm.tutorDraft.collectAsStateWithLifecycle()
    val sessions by vm.sessions.collectAsStateWithLifecycle()
    val u by vm.user.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    var mode by remember(s?.tutorMode) { mutableStateOf(s?.tutorMode ?: "Beginner tutor") }
    var modeMenu by remember { mutableStateOf(false) }
    var showSessions by remember { mutableStateOf(false) }
    var attach by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val hasKey = remember(s) { vm.aiKey().isNotBlank() || s?.aiProvider == "custom" }
    val lesson = lessonId?.let { vm.content.lesson(it) }

    LaunchedEffect(draft) { if (draft.isNotBlank()) { input = draft; vm.tutorDraft.value = "" } }
    LaunchedEffect(msgs.size, busy) { if (msgs.isNotEmpty()) listState.animateScrollToItem(msgs.size + (if (busy) 1 else 0)) }

    val voice = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        if (res.resultCode == Activity.RESULT_OK) res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.let { input = (input + " " + it).trim() }
    }

    fun send(text: String) { if (text.isNotBlank() && !busy) { vm.askTutor(text, mode); input = "" } }

    Column(Modifier.fillMaxSize()) {
        // Header controls
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box {
                AssistChip({ modeMenu = true }, { Text(mode, maxLines = 1) }, leadingIcon = { Icon(Icons.Filled.Psychology, null, Modifier.size(18.dp)) })
                DropdownMenu(modeMenu, { modeMenu = false }) {
                    vm.tutorEngine.modes.forEach { m -> DropdownMenuItem({ Text(m) }, { mode = m; modeMenu = false; vm.updateSettings { it.copy(tutorMode = m) } }) }
                }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { showSessions = true }) { Icon(Icons.Filled.History, "Conversation history") }
            IconButton(onClick = { vm.newSession("New session", mode) }) { Icon(Icons.Filled.AddComment, "New topic session") }
            IconButton(onClick = { vm.clearSession() }, enabled = msgs.isNotEmpty()) { Icon(Icons.Filled.DeleteSweep, "Clear session") }
        }
        if (lesson != null) Row(Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.MenuBook, null, Modifier.size(16.dp), tint = Brand.Gold); Spacer(Modifier.width(6.dp))
            Text("Lesson context: ${lesson.title}", style = MaterialTheme.typography.labelSmall, color = Brand.Gold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            TextButton(onClick = { nav.go("lesson/${lesson.id}") }) { Text("Open") }
            IconButton(onClick = { vm.tutorLesson.value = null }) { Icon(Icons.Filled.Close, "Remove context", Modifier.size(16.dp)) }
        }
        if (!online || !hasKey) {
            GlassCard(Modifier.fillMaxWidth().padding(horizontal = 12.dp), glow = Brand.Warn, padding = 10.dp) {
                Text(if (!online) "You are offline. Answers will come from the offline course assistant (bundled lessons, not AI). All lessons still work."
                    else "Add your own free AI key (e.g. Google Gemini or Groq) in Settings > AI tutor to enable the online tutor. Until then the offline course assistant answers.",
                    style = MaterialTheme.typography.bodySmall)
                if (online && !hasKey) TextButton(onClick = { nav.go("settings") }) { Text("Open AI settings") }
            }
        }

        LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (msgs.isEmpty()) item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    BrandImage(R.drawable.ic3d_support, 84.dp)
                    Text("Your personal cybersecurity tutor", style = MaterialTheme.typography.titleLarge)
                    Text("I explain, give hints before answers, quiz you and connect everything to your lessons - always within ethical, authorised boundaries.",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SUGGESTED.forEach { p -> SuggestionChip({ send(p) }, { Text(p) }) }
                    }
                }
            }
            items(msgs, key = { it.id }) { m -> Bubble(m, lesson?.id) }
            if (busy) item { Typing() }
        }

        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            QUICK.forEach { q ->
                AssistChip({
                    val topic = lesson?.title ?: msgs.lastOrNull { it.role == "user" }?.text?.take(80) ?: "my current lesson"
                    send(when (q) {
                        "Explain" -> "Explain $topic clearly for my level."
                        "Give me a hint" -> "Give me a hint (not the answer) for what I'm working on: $topic"
                        "Show an example" -> "Show a realistic, safe example of $topic."
                        "Quiz me" -> "Quiz me with 3 questions on $topic, one at a time, and wait for my answers."
                        "Go deeper" -> "Go deeper technically on $topic."
                        "Summarise" -> "Summarise $topic in 5 bullet points."
                        "Create flashcards" -> "Create 5 flashcards (Q: / A:) on $topic."
                        else -> "Help me troubleshoot step by step. Context: $topic"
                    })
                }, { Text(q) })
            }
        }
        if (attach) {
            val notes = u.notes.take(12)
            GlassCard(Modifier.fillMaxWidth().padding(8.dp), padding = 8.dp) {
                Text("Attach a note or lab evidence to your question", style = MaterialTheme.typography.labelLarge)
                if (notes.isEmpty()) Text("No notes yet.", style = MaterialTheme.typography.bodySmall)
                notes.forEach { n -> TextButton(onClick = { input = (input + "\n\n[Attached: ${n.refTitle}]\n" + n.text.take(1500)).trim(); attach = false }) { Text(n.refTitle, maxLines = 1) } }
            }
        }
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { attach = !attach }) { Icon(Icons.Filled.AttachFile, "Attach note") }
            OutlinedTextField(input, { input = it }, placeholder = { Text("Ask a cybersecurity question...") }, modifier = Modifier.weight(1f), maxLines = 5)
            IconButton(onClick = {
                val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                try { voice.launch(i) } catch (e: Exception) { toast(ctx, "Voice input is not available on this device") }
            }) { Icon(Icons.Filled.Mic, "Voice input") }
            FilledIconButton(onClick = { send(input) }, enabled = input.isNotBlank() && !busy) { Icon(Icons.AutoMirrored.Filled.Send, "Send") }
        }
    }

    if (showSessions) ModalBottomSheet(onDismissRequest = { showSessions = false }) {
        Column(Modifier.padding(16.dp).navigationBarsPadding()) {
            Text("Topic sessions", style = MaterialTheme.typography.titleLarge)
            if (sessions.isEmpty()) Text("No sessions yet.")
            sessions.forEach { ss ->
                ListItem(headlineContent = { Text(ss.title, maxLines = 1) }, supportingContent = { Text(ss.mode) },
                    trailingContent = { IconButton(onClick = { vm.deleteSession(ss.id) }) { Icon(Icons.Filled.Delete, "Delete") } },
                    modifier = Modifier.clickable { vm.currentSession.value = ss.id; showSessions = false })
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Bubble(m: ChatMsg, lessonId: String?) {
    val vm = LocalVm.current; val nav = LocalNav.current
    val ctx = LocalContext.current
    val mine = m.role == "user"
    Column(Modifier.fillMaxWidth(), horizontalAlignment = if (mine) Alignment.End else Alignment.Start) {
        Column(
            Modifier.widthIn(max = 560.dp).clip(RoundedCornerShape(18.dp, 18.dp, if (mine) 4.dp else 18.dp, if (mine) 18.dp else 4.dp))
                .background(if (mine) Brush.linearGradient(listOf(Brand.CyberBlue, Brand.Violet)) else Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)))
                .padding(12.dp)
        ) {
            RichText(m.text, if (mine) Color.White else MaterialTheme.colorScheme.onSurface)
        }
        if (!mine) Row {
            IconButton(onClick = { copyText(ctx, m.text) }) { Icon(Icons.Filled.ContentCopy, "Copy answer", Modifier.size(18.dp)) }
            IconButton(onClick = { vm.saveNote("tutor:${m.id}", "AI tutor answer", m.text); toast(ctx, "Saved to notes") }) { Icon(Icons.Filled.NoteAdd, "Save to notes", Modifier.size(18.dp)) }
            IconButton(onClick = { vm.toggleBookmark("tutor:${m.id}", m.text.take(60), "Tutor answer", "tutor"); toast(ctx, "Bookmarked") }) { Icon(Icons.Filled.BookmarkAdd, "Bookmark", Modifier.size(18.dp)) }
            IconButton(onClick = { vm.addCard(m.text.lineSequence().firstOrNull { it.isNotBlank() }?.take(120) ?: "Tutor note", m.text.take(1000)); toast(ctx, "Flashcard created") }) { Icon(Icons.Filled.Style, "Create flashcard", Modifier.size(18.dp)) }
            IconButton(onClick = { nav.go(if (lessonId != null) "quiz/lesson:$lessonId" else "quiz/daily") }) { Icon(Icons.Filled.Quiz, "Start quiz", Modifier.size(18.dp)) }
        }
    }
}

/** Minimal markdown rendering: ``` code blocks (with code panel), **bold** stripped, bullet lines. */
@Composable
fun RichText(text: String, color: Color) {
    val parts = text.split("```")
    Column {
        parts.forEachIndexed { i, part ->
            if (i % 2 == 1) {
                val code = part.substringAfter("\n", part)
                val ctx = LocalContext.current
                Box { MonoBlock(code.trimEnd()); IconButton(onClick = { copyText(ctx, code) }, Modifier.align(Alignment.TopEnd)) { Icon(Icons.Filled.ContentCopy, "Copy code", Modifier.size(16.dp), tint = Brand.Muted) } }
            } else if (part.isNotBlank()) {
                part.trim('\n').lines().forEach { line ->
                    val clean = line.replace("**", "").replace(Regex("^#{1,6}\\s*"), "")
                    val heading = line.trimStart().startsWith("#") || (line.startsWith("**") && line.endsWith("**") && line.length > 4)
                    Text(clean.replace(Regex("^\\s*[-*] "), "• "), color = color, style = if (heading) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                        fontWeight = if (heading) FontWeight.Bold else null)
                }
            }
        }
    }
}

@Composable
private fun Typing() {
    val t = rememberInfiniteTransition(label = "typing")
    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { i ->
            val a by t.animateFloat(0.2f, 1f, infiniteRepeatable(tween(500, delayMillis = i * 150), RepeatMode.Reverse), label = "d$i")
            Box(Modifier.padding(3.dp).size(9.dp).graphicsLayer { alpha = a; translationY = -a * 6f }.clip(CircleShape).background(Brand.Cyan))
        }
        Spacer(Modifier.width(8.dp)); Text("Tutor is thinking...", style = MaterialTheme.typography.labelMedium)
    }
}
