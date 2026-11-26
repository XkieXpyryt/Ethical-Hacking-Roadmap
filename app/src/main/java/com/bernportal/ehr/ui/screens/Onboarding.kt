@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.bernportal.ehr.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bernportal.ehr.data.AppViewModel
import com.bernportal.ehr.ui.components.AppBackground
import com.bernportal.ehr.ui.components.BrandOrbitAnimated
import com.bernportal.ehr.ui.components.CubeTrail
import com.bernportal.ehr.ui.components.ExtrudedText3D
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.composed
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.LaunchedEffect
import com.bernportal.ehr.ui.components.GlassCard
import com.bernportal.ehr.ui.components.Glossy3DIcon
import com.bernportal.ehr.ui.components.paletteFor
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

val LEVELS = listOf("Complete beginner", "Basic computer user", "IT student", "IT support professional", "Networking professional", "Software developer", "Experienced security practitioner")
val GOALS = listOf("Learn cybersecurity from scratch", "Become an ethical hacker", "Become a penetration tester", "Become a security analyst", "Learn web application security",
    "Become a red team operator", "Learn blue team defence", "Prepare for cybersecurity certifications", "Improve my IT career", "Build practical cybersecurity skills")
val PACES = listOf("Relaxed" to 15, "Standard" to 30, "Intensive" to 60, "Career bootcamp" to 120)

@Composable
fun OnboardingScreen(vm: AppViewModel, onDone: () -> Unit) {
    val pager = rememberPagerState { 5 }
    val scope = rememberCoroutineScope()
    var level by remember { mutableStateOf(LEVELS[0]) }
    var goals by remember { mutableStateOf(setOf(GOALS[0])) }
    var pace by remember { mutableStateOf("Standard") }
    var minutes by remember { mutableFloatStateOf(30f) }
    var name by remember { mutableStateOf("") }
    val motion = LocalMotion.current

    AppBackground {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
                repeat(5) { i ->
                    val w by animateFloatAsState(if (pager.currentPage == i) 28f else 8f, tween(motion.dur(250)), label = "dot")
                    Box(Modifier.padding(3.dp).height(8.dp).width(w.dp).clip(CircleShape).background(if (pager.currentPage == i) Brand.Cyan else MaterialTheme.colorScheme.outline))
                }
            }
            HorizontalPager(pager, Modifier.weight(1f), userScrollEnabled = true) { page ->
                val offset = (pager.currentPage - page + pager.currentPageOffsetFraction)
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
                        .graphicsLayer { if (motion.effects3d) { rotationY = offset * -28f; cameraDistance = 16f * density }; alpha = 1f - offset.absoluteValue * 0.5f },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (page) {
                        0 -> {
                            val shown = pager.currentPage == 0
                            Spacer(Modifier.height(8.dp))
                            BrandOrbitAnimated(320.dp, Modifier.stagger(shown, 0))
                            ExtrudedText3D("BERNPORTAL", 28.sp, Modifier.stagger(shown, 1), depth = 6, letterSpacing = 3.sp, flip3d = motion.effects3d)
                            ExtrudedText3D("SYSTEM INNOVATIONS", 14.sp, Modifier.stagger(shown, 2), depth = 3, letterSpacing = 2.sp, flip3d = motion.effects3d,
                                face = listOf(Color.White, Color(0xFF9CC9FF), Brand.CyberBlue), side = Color(0xFF0A1B4A))
                            CubeTrail(Modifier.padding(vertical = 6.dp).stagger(shown, 3))
                            Text("Welcome to your cybersecurity academy", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center, modifier = Modifier.stagger(shown, 4))
                            Spacer(Modifier.height(8.dp))
                            Text("From your first look inside a computer to professional ethical hacking: 16 modules, simulated labs, projects, an AI tutor, a capstone engagement and certificates - all main materials work offline.",
                                style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.stagger(shown, 5))
                            Spacer(Modifier.height(16.dp))
                            GlassCard(Modifier.stagger(shown, 6), glow = Brand.Gold) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Glossy3DIcon(Icons.Filled.Gavel, paletteFor(6), 44.dp)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Ethics first: you will only ever practise on simulations, your own isolated lab or platforms that authorise you. Unauthorised hacking is a crime.",
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                        1 -> {
                            Header("What best describes you?", "We use this to recommend where to start. You can change it at any time.")
                            LEVELS.forEachIndexed { i, l -> Box(Modifier.stagger(pager.currentPage == 1, i)) { SelectCard(l, level == l, iconForLevel(i), paletteFor(i)) { level = l } } }
                        }
                        2 -> {
                            Header("What are your goals?", "Choose as many as you like.")
                            GOALS.forEachIndexed { i, g -> Box(Modifier.stagger(pager.currentPage == 2, i)) { SelectCard(g, g in goals, Icons.Filled.Flag, paletteFor(i + 2)) { goals = if (g in goals) goals - g else goals + g } } }
                        }
                        3 -> {
                            Header("Choose your pace", "Your daily target and study reminders adapt to this.")
                            PACES.forEachIndexed { i, (p, m) -> Box(Modifier.stagger(pager.currentPage == 3, i)) { SelectCard("$p  -  about $m min/day", pace == p, Icons.Filled.Speed, paletteFor(i + 4)) { pace = p; minutes = m.toFloat() } } }
                            Spacer(Modifier.height(12.dp))
                            Text("Daily study time: ${minutes.toInt()} minutes", style = MaterialTheme.typography.titleSmall)
                            Slider(minutes, { minutes = it }, valueRange = 10f..180f, steps = 16)
                        }
                        else -> {
                            Header("Your personalised plan", "Tell us your name for your profile and certificates.")
                            OutlinedTextField(name, { name = it.take(40) }, label = { Text("Your name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(12.dp))
                            val start = vm.placementLevel(level)
                            val totalMin = vm.content.modules.filter { it.number >= start }.sumOf { it.minutes } * 3 // lessons + labs + revision
                            val weeks = (totalMin / (minutes * 6)).toInt().coerceAtLeast(4)
                            GlassCard(glow = Brand.Neon) {
                                Text("Starting point: Module $start - ${vm.content.modules[start - 1].title}", style = MaterialTheme.typography.titleSmall)
                                Text("Pace: $pace (${minutes.toInt()} min/day, 6 days a week)", style = MaterialTheme.typography.bodyMedium)
                                Text("Estimated time to complete the roadmap: about $weeks weeks", style = MaterialTheme.typography.bodyMedium)
                                Text("Focus areas: ${goals.take(3).joinToString()}", style = MaterialTheme.typography.bodyMedium)
                                if (start > 1) Text("Earlier modules stay open for review. To earn certificates you still need to pass each module assessment.",
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = {
                                vm.updateSettings { it.copy(onboarded = true, name = name.trim(), level = level, goals = goals, pace = pace, dailyMinutes = minutes.toInt()) }
                                onDone()
                            }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                                Icon(Icons.Filled.RocketLaunch, null); Spacer(Modifier.width(8.dp)); Text("Start learning")
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = { scope.launch { pager.animateScrollToPage((pager.currentPage - 1).coerceAtLeast(0)) } }, enabled = pager.currentPage > 0) { Text("Back") }
                if (pager.currentPage < 4) Button(onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } }) { Text("Next") }
            }
        }
    }
}

private fun iconForLevel(i: Int) = listOf(Icons.Filled.EmojiPeople, Icons.Filled.Computer, Icons.Filled.School, Icons.Filled.SupportAgent,
    Icons.Filled.Lan, Icons.Filled.Code, Icons.Filled.Security)[i]

@Composable
private fun Header(title: String, sub: String) {
    Spacer(Modifier.height(8.dp))
    Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.fillMaxWidth())
    Text(sub, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(12.dp))
}

@Composable
fun SelectCard(text: String, selected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, colors: List<androidx.compose.ui.graphics.Color>, onClick: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth().padding(vertical = 5.dp), glow = if (selected) Brand.Cyan else MaterialTheme.colorScheme.outline, onClick = onClick, padding = 12.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Glossy3DIcon(icon, colors, 40.dp)
            Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            Icon(if (selected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked, null, tint = if (selected) Brand.Neon else MaterialTheme.colorScheme.outline)
        }
    }
}


/**
 * Staggered 3D entrance: when [visible] becomes true the element tilts in around Y, rises and fades in,
 * delayed by [index] so a page's items cascade in one after another. Respects the motion and 3D settings.
 */
fun Modifier.stagger(visible: Boolean, index: Int): Modifier = composed {
    val motion = LocalMotion.current
    val p = remember { Animatable(if (motion.enabled) 0f else 1f) }
    LaunchedEffect(visible) {
        if (!motion.enabled) { p.snapTo(1f); return@LaunchedEffect }
        if (visible) { p.snapTo(0f); kotlinx.coroutines.delay(80L * index); p.animateTo(1f, tween(motion.dur(520), easing = FastOutSlowInEasing)) }
    }
    graphicsLayer {
        val v = p.value
        alpha = v
        translationY = (1 - v) * 40f * density
        if (motion.effects3d) { rotationX = (1 - v) * 35f; rotationY = (1 - v) * -20f }
        cameraDistance = 14f * density
    }
}
