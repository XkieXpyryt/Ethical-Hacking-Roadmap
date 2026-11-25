@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.R
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import com.bernportal.ehr.util.copyText

// ======================= Toolkit =======================
@Composable
fun ToolkitScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    var q by remember { mutableStateOf("") }
    var tab by remember { mutableIntStateOf(0) }
    val groups = vm.content.tools.groupBy { it.category }
    Column {
        PrimaryTabRow(tab, containerColor = Color.Transparent) { listOf("Tools", "Command library").forEachIndexed { i, t -> Tab(tab == i, { tab = i }, text = { Text(t) }) } }
        OutlinedTextField(q, { q = it }, leadingIcon = { Icon(Icons.Filled.Search, null) }, placeholder = { Text(if (tab == 0) "Search tools" else "Search commands") },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp))
        LazyColumn(contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (tab == 0) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandImage(R.drawable.ic3d_toolkit, 48.dp); Spacer(Modifier.width(10.dp))
                        Text("Offline tool dictionary. Every tool must be used only on systems you own or are authorised to test.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                groups.forEach { (cat, tools) ->
                    val shown = tools.filter { q.isBlank() || it.name.contains(q, true) || it.description.contains(q, true) }
                    if (shown.isNotEmpty()) {
                        item { SectionTitle(cat) }
                        items(shown) { t ->
                            GlassCard(Modifier.fillMaxWidth(), onClick = { nav.go("toolref/${t.id}") }, padding = 12.dp) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Glossy3DIcon(Icons.Filled.Build, paletteFor(t.name.length), 42.dp); Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) { Text(t.name, style = MaterialTheme.typography.titleSmall); Text(t.description, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                                    Pill(t.licence, Brand.Neon)
                                }
                            }
                        }
                    }
                }
            } else {
                val cmds = vm.content.tools.flatMap { t -> t.commands.map { it to t } } + vm.content.labs.flatMap { l -> l.tasks.filter { it.hint.any { c -> c == ' ' } && !it.hint.contains("Use the") }.map { it.hint to null } }
                item { Text("Examples are for isolated lab environments only.", style = MaterialTheme.typography.labelMedium, color = Brand.Warn) }
                items(cmds.filter { q.isBlank() || it.first.contains(q, true) || (it.second?.name?.contains(q, true) ?: false) }) { (c, t) ->
                    val ctx = LocalContext.current
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFF02060F)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(c, fontFamily = FontFamily.Monospace, color = Color(0xFF9EF0B5), style = MaterialTheme.typography.bodySmall)
                            Text(t?.name ?: "Lab command", style = MaterialTheme.typography.labelSmall, color = Brand.Muted)
                        }
                        IconButton(onClick = { copyText(ctx, c) }) { Icon(Icons.Filled.ContentCopy, "Copy", tint = Brand.Muted) }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolRefScreen(id: String) {
    val vm = LocalVm.current; val nav = LocalNav.current
    val t = vm.content.tool(id) ?: return
    val u by vm.user.collectAsStateWithLifecycle()
    LaunchedEffect(id) { vm.markViewed(t.id, t.name, "toolref/${t.id}") }
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item { Column {
            Breadcrumb(listOf("Toolkit", t.category))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Glossy3DIcon(Icons.Filled.Build, paletteFor(t.name.length), 60.dp); Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) { Text(t.name, style = MaterialTheme.typography.headlineSmall); Text("${t.category} · ${t.licence}", style = MaterialTheme.typography.labelMedium) }
                IconButton(onClick = { vm.toggleBookmark(t.id, t.name, "Tool", "toolref/${t.id}") }) {
                    Icon(if (u.bookmarks.any { it.refId == t.id }) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder, "Bookmark", tint = Brand.Gold)
                }
            }
            GlassCard(Modifier.padding(vertical = 8.dp), glow = Brand.Warn) { Text("Ethical and legal requirements", style = MaterialTheme.typography.titleSmall, color = Brand.Warn); Text(t.ethics) }
            Field("Description", t.description); Field("Purpose", t.purpose); Field("Installation overview", t.install); Field("Operating system compatibility", t.os)
            Field("Beginner guide", t.beginner); Field("Interface overview", t.interface_)
            SectionTitle("Common legitimate use cases"); BulletList(t.uses)
            SectionTitle("Safe practice exercises"); BulletList(t.practice, Brand.Neon)
            SectionTitle("Limitations"); BulletList(t.limitations, Brand.Warn)
            SectionTitle("Common mistakes"); BulletList(t.mistakes, Brand.Danger)
            Field("Defensive detection", t.detection)
            if (t.commands.isNotEmpty()) { SectionTitle("Example commands (isolated lab only)"); MonoBlock(t.commands.joinToString("\n")) }
            SectionTitle("Related lessons")
            t.lessons.mapNotNull { vm.content.lesson(it) }.forEach { l -> TextButton(onClick = { nav.go("lesson/${l.id}") }) { Text(l.title) } }
            if (t.labs.isNotEmpty()) { SectionTitle("Related labs"); t.labs.mapNotNull { vm.content.lab(it) }.forEach { l -> TextButton(onClick = { nav.go("lab/${l.id}") }) { Text(l.title) } } }
            SectionTitle("Alternative tools"); ChipRow(t.alternatives)
        } }
    }
}

@Composable private fun Field(title: String, body: String) { if (body.isNotBlank()) { SectionTitle(title); Text(body, style = MaterialTheme.typography.bodyMedium) } }

// ======================= Glossary =======================
@Composable
fun GlossaryScreen(initial: String) {
    val vm = LocalVm.current; val nav = LocalNav.current
    var q by remember { mutableStateOf(initial) }
    var cat by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(initial) }
    val cats = vm.content.glossary.map { it.category }.distinct().sorted()
    val list = vm.content.glossary.filter { (cat == null || it.category == cat) && (q.isBlank() || it.term.contains(q, true) || it.definition.contains(q, true)) }
    Column {
        OutlinedTextField(q, { q = it }, leadingIcon = { Icon(Icons.Filled.Search, null) }, placeholder = { Text("Search ${vm.content.glossary.size} terms") },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(cat == null, { cat = null }, { Text("All") }); cats.forEach { c -> FilterChip(cat == c, { cat = if (cat == c) null else c }, { Text(c) }) }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(list, key = { it.term }) { g ->
                val open = expanded.equals(g.term, true)
                GlassCard(Modifier.fillMaxWidth(), onClick = { expanded = if (open) "" else g.term }, padding = 12.dp, glow = if (open) Brand.Cyan else MaterialTheme.colorScheme.outline) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(g.term, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f)); Pill(g.category, Brand.Violet)
                    }
                    Text(g.definition, style = MaterialTheme.typography.bodyMedium, maxLines = if (open) 20 else 2, overflow = TextOverflow.Ellipsis)
                    if (open) {
                        Text("Example: ${g.example}", style = MaterialTheme.typography.bodySmall, color = Brand.Gold, modifier = Modifier.padding(top = 4.dp))
                        if (g.related.isNotEmpty()) { Text("Related terms", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 6.dp)); ChipRow(g.related) { r -> q = r; expanded = r } }
                        Row(Modifier.padding(top = 6.dp)) {
                            vm.content.lesson(g.lesson)?.let { l -> TextButton(onClick = { nav.go("lesson/${l.id}") }) { Icon(Icons.Filled.MenuBook, null); Text(" Lesson") } }
                            if (g.lab.isNotBlank()) TextButton(onClick = { nav.go("lab/${g.lab}") }) { Icon(Icons.Filled.Science, null); Text(" Lab") }
                            TextButton(onClick = { vm.addCard(g.term, g.definition + "\n\nExample: " + g.example, "glossary") }) { Icon(Icons.Filled.Style, null); Text(" Flashcard") }
                        }
                    }
                }
            }
        }
    }
}

// ======================= Search =======================
@Composable
fun SearchScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    var q by remember { mutableStateOf("") }
    var types by remember { mutableStateOf(setOf<String>()) }
    var diff by remember { mutableStateOf<String?>(null) }
    var module by remember { mutableStateOf<String?>(null) }
    var moduleMenu by remember { mutableStateOf(false) }
    val results = remember(q, types, diff, module) { vm.content.search(q, types.filter { it !in listOf("Notes", "Bookmarks", "Flashcards") }.toSet(), diff, module) }
    val noteHits = if (q.length > 1 && (types.isEmpty() || "Notes" in types)) u.notes.filter { it.text.contains(q, true) || it.refTitle.contains(q, true) } else emptyList()
    val bmHits = if (q.length > 1 && (types.isEmpty() || "Bookmarks" in types)) u.bookmarks.filter { it.title.contains(q, true) } else emptyList()
    val cardHits = if (q.length > 1 && (types.isEmpty() || "Flashcards" in types)) u.cards.filter { it.front.contains(q, true) || it.back.contains(q, true) } else emptyList()
    Column {
        OutlinedTextField(q, { q = it }, leadingIcon = { Icon(Icons.Filled.Search, null) }, placeholder = { Text("Search lessons, tools, commands, labs, notes...") },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Beginner", "Intermediate", "Advanced").forEach { d -> FilterChip(diff == d, { diff = if (diff == d) null else d }, { Text(d) }) }
            Box {
                FilterChip(module != null, { moduleMenu = true }, { Text(module?.let { m -> "Module ${vm.content.module(m)?.number}" } ?: "Module") })
                DropdownMenu(moduleMenu, { moduleMenu = false }) {
                    DropdownMenuItem({ Text("All modules") }, { module = null; moduleMenu = false })
                    vm.content.modules.forEach { m -> DropdownMenuItem({ Text("${m.number}. ${m.title}") }, { module = m.id; moduleMenu = false }) }
                }
            }
            listOf("Lesson", "Tool", "Command", "Lab", "Project", "Glossary", "Challenge", "Troubleshooting", "Notes", "Bookmarks", "Flashcards").forEach { t ->
                FilterChip(t in types, { types = if (t in types) types - t else types + t }, { Text(t) })
            }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (q.isBlank()) item { EmptyState(Icons.Filled.Search, "Search the whole course offline", "Try: subnet, XSS, Nmap, Kerberos, CVSS, chmod, phishing") }
            else if (results.isEmpty() && noteHits.isEmpty() && bmHits.isEmpty() && cardHits.isEmpty()) item { Text("No results for \"$q\".", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(results) { r ->
                GlassCard(Modifier.fillMaxWidth(), onClick = { nav.go(r.route) }, padding = 10.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Pill(r.type, typeColor(r.type)); Spacer(Modifier.width(8.dp))
                        Text(r.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, fontFamily = if (r.type == "Command") FontFamily.Monospace else null)
                    }
                    Text(r.snippet, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(noteHits) { n -> GlassCard(Modifier.fillMaxWidth(), onClick = { nav.go("notes") }, padding = 10.dp) { Pill("Note", Brand.Gold); Text(n.refTitle, style = MaterialTheme.typography.titleSmall); Text(n.text, maxLines = 2, style = MaterialTheme.typography.bodySmall) } }
            items(bmHits) { b -> GlassCard(Modifier.fillMaxWidth(), onClick = { nav.go(b.route) }, padding = 10.dp) { Pill("Bookmark", Brand.Gold); Text(b.title, style = MaterialTheme.typography.titleSmall) } }
            items(cardHits) { c -> GlassCard(Modifier.fillMaxWidth(), onClick = { nav.go("revision") }, padding = 10.dp) { Pill("Flashcard", Brand.Violet); Text(c.front, style = MaterialTheme.typography.titleSmall) } }
        }
    }
}

private fun typeColor(t: String) = when (t) { "Lesson" -> Brand.Cyan; "Lab" -> Brand.Neon; "Tool" -> Brand.Gold; "Command" -> Color(0xFF9EF0B5); "Glossary" -> Brand.Violet; "Project" -> Brand.CyberBlue; else -> Brand.Muted }

// ======================= Notes & bookmarks =======================
@Composable
fun NotesScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    var tab by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf<com.bernportal.ehr.data.db.Note?>(null) }
    var newText by remember { mutableStateOf("") }
    Column {
        PrimaryTabRow(tab, containerColor = Color.Transparent) { listOf("Notes (${u.notes.size})", "Bookmarks (${u.bookmarks.size})").forEachIndexed { i, t -> Tab(tab == i, { tab = i }, text = { Text(t) }) } }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (tab == 0) {
                item {
                    GlassCard {
                        OutlinedTextField(newText, { newText = it }, placeholder = { Text("Quick note...") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                        Button(onClick = { vm.saveNote("general", "General note", newText); newText = "" }, enabled = newText.isNotBlank()) { Text("Add note") }
                    }
                }
                if (u.notes.isEmpty()) item { EmptyState(Icons.Filled.EditNote, "No notes yet", "Write notes inside lessons, labs and projects - they all appear here.") }
                items(u.notes, key = { it.id }) { n ->
                    GlassCard(Modifier.fillMaxWidth(), onClick = { editing = n }, padding = 12.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(n.refTitle, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(com.bernportal.ehr.util.PdfBuilder.date(n.at), style = MaterialTheme.typography.labelSmall)
                        }
                        Text(n.text, style = MaterialTheme.typography.bodySmall, maxLines = 4, overflow = TextOverflow.Ellipsis)
                    }
                }
            } else {
                if (u.bookmarks.isEmpty()) item { EmptyState(Icons.Filled.Bookmarks, "No bookmarks yet", "Tap the bookmark icon on lessons, tools and tutor answers.") }
                items(u.bookmarks, key = { it.refId }) { b ->
                    GlassCard(Modifier.fillMaxWidth(), onClick = { nav.go(b.route) }, padding = 12.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Bookmark, null, tint = Brand.Gold); Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) { Text(b.title, style = MaterialTheme.typography.titleSmall, maxLines = 2); Text(b.kind, style = MaterialTheme.typography.labelSmall) }
                            IconButton(onClick = { vm.toggleBookmark(b.refId, b.title, b.kind, b.route) }) { Icon(Icons.Filled.Delete, "Remove bookmark") }
                        }
                    }
                }
            }
        }
    }
    editing?.let { n ->
        var text by remember(n.id) { mutableStateOf(n.text) }
        AlertDialog(onDismissRequest = { editing = null }, title = { Text(n.refTitle) },
            text = { OutlinedTextField(text, { text = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp)) },
            confirmButton = { TextButton(onClick = { vm.saveNote(n.refId, n.refTitle, text, n.id); editing = null }) { Text("Save") } },
            dismissButton = { Row { TextButton(onClick = { vm.deleteNote(n.id); editing = null }) { Text("Delete", color = Brand.Danger) }; TextButton(onClick = { editing = null }) { Text("Cancel") } } })
    }
}

// ======================= Revision centre / flashcards =======================
@Composable
fun RevisionScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val motion = LocalMotion.current
    val now = System.currentTimeMillis()
    val due = u.cards.filter { it.due <= now }
    var studying by remember { mutableStateOf(false) }
    var flipped by remember { mutableStateOf(false) }
    var front by remember { mutableStateOf("") }; var back by remember { mutableStateOf("") }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("${u.cards.size}", "Flashcards", Icons.Filled.Style, Brand.Violet, Modifier.weight(1f))
                StatTile("${due.size}", "Due now", Icons.Filled.Schedule, Brand.Warn, Modifier.weight(1f))
                StatTile("${u.cards.count { it.box >= 4 }}", "Mastered", Icons.Filled.Verified, Brand.Neon, Modifier.weight(1f))
            }
        }
        if (studying && due.isNotEmpty()) item {
            val c = due.first()
            val rot by animateFloatAsState(if (flipped) 180f else 0f, tween(motion.dur(500)), label = "flip")
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.fillMaxWidth().height(260.dp).graphicsLayer { rotationY = if (motion.effects3d) rot else 0f; cameraDistance = 16f * density }
                        .clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(if (rot < 90f) listOf(Brand.CyberBlue, Brand.Violet) else listOf(Color(0xFF065F46), Brand.Cyan)))
                        .clickable { flipped = !flipped }.padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (rot < 90f) c.front else c.back, color = Color.White, textAlign = TextAlign.Center, style = if (rot < 90f) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.graphicsLayer { if (rot >= 90f && motion.effects3d) rotationY = 180f }.verticalScroll(rememberScrollState()))
                }
                Text("Tap the card to flip · box ${c.box}/5", style = MaterialTheme.typography.labelSmall)
                if (flipped) Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedButton(onClick = { vm.reviewCard(c, false); flipped = false }) { Text("Didn't know") }
                    Button(onClick = { vm.reviewCard(c, true); flipped = false }) { Text("Knew it") }
                }
            }
        } else item {
            GlassCard(glow = Brand.Violet) {
                Text("Spaced repetition (Leitner boxes)", style = MaterialTheme.typography.titleMedium)
                Text("Cards you know move to later boxes and come back less often. Due cards: ${due.size}.", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    Button(onClick = { studying = true; flipped = false }, enabled = due.isNotEmpty()) { Text("Study due cards") }
                    OutlinedButton(onClick = { vm.generateGlossaryCards(u.lessonsDone) }) { Text("Auto-create from completed lessons") }
                }
            }
        }
        item { Column {
            SectionTitle("Generate an offline revision set")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { nav.go("quiz/revision") }) { Text("Weak-area test") }
                OutlinedButton(onClick = { nav.go("quiz/daily") }) { Text("Daily quiz") }
                OutlinedButton(onClick = { nav.go("quiz/mock") }) { Text("Mock exam") }
            }
            if (u.bookmarks.isNotEmpty()) { Text("From your bookmarks:", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
                u.bookmarks.filter { it.route.startsWith("lesson/") }.take(6).forEach { b -> TextButton(onClick = { nav.go("quiz/lesson:${b.refId}") }) { Text("Quiz: ${b.title}") } } }
        } }
        item { Column {
            SectionTitle("Create a flashcard")
            OutlinedTextField(front, { front = it }, label = { Text("Front (question/term)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(back, { back = it }, label = { Text("Back (answer/definition)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Button(onClick = { vm.addCard(front, back); front = ""; back = "" }, enabled = front.isNotBlank() && back.isNotBlank()) { Text("Add flashcard") }
        } }
        item { SectionTitle("All flashcards") }
        items(u.cards, key = { it.id }) { c ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text(c.front, style = MaterialTheme.typography.titleSmall, maxLines = 1); Text("Box ${c.box} · ${c.source}", style = MaterialTheme.typography.labelSmall) }
                IconButton(onClick = { vm.deleteCard(c.id) }) { Icon(Icons.Filled.Delete, "Delete card") }
            }
        }
    }
    @Suppress("UNUSED_VARIABLE") val w = FontWeight.Bold
}
