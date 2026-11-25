package do_.eki.yo.kisscrfinder.detection

data class Compound(
    val text: String,
    val radical: Int,
)

/**
 * Finds runs of two or more consecutive kanji that share the same radical (e.g. 海洋, 森林).
 * No word segmentation: a run may span a word boundary.
 * A run made of a single repeated kanji (山山) does not count; it needs two distinct kanji (海海洋 counts).
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
            if (runRadical == 0) return
            val run = text.substring(runStart, end)
            if (run.toSet().size >= MIN_DISTINCT_KANJI) found += Compound(run, runRadical)
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
        const val MIN_DISTINCT_KANJI = 2
    }
}
