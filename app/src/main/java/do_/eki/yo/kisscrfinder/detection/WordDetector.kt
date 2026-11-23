package do_.eki.yo.kisscrfinder.detection

/** Finds which of [words] appear in a stream of on-screen texts. */
class WordDetector(
    words: Collection<String>,
) {
    private val words: Set<String> = words.filter { it.isNotEmpty() }.toSet()

    fun detect(texts: Sequence<CharSequence>): Set<String> {
        val remaining = words.toMutableSet()
        val found = linkedSetOf<String>()
        for (text in texts) {
            val iterator = remaining.iterator()
            while (iterator.hasNext()) {
                val word = iterator.next()
                if (text.contains(word)) {
                    found += word
                    iterator.remove()
                }
            }
            if (remaining.isEmpty()) break
        }
        return found
    }

    companion object {
        val TARGET_WORDS = listOf("単語")
    }
}
