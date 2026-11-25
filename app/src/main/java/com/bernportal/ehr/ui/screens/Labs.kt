@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.data.Lab
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.util.copyText

@Composable
fun LabsHub(initialTab: Int) {
    var tab by remember { mutableIntStateOf(initialTab) }
    Column {
        PrimaryTabRow(selectedTabIndex = tab, containerColor = Color.Transparent) {
            listOf("Labs", "Projects", "Arena", "Capstone").forEachIndexed { i, t -> Tab(tab == i, { tab = i }, text = { Text(t) }) }
        }
        when (tab) {
            0 -> LabList()
            1 -> ProjectList()
            2 -> ArenaList()
            else -> CapstoneScreen()
        }
    }
}

@Composable
private fun LabList() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    var level by remember { mutableStateOf<Int?>(null) }
    var cat by remember { mutableStateOf<String?>(null) }
    val cats = vm.content.labs.map { it.category }.distinct()
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Column {
            GlassCard(glow = Brand.Gold) {
                Text("Safe simulated labs", style = MaterialTheme.typography.titleMedium)
                Text("Every lab is a simulation using fictional organisations and isolated lab networks. Commands never run on your device and the labs cannot reach real systems.",
                    style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Text("Difficulty", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(level == null, { level = null }, { Text("All") })
                listOf("1 Beginner", "2 Foundation", "3 Intermediate", "4 Advanced", "5 Professional").forEachIndexed { i, t -> FilterChip(level == i + 1, { level = i + 1 }, { Text(t) }) }
            }
            Text("Category", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(cat == null, { cat = null }, { Text("All") })
                cats.forEach { c -> FilterChip(cat == c, { cat = c }, { Text(c) }) }
            }
            Text("${u.labs.size} of ${vm.content.labs.size} labs completed", style = MaterialTheme.typography.labelMedium, color = Brand.Neon)
        } }
        items(vm.content.labs.filter { (level == null || it.level == level) && (cat == null || it.category == cat) }) { lab ->
            val res = u.labs[lab.id]
            GlassCard(Modifier.fillMaxWidth(), glow = if (res != null) Brand.Neon else paletteFor(lab.level + 1).last(), onClick = { nav.go("lab/${lab.id}") }, padding = 12.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Glossy3DIcon(if (lab.isTerminal) Icons.Filled.Terminal else Icons.Filled.Psychology, paletteFor(lab.level + 1), 48.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(lab.title, style = MaterialTheme.typography.titleSmall)
                        Text("${lab.category} · Level ${lab.level} ${lab.levelName} · ${lab.minutes} min", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row { repeat(5) { i -> Icon(if (i < lab.level) Icons.Filled.Star else Icons.Filled.StarBorder, null, Modifier.size(14.dp), tint = Brand.Gold) } }
                    }
                    if (res != null) Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.MilitaryTech, null, tint = Brand.Gold); Text("${res.score}%", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

// ======================= Lab runner =======================
@Composable
fun LabScreen(id: String) {
    val vm = LocalVm.current
    val lab = vm.content.lab(id) ?: return
    var stage by remember(id) { mutableIntStateOf(0) } // 0 brief, 1 run, 2 debrief
    var hints by remember(id) { mutableIntStateOf(0) }
    var taskScore by remember(id) { mutableFloatStateOf(0f) }
    var transcript by remember(id) { mutableStateOf("") }
    val start = remember(id) { System.currentTimeMillis() }
    LaunchedEffect(id) { vm.markViewed(lab.id, lab.title, "lab/${lab.id}") }
    when (stage) {
        0 -> LabBrief(lab) { stage = 1 }
        1 -> if (lab.isTerminal) TerminalLab(lab, onHint = { hints++ }) { score, log -> taskScore = score; transcript = log; stage = 2 }
             else DecisionLab(lab) { score, log -> taskScore = score; transcript = log; stage = 2 }
        else -> LabDebrief(lab, taskScore, hints, ((System.currentTimeMillis() - start) / 60000).toInt(), transcript)
    }
}

@Composable
private fun LabBrief(lab: Lab, onStart: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item { Column {
            Breadcrumb(listOf("Labs", lab.category, "Level ${lab.level}"))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Glossy3DIcon(if (lab.isTerminal) Icons.Filled.Terminal else Icons.Filled.Psychology, paletteFor(lab.level + 1), 64.dp)
                Spacer(Modifier.width(12.dp))
                Column { Text(lab.title, style = MaterialTheme.typography.headlineSmall); Text("${lab.levelName} · about ${lab.minutes} min", style = MaterialTheme.typography.bodySmall) }
            }
            SectionTitle("Story / scenario"); Text(lab.scenario, style = MaterialTheme.typography.bodyLarge)
            SectionTitle("Background"); Text(lab.background)
            SectionTitle("Learning objectives"); BulletList(lab.objectives)
            SectionTitle("Lab rules"); BulletList(lab.rules, Brand.Warn)
            SectionTitle("Target environment"); Text(lab.environment)
            SectionTitle("Required tools"); ChipRow(lab.tools)
            if (lab.isTerminal) { SectionTitle("Tasks & checkpoints"); BulletList(lab.tasks.map { it.text }, Brand.Neon) }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(52.dp)) { Icon(Icons.Filled.PlayArrow, null); Text(" I accept the lab rules - start") }
        } }
    }
}

private data class Line(val text: String, val kind: Int) // 0 cmd,1 out,2 sys

@Composable
private fun TerminalLab(lab: Lab, onHint: () -> Unit, onFinish: (Float, String) -> Unit) {
    val ctx = LocalContext.current
    val lines = remember { mutableStateListOf(Line("SIMULATED TERMINAL - ${lab.environment}. Type 'help' for built-in commands. Nothing runs on your device.", 2)) }
    val done = remember { mutableStateListOf<String>() }
    var input by remember { mutableStateOf("") }
    var showTasks by remember { mutableStateOf(true) }
    var hintShown by remember { mutableStateOf<String?>(null) }
    val history = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()
    LaunchedEffect(lines.size) { if (lines.isNotEmpty()) listState.animateScrollToItem(lines.lastIndex) }

    fun run(cmd: String) {
        val c = cmd.trim()
        if (c.isEmpty()) return
        history.add(c)
        lines.add(Line("${lab.prompt} $c", 0))
        when (c) {
            "help" -> lines.add(Line("Built-in: help, clear, tasks, hint. Other commands are interpreted by the lab simulation. Use the task list for guidance.", 2))
            "clear" -> { lines.clear(); return }
            "tasks" -> lines.add(Line(lab.tasks.joinToString("\n") { (if (it.id in done) "[x] " else "[ ] ") + it.text }, 2))
            "hint" -> { val next = lab.tasks.firstOrNull { it.id !in done }; hintShown = next?.hint; onHint(); lines.add(Line("Hint: ${next?.hint ?: "All tasks complete!"}", 2)) }
            else -> {
                val hit = lab.commands.firstOrNull { it.match.containsMatchIn(c) }
                if (hit == null) lines.add(Line("Command not recognised in this simulation. Check the task list or type 'hint'.", 2))
                else {
                    lines.add(Line(hit.output, 1))
                    val t = hit.task
                    if (t != null && t !in done) { done.add(t); lines.add(Line("✔ Checkpoint reached: ${lab.tasks.firstOrNull { it.id == t }?.text}", 2)) }
                }
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Pill("SIMULATION", Brand.Warn); Spacer(Modifier.width(8.dp))
            Text("${done.size}/${lab.tasks.size} checkpoints", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            IconButton(onClick = { showTasks = !showTasks }) { Icon(Icons.Filled.Checklist, "Toggle tasks") }
            IconButton(onClick = { copyText(ctx, lines.joinToString("\n") { it.text }) }) { Icon(Icons.Filled.ContentCopy, "Copy output") }
        }
        LinearProgressIndicator({ done.size.toFloat() / lab.tasks.size }, Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)), color = Brand.Neon)
        AnimatedVisibility(showTasks) {
            GlassCard(Modifier.fillMaxWidth().padding(vertical = 6.dp), padding = 10.dp) {
                lab.tasks.forEach { t ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (t.id in done) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked, null, Modifier.size(18.dp), tint = if (t.id in done) Brand.Neon else Brand.Muted)
                        Spacer(Modifier.width(6.dp)); Text(t.text, style = MaterialTheme.typography.bodySmall)
                    }
                }
                hintShown?.let { Text("Hint: $it", style = MaterialTheme.typography.bodySmall, color = Brand.Gold, modifier = Modifier.padding(top = 4.dp)) }
            }
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF02060F))
            .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(12.dp)).padding(10.dp)) {
            items(lines) { ln ->
                Text(ln.text, fontFamily = FontFamily.Monospace, fontSize = 12.5.sp, lineHeight = 17.sp,
                    color = when (ln.kind) { 0 -> Color(0xFF7DD3FC); 1 -> Color(0xFFB9F6CA); else -> Color(0xFFFCD34D) },
                    modifier = Modifier.padding(vertical = 2.dp).horizontalScroll(rememberScrollState()))
            }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AssistChip({ run("hint") }, { Text("hint") }, leadingIcon = { Icon(Icons.Filled.Lightbulb, null, Modifier.size(16.dp)) })
            AssistChip({ run("tasks") }, { Text("tasks") })
            history.takeLast(4).reversed().forEach { h -> AssistChip({ input = h }, { Text(h.take(22), maxLines = 1) }) }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                input, { input = it }, modifier = Modifier.weight(1f), singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                prefix = { Text("$ ", fontFamily = FontFamily.Monospace, color = Brand.Cyan) }, placeholder = { Text("type a command") },
                keyboardOptions = KeyboardOptions(autoCorrectEnabled = false, imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { run(input); input = "" })
            )
            IconButton(onClick = { run(input); input = "" }) { Icon(Icons.AutoMirrored.Filled.Send, "Run") }
        }
        Button(onClick = { onFinish(done.size.toFloat() / lab.tasks.size, lines.joinToString("\n") { it.text }) }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (done.size == lab.tasks.size) Brand.Neon else MaterialTheme.colorScheme.primary, contentColor = Color.Black)) {
            Text(if (done.size == lab.tasks.size) "All checkpoints done - continue to debrief" else "Finish lab (${done.size}/${lab.tasks.size})")
        }
    }
}

@Composable
private fun DecisionLab(lab: Lab, onFinish: (Float, String) -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var chosen by remember { mutableStateOf<Int?>(null) }
    var correct by remember { mutableIntStateOf(0) }
    val log = remember { StringBuilder() }
    val s = lab.steps[step]
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Column {
            Pill("Step ${step + 1} of ${lab.steps.size}", Brand.Cyan)
            LinearProgressIndicator({ (step + 1f) / lab.steps.size }, Modifier.fillMaxWidth().padding(vertical = 8.dp))
            Text(s.text, style = MaterialTheme.typography.bodyLarge)
            if (s.artifact.isNotBlank()) { Spacer(Modifier.height(8.dp)); MonoBlock(s.artifact) }
            Spacer(Modifier.height(8.dp))
            Text(s.question, style = MaterialTheme.typography.titleMedium)
        } }
        itemsIndexed(s.options) { i, o ->
            val col = when { chosen == null -> MaterialTheme.colorScheme.outline; i == s.answer -> Brand.Neon; i == chosen -> Brand.Danger; else -> MaterialTheme.colorScheme.outline }
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(1.5.dp, col, RoundedCornerShape(14.dp))
                .clickable(enabled = chosen == null) { chosen = i; if (i == s.answer) correct++; log.append("Step ${step + 1}: ${s.question} -> ${o} (${if (i == s.answer) "correct" else "incorrect"})\n") }
                .padding(14.dp)) { Text(o) }
        }
        if (chosen != null) item { Column {
            GlassCard(glow = if (chosen == s.answer) Brand.Neon else Brand.Warn) {
                Text(if (chosen == s.answer) "Correct" else "Not the best choice", color = if (chosen == s.answer) Brand.Neon else Brand.Warn, style = MaterialTheme.typography.titleSmall)
                Text(s.explain)
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                if (step < lab.steps.lastIndex) { step++; chosen = null } else onFinish(correct.toFloat() / lab.steps.size, log.toString())
            }, modifier = Modifier.fillMaxWidth()) { Text(if (step < lab.steps.lastIndex) "Next step" else "Continue to debrief") }
        } }
    }
}

@Composable
private fun LabDebrief(lab: Lab, taskScore: Float, hints: Int, minutes: Int, transcript: String) {
    val vm = LocalVm.current; val nav = LocalNav.current
    var answers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var submitted by remember { mutableStateOf(false) }
    var reflections by remember { mutableStateOf(List(lab.reflection.size) { "" }) }
    val kScore = if (lab.questions.isEmpty()) 1f else lab.questions.indices.count { answers[it] in lab.questions[it].answer }.toFloat() / lab.questions.size
    val score = ((taskScore * 60 + kScore * 40) - hints * 3).toInt().coerceIn(0, 100)
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Column {
            Text("Debrief: ${lab.title}", style = MaterialTheme.typography.headlineSmall)
            Text("Task completion ${(taskScore * 100).toInt()}% · hints used $hints", style = MaterialTheme.typography.bodyMedium)
            SectionTitle("Knowledge test")
        } }
        itemsIndexed(lab.questions) { qi, q ->
            GlassCard {
                Text(q.q, style = MaterialTheme.typography.titleSmall)
                q.options.forEachIndexed { oi, o ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable(enabled = !submitted) { answers = answers + (qi to oi) }) {
                        RadioButton(answers[qi] == oi, null); Text(o, color = if (submitted && oi in q.answer) Brand.Neon else MaterialTheme.colorScheme.onSurface)
                    }
                }
                if (submitted) Text(q.explain, style = MaterialTheme.typography.bodySmall, color = Brand.Gold)
            }
        }
        item { Column {
            SectionTitle("Reflection questions")
            lab.reflection.forEachIndexed { i, r ->
                Text(r, style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(reflections[i], { v -> reflections = reflections.toMutableList().also { it[i] = v } }, modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), minLines = 2)
            }
            if (!submitted) Button(onClick = {
                submitted = true
                vm.completeLab(lab, score, hints, minutes.coerceAtLeast(1), transcript + "\n\nReflection:\n" + lab.reflection.zip(reflections).joinToString("\n") { "Q: ${it.first}\nA: ${it.second}" })
            }, modifier = Modifier.fillMaxWidth()) { Text("Submit and see solution") }
        } }
        if (submitted) item { Column {
            GlassCard(glow = Brand.Gold) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Medal(lab.badge, true, 1f, 72.dp); Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Lab complete - score $score%", style = MaterialTheme.typography.titleLarge, color = Brand.Gold)
                        Text("Badge earned: ${lab.badge}. Your transcript was saved to Notes as lab evidence for the Report Builder.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            SectionTitle("Solution explanation"); Text(lab.solution, style = MaterialTheme.typography.bodyLarge)
            SectionTitle("Defensive lesson"); GlassCard(glow = Brand.Neon) { Text(lab.defensive) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
                OutlinedButton(onClick = { nav.go("labs?tab=0") }) { Text("Lab centre") }
                Button(onClick = { nav.go("reports") }) { Text("Write it up") }
            }
        } }
    }
}

@Suppress("unused") private val keepOverflow = TextOverflow.Ellipsis
@Suppress("unused") private val keepWeight = FontWeight.Bold
