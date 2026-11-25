package do_.eki.yo.kisscrfinder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** When each compound was last detected, whether or not it was notified then. */
@Entity(tableName = "last_seen")
data class LastSeen(
    @PrimaryKey val text: String,
    val lastDetectedAt: Long,
)
