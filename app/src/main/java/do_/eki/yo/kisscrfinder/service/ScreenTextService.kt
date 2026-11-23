package do_.eki.yo.kisscrfinder.service

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import do_.eki.yo.kisscrfinder.detection.RadicalTable
import do_.eki.yo.kisscrfinder.detection.SameRadicalCompoundDetector
import do_.eki.yo.kisscrfinder.notification.DetectionNotifier

/**
 * Stays resident while enabled in accessibility settings, scans the text of every
 * window on screen and notifies when a same-radical compound newly appears.
 */
class ScreenTextService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var detector: SameRadicalCompoundDetector
    private lateinit var notifier: DetectionNotifier

    private var scanScheduled = false
    private var lastDetected: Set<String> = emptySet()

    private val scanRunnable =
        Runnable {
            scanScheduled = false
            scan()
        }

    override fun onServiceConnected() {
        detector = SameRadicalCompoundDetector(RadicalTable.fromStream(assets.open(RadicalTable.ASSET_NAME)))
        notifier = DetectionNotifier(this)
        notifier.ensureChannel()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Throttle: events arrive in bursts, so scan at most once per SCAN_INTERVAL_MS.
        if (!scanScheduled) {
            scanScheduled = true
            handler.postDelayed(scanRunnable, SCAN_INTERVAL_MS)
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        handler.removeCallbacks(scanRunnable)
        super.onDestroy()
    }

    private fun scan() {
        val ignoredPackages = setOf(packageName, SYSTEM_UI_PACKAGE)
        val roots =
            windows
                .mapNotNull { it.root }
                .ifEmpty { listOfNotNull(rootInActiveWindow) }
                .filter { it.packageName?.toString() !in ignoredPackages }

        val detected = detector.detect(roots.asSequence().flatMap { it.texts() })
        val newlyDetected = detected.filter { it.text !in lastDetected }
        if (newlyDetected.isNotEmpty()) notifier.notifyDetected(newlyDetected)
        lastDetected = detected.mapTo(hashSetOf()) { it.text }
    }

    private fun AccessibilityNodeInfo.texts(): Sequence<CharSequence> =
        sequence {
            val stack = ArrayDeque<AccessibilityNodeInfo>().apply { add(this@texts) }
            var visited = 0
            while (stack.isNotEmpty() && visited < MAX_NODES_PER_WINDOW) {
                val node = stack.removeLast()
                visited++
                node.text?.let { yield(it) }
                node.contentDescription?.let { yield(it) }
                for (i in node.childCount - 1 downTo 0) {
                    node.getChild(i)?.let(stack::addLast)
                }
            }
        }

    private companion object {
        const val SCAN_INTERVAL_MS = 500L
        const val MAX_NODES_PER_WINDOW = 5_000
        const val SYSTEM_UI_PACKAGE = "com.android.systemui"
    }
}
