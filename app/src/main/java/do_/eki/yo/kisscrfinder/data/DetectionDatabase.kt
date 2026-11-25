package do_.eki.yo.kisscrfinder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Detection::class, LastSeen::class], version = 2)
abstract class DetectionDatabase : RoomDatabase() {
    abstract fun detectionDao(): DetectionDao

    companion object {
        @Volatile private var instance: DetectionDatabase? = null

        /** Adds last_seen, seeded from the notification log so nothing recent is re-notified. */
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `last_seen` " +
                            "(`text` TEXT NOT NULL, `lastDetectedAt` INTEGER NOT NULL, PRIMARY KEY(`text`))",
                    )
                    db.execSQL(
                        "INSERT INTO last_seen (text, lastDetectedAt) " +
                            "SELECT text, MAX(detectedAt) FROM detections GROUP BY text",
                    )
                }
            }

        /** Shared by the service and the UI (same process), so the log UI updates live. */
        fun get(context: Context): DetectionDatabase =
            instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(context.applicationContext, DetectionDatabase::class.java, "detections.db")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
    }
}
