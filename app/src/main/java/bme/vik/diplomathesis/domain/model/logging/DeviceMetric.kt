package bme.vik.diplomathesis.domain.model.logging

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.PhoneLocked
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.graphics.vector.ImageVector

enum class DeviceMetric(
    val title: String,
    val icon: ImageVector = Icons.Default.Info
) {
    DEVICE_INFORMATION("Device Information", Icons.Filled.Info),
    MEMORY_USAGE("Memory Usage", Icons.Filled.Memory),
    CALLS("Calls", Icons.Filled.Call),
    MOBILE_DATA_TRAFFIC("Mobile Data Traffic", Icons.Filled.DataUsage),
    LOCK_SCREEN_DATA("Lock Screen Data", Icons.Filled.PhoneLocked),
    STORAGE_INFORMATION("Storage Information", Icons.Filled.Storage),
    MOBILE_NETWORK_DATA("Mobile Network Data", Icons.Filled.NetworkCell),
    CELL_INFORMATION("Cell Information", Icons.Filled.CellTower),
    BATTERY_DATA("Battery Data", Icons.Filled.BatteryFull)
}