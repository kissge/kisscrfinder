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

    private fun detection(text: String) = Detection(text = text, radical = 1, packageName = "pkg", detectedAt = 0)

    private suspend fun insert(
        vararg texts: String,
        window: Int = DetectionDao.RECENT_WINDOW,
    ) = dao.insertIfNotRecent(texts.map(::detection), window).map { it.text }

    @Test
    fun insertsNewTexts() =
        runTest {
            assertEquals(listOf("森林", "海洋"), insert("森林", "海洋"))
            assertEquals(listOf("海洋", "森林"), dao.observeLatest(10).first().map { it.text })
        }

    @Test
    fun skipsTextsInRecentWindow() =
        runTest {
            insert("森林")
            assertEquals(listOf("海洋"), insert("森林", "海洋"))
        }

    @Test
    fun deduplicatesWithinOneBatch() =
        runTest {
            assertEquals(listOf("森林"), insert("森林", "森林"))
        }

    @Test
    fun allowsTextAgainOnceOutsideWindow() =
        runTest {
            insert("森林", window = 2)
            insert("海洋", window = 2)
            insert("評論", window = 2)
            // The latest 2 are 評論 and 海洋, so 森林 is notifiable again.
            assertEquals(listOf("森林"), insert("森林", window = 2))
            assertEquals(emptyList<String>(), insert("森林", window = 2))
        }
}
