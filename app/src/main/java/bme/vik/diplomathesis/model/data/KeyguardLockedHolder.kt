package bme.vik.diplomathesis.model.data

import java.io.Serializable

data class KeyguardLockedHolder(
    private var _keyguardLocked: MutableList<KeyguardLocked> = mutableListOf()
): Serializable {
    var keyguardLocked = _keyguardLocked
}
