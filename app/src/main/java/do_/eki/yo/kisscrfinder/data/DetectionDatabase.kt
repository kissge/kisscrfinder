package do_.eki.yo.kisscrfinder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Detection::class], version = 1)
abstract class DetectionDatabase : RoomDatabase() {
    abstract fun detectionDao(): DetectionDao

    companion object {
        @Volatile private var instance: DetectionDatabase? = null

        /** Shared by the service and the UI (same process), so the log UI updates live. */
        fun get(context: Context): DetectionDatabase =
            instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(context.applicationContext, DetectionDatabase::class.java, "detections.db")
                    .build()
                    .also { instance = it }
            }
    }
}
