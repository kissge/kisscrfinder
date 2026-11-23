package do_.eki.yo.kisscrfinder.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** One notified compound. */
@Entity(tableName = "detections", indices = [Index("text")])
data class Detection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val radical: Int,
    /** Package of the app whose window showed the compound. */
    val packageName: String?,
    val detectedAt: Long,
)
