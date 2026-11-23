package do_.eki.yo.kisscrfinder.detection

import org.junit.Assert.assertEquals
import org.junit.Test

class LongestOnlyTest {
    @Test
    fun dropsSubstringOfLongerText() {
        assertEquals(setOf("沼津港"), longestOnly(listOf("沼津", "沼津港")))
    }

    @Test
    fun dropsSubstringRegardlessOfOrderOrPosition() {
        assertEquals(setOf("沼津港"), longestOnly(listOf("沼津港", "津港", "沼津")))
    }

    @Test
    fun keepsUnrelatedTexts() {
        assertEquals(setOf("森林", "海洋"), longestOnly(listOf("森林", "海洋")))
    }

    @Test
    fun collapsesDuplicates() {
        assertEquals(setOf("森林"), longestOnly(listOf("森林", "森林")))
    }

    @Test
    fun keepsOverlappingButNotContainedTexts() {
        // 沼津 and 津港 overlap but neither contains the other.
        assertEquals(setOf("沼津", "津港"), longestOnly(listOf("沼津", "津港")))
    }
}
