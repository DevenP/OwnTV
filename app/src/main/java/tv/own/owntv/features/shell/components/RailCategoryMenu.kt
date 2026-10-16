package tv.own.owntv.features.shell.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import tv.own.owntv.R
import tv.own.owntv.core.customize.CategoryRestorePlan
import tv.own.owntv.core.customize.categoryRestoreSummary
import tv.own.owntv.core.live.LiveKey
import tv.own.owntv.features.live.LiveRailItem

/**
 * The long-press menu of a category in the Live TV, Movies and Series lists, wired to what each item
 * does to [item]. [onClose] runs after every action, and on Back.
 */
@Composable
internal fun RailCategoryMenu(
    item: LiveRailItem,
    name: String,
    restorePlan: suspend (LiveKey) -> CategoryRestorePlan,
    onHide: (LiveKey) -> Unit,
    onMove: (LiveKey) -> Unit,
    onRestore: (LiveKey) -> Unit,
    onClose: () -> Unit,
) {
    val plan by produceState<CategoryRestorePlan?>(null, item.key) { value = restorePlan(item.key) }
    CategoryContextMenu(
        categoryName = name,
        canHide = item.key is LiveKey.Folder || item.key is LiveKey.Custom,
        canMove = item.key is LiveKey.Folder || item.key is LiveKey.Custom,
        onHide = { onHide(item.key); onClose() },
        onMove = { onMove(item.key); onClose() },
        // Always offered: the plan is read in the background and only fills in the confirmation.
        resetLabel = stringResource(if (item.key is LiveKey.Custom) R.string.content_category_restore_custom else R.string.content_category_restore_playlist),
        resetLines = plan?.let { categoryRestoreSummary(it, LocalContext.current.resources) },
        onReset = { onRestore(item.key); onClose() },
        onDismiss = onClose,
    )
}
