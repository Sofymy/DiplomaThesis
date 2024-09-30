package bme.vik.diplomathesis.domain.model

data class MemoryUsageInfo(
    val usedMemory: Long,
    val allocatedHeapMemory: Long,
    val freeHeapMemory: Long,
)