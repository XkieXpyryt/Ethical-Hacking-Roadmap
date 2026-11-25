package com.bernportal.ehr

import android.app.Application
import com.bernportal.ehr.ai.TutorEngine
import com.bernportal.ehr.data.ContentRepository
import com.bernportal.ehr.data.Prefs
import com.bernportal.ehr.data.SecureStore
import com.bernportal.ehr.data.db.AppDatabase
import com.bernportal.ehr.util.Connectivity
import com.bernportal.ehr.util.Reminders

class EhrApp : Application() {
    lateinit var content: ContentRepository; private set
    lateinit var prefs: Prefs; private set
    lateinit var secure: SecureStore; private set
    lateinit var connectivity: Connectivity; private set
    lateinit var tutor: TutorEngine; private set
    val db by lazy { AppDatabase.get(this) }

    override fun onCreate() {
        super.onCreate()
        content = ContentRepository(this)
        prefs = Prefs(this)
        secure = SecureStore(this)
        connectivity = Connectivity(this)
        tutor = TutorEngine(content)
        Reminders.createChannel(this)
    }
}
