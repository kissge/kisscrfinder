package do_.eki.yo.kisscrfinder.ui.setup

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
class SetupScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun string(id: Int) = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun showsActionsWhenNotConfigured() {
        var opened = false
        composeTestRule.setContent {
            SetupScreen(
                serviceEnabled = false,
                notificationsAllowed = false,
                onOpenAccessibilitySettings = { opened = true },
                onRequestNotifications = {},
            )
        }
        composeTestRule.onNodeWithText(string(R.string.setup_notification_action)).assertExists()
        composeTestRule.onNodeWithText(string(R.string.setup_running)).assertDoesNotExist()
        composeTestRule.onNodeWithText(string(R.string.setup_service_action)).performClick()
        assertTrue(opened)
    }

    @Test
    fun showsRunningWhenConfigured() {
        composeTestRule.setContent {
            SetupScreen(
                serviceEnabled = true,
                notificationsAllowed = true,
                onOpenAccessibilitySettings = {},
                onRequestNotifications = {},
            )
        }
        composeTestRule.onNodeWithText(string(R.string.setup_running)).assertExists()
        composeTestRule.onNodeWithText(string(R.string.setup_service_action)).assertDoesNotExist()
    }
}
