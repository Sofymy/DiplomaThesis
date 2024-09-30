package bme.vik.diplomathesis.domain.model

data class NetworkInfo(
    val networkOperator: String,
    val networkType: String,
    val networkRoaming: Boolean,
    val networkSelectionMode: Int?,
    val networkSpecifier: String,
    val networkManualNetworkSelectionAllowed: Boolean?
)