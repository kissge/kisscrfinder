package do_.eki.yo.kisscrfinder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

@Dao
abstract class DetectionDao {
    @Query("SELECT * FROM detections ORDER BY id DESC LIMIT :limit")
    abstract fun observeLatest(limit: Int): Flow<List<Detection>>

    @Query("SELECT * FROM last_seen WHERE text IN (:texts)")
    protected abstract suspend fun lastSeen(texts: List<String>): List<LastSeen>

    @Upsert
    protected abstract suspend fun upsertLastSeen(lastSeen: List<LastSeen>)

    @Insert
    protected abstract suspend fun insertAll(detections: List<Detection>)

    /**
     * Records every candidate as detected at its [Detection.detectedAt], then inserts into the
     * log and returns (the ones to notify) only those not detected within the preceding [windowMillis].
     */
    @Transaction
    open suspend fun insertIfNotRecentlySeen(
        candidates: List<Detection>,
        windowMillis: Long = RECENT_WINDOW_MILLIS,
    ): List<Detection> {
        val distinct = candidates.distinctBy { it.text }
        if (distinct.isEmpty()) return emptyList()
        val lastDetectedAt = lastSeen(distinct.map { it.text }).associate { it.text to it.lastDetectedAt }
        val fresh =
            distinct.filter { candidate ->
                val last = lastDetectedAt[candidate.text]
                last == null || candidate.detectedAt - last >= windowMillis
            }
        upsertLastSeen(distinct.map { LastSeen(it.text, it.detectedAt) })
        if (fresh.isNotEmpty()) insertAll(fresh)
        return fresh
    }

    companion object {
        val RECENT_WINDOW_MILLIS = TimeUnit.HOURS.toMillis(72)
    }
}
