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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.data.InterviewQ
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.util.PdfBuilder
import com.bernportal.ehr.util.openUrl
import com.bernportal.ehr.util.rememberFileSaver
import kotlinx.coroutines.delay
import org.json.JSONObject

@Composable
fun CareerScreen() {
    var tab by remember { mutableIntStateOf(0) }
    Column {
        ScrollableTabRow(tab, edgePadding = 0.dp, containerColor = Color.Transparent) {
            listOf("Career paths", "Certifications", "Interview", "CV builder", "Readiness").forEachIndexed { i, t -> Tab(tab == i, { tab = i }, text = { Text(t) }) }
        }
        when (tab) { 0 -> PathsTab(); 1 -> CertsTab(); 2 -> InterviewTab(); 3 -> CvTab(); else -> ReadinessTab() }
    }
}

@Composable
private fun PathsTab() {
    val vm = LocalVm.current
    val u by vm.user.collectAsStateWithLifecycle()
    val s by vm.settings.collectAsStateWithLifecycle()
    val compare = remember { mutableStateListOf<String>() }
    val paths = vm.content.career.paths
    fun match(mods: List<String>) = mods.mapNotNull { vm.content.module(it) }.map { vm.moduleProgress(u, it) }.average().toFloat()
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Tap 'Compare' on two roles for a side-by-side view. Match % reflects your progress in each role's key modules.", style = MaterialTheme.typography.bodySmall) }
        if (compare.size == 2) item {
            val a = paths.first { it.title == compare[0] }; val b = paths.first { it.title == compare[1] }
            GlassCard(glow = Brand.Gold) {
                Text("Role comparison", style = MaterialTheme.typography.titleMedium)
                listOf("Role" to (a.title to b.title), "Entry level" to (a.entry to b.entry), "Key skills" to (a.skills.joinToString() to b.skills.joinToString()),
                    "Your match" to ("${(match(a.modules) * 100).toInt()}%" to "${(match(b.modules) * 100).toInt()}%")).forEach { (k, v) ->
                    Row(Modifier.padding(vertical = 3.dp)) {
                        Text(k, Modifier.width(90.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(v.first, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall); Text(v.second, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    }
                }
                TextButton(onClick = { compare.clear() }) { Text("Clear comparison") }
            }
        }
        items(paths) { p ->
            val m = match(p.modules)
            GlassCard(Modifier.fillMaxWidth(), glow = if (s?.targetRole == p.title) Brand.Gold else MaterialTheme.colorScheme.outline, padding = 12.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressRing(m, 54.dp, 6.dp); Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.title, style = MaterialTheme.typography.titleSmall); Text(p.summary, style = MaterialTheme.typography.bodySmall)
                        Text("Entry: ${p.entry} · Modules: ${p.modules.joinToString { it.removePrefix("m").trimStart('0') }}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                ChipRow(p.skills)
                Row {
                    TextButton(onClick = { vm.updateSettings { it.copy(targetRole = p.title) } }) { Text(if (s?.targetRole == p.title) "Target role ✓" else "Set as target") }
                    TextButton(onClick = { if (p.title in compare) compare.remove(p.title) else { if (compare.size == 2) compare.removeAt(0); compare.add(p.title) } }) { Text(if (p.title in compare) "Comparing" else "Compare") }
                }
            }
        }
    }
}

@Composable
private fun CertsTab() {
    val vm = LocalVm.current; val ctx = LocalContext.current
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            GlassCard(glow = Brand.Warn) {
                Text("Informational only", style = MaterialTheme.typography.titleSmall, color = Brand.Warn)
                Text("Certification details, prerequisites, exam formats and prices change. Always verify on the provider's official website before booking. This app is not affiliated with, endorsed by or approved by any certification body.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        items(vm.content.career.certs) { c ->
            GlassCard(Modifier.fillMaxWidth(), padding = 12.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Glossy3DIcon(Icons.Filled.WorkspacePremium, paletteFor(c.name.length), 44.dp); Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text(c.name, style = MaterialTheme.typography.titleSmall); Text("${c.provider} · ${c.level}", style = MaterialTheme.typography.labelSmall) }
                }
                Text("Format: ${c.format}", style = MaterialTheme.typography.bodySmall)
                Text("Maps to modules: " + c.modules.mapNotNull { vm.content.module(it) }.joinToString { "${it.number}. ${it.title}" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = { openUrl(ctx, c.url) }) { Icon(Icons.Filled.OpenInNew, null); Text(" Verify on provider site (opens browser)") }
            }
        }
    }
}

@Composable
private fun InterviewTab() {
    val vm = LocalVm.current
    val all = vm.content.career.interview
    var session by remember { mutableStateOf<List<InterviewQ>?>(null) }
    var idx by remember { mutableIntStateOf(0) }
    var answer by remember { mutableStateOf("") }
    var reveal by remember { mutableStateOf(false) }
    var remaining by remember { mutableIntStateOf(120) }
    val ratings = remember { mutableStateListOf<Int>() }
    val ss = session
    if (ss != null && !reveal && idx < ss.size) LaunchedEffect(idx) { remaining = 120; while (remaining > 0 && !reveal) { delay(1000); remaining-- } }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (ss == null) {
            item {
                GlassCard(glow = Brand.Violet) {
                    Text("Interview simulator", style = MaterialTheme.typography.titleMedium)
                    Text("5 random technical and behavioural questions, 2 minutes each. Answer in your own words, then compare with a model answer and rate yourself.", style = MaterialTheme.typography.bodySmall)
                    Button(onClick = { session = all.shuffled().take(5); idx = 0; ratings.clear(); answer = ""; reveal = false }, modifier = Modifier.padding(top = 8.dp)) { Text("Start practice interview") }
                }
            }
            item { SectionTitle("Question bank (${all.size})") }
            items(all) { q ->
                var open by remember { mutableStateOf(false) }
                GlassCard(Modifier.fillMaxWidth(), onClick = { open = !open }, padding = 12.dp) {
                    Text(q.q, style = MaterialTheme.typography.titleSmall)
                    if (open) Text(q.a, style = MaterialTheme.typography.bodySmall, color = Brand.Neon)
                }
            }
        } else if (idx < ss.size) item { Column {
            Row { Pill("Question ${idx + 1} of ${ss.size}"); Spacer(Modifier.weight(1f)); Pill("%d:%02d".format(remaining / 60, remaining % 60), if (remaining < 20) Brand.Danger else Brand.Cyan) }
            Spacer(Modifier.height(8.dp))
            Text(ss[idx].q, style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(answer, { answer = it }, label = { Text("Your answer") }, modifier = Modifier.fillMaxWidth(), minLines = 5, enabled = !reveal)
            if (!reveal) Button(onClick = { reveal = true }) { Text("Reveal model answer") }
            else {
                GlassCard(glow = Brand.Neon) { Text("Model answer", style = MaterialTheme.typography.titleSmall, color = Brand.Neon); Text(ss[idx].a) }
                Text("How did you do?", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Missed" to 0, "Partly" to 1, "Nailed it" to 2).forEach { (l, v) ->
                        OutlinedButton(onClick = { ratings.add(v); idx++; answer = ""; reveal = false }) { Text(l) }
                    }
                }
            }
        } } else item {
            LaunchedEffect(Unit) { vm.updateSettings { it.copy(interviewSessions = it.interviewSessions + 1) } }
            GlassCard(glow = Brand.Gold) {
                Text("Practice interview complete", style = MaterialTheme.typography.titleLarge)
                Text("Self-rating: ${ratings.sum()} / ${ratings.size * 2}")
                Text("Tip: use STAR for behavioural answers; for technical answers define, explain, relate to security, give an example and a mitigation.", style = MaterialTheme.typography.bodySmall)
                Button(onClick = { session = null }) { Text("Back to question bank") }
            }
        }
    }
}

private val CV_FIELDS = listOf("name" to "Full name", "headline" to "Headline", "email" to "Email", "phone" to "Phone", "location" to "Location (town, country)",
    "linkedin" to "LinkedIn URL", "github" to "GitHub URL", "profile" to "Professional profile (3-4 lines)", "skills" to "Key skills (comma separated)",
    "experience" to "Experience (one achievement per line: action + tool + result)", "education" to "Education", "certs" to "Other certifications")

@Composable
private fun CvTab() {
    val vm = LocalVm.current; val ctx = LocalContext.current
    val s by vm.settings.collectAsStateWithLifecycle()
    val u by vm.user.collectAsStateWithLifecycle()
    val saver = rememberFileSaver("application/pdf")
    val initial = remember { try { JSONObject(s?.cvJson?.ifBlank { "{}" } ?: "{}") } catch (e: Exception) { JSONObject() } }
    val values = remember { mutableStateMapOf<String, String>().apply { CV_FIELDS.forEach { (k, _) -> put(k, initial.optString(k, if (k == "name") s?.name ?: "" else "")) } } }
    fun save() { val o = JSONObject(); values.forEach { (k, v) -> o.put(k, v) }; vm.updateSettings { it.copy(cvJson = o.toString()) } }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Your CV is stored only on this device. Completed projects and in-app certificates are added automatically.", style = MaterialTheme.typography.bodySmall) }
        items(CV_FIELDS) { (k, label) ->
            OutlinedTextField(values[k] ?: "", { values[k] = it }, label = { Text(label) }, modifier = Modifier.fillMaxWidth(),
                minLines = if (k in listOf("profile", "experience", "education")) 3 else 1)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { save() }) { Icon(Icons.Filled.Save, null); Text(" Save") }
                OutlinedButton(onClick = {
                    save()
                    saver.launch("CV-${(values["name"] ?: "learner").replace(" ", "_")}.pdf") {
                        val pdf = PdfBuilder(ctx, "Curriculum Vitae")
                        pdf.title(values["name"].orEmpty().ifBlank { "Your Name" }, values["headline"].orEmpty())
                            .text(listOf(values["email"], values["phone"], values["location"]).filter { !it.isNullOrBlank() }.joinToString("  ·  "))
                            .text(listOf(values["linkedin"], values["github"]).filter { !it.isNullOrBlank() }.joinToString("  ·  "))
                            .heading("Profile").text(values["profile"].orEmpty())
                            .heading("Key skills").text(values["skills"].orEmpty())
                            .heading("Experience").bullet(values["experience"].orEmpty().lines().filter { it.isNotBlank() })
                            .heading("Projects").bullet(vm.content.projects.filter { u.projects[it.id]?.completed == true }.map { "${it.title} - ${it.summary}" }.ifEmpty { listOf("Hands-on lab portfolio in progress") })
                            .heading("Education").text(values["education"].orEmpty())
                            .heading("Certifications & training")
                            .bullet(values["certs"].orEmpty().split(",").map { it.trim() }.filter { it.isNotBlank() } + u.certificates.map { "${it.title} (in-app curriculum certificate)" })
                            .build()
                    }
                }) { Icon(Icons.Filled.PictureAsPdf, null); Text(" Export PDF") }
            }
        }
        item { Column {
            SectionTitle("LinkedIn preparation checklist")
            BulletList(listOf("Professional photo and banner", "Headline with your target role and key skills", "About section telling your story and goals",
                "Featured: portfolio PDF and capstone report (fictional client)", "Skills section matching job adverts", "Experience written as achievements",
                "Follow security communities and share learnings responsibly (no live solutions)"), Brand.Cyan)
        } }
    }
}

@Composable
private fun ReadinessTab() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val s by vm.settings.collectAsStateWithLifecycle()
    val score = vm.careerReadiness(u)
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            GlassCard(glow = Brand.Gold) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressRing(score / 100f, 100.dp, 10.dp, Brand.Gold); Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Job readiness score", style = MaterialTheme.typography.titleLarge)
                        Text("Target role: ${s?.targetRole}", color = Brand.Gold)
                        Text("Weighted: course 40%, labs 20%, projects 25%, final exam 10%, CV & interview practice 5%.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item { Column {
            SectionTitle("Readiness checklist")
            val checks = listOf(
                "Pass Modules 1-5 (foundations)" to (1..5).all { vm.modulePassed(u, "m%02d".format(it)) },
                "Complete 10+ labs" to (u.labs.size >= 10), "Complete 5+ projects" to (u.projects.values.count { it.completed } >= 5),
                "Finish the Silverline capstone" to (u.capstone.size >= vm.content.capstone.stages.size),
                "Pass the final examination" to u.attempts.any { it.scope == "final" && it.pct >= 70 },
                "Build your CV" to (s?.cvJson?.isNotBlank() == true), "Complete an interview simulation" to ((s?.interviewSessions ?: 0) > 0),
                "Export a professional report" to u.reports.isNotEmpty()
            )
            checks.forEach { (t, ok) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                    Icon(if (ok) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked, null, tint = if (ok) Brand.Neon else Brand.Muted); Spacer(Modifier.width(8.dp)); Text(t)
                }
            }
        } }
        item { Column {
            SectionTitle("Career progression planner")
            val stages = listOf("Foundations (IT support / networking)" to 0.25f, "Entry security role (SOC / vulnerability analyst)" to 0.5f,
                "Specialise (${s?.targetRole})" to 0.75f, "Senior / lead" to 0.9f, "Architect / manager / principal" to 1f)
            val cur = vm.courseCompletion(u)
            stages.forEachIndexed { i, (t, th) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(Modifier.size(28.dp).clip(CircleShape).background(if (cur >= th * 0.8f) Brand.Neon else MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) { Text("${i + 1}", fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(10.dp)); Text(t)
                }
            }
            SectionTitle("Portfolio project tracker")
            Text("${u.projects.values.count { it.completed }} of 14 projects complete", style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator({ u.projects.values.count { it.completed } / 14f }, Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(onClick = { nav.go("portfolio") }) { Text("Portfolio") }
                OutlinedButton(onClick = { nav.go("labs?tab=1") }) { Text("Projects") }
            }
        } }
    }
}
