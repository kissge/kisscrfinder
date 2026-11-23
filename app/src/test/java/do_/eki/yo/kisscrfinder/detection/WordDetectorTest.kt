package do_.eki.yo.kisscrfinder.detection

import org.junit.Assert.assertEquals
import org.junit.Test

class WordDetectorTest {
    private val detector = WordDetector(listOf("単語", "辞書"))

    @Test
    fun detectsWordContainedInText() {
        assertEquals(setOf("単語"), detector.detect(sequenceOf("英単語を覚える")))
    }

    @Test
    fun detectsWordsAcrossMultipleTexts() {
        assertEquals(setOf("単語", "辞書"), detector.detect(sequenceOf("辞書", "foo", "単語帳")))
    }

    @Test
    fun returnsEmptyWhenNothingMatches() {
        assertEquals(emptySet<String>(), detector.detect(sequenceOf("単", "語", "")))
    }

    @Test
    fun ignoresEmptyTargetWords() {
        assertEquals(emptySet<String>(), WordDetector(listOf("")).detect(sequenceOf("anything")))
    }
}
