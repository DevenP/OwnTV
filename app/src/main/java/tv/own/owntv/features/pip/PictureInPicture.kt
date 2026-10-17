package tv.own.owntv.features.pip

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.context.GlobalContext

/**
 * Picture-in-picture when leaving the app: the option, what the player offers right now, and whether
 * the activity is in the small window. The shell offers ([offer]); [PictureInPictureWindow] hands that
 * to Android; Android's answer comes back through [onWindowChanged].
 *
 * The option is stored on the device, not per profile, in SharedPreferences: it is about how this TV
 * behaves when the user presses Home, and it must be readable synchronously when the activity starts.
 */
internal class PictureInPicture(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    val supported: Boolean = context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)

    private val _enabled = MutableStateFlow(prefs.getBoolean(KEY_ON_LEAVE, false))
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _offer = MutableStateFlow<PipAspect?>(null)

    /** The window to open if the user leaves now; null when leaving should stop playback as it always has. */
    val offer: StateFlow<PipAspect?> = _offer.asStateFlow()

    private val _playerBounds = MutableStateFlow<PipBounds?>(null)

    /** Where the player is laid out in the window, for the window's opening animation. */
    val playerBounds: StateFlow<PipBounds?> = _playerBounds.asStateFlow()

    private val _inWindow = MutableStateFlow(false)
    val inWindow: StateFlow<Boolean> = _inWindow.asStateFlow()

    fun setEnabled(on: Boolean) {
        prefs.edit { putBoolean(KEY_ON_LEAVE, on) }
        _enabled.value = on
    }

    fun offer(aspect: PipAspect?) {
        _offer.value = aspect
    }

    fun onPlayerBounds(bounds: PipBounds) {
        _playerBounds.value = bounds
    }

    fun onWindowChanged(inPictureInPicture: Boolean) {
        _inWindow.value = inPictureInPicture
    }

    companion object {
        private const val PREFS = "owntv_pip"
        private const val KEY_ON_LEAVE = "on_leave"

        /** For lists built outside composition (the Video player catalogue); false before Koin starts. */
        fun supported(): Boolean = GlobalContext.getOrNull()?.getOrNull<PictureInPicture>()?.supported == true
    }
}
