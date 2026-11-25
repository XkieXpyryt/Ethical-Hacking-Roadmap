@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.data.QuizQ
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(scope: String) {
    val vm = LocalVm.current; val nav = LocalNav.current
    val motion = LocalMotion.current
    var attemptKey by remember { mutableIntStateOf(0) }
    val spec = remember(scope, attemptKey) { vm.quizSpec(scope) }
    val qs = spec.questions
    if (qs.isEmpty()) { EmptyState(Icons.Filled.Quiz, "No questions available", "Complete some lessons first."); return }

    var index by remember(attemptKey) { mutableIntStateOf(0) }
    val answers = remember(attemptKey) { mutableStateMapOf<Int, Set<Int>>() }
    val checked = remember(attemptKey) { mutableStateMapOf<Int, Boolean>() }
    var finished by remember(attemptKey) { mutableStateOf(false) }
    var reviewing by remember(attemptKey) { mutableStateOf(false) }
    val start = remember(attemptKey) { System.currentTimeMillis() }
    var remaining by remember(attemptKey) { mutableIntStateOf(spec.minutes * 60) }

    fun isCorrect(i: Int) = answers[i]?.let { it == qs[i].answer.toSet() } ?: false
    fun finish() {
        if (finished) return
        finished = true
        val score = qs.indices.count { isCorrect(it) }
        val byModule = qs.indices.groupBy { qs[it].module }.filterKeys { it.isNotBlank() }
            .map { (m, idx) -> "$m:${idx.count { isCorrect(it) }}/${idx.size}" }.joinToString(",")
        vm.recordAttempt(scope, spec.title, score, qs.size, byModule, ((System.currentTimeMillis() - start) / 60000).toInt())
    }

    if (spec.minutes > 0 && !finished) LaunchedEffect(attemptKey) {
        while (remaining > 0 && !finished) { delay(1000); remaining-- }
        if (!finished) finish()
    }

    if (finished && !reviewing) {
        QuizResults(spec.title, qs, ::isCorrect, onReview = { reviewing = true }, onRetry = { attemptKey++ }, onDone = { nav.popBackStack() })
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(spec.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            if (spec.minutes > 0 && !finished) Pill("%d:%02d".format(remaining / 60, remaining % 60), if (remaining < 120) Brand.Danger else Brand.Cyan)
            if (reviewing) Pill("Review mode", Brand.Violet)
        }
        LinearProgressIndicator({ (index + 1f) / qs.size }, Modifier.fillMaxWidth().padding(vertical = 8.dp).height(6.dp).clip(RoundedCornerShape(3.dp)))
        Text("Question ${index + 1} of ${qs.size}" + (qs[index].sourceTitle.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""),
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        AnimatedContent(index, transitionSpec = {
            (slideInHorizontally(tween(motion.dur(260))) { it / 4 } + fadeIn(tween(motion.dur(260)))) togetherWith fadeOut(tween(motion.dur(120)))
        }, modifier = Modifier.weight(1f), label = "q") { i ->
            val q = qs[i]
            val showFeedback = reviewing || (spec.instant && checked[i] == true)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                item { Column {
                    if (q.category.isNotBlank()) Pill(q.category.replaceFirstChar { it.uppercase() } + " question", Brand.Gold)
                    Spacer(Modifier.height(6.dp))
                    Text(q.q, style = MaterialTheme.typography.titleLarge)
                    if (q.isMulti) Text("Select ALL that apply.", style = MaterialTheme.typography.labelMedium, color = Brand.Warn)
                    if (q.artifact.isNotBlank()) { Spacer(Modifier.height(8.dp)); MonoBlock(q.artifact) }
                } }
                itemsIndexed(q.options) { oi, opt ->
                    val sel = answers[i]?.contains(oi) == true
                    val isAns = oi in q.answer
                    val border = when {
                        showFeedback && isAns -> Brand.Neon
                        showFeedback && sel && !isAns -> Brand.Danger
                        sel -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline
                    }
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(border.copy(alpha = if (sel || (showFeedback && isAns)) 0.12f else 0.04f))
                            .border(1.5.dp, border, RoundedCornerShape(14.dp))
                            .clickable(enabled = !showFeedback && !finished) {
                                val cur = answers[i] ?: emptySet()
                                answers[i] = if (q.isMulti) (if (oi in cur) cur - oi else cur + oi) else setOf(oi)
                            }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (q.isMulti) Checkbox(sel, null) else RadioButton(sel, null)
                        Spacer(Modifier.width(8.dp))
                        Text(opt, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        if (showFeedback && isAns) Icon(Icons.Filled.CheckCircle, null, tint = Brand.Neon)
                        if (showFeedback && sel && !isAns) Icon(Icons.Filled.Cancel, null, tint = Brand.Danger)
                    }
                }
                if (showFeedback) item {
                    val ok = isCorrect(i)
                    GlassCard(glow = if (ok) Brand.Neon else Brand.Warn) {
                        Text(if (ok) "Correct!" else if (answers[i] == null) "Not answered" else "Not quite", style = MaterialTheme.typography.titleMedium, color = if (ok) Brand.Neon else Brand.Warn)
                        Text("Correct answer: " + q.answer.joinToString(" + ") { q.options[it] }, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(q.explain, style = MaterialTheme.typography.bodyMedium)
                        val wrong = q.options.indices.filter { it !in q.answer }
                        if (wrong.isNotEmpty() && q.type != "truefalse") {
                            Spacer(Modifier.height(6.dp))
                            Text("Why the other options are wrong:", style = MaterialTheme.typography.labelLarge)
                            wrong.forEach { w -> Text("• \"${q.options[w]}\" does not fit: ${shortReason(q)}", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }

        if (!spec.instant && !reviewing) {
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                qs.indices.forEach { qi ->
                    Box(Modifier.size(28.dp).clip(CircleShape).background(
                        when { qi == index -> MaterialTheme.colorScheme.primary; answers[qi] != null -> Brand.Violet.copy(alpha = 0.5f); else -> MaterialTheme.colorScheme.surfaceVariant }
                    ).clickable { index = qi }, contentAlignment = Alignment.Center) { Text("${qi + 1}", style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center) }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { if (index > 0) index-- }, enabled = index > 0) { Text("Previous") }
            Spacer(Modifier.weight(1f))
            when {
                reviewing -> if (index < qs.lastIndex) Button(onClick = { index++ }) { Text("Next") } else Button(onClick = { reviewing = false }) { Text("Results") }
                spec.instant && checked[index] != true -> Button(onClick = { checked[index] = true }, enabled = answers[index] != null) { Text("Check answer") }
                index < qs.lastIndex -> Button(onClick = { index++ }) { Text("Next") }
                else -> Button(onClick = { finish() }, colors = ButtonDefaults.buttonColors(containerColor = Brand.Neon, contentColor = Color.Black)) { Text("Submit") }
            }
        }
    }
}

private fun shortReason(q: QuizQ) = "it does not match the key idea - ${q.explain.substringBefore(".").take(110)}."

@Composable
private fun QuizResults(title: String, qs: List<QuizQ>, isCorrect: (Int) -> Boolean, onReview: () -> Unit, onRetry: () -> Unit, onDone: () -> Unit) {
    val vm = LocalVm.current; val nav = LocalNav.current
    val score = qs.indices.count { isCorrect(it) }
    val pct = score * 100 / qs.size
    val passed = pct >= 70
    val byModule = qs.indices.groupBy { qs[it].module }.filterKeys { it.isNotBlank() }.mapValues { (_, idx) -> idx.count { isCorrect(it) } * 100 / idx.size }
    val weak = byModule.filter { it.value < 70 }.keys.mapNotNull { vm.content.module(it) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            GlassCard(glow = if (passed) Brand.Neon else Brand.Warn) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressRing(pct / 100f, 96.dp, 10.dp, if (passed) Brand.Neon else Brand.Warn)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(if (passed) "Passed!" else "Keep practising", style = MaterialTheme.typography.headlineSmall, color = if (passed) Brand.Neon else Brand.Warn)
                        Text(title, style = MaterialTheme.typography.bodyMedium)
                        Text("$score of ${qs.size} correct · pass mark 70%", style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (passed) Text("Results saved. If this completed a module assessment and all its lessons, your certificate has been issued in the Certificate Centre.",
                    style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
        }
        if (byModule.size > 1) item { Column {
            SectionTitle("Skill assessment by module")
            byModule.toSortedMap().forEach { (m, p) ->
                val mod = vm.content.module(m)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                    Text("M${mod?.number ?: "?"}", Modifier.width(40.dp), fontWeight = FontWeight.Bold)
                    LinearProgressIndicator({ p / 100f }, Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)), color = if (p >= 70) Brand.Neon else Brand.Warn)
                    Text(" $p%", Modifier.width(52.dp))
                }
            }
        } }
        if (weak.isNotEmpty()) item { Column {
            SectionTitle("Recommended revision")
            weak.forEach { m ->
                GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp), glow = Brand.Warn, onClick = { nav.go("module/${m.id}") }, padding = 12.dp) {
                    Text("Module ${m.number}: ${m.title}", style = MaterialTheme.typography.titleSmall)
                    Text("Revise: " + m.lessons.take(3).joinToString { it.title }, style = MaterialTheme.typography.bodySmall)
                }
            }
        } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onReview) { Icon(Icons.Filled.Visibility, null); Text(" Review answers") }
                OutlinedButton(onClick = onRetry) { Icon(Icons.Filled.Replay, null); Text(" Retry") }
            }
            TextButton(onClick = onDone) { Text("Done") }
        }
    }
}
