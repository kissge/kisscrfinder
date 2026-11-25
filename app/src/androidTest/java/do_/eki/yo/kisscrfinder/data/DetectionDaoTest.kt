package do_.eki.yo.kisscrfinder.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetectionDaoTest {
    private lateinit var db: DetectionDatabase
    private lateinit var dao: DetectionDao

    @Before
    fun setUp() {
        db =
            Room
                .inMemoryDatabaseBuilder(
                    InstrumentationRegistry.getInstrumentation().targetContext,
                    DetectionDatabase::class.java,
                ).build()
        dao = db.detectionDao()
    }

    @After
    fun tearDown() = db.close()

    private fun detection(
        text: String,
        at: Long,
    ) = Detection(text = text, radical = 1, packageName = "pkg", detectedAt = at)

    private suspend fun insert(
        vararg texts: String,
        at: Long = 0,
        window: Long = WINDOW,
    ) = dao.insertIfNotRecentlySeen(texts.map { detection(it, at) }, window).map { it.text }

    @Test
    fun insertsNewTexts() =
        runTest {
            assertEquals(listOf("森林", "海洋"), insert("森林", "海洋"))
            assertEquals(listOf("海洋", "森林"), dao.observeLatest(10).first().map { it.text })
        }

    @Test
    fun skipsTextsSeenWithinWindow() =
        runTest {
            insert("森林", at = 0)
            assertEquals(listOf("海洋"), insert("森林", "海洋", at = WINDOW - 1))
        }

    @Test
    fun deduplicatesWithinOneBatch() =
        runTest {
            assertEquals(listOf("森林"), insert("森林", "森林"))
        }

    @Test
    fun allowsTextAgainOnceWindowHasPassed() =
        runTest {
            insert("森林", at = 0)
            assertEquals(listOf("森林"), insert("森林", at = WINDOW))
        }

    @Test
    fun windowCountsFromLastDetectionNotLastNotification() =
        runTest {
            insert("森林", at = 0) // notified
            assertEquals(emptyList<String>(), insert("森林", at = WINDOW - 1)) // suppressed, but extends the window
            assertEquals(emptyList<String>(), insert("森林", at = WINDOW + 1))
            assertEquals(listOf("森林"), insert("森林", at = 2 * WINDOW + 1))
        }

    private companion object {
        const val WINDOW = 1_000L
    }
}
