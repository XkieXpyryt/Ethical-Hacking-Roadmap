package com.bernportal.ehr.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Loads all bundled course content. Everything here works fully offline. */
class ContentRepository(private val context: Context) {

    private fun read(name: String): String {
        val override = File(context.filesDir, "content/$name")
        if (override.exists()) return override.readText()
        return context.assets.open("content/$name").bufferedReader().use { it.readText() }
    }

    val modules: List<Module> by lazy { Parse.modules(JSONArray(read("modules.json"))) }
    val labs: List<Lab> by lazy { Parse.labs(JSONArray(read("labs.json"))) }
    val projects: List<Project> by lazy { Parse.projects(JSONArray(read("projects.json"))) }
    val challenges: List<Challenge> by lazy { Parse.challenges(JSONArray(read("challenges.json"))) }
    val capstone: Capstone by lazy { Parse.capstone(JSONObject(read("capstone.json"))) }
    val glossary: List<Term> by lazy { Parse.glossary(JSONArray(read("glossary.json"))) }
    val tools: List<ToolRef> by lazy { Parse.tools(JSONArray(read("tools.json"))) }
    val career: Career by lazy { Parse.career(JSONObject(read("career.json"))) }
    val meta: Meta by lazy { Parse.meta(JSONObject(read("meta.json"))) }
    val finalExam: List<QuizQ> by lazy {
        val arr = JSONArray(read("final_exam.json"))
        arr.mapObj { QuizQ.from(it, sourceTitle = "Final examination") }
    }

    val lessons: List<Lesson> by lazy { modules.flatMap { it.lessons } }
    private val lessonMap by lazy { lessons.associateBy { it.id } }
    private val moduleMap by lazy { modules.associateBy { it.id } }

    fun lesson(id: String) = lessonMap[id]
    fun module(id: String) = moduleMap[id]
    fun lab(id: String) = labs.firstOrNull { it.id == id }
    fun project(id: String) = projects.firstOrNull { it.id == id }
    fun challenge(id: String) = challenges.firstOrNull { it.id == id }
    fun tool(id: String) = tools.firstOrNull { it.id == id }
    fun term(name: String) = glossary.firstOrNull { it.term.equals(name, ignoreCase = true) }
    fun labsFor(moduleId: String) = labs.filter { it.module == moduleId }
    fun nextLesson(id: String): Lesson? {
        val i = lessons.indexOfFirst { it.id == id }
        return if (i in 0 until lessons.size - 1) lessons[i + 1] else null
    }

    fun moduleQuestions(moduleId: String): List<QuizQ> {
        val m = module(moduleId) ?: return emptyList()
        return m.lessons.flatMap { it.quiz } + labsFor(moduleId).flatMap { it.questions }
    }

    // ---------- Offline search ----------
    data class SearchItem(
        val type: String, val id: String, val title: String, val snippet: String, val level: String,
        val module: String, val route: String, val hay: String
    )

    val searchIndex: List<SearchItem> by lazy {
        val out = ArrayList<SearchItem>()
        modules.forEach { m ->
            out += SearchItem("Module", m.id, "Module ${m.number}: ${m.title}", m.intro.take(140), "", m.id, "module/${m.id}",
                (m.title + " " + m.intro + " " + m.topics.joinToString(" ")).lowercase())
            m.lessons.forEach { l ->
                out += SearchItem("Lesson", l.id, l.title, l.simple.take(160), l.difficulty, m.id, "lesson/${l.id}",
                    (l.title + " " + l.simple + " " + l.detailed + " " + l.terms.joinToString(" ") + " " + l.mistakes.joinToString(" ")).lowercase())
            }
        }
        labs.forEach { l ->
            out += SearchItem("Lab", l.id, l.title, l.scenario.take(160), levelToDifficulty(l.level), l.module, "lab/${l.id}",
                (l.title + " " + l.category + " " + l.scenario + " " + l.tools.joinToString(" ")).lowercase())
            l.tasks.forEach { t ->
                out += SearchItem("Command", l.id + t.id, t.hint, "Lab: ${l.title} - ${t.text}", levelToDifficulty(l.level), l.module, "lab/${l.id}",
                    (t.hint + " " + t.text).lowercase())
            }
        }
        projects.forEach { p ->
            out += SearchItem("Project", p.id, "Project ${p.number}: ${p.title}", p.summary, "", p.module, "project/${p.id}",
                (p.title + " " + p.summary + " " + p.tasks.joinToString(" ")).lowercase())
        }
        glossary.forEach { g ->
            out += SearchItem("Glossary", g.term, g.term, g.definition, "", g.lesson.take(3), "glossary?term=${android.net.Uri.encode(g.term)}",
                (g.term + " " + g.definition + " " + g.category).lowercase())
        }
        tools.forEach { t ->
            out += SearchItem("Tool", t.id, t.name, t.description, "", "", "toolref/${t.id}",
                (t.name + " " + t.category + " " + t.description + " " + t.uses.joinToString(" ")).lowercase())
            t.commands.forEach { c ->
                out += SearchItem("Command", t.id + c.hashCode(), c, "Tool: ${t.name} (isolated lab use)", "", "", "toolref/${t.id}", (c + " " + t.name).lowercase())
            }
        }
        challenges.forEach { c ->
            out += SearchItem("Challenge", c.id, c.title, c.prompt, c.difficulty, "", "challenge/${c.id}", (c.title + " " + c.kind + " " + c.prompt).lowercase())
        }
        lessons.filter { it.title.contains("Troubleshoot", true) || it.detailed.contains("Troubleshooting", true) }.forEach { l ->
            out += SearchItem("Troubleshooting", "ts_" + l.id, l.title, "Troubleshooting guidance inside this lesson", l.difficulty, l.module, "lesson/${l.id}", ("troubleshooting " + l.title).lowercase())
        }
        out
    }

    fun levelToDifficulty(level: Int) = when (level) { 1, 2 -> "Beginner"; 3 -> "Intermediate"; else -> "Advanced" }

    fun search(query: String, types: Set<String>, difficulty: String?, module: String?): List<SearchItem> {
        val words = query.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return emptyList()
        return searchIndex.asSequence()
            .filter { types.isEmpty() || it.type in types }
            .filter { difficulty == null || it.level.equals(difficulty, true) }
            .filter { module == null || it.module == module }
            .mapNotNull { item ->
                if (words.all { item.hay.contains(it) || item.title.lowercase().contains(it) }) {
                    val score = words.sumOf { w -> (if (item.title.lowercase().contains(w)) 5 else 0) + 1 } +
                        (if (item.type == "Lesson") 2 else 0)
                    score to item
                } else null
            }
            .sortedByDescending { it.first }
            .map { it.second }
            .take(80)
            .toList()
    }

    // ---------- In-app content validation (diagnostics) ----------
    fun validate(knownRoutes: Set<String>): List<String> {
        val issues = ArrayList<String>()
        if (modules.size != 16) issues += "Expected 16 modules, found ${modules.size}"
        if (modules.map { it.number } != (1..16).toList()) issues += "Incorrect module sequence"
        modules.forEach { m ->
            if (m.lessons.isEmpty()) issues += "${m.id}: missing lessons"
            if (project(m.project) == null) issues += "${m.id}: broken project link ${m.project}"
            m.lessons.forEach { l ->
                if (l.simple.isBlank() || l.detailed.isBlank()) issues += "${l.id}: empty content"
                if (l.quiz.size < 3) issues += "${l.id}: missing quiz questions"
                l.quiz.forEach { q -> if (q.answer.any { it !in q.options.indices }) issues += "${l.id}: invalid quiz answer" }
            }
        }
        labs.forEach { lab ->
            if (lab.isTerminal && lab.tasks.isEmpty()) issues += "${lab.id}: missing lab instructions"
            if (!lab.isTerminal && lab.steps.isEmpty()) issues += "${lab.id}: missing lab steps"
            if (module(lab.module) == null) issues += "${lab.id}: broken module link"
            lab.tasks.forEach { t -> if (lab.isTerminal && lab.commands.none { it.task == t.id }) issues += "${lab.id}: task ${t.id} cannot be completed" }
        }
        projects.forEach { p ->
            if (p.tasks.isEmpty() || p.rubric.isEmpty()) issues += "${p.id}: unfinished project"
            p.labs.forEach { if (lab(it) == null) issues += "${p.id}: broken lab link $it" }
        }
        glossary.forEach { g -> if (lesson(g.lesson) == null) issues += "Glossary '${g.term}': broken lesson link" }
        if (finalExam.size != 100) issues += "Final exam should have 100 questions (found ${finalExam.size})"
        if (capstone.stages.size != 17) issues += "Capstone should have 17 stages"
        val required = listOf("home", "learn", "labs", "tutor", "progress", "settings", "glossary", "toolkit", "reports", "career", "certificates")
        required.forEach { if (it !in knownRoutes) issues += "Broken navigation: route '$it' missing" }
        listOf("brand_emblem", "ic3d_learn", "ic3d_glossary", "ic3d_toolkit", "ic3d_settings").forEach {
            if (context.resources.getIdentifier(it, "drawable", context.packageName) == 0) issues += "Missing asset: $it"
        }
        return issues
    }
}
