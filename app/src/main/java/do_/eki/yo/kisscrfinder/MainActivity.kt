package do_.eki.yo.kisscrfinder

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import do_.eki.yo.kisscrfinder.notification.DetectionNotifier
import do_.eki.yo.kisscrfinder.service.ScreenTextService
import do_.eki.yo.kisscrfinder.ui.setup.SetupScreen
import do_.eki.yo.kisscrfinder.ui.theme.KissCrFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KissCrFinderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SetupRoute(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun SetupRoute(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var serviceEnabled by remember { mutableStateOf(false) }
    var notificationsAllowed by remember { mutableStateOf(false) }

    // Users toggle these in system settings, so re-check whenever we come back.
    LifecycleResumeEffect(Unit) {
        serviceEnabled = isScreenTextServiceEnabled(context)
        notificationsAllowed = DetectionNotifier.canPostNotifications(context)
        onPauseOrDispose {}
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            notificationsAllowed = granted
            // Denied permanently: the dialog no longer shows, so fall back to settings.
            if (!granted) openNotificationSettings(context)
        }

    SetupScreen(
        serviceEnabled = serviceEnabled,
        notificationsAllowed = notificationsAllowed,
        onOpenAccessibilitySettings = {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        },
        onRequestNotifications = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                openNotificationSettings(context)
            }
        },
        modifier = modifier,
    )
}

private fun isScreenTextServiceEnabled(context: Context): Boolean {
    val manager = context.getSystemService(AccessibilityManager::class.java)
    return manager
        .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        .any {
            it.resolveInfo.serviceInfo.packageName == context.packageName &&
                it.resolveInfo.serviceInfo.name == ScreenTextService::class.java.name
        }
}

private fun openNotificationSettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
    )
}
