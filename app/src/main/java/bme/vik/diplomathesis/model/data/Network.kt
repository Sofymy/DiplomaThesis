package bme.vik.diplomathesis.model.data

data class Network(
    val networkOperator: String,
    val networkType: String,
    val networkRoaming: Boolean,
    val networkSelectionMode: Int?,
    val networkSpecifier: String,
    val networkManualNetworkSelectionAllowed: Boolean?
)