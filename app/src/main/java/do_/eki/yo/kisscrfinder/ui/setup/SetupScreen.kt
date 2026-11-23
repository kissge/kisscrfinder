package do_.eki.yo.kisscrfinder.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import do_.eki.yo.kisscrfinder.R
import do_.eki.yo.kisscrfinder.ui.theme.KissCrFinderTheme

@Composable
fun SetupScreen(
    serviceEnabled: Boolean,
    notificationsAllowed: Boolean,
    onOpenAccessibilitySettings: () -> Unit,
    onRequestNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(R.string.setup_description), style = MaterialTheme.typography.bodyLarge)

        StepCard(
            title = stringResource(R.string.setup_service_title),
            done = serviceEnabled,
            actionLabel = stringResource(R.string.setup_service_action),
            onAction = onOpenAccessibilitySettings,
        )
        StepCard(
            title = stringResource(R.string.setup_notification_title),
            done = notificationsAllowed,
            actionLabel = stringResource(R.string.setup_notification_action),
            onAction = onRequestNotifications,
        )

        if (serviceEnabled && notificationsAllowed) {
            Text(
                stringResource(R.string.setup_running),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun StepCard(
    title: String,
    done: Boolean,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(stringResource(if (done) R.string.status_done else R.string.status_not_done))
            if (!done) {
                Button(onClick = onAction) { Text(actionLabel) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SetupScreenPreview() {
    KissCrFinderTheme {
        SetupScreen(
            serviceEnabled = true,
            notificationsAllowed = false,
            onOpenAccessibilitySettings = {},
            onRequestNotifications = {},
        )
    }
}
