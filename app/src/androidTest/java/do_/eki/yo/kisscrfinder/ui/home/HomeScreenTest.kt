package do_.eki.yo.kisscrfinder.ui.home

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import do_.eki.yo.kisscrfinder.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun string(id: Int) = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private fun setContent(
        serviceEnabled: Boolean,
        notificationsAllowed: Boolean,
        log: List<LogEntry> = emptyList(),
        onOpenAccessibilitySettings: () -> Unit = {},
    ) = composeTestRule.setContent {
        HomeScreen(
            serviceEnabled = serviceEnabled,
            notificationsAllowed = notificationsAllowed,
            log = log,
            onOpenAccessibilitySettings = onOpenAccessibilitySettings,
            onRequestNotifications = {},
        )
    }

    @Test
    fun showsActionsWhenNotConfigured() {
        var opened = false
        setContent(
            serviceEnabled = false,
            notificationsAllowed = false,
            onOpenAccessibilitySettings = { opened = true },
        )
        composeTestRule.onNodeWithText(string(R.string.setup_notification_action)).assertExists()
        composeTestRule.onNodeWithText(string(R.string.setup_running)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.setup_service_action)).performClick()
        assertTrue(opened)
    }

    @Test
    fun showsRunningWhenConfigured() {
        setContent(serviceEnabled = true, notificationsAllowed = true)
        composeTestRule.onNodeWithText(string(R.string.setup_running)).assertExists()
        composeTestRule.onNodeWithText(string(R.string.setup_service_action)).assertDoesNotExist()
    }

    @Test
    fun showsEmptyLog() {
        setContent(serviceEnabled = true, notificationsAllowed = true)
        composeTestRule.onNodeWithText(string(R.string.log_empty)).assertExists()
    }

    @Test
    fun showsLogEntries() {
        setContent(
            serviceEnabled = true,
            notificationsAllowed = true,
            log = listOf(LogEntry(1, "森林", "木", "Chrome", "11/23 10:34")),
        )
        composeTestRule.onNodeWithText("森林").assertExists()
        composeTestRule.onNodeWithText("11/23 10:34").assertExists()
        composeTestRule.onNodeWithText(string(R.string.log_empty)).assertDoesNotExist()
    }
}
