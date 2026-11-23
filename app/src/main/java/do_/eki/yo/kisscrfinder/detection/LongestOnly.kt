package do_.eki.yo.kisscrfinder.detection

/**
 * Drops texts that are a substring of another text in [texts]
 * (e.g. 沼津 and 沼津港 -> 沼津港). Duplicates collapse into one.
 */
fun longestOnly(texts: Collection<String>): Set<String> {
    val distinct = texts.toSet()
    return distinct.filterTo(linkedSetOf()) { text ->
        distinct.none { other -> other.length > text.length && other.contains(text) }
    }
}
