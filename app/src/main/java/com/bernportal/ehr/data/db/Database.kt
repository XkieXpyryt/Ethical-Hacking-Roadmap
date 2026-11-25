package com.bernportal.ehr.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity data class LessonDone(@PrimaryKey val lessonId: String, val moduleId: String, val at: Long)
@Entity data class Attempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, val scope: String, val title: String, val score: Int, val total: Int,
    val at: Long, val breakdown: String = "", val minutes: Int = 0
) { val pct get() = if (total == 0) 0 else score * 100 / total }
@Entity data class LabResult(@PrimaryKey val labId: String, val score: Int, val hintsUsed: Int, val completedAt: Long)
@Entity data class ProjectState(
    @PrimaryKey val projectId: String, val checked: String = "", val notes: String = "", val rubric: String = "",
    val completed: Boolean = false, val updatedAt: Long = 0
)
@Entity data class Note(@PrimaryKey(autoGenerate = true) val id: Long = 0, val refId: String, val refTitle: String, val text: String, val at: Long)
@Entity data class Bookmark(@PrimaryKey val refId: String, val title: String, val kind: String, val route: String, val at: Long)
@Entity data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, val front: String, val back: String, val box: Int = 1,
    val due: Long = 0, val source: String = "manual"
)
@Entity data class Report(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val client: String = "", val dates: String = "",
    val scope: String = "", val methodology: String = "", val limitations: String = "", val execSummary: String = "",
    val techSummary: String = "", val tester: String = "", val createdAt: Long = 0, val updatedAt: Long = 0
)
@Entity data class Finding(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, val reportId: Long, val title: String, val severity: String = "Medium",
    val cvss: String = "", val affected: String = "", val description: String = "", val evidence: String = "",
    val impact: String = "", val remediation: String = "", val status: String = "Open", val owner: String = "", val due: String = ""
)
@Entity data class ChatSession(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val mode: String, val at: Long)
@Entity data class ChatMsg(@PrimaryKey(autoGenerate = true) val id: Long = 0, val session: Long, val role: String, val text: String, val at: Long)
@Entity data class Certificate(
    @PrimaryKey val certId: String, val kind: String, val refId: String, val title: String, val learner: String,
    val scorePct: Int, val skills: String, val issuedAt: Long
)
@Entity data class StudyDay(@PrimaryKey val day: String, val minutes: Int)
@Entity data class ChallengeResult(@PrimaryKey val challengeId: String, val correct: Boolean, val score: Int, val at: Long)
@Entity data class CapAnswer(@PrimaryKey val stage: Int, val chosen: Int, val correct: Boolean, val category: String)
@Entity data class Viewed(@PrimaryKey val refId: String, val title: String, val route: String, val at: Long)

@Dao
interface AppDao {
    @Query("SELECT * FROM LessonDone") fun lessonsDone(): Flow<List<LessonDone>>
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun markLesson(l: LessonDone)

    @Query("SELECT * FROM Attempt ORDER BY at DESC") fun attempts(): Flow<List<Attempt>>
    @Insert suspend fun addAttempt(a: Attempt)

    @Query("SELECT * FROM LabResult") fun labResults(): Flow<List<LabResult>>
    @Upsert suspend fun upsertLab(r: LabResult)

    @Query("SELECT * FROM ProjectState") fun projectStates(): Flow<List<ProjectState>>
    @Upsert suspend fun upsertProject(p: ProjectState)

    @Query("SELECT * FROM Note ORDER BY at DESC") fun notes(): Flow<List<Note>>
    @Upsert suspend fun upsertNote(n: Note)
    @Query("DELETE FROM Note WHERE id = :id") suspend fun deleteNote(id: Long)

    @Query("SELECT * FROM Bookmark ORDER BY at DESC") fun bookmarks(): Flow<List<Bookmark>>
    @Upsert suspend fun upsertBookmark(b: Bookmark)
    @Query("DELETE FROM Bookmark WHERE refId = :refId") suspend fun deleteBookmark(refId: String)

    @Query("SELECT * FROM Flashcard ORDER BY due ASC") fun flashcards(): Flow<List<Flashcard>>
    @Upsert suspend fun upsertCard(c: Flashcard)
    @Query("DELETE FROM Flashcard WHERE id = :id") suspend fun deleteCard(id: Long)

    @Query("SELECT * FROM Report ORDER BY updatedAt DESC") fun reports(): Flow<List<Report>>
    @Upsert suspend fun upsertReport(r: Report): Long
    @Query("DELETE FROM Report WHERE id = :id") suspend fun deleteReport(id: Long)
    @Query("SELECT * FROM Finding WHERE reportId = :reportId ORDER BY id") fun findings(reportId: Long): Flow<List<Finding>>
    @Query("SELECT * FROM Finding") fun allFindings(): Flow<List<Finding>>
    @Upsert suspend fun upsertFinding(f: Finding): Long
    @Query("DELETE FROM Finding WHERE id = :id") suspend fun deleteFinding(id: Long)
    @Query("DELETE FROM Finding WHERE reportId = :reportId") suspend fun deleteFindingsFor(reportId: Long)

    @Query("SELECT * FROM ChatSession ORDER BY at DESC") fun sessions(): Flow<List<ChatSession>>
    @Insert suspend fun addSession(s: ChatSession): Long
    @Query("DELETE FROM ChatSession WHERE id = :id") suspend fun deleteSession(id: Long)
    @Query("SELECT * FROM ChatMsg WHERE session = :session ORDER BY id") fun messages(session: Long): Flow<List<ChatMsg>>
    @Insert suspend fun addMsg(m: ChatMsg)
    @Query("DELETE FROM ChatMsg WHERE session = :session") suspend fun clearMsgs(session: Long)

    @Query("SELECT * FROM Certificate ORDER BY issuedAt DESC") fun certificates(): Flow<List<Certificate>>
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun addCertificate(c: Certificate)

    @Query("SELECT * FROM StudyDay") fun studyDays(): Flow<List<StudyDay>>
    @Query("SELECT * FROM StudyDay WHERE day = :day") suspend fun studyDay(day: String): StudyDay?
    @Upsert suspend fun upsertDay(d: StudyDay)

    @Query("SELECT * FROM ChallengeResult") fun challengeResults(): Flow<List<ChallengeResult>>
    @Upsert suspend fun upsertChallenge(c: ChallengeResult)

    @Query("SELECT * FROM CapAnswer ORDER BY stage") fun capAnswers(): Flow<List<CapAnswer>>
    @Upsert suspend fun upsertCap(c: CapAnswer)
    @Query("DELETE FROM CapAnswer") suspend fun resetCapstone()

    @Query("SELECT * FROM Viewed ORDER BY at DESC LIMIT 12") fun viewed(): Flow<List<Viewed>>
    @Upsert suspend fun upsertViewed(v: Viewed)

    // ----- backup helpers (synchronous snapshots) -----
    @Query("SELECT * FROM LessonDone") suspend fun allLessonDone(): List<LessonDone>
    @Query("SELECT * FROM Attempt") suspend fun allAttempts(): List<Attempt>
    @Query("SELECT * FROM LabResult") suspend fun allLabResults(): List<LabResult>
    @Query("SELECT * FROM ProjectState") suspend fun allProjectStates(): List<ProjectState>
    @Query("SELECT * FROM Note") suspend fun allNotes(): List<Note>
    @Query("SELECT * FROM Bookmark") suspend fun allBookmarks(): List<Bookmark>
    @Query("SELECT * FROM Flashcard") suspend fun allCards(): List<Flashcard>
    @Query("SELECT * FROM Report") suspend fun allReports(): List<Report>
    @Query("SELECT * FROM Finding") suspend fun allFindingsList(): List<Finding>
    @Query("SELECT * FROM Certificate") suspend fun allCertificates(): List<Certificate>
    @Query("SELECT * FROM StudyDay") suspend fun allDays(): List<StudyDay>
    @Query("SELECT * FROM ChallengeResult") suspend fun allChallenges(): List<ChallengeResult>
    @Query("SELECT * FROM CapAnswer") suspend fun allCap(): List<CapAnswer>
    @Query("SELECT * FROM Finding WHERE reportId = :reportId ORDER BY id") suspend fun findingsList(reportId: Long): List<Finding>
    @Query("SELECT * FROM Report WHERE id = :id") suspend fun report(id: Long): Report?
}

@Database(
    entities = [LessonDone::class, Attempt::class, LabResult::class, ProjectState::class, Note::class, Bookmark::class, Flashcard::class,
        Report::class, Finding::class, ChatSession::class, ChatMsg::class, Certificate::class, StudyDay::class, ChallengeResult::class,
        CapAnswer::class, Viewed::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "ehr.db").build().also { instance = it }
        }
    }
}
