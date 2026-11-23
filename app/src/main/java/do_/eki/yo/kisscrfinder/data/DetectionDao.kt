package do_.eki.yo.kisscrfinder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DetectionDao {
    @Query("SELECT * FROM detections ORDER BY id DESC LIMIT :limit")
    abstract fun observeLatest(limit: Int): Flow<List<Detection>>

    @Query("SELECT text FROM detections ORDER BY id DESC LIMIT :limit")
    protected abstract suspend fun latestTexts(limit: Int): List<String>

    @Insert
    protected abstract suspend fun insertAll(detections: List<Detection>)

    /**
     * Inserts the candidates whose text is not among the latest [window] entries,
     * and returns them (the ones that should be notified).
     */
    @Transaction
    open suspend fun insertIfNotRecent(
        candidates: List<Detection>,
        window: Int = RECENT_WINDOW,
    ): List<Detection> {
        val recent = latestTexts(window).toHashSet()
        val fresh = candidates.distinctBy { it.text }.filter { it.text !in recent }
        if (fresh.isNotEmpty()) insertAll(fresh)
        return fresh
    }

    companion object {
        const val RECENT_WINDOW = 100
    }
}
