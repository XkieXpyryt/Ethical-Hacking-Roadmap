@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.R
import com.bernportal.ehr.data.AppViewModel
import com.bernportal.ehr.data.Module
import com.bernportal.ehr.data.UserData
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import java.util.Calendar

@Composable
fun HomeScreen() {
    val vm = LocalVm.current
    val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val s by vm.settings.collectAsStateWithLifecycle()
    val name = s?.name?.ifBlank { "Learner" } ?: "Learner"
    val xp = vm.xp(u)
    val next = vm.nextLesson(u)
    val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val motion = LocalMotion.current

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { WelcomeCard(vm, u, name, xp) }
        item {
            GlassCard(glow = Brand.Neon, onClick = { next?.let { nav.go("lesson/${it.id}") } }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BrandImage(R.drawable.ic3d_learn, 56.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Continue learning", style = MaterialTheme.typography.labelMedium, color = Brand.Neon)
                        Text(next?.title ?: "All available lessons complete - take your module exams!", style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        next?.let { l ->
                            val m = vm.content.module(l.module)!!
                            Text("Module ${m.number}: ${m.title} · ${l.minutes} min · ${l.difficulty}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    FilledIconButton(onClick = { next?.let { nav.go("lesson/${it.id}") } ?: nav.go("exams") }) { Icon(Icons.Filled.PlayArrow, "Continue") }
                }
            }
        }
        item { Column {
            SectionTitle("Learning roadmap", "Full map") { nav.go("roadmap") }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.content.modules) { m -> RoadmapNode(vm, u, m) { nav.go("module/${m.id}") } }
            }
        } }
        item { Column {
            SectionTitle("Progress overview", "Details") { nav.go("progress") }
            val labsPct = u.labs.size.toFloat() / vm.content.labs.size
            val projPct = u.projects.values.count { it.completed } / 14f
            val lvlPct = (xp % 500) / 500f
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                listOf(Triple(vm.courseCompletion(u), "Course", Brand.Cyan), Triple(labsPct, "Labs ${u.labs.size}/${vm.content.labs.size}", Brand.Neon),
                    Triple(projPct, "Projects ${u.projects.values.count { it.completed }}/14", Brand.Violet), Triple(lvlPct, "Level ${vm.level(xp)}", Brand.Gold)).forEach { (v, l, c) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ProgressRing(v, 70.dp, color = c)
                        Text(l, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Text("Lessons ${u.lessonsDone.size}/${vm.content.lessons.size} · Skills ${vm.skills(u).count { it.value >= 0.7f }}/8 mastered · Study time ${vm.studyMinutes(u)} min",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        } }
        item { Column {
            SectionTitle("Daily learning")
            val ch = vm.content.challenges[day % vm.content.challenges.size]
            val term = vm.content.glossary[day % vm.content.glossary.size]
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DailyTile("Daily lesson", next?.title ?: "Review a module", Icons.Filled.MenuBook, 0, Modifier.weight(1f)) { next?.let { nav.go("lesson/${it.id}") } }
                    DailyTile("Daily quiz", "5 questions from your modules", Icons.Filled.Quiz, 1, Modifier.weight(1f)) { nav.go("quiz/daily") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DailyTile("Daily lab challenge", ch.title, Icons.Filled.Terminal, 2, Modifier.weight(1f)) { nav.go("challenge/${ch.id}") }
                    DailyTile("Daily concept", term.term, Icons.Filled.Lightbulb, 6, Modifier.weight(1f)) { nav.go("glossary?term=${android.net.Uri.encode(term.term)}") }
                }
            }
        } }
        item { Column {
            SectionTitle("Skill map")
            GlassCard {
                val sk = vm.skills(u)
                RadarChart(sk.map { it.name }, sk.map { it.value }, Modifier.fillMaxWidth().height(300.dp))
            }
        } }
        item { Column {
            SectionTitle("Ask the AI tutor")
            var q by remember { mutableStateOf("") }
            GlassCard(glow = Brand.Violet) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BrandImage(R.drawable.ic3d_support, 44.dp)
                    Spacer(Modifier.width(10.dp))
                    OutlinedTextField(q, { q = it }, placeholder = { Text("e.g. What is the difference between TCP and UDP?") }, modifier = Modifier.weight(1f), maxLines = 3)
                    IconButton(onClick = { vm.tutorDraft.value = q; q = ""; nav.go("tutor") }) { Icon(Icons.AutoMirrored.Filled.Send, "Ask tutor") }
                }
            }
        } }
        item { Column {
            SectionTitle("Upcoming achievements", "All") { nav.go("achievements") }
            val up = vm.badges(u).filter { !it.earned }.sortedByDescending { it.progress }.take(4)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(up) { b -> Medal(b.title, false, b.progress) } }
        } }
        item { Column {
            SectionTitle("Weak areas", "Revision test") { nav.go("quiz/revision") }
            val weak = vm.weakModules(u)
            if (weak.isEmpty()) Text("No weak areas detected yet. Complete quizzes and the system will recommend revision automatically.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            weak.take(4).forEach { m ->
                GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp), glow = Brand.Danger, onClick = { nav.go("module/${m.id}") }, padding = 12.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.TrendingDown, null, tint = Brand.Danger); Spacer(Modifier.width(10.dp))
                        Text("Module ${m.number}: ${m.title} - ${vm.modulePerformance(u)[m.id]}%", Modifier.weight(1f))
                        Text("Revise", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        } }
        item { Column {
            SectionTitle("Quick access")
            val quick = listOf(
                Triple("Resume lesson", Icons.Filled.PlayCircle, next?.let { "lesson/${it.id}" } ?: "learn"),
                Triple("Start lab", Icons.Filled.Science, "labs?tab=0"), Triple("Take quiz", Icons.Filled.Quiz, "quiz/daily"),
                Triple("AI tutor", Icons.Filled.SmartToy, "tutor"), Triple("Toolkit", Icons.Filled.Build, "toolkit"),
                Triple("Notes", Icons.Filled.EditNote, "notes"), Triple("Search", Icons.Filled.Search, "search"), Triple("Glossary", Icons.Filled.Translate, "glossary")
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), maxItemsInEachRow = 4) {
                quick.forEachIndexed { i, (label, icon, r) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(76.dp)) {
                        val img = when (label) {
                            "Resume lesson" -> R.drawable.icc_book; "Toolkit" -> R.drawable.icc_library; "Notes" -> R.drawable.icc_notes
                            "Search" -> R.drawable.icc_search; "Glossary" -> R.drawable.icc_dict; else -> null
                        }
                        if (img != null) com.bernportal.ehr.ui.components.BrandIconButton(img, 58.dp, label) { nav.go(r) }
                        else Glossy3DIcon(icon, paletteFor(i), 58.dp, label) { nav.go(r) }
                        Spacer(Modifier.height(4.dp))
                        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
            }
        } }
        if (u.viewed.isNotEmpty()) item { Column {
            SectionTitle("Recently viewed")
            u.viewed.take(5).forEach { v ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable { nav.go(v.route) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.History, null, tint = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.width(10.dp))
                    Text(v.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        } }
        item { Spacer(Modifier.height(if (motion.enabled) 24.dp else 12.dp)) }
    }
}

@Composable
private fun WelcomeCard(vm: AppViewModel, u: UserData, name: String, xp: Int) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greet = when (hour) { in 5..11 -> "Good morning"; in 12..17 -> "Good afternoon"; else -> "Good evening" }
    val messages = listOf(
        "Every expert was once a beginner. Keep going.", "Defenders think in lists, attackers think in graphs - learn both.",
        "Small daily steps build professional skills.", "Curiosity + ethics = a great security career.", "Practice safely, document everything, never stop learning."
    )
    val streak = vm.streak(u)
    GlassCard(glow = Brand.Gold) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(64.dp).graphicsLayer { rotationZ = -6f }.clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Brand.CyberBlue, Brand.Violet))).border(3.dp, Brand.Gold, CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(name.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("$greet, $name", style = MaterialTheme.typography.titleLarge)
                Text("Level ${vm.level(xp)} · ${vm.levelName(xp)} · $xp XP", style = MaterialTheme.typography.labelLarge, color = Brand.Gold)
                LinearProgressIndicator({ (xp % 500) / 500f }, Modifier.fillMaxWidth().padding(vertical = 6.dp).height(6.dp).clip(RoundedCornerShape(3.dp)), color = Brand.Gold)
                Text(messages[(xp / 37 + Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) % messages.size], style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.LocalFireDepartment, "Streak", tint = if (streak > 0) Color(0xFFFF8A3D) else MaterialTheme.colorScheme.outline, modifier = Modifier.size(30.dp))
                Text("$streak", fontWeight = FontWeight.Bold)
                Text("day streak", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun RoadmapNode(vm: AppViewModel, u: UserData, m: Module, onClick: () -> Unit) {
    val unlocked = vm.isUnlocked(u, m)
    val passed = vm.modulePassed(u, m.id)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(84.dp).clickable(onClick = onClick)) {
        Box(contentAlignment = Alignment.TopEnd) {
            Glossy3DIcon(iconFor(m.icon), if (unlocked) paletteFor(m.number) else listOf(Color(0xFF334155), Color(0xFF475569)), 60.dp, m.title)
            Icon(
                when { passed -> Icons.Filled.CheckCircle; unlocked -> Icons.Filled.LockOpen; else -> Icons.Filled.Lock }, null,
                tint = when { passed -> Brand.Neon; unlocked -> Brand.Cyan; else -> Brand.Muted },
                modifier = Modifier.size(20.dp).clip(CircleShape).background(MaterialTheme.colorScheme.background)
            )
        }
        Text("${m.number}. ${m.title}", style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
        LinearProgressIndicator({ vm.moduleProgress(u, m) }, Modifier.width(60.dp).height(3.dp).clip(RoundedCornerShape(2.dp)))
    }
}

@Composable
private fun DailyTile(title: String, body: String, icon: androidx.compose.ui.graphics.vector.ImageVector, pal: Int, modifier: Modifier, onClick: () -> Unit) {
    GlassCard(modifier.height(118.dp), glow = paletteFor(pal).last(), onClick = onClick, padding = 12.dp) {
        Glossy3DIcon(icon, paletteFor(pal), 36.dp)
        Spacer(Modifier.height(6.dp))
        Text(title, style = MaterialTheme.typography.labelMedium, color = paletteFor(pal).last())
        Text(body, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
