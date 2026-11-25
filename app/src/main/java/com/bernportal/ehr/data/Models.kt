package com.bernportal.ehr.data

import org.json.JSONArray
import org.json.JSONObject

// ---------- JSON helpers ----------
fun JSONArray?.strings(): List<String> {
    if (this == null) return emptyList()
    return (0 until length()).map { optString(it) }
}

fun JSONArray?.ints(): List<Int> {
    if (this == null) return emptyList()
    return (0 until length()).map { optInt(it) }
}

inline fun <T> JSONArray?.mapObj(f: (JSONObject) -> T): List<T> {
    if (this == null) return emptyList()
    val out = ArrayList<T>(length())
    for (i in 0 until length()) out.add(f(getJSONObject(i)))
    return out
}

fun JSONObject.s(key: String): String = optString(key, "")

// ---------- Content models ----------
data class QuizQ(
    val q: String,
    val options: List<String>,
    val answer: List<Int>,
    val explain: String,
    val type: String,
    val artifact: String = "",
    val module: String = "",
    val category: String = "",
    val sourceTitle: String = ""
) {
    val isMulti get() = type == "multi" || answer.size > 1

    companion object {
        fun from(o: JSONObject, module: String = "", sourceTitle: String = "") = QuizQ(
            q = o.s("q"), options = o.optJSONArray("options").strings(), answer = o.optJSONArray("answer").ints(),
            explain = o.s("explain"), type = o.optString("type", "single"), artifact = o.s("artifact"),
            module = o.optString("module", module), category = o.s("category"), sourceTitle = sourceTitle
        )
    }
}

data class Lesson(
    val id: String, val module: String, val order: Int, val title: String, val difficulty: String, val minutes: Int,
    val why: String, val objectives: List<String>, val simple: String, val detailed: String, val analogy: String,
    val example: String, val relevance: String, val flow: List<String>, val practice: List<String>, val challenge: String,
    val mistakes: List<String>, val summary: String, val terms: List<String>, val examTip: String, val quiz: List<QuizQ>
)

data class Module(
    val id: String, val number: Int, val title: String, val icon: String, val intro: String, val objectives: List<String>,
    val prerequisites: List<String>, val lessons: List<Lesson>, val exercises: List<String>, val project: String,
    val completion: String, val revision: List<String>, val topics: List<String>
) {
    val minutes get() = lessons.sumOf { it.minutes }
}

data class LabCommand(val match: Regex, val output: String, val task: String?)
data class LabTask(val id: String, val text: String, val hint: String)
data class LabStep(val text: String, val artifact: String, val question: String, val options: List<String>, val answer: Int, val explain: String)

data class Lab(
    val id: String, val title: String, val category: String, val level: Int, val module: String, val type: String, val minutes: Int,
    val scenario: String, val background: String, val objectives: List<String>, val rules: List<String>, val environment: String,
    val tools: List<String>, val prompt: String, val tasks: List<LabTask>, val commands: List<LabCommand>, val steps: List<LabStep>,
    val reflection: List<String>, val solution: String, val defensive: String, val badge: String, val questions: List<QuizQ>
) {
    val isTerminal get() = type == "terminal"
    val levelName get() = when (level) { 1 -> "Beginner"; 2 -> "Foundation"; 3 -> "Intermediate"; 4 -> "Advanced"; else -> "Professional" }
}

data class Rubric(val criterion: String, val excellent: String, val insufficient: String)
data class Project(
    val id: String, val number: Int, val title: String, val module: String, val summary: String, val objectives: List<String>,
    val tasks: List<String>, val deliverable: String, val rubric: List<Rubric>, val labs: List<String>
)

data class Challenge(
    val id: String, val title: String, val kind: String, val difficulty: String, val minutes: Int, val prompt: String,
    val artifact: String, val question: String, val options: List<String>, val answer: Int, val explain: String, val hints: List<String>
)

data class CapStage(
    val n: Int, val title: String, val category: String, val brief: String, val artifact: String, val question: String,
    val options: List<String>, val answer: Int, val explain: String
)

data class Capstone(val title: String, val intro: String, val company: String, val scope: String, val assets: String, val stages: List<CapStage>)

data class Term(val term: String, val category: String, val definition: String, val example: String, val related: List<String>, val lesson: String, val lab: String)

data class ToolRef(
    val id: String, val name: String, val category: String, val description: String, val purpose: String, val install: String,
    val os: String, val beginner: String, val interface_: String, val uses: List<String>, val practice: List<String>,
    val limitations: List<String>, val mistakes: List<String>, val detection: String, val ethics: String, val lessons: List<String>,
    val labs: List<String>, val alternatives: List<String>, val licence: String, val commands: List<String>
)

data class CareerPath(val title: String, val summary: String, val skills: List<String>, val modules: List<String>, val entry: String)
data class Cert(val name: String, val provider: String, val level: String, val format: String, val modules: List<String>, val url: String)
data class InterviewQ(val q: String, val a: String)
data class Career(val paths: List<CareerPath>, val certs: List<Cert>, val interview: List<InterviewQ>)
data class UpdateEntry(val version: String, val date: String, val notes: String)
data class Meta(val courseVersion: String, val contentVersion: String, val updated: String, val history: List<UpdateEntry>)

// ---------- Parsers ----------
object Parse {
    fun modules(arr: JSONArray): List<Module> = arr.mapObj { m ->
        val mid = m.s("id")
        Module(
            id = mid, number = m.optInt("number"), title = m.s("title"), icon = m.s("icon"), intro = m.s("intro"),
            objectives = m.optJSONArray("objectives").strings(), prerequisites = m.optJSONArray("prerequisites").strings(),
            lessons = m.optJSONArray("lessons").mapObj { l ->
                val title = l.s("title")
                Lesson(
                    id = l.s("id"), module = mid, order = l.optInt("order"), title = title, difficulty = l.s("difficulty"),
                    minutes = l.optInt("minutes", 20), why = l.s("why"), objectives = l.optJSONArray("objectives").strings(),
                    simple = l.s("simple"), detailed = l.s("detailed"), analogy = l.s("analogy"), example = l.s("example"),
                    relevance = l.s("relevance"), flow = l.optJSONArray("flow").strings(), practice = l.optJSONArray("practice").strings(),
                    challenge = l.s("challenge"), mistakes = l.optJSONArray("mistakes").strings(), summary = l.s("summary"),
                    terms = l.optJSONArray("terms").strings(), examTip = l.s("examTip"),
                    quiz = l.optJSONArray("quiz").mapObj { QuizQ.from(it, mid, title) }
                )
            },
            exercises = m.optJSONArray("exercises").strings(), project = m.s("project"), completion = m.s("completion"),
            revision = m.optJSONArray("revision").strings(), topics = m.optJSONArray("topics").strings()
        )
    }

    fun labs(arr: JSONArray): List<Lab> = arr.mapObj { l ->
        Lab(
            id = l.s("id"), title = l.s("title"), category = l.s("category"), level = l.optInt("level", 1), module = l.s("module"),
            type = l.s("type"), minutes = l.optInt("minutes", 20), scenario = l.s("scenario"), background = l.s("background"),
            objectives = l.optJSONArray("objectives").strings(), rules = l.optJSONArray("rules").strings(), environment = l.s("environment"),
            tools = l.optJSONArray("tools").strings(), prompt = l.s("prompt"),
            tasks = l.optJSONArray("tasks").mapObj { LabTask(it.s("id"), it.s("text"), it.s("hint")) },
            commands = l.optJSONArray("commands").mapObj {
                val rx = try { Regex(it.s("match")) } catch (e: Exception) { Regex("^\\b\\B$") }
                LabCommand(rx, it.s("output"), it.optString("task").takeIf { t -> t.isNotBlank() && t != "null" })
            },
            steps = l.optJSONArray("steps").mapObj {
                LabStep(it.s("text"), it.s("artifact"), it.s("question"), it.optJSONArray("options").strings(), it.optInt("answer"), it.s("explain"))
            },
            reflection = l.optJSONArray("reflection").strings(), solution = l.s("solution"), defensive = l.s("defensive"),
            badge = l.s("badge"), questions = l.optJSONArray("questions").mapObj { QuizQ.from(it, l.s("module"), l.s("title")) }
        )
    }

    fun projects(arr: JSONArray) = arr.mapObj { p ->
        Project(
            p.s("id"), p.optInt("number"), p.s("title"), p.s("module"), p.s("summary"), p.optJSONArray("objectives").strings(),
            p.optJSONArray("tasks").strings(), p.s("deliverable"),
            p.optJSONArray("rubric").mapObj { Rubric(it.s("criterion"), it.s("excellent"), it.s("insufficient")) },
            p.optJSONArray("labs").strings()
        )
    }

    fun challenges(arr: JSONArray) = arr.mapObj { c ->
        Challenge(
            c.s("id"), c.s("title"), c.s("kind"), c.s("difficulty"), c.optInt("minutes", 3), c.s("prompt"), c.s("artifact"),
            c.s("question"), c.optJSONArray("options").strings(), c.optInt("answer"), c.s("explain"), c.optJSONArray("hints").strings()
        )
    }

    fun capstone(o: JSONObject) = Capstone(
        o.s("title"), o.s("intro"), o.s("company"), o.s("scope"), o.s("assets"),
        o.optJSONArray("stages").mapObj {
            CapStage(it.optInt("n"), it.s("title"), it.s("category"), it.s("brief"), it.s("artifact"), it.s("question"),
                it.optJSONArray("options").strings(), it.optInt("answer"), it.s("explain"))
        }
    )

    fun glossary(arr: JSONArray) = arr.mapObj {
        Term(it.s("term"), it.s("category"), it.s("definition"), it.s("example"), it.optJSONArray("related").strings(), it.s("lesson"), it.s("lab"))
    }

    fun tools(arr: JSONArray) = arr.mapObj {
        ToolRef(
            it.s("id"), it.s("name"), it.s("category"), it.s("description"), it.s("purpose"), it.s("install"), it.s("os"), it.s("beginner"),
            it.s("interface"), it.optJSONArray("uses").strings(), it.optJSONArray("practice").strings(), it.optJSONArray("limitations").strings(),
            it.optJSONArray("mistakes").strings(), it.s("detection"), it.s("ethics"), it.optJSONArray("lessons").strings(),
            it.optJSONArray("labs").strings(), it.optJSONArray("alternatives").strings(), it.s("licence"), it.optJSONArray("commands").strings()
        )
    }

    fun career(o: JSONObject) = Career(
        o.optJSONArray("paths").mapObj { CareerPath(it.s("title"), it.s("summary"), it.optJSONArray("skills").strings(), it.optJSONArray("modules").strings(), it.s("entry")) },
        o.optJSONArray("certs").mapObj { Cert(it.s("name"), it.s("provider"), it.s("level"), it.s("format"), it.optJSONArray("modules").strings(), it.s("url")) },
        o.optJSONArray("interview").mapObj { InterviewQ(it.s("q"), it.s("a")) }
    )

    fun meta(o: JSONObject) = Meta(
        o.s("courseVersion"), o.s("contentVersion"), o.s("updated"),
        o.optJSONArray("history").mapObj { UpdateEntry(it.s("version"), it.s("date"), it.s("notes")) }
    )
}
