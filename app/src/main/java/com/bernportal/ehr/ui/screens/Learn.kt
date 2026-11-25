@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.data.Lesson
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import com.bernportal.ehr.ui.theme.LocalSettings

// ======================= Roadmap =======================
@Composable
fun RoadmapScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val motion = LocalMotion.current
    val t = rememberInfiniteTransition(label = "road")
    val dash by t.animateFloat(0f, 40f, infiniteRepeatable(tween(1200, easing = LinearEasing)), label = "dash")
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            Text("Computer Fundamentals → Professional Ethical Hacker", style = MaterialTheme.typography.titleMedium, color = Brand.Gold)
            Text("Modules unlock progressively. Pass each module assessment (70%+) to unlock the next and earn its certificate.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 12.dp))
        }
        itemsIndexed(vm.content.modules) { i, m ->
            val unlocked = vm.isUnlocked(u, m)
            val passed = vm.modulePassed(u, m.id)
            val left = i % 2 == 0
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (left) Arrangement.Start else Arrangement.End) {
                    GlassCard(Modifier.fillMaxWidth(0.82f), glow = if (passed) Brand.Neon else if (unlocked) paletteFor(m.number).last() else Brand.Muted,
                        onClick = { nav.go("module/${m.id}") }, padding = 12.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Glossy3DIcon(iconFor(m.icon), if (unlocked) paletteFor(m.number) else listOf(Color(0xFF334155), Color(0xFF64748B)), 52.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Module ${m.number}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(m.title, style = MaterialTheme.typography.titleSmall)
                                Text("${m.lessons.size} lessons · ${vm.content.labsFor(m.id).size} labs · ${m.minutes} min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                LinearProgressIndicator({ vm.moduleProgress(u, m) }, Modifier.fillMaxWidth().padding(top = 6.dp).height(4.dp).clip(RoundedCornerShape(2.dp)))
                            }
                            Icon(if (passed) Icons.Filled.Verified else if (unlocked) Icons.Filled.LockOpen else Icons.Filled.Lock, null,
                                tint = if (passed) Brand.Neon else if (unlocked) Brand.Cyan else Brand.Muted)
                        }
                    }
                }
                if (i < vm.content.modules.lastIndex) {
                    Canvas(Modifier.fillMaxWidth().height(34.dp)) {
                        val x1 = if (left) size.width * 0.3f else size.width * 0.7f
                        val x2 = if (left) size.width * 0.7f else size.width * 0.3f
                        drawLine(Brush.horizontalGradient(listOf(Brand.Cyan, Brand.Violet)), Offset(x1, 0f), Offset(x2, size.height), 4f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), if (motion.enabled) -dash else 0f))
                    }
                }
            }
        }
        item {
            GlassCard(Modifier.fillMaxWidth().padding(top = 12.dp), glow = Brand.Gold, onClick = { nav.go("exams") }) {
                Text("Final destination: Professional Ethical Hacker", style = MaterialTheme.typography.titleMedium, color = Brand.Gold)
                Text("100-question final examination · Silverline capstone · Portfolio · Career centre", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// ======================= All courses =======================
@Composable
fun LearnScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    var tab by remember { mutableIntStateOf(0) }
    Column {
        PrimaryTabRow(selectedTabIndex = tab, containerColor = Color.Transparent) {
            listOf("Modules", "Roadmap", "Exams").forEachIndexed { i, t -> Tab(tab == i, { tab = i; if (i == 1) nav.go("roadmap"); if (i == 2) nav.go("exams") }, text = { Text(t) }) }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(vm.content.modules) { m ->
                val unlocked = vm.isUnlocked(u, m)
                GlassCard(Modifier.fillMaxWidth(), glow = paletteFor(m.number).last(), onClick = { nav.go("module/${m.id}") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Glossy3DIcon(iconFor(m.icon), if (unlocked) paletteFor(m.number) else listOf(Color(0xFF334155), Color(0xFF64748B)), 56.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("MODULE ${m.number}", style = MaterialTheme.typography.labelSmall, color = paletteFor(m.number).last())
                            Text(m.title, style = MaterialTheme.typography.titleMedium)
                            Text(m.intro, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        ProgressRing(vm.moduleProgress(u, m), 48.dp, 5.dp)
                    }
                    if (!unlocked) Text("Locked - pass Module ${m.number - 1}'s assessment to unlock (or take it now as a placement test).",
                        style = MaterialTheme.typography.labelSmall, color = Brand.Warn, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }
}

// ======================= Module =======================
@Composable
fun ModuleScreen(id: String) {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val m = vm.content.module(id) ?: return
    val unlocked = vm.isUnlocked(u, m)
    val best = vm.bestExam(u, m.id)
    val prev = vm.content.modules.getOrNull(m.number - 2)
    LaunchedEffect(id) { vm.markViewed(m.id, "Module ${m.number}: ${m.title}", "module/${m.id}") }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Breadcrumb(listOf("Learn", "Module ${m.number}"))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Glossy3DIcon(iconFor(m.icon), paletteFor(m.number), 72.dp)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Module ${m.number}", style = MaterialTheme.typography.labelLarge, color = paletteFor(m.number).last())
                    Text(m.title, style = MaterialTheme.typography.headlineSmall)
                    Text("${m.lessons.size} lessons · ${m.minutes} min · ${vm.content.labsFor(m.id).size} labs", style = MaterialTheme.typography.bodySmall)
                }
                ProgressRing(vm.moduleProgress(u, m), 60.dp)
            }
        }
        if (!unlocked && prev != null) item {
            GlassCard(glow = Brand.Warn) {
                Text("This module is locked", style = MaterialTheme.typography.titleSmall, color = Brand.Warn)
                Text("Demonstrate the foundations first: pass the Module ${prev.number} assessment with 70% or more. Already know it? Take that assessment now as a placement test.",
                    style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    Button(onClick = { nav.go("quiz/module:${prev.id}") }) { Text("Placement test") }
                    OutlinedButton(onClick = { nav.go("module/${prev.id}") }) { Text("Review Module ${prev.number}") }
                }
            }
        }
        item { Column {
            SectionTitle("Module introduction")
            Text(m.intro, style = MaterialTheme.typography.bodyLarge)
            SectionTitle("Learning objectives"); BulletList(m.objectives)
            SectionTitle("Prerequisites"); BulletList(m.prerequisites, Brand.Violet)
        } }
        item { SectionTitle("Lessons") }
        itemsIndexed(m.lessons) { i, l ->
            val done = l.id in u.lessonsDone
            GlassCard(Modifier.fillMaxWidth(), glow = if (done) Brand.Neon else MaterialTheme.colorScheme.outline,
                onClick = { if (unlocked) nav.go("lesson/${l.id}") }, padding = 12.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(36.dp).clip(CircleShape).background(if (done) Brand.Neon.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                        if (done) Icon(Icons.Filled.Check, null, tint = Brand.Neon) else Text("${i + 1}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(l.title, style = MaterialTheme.typography.titleSmall)
                        Text("${l.difficulty} · ${l.minutes} min · ${l.quiz.size} quiz questions", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(if (unlocked) Icons.Filled.ChevronRight else Icons.Filled.Lock, null)
                }
            }
        }
        item { Column {
            SectionTitle("Practical exercises"); BulletList(m.exercises, Brand.Neon)
            val labs = vm.content.labsFor(m.id)
            if (labs.isNotEmpty()) {
                SectionTitle("Lab activities")
                labs.forEach { lab ->
                    GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp), glow = Brand.Cyan, onClick = { nav.go("lab/${lab.id}") }, padding = 12.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (lab.isTerminal) Icons.Filled.Terminal else Icons.Filled.Psychology, null, tint = Brand.Cyan); Spacer(Modifier.width(10.dp))
                            Text(lab.title, Modifier.weight(1f)); Pill("L${lab.level}")
                            if (lab.id in u.labs) { Spacer(Modifier.width(6.dp)); Icon(Icons.Filled.CheckCircle, null, tint = Brand.Neon) }
                        }
                    }
                }
            }
            vm.content.project(m.project)?.let { p ->
                SectionTitle("Project work")
                GlassCard(Modifier.fillMaxWidth(), glow = Brand.Violet, onClick = { nav.go("project/${p.id}") }) {
                    Text("Project ${p.number}: ${p.title}", style = MaterialTheme.typography.titleSmall); Text(p.summary, style = MaterialTheme.typography.bodySmall)
                }
            }
            SectionTitle("Revision materials"); BulletList(m.revision, Brand.Gold)
            SectionTitle("Topics covered (glossary links)"); ChipRow(m.topics) { nav.go("search") }
            SectionTitle("Module completion criteria"); Text(m.completion, style = MaterialTheme.typography.bodyMedium)
        } }
        item {
            GlassCard(Modifier.fillMaxWidth(), glow = Brand.Gold) {
                Text("Final assessment", style = MaterialTheme.typography.titleMedium)
                Text("${vm.content.moduleQuestions(m.id).size} questions from all lessons and labs · pass mark 70%", style = MaterialTheme.typography.bodySmall)
                Text(if (best != null) "Best score: $best% ${if (best >= 70) "- PASSED" else ""}" else "Not attempted yet", color = if ((best ?: 0) >= 70) Brand.Neon else MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    Button(onClick = { nav.go("quiz/module:${m.id}") }) { Icon(Icons.Filled.Quiz, null); Spacer(Modifier.width(6.dp)); Text("Module exam") }
                    OutlinedButton(onClick = { nav.go("quiz/timed:${m.id}") }) { Icon(Icons.Filled.Timer, null); Spacer(Modifier.width(6.dp)); Text("Timed") }
                }
                if (u.certificates.any { it.refId == m.id }) TextButton(onClick = { nav.go("certificates") }) { Icon(Icons.Filled.WorkspacePremium, null); Text(" View your certificate") }
            }
        }
    }
}

// ======================= Lesson =======================
private val MODES = listOf("Read", "Animated", "Simulation", "Practice", "Quiz", "Lab", "Challenge", "Revision")

fun simFor(l: Lesson): String = when (l.id) {
    "m01l3" -> "convert"; "m02l1" -> "topology"; "m02l2", "m02l3" -> "subnet"; "m02l4" -> "osi"; "m02l5" -> "dns"; "m02l6" -> "routing"; "m02l7" -> "packet"
    "m05l1" -> "password"; "m05l2" -> "hash"; "m08l2", "m08l3", "m14l4" -> "cvss"; "m07l2", "m07l1", "m07l3" -> "ports"; "m04l6", "m09l3" -> "sqli"
    else -> when (l.module) { "m02" -> "osi"; "m07" -> "ports"; "m08" -> "cvss"; "m05" -> "hash"; else -> "steps" }
}

@Composable
fun LessonScreen(id: String) {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val l = vm.content.lesson(id) ?: return
    val m = vm.content.module(l.module)!!
    val settings = LocalSettings.current
    var mode by remember(id) { mutableIntStateOf(0) }
    var explainSheet by remember { mutableStateOf(false) }
    var explanation by remember(id) { mutableStateOf<Pair<String, String>?>(null) }
    val done = l.id in u.lessonsDone
    val bookmarked = u.bookmarks.any { it.refId == l.id }
    val next = vm.content.nextLesson(l.id)

    LaunchedEffect(id) { vm.markViewed(l.id, l.title, "lesson/${l.id}"); vm.tutorLesson.value = l.id }
    DisposableEffect(id) {
        val start = System.currentTimeMillis()
        onDispose { vm.addStudyMinutes(((System.currentTimeMillis() - start) / 60000).toInt()) }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Column {
                Breadcrumb(listOf("Learn", "Module ${m.number}", "Lesson ${l.order + 1} of ${m.lessons.size}"))
                Text(l.title, style = MaterialTheme.typography.headlineSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                    Pill(l.difficulty, Brand.Violet); Pill("${l.minutes} min", Brand.Cyan); if (done) Pill("Completed", Brand.Neon)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { vm.toggleBookmark(l.id, l.title, "Lesson", "lesson/${l.id}") }) {
                        Icon(if (bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder, "Bookmark", tint = Brand.Gold)
                    }
                }
                ScrollableTabRow(selectedTabIndex = mode, edgePadding = 0.dp, containerColor = Color.Transparent) {
                    MODES.forEachIndexed { i, t -> Tab(mode == i, { mode = i }, text = { Text(t) }) }
                }
            } }
            when (MODES[mode]) {
                "Read" -> item { ReadMode(l, settings.simpleLanguage) { mode = it } }
                "Animated" -> item { Column {
                    SectionTitle("Animated explanation")
                    FlowDiagram(l.flow)
                    Spacer(Modifier.height(12.dp))
                    GlassCard { Text(l.simple, style = MaterialTheme.typography.bodyLarge); Spacer(Modifier.height(8.dp)); Text("Analogy: ${l.analogy}", style = MaterialTheme.typography.bodyMedium, color = Brand.Gold) }
                } }
                "Simulation" -> item { Column {
                    SectionTitle("Interactive demonstration")
                    Text("Safe, fully simulated. Nothing runs on your device or network.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    SimContent(simFor(l), l)
                } }
                "Practice" -> item { PracticeMode(l) }
                "Quiz" -> item { Column {
                    SectionTitle("Knowledge check")
                    val best = u.attempts.filter { it.scope == "lesson:${l.id}" }.maxOfOrNull { it.pct }
                    Text("${l.quiz.size} questions with instant feedback. Score 70%+ to complete this lesson automatically.", style = MaterialTheme.typography.bodyMedium)
                    if (best != null) Text("Best score: $best%", color = if (best >= 70) Brand.Neon else Brand.Warn)
                    Button(onClick = { nav.go("quiz/lesson:${l.id}") }, modifier = Modifier.padding(top = 8.dp)) { Icon(Icons.Filled.Quiz, null); Spacer(Modifier.width(6.dp)); Text("Start lesson quiz") }
                } }
                "Lab" -> item { Column {
                    SectionTitle("Related labs")
                    val labs = vm.content.labsFor(l.module)
                    if (labs.isEmpty()) Text("No dedicated lab for this module - try the Practice Centre simulators.")
                    labs.forEach { lab ->
                        GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp), glow = Brand.Cyan, onClick = { nav.go("lab/${lab.id}") }, padding = 12.dp) {
                            Text(lab.title, style = MaterialTheme.typography.titleSmall); Text(lab.scenario, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                        }
                    }
                    OutlinedButton(onClick = { nav.go("practice") }) { Text("Open Practice Centre") }
                } }
                "Challenge" -> item { Column {
                    SectionTitle("Independent challenge")
                    GlassCard(glow = Brand.Violet) { Text(l.challenge, style = MaterialTheme.typography.bodyLarge) }
                    Spacer(Modifier.height(8.dp))
                    var answer by remember(id) { mutableStateOf(u.notes.firstOrNull { it.refId == "challenge:${l.id}" }?.text ?: "") }
                    OutlinedTextField(answer, { answer = it }, label = { Text("Your answer (saved to Notes)") }, modifier = Modifier.fillMaxWidth(), minLines = 4)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { vm.saveNote("challenge:${l.id}", "Challenge: ${l.title}", answer, u.notes.firstOrNull { it.refId == "challenge:${l.id}" }?.id ?: 0) }) { Text("Save") }
                        OutlinedButton(onClick = { vm.tutorDraft.value = "Please review my answer to this challenge and give hints, not the full solution.\nChallenge: ${l.challenge}\nMy answer: $answer"; nav.go("tutor") }) { Text("Ask AI to review") }
                    }
                    TextButton(onClick = { nav.go("labs?tab=2") }) { Text("More in the Challenge Arena →") }
                } }
                else -> item { Column {
                    SectionTitle("Summary"); GlassCard(glow = Brand.Gold) { Text(l.summary, style = MaterialTheme.typography.bodyLarge) }
                    SectionTitle("Key terminology"); ChipRow(l.terms) { t -> nav.go("glossary?term=${android.net.Uri.encode(t)}") }
                    SectionTitle("Exam explanation"); Text(l.examTip)
                    SectionTitle("Common mistakes"); BulletList(l.mistakes, Brand.Danger)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
                        Button(onClick = {
                            l.terms.forEach { t -> vm.content.term(t)?.let { g -> vm.addCard(g.term, g.definition, "auto") } }
                            vm.addCard("Summarise: ${l.title}", l.summary, "auto")
                        }) { Text("Create flashcards") }
                        OutlinedButton(onClick = { nav.go("revision") }) { Text("Revision centre") }
                    }
                } }
            }
            item {
                explanation?.let { (title, body) ->
                    GlassCard(glow = Brand.Neon) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AutoAwesome, null, tint = Brand.Neon); Spacer(Modifier.width(8.dp))
                            Text(title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                            IconButton(onClick = { explanation = null }) { Icon(Icons.Filled.Close, "Close") }
                        }
                        Text(body, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Button(onClick = { vm.completeLesson(l) }, enabled = !done, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.TaskAlt, null); Spacer(Modifier.width(6.dp)); Text(if (done) "Completed" else "Mark complete")
                    }
                    if (next != null) OutlinedButton(onClick = { nav.go("lesson/${next.id}") }, modifier = Modifier.weight(1f)) { Text("Next lesson"); Icon(Icons.Filled.ChevronRight, null) }
                    else OutlinedButton(onClick = { nav.go("exams") }, modifier = Modifier.weight(1f)) { Text("Final exam") }
                }
                if (next != null) Text("Next: ${next.title}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
        }
        ExtendedFloatingActionButton(
            onClick = { explainSheet = true }, icon = { Icon(Icons.Filled.Psychology, null) }, text = { Text("Explain this again") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), containerColor = Brand.Violet, contentColor = Color.White
        )
    }

    if (explainSheet) {
        ModalBottomSheet(onDismissRequest = { explainSheet = false }) {
            Column(Modifier.padding(16.dp).navigationBarsPadding()) {
                Text("Explain this again", style = MaterialTheme.typography.titleLarge)
                Text("Choose how you would like it explained.", style = MaterialTheme.typography.bodySmall)
                val opts = listOf(
                    "Simpler explanation" to l.simple,
                    "Real-world analogy" to l.analogy,
                    "Step-by-step explanation" to l.flow.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n") + "\n\nTry it:\n" + l.practice.joinToString("\n") { "• $it" },
                    "Technical explanation" to l.detailed,
                    "Visual explanation" to "",
                    "Exam explanation" to l.examTip + "\n\nSummary: " + l.summary,
                    "Practical example" to l.example + "\n\nWhy it matters for security: " + l.relevance
                )
                opts.forEachIndexed { i, (title, body) ->
                    ListItem(
                        headlineContent = { Text(title) },
                        leadingContent = { Glossy3DIcon(listOf(Icons.Filled.ChildCare, Icons.Filled.Lightbulb, Icons.Filled.FormatListNumbered, Icons.Filled.Memory, Icons.Filled.Animation, Icons.Filled.School, Icons.Filled.Handyman)[i], paletteFor(i), 36.dp) },
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable {
                            explainSheet = false
                            if (title == "Visual explanation") mode = 1 else explanation = title to body
                        }
                    )
                }
                OutlinedButton(onClick = { explainSheet = false; vm.tutorDraft.value = "Explain '${l.title}' in a different way, with a new example, for my level."; nav.go("tutor") }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.SmartToy, null); Spacer(Modifier.width(6.dp)); Text("Ask the AI tutor (online)")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReadMode(l: Lesson, simpleFirst: Boolean, switchMode: (Int) -> Unit) {
    val vm = LocalVm.current; val nav = LocalNav.current
    val m = vm.content.module(l.module)!!
    Column {
        SectionTitle("Why this topic matters"); Text(l.why, style = MaterialTheme.typography.bodyLarge)
        SectionTitle("Learning objectives"); BulletList(l.objectives)
        SectionTitle("Prerequisite knowledge")
        BulletList(m.prerequisites + listOfNotNull(m.lessons.getOrNull(l.order - 1)?.let { "Previous lesson: ${it.title}" }), Brand.Violet)
        val simple: @Composable () -> Unit = { SectionTitle("Simple explanation"); GlassCard(glow = Brand.Neon) { Text(l.simple, style = MaterialTheme.typography.bodyLarge) } }
        val detailed: @Composable () -> Unit = { SectionTitle("Detailed technical explanation"); GlassCard { Text(l.detailed, style = MaterialTheme.typography.bodyMedium) } }
        if (simpleFirst) { simple(); detailed() } else { detailed(); simple() }
        SectionTitle("Illustrated explanation")
        GlassCard {
            Row(Modifier.horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
                l.flow.forEachIndexed { i, s ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(96.dp)) {
                        Glossy3DIcon(Icons.Filled.Memory, paletteFor(i + 1), 44.dp); Text(s, style = MaterialTheme.typography.labelSmall, maxLines = 3)
                    }
                    if (i < l.flow.lastIndex) Icon(Icons.Filled.ArrowForward, null, tint = Brand.Cyan)
                }
            }
            TextButton(onClick = { switchMode(1) }) { Icon(Icons.Filled.PlayCircle, null); Text(" Watch the animated version") }
        }
        SectionTitle("Real-world example"); Text(l.example, style = MaterialTheme.typography.bodyLarge)
        SectionTitle("Cybersecurity relevance"); Text(l.relevance, style = MaterialTheme.typography.bodyLarge)
        SectionTitle("Interactive demonstration")
        OutlinedButton(onClick = { switchMode(2) }) { Icon(Icons.Filled.TouchApp, null); Text(" Open the simulation") }
        SectionTitle("Guided practice"); BulletList(l.practice, Brand.Neon)
        SectionTitle("Independent challenge"); GlassCard(glow = Brand.Violet) { Text(l.challenge) }
        SectionTitle("Knowledge check")
        l.quiz.firstOrNull()?.let { q -> InlineQuestion(q) }
        SectionTitle("Common mistakes"); BulletList(l.mistakes, Brand.Danger)
        SectionTitle("Summary"); GlassCard(glow = Brand.Gold) { Text(l.summary, style = MaterialTheme.typography.bodyLarge) }
        SectionTitle("Key terminology"); ChipRow(l.terms) { t -> nav.go("glossary?term=${android.net.Uri.encode(t)}") }
        SectionTitle("Notes for this lesson"); LessonNotes(l)
    }
}

@Composable
fun InlineQuestion(q: com.bernportal.ehr.data.QuizQ) {
    var chosen by remember(q) { mutableStateOf<Int?>(null) }
    GlassCard {
        Text(q.q, style = MaterialTheme.typography.titleSmall)
        q.options.forEachIndexed { i, o ->
            val correct = i in q.answer
            val color = when { chosen == null -> MaterialTheme.colorScheme.outline; correct -> Brand.Neon; chosen == i -> Brand.Danger; else -> MaterialTheme.colorScheme.outline }
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).clip(RoundedCornerShape(10.dp)).border(1.dp, color, RoundedCornerShape(10.dp))
                .clickable(enabled = chosen == null) { chosen = i }.padding(10.dp)) { Text(o) }
        }
        AnimatedVisibility(chosen != null) {
            Text((if (chosen in q.answer) "Correct. " else "Not quite. ") + q.explain, color = if (chosen in q.answer) Brand.Neon else Brand.Warn, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Composable
private fun LessonNotes(l: Lesson) {
    val vm = LocalVm.current
    val u by vm.user.collectAsStateWithLifecycle()
    val existing = u.notes.firstOrNull { it.refId == l.id }
    var text by remember(existing?.id) { mutableStateOf(existing?.text ?: "") }
    OutlinedTextField(text, { text = it }, placeholder = { Text("Write your notes - they stay on this device") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
    Row {
        Button(onClick = { vm.saveNote(l.id, l.title, text, existing?.id ?: 0) }) { Icon(Icons.Filled.Save, null); Text(" Save note") }
    }
}

@Composable
private fun PracticeMode(l: Lesson) {
    val vm = LocalVm.current
    val u by vm.user.collectAsStateWithLifecycle()
    val key = "practice:${l.id}"
    val saved = u.notes.firstOrNull { it.refId == key }
    val checked = remember(saved?.text) { mutableStateListOf<Int>().apply { saved?.text?.split(",")?.mapNotNull { it.toIntOrNull() }?.let { addAll(it) } } }
    Column {
        SectionTitle("Guided practice")
        l.practice.forEachIndexed { i, p ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {
                if (i in checked) checked.remove(i) else checked.add(i)
                vm.saveNote(key, "Practice progress: ${l.title}", checked.joinToString(","), saved?.id ?: 0)
            }) {
                Checkbox(i in checked, null); Text(p, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text("Exercises marked complete are saved automatically.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        SectionTitle("Common mistakes to avoid"); BulletList(l.mistakes, Brand.Danger)
        SectionTitle("Your notes"); LessonNotes(l)
    }
}

// ======================= Examination centre =======================
@Composable
fun ExamsScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val finalOk = vm.finalUnlocked(u)
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            GlassCard(glow = Brand.Gold) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Glossy3DIcon(Icons.Filled.WorkspacePremium, paletteFor(6), 56.dp); Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Final examination", style = MaterialTheme.typography.titleLarge)
                        Text("100 questions: objective, practical output analysis and lab scenarios across all 16 modules · 150 minutes · pass 70%", style = MaterialTheme.typography.bodySmall)
                        val best = u.attempts.filter { it.scope == "final" }.maxOfOrNull { it.pct }
                        if (best != null) Text("Best: $best%", color = if (best >= 70) Brand.Neon else Brand.Warn)
                    }
                }
                Button(onClick = { nav.go("quiz/final") }, enabled = finalOk, modifier = Modifier.padding(top = 8.dp)) { Text(if (finalOk) "Start final exam" else "Pass all 16 module assessments to unlock") }
            }
        }
        item { Column {
            SectionTitle("Practice exams")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExamTile("Mock certification exam", "50 questions · 60 min", Icons.Filled.Assignment, 3, Modifier.weight(1f)) { nav.go("quiz/mock") }
                ExamTile("Revision test", "Targets your weak areas", Icons.Filled.Replay, 1, Modifier.weight(1f)) { nav.go("quiz/revision") }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExamTile("Daily quiz", "5 quick questions", Icons.Filled.Today, 2, Modifier.weight(1f)) { nav.go("quiz/daily") }
                ExamTile("Capstone evaluation", "Silverline engagement", Icons.Filled.Apartment, 6, Modifier.weight(1f)) { nav.go("capstone") }
            }
        } }
        item { SectionTitle("Module examinations") }
        items(vm.content.modules) { m ->
            val best = vm.bestExam(u, m.id)
            GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, glow = if ((best ?: 0) >= 70) Brand.Neon else MaterialTheme.colorScheme.outline) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Glossy3DIcon(iconFor(m.icon), paletteFor(m.number), 40.dp); Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Module ${m.number}: ${m.title}", style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(best?.let { "Best $it%" } ?: "Not attempted", style = MaterialTheme.typography.labelSmall, color = if ((best ?: 0) >= 70) Brand.Neon else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { nav.go("quiz/module:${m.id}") }) { Icon(Icons.Filled.Quiz, "Module exam") }
                    IconButton(onClick = { nav.go("quiz/timed:${m.id}") }) { Icon(Icons.Filled.Timer, "Timed exam") }
                }
            }
        }
        item { SectionTitle("Assessment history") }
        if (u.attempts.isEmpty()) item { Text("No attempts yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(u.attempts.take(40)) { a ->
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(if (a.pct >= 70) Icons.Filled.CheckCircle else Icons.Filled.Cancel, null, tint = if (a.pct >= 70) Brand.Neon else Brand.Danger)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(a.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(com.bernportal.ehr.util.PdfBuilder.date(a.at), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${a.score}/${a.total} (${a.pct}%)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ExamTile(title: String, body: String, icon: androidx.compose.ui.graphics.vector.ImageVector, pal: Int, modifier: Modifier, onClick: () -> Unit) {
    GlassCard(modifier.height(120.dp), glow = paletteFor(pal).last(), onClick = onClick, padding = 12.dp) {
        Glossy3DIcon(icon, paletteFor(pal), 38.dp); Spacer(Modifier.height(6.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(body, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Suppress("unused") private val keepStroke = Stroke(1f)
@Suppress("unused") private fun Modifier.keepLayer() = this.graphicsLayer { }
