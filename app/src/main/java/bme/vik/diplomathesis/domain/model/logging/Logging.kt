package bme.vik.diplomathesis.domain.model.logging


data class Logging(
    val deviceTac: List<String> = emptyList<String>(),
    val loggingMetrics: List<DeviceMetric> = emptyList<DeviceMetric>()
)