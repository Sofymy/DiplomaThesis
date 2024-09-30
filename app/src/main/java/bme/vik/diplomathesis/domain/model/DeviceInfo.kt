package bme.vik.diplomathesis.domain.model

data class DeviceInfo(
    val deviceName: String,
    val deviceBrand: String,
    val deviceModel: String,
    val deviceTac: String?
)