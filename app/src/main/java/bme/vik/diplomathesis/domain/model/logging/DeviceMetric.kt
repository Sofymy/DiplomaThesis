package bme.vik.diplomathesis.domain.model.logging

enum class DeviceMetric(
    val title: String,
) {
    DEVICE_INFORMATION("Device Information"),
    MEMORY_USAGE("Memory Usage"),
    CALLS("Calls"),
    MOBILE_DATA_TRAFFIC("Mobile Data Traffic"),
    LOCK_SCREEN_DATA("Lock Screen Data"),
    STORAGE_INFORMATION("Storage Information"),
    MOBILE_NETWORK_DATA("Mobile Network Data"),
    CELL_INFORMATION("Cell Information"),
    BATTERY_DATA("Battery Data")
}