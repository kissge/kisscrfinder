package do_.eki.yo.kisscrfinder.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import do_.eki.yo.kisscrfinder.R
import do_.eki.yo.kisscrfinder.ui.theme.KissCrFinderTheme

/** A log row, already formatted for display. */
data class LogEntry(
    val id: Long,
    val text: String,
    val radical: String,
    val source: String,
    val time: String,
)

@Composable
fun HomeScreen(
    serviceEnabled: Boolean,
    notificationsAllowed: Boolean,
    log: List<LogEntry>,
    onOpenAccessibilitySettings: () -> Unit,
    onRequestNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            Setup(
                serviceEnabled = serviceEnabled,
                notificationsAllowed = notificationsAllowed,
                onOpenAccessibilitySettings = onOpenAccessibilitySettings,
                onRequestNotifications = onRequestNotifications,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        item {
            Text(
                stringResource(R.string.log_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
            )
        }
        if (log.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.log_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
        items(log, key = { it.id }) { entry ->
            ListItem(
                supportingContent = {
                    Text(stringResource(R.string.log_detail, entry.radical, entry.source))
                },
                trailingContent = { Text(entry.time) },
            ) {
                Text(entry.text, style = MaterialTheme.typography.titleLarge)
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun Setup(
    serviceEnabled: Boolean,
    notificationsAllowed: Boolean,
    onOpenAccessibilitySettings: () -> Unit,
    onRequestNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (serviceEnabled && notificationsAllowed) {
            Text(
                stringResource(R.string.setup_running),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            return@Column
        }
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
private fun HomeScreenPreview() {
    KissCrFinderTheme {
        HomeScreen(
            serviceEnabled = true,
            notificationsAllowed = true,
            log =
                listOf(
                    LogEntry(2, "森林", "木", "Chrome", "11/23 10:34"),
                    LogEntry(1, "海洋", "水", "Settings", "11/23 10:30"),
                ),
            onOpenAccessibilitySettings = {},
            onRequestNotifications = {},
        )
    }
}
