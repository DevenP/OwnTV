package tv.own.owntv.features.settings.lock

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsPinLockTest {

    @Test
    fun `asks only when a PIN is set, the switch is on and the visit is still locked`() {
        assertTrue(requiresSettingsPin(hasPin = true, enabled = true, unlocked = false))
        assertFalse(requiresSettingsPin(hasPin = true, enabled = true, unlocked = true))
        assertFalse(requiresSettingsPin(hasPin = true, enabled = false, unlocked = false))
        assertFalse(requiresSettingsPin(hasPin = false, enabled = true, unlocked = false))
        assertFalse(requiresSettingsPin(hasPin = false, enabled = false, unlocked = false))
    }

    @Test
    fun `a switch left on without a PIN does not lock Settings`() {
        val lock = SettingsPinLock(loaded = true, profileId = 1L, pin = null, askOnOpen = true)
        assertFalse(lock.requiresPin(unlocked = false))
    }

    @Test
    fun `a stored PIN with the switch on locks Settings until unlocked`() {
        val lock = SettingsPinLock(loaded = true, profileId = 1L, pin = "salt:hash", askOnOpen = true)
        assertTrue(lock.requiresPin(unlocked = false))
        assertFalse(lock.requiresPin(unlocked = true))
    }

    @Test
    fun `a PIN alone does not lock Settings`() {
        val lock = SettingsPinLock(loaded = true, profileId = 1L, pin = "salt:hash", askOnOpen = false)
        assertFalse(lock.requiresPin(unlocked = false))
    }
}
