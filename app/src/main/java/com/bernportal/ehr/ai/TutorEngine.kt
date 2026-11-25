package com.bernportal.ehr.ai

import com.bernportal.ehr.data.ContentRepository
import com.bernportal.ehr.data.Lesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * AI cybersecurity tutor.
 * - Online: calls the learner's OWN free-tier key with Google Gemini or any OpenAI-compatible endpoint (e.g. Groq, OpenRouter, a local Ollama server).
 *   No keys are bundled with the app.
 * - Offline / no key: a clearly-labelled course search assistant answers from the bundled lessons (not AI).
 * - Safety: requests that would facilitate real-world harm are refused locally before any network call, and the system prompt repeats the boundaries.
 */
class TutorEngine(private val content: ContentRepository) {

    data class Turn(val role: String, val text: String)
    data class Config(val provider: String, val model: String, val baseUrl: String, val apiKey: String)

    val modes = listOf(
        "Beginner tutor", "Technical mentor", "Lab assistant", "Coding assistant", "Networking tutor", "Linux tutor",
        "Windows tutor", "Web security tutor", "Exam coach", "Career mentor", "Report-writing assistant"
    )

    // ---------------- Safety boundary ----------------
    private val unsafe = listOf(
        Regex("(hack|break) into (my |his |her |their |someone|a )?(ex|girlfriend|boyfriend|wife|husband|partner|neighbou?r|friend|colleague|boss|school|employer)", RegexOption.IGNORE_CASE),
        Regex("(steal|harvest|grab|dump) (someone'?s |their |people'?s |real )?(password|credential|cookie|session|bank|credit card)", RegexOption.IGNORE_CASE),
        Regex("(write|create|build|make|code).{0,30}(ransomware|keylogger|stealer|botnet|worm|rootkit|crypter|rat\\b|remote access trojan)", RegexOption.IGNORE_CASE),
        Regex("(phishing|phish).{0,30}(campaign|email|page|kit).{0,40}(real|my company|customers|bank|paypal|microsoft|apple|people)", RegexOption.IGNORE_CASE),
        Regex("(evade|bypass|disable|avoid).{0,25}(antivirus|edr|defender|detection|monitoring|soc|av)", RegexOption.IGNORE_CASE),
        Regex("(ddos|dos attack|take down|knock offline).{0,40}(website|server|site|game|school|company)", RegexOption.IGNORE_CASE),
        Regex("(hide|stay hidden|persist|persistence).{0,30}(undetected|without (them|anyone) knowing|stealth)", RegexOption.IGNORE_CASE),
        Regex("(spy on|track|monitor) (my |a |someone'?s )?(partner|wife|husband|girlfriend|boyfriend|child'?s phone|employee'?s personal)", RegexOption.IGNORE_CASE),
    )

    fun safetyCheck(text: String): String? {
        if (unsafe.none { it.containsMatchIn(text) }) return null
        return "I can't help with that. It could facilitate harm to real people or systems you are not authorised to test, which is illegal under laws such as the UK Computer Misuse Act 1990 and similar laws worldwide.\n\n" +
            "Here is what we can do instead:\n" +
            "• Practise the concept safely in the built-in simulated labs (Labs tab).\n" +
            "• Study how defenders detect and prevent it (each lab has a Defensive Lesson).\n" +
            "• Use legal practice platforms such as TryHackMe, Hack The Box, PortSwigger Web Security Academy or OWASP Juice Shop on your own machine.\n" +
            "• Review Module 5, Lesson 5: Ethics, Law and Rules of Engagement.\n\n" +
            "Would you like me to explain the defensive side of this topic instead?"
    }

    private fun systemPrompt(mode: String, lesson: Lesson?, learnerLevel: String): String = buildString {
        append("You are the AI cybersecurity tutor inside the 'Ethical Hacking Roadmap 2026/2027' learning app by Bernportal System Innovations. ")
        append("Current mode: $mode. Learner level: $learnerLevel. ")
        append("Teaching behaviour: determine the learner's level; explain clearly; break complex topics into steps; use examples; ask one follow-up knowledge question when appropriate; ")
        append("encourage critical thinking; give hints before full solutions for labs and exercises; suggest a safe practical exercise; connect the answer to related lessons; recommend the next learning step. ")
        append("Use UK English. Keep answers focused and well structured. ")
        append("Safety boundaries: support ONLY ethical, authorised cybersecurity education. Do not facilitate attacks on real third-party systems, credential theft, phishing real people, malware or ransomware deployment, ")
        append("destructive activity, stealth intrusion, unauthorised persistence, evasion of security monitoring, unauthorised exploitation or theft of sensitive information. If asked, explain the concern and redirect to simulated labs, defensive analysis or remediation. ")
        append("Never claim an answer is guaranteed, officially approved, or that you have tested a system. ")
        if (lesson != null) {
            append("\nThe learner is studying lesson '${lesson.title}' (module ${lesson.module}). Lesson summary: ${lesson.summary} Key terms: ${lesson.terms.joinToString()}. ")
            append("Prefer explanations consistent with this lesson.")
        }
    }

    suspend fun ask(cfg: Config, mode: String, lesson: Lesson?, learnerLevel: String, history: List<Turn>, question: String): String {
        safetyCheck(question)?.let { return it }
        if (cfg.apiKey.isBlank() && cfg.provider != "custom") return offlineAnswer(question, lesson, reason = "No AI key set")
        return withContext(Dispatchers.IO) {
            try {
                if (cfg.provider == "gemini") gemini(cfg, systemPrompt(mode, lesson, learnerLevel), history, question)
                else openAiCompatible(cfg, systemPrompt(mode, lesson, learnerLevel), history, question)
            } catch (e: Exception) {
                "⚠ The online tutor could not be reached (${e.message?.take(120)}).\n\n" + offlineAnswer(question, lesson, reason = "Connection problem")
            }
        }
    }

    private fun post(url: String, headers: Map<String, String>, body: String): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"; connectTimeout = 15000; readTimeout = 60000; doOutput = true
            setRequestProperty("Content-Type", "application/json")
            headers.forEach { (k, v) -> setRequestProperty(k, v) }
        }
        conn.outputStream.use { it.write(body.toByteArray()) }
        val code = conn.responseCode
        val text = (if (code in 200..299) conn.inputStream else conn.errorStream)?.bufferedReader()?.use { it.readText() } ?: ""
        conn.disconnect()
        if (code !in 200..299) throw IllegalStateException("HTTP $code ${text.take(160)}")
        return text
    }

    private fun gemini(cfg: Config, system: String, history: List<Turn>, q: String): String {
        val contents = JSONArray()
        history.takeLast(10).forEach { t ->
            contents.put(JSONObject().put("role", if (t.role == "user") "user" else "model").put("parts", JSONArray().put(JSONObject().put("text", t.text))))
        }
        contents.put(JSONObject().put("role", "user").put("parts", JSONArray().put(JSONObject().put("text", q))))
        val body = JSONObject()
            .put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", system))))
            .put("contents", contents)
            .put("generationConfig", JSONObject().put("temperature", 0.4).put("maxOutputTokens", 1400))
        val model = cfg.model.ifBlank { "gemini-2.5-flash" }
        val res = JSONObject(post("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent", mapOf("x-goog-api-key" to cfg.apiKey), body.toString()))
        val parts = res.optJSONArray("candidates")?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")
        val sb = StringBuilder()
        if (parts != null) for (i in 0 until parts.length()) sb.append(parts.optJSONObject(i)?.optString("text") ?: "")
        return sb.toString().ifBlank { "The tutor returned an empty answer. Try rephrasing your question." }
    }

    private fun openAiCompatible(cfg: Config, system: String, history: List<Turn>, q: String): String {
        val base = when {
            cfg.baseUrl.isNotBlank() -> cfg.baseUrl.trimEnd('/')
            cfg.provider == "groq" -> "https://api.groq.com/openai/v1"
            cfg.provider == "openrouter" -> "https://openrouter.ai/api/v1"
            else -> "https://api.openai.com/v1"
        }
        val msgs = JSONArray().put(JSONObject().put("role", "system").put("content", system))
        history.takeLast(10).forEach { msgs.put(JSONObject().put("role", if (it.role == "user") "user" else "assistant").put("content", it.text)) }
        msgs.put(JSONObject().put("role", "user").put("content", q))
        val body = JSONObject().put("model", cfg.model.ifBlank { "llama-3.3-70b-versatile" }).put("messages", msgs).put("temperature", 0.4).put("max_tokens", 1400)
        val headers = if (cfg.apiKey.isNotBlank()) mapOf("Authorization" to "Bearer ${cfg.apiKey}") else emptyMap()
        val res = JSONObject(post("$base/chat/completions", headers, body.toString()))
        return res.optJSONArray("choices")?.optJSONObject(0)?.optJSONObject("message")?.optString("content")
            ?.ifBlank { null } ?: "The tutor returned an empty answer."
    }

    /** Offline course assistant: answers only from bundled lessons. Clearly labelled as not AI. */
    fun offlineAnswer(question: String, lesson: Lesson?, reason: String = "Offline"): String {
        val hits = content.search(question, setOf("Lesson", "Glossary"), null, null).take(3)
        val sb = StringBuilder("📚 Offline course assistant ($reason) - answers come from your bundled lessons, not AI.\n\n")
        val glossary = hits.firstOrNull { it.type == "Glossary" }?.let { content.term(it.id) }
        if (glossary != null) sb.append("**${glossary.term}**: ${glossary.definition}\nExample: ${glossary.example}\n\n")
        val l = hits.firstOrNull { it.type == "Lesson" }?.let { content.lesson(it.id) } ?: lesson
        if (l != null) {
            sb.append("From lesson \"${l.title}\":\n${l.simple}\n\nAnalogy: ${l.analogy}\n\nTry this: ${l.practice.firstOrNull() ?: l.challenge}\n\nCheck yourself: ${l.quiz.firstOrNull()?.q ?: ""}")
        } else {
            sb.append("I couldn't find that in the course. Try different keywords, use Search, or connect to the internet and add your AI key in Settings > AI tutor.")
        }
        val more = hits.drop(1).filter { it.type == "Lesson" }.joinToString("\n") { "• ${it.title}" }
        if (more.isNotBlank()) sb.append("\n\nRelated lessons:\n$more")
        return sb.toString()
    }
}
