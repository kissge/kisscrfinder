package do_.eki.yo.kisscrfinder.detection

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class SameRadicalCompoundDetectorTest {
    // Unit tests run with the module directory as the working directory.
    private val table = File("src/main/assets/${RadicalTable.ASSET_NAME}").inputStream().use(RadicalTable::fromStream)
    private val detector = SameRadicalCompoundDetector(table)

    private fun find(text: String) = detector.findIn(text).map { it.text }

    @Test
    fun radicalTableHasKnownValues() {
        assertEquals(85, table.radicalOf('海'))
        assertEquals(75, table.radicalOf('林'))
        assertEquals(149, table.radicalOf('語'))
        assertEquals(0, table.radicalOf('あ'))
        assertEquals(0, table.radicalOf('A'))
    }

    @Test
    fun radicalCharIsDisplayable() {
        assertEquals("水", RadicalTable.radicalChar(85))
        assertEquals("言", RadicalTable.radicalChar(149))
    }

    @Test
    fun detectsSameRadicalPairs() {
        assertEquals(listOf("海洋"), find("海洋"))
        assertEquals(listOf("森林"), find("広い森林を歩く"))
    }

    @Test
    fun detectsLongerRunAsOneCompound() {
        assertEquals(listOf("江湖海"), find("江湖海"))
    }

    @Test
    fun detectsMultipleCompoundsInOneText() {
        assertEquals(listOf("海洋", "評論"), find("海洋の評論"))
    }

    @Test
    fun ignoresDifferentRadicals() {
        assertEquals(emptyList<String>(), find("日本語の文章"))
    }

    @Test
    fun singleKanjiIsNotACompound() {
        assertEquals(emptyList<String>(), find("海と林"))
    }

    @Test
    fun reportsRadical() {
        assertEquals(listOf(Compound("語調", 149)), detector.findIn("語調"))
    }

    @Test
    fun detectDeduplicatesAcrossTexts() {
        assertEquals(
            setOf(Compound("海洋", 85), Compound("森林", 75)),
            detector.detect(sequenceOf("海洋", "森林", "海洋")),
        )
    }
}
