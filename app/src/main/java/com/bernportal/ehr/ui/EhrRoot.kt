@file:OptIn(ExperimentalMaterial3Api::class)

package com.bernportal.ehr.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bernportal.ehr.R
import com.bernportal.ehr.data.AppViewModel
import com.bernportal.ehr.ui.components.AppBackground
import com.bernportal.ehr.ui.components.BrandImage
import com.bernportal.ehr.ui.components.ConnectivityChip
import com.bernportal.ehr.ui.components.iconFor
import com.bernportal.ehr.ui.screens.*
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.EhrTheme
import com.bernportal.ehr.ui.theme.LocalMotion
import com.bernportal.ehr.util.Reminders
import kotlinx.coroutines.launch

val LocalNav = compositionLocalOf<NavHostController> { error("No nav") }
val LocalVm = compositionLocalOf<AppViewModel> { error("No vm") }

fun NavHostController.go(route: String) = navigate(route) { launchSingleTop = true }

@Composable
fun EhrRoot() {
    val vm: AppViewModel = viewModel()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val s = settings
    if (s == null) { Box(Modifier.fillMaxSize().background(Brand.Midnight)); return }
    var introDone by rememberSaveable { mutableStateOf(false) }
    val ctx = LocalContext.current
    LaunchedEffect(s.notifications, s.reminderHour) { Reminders.schedule(ctx, s.notifications, s.reminderHour) }
    EhrTheme(s) {
        CompositionLocalProvider(LocalVm provides vm) {
            when {
                !introDone && (s.playIntro || !s.onboarded) -> IntroScreen { introDone = true }
                !s.onboarded -> OnboardingScreen(vm) { }
                else -> MainShell(vm)
            }
        }
    }
}

data class NavItem(val route: String, val label: String, val icon: ImageVector)

val bottomItems = listOf(
    NavItem("home", "Home", Icons.Filled.Home), NavItem("learn", "Learn", Icons.Filled.School), NavItem("labs", "Labs", Icons.Filled.Science),
    NavItem("tutor", "AI Tutor", Icons.Filled.SmartToy), NavItem("progress", "Progress", Icons.Filled.Insights)
)

private val titles = mapOf(
    "home" to "Dashboard", "learn" to "All Courses", "roadmap" to "Learning Roadmap", "labs" to "Lab Centre", "tutor" to "AI Cybersecurity Tutor",
    "progress" to "Progress", "practice" to "Practice Centre", "exams" to "Examination Centre", "achievements" to "Achievements",
    "certificates" to "Certificate Centre", "career" to "Career Centre", "portfolio" to "Portfolio", "toolkit" to "Cybersecurity Toolkit",
    "glossary" to "Glossary", "search" to "Search", "notes" to "Notes & Bookmarks", "revision" to "Revision Centre", "reports" to "Report Builder",
    "settings" to "Settings", "help" to "Help & Support", "diagnostics" to "Content Validation", "capstone" to "Silverline Capstone"
)

@Composable
fun MainShell(vm: AppViewModel) {
    val nav = rememberNavController()
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "home"
    val base = route.substringBefore("/").substringBefore("?")
    val online by vm.online.collectAsStateWithLifecycle()
    val wide = LocalConfiguration.current.screenWidthDp >= 600
    val motion = LocalMotion.current

    CompositionLocalProvider(LocalNav provides nav) {
        ModalNavigationDrawer(drawerState = drawer, drawerContent = {
            AppDrawer(vm, route) { r -> scope.launch { drawer.close() }; nav.go(r) }
        }) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(titles[base] ?: dynamicTitle(vm, route, entry?.arguments?.getString("id")), maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                                ConnectivityChip(online)
                            }
                        },
                        navigationIcon = {
                            if (base in bottomItems.map { it.route } || nav.previousBackStackEntry == null)
                                IconButton(onClick = { scope.launch { drawer.open() } }) { Icon(Icons.Filled.Menu, "Open menu") }
                            else IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                        },
                        actions = {
                            IconButton(onClick = { nav.go("search") }) { Icon(Icons.Filled.Search, "Search everything") }
                            IconButton(onClick = { nav.go("notes") }) { Icon(Icons.Filled.Bookmarks, "Notes and bookmarks") }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.92f))
                    )
                },
                bottomBar = {
                    if (!wide) NavigationBar(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)) {
                        bottomItems.forEach { item ->
                            NavigationBarItem(selected = base == item.route, onClick = {
                                nav.navigate(item.route) { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true }
                            }, icon = { Icon(item.icon, null) }, label = { Text(item.label, maxLines = 1) })
                        }
                    }
                }
            ) { pad ->
                AppBackground(Modifier.padding(pad)) {
                    Row(Modifier.fillMaxSize()) {
                        if (wide) NavigationRail(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)) {
                            Spacer(Modifier.height(8.dp))
                            BrandImage(R.drawable.brand_emblem, 48.dp)
                            bottomItems.forEach { item ->
                                NavigationRailItem(selected = base == item.route, onClick = {
                                    nav.navigate(item.route) { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true }
                                }, icon = { Icon(item.icon, null) }, label = { Text(item.label) })
                            }
                        }
                        NavHost(
                            nav, startDestination = "home", modifier = Modifier.weight(1f),
                            enterTransition = { if (motion.enabled) fadeIn(tween(motion.dur(260))) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(motion.dur(300)), initialOffset = { it / 8 }) else fadeIn(tween(1)) },
                            exitTransition = { fadeOut(tween(motion.dur(180).coerceAtLeast(1))) },
                            popEnterTransition = { if (motion.enabled) fadeIn(tween(motion.dur(260))) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(motion.dur(300)), initialOffset = { it / 8 }) else fadeIn(tween(1)) },
                            popExitTransition = { fadeOut(tween(motion.dur(180).coerceAtLeast(1))) }
                        ) {
                            composable("home") { HomeScreen() }
                            composable("learn") { LearnScreen() }
                            composable("roadmap") { RoadmapScreen() }
                            composable("module/{id}") { ModuleScreen(it.arguments?.getString("id") ?: "m01") }
                            composable("lesson/{id}") { LessonScreen(it.arguments?.getString("id") ?: "m01l1") }
                            composable("quiz/{id}") { QuizScreen(it.arguments?.getString("id") ?: "daily") }
                            composable("exams") { ExamsScreen() }
                            composable("labs?tab={tab}", arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })) { LabsHub(it.arguments?.getInt("tab") ?: 0) }
                            composable("lab/{id}") { LabScreen(it.arguments?.getString("id") ?: "") }
                            composable("practice") { PracticeScreen() }
                            composable("sim/{id}") { SimScreen(it.arguments?.getString("id") ?: "subnet") }
                            composable("project/{id}") { ProjectScreen(it.arguments?.getString("id") ?: "p01") }
                            composable("challenge/{id}") { ChallengeScreen(it.arguments?.getString("id") ?: "c01") }
                            composable("capstone") { CapstoneScreen() }
                            composable("tutor") { TutorScreen() }
                            composable("progress") { ProgressScreen() }
                            composable("achievements") { AchievementsScreen() }
                            composable("certificates") { CertificatesScreen() }
                            composable("career") { CareerScreen() }
                            composable("portfolio") { PortfolioScreen() }
                            composable("toolkit") { ToolkitScreen() }
                            composable("toolref/{id}") { ToolRefScreen(it.arguments?.getString("id") ?: "") }
                            composable("glossary?term={term}", arguments = listOf(navArgument("term") { type = NavType.StringType; defaultValue = "" })) { GlossaryScreen(it.arguments?.getString("term") ?: "") }
                            composable("search") { SearchScreen() }
                            composable("notes") { NotesScreen() }
                            composable("revision") { RevisionScreen() }
                            composable("reports") { ReportsScreen() }
                            composable("report/{id}") { ReportEditor(it.arguments?.getString("id")?.toLongOrNull() ?: 0L) }
                            composable("settings") { SettingsScreen() }
                            composable("help") { HelpScreen() }
                            composable("diagnostics") { DiagnosticsScreen() }
                        }
                    }
                }
            }
        }
    }
}

val KNOWN_ROUTES = setOf("home", "learn", "roadmap", "module", "lesson", "quiz", "exams", "labs", "lab", "practice", "sim", "project", "challenge", "capstone",
    "tutor", "progress", "achievements", "certificates", "career", "portfolio", "toolkit", "toolref", "glossary", "search", "notes", "revision", "reports",
    "report", "settings", "help", "diagnostics")

private fun dynamicTitle(vm: AppViewModel, route: String, id: String?): String = when {
    route.startsWith("module/") -> vm.content.module(id ?: "")?.let { "Module ${it.number}" } ?: "Module"
    route.startsWith("lesson/") -> vm.content.lesson(id ?: "")?.title ?: "Lesson"
    route.startsWith("quiz/") -> "Assessment"
    route.startsWith("lab/") -> vm.content.lab(id ?: "")?.title ?: "Lab"
    route.startsWith("labs") -> "Lab Centre"
    route.startsWith("glossary") -> "Glossary"
    route.startsWith("project/") -> "Project"
    route.startsWith("challenge/") -> "Challenge Arena"
    route.startsWith("toolref/") -> vm.content.tool(id ?: "")?.name ?: "Tool"
    route.startsWith("report/") -> "Report Builder"
    route.startsWith("sim/") -> "Interactive Simulator"
    else -> "Ethical Hacking Roadmap"
}

@Composable
private fun AppDrawer(vm: AppViewModel, current: String, go: (String) -> Unit) {
    var modulesOpen by remember { mutableStateOf(false) }
    val settings by vm.settings.collectAsStateWithLifecycle()
    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 12.dp)) {
            Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                BrandImage(R.drawable.brand_emblem, 64.dp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Bernportal System Innovations", style = MaterialTheme.typography.labelMedium, color = Brand.Gold)
                    Text("Ethical Hacking Roadmap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    Text("2026/2027  ·  ${settings?.name?.ifBlank { "Learner" } ?: "Learner"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            HorizontalDivider()
            fun item(label: String, icon: ImageVector, route: String, img: Int? = null) = Triple(label, icon, route) to img
            val items = listOf(
                item("Dashboard", Icons.Filled.Dashboard, "home"), item("Learning Roadmap", Icons.Filled.Route, "roadmap"),
                item("All Courses", Icons.Filled.School, "learn", R.drawable.ic3d_learn)
            )
            val rest = listOf(
                item("Practice Centre", Icons.Filled.FitnessCenter, "practice"), item("Virtual Labs", Icons.Filled.Science, "labs?tab=0"),
                item("Projects", Icons.Filled.Construction, "labs?tab=1"), item("Challenge Arena", Icons.Filled.SportsMartialArts, "labs?tab=2"),
                item("Silverline Capstone", Icons.Filled.Apartment, "capstone"), item("AI Tutor", Icons.Filled.SmartToy, "tutor", R.drawable.ic3d_support),
                item("Notes & Bookmarks", Icons.Filled.EditNote, "notes", R.drawable.ic3d_notes), item("Revision Centre", Icons.Filled.Style, "revision"),
                item("Toolkit", Icons.Filled.Build, "toolkit", R.drawable.ic3d_toolkit), item("Glossary", Icons.Filled.Translate, "glossary", R.drawable.ic3d_glossary),
                item("Assessments", Icons.Filled.Quiz, "exams"), item("Report Builder", Icons.Filled.Description, "reports"),
                item("Achievements", Icons.Filled.EmojiEvents, "achievements"), item("Certificates", Icons.Filled.WorkspacePremium, "certificates"),
                item("Career Path", Icons.Filled.Work, "career", R.drawable.ic3d_career), item("Portfolio", Icons.Filled.Folder, "portfolio"),
                item("Settings", Icons.Filled.Settings, "settings", R.drawable.ic3d_settings), item("Help & Support", Icons.Filled.Help, "help", R.drawable.ic3d_about)
            )
            @Composable fun Draw(entry: Pair<Triple<String, ImageVector, String>, Int?>) {
                val (t, img) = entry
                NavigationDrawerItem(
                    label = { Text(t.first) }, selected = current.startsWith(t.third.substringBefore("?")) && t.third != "labs?tab=1" && t.third != "labs?tab=2",
                    icon = { if (img != null) BrandImage(img, 28.dp) else Icon(t.second, null) }, onClick = { go(t.third) }
                )
            }
            items.forEach { Draw(it) }
            NavigationDrawerItem(
                label = { Text("Course Modules") }, selected = false, icon = { Icon(Icons.Filled.ViewModule, null) },
                badge = { Icon(if (modulesOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null) }, onClick = { modulesOpen = !modulesOpen }
            )
            if (modulesOpen) vm.content.modules.forEach { m ->
                NavigationDrawerItem(
                    label = { Text("${m.number}. ${m.title}", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    selected = false, icon = { Icon(iconFor(m.icon), null, Modifier.size(18.dp)) }, onClick = { go("module/${m.id}") },
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            rest.forEach { Draw(it) }
            Spacer(Modifier.height(24.dp))
        }
    }
}
