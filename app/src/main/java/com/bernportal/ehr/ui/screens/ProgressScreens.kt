@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.R
import com.bernportal.ehr.data.db.Certificate
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import com.bernportal.ehr.util.PdfBuilder
import com.bernportal.ehr.util.rememberFileSaver
import com.bernportal.ehr.util.shareText

@Composable
fun ProgressScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val xp = vm.xp(u)
    val perf = vm.modulePerformance(u)
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("${(vm.courseCompletion(u) * 100).toInt()}%", "Course completion", Icons.Filled.School, Brand.Cyan, Modifier.weight(1f))
                StatTile("${vm.level(xp)}", "Level · ${vm.levelName(xp)}", Icons.Filled.MilitaryTech, Brand.Gold, Modifier.weight(1f))
                StatTile("${vm.streak(u)}", "Day streak", Icons.Filled.LocalFireDepartment, Color(0xFFFF8A3D), Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("${u.lessonsDone.size}/${vm.content.lessons.size}", "Lessons", Icons.Filled.MenuBook, Brand.Neon, Modifier.weight(1f))
                StatTile("${u.labs.size}/${vm.content.labs.size}", "Labs", Icons.Filled.Science, Brand.Violet, Modifier.weight(1f))
                StatTile("${u.projects.values.count { it.completed }}/14", "Projects", Icons.Filled.Construction, Brand.CyberBlue, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("${vm.studyMinutes(u)}", "Study minutes", Icons.Filled.Timer, Brand.Cyan, Modifier.weight(1f))
                StatTile("${u.challenges.values.count { it.correct }}", "Challenges solved", Icons.Filled.SportsMartialArts, Brand.Danger, Modifier.weight(1f))
                StatTile("${vm.careerReadiness(u)}%", "Career readiness", Icons.Filled.Work, Brand.Gold, Modifier.weight(1f))
            }
        }
        item { Column {
            SectionTitle("Skill map")
            GlassCard {
                val sk = vm.skills(u)
                RadarChart(sk.map { it.name }, sk.map { it.value }, Modifier.fillMaxWidth().height(300.dp))
                sk.forEach { s ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(s.name, Modifier.width(150.dp), style = MaterialTheme.typography.bodySmall)
                        LinearProgressIndicator({ s.value }, Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)))
                        Text(" ${(s.value * 100).toInt()}%", Modifier.width(48.dp), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        } }
        item { Column {
            SectionTitle("Module completion")
            vm.content.modules.forEach { m ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { nav.go("module/${m.id}") }.padding(vertical = 4.dp)) {
                    Text("${m.number}", Modifier.width(26.dp), fontWeight = FontWeight.Bold)
                    Text(m.title, Modifier.width(150.dp), style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    LinearProgressIndicator({ vm.moduleProgress(u, m) }, Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = if (vm.modulePassed(u, m.id)) Brand.Neon else MaterialTheme.colorScheme.primary)
                    Text(perf[m.id]?.let { " $it%" } ?: "  -", Modifier.width(48.dp), style = MaterialTheme.typography.labelSmall)
                }
            }
        } }
        item { Column {
            SectionTitle("Strengths and weak areas")
            val strong = vm.strongModules(u); val weak = vm.weakModules(u)
            Text("Strong: " + (strong.joinToString { "M${it.number} ${it.title}" }.ifBlank { "keep going - complete quizzes to reveal strengths" }), color = Brand.Neon, style = MaterialTheme.typography.bodyMedium)
            Text("Needs work: " + (weak.joinToString { "M${it.number} ${it.title}" }.ifBlank { "none detected" }), color = Brand.Warn, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                OutlinedButton(onClick = { nav.go("quiz/revision") }) { Text("Revision test") }
                OutlinedButton(onClick = { nav.go("achievements") }) { Text("Achievements") }
                OutlinedButton(onClick = { nav.go("exams") }) { Text("History") }
            }
        } }
        item { Column {
            SectionTitle("Study time - last 14 days")
            val days = remember(u.days) {
                val cal = java.util.Calendar.getInstance(); val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.UK)
                (0 until 14).map { cal.time.let { d -> fmt.format(d) }.also { cal.add(java.util.Calendar.DAY_OF_MONTH, -1) } }.reversed()
            }
            val map = u.days.associate { it.day to it.minutes }
            val max = (days.maxOfOrNull { map[it] ?: 0 } ?: 1).coerceAtLeast(10)
            Row(Modifier.fillMaxWidth().height(110.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                days.forEach { d ->
                    val v = map[d] ?: 0
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.fillMaxWidth().height((90f * v / max).dp.coerceAtLeast(2.dp)).clip(RoundedCornerShape(4.dp)).background(Brush.verticalGradient(listOf(Brand.Cyan, Brand.Violet))))
                        Text(d.takeLast(2), fontSize = 9.sp)
                    }
                }
            }
        } }
    }
}

@Composable
fun AchievementsScreen() {
    val vm = LocalVm.current
    val u by vm.user.collectAsStateWithLifecycle()
    val badges = vm.badges(u)
    Column(Modifier.padding(16.dp)) {
        Text("${badges.count { it.earned }} of ${badges.size} badges earned · ${vm.xp(u)} XP", style = MaterialTheme.typography.titleMedium, color = Brand.Gold)
        Text("Gamification rewards real competency: module badges require passing assessments.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(10.dp))
        LazyVerticalGrid(GridCells.Adaptive(96.dp), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            gridItems(badges.sortedByDescending { it.earned }) { b ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Medal(b.title, b.earned, b.progress)
                    Text(b.desc, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
                }
            }
        }
    }
}

@Composable
fun CertificatesScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    var open by remember { mutableStateOf<Certificate?>(null) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            GlassCard(glow = Brand.Gold) {
                Text("Certificate centre", style = MaterialTheme.typography.titleMedium)
                Text("Progressive certificates: one per module (lessons complete + assessment 70%+), the Silverline capstone, and the full programme (final exam 70%+). These certify completion of this app's curriculum only - they are not official third-party certifications.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        if (u.certificates.isEmpty()) item { EmptyState(Icons.Filled.WorkspacePremium, "No certificates yet", "Finish Module 1's lessons and pass its assessment to earn your first.") }
        items(u.certificates) { c ->
            GlassCard(Modifier.fillMaxWidth(), glow = Brand.Gold, onClick = { open = c }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Medal("", true, 1f, 52.dp); Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.title, style = MaterialTheme.typography.titleSmall)
                        Text("${c.scorePct}% · ${PdfBuilder.date(c.issuedAt)} · ${c.certId}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        item {
            val remaining = vm.content.modules.filter { m -> u.certificates.none { it.refId == m.id } }
            if (remaining.isNotEmpty()) Text("Still to earn: ${remaining.size} module certificates" + (if (u.certificates.none { it.kind == "course" }) " + programme certificate" else ""), style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = { nav.go("exams") }) { Text("Go to the Examination centre") }
        }
    }
    open?.let { c -> CertificateDialog(c) { open = null } }
}

@Composable
private fun CertificateDialog(c: Certificate, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val saver = rememberFileSaver("application/pdf")
    val motion = LocalMotion.current
    AlertDialog(onDismissRequest = onClose, confirmButton = {
        TextButton(onClick = {
            saver.launch("Certificate-${c.certId}.pdf") {
                PdfBuilder(ctx, "Certificate", landscape = true).certificate(c.learner, c.title,
                    if (c.kind == "module") "Ethical Hacking Roadmap 2026/2027 curriculum module" else "Ethical Hacking Roadmap 2026/2027",
                    c.skills, "${c.scorePct}%", PdfBuilder.date(c.issuedAt), c.certId).build()
            }
        }) { Text("Export PDF") }
    }, dismissButton = {
        Row {
            TextButton(onClick = { shareText(ctx, "My certificate", "I completed \"${c.title}\" in Ethical Hacking Roadmap 2026/2027 by Bernportal System Innovations. Certificate ID: ${c.certId}") }) { Text("Share") }
            TextButton(onClick = onClose) { Text("Close") }
        }
    }, text = {
        Column(
            Modifier.fillMaxWidth().graphicsLayer { if (motion.effects3d) rotationX = 4f }.clip(RoundedCornerShape(12.dp)).background(Color.White)
                .border(5.dp, Brush.linearGradient(listOf(Brand.GoldDeep, Brand.Gold, Color(0xFFFFE08A))), RoundedCornerShape(12.dp)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandImage(R.drawable.brand_emblem, 90.dp)
            Text("BERNPORTAL SYSTEM INNOVATIONS", color = Brand.GoldDeep, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Certificate of Completion", color = Color(0xFF0B1638), style = MaterialTheme.typography.titleLarge)
            Text("This certifies that", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            Text(c.learner, color = Color(0xFF1E40AF), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("has completed", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            Text(c.title, color = Color(0xFF0B1638), style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
            Text("Skills: ${c.skills}", color = Color.DarkGray, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
            Text("Result ${c.scorePct}% · ${PdfBuilder.date(c.issuedAt)}", color = Color.Black, style = MaterialTheme.typography.labelMedium)
            Text("ID ${c.certId}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            Text("In-app curriculum certificate - not an official third-party certification.", color = Color.Gray, fontSize = 9.sp, textAlign = TextAlign.Center)
        }
    })
}

@Composable
fun PortfolioScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val s by vm.settings.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    val saver = rememberFileSaver("application/pdf")
    val projects = vm.content.projects.filter { u.projects[it.id]?.completed == true }
    val labs = vm.content.labs.filter { it.id in u.labs }
    val capPct = if (u.capstone.size >= vm.content.capstone.stages.size) u.capstone.values.count { it.correct } * 100 / u.capstone.size else null
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            GlassCard(glow = Brand.Violet) {
                Text("${s?.name?.ifBlank { "Learner" }} - Cybersecurity Portfolio", style = MaterialTheme.typography.titleLarge)
                Text("Target role: ${s?.targetRole}", color = Brand.Gold)
                Text("Built from your completed projects, labs, capstone, reports, certificates and skills. All work uses fictional organisations and simulated environments.", style = MaterialTheme.typography.bodySmall)
                Button(onClick = {
                    saver.launch("Portfolio.pdf") {
                        val pdf = PdfBuilder(ctx, "Portfolio").title("${s?.name?.ifBlank { "Learner" }} - Cybersecurity Portfolio", "Target role: ${s?.targetRole}")
                        pdf.heading("Skills").table(listOf("Skill area", "Level"), vm.skills(u).map { listOf(it.name, "${(it.value * 100).toInt()}%") }, listOf(3f, 1f))
                        pdf.heading("Security assessment methodology").text(com.bernportal.ehr.data.AppViewModel.METHODOLOGY)
                        pdf.heading("Projects")
                        if (projects.isEmpty()) pdf.text("Projects in progress.")
                        projects.forEach { p -> pdf.text("Project ${p.number}: ${p.title}", 11.5f, true).text(p.summary).text("Deliverable: ${p.deliverable}").text("Notes: " + (u.projects[p.id]?.notes ?: "").take(800)) }
                        pdf.heading("Capstone").text(capPct?.let { "Silverline Enterprise Security Assessment - score $it%" } ?: "Not yet completed.")
                        pdf.heading("Labs completed").table(listOf("Lab", "Category", "Score"), labs.map { listOf(it.title, it.category, "${u.labs[it.id]?.score}%") }, listOf(4f, 2f, 1f))
                        pdf.heading("Reports").bullet(u.reports.map { it.title + " (" + it.client + ")" }.ifEmpty { listOf("None yet") })
                        pdf.heading("Certificates").bullet(u.certificates.map { "${it.title} - ${it.scorePct}% - ${it.certId}" }.ifEmpty { listOf("None yet") })
                        pdf.heading("Learning achievements").bullet(vm.badges(u).filter { it.earned }.map { it.title }.ifEmpty { listOf("In progress") })
                        pdf.build()
                    }
                }, modifier = Modifier.padding(top = 8.dp)) { Icon(Icons.Filled.PictureAsPdf, null); Text(" Export portfolio PDF") }
            }
        }
        item { Column {
            SectionTitle("Projects (${projects.size}/14)")
            if (projects.isEmpty()) Text("Complete projects in the Project centre to showcase them here.", style = MaterialTheme.typography.bodySmall)
            projects.forEach { p -> GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp), onClick = { nav.go("project/${p.id}") }, padding = 12.dp) { Text("Project ${p.number}: ${p.title}", style = MaterialTheme.typography.titleSmall); Text(p.summary, style = MaterialTheme.typography.bodySmall) } }
            SectionTitle("Capstone"); Text(capPct?.let { "Silverline Enterprise Security Assessment - $it%" } ?: "Not yet completed", color = if (capPct != null) Brand.Neon else MaterialTheme.colorScheme.onSurfaceVariant)
            SectionTitle("Labs completed (${labs.size})"); ChipRow(labs.map { it.title })
            SectionTitle("Reports"); u.reports.forEach { r -> TextButton(onClick = { nav.go("report/${r.id}") }) { Text(r.title) } }
            SectionTitle("Certificates"); ChipRow(u.certificates.map { it.title })
            SectionTitle("Coding projects"); Text("Notes from Module 4 challenges and the Code Review lab appear in Notes; publish your own scripts on GitHub (no secrets, no real targets).", style = MaterialTheme.typography.bodySmall)
        } }
    }
}
