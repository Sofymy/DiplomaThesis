package bme.vik.diplomathesis.domain.model

import java.io.Serializable

data class KeyguardLockedInfo(
    var keyguardLocked: Boolean = false,
): Serializable