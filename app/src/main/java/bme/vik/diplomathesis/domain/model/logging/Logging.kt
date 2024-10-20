package bme.vik.diplomathesis.domain.model.logging


data class Logging(
    val deviceTac: List<String> = emptyList(),
    val loggingMetrics: List<DeviceMetric> = emptyList()
)