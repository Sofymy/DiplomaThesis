package bme.vik.diplomathesis.model.data

data class MemoryUsage(
    val usedMemory: Long,
    val allocatedHeapMemory: Long,
    val freeHeapMemory: Long,
)