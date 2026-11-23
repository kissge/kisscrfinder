package do_.eki.yo.kisscrfinder.detection

data class Compound(
    val text: String,
    val radical: Int,
)

/**
 * Finds runs of two or more consecutive kanji that share the same radical (e.g. 海洋, 森林).
 * No word segmentation: a run may span a word boundary.
 */
class SameRadicalCompoundDetector(
    private val radicals: RadicalTable,
) {
    fun detect(texts: Sequence<CharSequence>): Set<Compound> = texts.flatMapTo(linkedSetOf()) { findIn(it) }

    fun findIn(text: CharSequence): List<Compound> {
        val found = mutableListOf<Compound>()
        var runStart = 0
        var runRadical = 0

        fun closeRun(end: Int) {
            if (runRadical != 0 && end - runStart >= MIN_LENGTH) {
                found += Compound(text.substring(runStart, end), runRadical)
            }
        }

        for (i in text.indices) {
            val radical = radicals.radicalOf(text[i])
            if (radical != 0 && radical == runRadical) continue
            closeRun(i)
            runStart = i
            runRadical = radical
        }
        closeRun(text.length)
        return found
    }

    private companion object {
        const val MIN_LENGTH = 2
    }
}
