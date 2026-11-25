package com.bernportal.ehr.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

val Context.dataStore by preferencesDataStore("settings")

data class Settings(
    val onboarded: Boolean = false,
    val name: String = "",
    val level: String = "Complete beginner",
    val goals: Set<String> = emptySet(),
    val pace: String = "Standard",
    val dailyMinutes: Int = 30,
    val theme: String = "Midnight",
    val animation: String = "Full",
    val effects3d: Boolean = true,
    val playIntro: Boolean = true,
    val fontScale: Float = 1f,
    val dyslexia: Boolean = false,
    val highContrast: Boolean = false,
    val simpleLanguage: Boolean = true,
    val notifications: Boolean = true,
    val reminderHour: Int = 19,
    val sound: Boolean = false,
    val haptics: Boolean = true,
    val aiProvider: String = "gemini",
    val aiModel: String = "gemini-2.5-flash",
    val aiBaseUrl: String = "",
    val tutorMode: String = "Beginner tutor",
    val freeExplore: Boolean = false,
    val language: String = "English (UK)",
    val cvJson: String = "",
    val targetRole: String = "Penetration Tester",
    val interviewSessions: Int = 0
) {
    val reducedMotion get() = animation != "Full"
    val noMotion get() = animation == "Off"
}

class Prefs(private val context: Context) {
    private object K {
        val onboarded = booleanPreferencesKey("onboarded"); val name = stringPreferencesKey("name"); val level = stringPreferencesKey("level")
        val goals = stringPreferencesKey("goals"); val pace = stringPreferencesKey("pace"); val daily = intPreferencesKey("daily")
        val theme = stringPreferencesKey("theme"); val anim = stringPreferencesKey("anim"); val fx3d = booleanPreferencesKey("fx3d")
        val intro = booleanPreferencesKey("intro"); val font = floatPreferencesKey("font"); val dys = booleanPreferencesKey("dys")
        val hc = booleanPreferencesKey("hc"); val simple = booleanPreferencesKey("simple"); val notif = booleanPreferencesKey("notif")
        val hour = intPreferencesKey("hour"); val sound = booleanPreferencesKey("sound"); val haptics = booleanPreferencesKey("haptics")
        val provider = stringPreferencesKey("provider"); val model = stringPreferencesKey("model"); val base = stringPreferencesKey("base")
        val mode = stringPreferencesKey("mode"); val explore = booleanPreferencesKey("explore"); val lang = stringPreferencesKey("lang")
        val cv = stringPreferencesKey("cv"); val role = stringPreferencesKey("role"); val iv = intPreferencesKey("iv")
    }

    val flow: Flow<Settings> = context.dataStore.data.map { p ->
        Settings(
            onboarded = p[K.onboarded] ?: false, name = p[K.name] ?: "", level = p[K.level] ?: "Complete beginner",
            goals = (p[K.goals] ?: "").split("|").filter { it.isNotBlank() }.toSet(), pace = p[K.pace] ?: "Standard",
            dailyMinutes = p[K.daily] ?: 30, theme = p[K.theme] ?: "Midnight", animation = p[K.anim] ?: "Full",
            effects3d = p[K.fx3d] ?: true, playIntro = p[K.intro] ?: true, fontScale = p[K.font] ?: 1f, dyslexia = p[K.dys] ?: false,
            highContrast = p[K.hc] ?: false, simpleLanguage = p[K.simple] ?: true, notifications = p[K.notif] ?: true,
            reminderHour = p[K.hour] ?: 19, sound = p[K.sound] ?: false, haptics = p[K.haptics] ?: true,
            aiProvider = p[K.provider] ?: "gemini", aiModel = p[K.model] ?: "gemini-2.5-flash", aiBaseUrl = p[K.base] ?: "",
            tutorMode = p[K.mode] ?: "Beginner tutor", freeExplore = p[K.explore] ?: false, language = p[K.lang] ?: "English (UK)",
            cvJson = p[K.cv] ?: "", targetRole = p[K.role] ?: "Penetration Tester", interviewSessions = p[K.iv] ?: 0
        )
    }

    suspend fun update(f: (Settings) -> Settings, current: Settings) {
        val s = f(current)
        context.dataStore.edit { p ->
            p[K.onboarded] = s.onboarded; p[K.name] = s.name; p[K.level] = s.level; p[K.goals] = s.goals.joinToString("|")
            p[K.pace] = s.pace; p[K.daily] = s.dailyMinutes; p[K.theme] = s.theme; p[K.anim] = s.animation; p[K.fx3d] = s.effects3d
            p[K.intro] = s.playIntro; p[K.font] = s.fontScale; p[K.dys] = s.dyslexia; p[K.hc] = s.highContrast; p[K.simple] = s.simpleLanguage
            p[K.notif] = s.notifications; p[K.hour] = s.reminderHour; p[K.sound] = s.sound; p[K.haptics] = s.haptics
            p[K.provider] = s.aiProvider; p[K.model] = s.aiModel; p[K.base] = s.aiBaseUrl; p[K.mode] = s.tutorMode
            p[K.explore] = s.freeExplore; p[K.lang] = s.language; p[K.cv] = s.cvJson; p[K.role] = s.targetRole; p[K.iv] = s.interviewSessions
        }
    }

    suspend fun clear() { context.dataStore.edit { it.clear() } }

    @Suppress("unused") private fun Preferences.dummy() = Unit
}

/** Stores secrets (the learner's own AI API key) encrypted with an Android Keystore AES-GCM key. Never hardcoded, never backed up. */
class SecureStore(context: Context) {
    private val sp = context.getSharedPreferences("secure_keys", Context.MODE_PRIVATE)
    private val alias = "ehr_ai_key"

    private fun key(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getEntry(alias, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        gen.init(
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256).build()
        )
        return gen.generateKey()
    }

    fun put(name: String, value: String) {
        if (value.isBlank()) { sp.edit().remove(name).apply(); return }
        val c = Cipher.getInstance("AES/GCM/NoPadding"); c.init(Cipher.ENCRYPT_MODE, key())
        val enc = c.doFinal(value.toByteArray())
        sp.edit().putString(name, Base64.encodeToString(c.iv, Base64.NO_WRAP) + ":" + Base64.encodeToString(enc, Base64.NO_WRAP)).apply()
    }

    fun get(name: String): String {
        val raw = sp.getString(name, null) ?: return ""
        return try {
            val (iv, data) = raw.split(":")
            val c = Cipher.getInstance("AES/GCM/NoPadding")
            c.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, Base64.decode(iv, Base64.NO_WRAP)))
            String(c.doFinal(Base64.decode(data, Base64.NO_WRAP)))
        } catch (e: Exception) { "" }
    }

    fun clear() { sp.edit().clear().apply() }
}
