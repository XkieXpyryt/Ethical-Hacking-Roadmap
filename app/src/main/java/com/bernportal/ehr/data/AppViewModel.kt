package com.bernportal.ehr.data

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bernportal.ehr.EhrApp
import com.bernportal.ehr.ai.TutorEngine
import com.bernportal.ehr.data.db.Attempt
import com.bernportal.ehr.data.db.Bookmark
import com.bernportal.ehr.data.db.CapAnswer
import com.bernportal.ehr.data.db.Certificate
import com.bernportal.ehr.data.db.ChallengeResult
import com.bernportal.ehr.data.db.ChatMsg
import com.bernportal.ehr.data.db.ChatSession
import com.bernportal.ehr.data.db.Finding
import com.bernportal.ehr.data.db.Flashcard
import com.bernportal.ehr.data.db.LabResult
import com.bernportal.ehr.data.db.LessonDone
import com.bernportal.ehr.data.db.Note
import com.bernportal.ehr.data.db.ProjectState
import com.bernportal.ehr.data.db.Report
import com.bernportal.ehr.data.db.StudyDay
import com.bernportal.ehr.data.db.Viewed
import com.bernportal.ehr.data.db.pct
import com.bernportal.ehr.util.Reminders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Badge(val id: String, val title: String, val desc: String, val earned: Boolean, val progress: Float)
data class Skill(val name: String, val value: Float, val modules: List<String>)

data class UserData(
    val lessonsDone: Set<String> = emptySet(),
    val attempts: List<Attempt> = emptyList(),
    val labs: Map<String, LabResult> = emptyMap(),
    val projects: Map<String, ProjectState> = emptyMap(),
    val challenges: Map<String, ChallengeResult> = emptyMap(),
    val capstone: Map<Int, CapAnswer> = emptyMap(),
    val certificates: List<Certificate> = emptyList(),
    val days: List<StudyDay> = emptyList(),
    val notes: List<Note> = emptyList(),
    val bookmarks: List<Bookmark> = emptyList(),
    val cards: List<Flashcard> = emptyList(),
    val viewed: List<Viewed> = emptyList(),
    val reports: List<Report> = emptyList(),
    val loaded: Boolean = false
)

private data class P1(val a: List<LessonDone>, val b: List<Attempt>, val c: List<LabResult>, val d: List<ProjectState>, val e: List<ChallengeResult>)
private data class P2(val a: List<CapAnswer>, val b: List<Certificate>, val c: List<StudyDay>, val d: List<Note>, val e: List<Bookmark>)
private data class P3(val a: List<Flashcard>, val b: List<Viewed>, val c: List<Report>)

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val ehr = app as EhrApp
    val content = ehr.content
    val tutorEngine: TutorEngine = ehr.tutor
    private val dao = ehr.db.dao()
    val online: StateFlow<Boolean> = ehr.connectivity.online

    val settings: StateFlow<Settings?> = ehr.prefs.flow.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val user: StateFlow<UserData> = combine(
        combine(dao.lessonsDone(), dao.attempts(), dao.labResults(), dao.projectStates(), dao.challengeResults()) { a, b, c, d, e -> P1(a, b, c, d, e) },
        combine(dao.capAnswers(), dao.certificates(), dao.studyDays(), dao.notes(), dao.bookmarks()) { a, b, c, d, e -> P2(a, b, c, d, e) },
        combine(dao.flashcards(), dao.viewed(), dao.reports()) { a, b, c -> P3(a, b, c) }
    ) { p1, p2, p3 ->
        UserData(
            p1.a.map { it.lessonId }.toSet(), p1.b, p1.c.associateBy { it.labId }, p1.d.associateBy { it.projectId }, p1.e.associateBy { it.challengeId },
            p2.a.associateBy { it.stage }, p2.b, p2.c, p2.d, p2.e, p3.a, p3.b, p3.c, true
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UserData())

    private val s get() = settings.value ?: Settings()

    // ================= Settings =================
    fun updateSettings(f: (Settings) -> Settings) = viewModelScope.launch {
        val cur = settings.value ?: Settings()
        ehr.prefs.update(f, cur)
        val n = f(cur)
        if (n.notifications != cur.notifications || n.reminderHour != cur.reminderHour) Reminders.schedule(getApplication(), n.notifications, n.reminderHour)
    }

    fun aiKey(): String = ehr.secure.get("ai_key")
    fun setAiKey(v: String) = ehr.secure.put("ai_key", v.trim())

    // ================= Progress logic =================
    fun bestExam(u: UserData, moduleId: String): Int? =
        u.attempts.filter { it.scope == "module:$moduleId" || it.scope == "timed:$moduleId" }.maxOfOrNull { it.pct }

    fun modulePassed(u: UserData, moduleId: String) = (bestExam(u, moduleId) ?: 0) >= 70

    fun lessonFraction(u: UserData, m: Module) = if (m.lessons.isEmpty()) 0f else m.lessons.count { it.id in u.lessonsDone }.toFloat() / m.lessons.size

    fun moduleProgress(u: UserData, m: Module) = 0.6f * lessonFraction(u, m) + 0.4f * ((bestExam(u, m.id) ?: 0) / 100f).coerceAtMost(1f)

    fun placementLevel(level: String) = when (level) {
        "IT student" -> 2; "IT support professional", "Networking professional" -> 3
        "Software developer" -> 4; "Experienced security practitioner" -> 8; else -> 1
    }

    fun isUnlocked(u: UserData, m: Module): Boolean {
        if (m.number == 1 || s.freeExplore || m.number <= placementLevel(s.level) || m.number >= 15) return true
        val prev = content.modules.getOrNull(m.number - 2) ?: return true
        return modulePassed(u, prev.id)
    }

    fun finalUnlocked(u: UserData) = s.freeExplore || content.modules.all { modulePassed(u, it.id) }

    fun courseCompletion(u: UserData) = content.modules.map { moduleProgress(u, it) }.average().toFloat()

    fun xp(u: UserData): Int {
        val passedScopes = u.attempts.filter { it.pct >= 70 }.map { it.scope }.toSet().size
        val capCorrect = u.capstone.values.count { it.correct }
        val finalBonus = if (u.attempts.any { it.scope == "final" && it.pct >= 70 }) 300 else 0
        return u.lessonsDone.size * 20 + passedScopes * 30 + u.labs.size * 50 + u.projects.values.count { it.completed } * 100 +
            u.challenges.values.count { it.correct } * 15 + capCorrect * 20 + finalBonus
    }

    val levelNames = listOf("Recruit", "Explorer", "Apprentice", "Analyst", "Operator", "Specialist", "Professional", "Expert", "Elite", "Master")
    fun level(xp: Int) = 1 + xp / 500
    fun levelName(xp: Int) = levelNames[(level(xp) - 1).coerceAtMost(levelNames.size - 1)]

    private val dayFmt = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
    fun today(): String = dayFmt.format(Date())

    fun streak(u: UserData): Int {
        val set = u.days.filter { it.minutes > 0 }.map { it.day }.toSet()
        val cal = Calendar.getInstance()
        if (dayFmt.format(cal.time) !in set) cal.add(Calendar.DAY_OF_MONTH, -1)
        var n = 0
        while (dayFmt.format(cal.time) in set) { n++; cal.add(Calendar.DAY_OF_MONTH, -1) }
        return n
    }

    fun studyMinutes(u: UserData) = u.days.sumOf { it.minutes }

    /** Module -> percentage from lesson quizzes, module exams and final exam breakdowns. */
    fun modulePerformance(u: UserData): Map<String, Int> {
        val acc = HashMap<String, Pair<Int, Int>>()
        fun add(m: String, sc: Int, tot: Int) { val p = acc[m] ?: (0 to 0); acc[m] = (p.first + sc) to (p.second + tot) }
        u.attempts.forEach { a ->
            when {
                a.scope.startsWith("lesson:") -> content.lesson(a.scope.removePrefix("lesson:"))?.let { add(it.module, a.score, a.total) }
                a.scope.startsWith("module:") || a.scope.startsWith("timed:") -> add(a.scope.substringAfter(":"), a.score, a.total)
            }
            if (a.breakdown.isNotBlank() && !a.scope.startsWith("module:") && !a.scope.startsWith("timed:")) {
                a.breakdown.split(",").forEach { part ->
                    val m = part.substringBefore(":"); val r = part.substringAfter(":").split("/")
                    if (r.size == 2 && !a.scope.startsWith("lesson:")) add(m, r[0].toIntOrNull() ?: 0, r[1].toIntOrNull() ?: 0)
                }
            }
        }
        return acc.filter { it.value.second > 0 }.mapValues { it.value.first * 100 / it.value.second }
    }

    fun weakModules(u: UserData) = modulePerformance(u).filter { it.value < 70 }.keys.mapNotNull { content.module(it) }.sortedBy { it.number }
    fun strongModules(u: UserData) = modulePerformance(u).filter { it.value >= 85 }.keys.mapNotNull { content.module(it) }.sortedBy { it.number }

    fun skills(u: UserData): List<Skill> {
        val areas = listOf(
            "Systems" to listOf("m01", "m03"), "Networking" to listOf("m02", "m07"), "Programming" to listOf("m04"),
            "Security core" to listOf("m05", "m08"), "Recon & scanning" to listOf("m06", "m07"), "Web & API" to listOf("m09"),
            "Infrastructure testing" to listOf("m10", "m11", "m12", "m13"), "Reporting & career" to listOf("m14", "m15", "m16")
        )
        return areas.map { (name, mods) ->
            val v = mods.mapNotNull { content.module(it) }.map { moduleProgress(u, it) }.average().toFloat()
            Skill(name, v, mods)
        }
    }

    fun careerReadiness(u: UserData): Int {
        val labs = u.labs.size.toFloat() / content.labs.size
        val projects = u.projects.values.count { it.completed } / 14f
        val final = (u.attempts.filter { it.scope == "final" }.maxOfOrNull { it.pct } ?: 0) / 100f
        val extras = (if (s.cvJson.isNotBlank()) 0.5f else 0f) + (if (s.interviewSessions > 0) 0.5f else 0f)
        return ((courseCompletion(u) * 40) + labs * 20 + projects * 25 + final * 10 + extras * 5).toInt().coerceIn(0, 100)
    }

    fun nextLesson(u: UserData): Lesson? {
        val lastViewed = u.viewed.firstOrNull { it.route.startsWith("lesson/") }?.let { content.lesson(it.refId) }
        if (lastViewed != null && lastViewed.id !in u.lessonsDone) return lastViewed
        return content.lessons.firstOrNull { it.id !in u.lessonsDone && isUnlocked(u, content.module(it.module)!!) }
    }

    fun badges(u: UserData): List<Badge> {
        val named = listOf(
            "m01" to "Computer Explorer", "m02" to "Network Navigator", "m03" to "Linux Apprentice", "m04" to "Python Builder",
            "m05" to "Security Defender", "m06" to "Recon Analyst", "m07" to "Scan Specialist", "m08" to "Vulnerability Investigator",
            "m09" to "Web Security Apprentice", "m10" to "Ethical Hacker", "m11" to "Wireless Warden", "m12" to "Safe Validator",
            "m13" to "Advanced Tester", "m14" to "Report Master", "m15" to "Lab Veteran", "m16" to "Career Ready"
        )
        val list = ArrayList<Badge>()
        named.forEach { (mid, title) ->
            val m = content.module(mid)!!
            list += Badge("mod_$mid", title, "Pass the Module ${m.number} assessment (${m.title})", modulePassed(u, mid), moduleProgress(u, m))
        }
        val finalPassed = u.attempts.any { it.scope == "final" && it.pct >= 70 }
        list += Badge("pro", "Cybersecurity Professional", "Pass the 100-question final examination", finalPassed, if (finalPassed) 1f else courseCompletion(u))
        val st = streak(u)
        list += Badge("streak7", "7-Day Streak", "Study 7 days in a row", st >= 7, (st / 7f).coerceAtMost(1f))
        list += Badge("streak30", "30-Day Streak", "Study 30 days in a row", st >= 30, (st / 30f).coerceAtMost(1f))
        list += Badge("labs5", "Lab Rat", "Complete 5 labs", u.labs.size >= 5, (u.labs.size / 5f).coerceAtMost(1f))
        list += Badge("labsAll", "Lab Legend", "Complete every lab", u.labs.size >= content.labs.size, u.labs.size.toFloat() / content.labs.size)
        val ch = u.challenges.values.count { it.correct }
        list += Badge("arena10", "Arena Challenger", "Solve 10 Challenge Arena problems", ch >= 10, (ch / 10f).coerceAtMost(1f))
        val pj = u.projects.values.count { it.completed }
        list += Badge("proj3", "Builder", "Complete 3 projects", pj >= 3, (pj / 3f).coerceAtMost(1f))
        val capDone = u.capstone.size >= content.capstone.stages.size
        list += Badge("capstone", "Silverline Consultant", "Finish the Silverline capstone", capDone, u.capstone.size.toFloat() / content.capstone.stages.size)
        list += Badge("notes", "Note Taker", "Write 10 notes", u.notes.size >= 10, (u.notes.size / 10f).coerceAtMost(1f))
        content.labs.forEach { lab -> list += Badge("lab_${lab.id}", lab.badge, "Complete lab: ${lab.title}", lab.id in u.labs, if (lab.id in u.labs) 1f else 0f) }
        return list.distinctBy { it.title }
    }

    // ================= Actions =================
    fun addStudyMinutes(min: Int) {
        if (min <= 0) return
        viewModelScope.launch {
            val d = today()
            val cur = dao.studyDay(d)
            dao.upsertDay(StudyDay(d, (cur?.minutes ?: 0) + min.coerceAtMost(120)))
        }
    }

    fun markViewed(refId: String, title: String, route: String) = viewModelScope.launch { dao.upsertViewed(Viewed(refId, title, route, System.currentTimeMillis())) }

    fun completeLesson(l: Lesson) = viewModelScope.launch {
        dao.markLesson(LessonDone(l.id, l.module, System.currentTimeMillis()))
        val d = today(); val cur = dao.studyDay(d)
        if (cur == null) dao.upsertDay(StudyDay(d, 1))
        checkModuleCertificate(l.module)
    }

    fun recordAttempt(scope: String, title: String, score: Int, total: Int, breakdown: String, minutes: Int) = viewModelScope.launch {
        dao.addAttempt(Attempt(scope = scope, title = title, score = score, total = total, at = System.currentTimeMillis(), breakdown = breakdown, minutes = minutes))
        addStudyMinutes(minutes.coerceAtLeast(1))
        val pct = if (total == 0) 0 else score * 100 / total
        when {
            scope.startsWith("module:") || scope.startsWith("timed:") -> checkModuleCertificate(scope.substringAfter(":"))
            scope.startsWith("lesson:") && pct >= 70 -> content.lesson(scope.removePrefix("lesson:"))?.let {
                dao.markLesson(LessonDone(it.id, it.module, System.currentTimeMillis())); checkModuleCertificate(it.module)
            }
            scope == "final" && pct >= 70 -> issue("course", "final", "Ethical Hacking Roadmap 2026/2027 - Complete Programme",
                pct, "All 16 modules: fundamentals, networking, systems, programming, security, recon, scanning, vulnerability assessment, web, network, wireless, exploitation concepts, advanced testing, reporting")
        }
    }

    private suspend fun checkModuleCertificate(moduleId: String) {
        val m = content.module(moduleId) ?: return
        val doneIds = dao.allLessonDone().map { d -> d.lessonId }.toSet()
        val done = m.lessons.all { it.id in doneIds }
        val best = dao.allAttempts().filter { it.scope == "module:$moduleId" || it.scope == "timed:$moduleId" }.maxOfOrNull { it.pct } ?: 0
        if (done && best >= 70) issue("module", moduleId, "Module ${m.number}: ${m.title}", best, m.objectives.take(3).joinToString("; "))
    }

    private suspend fun issue(kind: String, ref: String, title: String, pct: Int, skills: String) {
        val learner = s.name.ifBlank { "Learner" }
        val hash = MessageDigest.getInstance("SHA-256").digest("$learner|$kind|$ref|${getApplication<Application>().packageName}".toByteArray())
            .joinToString("") { "%02X".format(it) }.take(8)
        dao.addCertificate(Certificate("BSI-EHR-${ref.uppercase()}-$hash", kind, ref, title, learner, pct, skills, System.currentTimeMillis()))
    }

    fun completeLab(lab: Lab, score: Int, hints: Int, minutes: Int, transcript: String) = viewModelScope.launch {
        val prev = user.value.labs[lab.id]
        if (prev == null || score > prev.score) dao.upsertLab(LabResult(lab.id, score, hints, System.currentTimeMillis()))
        addStudyMinutes(minutes)
        if (transcript.isNotBlank()) dao.upsertNote(Note(refId = "lab:${lab.id}", refTitle = "Lab evidence: ${lab.title}", text = transcript.take(6000), at = System.currentTimeMillis()))
    }

    fun saveProject(p: ProjectState) = viewModelScope.launch { dao.upsertProject(p.copy(updatedAt = System.currentTimeMillis())) }

    fun saveChallenge(id: String, correct: Boolean, score: Int) = viewModelScope.launch {
        val prev = user.value.challenges[id]
        if (prev == null || score >= prev.score) dao.upsertChallenge(ChallengeResult(id, correct, score, System.currentTimeMillis()))
        addStudyMinutes(3)
    }

    fun answerCapstone(stage: CapStage, chosen: Int) = viewModelScope.launch {
        dao.upsertCap(CapAnswer(stage.n, chosen, chosen == stage.answer, stage.category))
        val all = dao.allCap()
        if (all.size >= content.capstone.stages.size) {
            val pct = all.count { it.correct } * 100 / all.size
            if (pct >= 70) issue("capstone", "capstone", "Silverline Enterprise Security Assessment (Capstone)", pct,
                "Scoping, reconnaissance, scanning, vulnerability analysis, web assessment, safe validation, risk classification, reporting, retesting")
            if (dao.allReports().none { it.title.startsWith("Silverline Enterprise") }) createCapstoneReport()
        }
    }

    fun resetCapstone() = viewModelScope.launch { dao.resetCapstone() }

    private suspend fun createCapstoneReport() {
        val now = System.currentTimeMillis()
        val id = dao.upsertReport(Report(title = "Silverline Enterprise Security Assessment", client = "Silverline Enterprise Ltd (fictional)",
            dates = "Capstone simulation", scope = content.capstone.scope, methodology = METHODOLOGY, limitations = LIMITATIONS,
            tester = s.name, createdAt = now, updatedAt = now))
        listOf(
            Finding(reportId = id, title = "Internet-facing VPN appliance affected by a known exploited vulnerability (KEV)", severity = "Critical", cvss = "8.1 (raised for KEV + exposure)",
                affected = "vpn.silverline.test (203.0.113.2)", description = "The VPN appliance version matches a vulnerability listed in CISA's Known Exploited Vulnerabilities catalogue.",
                evidence = "Version banner and vendor advisory match (capstone stage 8).", impact = "An internet attacker could gain access to the internal network without valid credentials.",
                remediation = "Apply the vendor patch within 48 hours; review VPN logs for indicators of compromise; enable MFA for VPN users.", owner = "Network team", due = "48 hours"),
            Finding(reportId = id, title = "Broken object level authorisation in shipments API", severity = "High", cvss = "7.7",
                affected = "api.silverline.test /api/shipments/{id} and /label", description = "The API returns shipment records for IDs not owned by the requesting customer.",
                evidence = "Test user 'learner' retrieved shipment 5512 owned by another test customer (redacted).", impact = "Any customer can view other customers' shipment and address data.",
                remediation = "Enforce server-side ownership checks on all shipment endpoints including /label; return 404 for non-owned IDs; add per-role authorisation tests.", status = "Partially fixed", owner = "API team", due = "14 days"),
            Finding(reportId = id, title = "Unauthenticated Jenkins on dev01", severity = "High", cvss = "8.8",
                affected = "10.10.10.30:8080", description = "Jenkins allows anonymous administrative access.", evidence = "Manage Jenkins accessible without login (lab).",
                impact = "An internal attacker could run code on the build server and access stored credentials.",
                remediation = "Enable SSO/MFA authentication, restrict network access to the build subnet, rotate stored credentials, update Jenkins.", owner = "DevOps", due = "7 days"),
            Finding(reportId = id, title = "DMARC policy not enforced", severity = "Medium", cvss = "5.3", affected = "silverline.test",
                description = "DMARC is published with p=none.", evidence = "_dmarc TXT record (stage 4).", impact = "Attackers can more easily spoof Silverline email addresses for phishing.",
                remediation = "Review DMARC reports, then move to p=quarantine and p=reject.", owner = "IT", due = "30 days")
        ).forEach { dao.upsertFinding(it) }
    }

    // ----- Notes / bookmarks / flashcards -----
    fun saveNote(refId: String, refTitle: String, text: String, id: Long = 0) = viewModelScope.launch {
        if (text.isNotBlank()) dao.upsertNote(Note(id, refId, refTitle, text, System.currentTimeMillis()))
    }
    fun deleteNote(id: Long) = viewModelScope.launch { dao.deleteNote(id) }

    fun toggleBookmark(refId: String, title: String, kind: String, route: String) = viewModelScope.launch {
        if (user.value.bookmarks.any { it.refId == refId }) dao.deleteBookmark(refId)
        else dao.upsertBookmark(Bookmark(refId, title, kind, route, System.currentTimeMillis()))
    }

    fun addCard(front: String, back: String, source: String = "manual") = viewModelScope.launch {
        if (front.isNotBlank() && back.isNotBlank()) dao.upsertCard(Flashcard(front = front.take(300), back = back.take(1200), source = source, due = 0))
    }
    fun deleteCard(id: Long) = viewModelScope.launch { dao.deleteCard(id) }

    /** Leitner spaced repetition: boxes 1-5 with 0,1,3,7,14 day intervals. */
    fun reviewCard(c: Flashcard, knew: Boolean) = viewModelScope.launch {
        val box = if (knew) (c.box + 1).coerceAtMost(5) else 1
        val days = listOf(0, 1, 3, 7, 14)[box - 1]
        dao.upsertCard(c.copy(box = box, due = System.currentTimeMillis() + days * 86_400_000L))
    }

    fun generateGlossaryCards(lessonIds: Set<String>) = viewModelScope.launch {
        val existing = user.value.cards.map { it.front.lowercase() }.toSet()
        content.glossary.filter { it.lesson in lessonIds && it.term.lowercase() !in existing }.forEach {
            dao.upsertCard(Flashcard(front = it.term, back = it.definition + "\n\nExample: " + it.example, source = "auto"))
        }
    }

    // ----- Reports -----
    fun findings(reportId: Long) = dao.findings(reportId)
    suspend fun newReport(title: String): Long {
        val now = System.currentTimeMillis()
        return dao.upsertReport(Report(title = title, methodology = METHODOLOGY, limitations = LIMITATIONS, tester = s.name, createdAt = now, updatedAt = now))
    }
    fun saveReport(r: Report) = viewModelScope.launch { dao.upsertReport(r.copy(updatedAt = System.currentTimeMillis())) }
    fun deleteReport(id: Long) = viewModelScope.launch { dao.deleteFindingsFor(id); dao.deleteReport(id) }
    fun saveFinding(f: Finding) = viewModelScope.launch { dao.upsertFinding(f) }
    fun deleteFinding(id: Long) = viewModelScope.launch { dao.deleteFinding(id) }
    suspend fun reportWithFindings(id: Long) = dao.report(id) to dao.findingsList(id)

    // ----- Tutor -----
    val sessions = dao.sessions().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val currentSession = MutableStateFlow(0L)
    val messages: StateFlow<List<ChatMsg>> = currentSession.flatMapLatest { if (it == 0L) flowOf(emptyList()) else dao.messages(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val tutorBusy = MutableStateFlow(false)
    val tutorLesson = MutableStateFlow<String?>(null)
    val tutorDraft = MutableStateFlow("")

    fun newSession(title: String, mode: String) = viewModelScope.launch {
        currentSession.value = dao.addSession(ChatSession(title = title.take(48), mode = mode, at = System.currentTimeMillis()))
    }
    fun deleteSession(id: Long) = viewModelScope.launch { dao.clearMsgs(id); dao.deleteSession(id); if (currentSession.value == id) currentSession.value = 0L }
    fun clearSession() = viewModelScope.launch { dao.clearMsgs(currentSession.value) }

    fun askTutor(question: String, mode: String) = viewModelScope.launch {
        if (question.isBlank()) return@launch
        if (currentSession.value == 0L) currentSession.value = dao.addSession(ChatSession(title = question.take(48), mode = mode, at = System.currentTimeMillis()))
        val sid = currentSession.value
        val history = messages.value.map { TutorEngine.Turn(it.role, it.text) }
        dao.addMsg(ChatMsg(session = sid, role = "user", text = question, at = System.currentTimeMillis()))
        tutorBusy.value = true
        val lesson = tutorLesson.value?.let { content.lesson(it) }
        val answer = if (!online.value) tutorEngine.safetyCheck(question) ?: tutorEngine.offlineAnswer(question, lesson, "Offline")
        else tutorEngine.ask(TutorEngine.Config(s.aiProvider, s.aiModel, s.aiBaseUrl, aiKey()), mode, lesson, s.level, history, question)
        dao.addMsg(ChatMsg(session = sid, role = "assistant", text = answer, at = System.currentTimeMillis()))
        tutorBusy.value = false
    }

    // ----- Quiz builder -----
    data class QuizSpec(val title: String, val questions: List<QuizQ>, val minutes: Int, val instant: Boolean, val pass: Int = 70)

    fun quizSpec(scope: String): QuizSpec {
        val u = user.value
        return when {
            scope.startsWith("lesson:") -> {
                val l = content.lesson(scope.removePrefix("lesson:"))
                QuizSpec("Lesson quiz: ${l?.title}", l?.quiz ?: emptyList(), 0, true)
            }
            scope.startsWith("module:") -> {
                val m = content.module(scope.removePrefix("module:"))!!
                QuizSpec("Module ${m.number} examination", content.moduleQuestions(m.id).shuffled(), 0, true)
            }
            scope.startsWith("timed:") -> {
                val m = content.module(scope.removePrefix("timed:"))!!
                val qs = content.moduleQuestions(m.id).shuffled()
                QuizSpec("Timed exam: Module ${m.number}", qs, (qs.size * 1.2).toInt().coerceAtLeast(5), false)
            }
            scope == "final" -> QuizSpec("Final examination (100 questions)", content.finalExam.shuffled(), 150, false)
            scope == "mock" -> QuizSpec("Mock certification exam", (content.lessons.flatMap { it.quiz } + content.finalExam).shuffled().take(50), 60, false)
            scope == "daily" -> {
                val pool = content.lessons.filter { isUnlocked(u, content.module(it.module)!!) }.flatMap { it.quiz }
                val seed = today().hashCode().toLong()
                QuizSpec("Daily quiz", pool.shuffled(java.util.Random(seed)).take(5), 0, true)
            }
            scope == "revision" -> {
                val weak = weakModules(u).map { it.id }.ifEmpty { content.modules.take(2).map { it.id } }
                QuizSpec("Revision test (weak areas)", weak.flatMap { content.moduleQuestions(it) }.shuffled().take(12), 0, true)
            }
            scope.startsWith("labq:") -> {
                val lab = content.lab(scope.removePrefix("labq:"))
                QuizSpec("Lab knowledge test", lab?.questions ?: emptyList(), 0, true)
            }
            else -> QuizSpec("Quiz", emptyList(), 0, true)
        }
    }

    // ================= Data export / import / deletion =================
    suspend fun exportJson(): String = withContext(Dispatchers.IO) {
        val o = JSONObject()
        o.put("app", "Ethical Hacking Roadmap 2026/2027").put("format", 1).put("exportedAt", System.currentTimeMillis())
        val st = s
        o.put("settings", JSONObject().put("name", st.name).put("level", st.level).put("goals", JSONArray(st.goals.toList())).put("pace", st.pace)
            .put("dailyMinutes", st.dailyMinutes).put("theme", st.theme).put("cv", st.cvJson).put("targetRole", st.targetRole))
        o.put("lessons", JSONArray(dao.allLessonDone().map { JSONObject().put("id", it.lessonId).put("m", it.moduleId).put("at", it.at) }))
        o.put("attempts", JSONArray(dao.allAttempts().map { JSONObject().put("scope", it.scope).put("title", it.title).put("score", it.score).put("total", it.total).put("at", it.at).put("b", it.breakdown).put("min", it.minutes) }))
        o.put("labs", JSONArray(dao.allLabResults().map { JSONObject().put("id", it.labId).put("score", it.score).put("hints", it.hintsUsed).put("at", it.completedAt) }))
        o.put("projects", JSONArray(dao.allProjectStates().map { JSONObject().put("id", it.projectId).put("checked", it.checked).put("notes", it.notes).put("rubric", it.rubric).put("done", it.completed) }))
        o.put("notes", JSONArray(dao.allNotes().map { JSONObject().put("ref", it.refId).put("title", it.refTitle).put("text", it.text).put("at", it.at) }))
        o.put("bookmarks", JSONArray(dao.allBookmarks().map { JSONObject().put("ref", it.refId).put("title", it.title).put("kind", it.kind).put("route", it.route).put("at", it.at) }))
        o.put("cards", JSONArray(dao.allCards().map { JSONObject().put("f", it.front).put("b", it.back).put("box", it.box).put("due", it.due).put("src", it.source) }))
        val reports = dao.allReports()
        o.put("reports", JSONArray(reports.map { r ->
            JSONObject().put("title", r.title).put("client", r.client).put("dates", r.dates).put("scope", r.scope).put("method", r.methodology)
                .put("limits", r.limitations).put("exec", r.execSummary).put("tech", r.techSummary).put("tester", r.tester).put("c", r.createdAt).put("u", r.updatedAt)
                .put("findings", JSONArray(dao.findingsList(r.id).map { f ->
                    JSONObject().put("title", f.title).put("sev", f.severity).put("cvss", f.cvss).put("aff", f.affected).put("desc", f.description).put("ev", f.evidence)
                        .put("imp", f.impact).put("rem", f.remediation).put("status", f.status).put("owner", f.owner).put("due", f.due)
                }))
        }))
        o.put("certificates", JSONArray(dao.allCertificates().map { JSONObject().put("id", it.certId).put("kind", it.kind).put("ref", it.refId).put("title", it.title).put("learner", it.learner).put("pct", it.scorePct).put("skills", it.skills).put("at", it.issuedAt) }))
        o.put("days", JSONArray(dao.allDays().map { JSONObject().put("d", it.day).put("m", it.minutes) }))
        o.put("challenges", JSONArray(dao.allChallenges().map { JSONObject().put("id", it.challengeId).put("ok", it.correct).put("score", it.score).put("at", it.at) }))
        o.put("capstone", JSONArray(dao.allCap().map { JSONObject().put("n", it.stage).put("c", it.chosen).put("ok", it.correct).put("cat", it.category) }))
        o.toString(1)
    }

    suspend fun importJson(text: String): String = withContext(Dispatchers.IO) {
        try {
            val o = JSONObject(text)
            if (o.optString("app") != "Ethical Hacking Roadmap 2026/2027") return@withContext "This file is not an Ethical Hacking Roadmap backup."
            ehr.db.clearAllTables()
            o.optJSONArray("lessons").mapObj { dao.markLesson(LessonDone(it.s("id"), it.s("m"), it.optLong("at"))) }
            o.optJSONArray("attempts").mapObj { dao.addAttempt(Attempt(scope = it.s("scope"), title = it.s("title"), score = it.optInt("score"), total = it.optInt("total"), at = it.optLong("at"), breakdown = it.s("b"), minutes = it.optInt("min"))) }
            o.optJSONArray("labs").mapObj { dao.upsertLab(LabResult(it.s("id"), it.optInt("score"), it.optInt("hints"), it.optLong("at"))) }
            o.optJSONArray("projects").mapObj { dao.upsertProject(ProjectState(it.s("id"), it.s("checked"), it.s("notes"), it.s("rubric"), it.optBoolean("done"))) }
            o.optJSONArray("notes").mapObj { dao.upsertNote(Note(refId = it.s("ref"), refTitle = it.s("title"), text = it.s("text"), at = it.optLong("at"))) }
            o.optJSONArray("bookmarks").mapObj { dao.upsertBookmark(Bookmark(it.s("ref"), it.s("title"), it.s("kind"), it.s("route"), it.optLong("at"))) }
            o.optJSONArray("cards").mapObj { dao.upsertCard(Flashcard(front = it.s("f"), back = it.s("b"), box = it.optInt("box", 1), due = it.optLong("due"), source = it.s("src"))) }
            o.optJSONArray("reports").mapObj { r ->
                val id = dao.upsertReport(Report(title = r.s("title"), client = r.s("client"), dates = r.s("dates"), scope = r.s("scope"), methodology = r.s("method"),
                    limitations = r.s("limits"), execSummary = r.s("exec"), techSummary = r.s("tech"), tester = r.s("tester"), createdAt = r.optLong("c"), updatedAt = r.optLong("u")))
                r.optJSONArray("findings").mapObj { f ->
                    dao.upsertFinding(Finding(reportId = id, title = f.s("title"), severity = f.s("sev"), cvss = f.s("cvss"), affected = f.s("aff"), description = f.s("desc"),
                        evidence = f.s("ev"), impact = f.s("imp"), remediation = f.s("rem"), status = f.s("status"), owner = f.s("owner"), due = f.s("due")))
                }
            }
            o.optJSONArray("certificates").mapObj { dao.addCertificate(Certificate(it.s("id"), it.s("kind"), it.s("ref"), it.s("title"), it.s("learner"), it.optInt("pct"), it.s("skills"), it.optLong("at"))) }
            o.optJSONArray("days").mapObj { dao.upsertDay(StudyDay(it.s("d"), it.optInt("m"))) }
            o.optJSONArray("challenges").mapObj { dao.upsertChallenge(ChallengeResult(it.s("id"), it.optBoolean("ok"), it.optInt("score"), it.optLong("at"))) }
            o.optJSONArray("capstone").mapObj { dao.upsertCap(CapAnswer(it.optInt("n"), it.optInt("c"), it.optBoolean("ok"), it.s("cat"))) }
            o.optJSONObject("settings")?.let { st ->
                val cur = settings.value ?: Settings()
                ehr.prefs.update({ it.copy(name = st.s("name"), level = st.optString("level", it.level), pace = st.optString("pace", it.pace),
                    goals = st.optJSONArray("goals").strings().toSet(), dailyMinutes = st.optInt("dailyMinutes", it.dailyMinutes), cvJson = st.s("cv"),
                    targetRole = st.optString("targetRole", it.targetRole), onboarded = true) }, cur)
            }
            "Backup restored successfully."
        } catch (e: Exception) { "Import failed: ${e.message}" }
    }

    fun resetProgress(done: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val notes = dao.allNotes(); val bms = dao.allBookmarks(); val cards = dao.allCards()
        ehr.db.clearAllTables()
        notes.forEach { dao.upsertNote(it) }; bms.forEach { dao.upsertBookmark(it) }; cards.forEach { dao.upsertCard(it) }
        withContext(Dispatchers.Main) { done() }
    }

    fun deleteAllData(done: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        ehr.db.clearAllTables(); ehr.prefs.clear(); ehr.secure.clear()
        File(getApplication<Application>().filesDir, "content").deleteRecursively()
        Reminders.schedule(getApplication(), false, 19)
        withContext(Dispatchers.Main) { done() }
    }

    /** Optional offline content update pack: a JSON object whose keys are content file names (modules, labs, ...). Progress is keyed by stable IDs and is preserved. */
    suspend fun importContentPack(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val text = getApplication<Application>().contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: return@withContext "Could not read file."
            val o = JSONObject(text)
            val meta = o.optJSONObject("meta") ?: return@withContext "Not a content pack (missing meta)."
            val dir = File(getApplication<Application>().filesDir, "content").apply { mkdirs() }
            val allowed = listOf("modules", "labs", "projects", "challenges", "capstone", "glossary", "tools", "career", "final_exam")
            var n = 0
            allowed.forEach { k -> if (o.has(k)) { File(dir, "$k.json").writeText(o.get(k).toString()); n++ } }
            File(dir, "meta.json").writeText(meta.toString())
            "Installed content pack ${meta.optString("contentVersion")} ($n files). Restart the app to apply - your progress, notes, bookmarks, certificates, lab results and projects are kept."
        } catch (e: Exception) { "Invalid content pack: ${e.message}" }
    }

    fun removeContentPack(): String {
        File(getApplication<Application>().filesDir, "content").deleteRecursively()
        return "Reverted to bundled content. Restart the app to apply."
    }

    companion object {
        const val METHODOLOGY = "Testing followed a structured methodology aligned with PTES, NIST SP 800-115 and the OWASP Web Security Testing Guide: pre-engagement and scoping; reconnaissance; scanning and enumeration; vulnerability analysis; controlled, minimal-impact validation; risk classification using CVSS v3.1 adjusted for exposure, known exploitation (CISA KEV) and asset criticality; reporting; and retesting."
        const val LIMITATIONS = "Testing was time-boxed and represents a point-in-time view. Only in-scope systems were assessed; out-of-scope assets were not tested. Denial-of-service and social engineering were excluded. Findings were validated using minimal-impact techniques; issues requiring destructive testing may not have been identified."
    }
}
