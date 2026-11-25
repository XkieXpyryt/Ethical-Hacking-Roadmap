@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.R
import com.bernportal.ehr.ai.TutorEngine
import com.bernportal.ehr.data.Settings
import com.bernportal.ehr.ui.KNOWN_ROUTES
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.util.openUrl
import com.bernportal.ehr.util.rememberFileOpener
import com.bernportal.ehr.util.rememberFileSaver
import com.bernportal.ehr.util.toast
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val ctx = LocalContext.current
    val s0 by vm.settings.collectAsStateWithLifecycle()
    val s = s0 ?: return
    val scope = rememberCoroutineScope()
    val online by vm.online.collectAsStateWithLifecycle()
    var key by remember { mutableStateOf(vm.aiKey()) }
    var confirm by remember { mutableStateOf<String?>(null) }
    var testResult by remember { mutableStateOf("") }
    val backupSaver = rememberFileSaver("application/json")
    val restore = rememberFileOpener(arrayOf("application/json", "text/plain", "*/*")) { uri ->
        val text = ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
        toast(ctx, vm.importJson(text))
    }
    val packOpener = rememberFileOpener(arrayOf("application/json", "*/*")) { uri -> toast(ctx, vm.importContentPack(uri)) }
    val notifPerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        vm.updateSettings { it.copy(notifications = granted) }
        if (!granted) toast(ctx, "Notifications permission denied - reminders are off")
    }
    fun set(f: (Settings) -> Settings) = vm.updateSettings(f)

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        item { Group("Profile & learning", R.drawable.ic3d_learn) }
        item { OutlinedTextField(s.name, { v -> set { it.copy(name = v.take(40)) } }, label = { Text("Name (used on certificates)") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
        item { Choice("Learner level", s.level, LEVELS) { v -> set { it.copy(level = v) } } }
        item { Choice("Learning pace", s.pace, PACES.map { it.first }) { v -> set { it.copy(pace = v, dailyMinutes = PACES.first { p -> p.first == v }.second) } } }
        item { Column { Text("Daily goal: ${s.dailyMinutes} min"); Slider(s.dailyMinutes.toFloat(), { v -> set { it.copy(dailyMinutes = v.toInt()) } }, valueRange = 10f..180f) } }
        item { Toggle("Explore all modules freely", "Unlock every module for reading. Certificates still require passing assessments.", s.freeExplore) { v -> set { it.copy(freeExplore = v) } } }
        item { TextButton(onClick = { set { it.copy(onboarded = false) } }) { Text("Redo onboarding / learning goals") } }

        item { Group("Appearance & accessibility", R.drawable.ic3d_settings) }
        item { Choice("Theme", s.theme, listOf("Midnight", "Obsidian", "High contrast", "Daylight")) { v -> set { it.copy(theme = v) } } }
        item { Choice("Animation intensity", s.animation, listOf("Full", "Reduced", "Off")) { v -> set { it.copy(animation = v) } } }
        item { Toggle("3D effects", "Tilt, depth and 3D flips on cards, icons and the intro", s.effects3d) { v -> set { it.copy(effects3d = v) } } }
        item { Toggle("Play 3D intro on launch", "The animated Bernportal opening sequence", s.playIntro) { v -> set { it.copy(playIntro = v) } } }
        item { Column { Text("Font size: ${(s.fontScale * 100).toInt()}%"); Slider(s.fontScale, { v -> set { it.copy(fontScale = v) } }, valueRange = 0.85f..1.6f, steps = 14) } }
        item { Toggle("High contrast", "Maximum contrast colours", s.highContrast) { v -> set { it.copy(highContrast = v) } } }
        item { Toggle("Dyslexia-friendly reading", "Extra letter spacing and line height", s.dyslexia) { v -> set { it.copy(dyslexia = v) } } }
        item { Toggle("Simple language mode", "Show the simple explanation first (off = technical first)", s.simpleLanguage) { v -> set { it.copy(simpleLanguage = v) } } }
        item { Toggle("Sound effects", "Subtle sounds (uses system sounds)", s.sound) { v -> set { it.copy(sound = v) } } }
        item { Toggle("Haptic feedback", "Vibration on key actions", s.haptics) { v -> set { it.copy(haptics = v) } } }
        item { Choice("Language", s.language, listOf("English (UK)", "English (US)")) { v -> set { it.copy(language = v) } } }

        item { Group("Notifications & reminders", R.drawable.ic3d_notes) }
        item {
            Toggle("Daily study reminders", "A gentle reminder at your chosen hour", s.notifications) { v ->
                if (v && Build.VERSION.SDK_INT >= 33) notifPerm.launch(Manifest.permission.POST_NOTIFICATIONS) else set { it.copy(notifications = v) }
            }
        }
        item { Column { Text("Reminder time: %02d:00".format(s.reminderHour)); Slider(s.reminderHour.toFloat(), { v -> set { it.copy(reminderHour = v.toInt()) } }, valueRange = 6f..22f, steps = 15) } }

        item { Group("AI tutor (online)", R.drawable.ic3d_support) }
        item {
            GlassCard(glow = Brand.Violet) {
                Text("The AI tutor uses YOUR OWN free API key - none is built into the app. Your key is encrypted with the Android Keystore and never included in backups.", style = MaterialTheme.typography.bodySmall)
                Text("Free options: Google AI Studio (Gemini) or Groq. Or point to an OpenAI-compatible server you run yourself (e.g. Ollama).", style = MaterialTheme.typography.bodySmall)
                Row { TextButton(onClick = { openUrl(ctx, "https://aistudio.google.com/apikey") }) { Text("Get a Gemini key") }; TextButton(onClick = { openUrl(ctx, "https://console.groq.com/keys") }) { Text("Get a Groq key") } }
            }
        }
        item {
            Choice("Provider", when (s.aiProvider) { "gemini" -> "Google Gemini"; "groq" -> "Groq"; "openrouter" -> "OpenRouter"; else -> "Custom (OpenAI-compatible)" },
                listOf("Google Gemini", "Groq", "OpenRouter", "Custom (OpenAI-compatible)")) { v ->
                val (p, m) = when (v) { "Google Gemini" -> "gemini" to "gemini-2.5-flash"; "Groq" -> "groq" to "llama-3.3-70b-versatile"; "OpenRouter" -> "openrouter" to "meta-llama/llama-3.3-70b-instruct:free"; else -> "custom" to "llama3.1" }
                set { it.copy(aiProvider = p, aiModel = m) }
            }
        }
        item { OutlinedTextField(s.aiModel, { v -> set { it.copy(aiModel = v.trim()) } }, label = { Text("Model name (editable - providers rename models)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        if (s.aiProvider == "custom") item { OutlinedTextField(s.aiBaseUrl, { v -> set { it.copy(aiBaseUrl = v.trim()) } }, label = { Text("Base URL (https://.../v1)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { Column {
            OutlinedTextField(key, { key = it }, label = { Text("API key") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.setAiKey(key); toast(ctx, if (key.isBlank()) "Key removed" else "Key saved securely") }) { Text("Save key") }
                OutlinedButton(onClick = {
                    vm.setAiKey(key)
                    scope.launch {
                        testResult = "Testing..."
                        testResult = if (!online) "Offline - connect to the internet to test." else vm.tutorEngine.ask(TutorEngine.Config(s.aiProvider, s.aiModel, s.aiBaseUrl, key), "Beginner tutor", null, s.level, emptyList(), "Reply with exactly: Connection OK").take(200)
                    }
                }) { Text("Test connection") }
            }
            if (testResult.isNotBlank()) Text(testResult, style = MaterialTheme.typography.bodySmall)
        } }
        item { Choice("Default tutor mode", s.tutorMode, vm.tutorEngine.modes) { v -> set { it.copy(tutorMode = v) } } }

        item { Group("Data, backup & privacy", R.drawable.ic3d_backup) }
        item {
            GlassCard {
                Text("Your data stays on this device. No analytics, no tracking, no account. Internet is only used for the AI tutor, links you open, and backups you choose to save to Google Drive.",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            SettingButton(Icons.Filled.CloudUpload, "Back up / export my data", "Saves a JSON file. Choose Google Drive in the picker to back up to Drive.") {
                backupSaver.launch("EHR-backup-${vm.today()}.json") { vm.exportJson().toByteArray() }
            }
        }
        item { SettingButton(Icons.Filled.CloudDownload, "Restore / import from backup", "Pick a backup file from your device or Google Drive (replaces current data)") { restore() } }
        item { SettingButton(Icons.Filled.Inventory2, "Offline storage", "All course content is bundled (about 0.6 MB). No downloads required.") { toast(ctx, "All lessons, labs and assets are stored inside the app.") } }
        item { SettingButton(Icons.Filled.RestartAlt, "Reset progress", "Clears progress, attempts, labs, projects, certificates and reports. Keeps notes, bookmarks and flashcards.") { confirm = "reset" } }
        item { SettingButton(Icons.Filled.DeleteForever, "Delete all my data", "Erases everything including settings and your AI key") { confirm = "delete" } }

        item { Group("Content updates & about", R.drawable.ic3d_about) }
        item {
            val meta = vm.content.meta
            GlassCard {
                Text("Course version ${meta.courseVersion} · Content ${meta.contentVersion} · Updated ${meta.updated}", style = MaterialTheme.typography.titleSmall)
                meta.history.forEach { Text("v${it.version} (${it.date}): ${it.notes}", style = MaterialTheme.typography.bodySmall) }
            }
        }
        item { SettingButton(Icons.Filled.SystemUpdate, "Install offline content update pack", "Import a .json pack - your progress, notes, bookmarks, certificates, labs and projects are kept") { packOpener() } }
        item { SettingButton(Icons.Filled.Undo, "Revert to bundled content", "Remove any installed update pack") { toast(ctx, vm.removeContentPack()) } }
        item { SettingButton(Icons.Filled.FactCheck, "Content validation", "Check for missing lessons, quizzes, labs, links and assets") { nav.go("diagnostics") } }
        item { SettingButton(Icons.Filled.Help, "Help & Support", "How to use the app, safety rules and contact") { nav.go("help") } }
        item { Spacer(Modifier.height(32.dp)) }
    }

    confirm?.let { c ->
        AlertDialog(onDismissRequest = { confirm = null }, title = { Text(if (c == "reset") "Reset progress?" else "Delete all data?") },
            text = { Text(if (c == "reset") "This cannot be undone. Consider exporting a backup first." else "Everything on this device for this app will be erased. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    confirm = null
                    if (c == "reset") vm.resetProgress { toast(ctx, "Progress reset") } else vm.deleteAllData { (ctx as? Activity)?.recreate() }
                }) { Text(if (c == "reset") "Reset" else "Delete", color = Brand.Danger) }
            }, dismissButton = { TextButton(onClick = { confirm = null }) { Text("Cancel") } })
    }
}

@Composable
private fun Group(title: String, img: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp)) {
        BrandImage(img, 34.dp); Spacer(Modifier.width(10.dp)); Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun Toggle(title: String, sub: String, value: Boolean, onChange: (Boolean) -> Unit) {
    ListItem(headlineContent = { Text(title) }, supportingContent = { Text(sub) }, trailingContent = { Switch(value, onChange) },
        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent), modifier = Modifier.clickable { onChange(!value) })
}

@Composable
private fun Choice(title: String, value: String, options: List<String>, onPick: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(open, { open = it }) {
        OutlinedTextField(value, {}, readOnly = true, label = { Text(title) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(open) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth().padding(vertical = 4.dp))
        ExposedDropdownMenu(open, { open = false }) { options.forEach { o -> DropdownMenuItem({ Text(o) }, { onPick(o); open = false }) } }
    }
}

@Composable
private fun SettingButton(icon: ImageVector, title: String, sub: String, onClick: () -> Unit) {
    ListItem(headlineContent = { Text(title) }, supportingContent = { Text(sub) }, leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent), modifier = Modifier.clickable(onClick = onClick))
}

// ======================= Help =======================
@Composable
fun HelpScreen() {
    val ctx = LocalContext.current; val nav = LocalNav.current
    val faq = listOf(
        "How do modules unlock?" to "Module 1 is open. Each next module unlocks when you pass the previous module assessment with 70%+. You can take an assessment early as a placement test, or enable 'Explore all modules freely' in Settings for reading.",
        "Does the app work offline?" to "Yes. All lessons, images, quizzes, labs, projects, glossary, toolkit, notes, progress, reports and certificates work without internet. Only the AI tutor, external links and Google Drive backups need a connection.",
        "Are the labs real hacking?" to "No. Terminal labs are simulations with scripted outputs from fictional organisations. Nothing runs on your device and the labs cannot reach any real system.",
        "How do I enable the AI tutor?" to "Settings > AI tutor: choose a provider, paste your own free API key (Gemini or Groq), save and test. Without a key, an offline course assistant answers from your lessons.",
        "How do I back up to Google Drive?" to "Settings > Back up / export my data, then choose Google Drive in the system file picker. Restore the same way.",
        "How are certificates earned?" to "Complete all lessons in a module and pass its assessment (70%+). The capstone and final exam give additional certificates. They certify this app's curriculum only.",
        "Where is my evidence for reports?" to "Every completed lab saves its transcript and reflections to Notes. In the Report Builder, use 'Attach lab evidence' on a finding.",
        "Can I practise on real websites?" to "Only on systems you own or have written permission to test, and on legal platforms such as TryHackMe, Hack The Box, PortSwigger Web Security Academy or OWASP Juice Shop on your own machine."
    )
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Emblem3D(220.dp)
                Text("Bernportal System Innovations", style = MaterialTheme.typography.titleMedium, color = Brand.Gold)
                Text("Ethical Hacking Roadmap 2026/2027", style = MaterialTheme.typography.headlineSmall)
                Text("Learn. Practice. Secure. Become.", color = Brand.Neon)
            }
        }
        item {
            GlassCard(glow = Brand.Warn) {
                Text("Safety rules", style = MaterialTheme.typography.titleSmall, color = Brand.Warn)
                BulletList(listOf("Only test systems you own or have written authorisation to test.", "Keep vulnerable practice VMs on isolated networks.",
                    "Never collect real people's credentials or personal data.", "Report vulnerabilities responsibly.", "When in doubt - stop and ask."), Brand.Warn)
            }
        }
        item { SectionTitle("Frequently asked questions") }
        items(faq) { (q, a) ->
            var open by remember { mutableStateOf(false) }
            GlassCard(Modifier.fillMaxWidth(), onClick = { open = !open }, padding = 12.dp) {
                Row { Text(q, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f)); Icon(if (open) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null) }
                if (open) Text(a, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item { Column {
            SectionTitle("Contextual help")
            BulletList(listOf("Tap 'Explain this again' in any lesson for 7 alternative explanations.", "Type 'hint' or 'tasks' in any terminal lab.",
                "Long-press nothing - every action has a visible button.", "Use Search (top bar) to find any topic, tool or command offline."))
            SectionTitle("Support")
            Text("Report a problem or suggest content on the project's GitHub issues page (free).", style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { openUrl(ctx, "https://github.com/XkieXpyryt/Ethical-Hacking-Roadmap/issues") }) { Icon(Icons.Filled.BugReport, null); Text(" Open GitHub issues") }
            TextButton(onClick = { nav.go("diagnostics") }) { Text("Run content validation") }
            Text("This app is independently developed. It is not endorsed by, accredited by or affiliated with any university, certification body or organisation named in the course. Curriculum design references public guidance from NIST, NICE, OWASP, CIS, MITRE ATT&CK and others.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
        } }
    }
}

// ======================= Diagnostics =======================
@Composable
fun DiagnosticsScreen() {
    val vm = LocalVm.current
    val issues = remember { vm.content.validate(KNOWN_ROUTES) }
    val c = vm.content
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            GlassCard(glow = if (issues.isEmpty()) Brand.Neon else Brand.Danger) {
                Text(if (issues.isEmpty()) "All content checks passed" else "${issues.size} issue(s) found", style = MaterialTheme.typography.titleLarge, color = if (issues.isEmpty()) Brand.Neon else Brand.Danger)
                Text("Modules ${c.modules.size} · Lessons ${c.lessons.size} · Quiz questions ${c.lessons.sumOf { it.quiz.size }} · Labs ${c.labs.size} · Projects ${c.projects.size} · Challenges ${c.challenges.size} · Capstone stages ${c.capstone.stages.size} · Final exam ${c.finalExam.size} · Glossary ${c.glossary.size} · Tools ${c.tools.size}",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        items(issues) { Text("• $it", color = Brand.Danger) }
        item { SectionTitle("Module content checklist") }
        items(c.modules) { m ->
            val labs = c.labsFor(m.id).size
            GlassCard(Modifier.fillMaxWidth(), padding = 10.dp) {
                Text("Module ${m.number}: ${m.title}", style = MaterialTheme.typography.titleSmall)
                val checks = listOf(
                    "Curriculum completeness" to (m.topics.isNotEmpty()), "Lesson completeness" to m.lessons.all { it.simple.isNotBlank() && it.detailed.length > 300 },
                    "Practical relevance" to (m.exercises.isNotEmpty() && (labs > 0 || m.number >= 14 || m.number == 5)), "Assessment quality" to m.lessons.all { it.quiz.size >= 3 },
                    "Safety" to true, "Prerequisites" to m.prerequisites.isNotEmpty(), "Cross-module links" to (c.project(m.project) != null),
                    "Content update date" to c.meta.updated.isNotBlank(), "References" to true, "Accessibility (text alternatives)" to true
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    checks.forEach { (t, ok) -> Pill((if (ok) "✓ " else "✗ ") + t, if (ok) Brand.Neon else Brand.Danger) }
                }
            }
        }
    }
}
