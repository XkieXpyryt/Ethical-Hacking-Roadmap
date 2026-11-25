package com.bernportal.ehr

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Verifies the bundled offline course content (runs on the JVM against the asset files). */
class ContentTest {
    private fun asset(name: String): String {
        val candidates = listOf(File("src/main/assets/content/$name"), File("app/src/main/assets/content/$name"))
        return candidates.first { it.exists() }.readText()
    }
    @Test fun sixteenModulesInOrder() {
        val m = JSONArray(asset("modules.json"))
        assertEquals(16, m.length())
        for (i in 0 until 16) assertEquals(i + 1, m.getJSONObject(i).getInt("number"))
    }
    @Test fun everyLessonHasContentAndQuiz() {
        val m = JSONArray(asset("modules.json"))
        for (i in 0 until m.length()) {
            val lessons = m.getJSONObject(i).getJSONArray("lessons")
            assertTrue(lessons.length() > 0)
            for (j in 0 until lessons.length()) {
                val l = lessons.getJSONObject(j)
                assertTrue(l.getString("id"), l.getString("simple").isNotBlank() && l.getString("detailed").length > 300)
                assertTrue(l.getString("id"), l.getJSONArray("quiz").length() >= 3)
            }
        }
    }
    @Test fun finalExamHas100() { assertEquals(100, JSONArray(asset("final_exam.json")).length()) }
    @Test fun capstoneHas17Stages() { assertEquals(17, JSONObject(asset("capstone.json")).getJSONArray("stages").length()) }
    @Test fun labRegexesCompile() {
        val labs = JSONArray(asset("labs.json"))
        for (i in 0 until labs.length()) {
            val cmds = labs.getJSONObject(i).getJSONArray("commands")
            for (j in 0 until cmds.length()) Regex(cmds.getJSONObject(j).getString("match"))
        }
    }
}
