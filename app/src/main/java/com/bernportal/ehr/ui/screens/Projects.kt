@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.data.db.ProjectState
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import kotlinx.coroutines.delay

// ======================= Projects =======================
@Composable
fun ProjectList() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("14 core projects with grading rubrics. Completed projects appear in your Portfolio.", style = MaterialTheme.typography.bodyMedium) }
        items(vm.content.projects) { p ->
            val st = u.projects[p.id]
            val doneTasks = st?.checked?.split(",")?.count { it.isNotBlank() } ?: 0
            GlassCard(Modifier.fillMaxWidth(), glow = if (st?.completed == true) Brand.Neon else paletteFor(p.number).last(), onClick = { nav.go("project/${p.id}") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Glossy3DIcon(Icons.Filled.Construction, paletteFor(p.number), 48.dp); Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Project ${p.number}", style = MaterialTheme.typography.labelSmall, color = paletteFor(p.number).last())
                        Text(p.title, style = MaterialTheme.typography.titleSmall)
                        LinearProgressIndicator({ doneTasks.toFloat() / p.tasks.size }, Modifier.fillMaxWidth().padding(top = 6.dp).height(4.dp).clip(RoundedCornerShape(2.dp)))
                    }
                    if (st?.completed == true) Icon(Icons.Filled.Verified, null, tint = Brand.Neon)
                }
            }
        }
    }
}

@Composable
fun ProjectScreen(id: String) {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val p = vm.content.project(id) ?: return
    val st = u.projects[p.id] ?: ProjectState(p.id)
    val checked = st.checked.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    val rubric = st.rubric.split(",").mapNotNull { it.toIntOrNull() }.let { if (it.size == p.rubric.size) it else List(p.rubric.size) { 0 } }
    var notes by remember(id) { mutableStateOf(st.notes) }
    val avg = if (rubric.all { it > 0 }) rubric.average() else 0.0
    val safetyOk = rubric.firstOrNull()?.let { it >= 2 } ?: false
    val canComplete = checked.size == p.tasks.size && rubric.all { it > 0 } && avg >= 2.5 && safetyOk
    LaunchedEffect(id) { vm.markViewed(p.id, "Project ${p.number}: ${p.title}", "project/${p.id}") }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item { Column {
            Breadcrumb(listOf("Projects", "Project ${p.number}"))
            Text(p.title, style = MaterialTheme.typography.headlineSmall)
            Text(p.summary, style = MaterialTheme.typography.bodyLarge)
            SectionTitle("Objectives"); BulletList(p.objectives)
            SectionTitle("Tasks (${checked.size}/${p.tasks.size})")
        } }
        itemsIndexed(p.tasks) { i, t ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable {
                val n = if (i in checked) checked - i else checked + i
                vm.saveProject(st.copy(checked = n.sorted().joinToString(","), completed = false))
            }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(i in checked, null); Text(t, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item { Column {
            SectionTitle("Deliverable"); GlassCard(glow = Brand.Violet) { Text(p.deliverable) }
            if (p.labs.isNotEmpty()) {
                SectionTitle("Related labs")
                p.labs.forEach { lid -> vm.content.lab(lid)?.let { lab -> TextButton(onClick = { nav.go("lab/${lab.id}") }) { Icon(Icons.Filled.Science, null); Text(" ${lab.title}") } } }
            }
            SectionTitle("Project notes / evidence")
            OutlinedTextField(notes, { notes = it }, modifier = Modifier.fillMaxWidth(), minLines = 4, placeholder = { Text("Document your work here - findings, commands, screenshots references") })
            Button(onClick = { vm.saveProject(st.copy(notes = notes)) }) { Text("Save notes") }
            SectionTitle("Grading rubric - self assessment")
            Text("Rate yourself honestly (1 Insufficient - 4 Excellent). Pass = average 2.5+, and Safety & scope must not be Insufficient.", style = MaterialTheme.typography.bodySmall)
        } }
        itemsIndexed(p.rubric) { i, r ->
            GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, glow = if (i == 0) Brand.Warn else MaterialTheme.colorScheme.outline) {
                Text(r.criterion, style = MaterialTheme.typography.titleSmall)
                Text("Excellent: ${r.excellent}", style = MaterialTheme.typography.bodySmall, color = Brand.Neon)
                Text("Insufficient: ${r.insufficient}", style = MaterialTheme.typography.bodySmall, color = Brand.Danger)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
                    listOf("1", "2", "3", "4").forEachIndexed { li, lab ->
                        FilterChip(rubric[i] == li + 1, {
                            val n = rubric.toMutableList(); n[i] = li + 1
                            vm.saveProject(st.copy(rubric = n.joinToString(","), notes = notes, completed = false))
                        }, { Text(lab) })
                    }
                }
            }
        }
        item { Column {
            Spacer(Modifier.height(8.dp))
            if (st.completed) GlassCard(glow = Brand.Neon) { Text("Project completed and added to your portfolio (rubric average ${"%.1f".format(avg)}).", color = Brand.Neon) }
            else Button(onClick = { vm.saveProject(st.copy(notes = notes, completed = true)) }, enabled = canComplete, modifier = Modifier.fillMaxWidth()) {
                Text(if (canComplete) "Mark project complete" else "Complete all tasks and the rubric to finish")
            }
        } }
    }
}

// ======================= Challenge arena =======================
@Composable
fun ArenaList() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    var diff by remember { mutableStateOf<String?>(null) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Column {
            Text("Safe offline challenges with hints, optional timers, scoring and replay.", style = MaterialTheme.typography.bodyMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(diff == null, { diff = null }, { Text("All") })
                listOf("Easy", "Medium", "Hard", "Expert").forEach { d -> FilterChip(diff == d, { diff = d }, { Text(d) }) }
            }
            Text("Solved ${u.challenges.values.count { it.correct }} / ${vm.content.challenges.size}", color = Brand.Neon, style = MaterialTheme.typography.labelLarge)
        } }
        items(vm.content.challenges.filter { diff == null || it.difficulty == diff }) { c ->
            val r = u.challenges[c.id]
            val pal = when (c.difficulty) { "Easy" -> 2; "Medium" -> 1; "Hard" -> 6; else -> 7 }
            GlassCard(Modifier.fillMaxWidth(), glow = paletteFor(pal).last(), onClick = { nav.go("challenge/${c.id}") }, padding = 12.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Glossy3DIcon(Icons.Filled.SportsMartialArts, paletteFor(pal), 44.dp); Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text(c.title, style = MaterialTheme.typography.titleSmall); Text("${c.kind} · ${c.difficulty} · ${c.minutes} min", style = MaterialTheme.typography.labelSmall) }
                    if (r != null) Text("${r.score}", color = if (r.correct) Brand.Neon else Brand.Warn, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ChallengeScreen(id: String) {
    val vm = LocalVm.current; val nav = LocalNav.current
    val c = vm.content.challenge(id) ?: return
    var key by remember { mutableIntStateOf(0) }
    var timed by remember(key) { mutableStateOf(false) }
    var remaining by remember(key) { mutableIntStateOf(c.minutes * 60) }
    var hints by remember(key) { mutableIntStateOf(0) }
    var chosen by remember(key) { mutableStateOf<Int?>(null) }
    if (timed && chosen == null) LaunchedEffect(key, timed) { while (remaining > 0 && chosen == null) { delay(1000); remaining-- } }
    val timeUp = timed && remaining <= 0
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Column {
            Breadcrumb(listOf("Challenge Arena", c.difficulty))
            Text(c.title, style = MaterialTheme.typography.headlineSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Pill(c.kind); Spacer(Modifier.width(8.dp)); Pill(c.difficulty, Brand.Gold); Spacer(Modifier.weight(1f))
                Text("Timer"); Switch(timed, { timed = it }, enabled = chosen == null)
            }
            if (timed) Text("Time left: %d:%02d".format(remaining / 60, remaining % 60), color = if (remaining < 30) Brand.Danger else Brand.Cyan, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(c.prompt, style = MaterialTheme.typography.bodyLarge)
            if (c.artifact.isNotBlank()) { Spacer(Modifier.height(8.dp)); MonoBlock(c.artifact) }
            Spacer(Modifier.height(8.dp))
            Text(c.question, style = MaterialTheme.typography.titleMedium)
        } }
        itemsIndexed(c.options) { i, o ->
            val col = when { chosen == null -> MaterialTheme.colorScheme.outline; i == c.answer -> Brand.Neon; i == chosen -> Brand.Danger; else -> MaterialTheme.colorScheme.outline }
            Text(o, fontFamily = if (o.contains("\n") || o.contains("(")) androidx.compose.ui.text.font.FontFamily.Monospace else null,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(1.5.dp, col, RoundedCornerShape(14.dp))
                    .clickable(enabled = chosen == null && !timeUp) {
                        chosen = i
                        val ok = i == c.answer
                        val score = if (ok) (100 - hints * 20 + if (timed) remaining.coerceAtMost(60) / 3 else 0).coerceIn(10, 120) else 0
                        vm.saveChallenge(c.id, ok, score)
                    }.padding(14.dp))
        }
        item { Column {
            if (chosen == null && !timeUp) {
                TextButton(onClick = { if (hints < c.hints.size) hints++ }, enabled = hints < c.hints.size) { Icon(Icons.Filled.Lightbulb, null); Text(" Hint (${hints}/${c.hints.size}, -20 points each)") }
                c.hints.take(hints).forEach { Text("• $it", color = Brand.Gold) }
            }
            if (timeUp && chosen == null) Text("Time's up! Replay to try again.", color = Brand.Danger)
            if (chosen != null) {
                GlassCard(glow = if (chosen == c.answer) Brand.Neon else Brand.Warn) {
                    Text(if (chosen == c.answer) "Solved!" else "Incorrect", style = MaterialTheme.typography.titleMedium, color = if (chosen == c.answer) Brand.Neon else Brand.Warn)
                    Text(c.explain)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(onClick = { key++ }) { Icon(Icons.Filled.Replay, null); Text(" Replay") }
                val idx = vm.content.challenges.indexOf(c)
                vm.content.challenges.getOrNull(idx + 1)?.let { n -> Button(onClick = { nav.go("challenge/${n.id}") }) { Text("Next challenge") } }
            }
        } }
    }
}

// ======================= Capstone =======================
@Composable
fun CapstoneScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val cap = vm.content.capstone
    val answered = u.capstone
    val current = cap.stages.firstOrNull { it.n !in answered }
    var confirmReset by remember { mutableStateOf(false) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Glossy3DIcon(Icons.Filled.Apartment, paletteFor(6), 60.dp); Spacer(Modifier.width(12.dp))
                Column { Text("CAPSTONE", style = MaterialTheme.typography.labelMedium, color = Brand.Gold); Text(cap.title, style = MaterialTheme.typography.titleLarge) }
            }
            Text(cap.intro, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
            LinearProgressIndicator({ answered.size.toFloat() / cap.stages.size }, Modifier.fillMaxWidth().padding(vertical = 8.dp).height(6.dp).clip(RoundedCornerShape(3.dp)), color = Brand.Gold)
            Text("Stage ${answered.size.coerceAtMost(cap.stages.size)} of ${cap.stages.size} complete", style = MaterialTheme.typography.labelMedium)
        } }
        item { Column {
            var open by remember { mutableIntStateOf(-1) }
            listOf("Company profile" to cap.company, "Scope & rules of engagement" to cap.scope, "Asset inventory" to cap.assets).forEachIndexed { i, (t, b) ->
                GlassCard(Modifier.fillMaxWidth().padding(vertical = 3.dp), onClick = { open = if (open == i) -1 else i }, padding = 12.dp) {
                    Row { Text(t, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f)); Icon(if (open == i) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null) }
                    if (open == i) MonoBlock(b.replace(" | ", "\n").replace(". ", ".\n"), color = Color(0xFFE2E8F0))
                }
            }
        } }
        if (current != null) item { Column {
            SectionTitle("Stage ${current.n}: ${current.title}")
            Pill("Scored on: ${current.category}", Brand.Violet)
            Spacer(Modifier.height(6.dp))
            Text(current.brief, style = MaterialTheme.typography.bodyLarge)
            if (current.artifact == "SCOPE") MonoBlock(cap.scope) else if (current.artifact == "ASSETS") MonoBlock(cap.assets.replace(" | ", "\n")) else if (current.artifact.isNotBlank()) MonoBlock(current.artifact)
            Spacer(Modifier.height(6.dp))
            Text(current.question, style = MaterialTheme.typography.titleMedium)
            current.options.forEachIndexed { i, o ->
                Text(o, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(14.dp)).border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                    .clickable { vm.answerCapstone(current, i) }.padding(14.dp))
            }
        } } else item { CapstoneResults() }
        if (answered.isNotEmpty()) item { Column {
            SectionTitle("Engagement log")
            cap.stages.filter { it.n in answered }.forEach { st ->
                val a = answered[st.n]!!
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(if (a.correct) Icons.Filled.CheckCircle else Icons.Filled.Cancel, null, tint = if (a.correct) Brand.Neon else Brand.Danger)
                    Spacer(Modifier.width(8.dp))
                    Column { Text("${st.n}. ${st.title}", style = MaterialTheme.typography.titleSmall); Text(st.explain, style = MaterialTheme.typography.bodySmall) }
                }
            }
            TextButton(onClick = { confirmReset = true }) { Icon(Icons.Filled.RestartAlt, null); Text(" Restart capstone") }
        } }
    }
    if (confirmReset) AlertDialog(onDismissRequest = { confirmReset = false }, title = { Text("Restart the capstone?") },
        text = { Text("Your stage answers will be cleared. Certificates already earned are kept.") },
        confirmButton = { TextButton(onClick = { vm.resetCapstone(); confirmReset = false }) { Text("Restart") } },
        dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel") } })
    @Suppress("UNUSED_VARIABLE") val n = nav
}

@Composable
private fun CapstoneResults() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val all = u.capstone.values
    val pct = if (all.isEmpty()) 0 else all.count { it.correct } * 100 / all.size
    val cats = listOf("Accuracy", "Methodology", "Safety", "Scope compliance", "Technical reasoning", "Evidence quality", "Risk classification", "Remediation quality", "Report quality", "Professional communication")
    Column {
        GlassCard(glow = Brand.Gold) {
            Text("Engagement complete", style = MaterialTheme.typography.headlineSmall, color = Brand.Gold)
            Text("Overall score: $pct% ${if (pct >= 70) "- certificate issued" else "- 70% needed for the certificate"}")
            Text("A draft 'Silverline Enterprise Security Assessment' report with findings was created in the Report Builder, and a portfolio entry was added.", style = MaterialTheme.typography.bodySmall)
        }
        SectionTitle("Scoring by criterion")
        cats.forEach { c ->
            val inCat = all.filter { it.category == c }
            if (inCat.isNotEmpty()) {
                val p = inCat.count { it.correct } * 100 / inCat.size
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                    Text(c, Modifier.width(170.dp), style = MaterialTheme.typography.bodySmall)
                    LinearProgressIndicator({ p / 100f }, Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)), color = if (p >= 70) Brand.Neon else Brand.Warn)
                    Text(" $p%", Modifier.width(48.dp))
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            Button(onClick = { nav.go("reports") }) { Text("Open report") }
            OutlinedButton(onClick = { nav.go("certificates") }) { Text("Certificates") }
            OutlinedButton(onClick = { nav.go("portfolio") }) { Text("Portfolio") }
        }
    }
}
